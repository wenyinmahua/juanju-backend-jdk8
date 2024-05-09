package com.mahua.juanju.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mahua.juanju.Exception.BusinessException;
import com.mahua.juanju.common.ErrorCode;
import com.mahua.juanju.model.domain.User;
import com.mahua.juanju.model.vo.UserVO;
import com.mahua.juanju.service.UserService;
import com.mahua.juanju.mapper.UserMapper;
import com.mahua.juanju.utils.AlgorithmUtils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.math3.util.Pair;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.mahua.juanju.constant.RedisConstants.USER_LOGIN_KEY;
import static com.mahua.juanju.constant.RedisConstants.USER_LOGIN_TIME;
import static com.mahua.juanju.constant.SystemConstants.Page_Size;
import static com.mahua.juanju.constant.UserConstant.ADMIN_ROLE;

/**
 * 用户服务实现类
 *
 * @author mahua
 */
@Slf4j
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService{
	@Resource
	private UserMapper userMapper;
	/**
	 * 盐值，混淆密码
	 */
	private static final String SALT = "mahua";

	@Resource
	private RedisTemplate redisTemplate;

	/**
	 * 启用随机展示用户最低限度
	 */
	public static final int MINIMUM_ENABLE_RANDOM_USER_NUM = 10;

	@Override
	public long userRegister(String userAccount, String userPassword, String checkPassword,String stuId) {
		//1.校验
		if(StringUtils.isAnyBlank(userAccount,userPassword,checkPassword,stuId)){
			throw new BusinessException(ErrorCode.NULL_PARAMS,"参数为空");
		}
		if (userAccount.length() < 4){
			throw new BusinessException(ErrorCode.USER_ACCOUNT_ERROR,"账号长度不能小于4");
		}
		if (userPassword.length() < 8 || !userPassword.equals(checkPassword)){
			throw new BusinessException(ErrorCode.USER_PASSWORD_ERROR,"密码长度不能小于8");
		}
		if (stuId.length() != 10){
			throw new BusinessException(ErrorCode.PARAMS_ERROR,"学号长度必须为10");
		}
		//账号不能含有特殊字符
		String validPattern = "[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
		Matcher matcher = Pattern.compile(validPattern).matcher(userAccount);
		if(matcher.find()){
			throw new BusinessException(ErrorCode.USER_ACCOUNT_ERROR,"账号不能含有特殊字符");
		}

		//账号不能重复
		//创建一个QueryWrapper对象，该对象用于封装查询条件，用于查询User实体类的相关信息。
		QueryWrapper<User> queryWrapper = new QueryWrapper<>();
		//判断插入的userAccount在数据库中是否已有
		queryWrapper.eq("user_account",userAccount);
		long count = userMapper.selectCount(queryWrapper);
		if(count > 0){
			throw new BusinessException(ErrorCode.USER_ACCOUNT_EXIST,"账号已存在");
		}
		//学号不能重复
		queryWrapper = new QueryWrapper<>();
		queryWrapper.eq("stu_id",stuId);
		count = userMapper.selectCount(queryWrapper);
		if(count > 0){
			throw new BusinessException(ErrorCode.STU_ID_EXIST,"学号已存在");
		}
		//2.加密
		String encryptPassword = DigestUtils.md5DigestAsHex((SALT+userPassword).getBytes());
		//3.插入数据
		User user = new User();
		user.setUserAccount(userAccount);
		user.setUserPassword(encryptPassword);
		user.setStuId(stuId);
		userMapper.insert(user);
		log.info("用户注册成功"+user.getId());
		return user.getId() ;
	}

	@Override
	public String doLogin(String userAccount, String userPassword, HttpServletRequest request) {
		//1.校验
		validateUserLoginMessage(userAccount, userPassword);
		//2.从数据库中取出用户的账户信息
		User user = getUserInDataBase(userAccount, userPassword);
		//3.记录用户的登录态
		String token = getUserLoginState(request, user);
		return token;
	}

	private String getUserLoginState(HttpServletRequest request, User user) {
		User safetyUser = this.getSafetyUser(user);
		String token = UUID.randomUUID().toString();
		Gson gson = new Gson();
		String userJson = gson.toJson(safetyUser);
		String key = USER_LOGIN_KEY + token;
		redisTemplate.opsForValue().set(key,userJson);
		redisTemplate.expire(key,USER_LOGIN_TIME,TimeUnit.SECONDS);
		return token;
	}

	private User getUserInDataBase(String userAccount, String userPassword) {
		userPassword = DigestUtils.md5DigestAsHex((SALT+ userPassword).getBytes());
		//创建一个QueryWrapper对象，该对象用于封装查询条件，用于查询User实体类的相关信息。
		QueryWrapper<User> queryWrapper = new QueryWrapper<>();
		//判断插入的userAccount在数据库中是否已有
		if(userAccount.length() != 10){
			queryWrapper.eq("user_account", userAccount);
		}else{
			queryWrapper.eq("stu_id", userAccount);
		}
		queryWrapper.eq("user_password", userPassword);
		User user = userMapper.selectOne(queryWrapper);
		//用户不存在
		if(user == null){
			log.info("user login failed，userAccount cannot match userPassword");
			throw new BusinessException(ErrorCode.USER_ACCOUNT_ERROR,"账号或密码错误");
		}
		return user;
	}

	private static void validateUserLoginMessage(String userAccount, String userPassword) {
		if(StringUtils.isAnyBlank(userAccount, userPassword)){
			throw new BusinessException(ErrorCode.NULL_PARAMS, "参数为空");
		}
		if (userAccount.length() < 4){
			throw new BusinessException(ErrorCode.USER_ACCOUNT_ERROR,"账号长度不能小于4");
		}
		if (userPassword.length() < 8){
			throw new BusinessException(ErrorCode.USER_PASSWORD_ERROR,"密码长度不能小于8");
		}
		//账号不能含有特殊字符
		String validPattern = "[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
		Matcher matcher = Pattern.compile(validPattern).matcher(userAccount);
		if(matcher.find()){
			throw new BusinessException(ErrorCode.USER_ACCOUNT_ERROR,"账号不能含有特殊字符");
		}
	}


	@Override
	public User getSafetyUser(User user){
		if (user == null){
			throw new BusinessException(ErrorCode.USER_NOT_EXIST,"用户不存在");
		}
		User safetyUser = new User();
		safetyUser.setId(user.getId());
		safetyUser.setUsername(user.getUsername());
		safetyUser.setUserAccount(user.getUserAccount());
		safetyUser.setAvatarUrl(user.getAvatarUrl());
		safetyUser.setGender(user.getGender());
		safetyUser.setPhone(user.getPhone());
		safetyUser.setEmail(user.getEmail());
		safetyUser.setMajor(user.getMajor());
		safetyUser.setUserStatus(user.getUserStatus());
		safetyUser.setCreateTime(user.getCreateTime());
		safetyUser.setUserRole(user.getUserRole());
		safetyUser.setStuId(user.getStuId());
		safetyUser.setTags(user.getTags());
		safetyUser.setProfile(user.getProfile());

		return safetyUser;
	}

	/**
	 * 退出登录
	 *
	 * @param request 请求
	 * @return
	 */
	@Override
	public int logout(HttpServletRequest request) {
		String token = request.getHeader("authorization");
		String key = USER_LOGIN_KEY + token;
		redisTemplate.delete(key);
		return 1;
	}

	@Override
	public IPage<User> searchUsersByTags(Long pageSize, Long pageNum, List<String> tagNameList) {
				if (CollectionUtils.isEmpty(tagNameList)){
			throw new BusinessException(ErrorCode.NULL_PARAMS);
		}
		//SQL查询
		/*QueryWrapper<User> queryWrapper = new QueryWrapper<>();
		for (String tageName : tagNameList) {
			queryWrapper = queryWrapper.like("tags",tageName);
		}
		List<User> userList = userMapper.selectList(queryWrapper);
		userList.forEach(user ->{
			getSafetyUser(user);
		});
		userList.forEach(this::getSafetyUser);
		return userList.stream().map(this::getSafetyUser).collect(Collectors.toList());
*/
		//内存查询
		//1.查询所有的用户
		QueryWrapper<User> queryWrapper = new QueryWrapper<>();
		for (String tageName : tagNameList) {
			queryWrapper = queryWrapper.like("tags",tageName);
		}
		IPage<User> userPageList = userMapper.selectPage(new Page<>(pageNum,pageSize),queryWrapper);
//		List<User> userList = userMapper.selectList(queryWrapper);
		//2.在内存中判断是否包含要求的标签，需要将Json转换为java对象（String）格式,反序列化
		Gson gson = new Gson();
		/*for (User user : userList) {
			String tagStr = user.getTags();
			Set<String> tempTagNameSet = gson.fromJson(tagStr,new TypeToken<Set<String>>(){}.getType());
			for (String tagName : tempTagNameSet) {
				if (!tempTagNameSet.contains(tagName)){
					return false;
				}
			}
			return true;
		}*/
		List<User> userList = userPageList.getRecords();
		userList = userList.stream().filter(user ->{
			String tagStr = user.getTags();
			if (StringUtils.isEmpty(tagStr)){
				return false;
			}
			//Json 转换为String类型
			Set<String> tempTagNameSet = gson.fromJson(tagStr,new TypeToken<Set<String>>(){}.getType());
			//如果取得的结果为空，那么就将其变为一个HashSet对象，Optional执行的内容类似于if得作用
			tempTagNameSet = Optional.ofNullable(tempTagNameSet).orElse(new HashSet<>());
			for (String tagName : tempTagNameSet) {
				if (!tempTagNameSet.contains(tagName)){
					return false;
				}
			}
			return true;
		}).map(this::getSafetyUser).collect(Collectors.toList());
		userPageList.setRecords(userList);
		return userPageList;
	}

	/**
	 * 在内存中查询用户
	 *
	 * @param tagNameList 查询的用户需要具有的标签
	 * @return
	 */

	@Override
	public List<User> searchUsersByTags(List<String> tagNameList){
		if (CollectionUtils.isEmpty(tagNameList)){
			throw new BusinessException(ErrorCode.NULL_PARAMS);
		}
		//SQL查询
		/*QueryWrapper<User> queryWrapper = new QueryWrapper<>();
		for (String tageName : tagNameList) {
			queryWrapper = queryWrapper.like("tags",tageName);
		}
		List<User> userList = userMapper.selectList(queryWrapper);
		userList.forEach(user ->{
			getSafetyUser(user);
		});
		userList.forEach(this::getSafetyUser);
		return userList.stream().map(this::getSafetyUser).collect(Collectors.toList());
*/
		//内存查询
		//1.查询所有的用户
		QueryWrapper<User> queryWrapper = new QueryWrapper<>();
		for (String tageName : tagNameList) {
			queryWrapper = queryWrapper.like("tags",tageName);
		}
		List<User> userList = userMapper.selectList(queryWrapper);
		//2.在内存中判断是否包含要求的标签，需要将Json转换为java对象（String）格式,反序列化
		Gson gson = new Gson();
		/*for (User user : userList) {
			String tagStr = user.getTags();
			Set<String> tempTagNameSet = gson.fromJson(tagStr,new TypeToken<Set<String>>(){}.getType());
			for (String tagName : tempTagNameSet) {
				if (!tempTagNameSet.contains(tagName)){
					return false;
				}
			}
			return true;
		}*/
		userList = userList.stream().filter(user ->{
			String tagStr = user.getTags();
			if (StringUtils.isEmpty(tagStr)){
				return false;
			}
			//Json 转换为String类型
			Set<String> tempTagNameSet = gson.fromJson(tagStr,new TypeToken<Set<String>>(){}.getType());
			//如果取得的结果为空，那么就将其变为一个HashSet对象，Optional执行的内容类似于if得作用
			tempTagNameSet = Optional.ofNullable(tempTagNameSet).orElse(new HashSet<>());
			for (String tagName : tempTagNameSet) {
				if (!tempTagNameSet.contains(tagName)){
					return false;
				}
			}
			return true;
		}).map(this::getSafetyUser).collect(Collectors.toList());
		return userList;


	}

	@Override
	public int updateUser(User user, User loginUser) {
		long userId = user.getId();
		if (userId <= 0){
			throw new BusinessException(ErrorCode.NULL_PARAMS);
		}
		if ((long)user.getId() != (long)loginUser.getId() && !isAdmin(loginUser) ){
			throw new BusinessException(ErrorCode.NO_AUTHORIZED);
		}
		User oldUser = userMapper.selectById(user.getId());
		if (oldUser == null){
			throw new BusinessException(ErrorCode.USER_NOT_EXIST);
		}
		return userMapper.updateById(user);

	}

	@Override
	public User getLoginUser(HttpServletRequest request) {
		if (request == null){
			return null;
		}
		String token = request.getHeader("authorization");
		String key = USER_LOGIN_KEY + token;
		String userJson  = (String) redisTemplate.opsForValue().get(key);
		if (StringUtils.isBlank(userJson)){
			throw new BusinessException(ErrorCode.NO_LOGIN,"请先登录");
		}
		redisTemplate.expire(key,USER_LOGIN_TIME,TimeUnit.SECONDS);
		Gson gson = new Gson();
		User user = gson.fromJson(userJson, User.class);
		return user;
	}

	/**
	 * 根据用户搜索标签（SQL查询版）
	 *
	 * @param tagNameList 用户要拥有的标签
	 * @return
	 */
	@Deprecated//方法已过期的注解
	private List<User> searchUsersByTagsSQL(List<String> tagNameList) {
		if (CollectionUtils.isEmpty(tagNameList)) {
			throw new BusinessException(ErrorCode.NULL_PARAMS);
		}
		//SQL查询
		QueryWrapper<User> queryWrapper = new QueryWrapper<>();
		for (String tageName : tagNameList) {
			queryWrapper = queryWrapper.like("tags",tageName);
		}
		List<User> userList = userMapper.selectList(queryWrapper);
		return userList.stream().map(this::getSafetyUser).collect(Collectors.toList());
	}

	/**
	 * 是否为管理员
	 * @param request
	 * @return
	 */
	public boolean isAdmin(HttpServletRequest request){
		String token = request.getHeader("authorization");
		String key = USER_LOGIN_KEY + token;
		String userJson = (String) redisTemplate.opsForValue().get(key);
		Gson gson = new Gson();
		User user = gson.fromJson(userJson,User.class);
		if (user == null){
			throw new BusinessException(ErrorCode.NO_LOGIN);
		}
		Integer userRole = user.getUserRole();
		if(user == null || userRole != ADMIN_ROLE){
			return false;
		}
		return true;
	}

	@Override
	public boolean isAdmin(User loginUser){
		return loginUser != null && loginUser.getUserRole() == ADMIN_ROLE;
	}

	@Override
	public List<UserVO> matchUsers(long num, User loginUser) {
		String key = String.format("juanju:user:match:%s",loginUser.getId());
		ValueOperations valueOperations = redisTemplate.opsForValue();
		List<UserVO> finalUserList;
		finalUserList = (List<UserVO>) valueOperations.get(key);
		if (finalUserList != null){
			return finalUserList;
		}
		String tags = loginUser.getTags();
		Gson gson = new Gson();
		List<String> tagList = gson.fromJson(tags, new TypeToken<List<String>>() {}.getType());
		QueryWrapper queryWrapper = new QueryWrapper();
		queryWrapper.select("id","tags");
		queryWrapper.isNotNull("tags");
		List<User> userList = this.list(queryWrapper);
		// 用户列表的下标 ==》 相似度
		List<Pair<User,Long>> list = new ArrayList<>();
		// 依次计算所有用户和当前用户的相似度
		for (int i = 0; i < userList.size(); i++) {
			User user = userList.get(i);
			String userTags = user.getTags();
			if (StringUtils.isBlank(userTags) || (long)user.getId() == (long) loginUser.getId()){
				continue;
			}
			List<String> userTagList = gson.fromJson(userTags, new TypeToken<List<String>>() {
			}.getType());
			long distance = AlgorithmUtils.minDistance(tagList, userTagList);
			list.add(new Pair<>(user,distance));
		}
		// 按编辑距离由小到大排序
		List<Pair<User,Long>> topUserPairList = list.stream()
				.sorted((a,b) -> (int)(a.getValue() - b.getValue())).
				limit(num)
				.collect(Collectors.toList());
		List<Long> userIdList = topUserPairList.stream().map(pair -> pair.getKey().getId()).collect(Collectors.toList());
		QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
		userQueryWrapper.in("id",userIdList);

		// 注意 MySQL 会将 id 列表优化，加快查询数据，导致返回的结果是按照 id 递增的顺序返回，而不是排序后的id 顺序返回
		// 解决方案：将所有符合条件的数据全查询出来，之后按照给定的 id 顺序进行排序
		finalUserList = new ArrayList<>();
		// 通过 Map 保存 用户id 以及 用户的信息
		Map<Long,List<User>> userIdUserListMap = this.list(userQueryWrapper).stream().map(user -> getSafetyUser(user)).collect(Collectors.groupingBy(User::getId));
		// 根据 排序好后的 id 从 map 中取出相关的数据插入链表，保证了返回的顺序，同时保存在 Redis 缓存中
		for (Long userId : userIdList){
			UserVO userVO = new UserVO();
			BeanUtils.copyProperties(userIdUserListMap.get(userId).get(0),userVO);
			finalUserList.add(userVO);
		}
		try {
			valueOperations.set(key,finalUserList,24, TimeUnit.HOURS);
		} catch (Exception e) {
			log.info("redis set key err",e);
		}
		return finalUserList;

	}

	@Override
	public Page<UserVO> recommend(long pageNum) {
		long count = this.count();
		if (count <= MINIMUM_ENABLE_RANDOM_USER_NUM){
			Page<User> userPage = page(new Page<>(pageNum,Page_Size));
			List<UserVO> userVOList= userPage.getRecords().stream().map((user)->{
				UserVO userVO = new UserVO();
				BeanUtils.copyProperties(user,userVO);
				return userVO;
			}).collect(Collectors.toList());
			Page<UserVO> userVOPage = new Page<>();
			userVOPage.setRecords(userVOList);
			return userVOPage;
		}
		return this.getRandomUser();
	}

	@Override
	public void updatePassword(String oldPassword, String newPassword, String checkPassword, HttpServletRequest request) {
		if (StringUtils.isAnyBlank(oldPassword,newPassword,checkPassword)){
			throw new BusinessException(ErrorCode.PARAMS_ERROR);
		}
		if (!newPassword.equals(checkPassword)){
			throw new BusinessException(ErrorCode.PARAMS_ERROR,"两次密码不一致");
		}
		if (oldPassword.equals(newPassword)){
			throw new BusinessException(ErrorCode.PARAMS_ERROR,"旧密码和新密码不能一样");
		}
		User loginUser = this.getLoginUser(request);
		QueryWrapper<User> queryWrapper = new QueryWrapper<>();
		queryWrapper.select("id","user_password").eq("id",loginUser.getId());
		User user = getOne(queryWrapper);
		oldPassword = DigestUtils.md5DigestAsHex((SALT+oldPassword).getBytes());
		if (!oldPassword.equals(user.getUserPassword())){
			throw new BusinessException(ErrorCode.PARAMS_ERROR,"原密码错误");
		}
		newPassword = DigestUtils.md5DigestAsHex((SALT+newPassword).getBytes());
		user.setUserPassword(newPassword);
		int update = updateUser(user, loginUser);
		if (update <= 0){
			throw new BusinessException(ErrorCode.SYSTEM_ERROR);
		}
	}

	private Page<UserVO> getRandomUser() {
		List<User> randomUserList = userMapper.getRandomUser();
		List<UserVO> userVOList = randomUserList.stream().map(user -> {
			UserVO userVO = new UserVO();
			BeanUtils.copyProperties(user,userVO);
			return userVO;
		}).collect(Collectors.toList());
		Page<UserVO> userVOPage= new Page<>();
		userVOPage.setRecords(userVOList);
		return userVOPage;
	}

}




