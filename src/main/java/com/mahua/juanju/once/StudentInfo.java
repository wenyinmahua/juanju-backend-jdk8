package com.mahua.juanju.once;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;

import lombok.Data;

@Data
@ExcelIgnoreUnannotated
public class StudentInfo {

	@ExcelProperty("学号")
	private String stuId;

	@ExcelProperty("姓名")
	private String username;

	@ExcelProperty("编号")
	private String userAccount;

	@ExcelProperty("班级")
	private String major;

//	private String avatarUrl;
//
//	private Integer gender;
//
//	private String userPassword;
//
//	private String phone;
//
//	private String email;
//
//	private Integer userStatus;
//
//	private Date createTime;
//
//	private Date updateTime;
//
//	private Integer isDelete;
//
//	private Integer userRole;
//
//	private String tags;
}
