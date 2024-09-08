package com.mahua.juanju.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.benmanes.caffeine.cache.Cache;
import com.google.gson.Gson;
import com.mahua.juanju.Exception.BusinessException;
import com.mahua.juanju.common.BaseResponse;
import com.mahua.juanju.common.ErrorCode;
import com.mahua.juanju.common.ResultUtils;
import com.mahua.juanju.config.CacheConfig;
import com.mahua.juanju.model.domain.User;
import com.mahua.juanju.model.request.*;
import com.mahua.juanju.model.vo.UserVO;
import com.mahua.juanju.service.UserService;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;


import static com.mahua.juanju.constant.RedisConstants.USER_LOGIN_KEY;
import static com.mahua.juanju.constant.UserConstant.USER_LOGIN_STATUS;

/**
 * 用户接口
 *
 * @author mahua
 */
@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {

	@Resource
	private UserService userService;

	@Resource
	private RedisTemplate redisTemplate;

	@Resource
	private Cache<String,List<UserVO>> userCache;
	@PostMapping("/register")
//	@Operation(summary = "用户注册")
	public BaseResponse<Long> userRegister(@RequestBody UserRegisterRequest userRegisterRequest){
		if(userRegisterRequest == null){
			throw new BusinessException(ErrorCode.NULL_PARAMS);
		}

		String userAccount = userRegisterRequest.getUserAccount();
		String userPassword = userRegisterRequest.getUserPassword();
		String checkPassword = userRegisterRequest.getCheckPassword();
		String stuId = userRegisterRequest.getStuId();
		if(StringUtils.isAnyBlank( userAccount,userPassword,checkPassword,stuId)){
			throw new BusinessException(ErrorCode.NULL_PARAMS);
		}
		long result = userService.userRegister(userAccount, userPassword, checkPassword,stuId);
		return ResultUtils.success(result);
	}


	@PostMapping("/login")
//	@Operation(summary = "用户登录")
	public BaseResponse<String> userLogin(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request){
		//@RequestBody 注解：将前端传来的JSON参数和UserRegisterRequest参数进行绑定，并自动将参数注入到UserRegisterRequest对象中
		if(userLoginRequest == null){
			throw new BusinessException(ErrorCode.NULL_PARAMS);
		}
		String userAccount = userLoginRequest.getUserAccount();
		String userPassword = userLoginRequest.getUserPassword();
		if(StringUtils.isAnyBlank( userAccount,userPassword)){
			throw new BusinessException(ErrorCode.NULL_PARAMS);
		}
		String token = userService.doLogin(userAccount, userPassword,request);
		return ResultUtils.success(token,"登陆成功");

	}

	@PostMapping("/logout")
//	@Operation(summary = "用户退出登录")
	public BaseResponse<Integer> userLogout( HttpServletRequest request){
		//@RequestBody 注解：将前端传来的JSON参数和UserRegisterRequest参数进行绑定，并自动将参数注入到UserRegisterRequest对象中
		if(request == null){
			throw new BusinessException(ErrorCode.NULL_PARAMS);
		}
		int result =  userService.logout(request);
		return ResultUtils.success(result);
	}


	@GetMapping("/search")
//	@Operation(summary = "用户搜索")
	public BaseResponse<List<User>> searchUsers(String username, HttpServletRequest request){
		if(!userService.isAdmin(request)){
			throw new BusinessException(ErrorCode.NO_AUTHORIZED);
		}
		QueryWrapper<User> queryWrapper = new QueryWrapper<>();
		if(StringUtils.isNotBlank(username)){
			queryWrapper.like("username",username);
		}
		List<User> userList = userService.list(queryWrapper);
		// 脱敏
		List<User> list = userList.stream().map(user -> userService.getSafetyUser(user)).collect(Collectors.toList());
		return ResultUtils.success(list);
	}

	@GetMapping("/recommend")
//	@Operation(summary = "用户推荐")
	public BaseResponse<Page<UserVO>> recommendUsers(long pageNum){
		Page<UserVO> userPageList = userService.recommend(pageNum);
		return ResultUtils.success(userPageList);

	}


//	@Operation(summary = "获取最匹配的用户")
	@GetMapping("/match")
	public BaseResponse<List<UserVO>> matchUsers( long num,HttpServletRequest request){
		if (num < 0 || num >= 20){
			throw new BusinessException(ErrorCode.PARAMS_ERROR);
		}
		User loginUser = userService.getLoginUser(request);
		List<UserVO> userVOList = userCache.get(String.valueOf(loginUser.getId()), (key) ->
				userService.matchUsers(num, loginUser)
		);
//		List<UserVO> userVOList = userService.matchUsers(num, loginUser);
		return ResultUtils.success(userVOList);
	}
	@GetMapping("/search/tags")
//	@Operation(summary = "根据标签搜索用户")
	public BaseResponse<IPage<User>> searchUsersByTags(Long pageSize,Long pageNum, @RequestParam(required = false) List<String> tagNameList){
		if(CollectionUtils.isEmpty(tagNameList)){
			throw new BusinessException(ErrorCode.NULL_PARAMS);
		}
		IPage<User> userList = userService.searchUsersByTags(pageSize,pageNum,tagNameList);
		return ResultUtils.success(userList);
	}

	@PostMapping("/update")
//	@Operation(summary = "更新用户信息")
	public BaseResponse<Integer> update(@RequestBody User user, HttpServletRequest request){
		//1.校验参数是否为空
		if(user == null){
			throw new BusinessException(ErrorCode.NULL_PARAMS);
		}
		User loginUser = userService.getLoginUser(request);
		int result = userService.updateUser(user,loginUser);
		return ResultUtils.success(result);
	}

	@PostMapping("/update/password")
//	@Operation(summary = "修改用户密码")
	public BaseResponse updatePassword(@RequestBody UserUpdatePasswordRequest userUpdatePasswordRequest, HttpServletRequest request){
		String oldPassword = userUpdatePasswordRequest.getOldPassword();
		String newPassword = userUpdatePasswordRequest.getNewPassword();
		String checkPassword = userUpdatePasswordRequest.getCheckPassword();
		userService.updatePassword(oldPassword, newPassword, checkPassword,request);
		return ResultUtils.success("修改密码成功");
	}

	@PostMapping("/delete")
//	@Operation(summary = "用户删除")
	public BaseResponse<Boolean> delete(long id,HttpServletRequest request){
		if(!userService.isAdmin(request)){
			throw new BusinessException(ErrorCode.NO_AUTHORIZED);
		}
		if(id <= 0){
			throw new BusinessException(ErrorCode.PARAMS_ERROR);
		}
		boolean result = userService.removeById(id);
		return ResultUtils.success(result);
	}

	@GetMapping("/current")
//	@Operation(summary = "获取当前用户信息")
	public BaseResponse<User> getCurrentUser(HttpServletRequest request){
		if (request == null) {
			throw new BusinessException(ErrorCode.NO_LOGIN);
		}
		String token = request.getHeader("authorization");
		log.error(token);
		if(token == null){
			throw new BusinessException(ErrorCode.NO_LOGIN);
		}
		String key = USER_LOGIN_KEY + token;
		String userJson = (String) redisTemplate.opsForValue().get(key);
		if (StringUtils.isBlank(userJson)){
			throw new BusinessException(ErrorCode.NO_LOGIN);
		}
		Gson gson = new Gson();
		User currentUser = gson.fromJson(userJson,User.class);
		if(currentUser == null){
			throw new BusinessException(ErrorCode.NO_LOGIN);
		}
		long userId = currentUser.getId();
		User user = userService.getById(userId);
		User safetyUser = userService.getSafetyUser(user);
		return ResultUtils.success(safetyUser);
	}

	@GetMapping("/list")
	public BaseResponse<Page<UserVO>> getUserByPage(UserQueryRequest userQueryRequest){
		Page<UserVO> userVOPage = userService.getUserByPage(userQueryRequest);
		return ResultUtils.success(userVOPage);
	}

	@PostMapping("/register/user/single")
	public BaseResponse<Boolean> registerUserSingle(@RequestBody AdminUserRegisterRequest adminUserRegisterRequest, HttpServletRequest request){
		boolean result = userService.registerUserSingle(adminUserRegisterRequest);
		return ResultUtils.success(result);
	}

	@PostMapping("/register/user/multiple")
	public BaseResponse<Boolean> registerUserMultiple(MultipartFile file){
		boolean result = userService.registerUserMultiple(file);
		return ResultUtils.success(result);
	}
}
