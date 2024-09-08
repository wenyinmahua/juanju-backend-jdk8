package com.mahua.juanju.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mahua.juanju.common.BaseResponse;
import com.mahua.juanju.model.domain.User;
import com.baomidou.mybatisplus.extension.service.IService;
import com.mahua.juanju.model.request.AdminUserRegisterRequest;
import com.mahua.juanju.model.request.UserQueryRequest;
import com.mahua.juanju.model.vo.UserVO;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 用户服务
 *
* @author mahua
*
*/
public interface  UserService extends IService<User> {

	/**
	 * 用户注册
	 * @param userAccount 用户账号
	 * @param userPassword 用户密码
	 * @param checkPassword 用户校验密码
	 * @return 新用户id
	 */
	long userRegister(String userAccount, String userPassword, String checkPassword,String sutId);

	/**
	 * 用户登录
	 *
	 * @param userAccount  登录账号
	 * @param userPassword 登录密码
	 * @return token
	 */
	String doLogin(String userAccount, String userPassword, HttpServletRequest request);

	/**
	 *  用户脱敏
	 * @param user 需要脱敏的用户
	 * @return 脱敏后的用户
	 */
	User getSafetyUser(User user);

	/**
	 * 用户注销
	 * @param request
	 */
	int logout(HttpServletRequest request);

	IPage<User> searchUsersByTags(Long pageSize, Long pageNum, List<String> tagNameList);

	List<User> searchUsersByTags(List<String> tagNameList);

	int updateUser(User user, User loginUser);

	User getLoginUser(HttpServletRequest request);

	boolean isAdmin(HttpServletRequest request);

	boolean isAdmin(User loginUser);

	/**
	 * 匹配用户
	 * @param num
	 * @param loginUser
	 * @return
	 */
	List<UserVO> matchUsers(long num, User loginUser);

	Page<UserVO> recommend(long pageNum);

	void updatePassword(String oldPassword, String newPassword, String checkPassword, HttpServletRequest request);

	Page<UserVO> getUserByPage(UserQueryRequest userQueryRequest);

	boolean registerUserSingle(AdminUserRegisterRequest adminUserRegisterRequest);

	boolean registerUserMultiple(MultipartFile file);

	UserVO getUserInfoByUserAccount(String userAccount);
}
