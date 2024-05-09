package com.mahua.juanju.model.request;


import lombok.Data;

import java.io.Serializable;

/**
 * 用户注册请求体
 *
 * @author mahua
 */
@Data
public class UserRegisterRequest implements Serializable {

	// 序列化UID，防止后端更改后，前端传参时，出现不识别的错误误
	// 因为前端传参时，会使用默认的序列化方式，而默认的序列化方式是JDK的序列化方式，
	// 它序列化后的字符串，会带有类型信息，而后台的实现是使用Kryo的序列化方式，
	// 它序列化后的字符串，不会带有类型信息，所以就会出现不识别的错误
	// 解决方案：自定义一个序列化方式，让后台使用自定义的序列化方式
	private static final long serialVersionUID = 3191241716230486735L;

	private String userAccount;

	private String userPassword;

	private String checkPassword;

	private String stuId;

}
