-- 用户表
create table if not exists cloud_friends.user
(
    id            bigint auto_increment
    primary key,
    username      varchar(256)                       null comment '用户昵称',
    user_account  varchar(256)                       null comment '账号',
    avatar_url    varchar(1024)                      null comment '头像',
    gender        tinyint                            null comment '性别',
    user_password varchar(512)                       not null comment '密码',
    phone         varchar(128)                       null comment '电话',
    email         varchar(512)                       null comment '邮箱',
    user_status   int      default 0                 not null comment '用户状态 0正常',
    create_time   datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    update_time   datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '修改时间',
    is_delete     tinyint  default 0                 not null comment '是否删除',
    user_role     int      default 0                 null comment '用户角色 0-普通用户 1-管理员',
    tags          varchar(1024)                      null comment 'json 标签列表',
    profile       varchar(512)                       null comment '用户简介'
);

-- 队伍表
create table cloud_friends.team
(
    id               bigint auto_increment
        primary key,
    team_name        varchar(256)                       not null comment '队伍名称',
    team_description varchar(1024)                      null comment '队伍描述',
    max_num          int      default 1                 not null comment '最大人数',
    expire_time      datetime                           not null comment '过期时间',
    captain_id       bigint                             not null comment '队长id',
    team_status      int      default 0                 not null comment '队伍状态 0 - 公开，1 - 私有，2 - 加密',
    team_password    varchar(512)                       null comment '入队密码',
    create_time      datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    update_time      datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '修改时间',
    is_delete        tinyint  default 0                 not null comment '是否删除',
    current_num      int                                null comment '当前队伍人数',
    founder_id       bigint                             null comment '队伍创建人id',
    team_avatar       varchar(1024)                       null comment '队伍头像'
);

-- 用户 - 队伍 关系表
create table if not exists cloud_friends.user_team
(
    id            bigint auto_increment
    primary key,
    user_id       bigint                             not null comment '用户id',
    team_id       bigint                             not null comment '队伍id',
    join_time   datetime default                     null comment '加入时间',
    create_time   datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    update_time   datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '修改时间',
    is_delete     tinyint  default 0                 not null comment '是否删除'
);

-- 标签表
create table cloud_friends.tag
(
    id          bigint auto_increment
        primary key,
    tag_name    varchar(256)                       not null comment '标签名称',
    user_id     bigint                             null comment '创建用户id',
    parent_id   bigint                             null comment '父标签id',
    is_parent   tinyint                            null comment '0-不是父标签，1-是父标签',
    create_time datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    update_time datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '修改时间',
    is_delete   tinyint  default 0                 not null comment '是否删除',
    constraint tag_name
        unique (tag_name)
)
    comment '标签';

create index user_id
    on cloud_friends.tag (user_id);
