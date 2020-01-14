package com.service;

import com.mongodb.Block;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import com.util.MongoConnection;
import org.bson.Document;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.mongodb.client.model.Filters.*;
import static com.mongodb.client.model.Updates.inc;

public class ActionForDb {
    public static void main(String[] args) {
        MongoClient client = MongoConnection.getConnection();
        //如果不存在会新建这个db
        MongoDatabase database = client.getDatabase("testdb");
        //获取collection,如果不存在会创建
        MongoCollection<Document> collection = database.getCollection("firstCollection");
        /**
         * 插入操作
         */
        //文档数据
        Document doc = new Document("name","HelloWorld")
                .append("type","doc")
                .append("count",1)
                .append("versions", Arrays.asList("V1.0,V1.0.1"))
                .append("info",new Document("add","1").append("create","root"));
        collection.insertOne(doc);
        //插入多条文档数据
        List<Document> documents = new ArrayList<>();
        for (int i = 0; i < 100 ; i++) {
            documents.add(new Document("i",i));
        }
        collection.insertMany(documents);

        //统计记录数量
        System.out.println(collection.countDocuments());

        System.out.println("-------------------------------------------------");

        /**
         * 查询操作
         */
        //查询第一个
        Document findFirstOne = collection.find().first();
        System.out.println(findFirstOne.toJson());
        System.out.println("-------------------------------------------------");
        //指定条件查询一个
        Document one = collection.find(eq("i",71)).first();
        System.out.println(one.toJson());
        System.out.println("-------------------------------------------------");
        //查询所有Docs
        MongoCursor<Document> cursor = collection.find().iterator();
        try {
            while (cursor.hasNext()){
                System.out.println(cursor.next().toJson());
            }
        }finally {
            cursor.close();
        }
        System.out.println("-------------------------------------------------");
        for (Document cur : collection.find()){
            System.out.println(cur.toJson());
        }
        System.out.println("-------------------------------------------------");

        Block<Document> block = new Block<Document>() {
            @Override
            public void apply(Document document) {
                System.out.println(document.toJson());
            }
        };
        System.out.println("-------------------------------------------------");
        //条件过滤获取一部分docs
        collection.find(gt("i",50)).forEach(block);
        System.out.println("-------------------------------------------------");
        collection.find(and(gt("i",50),lte("i",100))).forEach(block);

        System.out.println("-------------------------------------------------");
        /**
         * 更新操作
         */
        //更新一个
        collection.updateOne(eq("i",10),new Document("$set",new Document("i",110)));
        //更新多个docs
        UpdateResult result = collection.updateMany(lt("1",100),inc("1",100));
        System.out.println(result.getModifiedCount());

        System.out.println("-------------------------------------------------");

        /**
         * 删除操作
         */
        collection.deleteOne(eq("i",110));
        DeleteResult deleteResult = collection.deleteMany(gte("i", 100));
        System.out.println(deleteResult.getDeletedCount());

        System.out.println("-------------------------------------------------");
        /**
         * 创建索引
         */
        collection.createIndex(new Document("i",1));
    }
}
