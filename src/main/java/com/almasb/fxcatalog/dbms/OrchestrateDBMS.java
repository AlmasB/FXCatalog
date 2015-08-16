package com.almasb.fxcatalog.dbms;

import java.util.ResourceBundle;

import io.orchestrate.client.Client;
import io.orchestrate.client.KvObject;
import io.orchestrate.client.OrchestrateClient;

public class OrchestrateDBMS<K extends DBMSKey, E> implements DBMS<K, E> {

    @SuppressWarnings("unused")
    private Class<K> keyClass;
    private Class<E> entryClass;

    private Client client;

    private String apiKey;
    private String host;
    private String collection;

    public OrchestrateDBMS(Class<K> keyClass, Class<E> entryClass, ResourceBundle props) {
        this.keyClass = keyClass;
        this.entryClass = entryClass;

        apiKey = props.getString("api_key");
        host = props.getString("host");
        collection = props.getString("collection");
    }

    @Override
    public void connect() throws Exception {
        client = OrchestrateClient.builder(apiKey)
                .host(host)
                .build();
    }

    @Override
    public void close() throws Exception {
        client.close();
    }

    @Override
    public boolean containsKey(K key) {
        KvObject<E> kv = client.kv(collection, key.key()).get(entryClass).get();
        return kv != null;
    }

    @Override
    public E get(K key) {
        return client.kv(collection, key.key()).get(entryClass).get().getValue();
    }

    @Override
    public void put(K key, E entry) {
        client.kv(collection, key.key()).put(entry).get();
    }
}
