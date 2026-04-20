package com.virality.engine.service;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class NotificationScheduler {

    private final StringRedisTemplate redisTemplate;

    public NotificationScheduler(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Scheduled(fixedRate = 300000)
    public void processNotifications() {

        Set<String> keys = redisTemplate.keys("user:*:pending_notifs");

        if (keys == null || keys.isEmpty()) return;

        for (String key : keys) {

            Long count = redisTemplate.opsForList().size(key);

            if (count != null && count > 0) {

                Long userId = Long.parseLong(key.split(":")[1]);

                System.out.println(
                        "Summarized Push Notification: Bot X and " + count +
                                " others interacted with your posts (User " + userId + ")"
                );

                redisTemplate.delete(key);
            }
        }
    }}