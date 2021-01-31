package com.yqs.search.util;

import javafx.application.Platform;
import javafx.scene.control.Alert;

public class MyAlert {
    /**
     * 提示
     */
	public static void alert(String msg) {

		new Thread(() -> {
			Platform.runLater(() -> {
				Alert information = new Alert(Alert.AlertType.INFORMATION, msg);
				information.setTitle("操作提示"); // 设置标题，不设置默认标题为本地语言的information
				information.setHeaderText("");
				information.showAndWait();
			});
		}).start();

	}

    /**
     * 错误提示
     */
	public static void alertError(String msg) {

		new Thread(() -> {
			Platform.runLater(() -> {
				Alert information = new Alert(Alert.AlertType.ERROR, msg);
				information.setTitle("错误提示"); // 设置标题，不设置默认标题为本地语言的information
				information.setHeaderText("");
				information.showAndWait();
			});
		}).start();

	}

}
