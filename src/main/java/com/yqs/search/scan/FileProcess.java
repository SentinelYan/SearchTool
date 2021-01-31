package com.yqs.search.scan;

import com.yqs.search.base.BaseDocument;
import com.yqs.search.constants.SystemConstants;
import com.yqs.search.constants.NoIndexConstants;
import com.yqs.search.constants.ParseConstants;
import com.yqs.search.index.IndexFileInterface;
import com.yqs.search.store.Stores;
import com.yqs.search.task.Task;
import com.yqs.search.App;
import javafx.application.Platform;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 扫描文件接口 1.可开始,可暂停
 */
public class FileProcess implements FileVisitor<Path> {

	// C盘只扫描用户文件夹
	ExecutorService pool;
	//每一百次提交
	private int bai = 100;
	//返回文件的时间
	private static FileTime ONE = FileTime.fromMillis(1);
	/***
	 * 
	 * @param pool 扫描或搜索什么类型的文件
	 */
	public FileProcess(ExecutorService pool) {
		this.pool = pool;
	}

	//文件数量
	int tempnum = 1;

	Task task = new Task(null, null, null);

	File fileGlobal;

	// 存储以 '&' '~' '_lucene' 开头的文件的后缀名
	Set<String> noIndexSuffixSet = new HashSet<>();

    /**
     * 目录处理前
     */
	@Override
	public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
		// TODO 这些路径跳过，不参与索引
		if (NoIndexConstants.WINDOWS.equals(dir)
                || NoIndexConstants.PerfLogs.equals(dir)
				|| NoIndexConstants.ProgramFiles.equals(dir)
                || NoIndexConstants.ProgramFiles86.equals(dir)
				|| NoIndexConstants.LOCAL.equals(dir)
                || NoIndexConstants.ProgramData.equals(dir)
				|| NoIndexConstants.M2.equals(dir)) {

			return FileVisitResult.SKIP_SUBTREE;
		}

		//TODO 当 精简模式 && 文件下拉框（文件夹）选项被选中时
		if (!App.rbtnB.isSelected() && App.cboxA.getValue().equals(SystemConstants.QT2)) {

		    //如果全新扫描被选中 或者 文件最后访问时间等于当前时间
			if (App.rbtnA.isSelected() || attrs.lastAccessTime().equals(ONE)) {

				IndexFileInterface iff = null;
				iff = ParseConstants.idn;
				fileGlobal = dir.toFile();

				//获取文件名，并转换为大写
				String fname = fileGlobal.getName().toUpperCase();

				//设置文件名
				task.setFileName(fname);
				task.setIff(iff);
				task.setFile(fileGlobal);
				try {
				    //等待线程10秒
					pool.submit(task).get(SystemConstants.TASK_TIME, TimeUnit.SECONDS);

				} catch (Exception e1) {
					e1.printStackTrace();
					System.out.println(fileGlobal + "在指定时间内没有索引完,任务放弃");
				}
				try {
				    //设置文件属性 路径、最后修改时间
					Files.setAttribute(dir, SystemConstants.LASTACCTIME, ONE);
					tempnum++;
					Platform.runLater(() -> {
					    //已经扫描到的文件数量
						App.prompt.setText(SystemConstants.YSM + tempnum);
					});
					if (tempnum / bai > 1) {
						bai += 100;
						BaseDocument.updateReaderAndSearcher();
					}
				} catch (IOException e) {
				}
				fname = null;
			}
		}

