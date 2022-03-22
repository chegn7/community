package com.c.community.dao;

import com.c.community.CommunityApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisStringCommands;
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
                setOperations.add(1, 2, 3, 4);
                System.out.println(setOperations.members());

                return operations.exec();
            }
        });
        System.out.println(object);
    }

    @Test
    public void testHyperLogLog() {
        String redisKey = "tst:hll:01";
        for (int i = 1; i <= 100000; i++) {
            redisTemplate.opsForHyperLogLog().add(redisKey, i);
        }
        for (int i = 0; i < 100000; i++) {
            int r = (int) (Math.random() * 100000 + 1);
            redisTemplate.opsForHyperLogLog().add(redisKey, r);
        }

        Long size = redisTemplate.opsForHyperLogLog().size(redisKey);
        System.out.println(size);

    }

    // 合并3组数据，再统计合并后的重复数据的独立总数
    @Test
    public void testHyperLogLogUnion() {
        String redisKey2 = "tst:hll:02";
        for (int i = 0; i < 10000; i++) {
            redisTemplate.opsForHyperLogLog().add(redisKey2, i);
        }
        String redisKey3 = "tst:hll:03";
        for (int i = 5000; i < 15000; i++) {
            redisTemplate.opsForHyperLogLog().add(redisKey3, i);
        }
        String redisKey4 = "tst:hll:04";
        for (int i = 10000; i < 20000; i++) {
            redisTemplate.opsForHyperLogLog().add(redisKey4, i);
        }
        String unionKey = "test:hll:union";
        redisTemplate.opsForHyperLogLog().union(unionKey, redisKey2, redisKey3, redisKey4);
        System.out.println(redisTemplate.opsForHyperLogLog().size(unionKey));

    }

    @Test
    public void testBitMap() {
        String redisKey = "test:bm:01";

        redisTemplate.opsForValue().setBit(redisKey, 0, true);
        redisTemplate.opsForValue().setBit(redisKey, 3, true);
        redisTemplate.opsForValue().setBit(redisKey, 9, true);

        System.out.println(redisTemplate.opsForValue().getBit(redisKey, 0));
        System.out.println(redisTemplate.opsForValue().getBit(redisKey, 1));

        Object o = redisTemplate.execute(new RedisCallback() {
            @Override
            public Object doInRedis(RedisConnection connection) throws DataAccessException {
                return connection.bitCount(redisKey.getBytes());
            }
        });
        System.out.println(o);
    }


    @Test
    public void testBitMapOps() {
        String redisKey2 = "test:bm:02";
        redisTemplate.opsForValue().setBit(redisKey2, 0, true);
        redisTemplate.opsForValue().setBit(redisKey2, 1, true);
        redisTemplate.opsForValue().setBit(redisKey2, 2, true);
        String redisKey3 = "test:bm:03";
        redisTemplate.opsForValue().setBit(redisKey3, 2, true);
        redisTemplate.opsForValue().setBit(redisKey3, 3, true);
        redisTemplate.opsForValue().setBit(redisKey3, 4, true);
        String redisKey4 = "test:bm:04";
        redisTemplate.opsForValue().setBit(redisKey4, 4, true);
        redisTemplate.opsForValue().setBit(redisKey4, 5, true);
        redisTemplate.opsForValue().setBit(redisKey4, 7, true);
        String redisKey = "test:bm:or";
        Object o = redisTemplate.execute(new RedisCallback() {
            @Override
            public Object doInRedis(RedisConnection connection) throws DataAccessException {
                connection.bitOp(RedisStringCommands.BitOperation.OR,
                        redisKey.getBytes(),
                        redisKey2.getBytes(), redisKey3.getBytes(), redisKey4.getBytes());
                return connection.bitCount(redisKey.getBytes());
            }
        });
        System.out.println(o);
        for (int i = 0; i < 7; i++) {
            System.out.println(redisTemplate.opsForValue().getBit(redisKey, i));
        }
    }

}
