package com.kralofsky.citybikes.persistance;

import org.telegram.abilitybots.api.db.Var;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface Persistance {
    /**
     * @param name the unique name of the {@link List}
     * @param <T>  the type that the List holds
     * @return the List with the specified name
     */
    <T> List<T> getList(String name);

    /**
     * @param name the unique name of the {@link Map}
     * @param <K>  the type of the Map keys
     * @param <V>  the type of the Map values
     * @return the Map with the specified name
     */
    <K, V> Map<K, V> getMap(String name);

    /**
     * @param name the unique name of the {@link Set}
     * @param <T>  the type that the Set holds
     * @return the Set with the specified name
     */
    <T> Set<T> getSet(String name);

    /**
     * @param name the unique name of the {@link Var}
     * @param <T>  the type that the variable holds
     * @return the variable with the specified name
     */
    <T> Var<T> getVar(String name);
}
