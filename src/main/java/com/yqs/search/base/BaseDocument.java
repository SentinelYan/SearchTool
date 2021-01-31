package com.yqs.search.base;

import com.yqs.search.constants.FieldConstant;
import com.yqs.search.constants.SystemConstants;
import com.yqs.search.scan.ScanFileInfo;
import com.yqs.search.App;
import com.yqs.search.util.MyAlert;
import javafx.application.Platform;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.*;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.LockObtainFailedException;

import java.io.IOException;
import java.nio.file.Paths;

public class BaseDocument {

	private static String dirName = "lucenedirs2";
	//打开索引目录
	private static FSDirectory dir;
	//写入索引
	public static IndexWriter writer;
	//写入索引配置
	private static IndexWriterConfig writerConfig;
	//这个read主要是为了全盘索引用的
	public static IndexReader reader;
	//这个是自定义过滤表，没有过滤任何词汇，保证完全可搜索

    //3 创建分词器对象
	private static Analyzer analyzer = new StandardAnalyzer();
	//搜索对象
	public static IndexSearcher searcher;

	public static void init(){}

	/**
	 * 初始化
	 */
	static {
		new Thread(() -> {
			try {
			    //指定索引在磁盘中的位置
				dir = FSDirectory.open(Paths.get(System.getProperty("user.dir") + "\\CONF\\" + dirName));

                //4 索引写出工具的配置对象
				writerConfig = new IndexWriterConfig(analyzer);
				LogMergePolicy mergePolicy = new LogByteSizeMergePolicy();
                //当IndexWriter添加的文档的大小超过RAMBufferSizeMB ，IndexWriter就会把在内存中的操作，写入到硬盘中.
                //IndexWriter在执行AddDocuments(写入文档),DeleteDocuments（删除文档），UpdateDocuments(更新文档)，
                // 这些操作的时候，这些操作都会先缓冲到内存中，也就是说执行完这些函数，
                // 其实储存的索引目录下是没有任何改变的，当AddDocuments的容量超过上述的属性的时候，
                // 这些操作才会具体执行到储存索引的硬盘当中
				writerConfig.setRAMBufferSizeMB(1);
				mergePolicy.setMergeFactor(2);
				//设置索引合并的策略
				writerConfig.setMergePolicy(mergePolicy);
				writerConfig.setRAMPerThreadHardLimitMB(1);
				//设置创建或者添加模式
				writerConfig.setOpenMode(OpenMode.CREATE_OR_APPEND);
				// writerConfig.setUseCompoundFile(false);
				try {
                    //5 创建索引的写出工具类。参数：索引的目录和配置信息
					writer = new IndexWriter(dir, writerConfig);

				} catch (Exception e) {
					App.close();
					if (e instanceof LockObtainFailedException)
						MyAlert.alertError("您当前正在运行着一个工具,请勿重复启动!!!(3秒后自动关闭)");
					else
						MyAlert.alertError("启动失败!!!");
					try {
						Thread.sleep(3000);
						System.exit(0);
					} catch (Exception e1) {

					}

				}
				// 第一次索引后如果索引文件巨大会卡顿，用线程
				try {
				    //当修改、删除或插入数据的时候，如果短时间内没有关闭IndexWriter，可以使用commit来提交当前的更新，这样的话indexReader马上可以察觉到索引被更新。
					writer.commit();
					App.scanFileInfo = new ScanFileInfo();
					reader = DirectoryReader.open(writer);
					searcher = new IndexSearcher(reader);
					SystemConstants.GO = 0;
					Platform.runLater(() -> {
					    //工具初始化按钮
						App.prompt.setText("");
					});
					//如果自动模式被选中
					if (App.rbtnC.isSelected()) {
                        App.scanFileInfo.autoScan();
					}
//					App.preLoad();

				} catch (IOException e) {
					e.printStackTrace();
				}

			} catch (IOException e) {
				e.printStackTrace();
			}
		}).start();

	}

    /**
     * 获得Search对象
     */
	public static IndexSearcher getIndexSearcher() {
		if (searcher == null)
			return new IndexSearcher(reader);
		return searcher;
	}

    /**
     * 获取IndexReader对象
     */
	public static IndexReader getRtIndexReader() {

		try {
			reader = DirectoryReader.open(writer);
			return reader;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return reader;
	}

    /**
     * 更新IndexReader对象
     */
	public synchronized static void updateReaderAndSearcher() {

		new Thread(() -> {
			if (writer.isOpen()) {
			    //获取IndexReader对象
				getRtIndexReader();
				searcher = new IndexSearcher(reader);
			}
		}).start();

	}

	/***
	 * 这个操作其实就是更新操作
	 * 
	 * @param doc
	 */
	public static void addDocument(Document doc) {

		try {
			if (writer.isOpen()) {
				Term term = new Term(FieldConstant.FILE_PATH, doc.get(FieldConstant.FILE_PATH));
				writer.updateDocument(term, doc);
				term = null;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void commit() {

		new Thread(() -> {
			try {
				if (writer != null && writer.isOpen())
					writer.commit();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}).start();
	}

	public static void closeAll() {

		if (reader != null)
			try {
				reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		if (writer != null && writer.isOpen())
			try {
				writer.flush();
				writer.commit();
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
	}

	public static void updates() {

		BaseDocument.updateReaderAndSearcher();
		BaseDocument.commit();
		System.gc();
	}
}
