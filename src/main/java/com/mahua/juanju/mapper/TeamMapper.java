package com.mahua.juanju.mapper;

import com.mahua.juanju.model.domain.Team;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mahua.juanju.model.vo.UserVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
* @author mahua
* @description 针对表【team(队伍表)】的数据库操作Mapper
* @createDate 2024-02-06 17:56:53
* @Entity com.mahua.juanju.model.domain.Team
*/
@Mapper
public interface TeamMapper extends BaseMapper<Team> {

}




