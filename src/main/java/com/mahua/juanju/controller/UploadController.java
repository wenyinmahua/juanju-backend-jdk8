package com.mahua.juanju.controller;

import com.mahua.juanju.common.BaseResponse;
import com.mahua.juanju.common.ErrorCode;
import com.mahua.juanju.common.ResultUtils;
import com.mahua.juanju.model.domain.User;
import com.mahua.juanju.service.UserService;
import com.mahua.juanju.utils.AliOSSUtil;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.UUID;

@RestController
@Slf4j
public class UploadController {
	@Autowired
	private AliOSSUtil aliOssUtil;
	//得到上传路径

	@Resource
	private UserService userService;
	@PostMapping("/upload")
	public BaseResponse upload(MultipartFile file, HttpServletRequest request){
		User loginUser = userService.getLoginUser(request);
		if (loginUser == null){
			return ResultUtils.error(ErrorCode.NO_LOGIN);
		}
//		log.info("文件上传{}",file);
		if (file == null){
			return ResultUtils.error(ErrorCode.PARAMS_ERROR,"文件不存在");
		}
		try {
			String originalFilename = file.getOriginalFilename();
			String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
			//将图片使用UUID进行重命名，防止上传到阿里云的图片因为命名重复而冲突
			String objectname = UUID.randomUUID().toString() + extension;
			//调用阿里云OSS工具上传图片
			String filePath = aliOssUtil.upload(file.getInputStream(),objectname);
			//图片上传成功，返回文件路径
			//https://web-tlias-xxxx.oss-cn-beijing.aliyuncs.com/2b502878-11f1-431c-a17f-9665c7cc7dac.jpg
			return ResultUtils.success(filePath);
		} catch (IOException e) {
			log.error("文件上传失败{}",e);
		}
		return ResultUtils.error(ErrorCode.PARAMS_ERROR);
	}
}