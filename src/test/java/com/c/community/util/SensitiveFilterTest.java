package com.c.community.util;

import com.c.community.CommunityApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
class SensitiveFilterTest {

    @Autowired
    SensitiveFilter sensitiveFilter;

    @Test
    void filter() {
        String text = "2开*票附件嚄i";
        String filter = sensitiveFilter.filter(text);
        System.out.println(filter.length());
        System.out.println(filter);
    }
}