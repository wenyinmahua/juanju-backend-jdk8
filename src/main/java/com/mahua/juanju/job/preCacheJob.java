package com.mahua.juanju.job;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mahua.juanju.model.domain.User;
import com.mahua.juanju.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 缓存预热任务
 */
@Component
@Slf4j
public class preCacheJob {

	@Resource
	private UserService userService;

	@Resource
	private RedisTemplate<String,Object> redisTemplate;

	@Resource
	private RedissonClient redissonClient;


	private List<Long> mainUserList = Arrays.asList(1L);
	//每天00 : 00点执行，预热推荐缓存
	@Scheduled(cron = "0 0 0 * * ?")
	public void doCacheRecommendUser(){
		RLock lock = redissonClient.getLock("juanju:preCacheJob:doCache:lock");
		try {
			// 等待时间为 0，抢锁只抢一次，抢不到就放弃，锁存在时间为 -1
			if(lock.tryLock(0,-1,TimeUnit.MILLISECONDS)){
				System.out.println("get lock"+Thread.currentThread().getId());
				for (Long id : mainUserList) {
					//预热推荐用户,只加载该用户获取信息中的前64条数据，每页保存8条数据，
					for(int pageNum = 1;pageNum <= 8; pageNum++){
						String redisKey = String.format("juanju:user:recommend:%s:%s",id,pageNum);
						ValueOperations<String,Object> valueOperations = redisTemplate.opsForValue();
						QueryWrapper<User> queryWrapper = new QueryWrapper<>();
						Page<User> userPageList = userService.page(new Page<>(pageNum,8 ),queryWrapper);
						userPageList.setRecords(userPageList.getRecords().stream().map(user -> userService.getSafetyUser(user)).collect(Collectors.toList()));
						userPageList.setTotal(Math.min((int) userPageList.getTotal(), 64));
						try {
							valueOperations.set(redisKey,userPageList,300000, TimeUnit.MILLISECONDS);
						} catch (Exception e) {
							log.error("redis set key error",e);
						}
					}
				}
			}
		} catch (InterruptedException e) {
			log.error("doCacheRecommendUser err",e);
		}finally {
			if(lock.isHeldByCurrentThread()){
				System.out.println("release lock：" + Thread.currentThread().getId());
				lock.unlock();
			}
		}
	}

}
