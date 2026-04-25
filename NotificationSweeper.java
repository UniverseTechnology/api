package com.coreapi.api;


import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class NotificationSweeper {
    private final RedisTemplate<String, String> redisTemplate;

    @Scheduled(fixedRate = 300000)
    public void sweepPendingNotifications(){
        Set<String> keys= redisTemplate.keys("user:*:pending_notification");
        if (keys==null)
            return;

        for(String key : keys){
            List<String> messages = redisTemplate.opsForList().range(key,0,-1);
            if(messages != null && !messages.isEmpty()){
                System.out.println("Summerized Push Notification:"+messages.get(0)+"and ["+(messages.size() -1 )+"] others interacted.");
                redisTemplate.delete(key);
            }
        }
    }
}
