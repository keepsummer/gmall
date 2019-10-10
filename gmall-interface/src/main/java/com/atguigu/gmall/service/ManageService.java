package com.atguigu.gmall.service;

import com.atguigu.gmall.bean.*;

import java.util.List;

public interface ManageService {
    public List<BaseCatalog1> getCatalog1();

    public List<BaseCatalog2> getCatalog2(String catalog1Id);

    public List<BaseCatalog3> getCatalog3(String catalog2Id);

    public List<BaseAttrInfo> getAttrList(String catalog3Id);

    public void addBaseAttrInfo(BaseAttrInfo baseAttrInfo);

    public List<BaseAttrValue> getBaseAttrValue(String attrId);
    public BaseAttrInfo getBaseAttrInfo(String attrId);
    /**
     * http://localhost:8082/spuList?catalog3Id=61
     */
    public List<SpuInfo> getSpuInfoList(SpuInfo spuInfo);

    /**
     * 获取销售属性的集合
     * @return
     */
    public List<BaseSaleAttr>  baseSaleAttrList();

    /**
     * 存储spu，
     * @param spuInfo
     */
    public void saveSpuInfo(SpuInfo spuInfo);


}
