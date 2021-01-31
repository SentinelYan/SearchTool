package com.yqs.search.index;

import com.yqs.search.base.BaseDocument;
import com.yqs.search.constants.FieldConstant;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.TextField;

import java.io.File;

public class  IndexWordFiles implements IndexFileInterface {

	@Override
	public void indexFile(File file, String upFileName, String suffix) {
		Document doc = null;
		String content = "";

		doc = getSimDoc(file, upFileName, suffix);

		if (doc != null) {
			content = getContent(file, upFileName);
			TextField tf = new TextField(FieldConstant.FILE_CONTENT, content, Store.NO);
			doc.add(tf);
			tf = null;
		}
		BaseDocument.addDocument(doc);

		upFileName = null;
		content = null;
		file = null;
		doc = null;

	}
}
