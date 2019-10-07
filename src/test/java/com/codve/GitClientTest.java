package com.codve;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Repository;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;

import java.io.IOException;
import java.util.HashMap;

import static org.eclipse.jgit.diff.DiffEntry.ChangeType;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class GitClientTest {

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Rule
    public TemporaryFolder dir = new TemporaryFolder();

    private GitClient client;
    private Git git;
    private Repository repository;

    @Before
    public void before() throws GitAPIException {
        client = spy(new GitClient(dir.getRoot()));
        git = mock(Git.class);
        repository = mock(Repository.class);
    }

    /**
     * 获取添加, 删除, 修改的文件列表
     */
    @Test
    public void WhenGetFileMapThenReturnMap() throws GitAPIException, IOException {
        HashMap<String, ChangeType> fileMap = new HashMap<>();

        fileMap.put("/add.html", ChangeType.ADD);
        fileMap.put("/modify.html", ChangeType.MODIFY);
        fileMap.put("/delete.html", ChangeType.DELETE);
        fileMap.put("/rename.html", ChangeType.RENAME);
        doReturn(fileMap).when(client).getFileMap();
        client.getFileMap();
        assertEquals(fileMap, client.getFileMap());
    }


}