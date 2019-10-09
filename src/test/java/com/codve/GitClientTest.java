package com.codve;

import org.eclipse.jgit.api.DiffCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.LogCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.treewalk.AbstractTreeIterator;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        client.setGit(git);
        client.setRepository(repository);
    }

    /**
     * 获取添加, 删除, 修改的文件列表
     */
    @Test
    public void WhenGetFileMapThenReturnMap() throws GitAPIException, IOException {
        // 构造 git 提交记录
        RevCommit oldCommit = mock(RevCommit.class);
        RevTree oldTree = mock(RevTree.class);
//        doReturn(oldTree).when(oldCommit).getTree();

        ObjectId oldCommitId = mock(ObjectId.class);
//        doReturn(oldCommitId).when(oldTree).getId();

        RevCommit newCommit = mock(RevCommit.class);
        RevTree newTree = mock(RevTree.class);
//        doReturn(newTree).when(newCommit).getTree();

        ObjectId newCommitId = mock(ObjectId.class);
//        doReturn(newCommitId).when(newTree).getId();

        List<RevCommit> logs = new ArrayList<>();
        logs.add(oldCommit);
        logs.add(newCommit);

        LogCommand logCommand = mock(LogCommand.class);
        doReturn(logCommand).when(git).log();
        logCommand.setMaxCount(2);
        doReturn(logs).when(logCommand).call();

        ObjectReader objectReader = mock(ObjectReader.class);
        doReturn(objectReader).when(repository).newObjectReader();

        DiffEntry diffEntry = mock(DiffEntry.class);
        doReturn("file.txt").when(diffEntry).getNewPath();
        doReturn(ChangeType.MODIFY).when(diffEntry).getChangeType();

        List<DiffEntry> diffEntries = new ArrayList<>();
        diffEntries.add(diffEntry);

        DiffCommand diffCommand = mock(DiffCommand.class);
        doReturn(diffCommand).when(git).diff();

        AbstractTreeIterator newIterator = mock(AbstractTreeIterator.class);
        AbstractTreeIterator oldIterator = mock(AbstractTreeIterator.class);
        doReturn(diffEntries).when(diffCommand).call();

        Map<String, ChangeType> fileMap = new HashMap<>();
        for (DiffEntry entry : diffEntries) {
            fileMap.put(entry.getNewPath(), entry.getChangeType());
        }
        assertEquals(fileMap, client.getFileMap());

    }


}