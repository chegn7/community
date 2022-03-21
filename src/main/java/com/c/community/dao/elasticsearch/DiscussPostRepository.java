package com.c.community.dao.elasticsearch;

import com.c.community.entity.DiscussPost;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DiscussPostRepository extends ElasticsearchRepository<DiscussPost, Integer> {
}
