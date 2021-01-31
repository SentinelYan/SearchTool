package com.yqs.search.util;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.yqs.search.constants.ParseConstants;
import com.yqs.search.suffixes.*;
import com.yqs.search.index.IndexFileInterface;
import com.yqs.search.store.Stores;
import javafx.application.Platform;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class FilesUtil {

    public static Runtime runtime = Runtime.getRuntime();

    public static String cmdStr =  "explorer /select, " ;
	/**
	 * 拷贝文件
	 * 
	 * @param FilePath
	 */
	public void copeFile(final String FilePath) {

		Transferable t = new Transferable() {
			// 返回对象一定是要一个集合
			@Override
			public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
				String[] p = { FilePath };
				List<File> l = new ArrayList<>();
				for (String str : p) {
					l.add(new File(str));
				}
				return l;
			}

			@Override
			public DataFlavor[] getTransferDataFlavors() {

				DataFlavor[] d = new DataFlavor[] { DataFlavor.javaFileListFlavor };

				return d;
			}

			@Override
			public boolean isDataFlavorSupported(DataFlavor flavor) {
				boolean b = DataFlavor.javaFileListFlavor.equals(flavor);

				return b;
			}
		};
		// Put the selected files into the system clipboard
		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		clipboard.setContents(t, null);

		// 返回指定flavor类型的数据
	}

	public static String getAbsPath(String filePath) {

		String path = null;
		path = System.getProperty("user.dir") + File.separator + "conf" + File.separator + filePath;
		return path;
	}

	//删除文件夹
	public static void delDir(String dirPath) {

		File dir = new File(dirPath);
		File[] dirList = dir.listFiles();
		if (dirList == null || dirList.length == 0)
			dir.delete();
		else {
			// 删除所有文件
			for (File f : dirList)
				if (f.isDirectory())
					delDir(f.getAbsolutePath());
				else
					f.delete();
			// 删除完当前文件夹下所有文件后删除文件夹
			dirList = dir.listFiles();
			if (dirList.length == 0)
				dir.delete();
		}
	}
    /**
     * 打开文件夹
     */
    public static void openDir(Path path) {
        Platform.runLater(() -> {
            if (!Files.exists(path)) {
                MyAlert.alertError("抱歉,文件未找到!!!");
            } else {
                try {
                    //执行exe命令打开文件夹，并选中文件
                    FilesUtil.runtime.exec(cmdStr+path);
                } catch (Exception e1) {
                    e1.printStackTrace();
                }

            }
        });
    }
    /**
     * 文件预览
     *
     * @param path
     * @param searchKeys
     * @return
     */
    public static String yuLanFile(Path path, String[] searchKeys) {
        //获取文件名 ，将字符串小写字符转换为大写。
        String fileName = path.getFileName().toString().toUpperCase();

        IndexFileInterface iff = null;
        //检查文件是否以指定后缀
        if (TxtEnums.suffixContains(fileName)) {
            iff = ParseConstants.itf;
        } else if (fileName.endsWith(PdfEnums.PDF)) {
            iff = ParseConstants.ipf;
        } else if (PptEnums.suffixContains(fileName)) {
            iff = ParseConstants.ippf;
        } else if (WordEnums.suffixContains(fileName)) {
            iff = ParseConstants.iwf;
        } else if (ExcelEnums.suffixContains(fileName)) {
            iff = ParseConstants.ief;
        }
        if (iff != null) {
            return FileHighLight.high(
                    //内容
                    iff.getContent(path.toFile(), fileName),
                    //搜索的关键字
                    searchKeys,
                    "<span style=\"color:red\">", "</span>"
            );
        }
        return "抱歉,非可读文件的内容暂时不可预览!!!";
    }

    //加载 跳过的 配置文件
    public static void addSkip() {

        Kryo kryo = new Kryo();
        File file = new File(System.getProperty("user.dir") + "\\CONF\\aa");
        if (file.exists()) {
            Input input;
            try {
                input = new Input(new FileInputStream(file));
                Stores.skipFilesMap = kryo.readObject(input, HashSet.class);
                input.close();
                System.out.println("加载了" + Stores.skipFilesMap.size());
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 将跳过的文件写到\\CONF\\aa中
     */
    public static void writerSkip() {
        //序列化和反序列化
        Kryo kryo = new Kryo();
        //获取文件对象
        File kf = new File(System.getProperty("user.dir") + "\\CONF\\aa");
        Output output;
        try {
            output = new Output(new FileOutputStream(kf));
            System.out.println("写入:" + Stores.skipFilesMap.size());
            //将对象序列化到Set中
            kryo.writeObject(output, Stores.skipFilesMap);
            output.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

}
