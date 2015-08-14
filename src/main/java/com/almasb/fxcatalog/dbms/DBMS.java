package com.almasb.fxcatalog.dbms;

/**
 *
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 *
 * @param <K> key (unique database id)
 * @param <E> entry (all other database columns)
 */
public interface DBMS<K, E> {
    public void connect() throws Exception;
    public void close() throws Exception;

    public boolean containsKey(K key);
    public E get(K key);
    public void put(K key, E entry);
}
