package com.mahua.juanju.once;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.read.listener.PageReadListener;

import java.util.List;
import java.util.Map;

/**
 * @author mahua
 */
public class ImportExcel {
	public static void main(String[] args) {
		// 写法1：JDK8+ ,不用额外写一个DemoDataListener
		// since: 3.0.0-beta1
		String fileName = "C:\\Users\\50184\\Desktop\\studentInfo.xlsx";
//		readByListen(fileName);
		synchronousRead(fileName);

	}

	/**
	 * 监听器读取
	 * 先创建监听器、在读取文件时绑定监听器，单独抽离处理逻辑，代码清晰易于维护
	 * 一条一条处理，适用于数据量大的场景
	 * TableListener是自定义的监听器
	 * @param fileName
	 */
	public static void readByListen(String fileName){
		EasyExcel.read(fileName, StudentInfo.class, new TableListener()).sheet().doRead();
	}

	/**
	 * 同步读取
	 * 无需创建监听器,一次性获取所有的数据
	 * @param fileName
	 */
	public static void synchronousRead(String fileName) {
		// 这里 需要指定读用哪个class去读，然后读取第一个sheet 同步读取会自动finish
		List<StudentInfo> totalData = 
				EasyExcel.read(fileName).head(StudentInfo.class).sheet().doReadSync();
		for (StudentInfo studentInfo : totalData) {
			System.out.println(studentInfo);
		}
	}

}
