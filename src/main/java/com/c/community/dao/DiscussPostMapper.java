package com.c.community.dao;

import com.c.community.entity.DiscussPost;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface DiscussPostMapper {
    /**
     *根据输入的userId查询帖子，之所以传入userId是考虑到后期开发我的空间功能里
     *查询用户自己发的帖子需要传入用户的id
     * @param userId 用户id
     * @param offset 起始行行号，注意，第一行offset为0
     * @param limit 每页显示的帖子数量
     * @return 帖子列表
     */
    // List是Java自带的类，不用声明也可以检测到。
    List<DiscussPost> selectDiscussPosts(int userId, int offset, int limit);
    //@Param 注解用于给参数取别名
    //如果只有一个参数，并且在<if>里使用，就必须加别名

    /**
     * 查询userId用户发的帖子总数
     * @param userId
     * @return
     */
    int selectDiscussPostRows(@Param("userId") int userId);

    int insertDiscussPost(DiscussPost discussPost);

    DiscussPost selectByPostId(int id);

    int updateCommentCount(int id, int commentCount);

}
