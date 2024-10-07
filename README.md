# 聚友阁
介绍：帮助大家找到志同道合的朋友，移动端H5页面（尽量兼容PC端）

# 需求分析

1. 用户去添加标签，标签的分类（要添加哪些标签，怎么把标签进行分类）学习方向java/c++，工作/大学
2. 主动搜索：允许用户跟着标签去搜索其他用户
    1. Redis缓存
3. 组队
    1. 创建队伍
    2. 加入队伍
    3. 根据标签查询队伍
    4. 邀请其他人
4. 允许用户去修改标签
5. 匹配队友（找相同标签的队友）
    - 编辑距离算法
6. 随机推荐 10 个用户

# 技术栈

### 后端

1. Java编程语言 + SpringBoot框架
2. SpringMVC+MyBatis+MyBatis-Plus（提高开发效率）
3. MySQL数据库
4. Swagger+Knifej接口文档


# 组队

### 需求分析

用户可以创建一个队伍，设置队伍的人数、队伍名称（标题）、描述、超时时间  P0

> 队长、剩余人数
>
> 聊天？
>
> 公开或加密
>
> 不展示已经过期的队伍
>
> 根据标签搜索队伍

展示队伍列表，根据标签或名称搜索队伍 P0

修改队伍信息 P0~P1

用户创建队伍最多 5 个

用户可以加入队伍（其他人、未满、未过期），允许加入多个队伍，但是要有上限、退出之后得等24小时才能加入  	P0

> 是否要队长同意?筛选审批？

用户可以退出队伍（如果队长退出，那么权限就交给第二个用户--先来后到） 	 P1

队长可以解散队伍 	 P0

分享队伍-->要求其他用户加入队伍  	P1

队伍人满之后发送消息通知	P1





### 数据库表设计

队伍表 team

字段：

- id 主键 bigint （最简单、连续、放url上比较简短，但缺点是爬虫）
- name 队伍名称
- description 描述
- avatar_url 队伍头像
- max_num 最大人数
- expire_time 过期时间
- userId 创建人id
- status 0 - 公开，1 - 私有
- password 加入队伍使用的密码
- create_time 创建时间
- update_time 更新时间
- is_detele 是否删除

```sql
create table team
(
    id    bigint  auto_increment  primary key  comment '队伍id',
    name         varchar(255)       unique           not null comment '队伍名称',
    description  varchar(1024)      default '暂无描述'    null comment '队伍描述',
    avatar_url    varchar(1024)                          null comment '队伍头像',
    max_num        tinyint      default 5                null comment '队伍最大人数',
    team_password varchar(255)                           null comment '加入队伍密码',
    expire_time   varchar(128)                           null comment '队伍过期时间',
    user_id       bigint                                 not null comment '创始人id',
    category  varchar(255)                               not null comment '队伍分类',
    state     tinyint      default 0    not null comment '队伍状态：0-公开 1-私有 2-加密',
	create_time   datetime     default CURRENT_TIMESTAMP null comment '创建时间',
    update_time   datetime     default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间',
    is_delete     tinyint      default 0                 null comment '是否删除'
)
    comment '队伍表';
```



维护的两个关系：

1. 用户加了哪些队伍？
2. 队伍有哪些用户？

> 方式：
>
> 1. 建立用户-队伍关系表 team_id user_id
> 2. 用户表补充已加入的队伍字段、队伍表补充已加入的用户字段



用户 -- 队伍表 user_team

字段：

- id 主键
- user_id 用户id
- team_id 队伍id
- join_time 加入时间
- create_time 创建时间
- update_time 更新时间
- is_detele 是否删除

```sql
-- auto-generated definition
create table user_team
(
    id    bigint  auto_increment  primary key  comment '用户_队伍id',
    user_id    bigint   comment '用户id',
    team_id    bigint   comment '队伍id',
    join_time  datetime     default CURRENT_TIMESTAMP null comment '加入时间',
	create_time   datetime     default CURRENT_TIMESTAMP null comment '创建时间',
    update_time   datetime     default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间',
    is_delete     tinyint      default 0                 null comment '是否删除'
)
    comment '用户_队伍表';
```



### 系统（接口）设计

#### 1.创建队伍

