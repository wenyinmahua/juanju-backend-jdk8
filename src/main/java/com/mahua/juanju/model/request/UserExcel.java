package com.mahua.juanju.model.request;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class UserExcel {
	@ExcelProperty("用户姓名")
	private String username;

	@ExcelProperty("用户学号")
	private String stuId;
}
