package com.util;

import com.mongodb.MongoClient;

public class MongoConnection {
    public static MongoClient getConnection(){
        MongoClient mongoClient = new MongoClient("172.20.10.12",27017);
        return mongoClient;
    }
}
