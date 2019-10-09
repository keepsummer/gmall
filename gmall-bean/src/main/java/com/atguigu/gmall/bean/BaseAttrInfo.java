package com.atguigu.gmall.bean;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Data
public class BaseAttrInfo implements Serializable {

    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY) // mysql获取方式：表示获取主键自增 oracle: GenerationType.AUTO
    private String id;
    @Column
    private String attrName;
    @Column
    private String catalog3Id;

    @Transient // 表示在数据库中并没有该字段，而是业务需要使用！
    private List<BaseAttrValue> attrValueList;


}
