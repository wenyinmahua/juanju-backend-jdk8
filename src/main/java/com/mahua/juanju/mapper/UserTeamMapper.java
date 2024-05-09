package com.mahua.juanju.mapper;

import com.mahua.juanju.model.domain.UserTeam;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
* @author mahua
* @description 针对表【user_team(用户_队伍表)】的数据库操作Mapper
* @createDate 2024-02-06 18:00:25
* @Entity com.mahua.juanju.model.domain.UserTeam
*/
@Mapper
public interface UserTeamMapper extends BaseMapper<UserTeam> {

}




