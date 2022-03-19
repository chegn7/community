package com.c.community.service;

import com.c.community.entity.User;
import com.c.community.util.CommunityConstant;
import com.c.community.util.CommunityUtil;
import com.c.community.util.Follow;
import com.c.community.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class FollowService {
    @Autowired
    RedisTemplate redisTemplate;

    @Autowired
    UserService userService;

    public void follow(int userId, int entityType, int entityId) {
        redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                String followerKey = RedisKeyUtil.getFollowerKey(entityType, entityId);
                String followeeKey = RedisKeyUtil.getFolloweeKey(userId, entityType);

                operations.multi();
                // 当这个userId对应的用户执行follow操作后，这个用户的followeeKey增加一个entity， entity的follower增加一个userId
                operations.opsForZSet().add(followeeKey, entityId, System.currentTimeMillis());
                operations.opsForZSet().add(followerKey, userId, System.currentTimeMillis());
                return operations.exec();
            }
        });
    }

    public void unFollow(int userId, int entityType, int entityId) {
        redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                String followerKey = RedisKeyUtil.getFollowerKey(entityType, entityId);
                String followeeKey = RedisKeyUtil.getFolloweeKey(userId, entityType);

                operations.multi();
                // 当这个userId对应的用户执行unFollow操作后，这个用户的followeeKey删除一个entity， entity的follower删除一个userId
                operations.opsForZSet().remove(followeeKey, entityId);
                operations.opsForZSet().remove(followerKey, userId);
                return operations.exec();
            }
        });
    }

    /**
     * 查询该用户关注某实体类型的实体数量
     *
     * @param userId     用户id
     * @param entityType 实体类型
     * @return
     */
    public long findFolloweeCount(int userId, int entityType) {
        String followeeKey = RedisKeyUtil.getFolloweeKey(userId, entityType);
        return redisTemplate.opsForZSet().zCard(followeeKey);
    }


    /**
     * 查询实体粉丝的数量
     *
     * @param entityType
     * @param entityId
     * @return
     */
    public long findFollowerCount(int entityType, int entityId) {
        String followerKey = RedisKeyUtil.getFollowerKey(entityType, entityId);
        return redisTemplate.opsForZSet().zCard(followerKey);
    }

    public int findFollowStatus(int userId, int entityType, int entityId) {
        String followerKey = RedisKeyUtil.getFollowerKey(entityType, entityId);
        Double score = redisTemplate.opsForZSet().score(followerKey, userId);
        if (score == null) {
            return CommunityConstant.UNFOLLOW_STATUS;
        } else {
            return CommunityConstant.FOLLOWING_STATUS;
        }
    }

    /**
     * 查询userId对应用户关注的人
     *
     * @param userId
     * @return 关注列表
     */
    public List<Map<String, Object>> findFolloweeList(int userId, int offset, int limit) {
        String followeeKey = RedisKeyUtil.getFolloweeKey(userId, CommunityConstant.ENTITY_USER);
        Set<Integer> followeeIds = redisTemplate.opsForZSet().reverseRange(followeeKey, offset, offset + limit - 1);// -1是因为该查询是闭区间
        if (followeeIds == null) return null;
        List<Map<String, Object>> res = new ArrayList<>();
        for (Integer id : followeeIds) {
            Map<String, Object> map = new HashMap<>();
            User followeeUser = userService.findUser(id);
            map.put("followeeUser", followeeUser);
            Double score = redisTemplate.opsForZSet().score(followeeKey, id);
            map.put("followTime", new Date(score.longValue()));
            res.add(map);
        }
        return res;
    }

    public List<Map<String, Object>> findFollowerList(int userId, int offset, int limit) {
        String followerKey = RedisKeyUtil.getFollowerKey(CommunityConstant.ENTITY_USER, userId);
        Set<Integer> followerIds = redisTemplate.opsForZSet().reverseRange(followerKey, offset, offset + limit - 1);// -1是因为该查询是闭区间
        if (followerIds == null) return null;
        List<Map<String, Object>> res = new ArrayList<>();
        for (Integer id : followerIds) {
            Map<String, Object> map = new HashMap<>();
            User followerUser = userService.findUser(id);
            map.put("followerUser", followerUser);
            Double score = redisTemplate.opsForZSet().score(followerKey, id);
            map.put("followTime", new Date(score.longValue()));
            res.add(map);
        }
        return res;
    }


}