		dir = null;
		attrs = null;
		fileGlobal = null;
		return FileVisitResult.CONTINUE;
	}

    /**
     * 正在访问目录时
     */
	@Override
	public FileVisitResult visitFile(Path path, BasicFileAttributes attrs) throws IOException {

		if (SystemConstants.SYSTEM_STATUS == 4)
		    //终止访问
			return FileVisitResult.TERMINATE;

		// TODO 如果全新扫描被选中 或者 文件最后访问时间与当前时间不相等
		if (App.rbtnA.isSelected() || !attrs.lastAccessTime().equals(ONE)) {
            //系统状态为2时，扫描暂停
			while (SystemConstants.SYSTEM_STATUS == 2) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			//TODO 获取此路径File对象
			fileGlobal = path.toFile();

			//TODO 获取File名称
			String fileName = fileGlobal.getName().toUpperCase();
//            System.out.println(fname);

            //获取文件的 . 最后出现的坐标
			int dian = fileName.lastIndexOf(SystemConstants.DIAN);

			//如果此文件没有 .
			if (dian == -1){
			    //继续访问下一个
                return FileVisitResult.CONTINUE;
            }

			//TODO 获取文件的后缀名 根据.坐标
			String suffix = fileName.substring(dian);

			//如果 noIndexSuffixSet 里面有这个后缀
			if (noIndexSuffixSet.contains(suffix)) {

			    //则继续下一个
				return FileVisitResult.CONTINUE;
			}
			//TODO 获取文件的字节长度
			long size = fileGlobal.length();
			//如果字节为0
			if (size == 0)
			    //则下一个
				return FileVisitResult.CONTINUE;

            //如果 skipFilesMap 中包含这个文件的绝对路径
			if (Stores.skipFilesMap.contains(fileGlobal.getAbsolutePath())) {

			    //则继续下一个
				return FileVisitResult.CONTINUE;
			}

			//TODO 获取文件名首字母
			char tou = fileName.charAt(0);

            //不扫描的文件
            //查找指定字符或字符串在字符串中第一次出现地方的索引，未找到的情况返回 -1.
            //TODO 判断文件名首字母不等于 ‘& ’还有不等于 ‘~ ’ 还有不等于 '_lucene'
			if (tou != NoIndexConstants.DAO && tou != NoIndexConstants.PIE && fileName.indexOf(NoIndexConstants.LUCENE) == -1) {

				IndexFileInterface indexFileInterface = null;

				//TODO 获取文件类型下拉框的 文件类型，根据类型解析出文件对象
				switch (App.cboxA.getValue()) {
				    //全部类型
				case SystemConstants.QBLX:
				    //精简模式被选中
					if (App.rbtnB.isSelected())
						indexFileInterface = ParseConstants.PARSE_MAP_SIMPLE.get(suffix);
					else
					    //普通模式
						indexFileInterface = ParseConstants.PARSE_MAP.get(suffix);
					break;
					//其他文件夹
				case SystemConstants.QT:
					indexFileInterface = ParseConstants.PARSE_MAP_OTHER.get(suffix);
					break;
				default:
				    //文件类型下拉框
					if (suffix.equals(App.cboxA.getValue()))
						indexFileInterface = ParseConstants.PARSE_MAP.get(suffix);
					break;
				}

                //TODO 如果对应的文件对象不为空
				if (indexFileInterface != null) {

					//每100个文件更新一次Search对象
					if (tempnum / bai > 1) {
						bai += 100;
						BaseDocument.updateReaderAndSearcher();

					}
					//设置文件名
					task.setFileName(fileName);
					//设置索引接口
					task.setIff(indexFileInterface);
					//设置文件对象
					task.setFile(fileGlobal);
					//设置后缀
					task.setSuffix(suffix);

					try {
					    //TODO 执行写入任务，超时时间30秒。 TimeUnit.SECONDS（5）线程等待五秒
						pool.submit(task).get(SystemConstants.TASK_TIME, TimeUnit.SECONDS);

					} catch (Exception e1) {
						Stores.skipFilesMap.add(fileGlobal.getAbsolutePath());
						System.out.println(fileGlobal + "在指定时间内没有索引完,任务放弃");
					}
					try {
					    //设置文件的最后访问时间
						Files.setAttribute(path, SystemConstants.LASTACCTIME, ONE);
						tempnum++;
						/* 把结果显示到UI */
						Platform.runLater(() -> {
							App.prompt.setText(SystemConstants.YSM + tempnum);
						});
					} catch (IOException e) {
						System.out.println("同步失败:" + path);
					}
				//TODO 如果文件名首字母是以 '&' '~' ‘_lucene’开头，则将后缀添加到noIndexSet中
				} else {
					noIndexSuffixSet.add(suffix);
					return FileVisitResult.CONTINUE;
				}
				fileName = null;
				path = null;
				attrs = null;
				fileGlobal = null;
				suffix = null;
			}
		}

		return FileVisitResult.CONTINUE;
	}

    /**
     * 访问文件失败时
     */
	@Override
	public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {

		file = null;
		exc = null;
		//继续访问一下一个文件
		return FileVisitResult.CONTINUE;
	}

    /**
     * 目录处理后
     */
	@Override
	public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {

		dir = null;
		exc = null;
		//继续访问一下一个文件
		return FileVisitResult.CONTINUE;
	}

}
