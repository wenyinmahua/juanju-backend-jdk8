package com.mahua.juanju.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mahua.juanju.model.domain.UserTeam;
import com.mahua.juanju.service.UserTeamService;
import com.mahua.juanju.mapper.UserTeamMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
* @author mahua
* @description 针对表【user_team(用户_队伍表)】的数据库操作Service实现
* @createDate 2024-02-06 18:00:25
*/
@Service
public class UserTeamServiceImpl extends ServiceImpl<UserTeamMapper, UserTeam>
    implements UserTeamService{

	@Resource
	private UserTeamMapper userTeamMapper;
}




