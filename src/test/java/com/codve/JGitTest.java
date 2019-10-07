package com.codve;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.Status;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.treewalk.AbstractTreeIterator;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;

public class JGitTest {
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    private Git git;
    private Repository repository;

    @Before
    public void setUp() throws GitAPIException {
        // 初始化 git 目录, 如果已经是 git 目录, 就不会被覆盖.
        git = Git.init().setDirectory(folder.getRoot()).call();
        repository = git.getRepository();
    }

    @After
    public void tearDown() {
        repository.close();
    }

    /**
     * git add
     */
    @Test
    public void add() throws IOException, GitAPIException {
        File file = writeToFile("config.json", "work: 1024");
        git.add().addFilepattern(file.getName()).call();

        Status status = git.status().call();
        Set<String> addedFiles = status.getAdded();
        for (String filename : addedFiles) {
            System.out.println(filename);
        }
        assertEquals(1, addedFiles.size());
    }

    /**
     * git commit
     * @throws IOException IOException
     * @throws GitAPIException GitAPIException
     */
    @Test
    public void commit() throws IOException, GitAPIException {
        File file = writeToFile("config.yml", "worker: 1024");
        git.add().addFilepattern(file.getName()).call();
        git.commit().setAuthor("Jimmy", "mailAddress.com")
                .setMessage("the first time to commit.")
                .call();

        file = writeToFile("config.json", "worker: 1024\nmemory: 2048MB");
        git.add().addFilepattern(file.getName()).call();
        git.commit().setMessage("the second time to commit").call();

        // 获取最近的 2 次提交
        Iterable<RevCommit> log = git.log().setMaxCount(2).call();
        Iterator<RevCommit> iterator = log.iterator();

        RevCommit newCommit = iterator.next(); // 新的提交
        ObjectId newTreeId = newCommit.getTree().getId();
        System.out.println(newCommit.toString());

        RevCommit oldCommit = iterator.next(); // 旧的提交
        ObjectId oldTreeId = oldCommit.getTree().getId();
        System.out.println(oldCommit.toString());

        // 比较 2 次提交的差异
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ObjectReader newReader = repository.newObjectReader();
        ObjectReader oldReader = repository.newObjectReader();

        AbstractTreeIterator newTree = new CanonicalTreeParser(null, newReader, newTreeId);
        AbstractTreeIterator oldTree = new CanonicalTreeParser(null, oldReader, oldTreeId);
        List<DiffEntry> diffEntries = git.diff()
                .setShowNameAndStatusOnly(true)
                .setOutputStream(outputStream)
                .setNewTree(newTree)
                .setOldTree(oldTree)
                .call();
        System.out.println(outputStream.toString());

        // 断言 文件的修改个数为 1
        assertEquals(1, diffEntries.size());

        for (DiffEntry entry : diffEntries) {
            StringBuilder builder = new StringBuilder();
            builder.append(entry.getChangeType()).append(" ");
            builder.append(entry.getNewPath()).append(" ");
            System.out.println(builder.toString());
        }
    }

    // 创建文件, 并写入数据
    private File writeToFile(String filename, String content) throws IOException {
        File file = new File(git.getRepository().getWorkTree(), filename);

        try (FileOutputStream outputStream = new FileOutputStream(file)) {
            outputStream.write(content.getBytes(StandardCharsets.UTF_8));
        }
        return file;

    }
}
