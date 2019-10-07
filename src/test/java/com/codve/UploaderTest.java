package com.codve;

import com.baidubce.services.bos.BosClient;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.File;

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

}
