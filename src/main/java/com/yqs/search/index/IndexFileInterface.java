package com.yqs.search.index;

import com.yqs.search.constants.FieldConstant;
import com.yqs.search.suffixes.PptEnums;
import com.yqs.search.scan.ScanFileInfo;
import com.yqs.search.store.Stores;
import org.apache.lucene.document.*;
import org.apache.lucene.document.Field.Store;
import org.apache.poi.extractor.POITextExtractor;
import org.apache.poi.ooxml.extractor.ExtractorFactory;
import org.apache.poi.sl.extractor.SlideShowExtractor;
import org.apache.poi.xslf.usermodel.XMLSlideShow;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public interface IndexFileInterface {
    // 创建域对象,并且放入文档对象中
    static Document doc = new Document();

    //这个Field用来构建一个字符串Field，但是不会进行分词，会将整个串存储在索引中，比如（订单号，身份证号等）是否存储在文档中用Store.YES或Store.No决定
    //路径
    static StringField PATH_FIELD = new StringField(FieldConstant.FILE_PATH, "", Store.YES);
    //盘符
    static StringField DISK_FIELD = new StringField(FieldConstant.FILE_PANFU, "", Store.NO);
    //后缀
    static StringField SUFFIX_FIELD = new StringField(FieldConstant.FILE_SUFFIX, "", Store.NO);
    //文件名
    static TextField FILENAME_FIELD = new TextField(FieldConstant.FILE_NAME, "", Store.YES);


    //配合其他域排序使用  文件最后修改时间
    static NumericDocValuesField nvf = new NumericDocValuesField(FieldConstant.FILE_UPDATE_TIME, 0l);

    //这个field用来构建不同类型Field不分析，不索引，但要Field存储在文档中
    static StoredField sdf = new StoredField(FieldConstant.FILE_UPDATE_TIME, 0l);

    //这个Field用来构建一个Integer数字型Field，进行分词和索引，不存储。
    static LongPoint lp = new LongPoint(FieldConstant.FILE_UPDATE_TIME, 0l);

    /**
     *
     */
    public default Document getSimDoc(File file, String fileName, String suffix) {
        doc.clear();
        // 文件路径
        PATH_FIELD.setStringValue(file.getAbsolutePath());
        //将文件路径存储在文档中
        doc.add(PATH_FIELD);
        // 如果ScanFileInf盘符名称不等于空
        if (ScanFileInfo.ROOT_NAME != null)
            //设置盘符名称
            DISK_FIELD.setStringValue(ScanFileInfo.ROOT_NAME);
        else
            //否则将文件的绝对路径的第一个字符作为盘符存储
            DISK_FIELD.setStringValue(file.getAbsolutePath().charAt(0) + "");

        //
        doc.add(DISK_FIELD);

        // 文件后缀
        SUFFIX_FIELD.setStringValue(suffix);

        //TODO doc添加后缀字段
        doc.add(SUFFIX_FIELD);
        // 文件名
        fileName = fileName.replaceAll("\\s+", "");

        FILENAME_FIELD.setStringValue(fileName);
        //
        doc.add(FILENAME_FIELD);

        //表示的文件最后一次被修改的时间。
        long ft = file.lastModified();

        nvf.setLongValue(ft);
        sdf.setLongValue(ft);
        lp.setLongValue(ft);

        doc.add(nvf);
        doc.add(sdf);
        doc.add(lp);

        fileName = null;
        suffix = null;
        file = null;

        return doc;
    }

    /**
     * 文件内容
     */
    public default String getContent(File file, String upFileName) {
        String content = "抱歉,文件暂时无法预览";
        POITextExtractor extractor = null;
        try {
            InputStream inputStream = new FileInputStream(file);
            //如果后缀是PPT，则用SlideExtractor提取文件内容
            if (upFileName.endsWith(PptEnums.PPTX)) {
                content = readPptx(file);
            } else {
                extractor = ExtractorFactory.createExtractor(inputStream);
                //获取正文
                content = extractor.getText();
            }
            return content;
        } catch (Exception e) {
            Stores.skipFilesMap.add(file.getAbsolutePath());
            System.out.println("inf无法解析:" + file);
            System.out.println("长度为：" + Stores.skipFilesMap.size());
        } finally {
            if (extractor != null)
                try {
                    extractor.close();
                } catch (IOException e) {
                    extractor = null;
                }
            content = null;
            file = null;
            extractor = null;
            upFileName = null;
        }
        return content;
    }

    /**
     * 获取PPT内容
     */
    default String  readPptx(File file) {

        String text2007 = "抱歉，文件暂时无法预览";
        try {
            FileInputStream fis = new FileInputStream(file);
            SlideShowExtractor showExtractor = new SlideShowExtractor(new XMLSlideShow(fis));
            text2007 = showExtractor.getText();
        } catch (Exception e) {
        }

        return text2007;
    }

    public void indexFile(File file, String upFileName, String suffix);

}
