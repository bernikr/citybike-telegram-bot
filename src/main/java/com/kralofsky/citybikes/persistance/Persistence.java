package com.kralofsky.citybikes.persistance;

import com.kralofsky.citybikes.config.Values;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.codec.JsonJacksonCodec;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Persistence {
    private RedissonClient db;

    @Autowired
    public Persistence(Values values) {
        Config config = new Config();
        config.setCodec(new JsonJacksonCodec());
        config.useSingleServer().setAddress(values.getRedisURL());
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
