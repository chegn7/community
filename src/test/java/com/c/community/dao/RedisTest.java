package com.c.community.dao;

import com.c.community.CommunityApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.*;
import org.springframework.test.context.ContextConfiguration;

import java.util.concurrent.TimeUnit;

@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class RedisTest {
    @Autowired
    private RedisTemplate redisTemplate;

    @Test
    public void testStrings() {
        String redisKey = "test:count";
        redisTemplate.opsForValue().set(redisKey, 1);

        System.out.println(redisTemplate.opsForValue().get(redisKey));

        redisTemplate.opsForValue().increment(redisKey);
        System.out.println(redisTemplate.opsForValue().get(redisKey));
        redisTemplate.opsForValue().decrement(redisKey);
        System.out.println(redisTemplate.opsForValue().get(redisKey));

    }

    @Test
    public void testHash() {
        String redisKey = "test:user";
        redisTemplate.opsForHash().put(redisKey, "id", 101);
        redisTemplate.opsForHash().put(redisKey, "username", "zhangsan");
        redisTemplate.opsForHash().put(redisKey, "age", 20);

        System.out.println(redisTemplate.opsForHash().get(redisKey, "age"));

        redisTemplate.opsForHash().put(redisKey, "age", 16);

        System.out.println(redisTemplate.opsForHash().get(redisKey, "age"));
    }

    @Test
    public void testList() {
        String redisKey = "test:ids";

        redisTemplate.opsForList().leftPush(redisKey, 101);
        redisTemplate.opsForList().leftPush(redisKey, 102);
        redisTemplate.opsForList().leftPush(redisKey, 103);
        System.out.println(redisTemplate.opsForList().size(redisKey));
        System.out.println(redisTemplate.opsForList().index(redisKey, 0));
        System.out.println(redisTemplate.opsForList().range(redisKey, 0, 1));
        // 队列
        System.out.println(redisTemplate.opsForList().rightPop(redisKey));
        System.out.println(redisTemplate.opsForList().rightPop(redisKey));
        System.out.println(redisTemplate.opsForList().rightPop(redisKey));
        System.out.println(redisTemplate.opsForList().rightPop(redisKey));
        // 栈
//        System.out.println(redisTemplate.opsForList().leftPop(redisKey));
//        System.out.println(redisTemplate.opsForList().leftPop(redisKey));
//        System.out.println(redisTemplate.opsForList().leftPop(redisKey));
    }

    @Test
    public void testSet() {
        String redisKey = "test:teachers";

        redisTemplate.opsForSet().add(redisKey, "zhangsan1", "zhangsan2", "zhangsan3", "zhangsan3", "zhangsan4");
        System.out.println(redisTemplate.opsForSet().size(redisKey));
        System.out.println(redisTemplate.opsForSet().pop(redisKey, 2));
        System.out.println(redisTemplate.opsForSet().members(redisKey));
    }

    @Test
    public void testZSet() {
        String redisKey = "test:students";

        redisTemplate.opsForZSet().add(redisKey, "zhangsan1", 10);
        redisTemplate.opsForZSet().add(redisKey, "zhangsan2", 20);
        redisTemplate.opsForZSet().add(redisKey, "zhangsan3", 40);
        redisTemplate.opsForZSet().add(redisKey, "zhangsan4", 30);
        redisTemplate.opsForZSet().add(redisKey, "zhangsan5", 22);
        redisTemplate.opsForZSet().add(redisKey, "zhangsan6", 30);
        System.out.println(redisTemplate.opsForZSet().size(redisKey));
        System.out.println(redisTemplate.opsForZSet().zCard(redisKey));
        System.out.println(redisTemplate.opsForZSet().score(redisKey, "zhangsan2"));
        System.out.println(redisTemplate.opsForZSet().rank(redisKey, "zhangsan2"));// 默认从小到大排
        System.out.println(redisTemplate.opsForZSet().reverseRank(redisKey, "zhangsan2"));// 从大到小是reverse排

        System.out.println(redisTemplate.opsForZSet().range(redisKey, 0, 2)); // 取分最低的三个人
        System.out.println(redisTemplate.opsForZSet().reverseRange(redisKey, 0, 2)); // 取分最高的三个人
    }

    @Test
    public void testKeys() {
        redisTemplate.delete("test:user");

        System.out.println(redisTemplate.hasKey("test:user"));

        redisTemplate.expire("test:students", 5, TimeUnit.SECONDS);
    }

    // 绑定key
    @Test
    public void bindKey() {
        String redisKey = "test:count";
        BoundValueOperations operations = redisTemplate.boundValueOps(redisKey);
        System.out.println(operations.increment());
        System.out.println(operations.increment());
        System.out.println(operations.increment());
        System.out.println(operations.increment());
        System.out.println(operations.increment());
        System.out.println(operations.get());
    }

    /**
     * 事务，nosql数据库，不能满足acid
     * 事务里执行查询操作不会立即执行，会事务结束之后将多条语句一起执行得到返回结果，因此需要提前查或者事务结束之后查操作
     * Redis通常用编程式事务
     */
    @Test
    public void testTransactional() {
        Object object = redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                String redisKey = "test:tx";

                // 启用事务
                operations.multi();

                BoundSetOperations setOperations = operations.boundSetOps(redisKey);
                setOperations.add(1,2,3,4);
                System.out.println(setOperations.members());

                return operations.exec();
            }
        });
        System.out.println(object);
    }


}
