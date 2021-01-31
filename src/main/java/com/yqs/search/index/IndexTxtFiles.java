package com.yqs.search.index;

import com.yqs.search.base.BaseDocument;
import com.yqs.search.constants.FieldConstant;
import com.yqs.search.util.FileCodingUtil2;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.TextField;

import java.io.*;

/**
 * 索引txt文本
 */
public class IndexTxtFiles implements IndexFileInterface {

    /**
     * 索引文件 存入配置文件中
     */
    @Override
    public void indexFile(File file, String upFileName, String suffix) {
        Document doc = getSimDoc(file, upFileName, suffix);

        if (file.length() > 0) {
            try {
                //获取字符集名称
                suffix = FileCodingUtil2.getCharsetName(file, 100);
                //
                FileInputStream fis = new FileInputStream(file);
                Reader reader = new InputStreamReader(fis, suffix);
                TextField tx = new TextField(FieldConstant.FILE_CONTENT, reader);
                doc.add(tx);
                BaseDocument.addDocument(doc);
                tx = null;

            } catch (IOException e) {
                System.out.println(file + "拒绝访问");
            } finally {
                suffix = null;
                file = null;
                upFileName = null;
                doc = null;
            }
        }
    }

    StringBuilder sb = new StringBuilder();

    /**
     * 获取内容
     */
    public String getContent(File file, String upFileName) {

        sb.setLength(0);
        String str;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), FileCodingUtil2.getCharsetName(file, 1024)));) {
            int num = 0;
            while ((str = reader.readLine()) != null) {
                if (str.trim().length() > 0) {
                    sb.append(str).append("<br/>");
                    num++;
                }
                if (num > 300)
                    break;
            }
            str = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        file = null;
        upFileName = null;
        return sb.toString();
    }
}
