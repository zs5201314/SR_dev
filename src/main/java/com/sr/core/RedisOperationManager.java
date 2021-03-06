package com.sr.core;

import org.crazycake.shiro.RedisManager;
import org.crazycake.shiro.SerializeUtils;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.Set;

/**
 * Created by KingZS on 2018/5/28
 */
public class RedisOperationManager {
    private static RedisManager redisManager;
    private static RedisOperationManager redisOperationManager;
    private static JedisPool jedisPool = null;

    public static RedisOperationManager init(){
        if (redisOperationManager == null){
            redisOperationManager = new RedisOperationManager();
            redisManager = new RedisManager();
            redisManager.init();
        }
        return redisOperationManager;
    }

    public static void initjedis() {
        if(jedisPool == null) {
            if (Constants.REDIS_PASSWORD.length() > 0){
                jedisPool = new JedisPool(new JedisPoolConfig(), Constants.REDIS_HOST, Constants.REDIS_PORT, Constants.REDIS_TIMEOUR, Constants.REDIS_PASSWORD);
            }else{
                jedisPool = new JedisPool(new JedisPoolConfig(), Constants.REDIS_HOST, Constants.REDIS_PORT, Constants.REDIS_TIMEOUR);
            }
        }
    }

    public void setDate(String key, Object value) {
        redisManager.set(key.getBytes(),SerializeUtils.serialize(value));
    }

    public void setDate(String key, Object value,int expire) {
        redisManager.set(key.getBytes(),SerializeUtils.serialize(value),expire);
    }

    public String getString(String key){
        byte[] bytes = redisManager.get(key.getBytes());
        String content = (String)SerializeUtils.deserialize(bytes);
        return content;
    }

    public int getInt(String key){
        byte[] bytes = redisManager.get(key.getBytes());
        int content = bytes == null ? 0: (Integer) SerializeUtils.deserialize(bytes);
        return content;
    }

    public Object getObject(String key){
        byte[] bytes = redisManager.get(key.getBytes());
        Object content = (Object)SerializeUtils.deserialize(bytes);
        return content;
    }

    public void delDate(Object key) {
        redisManager.del(SerializeUtils.serialize(key));
    }

//    //直接操作
//    public void setZadd(String key,double soure,String uid){
//        Jedis jedis = (Jedis)jedisPool.getResource();
//        try {
//            jedis.zadd(key,soure,uid);
//        } finally {
//            jedisPool.returnResource(jedis);
//        }
//    }
//
//    public void flushDB() {
//        Jedis jedis = (Jedis)jedisPool.getResource();
//        try {
//            jedis.flushDB();
//        } finally {
//            jedisPool.returnResource(jedis);
//        }
//    }

    /**
     * 批量删除缓存
     * @param key
     */
    public void delDataLike(String key) {
        Set<byte[]> set =  redisManager.keys(key+"*");
        for(byte[] b:set){
            redisManager.del(b);
        }
    }

}
