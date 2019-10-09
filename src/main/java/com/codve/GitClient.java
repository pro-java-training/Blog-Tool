package com.codve;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.treewalk.AbstractTreeIterator;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static org.eclipse.jgit.diff.DiffEntry.ChangeType;

public class GitClient {
    private Repository repository;

    private File dir;

    private Git git;

    public GitClient(File dir) throws GitAPIException {
        setDir(dir);
        setGit();
        setRepository();
    }

    public void setDir(File dir) {
        this.dir = dir;
    }

    public void setGit() throws GitAPIException {
        git = Git.init().setDirectory(dir).call();
    }

    public void setGit(Git git) {
        this.git = git;
    }

    public void setRepository() {
        repository = git.getRepository();
    }

    public void setRepository(Repository repository) {
        this.repository = repository;
    }

    public Map<String, ChangeType> getFileMap() throws GitAPIException, IOException {
        Map<String, ChangeType> fileMap = new HashMap<>();

        // 获取最近的 2 次提交
        Iterable<RevCommit> log = git.log().setMaxCount(2).call();
        Iterator<RevCommit> iterator = log.iterator();

        RevCommit newCommit = iterator.next(); // 新的提交
        ObjectId newTreeId = newCommit.getTree().getId();

        RevCommit oldCommit = iterator.next(); // 旧的提交
        ObjectId oldTreeId = oldCommit.getTree().getId();

        // 比较 2 次提交的差异
        ObjectReader newReader = repository.newObjectReader();
        ObjectReader oldReader = repository.newObjectReader();

        AbstractTreeIterator newTree = new CanonicalTreeParser(null, newReader, newTreeId);
        AbstractTreeIterator oldTree = new CanonicalTreeParser(null, oldReader, oldTreeId);
        List<DiffEntry> diffEntries = git.diff()
                .setShowNameAndStatusOnly(true)
                .setNewTree(newTree)
                .setOldTree(oldTree)
                .call();

        for (DiffEntry entry : diffEntries) {
            fileMap.put(entry.getNewPath(), entry.getChangeType());
        }
        return fileMap;
    }
}