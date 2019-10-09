package com.codve;

import com.baidubce.Protocol;
import com.baidubce.auth.DefaultBceCredentials;
import com.baidubce.services.bos.BosClient;
import com.baidubce.services.bos.BosClientConfiguration;
import com.baidubce.services.bos.model.BosObjectSummary;
import com.baidubce.services.bos.model.DeleteMultipleObjectsRequest;
import com.baidubce.services.bos.model.ListObjectsRequest;
import com.baidubce.services.bos.model.ListObjectsResponse;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Uploader {

    private String accessId;
    private String accessSecret;
    private String endPoint;
    private String bucket;
    private BosClient client;
    private DeleteMultipleObjectsRequest deleteRequest;
    private ListObjectsRequest listRequest;

    public Uploader(String accessId, String accessSecret, String endPoint, String bucket) {
        this.accessId = accessId;
        this.accessSecret = accessSecret;
        this.endPoint = endPoint;
        this.bucket = bucket;
        setClient();
        setDeleteRequest();
        setListRequest();
    }

    public void setClient() {
        BosClientConfiguration config = new BosClientConfiguration();
        config.setCredentials(new DefaultBceCredentials(accessId, accessSecret));
        config.setEndpoint(endPoint);
        config.setProtocol(Protocol.HTTPS);
        config.setConnectionTimeoutInMillis(3000);
        setClient(new BosClient(config));
    }

    public void setClient(BosClient client) {
        this.client = client;
    }

    public BosClient getClient() {
        return client;
    }

    private void setDeleteRequest() {
        DeleteMultipleObjectsRequest tmpDeleteRequest = new DeleteMultipleObjectsRequest();
        tmpDeleteRequest.setBucketName(bucket);
        setDeleteRequest(tmpDeleteRequest);
    }

    public void setDeleteRequest(DeleteMultipleObjectsRequest deleteRequest) {
        this.deleteRequest = deleteRequest;
    }

    public DeleteMultipleObjectsRequest getDeleteRequest() {
        return deleteRequest;
    }

    public void setListRequest() {
        ListObjectsRequest tmpListRequest = new ListObjectsRequest(bucket);
        tmpListRequest.setMaxKeys(1000);
        setListRequest(tmpListRequest);
    }

    public void setListRequest(ListObjectsRequest listRequest) {
        this.listRequest = listRequest;
    }

    public ListObjectsRequest getListRequest() {
        return listRequest;
    }


    public void putFile(String key, File file){
        client.putObject(bucket, key, file);
    }

    public void deleteFile(String key){
        client.deleteObject(bucket, key);
    }

    public void deleteFiles(List<String> files) {
        deleteRequest.setObjectKeys(files);
        client.deleteMultipleObjects(deleteRequest);
    }

    public List<String> listFiles() {
        List<String> files = new ArrayList<>();
        ListObjectsResponse response;
        boolean isTruncated = true;
        while (isTruncated) {
            response = client.listObjects(listRequest);
            for (BosObjectSummary summary : response.getContents()) {
                files.add(summary.getKey());
            }
            isTruncated = response.isTruncated();
            if (response.getNextMarker() != null) {
                listRequest.withMarker(response.getNextMarker());
            }
        }
        listRequest.setMarker("");
        return files;
    }
}
