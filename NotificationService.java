package com.coreapi.api;


import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private final RedisTemplate<String, String> redisTemplate;

    public void handleBotNotification(Long userId, String message){
        String cooldownKey ="notification:cooldown:"+userId;
        String pendingKey = "user:"+userId+":pending_notification";

        Boolean onCooldown =redisTemplate.hasKey(cooldownKey);

        if (Boolean.TRUE.equals(onCooldown)) {
            redisTemplate.opsForList().leftPush(pendingKey, message);
        } else{
            System.out.println("Push Notification sent to user"+userId);
            redisTemplate.opsForValue().set(cooldownKey,"1",15, TimeUnit.MINUTES);
        }
    }
}
