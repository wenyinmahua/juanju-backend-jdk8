package com.mahua.juanju.service;

import com.mahua.juanju.model.domain.User;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.StopWatch;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

@SpringBootTest
public class InserUserTest {
//	@Resource
//	private UserMapper userMapper;
	@Resource
	private UserService userService;

	private ExecutorService executorService = new ThreadPoolExecutor(60, 100, 10000, TimeUnit.SECONDS, new ArrayBlockingQueue<>(10000));
	/**
	 * 批量插入用户
	 */
	@Test
	public void doInsertUsers(){
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();
		final int INSET_NUM = 100000;
		List<User> userList = new ArrayList<>();
		for (int i = 0; i < INSET_NUM; i++) {
			User user = new User();
			user.setUsername("伽玛花");
			user.setUserAccount("12211212");
			user.setAvatarUrl("https://assets.leetcode.cn/aliyun-lc-upload/users/qi-yu-de-mao-3/avatar_1681313384.png?x-oss-process=image%2Fformat%2Cwebp");
			user.setGender(0);
			user.setUserPassword("123456789");
			user.setPhone("2505422955989");
			user.setEmail("123456789@qq.com");
			user.setMajor("软件工程");
			user.setUserStatus(0);
			user.setStuId("123456969859");
			user.setTags("");
//			userMapper.insert(user);
			userList.add(user);
		}
		//1000 107s
		//10000 94s
		userService.saveBatch(userList,10000);
		stopWatch.stop();
		System.out.println(stopWatch.getTotalTimeMillis());
	}


	@Test
	public void doConcurrencyInsertUsers(){
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();
		final int INSET_NUM = 100000;
		int batchSize =500;
		int j = 0;
		int size = INSET_NUM / batchSize;
		List<CompletableFuture<Void>> futureList = new ArrayList<>();
		for (int i = 0; i < size ; i++) {
			List<User> userList = new ArrayList<>();
			while (true){
				j++;
				User user = new User();
				user.setUsername("伽玛花");
				user.setUserAccount("12211212");
				user.setAvatarUrl("https://assets.leetcode.cn/aliyun-lc-upload/users/qi-yu-de-mao-3/avatar_1681313384.png?x-oss-process=image%2Fformat%2Cwebp");
				user.setGender(0);
				user.setUserPassword("123456789");
				user.setPhone("2505422955989");
				user.setEmail("123456789@qq.com");
				user.setMajor("软件工程");
				user.setUserStatus(0);
				user.setStuId("123456969859");
				user.setTags("");
				userList.add(user);
				if(j % batchSize == 0){
					break;
				}
			}

			CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
				System.out.println("ThreadName:"+Thread.currentThread().getName());
				userService.saveBatch(userList, batchSize);
			},executorService);
			futureList.add(future);
		}
		CompletableFuture.allOf(futureList.toArray(new CompletableFuture[]{})).join();
		stopWatch.stop();
		System.out.println(stopWatch.getTotalTimeMillis());
	}

}
