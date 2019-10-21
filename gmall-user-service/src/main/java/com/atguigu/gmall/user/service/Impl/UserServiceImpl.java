package com.atguigu.gmall.user.service.Impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.bean.UserAddress;
import com.atguigu.gmall.bean.UserInfo;
import com.atguigu.gmall.config.RedisUtil;
import com.atguigu.gmall.user.mapper.UserAddressMapper;
import com.atguigu.gmall.user.mapper.UserInfoMapper;

import com.atguigu.gmall.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;
import redis.clients.jedis.Jedis;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserInfoMapper userInfoMapper;
    @Autowired
    private UserAddressMapper userAddressMapper;

    @Autowired
    private RedisUtil redisUtil;
    //存到redis中的key和过期时间
    public String userKey_prefix="user:";
    public String userinfoKey_suffix=":info";
    public int userKey_timeOut=60*60*24;

    @Override
    public List<UserInfo> findAll() {
        return userInfoMapper.selectAll();
    }

    @Override
    public List<UserInfo> findByUserInfo(UserInfo userInfo) {
        return null;
    }

    @Override
    public List<UserInfo> findByLoginName(String loginName) {
        return null;
    }

    @Override
    public void addUser(UserInfo userInfo) {

    }

    @Override
    public void updUser(UserInfo userInfo) {

    }

    @Override
    public void delUser(UserInfo userInfo) {

    }

    @Override
    public List<UserAddress> findUserAddressByUserId(String userId) {
        UserAddress userAddress = new UserAddress();
        userAddress.setUserId(userId);

        return userAddressMapper.select(userAddress);
    }
    @Override
    public UserInfo login(UserInfo userInfo){
        //使用md5加密
        String password = DigestUtils.md5DigestAsHex(userInfo.getPasswd().getBytes());
        userInfo.setPasswd(password);
        //从数据中获取到用户信息
        UserInfo info = userInfoMapper.selectOne(userInfo);
        //匹配正确 把数据存到redis中
        /**
         * 1、使用redis之前要更改配置文件
         * 2、在启动类上扫描包扫描到RedisUtil
         * 3、关闭redis连接
         */
        Jedis jedis =null;
        if(info!=null){
            try {
                jedis = redisUtil.getJedis();
                //设置key的值 sku：skuId：info|user：userId：info
                String userKey =  userKey_prefix+info.getId()+userinfoKey_suffix;
                //从redis中获取该key值，查看是否存在
//                String userJson = jedis.get(userKey);
//                /**
//                 * redis中的数据有可能不为空，过期时间是七天，所以有可能几天前用户登录过，已经存到了redis中
//                 * 直接从redis中取得用户信息
//                 * 把String类型转换成UserInfo类型
//                     */
//                if(!StringUtils.isEmpty(userJson)){
//                    UserInfo userInfo1 = JSON.parseObject(userJson, UserInfo.class);
//
//                    return userInfo1;
//
//                }
                //把用户信息转成字符串再存入redis中
                jedis.setex(userKey,this.userKey_timeOut, JSON.toJSONString(info));
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if(jedis!=null){
                    jedis.close();
                }
            }
            return info;
        }
        return null;
    }

    @Override
    public UserInfo verify(String userId) {
        Jedis jedis =null;
        try {
            //获取jedis连接

            jedis = redisUtil.getJedis();

            String userKey =  userKey_prefix+userId+userinfoKey_suffix;
          //  从redis中获取use 信息
            String userInfoJSON = jedis.get(userKey);
            if(!StringUtils.isEmpty(userInfoJSON)){
                //给redis中的用户信息延长过期时间
                jedis.expire(userKey,userKey_timeOut);
                //把从redis 中获取的字符串换成对象，返回回去
                UserInfo userInfo = JSON.parseObject(userInfoJSON, UserInfo.class);

                return userInfo;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(jedis!=null){
                jedis.close();
            }
        }


        return null;
    }
}
