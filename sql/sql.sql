use juanju;

create table user_team
(
    id          bigint auto_increment comment '用户_队伍id'
        primary key,
    user_id     bigint                             null comment '用户id',
    team_id     bigint                             null comment '队伍id',
    join_time   datetime default CURRENT_TIMESTAMP null comment '加入时间',
    create_time datetime default CURRENT_TIMESTAMP null comment '创建时间',
    update_time datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间',
    is_delete   tinyint  default 0                 null comment '是否删除'
)
    comment '用户_队伍表';

create table user
(
    id            bigint auto_increment comment '用户id'
        primary key,
    username      varchar(255) default '暂无昵称'        null comment '用户昵称',
    user_account  varchar(255)                           null comment '账号',
    avatar_url    varchar(1024)                          null comment '用户头像',
    gender        tinyint      default 0                 null comment '性别',
    user_password varchar(255)                           not null comment '密码',
    phone         varchar(128)                           null comment '电话',
    email         varchar(255)                           null comment '邮箱',
    major         varchar(255) default '无'              null comment '专业',
    user_status   int          default 0                 null comment '状态：0-正常，1-禁用',
    create_time   datetime     default CURRENT_TIMESTAMP null comment '创建时间',
    update_time   datetime     default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间',
    is_delete     tinyint      default 0                 null comment '是否删除',
    user_role     tinyint      default 0                 null comment '用户角色：user/admin',
    stu_id        varchar(255)                           null comment '学号',
    tags          varchar(1024)                          null comment '标签列表',
    profile       varchar(512) default '暂无简介'        null comment '''个人简介'''
)
    comment '用户表';

create table team
(
    id            bigint auto_increment comment '队伍id'
        primary key,
    name          varchar(255)                            not null comment '队伍名称',
    description   varchar(1024) default '暂无描述'        null comment '队伍描述',
    avatar_url    varchar(1024)                           null comment '队伍头像',
    max_num       tinyint       default 5                 null comment '队伍最大人数',
    team_password varchar(255)                            null comment '加入队伍密码',
    expire_time   datetime                                null comment '队伍过期时间',
    user_id       bigint                                  not null comment '创始人id',
    category      varchar(255)                            not null comment '队伍分类',
    status        tinyint       default 0                 not null comment '队伍状态：0-公开 1-私有 2-加密',
    create_time   datetime      default CURRENT_TIMESTAMP null comment '创建时间',
    update_time   datetime      default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间',
    is_delete     tinyint       default 0                 null comment '是否删除'
)
    comment '队伍表';
-- create table tag
-- (
--     id          bigint auto_increment comment 'id'
--         primary key,
--     tag_name    varchar(255)                       null comment '标签名称',
--     user_id     bigint                             null comment '用户id',
--     parent_id   bigint                             null comment '父级id',
--     is_parent   tinyint                            null comment '是否为父级:0不是父标签，1是父标签',
--     create_time datetime default CURRENT_TIMESTAMP null comment '创建时间',
--     update_time datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间',
--     is_delete   tinyint  default 0                 not null comment '是否删除',
--     constraint idx_userId
--         unique (user_id),
--     constraint uniIdx_tageName
--         unique (tag_name)
-- )
--     comment '标签表';

