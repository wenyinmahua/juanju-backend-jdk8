package com.mahua.juanju.once;

import com.mahua.juanju.mapper.UserMapper;
import com.mahua.juanju.model.domain.User;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import javax.annotation.Resource;

@Component
public class InsertUsers {
	@Resource
	private UserMapper userMapper;

	/**
	 * 批量插入用户
	 */
//	@Scheduled(initialDelay = 5000,fixedRate = Long.MAX_VALUE)
	public void doInsertUsers(){
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();
		final int INSET_NUM = 1000;
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
			userMapper.insert(user);
		}
		stopWatch.stop();
		System.out.println(stopWatch.getTotalTimeMillis());
	}


}
