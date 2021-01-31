package com.yqs.search.constants;


import com.yqs.search.suffixes.*;
import com.yqs.search.index.*;

import java.util.HashMap;
import java.util.Map;

public class ParseConstants {

    //TXT索引
    public static IndexTxtFiles itf = new IndexTxtFiles();
    //WORD索引
    public static IndexWordFiles iwf = new IndexWordFiles();
    //Excel索引
    public static IndexExcelFiles ief = new IndexExcelFiles();
    //PDF索引
    public static IndexPdfFiles ipf = new IndexPdfFiles();
    //PPT索引
    public static IndexPptFiles ippf = new IndexPptFiles();
    //文件名索引
    public static IndexFileName ifn = new IndexFileName();
    //文件夹索引
    public static IndexDir idn = new IndexDir();

    //存放正常带JAVA\JSP\CSS等的MAP
    public static Map<String, IndexFileInterface> PARSE_MAP = new HashMap<String, IndexFileInterface>();
    //存放 TXT 的MAP
    public static Map<String, IndexFileInterface> PARSE_MAP_SIMPLE = new HashMap<String, IndexFileInterface>();
    //存放带有 MP3\MP4 的MAP
    public static Map<String, IndexFileInterface> PARSE_MAP_OTHER = new HashMap<String, IndexFileInterface>();

    public static void init() {

    }

    static {
        /**
         * 将TXT_SET的后缀名遍历出来，存放在Parse_MAP中
         */
        TxtEnums.ORDINARY_TXT_LIST.forEach(t -> {
            //将对应的后缀名和索引对象存入MAP中
            PARSE_MAP.put(t, itf);
        });
        PptEnums.getValues().forEach(p -> {
            PARSE_MAP.put(p, ippf);
        });

        WordEnums.getValues().forEach(w -> {
            PARSE_MAP.put(w, iwf);
        });
        ExcelEnums.getValues().forEach(e -> {
            PARSE_MAP.put(e, ief);
        });
        PARSE_MAP.put(".PDF", ipf);

        OtherEnums.getValues().forEach(o -> {
            PARSE_MAP.put(o, ifn);
        });

        // --------------------------
        PptEnums.getValues().forEach(p -> {
            PARSE_MAP_SIMPLE.put(p, ippf);
        });
        WordEnums.getValues().forEach(w -> {
            PARSE_MAP_SIMPLE.put(w, iwf);
        });
        ExcelEnums.getValues().forEach(e -> {
            PARSE_MAP_SIMPLE.put(e, ief);
        });
        PARSE_MAP_SIMPLE.put(".PDF", ipf);

        PARSE_MAP_SIMPLE.put(".TXT", itf);

        //-----------------------
        OtherEnums.getValues().forEach(o -> {
            PARSE_MAP_OTHER.put(o, ifn);
        });
    }
}