1. 请求参数是否为空
2. 是否登录，未登录不允许创建
3. 校验信息
   1. 队伍人数 >=1 <=20
   2. 队伍标题 <= 20
   3. 描述 <= 512
   4. status 是否为公开、不传默认为公开
   5. 如果 status 是加密，一定要有密码，且密码 <= 32
   6. 超过时间 > 当前时间
4. 插入队伍信息到队伍表
5. 插入用户 => 队伍关系到关系表



#### 2.查询队伍列表

分页展示队伍列表，根据名称搜索队伍P0，信息流中不展示已过期的队伍。

1. 从请求参数中取出队伍名称，如果存在则作为查询条件
2. 不展示已过期的队伍（根据过期时间筛选）
3. 可以通过某个关键词对名称和描述进行查询
4. 只有管理员才能查看加密还有非公开的房间
5. 关联查询已加入队伍的用户信息



#### 3. 修改用户信息

1. 判断请求是否为空
2. 查询队伍是都存在
3. 只有管理员或者队伍的创建者可以修改
4. 如果用户传入的新值和就职一致，那么就不用update（可以自行实现，降低数据库的使用次数）
   1. name、descript、avatarUrl、teamPassword、expireTime、category、status
5. 如果队伍状态改为加密，必须有密码
6. 更新成功



#### 4.用户可以加入队伍

其他人，未满、未过期，允许加入多个队伍，但是要有上限

1. 用户最多只能加入五个队伍
2. 只能加入未满、未过期的队伍
3. 不能加入自己的队伍，不能重复加入已加入的队伍（幂等性）
4. 禁止加入私有的队伍
5. 如果加入的队伍是已加密的，必须密码匹配才行
6. 新增队伍 -- 用户关联信息

> 注意，并发请求可能会出现问题

一定要加上事务注解



#### 5.用户退出队伍

请求参数：队伍id

1. 校验请求参数
2. 校验队伍是否存在
3. 校验我是否加入队伍
4. 如果队伍
   1. 只剩一个人，队伍解散
   2. 还有其他人
      1. 如果是队长退出队伍，则权限移交给第二早加入的用户 ---- 先来后到
      2. 非队长，自己退出队伍



#### 6.队长可以解散队伍

请求参数：队伍 id

业务流程：

1. 校验请求参数
2. 判断队伍是否存在
3. 检验你是不是队伍的队长
4. 移除所有已经加入队伍的关联信息
5. 删除队伍

## Caffeine的使用

 经过本地主机 + 远程 Redis 数据库测试，在加入缓存 Caffeine 后，查询时间从 230 ms 左右缩短至 120 ms左右

Caffeine 的默认驱逐策略是基于容量的 LRU（Least Recently Used，最近最少使用）算法。这意味着当缓存的大小达到预先设定的最大容量时，它会淘汰最近最少使用的条目来为新的条目腾出空间。这种策略是通过内部实现的双队列结构来优化的，结合了 LRU 和 LFU（Least Frequently Used，最少使用频率）的一些特性，使其更加灵活和适应不同的访问模式。

1. 引入 maven 依赖，注意最新版本不一定适配 jdk8

   ```xml
        <dependency>
            <groupId>com.github.ben-manes.caffeine</groupId>
            <artifactId>caffeine</artifactId>
            <version>2.9.3</version>
        </dependency>
   ```

2. 配置 Caffeine

   ```java
   @Configuration
   public class CacheConfig {
   
       @Bean
       public Cache<String, List<UserVO>> userCache() {
           return Caffeine.newBuilder()
                   // 设置最后一次写入后经过固定时间过期
                   .expireAfterWrite(24, TimeUnit.HOURS)
                   // 初始的缓存空间大小
                   .initialCapacity(100)
                   // 缓存的最大条数
                   .maximumSize(1000)
                   .build();
       }
   
   }
   ```

3. 使用 Caffeine 将数据放入缓存中

   1. 注入 userCache

      ```java
          @Resource
          private Cache<String,List<UserVO>> userCache;
   ```
   
   2. 使用 userCache 查询缓存
   
   ```java
           List<UserVO> userVOList = userCache.get(String.valueOf(loginUser.getId()), (key) ->
             userService.matchUsers(num, loginUser));
```


