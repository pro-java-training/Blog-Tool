package com.codve;

import com.baidubce.services.bos.BosClient;
import com.baidubce.services.bos.model.BosObjectSummary;
import com.baidubce.services.bos.model.DeleteMultipleObjectsRequest;
import com.baidubce.services.bos.model.ListObjectsRequest;
import com.baidubce.services.bos.model.ListObjectsResponse;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;

/**
 * 这是一个类
 */
public class UploaderTest {

    @Rule
    public ExpectedException exception = ExpectedException.none();

    private Uploader uploader;
    private File file;
    private BosClient client;
    private String accessId;
    private String accessSecret;
    private String endPoint;
    private String bucket;


    @Before
    public void before() {
        accessId = "id";
        accessSecret = "secret";
        endPoint = "endPoint";
        bucket = "bucket";

        uploader = spy(new Uploader(accessId, accessSecret, endPoint, bucket));
        file = mock(File.class);
        client = mock(BosClient.class);
        uploader.setClient(client);
    }

    /**
     * 上传文件
     */
    @Test
    public void putFile(){
        String key = file.getName();
        uploader.putFile(key, file);
        verify(client, times(1)).putObject(bucket, key, file);
    }

    /**
     * 上传文件出错时抛出异常
     */
    @Test(expected = Exception.class)
    public void WhenPutFileFailedThenException(){
        String key = file.getName();
        doThrow(Exception.class).when(client).putObject(bucket, key, file);
        uploader.putFile(key, file);
    }

    /**
     * 删除文件
     */
    @Test
    public void deleteFile() {
        String key = file.getName();
        uploader.deleteFile(key);
        verify(client, times(1)).deleteObject(bucket, key);
    }

    /**
     * 删除时出错
     */
    @Test(expected = Exception.class)
    public void whenDeleteFileFailedThenException() throws Exception{
        String key = file.getName();
        doThrow(Exception.class).when(client).deleteObject(bucket, key);
        uploader.deleteFile(key);
    }

    /**
     * 批量删除文件
     */
    @Test
    public void deleteFiles() {
        List<String> files = new ArrayList<>();
        files.add("file1");
        files.add("file2");
        DeleteMultipleObjectsRequest request = new DeleteMultipleObjectsRequest();
        request.setBucketName(bucket);
        request.setObjectKeys(files);
        uploader.setDeleteRequest(request);

        uploader.deleteFiles(files);
        verify(client, times(1)).deleteMultipleObjects(request);
    }

    /**
     * 列出 bucket 下面的所有文件
     */
    @Test
    public void listFiles() {
        ListObjectsRequest request = new ListObjectsRequest(bucket);
        request.setMaxKeys(1000);
        uploader.setListRequest(request);

        // 构造返回结果
        BosObjectSummary summary = new BosObjectSummary();
        summary.setKey("key");

        List<BosObjectSummary> contents = new ArrayList<>();
        contents.add(summary);

        ListObjectsResponse response = new ListObjectsResponse();
        response.setContents(contents);
        response.setNextMarker("next");
        response.setTruncated(false);

        doReturn(response).when(client).listObjects(request);

        List<String> files = new ArrayList<>();
        for (BosObjectSummary tmpSummary : contents) {
            files.add(tmpSummary.getKey());
        }
        assertEquals(files, uploader.listFiles());
    }

}
