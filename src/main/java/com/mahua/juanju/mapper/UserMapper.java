package com.mahua.juanju.mapper;

import com.mahua.juanju.model.domain.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
* @author mahua
* @description 针对表【user(用户表)】的数据库操作Mapper
* @createDate 2023-12-31 14:58:40
* @Entity com.mahua.juanju.model.domain.User
*/
@Mapper
public interface UserMapper extends BaseMapper<User> {

	List<User> getRandomUser();
}




