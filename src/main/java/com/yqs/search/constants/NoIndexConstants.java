package com.yqs.search.constants;


import javax.swing.filechooser.FileSystemView;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * 不参与索引的常量
 */
public class NoIndexConstants {

	
	public static String LUCENE = "_LUCENE";
	public static char PIE = '~';
	public static char DAO = '$';
	public static Path DESK = FileSystemView.getFileSystemView().getHomeDirectory().toPath();
	public static Path DR = DESK.getParent();
	public static Path DRR = DR.getParent();
	public static Path DRRR = DRR.getParent();
	public static Path DOWN = Paths.get(DR+"\\Downloads\\");
	public static Path DOCUMENTS = Paths.get(DR+"\\Documents\\");
	public static Path WINDOWS = Paths.get("C:\\Windows\\");
	public static Path PerfLogs = Paths.get("C:\\PerfLogs\\");
	public static Path ProgramFiles = Paths.get("C:\\Program Files\\");
	public static Path ProgramFiles86 = Paths.get("C:\\Program Files (x86)\\");
	public static Path LOCAL = Paths.get(System.getProperty("user.home")+"\\AppData\\");
	public static Path ProgramData = Paths.get("C:\\ProgramData\\");
	public static Path Default = Paths.get(System.getProperty("user.home")+"\\Default\\");
	public static Path M2 = Paths.get(System.getProperty("user.home")+"\\.m2\\");
}
