package com.c.community.actuator;

import com.c.community.util.CommunityUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

@Component
@Endpoint(id = "database")
public class DatabaseEndpoint {
    private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseEndpoint.class);

    // 通过连接池访问数据库连接
    @Autowired
    DataSource dataSource;

    @ReadOperation
    public String checkConnection() {
        try (
                Connection connection = dataSource.getConnection()
        ) {
            return CommunityUtil.getJSONString(0, "获取连接成功");
        } catch (SQLException e) {
            LOGGER.error("获取连接失败" + e.getMessage());
            return CommunityUtil.getJSONString(1, "获取连接失败");
        }
    }
}
