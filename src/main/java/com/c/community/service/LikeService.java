package com.c.community.service;

import com.c.community.entity.User;
import com.c.community.util.CommunityConstant;
import com.c.community.util.CommunityUtil;
import com.c.community.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class LikeService {
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private DiscussPostService discussPostService;
    @Autowired
    private CommentService commentService;
    @Autowired
    private UserService userService;

    // 点赞操作
    public void like(int userId, int entityType, int entityId, int entityCreateUserId) {
        redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                String entityLikeKey = RedisKeyUtil.getEntityLikeKey(entityType, entityId);
                // 注意这里我们要找的是被点赞的那个人的userId，也就是实体创建者的id
                String userLikeKey = RedisKeyUtil.getUserLikeKey(entityCreateUserId);
                Boolean isMember = operations.opsForSet().isMember(entityLikeKey, userId);
                // 注意要先查了再开启事务
                operations.multi();
                if (isMember) {
                    operations.opsForSet().remove(entityLikeKey, userId);
                    operations.opsForValue().decrement(userLikeKey);
                } else {
                    operations.opsForSet().add(entityLikeKey, userId);
                    operations.opsForValue().increment(userLikeKey);
                }
                return operations.exec();
            }
        });
    }

    // 统计点赞数
    public long likeCount(int entityType, int entityId) {
        String entityLikeKey = RedisKeyUtil.getEntityLikeKey(entityType, entityId);
        return redisTemplate.opsForSet().size(entityLikeKey);
    }

    // 获得点赞与否的状态
    public int findLikeStatus(int userId, int entityType, int entityId) {
        String entityLikeKey = RedisKeyUtil.getEntityLikeKey(entityType, entityId);
        if (redisTemplate.opsForSet().isMember(entityLikeKey, userId)) return CommunityConstant.LIKE_STATUS;
        else return CommunityConstant.DEFAULT_LIKE_STATUS;
    }

    // 查询某用户的获赞数
    public int findUserLikeCount(int userId) {
        String userLikeKey = RedisKeyUtil.getUserLikeKey(userId);
        Integer count = (Integer) redisTemplate.opsForValue().get(userLikeKey);
        return count == null ? 0 : count;
    }
}
