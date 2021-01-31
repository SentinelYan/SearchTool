package com.yqs.search.index;

import com.yqs.search.base.BaseDocument;
import com.yqs.search.constants.FieldConstant;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.TextField;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.File;
import java.io.IOException;

public class IndexPdfFiles implements IndexFileInterface {

	public static PDFTextStripper stripper;

	static {
		try {
			stripper = new PDFTextStripper();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

    /**
     * 获取内容
     */
	public String getContent(File file, String s) {

		return getContent(file, 30);
	}

    /**
     * 获取内容
     */
	public String getContent(File file, int endNum) {

		// 结束提取页数
		String content = "";
		try {
            PDDocument document = PDDocument.load(file);
			stripper.setStartPage(1);
			stripper.setEndPage(endNum);
			content = stripper.getText(document);
            if (content.length() == 0 && endNum == 10) content = "抱歉,文件暂时无法预览";
            return content;
		} catch (java.io.EOFException e) {
			// System.out.println("无法解析2" + path);
		} catch (Exception e) {
			// System.out.println("无法解析" + path);
		} finally {
            file = null;
		    content = null;
		}
		return content;

	}

	@Override
	public void indexFile(File file, String upFileName, String suffix) {
		Document doc = getSimDoc(file, upFileName, suffix);
        TextField tf;
		// 开始提取页数
		// 结束提取页数
		String content = "";
		try {
			content = getContent(file, 20);
			//对内容做一个解析，将内容分词存储到索引库
            tf = new TextField(FieldConstant.FILE_CONTENT, content, Store.NO);
            doc.add(tf);
            BaseDocument.addDocument(doc);
		} catch (Exception e) {

			// System.out.println("无法解析" + path);
		} finally {

			tf = null;
			content = null;
			upFileName = null;
		}
	}

}
