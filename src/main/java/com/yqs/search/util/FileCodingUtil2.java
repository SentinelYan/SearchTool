package com.yqs.search.util;

import info.monitorenter.cpdetector.io.*;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

public class FileCodingUtil2 {

    /***
     * 这是获取文本编码方式的代码
     */
    private static CodepageDetectorProxy detector;

    static {
        detector = CodepageDetectorProxy.getInstance();
        detector.add(new ParsingDetector(false));
        detector.add(UnicodeDetector.getInstance());
        detector.add(JChardetFacade.getInstance());//
        // 需要第三方JAR包:antlr.jar、chardet.jar.
        detector.add(ASCIIDetector.getInstance());
    }
    //字符集
    static Map<String, String> map = new HashMap<>();

    static {
        map.put("Unicode", "Unicode");
        map.put("GBK", "GBK");
        map.put("US-ASCII", "US-ASCII");
        map.put("windows1252", "GBK");
        map.put("UTF-16LE", "GBK");
        map.put("UTF-16BE", "GBK");
        map.put("UTF-8", "UTF-8");
        map.put("UTF-16", "UTF-16");
        map.put("ISO-8859-1", "ISO-8859-1");
        map.put("GB18030", "GB18030");
    }

    //获取字符集名称
    public static String getCharsetName(File path, int byteNum) throws IOException {
        String charsetSstr = null;
        Charset charset = null;
        try {
            //获取文件
            FileInputStream fis = new FileInputStream(path);
            //读取文件
            BufferedInputStream bis = new BufferedInputStream(fis);
            //检测文件编码
            charset = detector.detectCodepage(bis, 1024);
            //获取字符集名称
            charsetSstr = charset.name();
            //如果在字符集的map中找到了
            if (map.get(charsetSstr) != null)
                //返回此编码
                return map.get(charsetSstr);
        } catch (Exception e) {
            System.out.println(path + "拒绝访问");
        } finally {
            charset = null;
            charsetSstr = null;
            path = null;

        }
        return "GBK";
    }
}
