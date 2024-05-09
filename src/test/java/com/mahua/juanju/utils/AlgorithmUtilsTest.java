package com.mahua.juanju.utils;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

class AlgorithmUtilsTest {


	@Test
	public void test(){
		List<String> tag1 = Arrays.asList("java","python","C++", "男");
		List<String> tag2 = Arrays.asList("java","C++","python", "男","大四");
		System.out.println(AlgorithmUtils.minDistance(tag1, tag2));
	}

}