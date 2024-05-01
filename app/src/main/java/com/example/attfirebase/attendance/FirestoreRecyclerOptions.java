package com.example.attfirebase.attendance;

import com.google.common.cache.CacheBuilder;
import com.google.firebase.firestore.Query;

import java.util.Date;

public class FirestoreRecyclerOptions<T> {

    public static class Builder<T> {
        public CacheBuilder<Object, Object> setQuery(Query query, Class<Date> studentClass) {
            return null;
        }
    }
}
