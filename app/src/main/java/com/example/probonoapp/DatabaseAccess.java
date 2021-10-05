package com.example.probonoapp;

import android.content.Context;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.dynamodbv2.document.ScanOperationConfig;
import com.amazonaws.mobileconnectors.dynamodbv2.document.Search;
import com.amazonaws.mobileconnectors.dynamodbv2.document.Table;
import com.amazonaws.mobileconnectors.dynamodbv2.document.datatype.Document;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;

import java.util.ArrayList;
import java.util.List;

public class DatabaseAccess {
    private String TAG = "DynamoDb_Demo";

    private final String COGNITO_IDENTITY_POOL_ID = "ap-northeast-2:f97cb593-40ce-4834-a1a4-ddac9607a27c";
    private final Regions COGNITO_IDENTITY_POOL_REGION = Regions.AP_NORTHEAST_2;
    private final String DYNAMODB_TABLE = "iotData";
    private Context context;
    private CognitoCachingCredentialsProvider credentialsProvider;
    private AmazonDynamoDBClient dbClient;
    private Table dbTable;

    private static volatile DatabaseAccess instance;

    private DatabaseAccess(Context context){
        this.context = context;
        credentialsProvider = new CognitoCachingCredentialsProvider(context,COGNITO_IDENTITY_POOL_ID, COGNITO_IDENTITY_POOL_REGION);
        dbClient = new AmazonDynamoDBClient(credentialsProvider);
        dbClient.setRegion(Region.getRegion(Regions.AP_NORTHEAST_2));
        dbTable = Table.loadTable(dbClient,DYNAMODB_TABLE);
    }

    public static synchronized DatabaseAccess getInstance(Context context){
        if (instance == null){
            instance = new DatabaseAccess(context);
        }
        return instance;
    }

    //public Document getItem(String telephone){}

    public List<Document> getAllContacts(){
        ScanOperationConfig scanConfig = new ScanOperationConfig();
        List<String> attributeList = new ArrayList<>();
        attributeList.add("button");
        attributeList.add("thirty_mins");
        attributeList.add("sixty_mins");
        attributeList.add("time");
        attributeList.add("enter");
        scanConfig.withAttributesToGet(attributeList);
        Search searchResult = dbTable.scan(scanConfig);
        return searchResult.getAllResults();
    }
}
