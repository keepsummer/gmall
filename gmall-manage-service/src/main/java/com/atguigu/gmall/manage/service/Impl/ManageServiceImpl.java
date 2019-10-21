package com.atguigu.gmall.manage.service.Impl;


import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.bean.*;
import org.apache.commons.lang3.StringUtils;
import com.atguigu.gmall.config.RedisUtil;
import com.atguigu.gmall.manage.constant.ManageConst;
import com.atguigu.gmall.manage.mapper.*;
import com.atguigu.gmall.service.ManageService;

import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.exceptions.JedisConnectionException;


import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class ManageServiceImpl implements ManageService {

    @Autowired
    private BaseCatalog1Mapper baseCatalog1Mapper;
    @Autowired
    private BaseCatalog2Mapper baseCatalog2Mapper;
    @Autowired
    private BaseCatalog3Mapper baseCatalog3Mapper;
    @Autowired
    private BaseAttrInfoMapper baseAttrInfoMapper;
    @Autowired
    private BaseAttrValueMapper baseAttrValueMapper;
    @Autowired
    private SpuInfoMapper spuInfoMapper;
    @Autowired
    private BaseSaleAttrMapper baseSaleAttrMapper;
    @Autowired
    private SpuImageMapper spuImageMapper;
    @Autowired
    private SpuSaleAttrMapper spuSaleAttrMapper;
    @Autowired
    private  SpuSaleAttrValueMapper spuSaleAttrValueMapper;
    @Autowired
    private SkuImageMapper skuImageMapper;
    @Autowired
    private SkuInfoMapper skuInfoMapper;
    @Autowired
    private SkuAttrValueMapper skuAttrValueMapper;
    @Autowired
    private SkuSaleAttrValueMapper skuSaleAttrValueMapper;
    @Autowired
    private RedisUtil redisUtil;





    @Override
    public List<BaseCatalog1> getCatalog1() {
        return  baseCatalog1Mapper.selectAll();
    }

    @Override
    public List<BaseCatalog2> getCatalog2(String catalog1Id) {
        BaseCatalog2 baseCatalog2 = new BaseCatalog2();
        baseCatalog2.setCatalog1Id(catalog1Id);


        return baseCatalog2Mapper.select(baseCatalog2);
    }

    @Override
    public List<BaseCatalog3> getCatalog3(String catalog2Id) {
        BaseCatalog3 baseCatalog3 = new BaseCatalog3();
        baseCatalog3.setCatalog2Id(catalog2Id);

        return baseCatalog3Mapper.select(baseCatalog3);
    }

    @Override
    public List<BaseAttrInfo> getAttrList(String catalog3Id) {

        return baseAttrInfoMapper.selectBaseAttrList(catalog3Id);
    }
    @Transactional
    @Override
    public void addBaseAttrInfo(BaseAttrInfo baseAttrInfo) {

        if(baseAttrInfo.getId()!=null && baseAttrInfo.getId().length()>0){
            baseAttrInfoMapper.updateByPrimaryKeySelective(baseAttrInfo);
        }else {
            //添加属性名称
            baseAttrInfoMapper.insertSelective(baseAttrInfo);
        }

       //添加属性值
        //查询baseAtrrInfo 是否有id值，如果有则是修改属性值，如果没有就是添加属性值
        /**
         * 添加属性值是要删除以前的属性值，再重新添加
         */
        //int i =1/0;
        BaseAttrValue baseAttrValueDel = new BaseAttrValue();
        baseAttrValueDel.setAttrId(baseAttrInfo.getId());
        baseAttrValueMapper.delete(baseAttrValueDel);


        List<BaseAttrValue> attrValueList = baseAttrInfo.getAttrValueList();
        //判读属性值是否为空
        if(attrValueList !=null && attrValueList.size()>0) {
            for (BaseAttrValue baseAttrValue : attrValueList) {
                //循环遍历添加属性值，需要设置attr_id 否则存储不完整，后期查询不到
                baseAttrValue.setAttrId(baseAttrInfo.getId());
                baseAttrValueMapper.insertSelective(baseAttrValue);
            }
        }

    }

    @Override
    public List<BaseAttrValue> getBaseAttrValue(String attrId) {

        BaseAttrValue baseAttrValue = new BaseAttrValue();
        baseAttrValue.setAttrId(attrId);

        return baseAttrValueMapper.select(baseAttrValue);
    }

    @Override
    public BaseAttrInfo getBaseAttrInfo(String attrId) {

        BaseAttrInfo baseAttrInfo = baseAttrInfoMapper.selectByPrimaryKey(attrId);
        baseAttrInfo.setAttrValueList(getBaseAttrValue(attrId));

        return baseAttrInfo;
    }

    @Override
    public List<SpuInfo> getSpuInfoList(SpuInfo spuInfo) {
        return spuInfoMapper.selectAll();
    }

    @Override
    public List<BaseSaleAttr> baseSaleAttrList() {
        return baseSaleAttrMapper.selectAll();
    }

    @Override
    public void saveSpuInfo(SpuInfo spuInfo) {
        //保存到四张表
    //保存到spuInfo
       spuInfoMapper.insertSelective(spuInfo);
       //spuImage

        List<SpuImage> spuImageList = spuInfo.getSpuImageList();
        if(spuImageList!=null && spuImageList.size()>0){
            for (SpuImage spuImage : spuImageList) {
                spuImage.setSpuId(spuInfo.getId());
                spuImageMapper.insertSelective(spuImage);
            }
        }

        //spuSaleAttr
        List<SpuSaleAttr> spuSaleAttrList = spuInfo.getSpuSaleAttrList();
        if(spuSaleAttrList!=null && spuSaleAttrList.size()>0){
            for (SpuSaleAttr spuSaleAttr : spuSaleAttrList) {
                spuSaleAttr.setSpuId(spuInfo.getId());
                spuSaleAttrMapper.insertSelective(spuSaleAttr);

                List<SpuSaleAttrValue> spuSaleAttrValueList = spuSaleAttr.getSpuSaleAttrValueList();
                for (SpuSaleAttrValue spuSaleAttrValue : spuSaleAttrValueList) {
                    spuSaleAttrValue.setSpuId(spuInfo.getId());
                    spuSaleAttrValueMapper.insertSelective(spuSaleAttrValue);

                }
            }
        }

    }

    @Override
    public List<SpuImage> getSpuImageList(String spuId) {
        SpuImage spuImage = new SpuImage();
        spuImage.setSpuId(spuId);
        return spuImageMapper.select(spuImage);
    }

    @Override
    public List<SpuSaleAttr> getSpuSaleAttrList(String spuId) {
        return spuSaleAttrMapper.selectSpuSaleAttrList(spuId);
    }

    @Override
    public void saveSkuInfo(SkuInfo skuInfo) {

        skuInfoMapper.insertSelective(skuInfo);

//        skuImage
        List<SkuImage> skuImageList = skuInfo.getSkuImageList();

        // 集合的长度怎么求？ .size(); 字符串的长度？.length();  数组的长度？ .length; 文件的长度咋求？ .length();
        // =赋值， ==判断值 ===判断值，以及数据类型

        if (skuImageList!=null && skuImageList.size()>0){
            // 先遍历skuImageList
            for (SkuImage skuImage : skuImageList) {
                //skuId = skuInfo.getId();
                skuImage.setSkuId(skuInfo.getId());
                skuImageMapper.insertSelective(skuImage);
            }
        }
//        skuAttrValue
        List<SkuAttrValue> skuAttrValueList = skuInfo.getSkuAttrValueList();
        if (skuAttrValueList!=null && skuAttrValueList.size()>0){
            for (SkuAttrValue skuAttrValue : skuAttrValueList) {
                skuAttrValue.setSkuId(skuInfo.getId());
                skuAttrValueMapper.insertSelective(skuAttrValue);
            }
        }
//        skuSaleAttrValue
        if (skuInfo.getSkuSaleAttrValueList()!=null && skuInfo.getSkuSaleAttrValueList().size()>0){

            for (SkuSaleAttrValue skuSaleAttrValue : skuInfo.getSkuSaleAttrValueList()) {
                skuSaleAttrValue.setSkuId(skuInfo.getId());
                skuSaleAttrValueMapper.insertSelective(skuSaleAttrValue);
            }

        }


    }

    @Override
    public SpuInfo getSpuInfo(String spuId) {
        return spuInfoMapper.selectByPrimaryKey(spuId);
    }

    @Override
    public SkuInfo getSkuInfo(String skuId) {
//        return getSkuInfoRedisson(skuId);
        return getSkuInfoRedis(skuId);

    }

    private SkuInfo getSkuInfoRedisson(String skuId) {
        RLock lock =null;
        SkuInfo skuInfo=null;
        Jedis jedis=null;
        try{
            //获取连接
            jedis = redisUtil.getJedis();
            //定义key
            String skuInfoKey = ManageConst.SKUKEY_PREFIX+skuId+ManageConst.SKUKEY_SUFFIX;
            if(jedis.exists(skuInfoKey)){
                String skuInfoJson = jedis.get(skuInfoKey);
                if(skuInfoJson!=null && skuInfoJson.length()!=0){
                    skuInfo = JSON.parseObject(skuInfoJson,SkuInfo.class);
                    return skuInfo;
                }

            }else{
                Config config = new Config();
                config.useSingleServer().setAddress("redis://10.211.55.11:6379");
                RedissonClient redisson = Redisson.create(config);
                lock = redisson.getLock("my-lock");
                lock.lock(10, TimeUnit.SECONDS);

                //从数据库查询数据
                skuInfo = getSkuInfoDB(skuId);
                //String jsonString = JSON.toJSONString(skuInfo);
                jedis.setex(skuInfoKey,ManageConst.SKUKEY_TIMEOUT,JSON.toJSONString(skuInfo));
                return skuInfo;
            }

            
        }catch (Exception e){
            e.printStackTrace();
            
        }finally {
            if(jedis!=null){
                jedis.close();
            }
            if(lock!=null){
                lock.unlock();
            }

        }
        
        
       


        return getSkuInfoDB(skuId);

    }
    private SkuInfo getSkuInfoRedis(String skuId) {
        SkuInfo skuInfo = null;
        Jedis jedis = null;
        try {
            // 获取Jedis
            jedis = redisUtil.getJedis();

            // 定义key sku:skuId:info
            String skuKey = ManageConst.SKUKEY_PREFIX+skuId+ManageConst.SKUKEY_SUFFIX;
            // 获取缓存中的数据
            String skuJson = jedis.get(skuKey);
            // 当缓存中没用数据的时候加锁
            if (skuJson==null){
                System.out.println("没用缓存准备上锁");
                // set k1 OK PX 10000 NX
                // 定义锁的Key sku:skuId:lock
                String skuLockKey = ManageConst.SKUKEY_PREFIX+skuId+ManageConst.SKULOCK_SUFFIX; // k1
                String token = UUID.randomUUID().toString().replace("-",""); // OK

                // 调用set 方法 执行正常则lockKey = OK
                String lockKey = jedis.set(skuLockKey, token, "nx", "ex", ManageConst.SKULOCK_EXPIRE_PX);

                if ("OK".equals(lockKey)){
                    System.out.println("获取到分布式锁！");
                    // 获取数据库中的数据，放入缓存！
                    skuInfo = getSkuInfoDB(skuId);

                    jedis.setex(skuKey,ManageConst.SKUKEY_TIMEOUT, JSON.toJSONString(skuInfo));

                    // 删除锁！
                    // jedis.del(skuLockKey);
                    // 保证删除锁的唯一性！
                    String script ="if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
                    jedis.eval(script, Collections.singletonList(skuLockKey),Collections.singletonList(token));
                    return skuInfo;

                } else {
                    // 说其他线程进来了。需要等待一会
                    Thread.sleep(1000);
                    // 自旋
                    return getSkuInfo(skuId);
                }
            }else {
                // 说明缓存中已经有数据了
                skuInfo = JSON.parseObject(skuJson, SkuInfo.class);
                return skuInfo;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 如何解决空指针？
            if (jedis!=null){
                jedis.close();
            }
        }
        return  getSkuInfoDB(skuId);
    }
    private SkuInfo getSkuInfoDB(String skuId) {

        SkuInfo skuInfo = skuInfoMapper.selectByPrimaryKey(skuId);
        skuInfo.setSkuImageList(getSkuImageList(skuId));
        skuInfo.setSkuAttrValueList(getSkuAttrValueList(skuId));
        return skuInfo;

        /**
         * 获取销售属性和销售属性值
         */

    }

    private List<SkuAttrValue> getSkuAttrValueList(String skuId) {
        SkuAttrValue skuAttrValue = new SkuAttrValue();
        skuAttrValue.setSkuId(skuId);

        return skuAttrValueMapper.select(skuAttrValue);
    }

    @Override
    public List<SkuImage> getSkuImageList(String skuId) {
        SkuImage skuImage = new SkuImage();
        skuImage.setSkuId(skuId);
        return skuImageMapper.select(skuImage);
    }
    /**
     * 获取当前sku中选中的销售属性
     * 并返回所有销售属性
     * @param skuInfo
     * @return
     */

    @Override
    public List<SpuSaleAttr> getSpuSaleAttrListCheckBySku(SkuInfo skuInfo) {
        return spuSaleAttrMapper.selectSpuSaleAttrListCheckBySku(skuInfo.getId(),skuInfo.getSpuId());
    }

    @Override
    public List<SkuSaleAttrValue> getSkuSaleAttrValueListBySpu(String spuId) {
        return skuSaleAttrValueMapper.selectSkuSaleAttrValueListBySpu(spuId);
    }

    @Override
    public List<BaseAttrInfo> getAttrList(List<String> attrValueIdList) {
        String valueIds = StringUtils.join(attrValueIdList.toArray(),",");
        return baseAttrInfoMapper.selectAttrInfoListByIds(valueIds);
    }


}
