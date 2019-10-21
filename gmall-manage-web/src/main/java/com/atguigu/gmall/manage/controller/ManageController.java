package com.atguigu.gmall.manage.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.atguigu.gmall.bean.*;
import com.atguigu.gmall.service.ManageService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@CrossOrigin
public class ManageController {

    @Reference
    private ManageService manageService;

    /**
     * 获取一级目录
     * @return
     */
    @RequestMapping("getCatalog1")
    public List<BaseCatalog1> getCatalog1(){
        return manageService.getCatalog1();
    }

    /**
     * 获取二级目录
     * @param catalog1Id
     * @return
     */
    @RequestMapping("getCatalog2")
    public List<BaseCatalog2> getCatalog2(String catalog1Id){
        return manageService.getCatalog2(catalog1Id);
    }

    /**
     * 获取三级分类
     * @param catalog2Id
     * @return
     */
    @RequestMapping("getCatalog3")
    public List<BaseCatalog3> getCatalog3(String catalog2Id){
        return manageService.getCatalog3(catalog2Id);
    }

    /**
     *   // http://localhost:8082/attrInfoList?catalog3Id=61
     * 获取所有的平台属性名称
     * @param catalog3Id
     * @return
     */
    @RequestMapping("attrInfoList")
    public List<BaseAttrInfo> GetAttrInfoList(String catalog3Id){
        return manageService.getAttrList(catalog3Id);

    }

    /**
     * 保存更改的属性值和属性名称
     * @param baseAttrInfo
     */
    @RequestMapping("saveAttrInfo")
    public void saveAttrInfo(@RequestBody BaseAttrInfo baseAttrInfo){
        manageService.addBaseAttrInfo(baseAttrInfo);
    }

    /**
     * 根据属性id获取所有的属性值
     * @param attrId
     * @return
     */
    @RequestMapping("getAttrValueList")
    public List<BaseAttrValue> getAttrValueList(String attrId){
        BaseAttrInfo baseAttrInfo = manageService.getBaseAttrInfo(attrId);
        return baseAttrInfo.getAttrValueList();
    }
    @RequestMapping("spuList")
    public List<SpuInfo> getSpuInfoList(SpuInfo spuInfo){
        return manageService.getSpuInfoList(spuInfo);
    }






}
