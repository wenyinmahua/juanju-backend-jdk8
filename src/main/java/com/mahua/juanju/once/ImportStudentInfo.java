package com.mahua.juanju.once;

import com.alibaba.excel.EasyExcel;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 导入学生信息到数据库中
 */
public class ImportStudentInfo {
	public static void main(String[] args) {
		String fileName = "C:\\Users\\50184\\Desktop\\studentInfo.xlsx";

		List<StudentInfo> studentInfoList =
				EasyExcel.read(fileName).head(StudentInfo.class).sheet().doReadSync();
		System.out.println("总数= " + studentInfoList.size());
		Map<String,List<StudentInfo>> listMap =
				studentInfoList.stream()
						.filter(studentInfo -> StringUtils.isNotEmpty(studentInfo.getUsername()))
						.collect(Collectors.groupingBy(StudentInfo::getUsername));
		System.out.println("不重复昵称个数为" +listMap.keySet().size());

		for (Map.Entry<String, List<StudentInfo>> stringListEntry : listMap.entrySet()) {
			if (stringListEntry.getValue().size() > 1){
				System.out.println("username= "+ stringListEntry.getKey() + "，个数=" +stringListEntry.getValue().size());
			}
			
		}
	}
}
