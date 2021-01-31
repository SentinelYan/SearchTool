package com.yqs.search.index;

import com.alibaba.excel.ExcelReader;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.yqs.search.base.BaseDocument;
import com.yqs.search.constants.FieldConstant;
import com.yqs.search.suffixes.ExcelEnums;
import com.yqs.search.store.Stores;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.TextField;
import org.apache.lucene.document.Field.Store;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IndexExcelFiles implements IndexFileInterface {

	private static Map<String, ExcelTypeEnum> suffixMap = new HashMap<String, ExcelTypeEnum>();

	static {
        suffixMap.put(".XLS", ExcelTypeEnum.XLS);
        suffixMap.put(".ET", ExcelTypeEnum.XLS);
        suffixMap.put(".XLSX", ExcelTypeEnum.XLSX);
	}

	@Override
	public void indexFile(File file, String upFileName, String suffix) {
		ExcelTypeEnum en = null;
		Document doc = getSimDoc(file, upFileName, suffix);

		//根据文件后缀获取文件类型
		en = suffixMap.get(suffix);
		//
		upFileName = getContent(file, en);
		//去除空白符
		upFileName = upFileName.replaceAll("\\s+|\t|\n", "");

		TextField tx = new TextField(FieldConstant.FILE_CONTENT, upFileName, Store.NO);

		doc.add(tx);

		BaseDocument.addDocument(doc);


		tx = null;
		upFileName = null;
		en = null;
		doc = null;
		suffix = null;
		file = null;
	}

    /**
     * 获取文件内容
     */
	public String getContent(File file) {

		ExcelTypeEnum en = null;
        //获取文件绝对路径并转换成大写
		String ps = file.getAbsolutePath().toUpperCase();
		//判断文件后缀是否是以XLS结束
		if (ps.endsWith(ExcelEnums.XLS)) {
			en = ExcelTypeEnum.XLS;
		} else if (ps.endsWith(ExcelEnums.XLSX)) {
			en = ExcelTypeEnum.XLSX;
		} else if (ps.endsWith(ExcelEnums.ET)) {
			en = ExcelTypeEnum.XLS;
		}
		return getContent(file, en);
	}

    /**
     * 获取文件内容
     */
	public String getContent(File file, ExcelTypeEnum en) {
        //如果文件类型为空
		if (en == null) {
		    //获取文件路径并转换成大写
			String ps = file.getAbsolutePath().toUpperCase();
			if (ps.endsWith(ExcelEnums.XLS)) {
				en = ExcelTypeEnum.XLS;
			} else if (ps.endsWith(ExcelEnums.XLSX)) {
				en = ExcelTypeEnum.XLSX;
			} else if (ps.endsWith(ExcelEnums.ET)) {
				en = ExcelTypeEnum.XLS;
			}
		}
		StringBuilder sb = new StringBuilder();
		try {
            InputStream inputStream = new FileInputStream(file);
			@SuppressWarnings("deprecation")
			ExcelReader reader = new ExcelReader(inputStream, en, null, new AnalysisEventListener<List<String>>() {

			    //通过 AnalysisContext 对象还可以获取当前 sheet，当前行等数据
				@Override
				public void invoke(List<String> object, AnalysisContext context) {
                    //如果当前行的数据长度大于0
					if (object.size() > 0) {
						for (String s : object) {
						    //trim方法用于删除字符串的头尾空白符。
                            //数据不等于空，且 去除数据前后空白符的长度 大于0
							if (s != null && s.trim().length() > 0)
								sb.append(s).append("\t");
						}
						//添加换行符
						sb.append("\n");
					}
					object = null;
					context = null;
				}
                //解析结束销毁不用的资源
				@Override
				public void doAfterAllAnalysed(AnalysisContext context) {
					context = null;
				}
			});

			reader.read();

		} catch (Exception e) {

            Stores.skipFilesMap.add(file.getAbsolutePath());
			System.out.println("无法解析" + file);
		}
		return sb + "";
	}

}
