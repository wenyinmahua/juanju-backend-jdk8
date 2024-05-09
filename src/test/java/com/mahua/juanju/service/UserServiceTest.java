package com.mahua.juanju.service;

import com.mahua.juanju.model.domain.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.List;


/**
 * 用户服务测试
 *
 * @author mahua
 *
 */
@SpringBootTest
class UserServiceTest {
	@Autowired
	private UserService userService;
	@Test
	void testAddUser(){
		User user = new User();
		user.setUsername("mahua");
		user.setUserAccount("123");
		user.setAvatarUrl("https://assets.leetcode.cn/aliyun-lc-upload/users/xenodochial-northcutt31d/avatar_1703245011.png");
		user.setUserPassword("123456");
		user.setPhone("123123");
		user.setEmail("123123@qq.com");
		user.setMajor("软件工程");
		user.setStuId("123234");

		boolean result = userService.save(user);
		System.out.println(result);
	}

	@Test
	void userRegister() {
		String userAccount = "ma666hua";
		String userPassword = "123456789";
	  	String checkPassword = "123456789";
		  String stuId = "123234";
		long result = userService.userRegister(userAccount, userPassword, checkPassword,stuId);
		Assertions.assertTrue(result > 0);
	}
	@Test
	void searchUsersByTags(){
		List<String> tagNameList = Arrays.asList("java","python");
//		List<User> userList = userService.searchUsersByTags(tagNameList);
//		Assert.assertNotNull(userList);
	}

}