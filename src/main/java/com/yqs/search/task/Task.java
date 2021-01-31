package com.yqs.search.task;

import com.yqs.search.index.IndexFileInterface;
import lombok.Data;
import java.io.File;

@Data
public class Task extends Thread {

    //文件索引信息
    private IndexFileInterface iff;

    //文件名
    private String fileName;

    //文件后缀
    private String suffix;

    //文件
    private File file;


    public Task(IndexFileInterface iff, File file, String fileName) {

        this.iff = iff;
        this.file = file;
        this.fileName = fileName;

    }

    @Override
    public void run() {
        try {
            writeIndex();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void writeIndex() throws Exception {
        iff.indexFile(file, fileName, suffix);
        file = null;
        fileName = null;
        suffix = null;
    }
}
