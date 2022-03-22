package com.c.community.service;

import com.c.community.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisStringCommands;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.security.PublicKey;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

@Service
public class DataService {

    @Autowired
    RedisTemplate redisTemplate;

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

    // 将指定ip计入uv
    public void recordUV(String ip) {
        String redisKey = RedisKeyUtil.getUVKey(sdf.format(new Date()));
        redisTemplate.opsForHyperLogLog().add(redisKey, ip);
    }

    // 统计置顶日期的uv
    public long calUV(Date start, Date end) {
        if (start == null || end == null) throw new IllegalArgumentException("参数不能为空");
        // 日期范围内的key
        List<String> keyList = new LinkedList<>();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(start);
        while (!calendar.getTime().after(end)) {
            String uvKey = RedisKeyUtil.getUVKey(sdf.format(calendar.getTime()));
            keyList.add(uvKey);
            calendar.add(Calendar.DATE, 1);
        }
        String redisKey = RedisKeyUtil.getUVkey(sdf.format(start), sdf.format(end));
        redisTemplate.opsForHyperLogLog().union(redisKey, keyList.toArray());

        return redisTemplate.opsForHyperLogLog().size(redisKey);
    }

    // 将用户计入dau
    public void recordDAU(int userId) {
        String redisKey = RedisKeyUtil.getDAUKey(sdf.format(new Date()));
        redisTemplate.opsForValue().setBit(redisKey, userId, true);
    }

    public long calDau(Date start, Date end) {
        if (start == null || end == null) throw new IllegalArgumentException("参数不能为空");
        // 日期范围内的key
        List<byte[]> bytes = new LinkedList<>();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(start);
        while (!calendar.getTime().after(end)) {
            String dauKey = RedisKeyUtil.getDAUKey(sdf.format(calendar.getTime()));
            bytes.add(dauKey.getBytes());
            calendar.add(Calendar.DATE, 1);
        }
        // 进行or运算
        long res = (long) redisTemplate.execute(new RedisCallback() {
            @Override
            public Object doInRedis(RedisConnection connection) throws DataAccessException {
                String redisKey = RedisKeyUtil.getDAUkey(sdf.format(start), sdf.format(end));
                connection.bitOp(RedisStringCommands.BitOperation.OR,
                        redisKey.getBytes(),
                        bytes.toArray(new byte[bytes.size()][]));
                return connection.bitCount(redisKey.getBytes());
            }
        });
        return res;
    }
}
