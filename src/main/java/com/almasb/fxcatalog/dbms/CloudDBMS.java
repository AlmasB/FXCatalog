package com.almasb.fxcatalog.dbms;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import com.almasb.fxcatalog.data.BookCollection;
import com.almasb.fxcatalog.data.User;

import io.orchestrate.client.Client;
import io.orchestrate.client.KvObject;
import io.orchestrate.client.OrchestrateClient;

public class CloudDBMS implements DBMS<User, BookCollection> {

    private Client client;

    public CloudDBMS() {

    }

    @Override
    public void connect() throws Exception {
        String key = "";
        try (BufferedReader br = new BufferedReader(new InputStreamReader(
                getClass().getResourceAsStream("/orchestrate.key")))) {
            key = br.readLine();
        }

        client = OrchestrateClient.builder(key)
                .host("https://api.aws-eu-west-1.orchestrate.io")
                .build();
    }

    @Override
    public void close() throws Exception {
        client.close();
    }

    @Override
    public boolean containsKey(User key) {
        KvObject<BookCollection> kv = client.kv("books", key.key()).get(BookCollection.class).get();
        return kv != null;
    }

    @Override
    public BookCollection get(User key) {
        return client.kv("books", key.key()).get(BookCollection.class).get().getValue();
    }

    @Override
    public void put(User key, BookCollection entry) {
        client.kv("books", key.key()).put(entry).get();
    }
}
