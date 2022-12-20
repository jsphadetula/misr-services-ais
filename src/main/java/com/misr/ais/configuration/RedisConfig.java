package com.misr.ais.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisKeyValueAdapter;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;

import com.misr.ais.service.LandService;

@Configuration
@EnableRedisRepositories(enableKeyspaceEvents = RedisKeyValueAdapter.EnableKeyspaceEvents.ON_STARTUP)
public class RedisConfig {

  @Bean
  public StringRedisTemplate redisTemplate(RedisConnectionFactory redisConnectionFactory) {
    return new StringRedisTemplate(redisConnectionFactory);
  }

  @Bean
  public ValueOperations<String, String> valueOperations(StringRedisTemplate stringRedisTemplate) {
    return stringRedisTemplate.opsForValue();
  }

  @Bean
  RedisMessageListenerContainer keyExpirationListenerContainer(RedisConnectionFactory connectionFactory,
      LandService landService) {

    RedisMessageListenerContainer listenerContainer = new RedisMessageListenerContainer();
    listenerContainer.setConnectionFactory(connectionFactory);

    listenerContainer.addMessageListener((message, pattern) -> {
      String key = new String(message.getBody());
      Long slot = Long.valueOf(key.split(":")[1]);

      landService.notifyIrrigationSensor(slot);
    }, new PatternTopic("__keyevent@*__:expired"));

    return listenerContainer;
  }

}
