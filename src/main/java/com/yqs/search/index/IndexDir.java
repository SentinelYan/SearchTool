package com.yqs.search.index;

import com.yqs.search.base.BaseDocument;
import com.yqs.search.constants.SystemConstants;
import org.apache.lucene.document.Document;

import java.io.File;

/**
 * 文件夹索引
 */
public class IndexDir implements IndexFileInterface {

	@Override
	public void indexFile(File file, String upFileName, String suffix) {
		Document doc = getSimDoc(file, upFileName, SystemConstants.QT2);
		BaseDocument.addDocument(doc);
		file = null;
		upFileName = null;
		suffix = null;
		doc = null;
	}

}
