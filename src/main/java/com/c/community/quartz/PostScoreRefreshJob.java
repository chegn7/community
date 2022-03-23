package com.c.community.quartz;

import com.c.community.entity.DiscussPost;
import com.c.community.service.DiscussPostService;
import com.c.community.service.ElasticSearchService;
import com.c.community.service.LikeService;
import com.c.community.util.CommunityConstant;
import com.c.community.util.RedisKeyUtil;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundSetOperations;
import org.springframework.data.redis.core.RedisTemplate;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PostScoreRefreshJob implements Job {
    private static final Logger LOGGER = LoggerFactory.getLogger(PostScoreRefreshJob.class);
    @Autowired
    RedisTemplate redisTemplate;
    @Autowired
    DiscussPostService discussPostService;
    @Autowired
    LikeService likeService;
    @Autowired
    ElasticSearchService elasticSearchService;

    private static final Date epoch;

    static {
        try {
            epoch = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2014-08-01 00:00:00");
        } catch (ParseException e) {
            throw new RuntimeException("初始化初始时间失败", e);
        }
    }

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        String redisKey = RedisKeyUtil.getPostScoreKey();
        BoundSetOperations boundSetOps = redisTemplate.boundSetOps(redisKey);
        if (boundSetOps == null || boundSetOps.size() == 0) {
            LOGGER.info("[任务取消] 无需要刷新分数的帖子");
            return;
        }

        LOGGER.info("[任务开始] 刷新帖子分数，共" + boundSetOps.size() + "条");
        while (boundSetOps.size() > 0) {
            this.refresh((Integer) boundSetOps.pop());
        }
        LOGGER.info("[任务结束] 刷新完成");
    }

    private void refresh(int postId) {
        DiscussPost post = discussPostService.findPost(postId);
        if (post == null) {
            LOGGER.error("[删除失败] id = " + postId + "帖子未找到");
            return;
        }
        boolean isWonderful = post.getStatus() == 1;
        int commentCount = post.getCommentCount();
        long likeCount = likeService.likeCount(CommunityConstant.ENTITY_POST, postId);

        double weight = (isWonderful ? 75 : 0) + commentCount * 10 + likeCount * 2;
//        double score = Math.log10(Math.max(weight, 1));// 保证score非负
        double score = weight < 1 ? 0 : Math.log(weight);
        score += (post.getCreateTime().getTime() - epoch.getTime()) / (24 * 3600 * 1000);
        discussPostService.updateScore(postId, score);
        post.setScore(score);
        elasticSearchService.saveDiscussPost(post);

    }
}
