package com.kralofsky.citybikes.persistance;

import org.apache.commons.lang3.NotImplementedException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.abilitybots.api.db.DBContext;
import org.telegram.abilitybots.api.db.Var;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class TelegramDBWrapper implements DBContext {
    private Persistence persistence;
    private RedissonClient db;

    @Autowired
    public TelegramDBWrapper(Persistence persistence) {
        this.persistence = persistence;
        db = persistence.getDB();
    }

    @Override
    public <T> List<T> getList(String name) {
        return db.getList(name);
    }

    @Override
    public <K, V> Map<K, V> getMap(String name) {
        return new AbstractMap<K, V>() {
            @Override
            public int size() {
                return db.getSet(name).size();
            }

            @Override
            public boolean isEmpty() {
                return db.getSet(name).isEmpty();
            }

            @Override
            public boolean containsKey(Object o) {
                return db.getSet(name).contains(o);
            }

            @Override
            public boolean containsValue(Object o) {
                return db.getSet(name).stream()
                        .map(k -> db.getBucket(name + ":" + k).get())
                        .anyMatch(obj -> obj.equals(o));
            }

            @Override
            public V get(Object o) {
                return db.<V>getBucket(name + ":" + o).get();
            }

            @Nullable
            @Override
            public V put(K k, V v) {
                db.<V>getBucket(name + ":" + k).set(v);
                db.getSet(name).add(k);
                return v;
            }

            @Override
            public V remove(Object o) {
                db.getSet(name).remove(o);
                return db.<V>getBucket(name + ":" + o).getAndDelete();
            }

            @NotNull
            @Override
            public Set<Entry<K, V>> entrySet() {
                return db.<K>getSet(name).stream().map(k -> new Entry<K, V>(){
                    @Override
                    public K getKey() {
                        return k;
                    }

                    @Override
                    public V getValue() {
                        return db.<V>getBucket(name + ":" + k).get();
                    }

                    @Override
                    public V setValue(V v) {
                        db.<V>getBucket(name + ":" + k).set(v);
                        return v;
                    }
                }).collect(Collectors.toSet());
            }
        };
    }

    @Override
    public <T> Set<T> getSet(String name) {
        return persistence.getDB().getSet(name);
    }

    @Override
    public <T> Var<T> getVar(String name) {
        return new Var<T>() {
            RBucket<T> bucket = persistence.getDB().getBucket(name);

            @Override
            public T get() {
                return bucket.get();
            }

            @Override
            public void set(T var) {
                bucket.set(var);
            }
        };
    }

    @Override
    public String summary() {
        return "No summery avalible for redis";
    }

    @Override
    public Object backup() {
        throw new NotImplementedException("No backup avalible for redis");
    }

    @Override
    public boolean recover(Object backup) {
        throw new NotImplementedException("No backup avalible for redis");
    }

    @Override
    public String info(String name) {
        return "No info avalible for redis";
    }

    @Override
    public void commit() {}

    @Override
    public void clear() {
        throw new NotImplementedException("Clear not avalible for redis");
    }

    @Override
    public boolean contains(String name) {
        return persistence.getDB().getKeys().countExists(name) > 0;
    }

    @Override
    public void close() throws IOException {
        persistence.getDB().shutdown();
    }
}
