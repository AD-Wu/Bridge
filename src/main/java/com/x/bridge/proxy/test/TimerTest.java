package com.x.bridge.proxy.test;

import java.time.LocalTime;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class TimerTest {

    public static void main(String[] args) {
        scheduleTest();
    }

    private static void timerTest() {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                System.out.println("定时任务-1，当前时间：" + LocalTime.now());
                try {
                    TimeUnit.SECONDS.sleep(3);
                } catch (InterruptedException interruptedException) {
                    interruptedException.printStackTrace();
                }
            }
        }, 100, 2000);
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                System.out.println("定时任务-2，当前时间：" + LocalTime.now());
            }
        }, 100, 2000);
    }

    private static void scheduleTest() {
        ScheduledExecutorService sche = Executors.newScheduledThreadPool(2);

        sche.scheduleWithFixedDelay(() -> {
            System.out.println("定时任务-1，当前时间：" + LocalTime.now());
            try {
                TimeUnit.SECONDS.sleep(5);
            } catch (InterruptedException interruptedException) {
                interruptedException.printStackTrace();
            }
        }, 100, 2000, TimeUnit.MILLISECONDS);

        sche.scheduleWithFixedDelay(() -> {
            System.out.println("定时任务-2，当前时间：" + LocalTime.now());
        }, 100, 2000, TimeUnit.MILLISECONDS);
    }

}
