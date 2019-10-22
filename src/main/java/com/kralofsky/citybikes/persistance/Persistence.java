package com.kralofsky.citybikes.persistance;

import com.kralofsky.citybikes.config.Values;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.codec.JsonJacksonCodec;
import org.redisson.config.Config;
import org.redisson.config.SingleServerConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URI;

@Component
public class Persistence {
    private RedissonClient db;

    @Autowired
    public Persistence(Values values) {
        String redisUriString = values.getRedisURL();
        URI redisUri = URI.create(redisUriString);

        Config config = new Config();
        SingleServerConfig serverConfig = config.useSingleServer()
                .setAddress(redisUriString)
                .setConnectionPoolSize(10)
                .setConnectionMinimumIdleSize(10)
                .setTimeout(5000);

        if (redisUri.getUserInfo() != null) {
            serverConfig.setPassword(redisUri.getUserInfo().substring(redisUri.getUserInfo().indexOf(":")+1));
        }

        db = Redisson.create(config);
    }

    RedissonClient getDB() {
        return db;
    }

    public Integer getHomeStationId(Long userId) {
        return db.<Integer>getBucket(String.format("home_station:%d", userId)).get();
    }

    public void setHomeStationId(Long userId, Integer stationId) {
        db.getBucket(String.format("home_station:%d", userId)).set(stationId);
    }
}
