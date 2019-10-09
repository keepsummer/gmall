package com.atguigu.gmall.bean;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.io.Serializable;
@Data
public class UserInfo implements Serializable {
    @Id // 表示主键
    @Column // 表示字段
    @GeneratedValue(strategy = GenerationType.IDENTITY) // mysql获取方式：表示获取主键自增 oracle: GenerationType.AUTO
    private String id;
    @Column
    private String loginName;
    @Column
    private String nickName;
    @Column
    private String passwd;
    @Column
    private String name;
    @Column
    private String phoneNum;
    @Column
    private String email;
    @Column
    private String headImg;
    @Column
    private String userLevel;
}
