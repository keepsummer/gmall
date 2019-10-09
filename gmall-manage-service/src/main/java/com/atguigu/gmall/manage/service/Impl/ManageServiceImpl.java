package com.atguigu.gmall.manage.service.Impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.atguigu.gmall.bean.BaseAttrInfo;
import com.atguigu.gmall.bean.BaseCatalog1;
import com.atguigu.gmall.bean.BaseCatalog2;
import com.atguigu.gmall.bean.BaseCatalog3;
import com.atguigu.gmall.manage.Mapper.BaseAttrInfoMapper;
import com.atguigu.gmall.service.ManageService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
@Service
public class ManageServiceImpl implements ManageService {

    @Autowired
    private BaseAttrInfoMapper baseAttrInfoMapper;


    @Override
    public List<BaseCatalog1> getCatalog1() {
        return null;
    }

    @Override
    public List<BaseCatalog2> getCatalog2(String catalog1Id) {
        return null;
    }

    @Override
    public List<BaseCatalog3> getCatalog3(String catalog2Id) {
        return null;
    }

    @Override
    public List<BaseAttrInfo> getAttrList(String catalog3Id) {
        return null;
    }
}
