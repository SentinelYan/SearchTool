package com.yqs.search.index;

import com.yqs.search.base.BaseDocument;
import com.yqs.search.constants.FieldConstant;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.TextField;

import java.io.File;


public class IndexPptFiles implements IndexFileInterface {

	@Override
	public void indexFile(File file, String upFileName, String suffix) {
		Document doc = null;
		String content = "";

		doc = getSimDoc(file, upFileName, suffix);

		try {
			content = getContent(file, upFileName);
			//content = content.replaceAll("\\s+|\t|\n", "");
		} catch (Exception e) {
			System.out.println("无法解析" + file);
		} finally {
			TextField tf = new TextField(FieldConstant.FILE_CONTENT, content, Store.NO);
			doc.add(tf);
			BaseDocument.addDocument(doc);
			tf = null;

			content = null;
			upFileName = null;
			file = null;
		}
	}

}
