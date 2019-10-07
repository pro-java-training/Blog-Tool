package com.codve;

import com.baidubce.Protocol;
import com.baidubce.auth.DefaultBceCredentials;
import com.baidubce.services.bos.BosClient;
import com.baidubce.services.bos.BosClientConfiguration;

import java.io.File;

public class Uploader {

    private String accessId;
    private String accessSecret;
    private String endPoint;
    private String bucket;
    private BosClient client;

    public Uploader(String accessId, String accessSecret, String endPoint, String bucket) {
        this.accessId = accessId;
        this.accessSecret = accessSecret;
        this.endPoint = endPoint;
        this.bucket = bucket;
        setClient();
    }

    public void setClient() {
        BosClientConfiguration config = new BosClientConfiguration();
        config.setCredentials(new DefaultBceCredentials(accessId, accessSecret));
        config.setEndpoint(endPoint);
        config.setProtocol(Protocol.HTTPS);
        config.setConnectionTimeoutInMillis(3000);
        client = new BosClient(config);
    }

    public BosClient getClient() {
        return client;
    }

    public void putFile(String key, File file){
        getClient().putObject(bucket, key, file);
    }

    public void deleteFile(String key){
        getClient().deleteObject(bucket, key);
    }
}
