package com.kralofsky.citybikes.persistance;

import org.springframework.stereotype.Component;
import org.telegram.abilitybots.api.db.DBContext;
import org.telegram.abilitybots.api.db.Var;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.telegram.abilitybots.api.db.MapDBContext.onlineInstance;

@Component
public class TelegramDBWrapper implements DBContext, Persistance {
    private DBContext db;

    public TelegramDBWrapper() {
        db = onlineInstance("bot.db");
    }

    @Override
    public <T> List<T> getList(String name) {
        return db.getList(name);
    }

    @Override
    public <K, V> Map<K, V> getMap(String name) {
        return db.getMap(name);
    }

    @Override
    public <T> Set<T> getSet(String name) {
        return db.getSet(name);
    }

    @Override
    public <T> Var<T> getVar(String name) {
        return db.getVar(name);
    }

    @Override
    public String summary() {
        return db.summary();
    }

    @Override
    public Object backup() {
        return db.backup();
    }

    @Override
    public boolean recover(Object backup) {
        return db.recover(backup);
    }

    @Override
    public String info(String name) {
        return db.info(name);
    }

    @Override
    public void commit() {
        db.commit();
    }

    @Override
    public void clear() {
        db.clear();
    }

    @Override
    public boolean contains(String name) {
        return db.contains(name);
    }

    @Override
    public void close() throws IOException {
        db.close();
    }
}
