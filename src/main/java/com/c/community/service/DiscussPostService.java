package com.c.community.service;

import com.c.community.dao.DiscussPostMapper;
import com.c.community.entity.DiscussPost;
import com.c.community.util.CommunityUtil;
import com.c.community.util.RedisKeyUtil;
import com.c.community.util.SensitiveFilter;
import com.github.benmanes.caffeine.cache.CacheLoader;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.util.HtmlUtils;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class DiscussPostService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DiscussPostService.class);

    @Autowired
    private DiscussPostMapper discussPostMapper;

    @Autowired
    private SensitiveFilter sensitiveFilter;

    @Autowired
    private RedisTemplate redisTemplate;

    @Value("${caffeine.posts.max-size}")
    private int maxSize;

    @Value("${caffeine.posts.expire-seconds}")
    private int expreSeconds;

    // Caffeine 核心接口 Cache, LoadingCache , AsyncLoadingCache

    // 帖子列表的缓存
    private LoadingCache<String, List<DiscussPost>> postListCache;

    // 帖子总数的缓存
    private LoadingCache<Integer, Integer> postRowsCache;

    @PostConstruct
    public void init() {
        // 初始化缓存
        postListCache = Caffeine.newBuilder()
                .maximumSize(maxSize)
                .expireAfterWrite(expreSeconds, TimeUnit.SECONDS)
                .build(new CacheLoader<String, List<DiscussPost>>() {
                    @Override
                    public @Nullable List<DiscussPost> load(String s) throws Exception {
                        if (s == null || s.length() == 0) {
                            throw new IllegalArgumentException("key参数错误");
                        }
                        String[] params = s.split(":");
                        if (params == null || params.length != 2) {
                            throw new IllegalArgumentException("key参数错误");
                        }
                        int offset = Integer.parseInt(params[0]);
                        int limit = Integer.parseInt(params[1]);
                        // 二级缓存 访问redis
                        String redisKey = RedisKeyUtil.getPostListCacheKey(offset, limit);
                        List<DiscussPost> posts = (List<DiscussPost>) redisTemplate.opsForValue().get(redisKey);
                        if (posts == null) {
                            LOGGER.debug("can not found in Redis, select posts from DB");
                            posts = discussPostMapper.selectDiscussPosts(0, offset, limit, 1);
                            redisTemplate.opsForValue().set(redisKey, posts, expreSeconds, TimeUnit.SECONDS);
                        }
                        // 无二级缓存
//                        List<DiscussPost> posts = discussPostMapper.selectDiscussPosts(0, offset, limit, 1);
                        return posts;
                    }
                });
        postRowsCache = Caffeine.newBuilder()
                .maximumSize(maxSize)
                .expireAfterWrite(expreSeconds, TimeUnit.SECONDS)
                .build(new CacheLoader<Integer, Integer>() {
                    @Override
                    public @Nullable Integer load(Integer integer) throws Exception {
                        if (integer == null) {
                            throw new IllegalArgumentException("key参数错误");
                        }
                        // 访问 查询redis
                        String redisKey = RedisKeyUtil.getPostRowsCacheKey(integer);
                        Integer rows = (Integer) redisTemplate.opsForValue().get(redisKey);
                        if (rows == null) {
                            LOGGER.debug("can not found in Redis, select posts from DB");
                            rows = discussPostMapper.selectDiscussPostRows(integer);
                            redisTemplate.opsForValue().set(redisKey, rows, expreSeconds, TimeUnit.SECONDS);
                        }
                        // 无二级缓存
//                        int rows = discussPostMapper.selectDiscussPostRows(integer);
                        return rows;
                    }
                });
    }

    /**
     * 根据输入的userId，offset，limit查询已发的非黑名单内的帖子
     * userId = 0 表示查看所有用户的发帖
     *
     * @param userId
     * @param offset
     * @param limit
     * @return
     */
    public List<DiscussPost> findDiscussPosts(int userId, int offset, int limit, int orderMode) {
        if (userId == 0 && orderMode == 1) {
            return postListCache.get(offset + ":" + limit);
        }
        LOGGER.debug("can not found in Caffeine, select posts from DB");
        return discussPostMapper.selectDiscussPosts(userId, offset, limit, orderMode);
    }

    public int findDiscussPostRows(int userId) {
        if (userId == 0) {
            return postRowsCache.get(userId);
        }
        LOGGER.debug("can not found in Caffeine, select rows from DB");
        return discussPostMapper.selectDiscussPostRows(userId);
    }

    public int addDiscussPost(DiscussPost discussPost) {
        if (discussPost == null) throw new IllegalArgumentException("帖子不能为空！");
        // 防止script注入
        // 转义html标记
        discussPost.setTitle(HtmlUtils.htmlEscape(discussPost.getTitle()));
        discussPost.setContent(HtmlUtils.htmlEscape(discussPost.getContent()));
        // 过滤敏感词
        discussPost.setTitle(sensitiveFilter.filter(discussPost.getTitle()));
        discussPost.setContent(sensitiveFilter.filter(discussPost.getContent()));

        return discussPostMapper.insertDiscussPost(discussPost);
    }

    public DiscussPost findPost(int id) {
        return discussPostMapper.selectByPostId(id);
    }

    public int updateCommentCount(int id, int commentCount) {
        return discussPostMapper.updateCommentCount(id, commentCount);
    }

    public int updateType(int id, int type) {
        return discussPostMapper.updateType(id, type);
    }

    public int updateStatus(int id, int status) {
        return discussPostMapper.updateStatus(id, status);
    }

    public int updateScore(int id, double score) {
        return discussPostMapper.updateScore(id, score);
    }

}
