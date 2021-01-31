package com.yqs.search.index;

import com.yqs.search.base.BaseDocument;
import com.yqs.search.constants.SystemConstants;
import org.apache.lucene.document.Document;

import java.io.File;

public class IndexFileName implements IndexFileInterface {

	@Override
	public void indexFile(File file, String upFileName, String suffix) {
		Document doc = getSimDoc(file, upFileName, SystemConstants.QT);
		BaseDocument.addDocument(doc);
		file = null;
		doc = null;
		upFileName = null;
		suffix = null;

	}

}
