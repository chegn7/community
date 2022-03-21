package com.c.community;

import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class BlockingQueueTest {

    public static void main(String[] args) {
        BlockingQueue<Integer> queue = new ArrayBlockingQueue<>(10);
        Producer producer = new Producer(queue);
        Consumer consumer1 = new Consumer(queue);
        Consumer consumer2 = new Consumer(queue);
        Consumer consumer3 = new Consumer(queue);
        new Thread(producer).start();
        new Thread(consumer1).start();
        new Thread(consumer2).start();
        new Thread(consumer3).start();



    }
}

class Producer implements Runnable {

    private BlockingQueue<Integer> queue;

    public Producer(BlockingQueue<Integer> queue) {
        this.queue = queue;
    }

    @Override
    public void run() {
        try {
            for (int i = 0; i < 100; i++) {
                Thread.sleep(20);
                queue.put(i);
                System.out.println(Thread.currentThread().getName() + " produce num :" + queue.size());
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
class Consumer implements Runnable {
    private BlockingQueue<Integer> queue;

    public Consumer(BlockingQueue<Integer> queue) {
        this.queue = queue;
    }

    @Override
    public void run() {
        try {
            while (true) {
                Thread.sleep(new Random().nextInt(1000));
                queue.poll();
                System.out.println(Thread.currentThread().getName() + " consume num :" + queue.size());
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}