package com.mahua.juanju.interceptor;

import com.mahua.juanju.Exception.BusinessException;
import com.mahua.juanju.common.ErrorCode;
import com.mahua.juanju.constant.RedisConstants;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.concurrent.TimeUnit;

public class RefreshTokenInterceptor implements HandlerInterceptor{
	private StringRedisTemplate stringRedisTemplate;

	public RefreshTokenInterceptor(StringRedisTemplate stringRedisTemplate) {
		this.stringRedisTemplate = stringRedisTemplate;
	}

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		String token = request.getHeader("authorization");
		if (StringUtils.isBlank(token)){
			return true;
		}
		String userJson = stringRedisTemplate.opsForValue().get(RedisConstants.USER_LOGIN_KEY+token);
		if (userJson == null){
			response.setStatus(401);
			throw new BusinessException(ErrorCode.NO_LOGIN,"用户登录过期");
		}
		stringRedisTemplate.expire(RedisConstants.USER_LOGIN_KEY+token,RedisConstants.USER_LOGIN_TIME, TimeUnit.SECONDS);
		return true;
	}
}
