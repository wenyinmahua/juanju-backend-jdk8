package com.mahua.juanju.model.vo;

import lombok.Data;

import java.io.Serializable;


/**
 * 用户表
 *
 */
@Data
public class UserVO implements Serializable {
    private static final long serialVersionUID = 8696175249543933002L;

    private Long id;


    /**
     * 用户昵称
     */
    private String username;

    /**
     * 账号
     */
    private String userAccount;

    /**
     * 用户头像
     */
    private String avatarUrl;

    /**
     * 性别
     */
    private Integer gender;


    /**
     * 电话
     */
    private String phone;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 专业
     */
    private String major;

    /**
     * 状态：0-正常，1-禁用
     */
    private Integer userStatus;

    /**
     * 用户角色：
     * 0-普通用户
     * 1-管理员
     * 2-超级管理员
     */
//    private Integer userRole;

    /**
     * 学号
     */
//    private String stuId;

    /**
     * 标签列表
     */
    private String tags;

    /**
     * 个人简介
     */
    private String profile;

}