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
    }

    /**
     * 上传文件
     */
    @Test
    public void putFile(){
        String key = file.getName();
        doReturn(client).when(uploader).getClient();
        uploader.putFile(key, file);
        verify(client, times(1)).putObject(bucket, key, file);
    }

    /**
     * 上传文件出错时抛出异常
     */
    @Test(expected = Exception.class)
    public void WhenPutFileFailedThenException(){
        String key = file.getName();
        doReturn(client).when(uploader).getClient();
        doThrow(Exception.class).when(client).putObject(bucket, key, file);
        uploader.putFile(key, file);
    }

    /**
     * 删除文件
     */
    @Test
    public void deleteFile() {
        String key = file.getName();
        doReturn(client).when(uploader).getClient();
        uploader.deleteFile(key);
        verify(client, times(1)).deleteObject(bucket, key);
    }

    /**
     * 删除时出错
     */
    @Test(expected = Exception.class)
    public void whenDeleteFileFailedThenException() throws Exception{
        String key = file.getName();
        doReturn(client).when(uploader).getClient();
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
        doReturn(client).when(uploader).getClient();
        doReturn(request).when(uploader).getDeleteRequest();
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
        doReturn(client).when(uploader).getClient();
        doReturn(request).when(uploader).getListRequest();

        // 构造返回结果
        List<BosObjectSummary> contents = new ArrayList<>();
        contents.add(mock(BosObjectSummary.class));

        ListObjectsResponse response = mock(ListObjectsResponse.class);
        response.setContents(contents);

        response.setTruncated(false);
        doReturn(response).when(client).listObjects(request);

        uploader.listFiles();
        verify(client, times(1)).listObjects(request);

    }

}
