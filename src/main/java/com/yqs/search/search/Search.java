package com.yqs.search.search;

import com.yqs.search.base.BaseDocument;
import com.yqs.search.constants.FieldConstant;
import com.yqs.search.constants.SystemConstants;
import com.yqs.search.entity.Paging;
import com.yqs.search.util.TimeUtil;
import com.yqs.search.App;
import javafx.application.Platform;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.LongPoint;
import org.apache.lucene.index.Term;
import org.apache.lucene.queries.function.FunctionScoreQuery;
import org.apache.lucene.search.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Search {

    //搜索对象
    public static SearchBean searchBean;
    //查询索引名称内容
	private static String[] QUERIES_INDEX_NAMES = { FieldConstant.FILE_NAME, FieldConstant.FILE_CONTENT };
    //全部类型 简洁模式查询
	private static List<Query> QBLX_SIMPLE_SUFFIX = new ArrayList<Query>();
	//全部类型 普通模式查询 List
	private static List<Query> QBLX_ORDINARY_SUFFIX = new ArrayList<Query>();

	//其他文件
	private static TermQuery QT_FILE = new TermQuery(new Term(FieldConstant.FILE_SUFFIX, SystemConstants.QT));

	//其他文件夹
	private static TermQuery QT_DIR = new TermQuery(new Term(FieldConstant.FILE_SUFFIX, SystemConstants.QT2));

	static {
        //简洁模式后缀
		SystemConstants.simpleSuffixList.forEach(s -> {
		    //简单模式文件后缀字段
			QBLX_SIMPLE_SUFFIX.add(new TermQuery(new Term(FieldConstant.FILE_SUFFIX, s)));
		});
        //普通模式后缀
		SystemConstants.suffixList.forEach(s -> {
		    //文件后缀字段
			QBLX_ORDINARY_SUFFIX.add(new TermQuery(new Term(FieldConstant.FILE_SUFFIX, s)));
		});
	}
	//精简模式选中
	private static BooleanQuery.Builder SELECT_QUERY_BUILD = new BooleanQuery.Builder();
	//精简模式未被选中
	private static BooleanQuery.Builder ORDINARY_QUERY_BUILD = new BooleanQuery.Builder();

	static {
	    //TODO 简洁模式查询List
		QBLX_SIMPLE_SUFFIX.forEach(q -> {
		    //BooleanClause：lucene中BooleanQuery 实现与或的复合搜索 .
            //SHOULD：S表示“或”关系，最终检索结果为所有检索子句的并集。表示or.
            //BooleanClause.Occur.MUST表示and
            //BooleanClause.Occur.MUST_NOT表示not

            //遍历出每一条查询对象 FILE_SUFFIX ：后缀
			SELECT_QUERY_BUILD.add(q, BooleanClause.Occur.SHOULD);
		});
        //TODO 普通模式查询 List
		QBLX_ORDINARY_SUFFIX.forEach(q -> {

		    //遍历出每一条查询对象 FILE_SUFFIX ：后缀
			ORDINARY_QUERY_BUILD.add(q, BooleanClause.Occur.SHOULD);
		});

		//TODO 添加 其他文件 查询对象：FILE_SUFFIX：后缀
		ORDINARY_QUERY_BUILD.add(QT_FILE, BooleanClause.Occur.SHOULD);
	}

	//精简模式被选中 BooleanQuery: 组合检索
	private static BooleanQuery SIMPLE_QUERY = SELECT_QUERY_BUILD.build();
	//精简模式未被选中
	private static BooleanQuery ORDINARY_QUERY = ORDINARY_QUERY_BUILD.build();

	/**
	 * 生成一个查询query s
	 * 
	 * @param body
	 * @return
	 */
	public static Query buildQuery(SearchBody body) {
        //组合查询
		BooleanQuery.Builder booleanQuery = new BooleanQuery.Builder();
		//TODO 关键词
		for (String key : body.getSearchKeys()) {
            //组合查询
			BooleanQuery.Builder bQuery2 = new BooleanQuery.Builder();
			//
			for (int i = 0; i < QUERIES_INDEX_NAMES.length; i++) {
				Query query2 = new PhraseQuery(0, QUERIES_INDEX_NAMES[i], key.split(""));
				bQuery2.add(query2, BooleanClause.Occur.SHOULD);
			}
			booleanQuery.add(bQuery2.build(), BooleanClause.Occur.FILTER);
		}
		//TODO 过滤词不为空
		if (body.getNoSearchKeys() != null)
			// 不扫描列表
			for (String key : body.getNoSearchKeys()) {
                for (int y = 0; y < QUERIES_INDEX_NAMES.length; y++) {
                    // BoostQuery q3 = new BoostQuery(q1,100f);
                    Query query2;
                    if (y == 0) {
                        query2 = new PhraseQuery(0, QUERIES_INDEX_NAMES[y], key.split(""));
                        query2 = new BoostQuery(query2, 999999);

                    } else
                        query2 = new PhraseQuery(0, QUERIES_INDEX_NAMES[y], key.split(""));

                    booleanQuery.add(query2, BooleanClause.Occur.MUST_NOT);
                }
			}
		//盘符下拉框
		String va = App.cboxB.getValue();
		// 是否盘符搜索
		if (!SystemConstants.SYPF.equals(va)) {
			TermQuery tq = new TermQuery(new Term(FieldConstant.FILE_PANFU, va));
			booleanQuery.add(tq, BooleanClause.Occur.FILTER);
			tq = null;
		}
		//TODO 文件类型下拉框
		va = App.cboxA.getValue();
		switch (va) {
		    //todo 如果是 全部类型
		case SystemConstants.QBLX:
		    //如果精简模式被选中
			if (App.rbtnB.isSelected()) {
				booleanQuery.add(SIMPLE_QUERY, BooleanClause.Occur.FILTER);
			} else {
			    //如果精简模式未被选中
				booleanQuery.add(ORDINARY_QUERY, BooleanClause.Occur.FILTER);
			}
			break;
			//todo  如果是 其他文件
		case SystemConstants.QT:
			booleanQuery.add(QT_FILE, BooleanClause.Occur.FILTER);
			break;
			//如果是 文件夹
		case SystemConstants.QT2:
			booleanQuery.add(QT_DIR, BooleanClause.Occur.FILTER);
			break;

		default:
			TermQuery tq = new TermQuery(new Term(FieldConstant.FILE_SUFFIX, va));
			booleanQuery.add(tq, BooleanClause.Occur.FILTER);
			break;
		}
		//TODO 如果 开始时间 不等于空  结束时间也不为空
		if (body.getStartDate() != null && body.getEndDate() != null) {
		    //对时间范围内的参数进行查询
			Query querys = LongPoint.newRangeQuery(FieldConstant.FILE_UPDATE_TIME, body.getStartDate().getTime(), body.getEndDate().getTime());
			booleanQuery.add(querys, BooleanClause.Occur.FILTER);
		}
        //TODO 判断是否选择是文件名排序
		if (App.cboxC.getValue().equals(SystemConstants.WJMPX))
            return booleanQuery.build();
		else
			return booleanQuery.build();
	}

	//按照文件修改时间字段排序
	private static SortField sortField = new SortField(FieldConstant.FILE_UPDATE_TIME, SortField.Type.LONG, true);

	//排序对象
	private static Sort sort = new Sort(sortField);

	//缓存一条document 记录的 ID
	public static Map<Integer, Document> DOC_CACHE = new HashMap<>();

    /**
     * 查询索引库
     */
	public static Paging search(Query query, ScoreDoc after) {

		boolean byName = false;
		if (query instanceof FunctionScoreQuery){
            byName = true;
        }
		Paging page = new Paging();
		//获取每页条数
		int pageSize = page.getPageSize();
        //通过最后一个元素搜索下页的pageSize个元素。
		TopDocs docs = null;
		//获取Search对象
		IndexSearcher searcher = BaseDocument.getIndexSearcher();
		int len = 0;
		try {
			if (after != null) {
//				if (byName){
				    //获取上一页的最后一个元素和pageSize，再从最后一个元素的后一个开始取pageSize条数据
                    docs = searcher.searchAfter(after, query, pageSize);
//                } else{
//                    docs = searcher.searchAfter(after, query, pageSize, sort);
//                }
				//一共查询到的条数
				len = docs.scoreDocs.length;
			} else {
			    // 如果是根据名称，就默认排序
//				if (byName)
					docs = searcher.search(query, SystemConstants.SEARCH_NUM);
//				else
				    //文件更新时间进行排序
//					docs = searcher.search(query, SystemConstants.SEARCH_NUM, sort);

				//一共查询到的条数
				len = docs.scoreDocs.length;

				//TODO 设置总条数
				page.setTotalResultNum(len);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		//如果总条数 > 每页条数
		int tempSize = (len > pageSize ? pageSize : len);
		Document doc;
		//存储document对象的ID
		ScoreDoc sdoc;
		if (tempSize > 0) {
			for (int i = 0; i < tempSize; i++) {
				sdoc = docs.scoreDocs[i];
				try {
				    //scoreDoc.doc属性就是document对象的id ，根据ID获取document对象
					doc = DOC_CACHE.get(sdoc.doc);
					if (doc == null) {
					    //根据ID取出对应Document
						doc = searcher.doc(sdoc.doc);
						//缓存进map中
						DOC_CACHE.put(sdoc.doc, doc);
					}
					//TODO 添加doc对象
					page.getDocList().add(doc);

				} catch (Exception e) {
					// BaseDocument.updateReaderAndSearcher();
				}
			}
			//TODO 设置document对象的ID
			page.setAfterScoreDoc(docs.scoreDocs[tempSize - 1]);
		}
		docs = null;
		doc = null;
		sdoc = null;
		return page;
	}


    public static void getSearchResult() {

        Platform.runLater(() -> {

            if (App.prompt.getText().indexOf("关") != -1)
                App.prompt.setText("");
            if (SystemConstants.GO == 1) {
                App.prompt.setText("工具初始化中,请稍等...");
            } else {
                if (SystemConstants.SYSTEM_STATUS == 4)
                    SystemConstants.SYSTEM_STATUS = 0;

                //获取搜索对象
                SearchBody body = searchBean.getBody();
//                WindowsB windowsB;
                //
                if (SystemConstants.SYSTEM_STATUS == 0 && body != null) {
                    if ("".equals(body.getSearchKey())) {
                        App.prompt.setText("关键词不能为空");
                        return;
                    }
                } else {
                    //TODO 获取用户的关键词内容、盘符、起始日期、结束日期等
                    body = Search.getSearchBody();
                    //TODO 判断用户输入关键词是否为空
                    if ("".equals(body.getSearchKey())) {
                        App. prompt.setText("关键词不能为空");
                    } else {
                        //TODO 将 用户的关键词内容、盘符、起始日期、结束日期等 设置到searchBean中，初始化searchBean
                        searchBean.setBody(body);
                    }
                }
            }
        });
    }

    /**
     * 获取 用户的关键词内容、盘符、起始日期、结束日期等
     */
    public static SearchBody getSearchBody() {

        SearchBody body = new SearchBody();

        //搜索框内容获取，并使用在正则过滤掉空格换行等
        //关键词搜索框
        String searchKey = App.txtfieldA.getText().replaceAll("\\s+", " ").trim().toLowerCase();
        //按照空格分隔字符串
        String[] searchKeys = searchKey.split(" ");
        body.setSearchKey(searchKey);
        body.setSearchKeys(searchKeys);

        //获取过滤框内容，并使用正则过滤空格换行等
        //过滤词搜索框
        String nosearchKey = App.txtfieldB.getText().replaceAll("\\s+", " ").trim().toLowerCase();
        if (nosearchKey.length() > 0) {
            String[] nosearchKeys = nosearchKey.replaceAll("\\s+", " ").split(" ");
            body.setNoSearchKeys(nosearchKeys);
            body.setNoSearchKey(nosearchKey);
        }
        StringBuilder sbb = new StringBuilder()
                //搜索内容
                .append(searchKey)
                //搜索过滤词
                .append(body.getNoSearchKey())
                //文件类型
                .append(App.cboxA.getValue())
                //盘符
                .append(App.cboxB.getValue()).append(App.cboxC.getValue()).append(App.rbtnA.isSelected()).append(App.rbtnB.isSelected())
                //开始时间
                .append(App.startDate.getValue())
                //结束时间
                .append(App.endDate.getValue());

        System.out.println("sbb:" + sbb.toString());
        //搜索的所有选择设置进SearchBody中
        body.setCachePre(sbb.toString());

        if (App.startDate.getValue() != null && App.endDate.getValue() != null) {
            try {
                //格式化时间，并设置进body中
                body.setStartDate(TimeUtil.dateFormat.parse(App.startDate.getValue().toString()));
                body.setEndDate(TimeUtil.dateFormat.parse(App.endDate.getValue().toString()));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        sbb = null;
        return body;
    }

    /**
     * 清理旧数据
     */
	public static void delOld() {

		System.out.println("开始清理");
		SearchBody body = new SearchBody();
		body.setSearchKeys(new String[] { "." });
		Query query = buildQuery(body);
		try {
			IndexSearcher searcher = BaseDocument.getIndexSearcher();
			ScoreDoc doc;
			Document docc;
			//TopDocs 指向相匹配的搜索条件的前N个搜索结果。它是指针的简单容器指向它们的搜索结果输出的文档。
			TopDocs docs = searcher.search(query, SystemConstants.SEARCH_NUM, sort);
			Path path;
			String pathStr;
			for (int i = 0; i < docs.scoreDocs.length; i++) {
				if (SystemConstants.SYSTEM_STATUS == 4){
                    break;
                }
				doc = docs.scoreDocs[i];
				docc = searcher.doc(doc.doc);
				pathStr = docc.get(FieldConstant.FILE_PATH);
				path = Paths.get(pathStr);
				if (!Files.exists(path)) {
					Term term = new Term(FieldConstant.FILE_PATH, pathStr);
					BaseDocument.writer.deleteDocuments(term);
					term = null;
				}
				docc.clear();
				docc = null;
			}
			docs = null;
			pathStr = null;
			doc = null;
			query = null;
			body = null;
			System.out.println("清理完毕");

		} catch (IOException e) {

			e.printStackTrace();
		}
	}
}
