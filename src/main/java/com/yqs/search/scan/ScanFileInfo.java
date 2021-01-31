package com.yqs.search.scan;

import com.yqs.search.base.BaseDocument;
import com.yqs.search.constants.SystemConstants;
import com.yqs.search.search.Search;
import com.yqs.search.store.Stores;
import com.yqs.search.util.FilesUtil;
import com.yqs.search.util.TimeUtil;
import com.yqs.search.App;
import com.yqs.search.util.MyAlert;
import javafx.application.Platform;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ScanFileInfo {

    //盘符名称
	public static String ROOT_NAME;

    /**
     *  扫描文件
     * @return
     */
	public long scan() {
	    //线程池
		ExecutorService pool = Executors.newFixedThreadPool(1);
		FileProcess sf = new FileProcess(pool);
        SystemConstants.SYSTEM_STATUS = 1;
		//启动时间
		long startTime = new Date().getTime();

		//遍历系统盘符数组
		for (File f : Stores.DIRROOTS) {
			try {
			    //截取到根目录名称
				ROOT_NAME = f.toString().replaceAll(":\\\\", "");

				//遍历目录文件
				Files.walkFileTree(f.toPath(), sf);

			} catch (IOException e) {
				e.printStackTrace();
			}
			System.gc();
		}
		long cha = new Date().getTime() - startTime;
		try {
			pool.shutdown();
			//提交超时
			pool.awaitTermination(1, TimeUnit.DAYS);
		} catch (InterruptedException e) {
			e.printStackTrace();
			pool.shutdownNow();
			pool = null;
		}
		pool = null;
		FilesUtil.writerSkip();
	
		sf = null;
		return cha;
	}

    public static void autoScan() {
        if (BaseDocument.writer == null) {
            MyAlert.alert("工具正在初始化中,请稍后再试!");
            return;
        }
        if (SystemConstants.SYSTEM_STATUS == 0 || SystemConstants.SYSTEM_STATUS == 4) {
            App.prompt.setText("");
            SystemConstants.SYSTEM_STATUS = 1;
            Platform.runLater(() -> {
                App. btnC.setText("扫描中");
                App. pinA.setVisible(true);
            });

            new Thread(() -> {
                //遍历目录树,扫描文件
                long cha = App.scanFileInfo.scan();
                SystemConstants.SYSTEM_STATUS = 0;
                //扫描耗时
                String chaStr = TimeUtil.formatTime(cha);

                Platform.runLater(() -> {
                    App. btnC.setText("扫描文件");
                    App. pinA.setVisible(false);
                    if (App.prompt.getText().length() > 0) {
                        if (App.rbtnA.isSelected()) {
                            App. prompt.setText("全盘扫描完毕," + App.prompt.getText() + ",耗时" + chaStr);
                        } else
                            App. prompt.setText("全盘扫描完毕," + App. prompt.getText().replaceAll("已扫描的文件数量", "更新的文件数量") + ",耗时" + chaStr);
                    } else {
                        App. prompt.setText("全盘扫描完毕,耗时" + chaStr);
                    }

                    Search.searchBean.setBody(null);
//                    Stores.windowsBMap.clear();
                    //全新扫描被选中 或者 自动模式被选中
                    if (App.rbtnA.isSelected() || App.rbtnC.isSelected()) {
                        // 此操作用后台线程处理,可减少最后显示时的卡顿
                        new Thread(() -> {
                            Search.delOld();
                            BaseDocument.updates();
                            //全新扫描不被选中
                            App.rbtnA.setSelected(false);
                        }).start();
                    } else {
                        BaseDocument.updates();
                    }
                });
            }).start();
        } else if (SystemConstants.SYSTEM_STATUS == 1) {
            SystemConstants.SYSTEM_STATUS = 2;
            App.btnC.setText("扫描暂停中");
            App. pinA.setVisible(false);
        } else if (SystemConstants.SYSTEM_STATUS == 2) {
            App. btnC.setText("扫描中");
            SystemConstants.SYSTEM_STATUS = 1;
            App.  pinA.setVisible(true);
        }
    }
}
