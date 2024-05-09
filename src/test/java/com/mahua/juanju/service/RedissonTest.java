package com.mahua.juanju.service;


import org.junit.jupiter.api.Test;
import org.redisson.api.RList;
import org.redisson.api.RedissonClient;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest
public class RedissonTest {

	@Resource
	private RedissonClient redissonClient;

	@Test
	void redissonTest(){
		// list
		// list，数据存放在 JVM 内存中
		List<String> list = new ArrayList<>();
		list.add("mahua");
		System.out.println("list:" + list.get(0));
		list.remove(0);

		// 数据存放在 Redis 里面
		RList<Object> rList = redissonClient.getList("test-list");
		rList.add("mahua");
		rList.add("yupi");
		System.out.println("rList:"+rList.get(0));
		rList.remove(0);
		// map
		// set
		// stack
	}
}
