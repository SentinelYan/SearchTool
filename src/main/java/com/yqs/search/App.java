
package com.yqs.search;

import com.yqs.search.base.BaseDocument;
import com.yqs.search.constants.FieldConstant;
import com.yqs.search.constants.ParseConstants;
import com.yqs.search.constants.SystemConstants;
import com.yqs.search.entity.Paging;
import com.yqs.search.suffixes.*;
import com.yqs.search.scan.ScanFileInfo;
import com.yqs.search.search.Search;
import com.yqs.search.search.SearchBean;
import com.yqs.search.search.SearchBody;
import com.yqs.search.store.Stores;
import com.yqs.search.util.FileHighLight;
import com.yqs.search.util.FilesUtil;
import com.yqs.search.util.MyAlert;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.scene.web.HTMLEditor;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Callback;
import lombok.Getter;
import org.apache.lucene.document.Document;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@SuppressWarnings("unchecked")
public class App extends Application {

    //遍历目录树，扫描文件
    public static ScanFileInfo scanFileInfo;


    //此全局变量是缓存 B 页面的初始化布局，以便在别的页面调用
//    public static Stage cacheSate;

    //scan.txt文件路径
    private Path scanTxtPath;

    public static Path pathNow;

    public SearchBody body;
    /**
     * 基础组件
     */
    private static Stage primaryStage;

    public static Text prompt = new Text("工具初始化中...");
    //开始时间
    public static DatePicker startDate = new DatePicker();
    //结束时间
    public static DatePicker endDate = new DatePicker();
    //关键词标签
    private Label labelA = new Label(SystemConstants.LabelA_STR);
    //过滤词标签
    private Label labelB = new Label(SystemConstants.LabelB_STR);
    //检索条件标签
    private Label labelC = new Label(SystemConstants.LabelC_STR);
    //扫描设置标签
    private Label labelD = new Label(SystemConstants.LabelD_STR);
    //时间范围标签
    private Label labelE = new Label(SystemConstants.LabelE_STR);
    //关键词搜索框
    public static TextField txtfieldA = new TextField();
    //过滤词搜索框
    public static TextField txtfieldB = new TextField();
    //搜索按钮
    public Button btnA = new Button(SystemConstants.BtnA_STR);
    //重置按钮
    public Button btnB = new Button(SystemConstants.BtnB_STR);
    //扫描按钮
    public static Button btnC = new Button(SystemConstants.BtnC_STR);
    //文件类型下拉框
    public static ComboBox<String> cboxA = new ComboBox<String>();
    //盘符下拉框
    public static ComboBox<String> cboxB = new ComboBox<String>();
    //文件名/时间排序下拉框
    public static ComboBox<String> cboxC = new ComboBox<String>();
    //进度条
    public static ProgressIndicator pinA = new ProgressIndicator();
    //全新扫描
    public static final RadioButton rbtnA = new RadioButton(SystemConstants.rbtnA_STR);
    //精简模式
    public static final RadioButton rbtnB = new RadioButton(SystemConstants.rbtnB_STR);
    //自动模式
    public static final RadioButton rbtnC = new RadioButton(SystemConstants.rbtnC_STR);

    ///========================================= B =========================================================================

    //列表显示
    private TableView<TextFlowData> tableView = new TableView<TextFlowData>();
    //可应用于监听子节点的变化
    private ObservableList<TextFlowData> dataItem = FXCollections.observableArrayList();

    //    private VBox vBox = new VBox();
    private HBox pageButtonHBox = new HBox();
    private TextFlow resultText = new TextFlow();
    private Text text = new Text();

    private Button nextButton = new Button("下一页");
    private Button previousButton = new Button("上一页");
    private Button currentButton = new Button("当前页: 1");
    private Text currentPageNumText = new Text("当前页:  ");

    // 绑定文件名
    private TableColumn<TextFlowData, TextFlow> nameCol = new TableColumn<TextFlowData, TextFlow>("文件名");
    // 绑定文件预览按钮
    private TableColumn<TextFlowData, Button> seeCol = new TableColumn<TextFlowData, Button>("内容预览");
    // 绑定访问目录按钮
    private TableColumn<TextFlowData, Button> dirCol = new TableColumn<TextFlowData, Button>("访问目录");
    // 绑定复制文件按钮
    private TableColumn<TextFlowData, Button> cpoyCol = new TableColumn<TextFlowData, Button>("复制文件");

    public int pageNum = 1;

//    private Query query;
    ///========================================= B =========================================================================

    private HTMLEditor htmlEditor = new HTMLEditor();

    @Override
    public void start(Stage primaryStage) throws Exception {
        //界面初始化
        Platform.runLater(() -> {
            HBox mainHBox = new HBox();
            GridPane grid = new GridPane();
            HBox leftTopHBox = new HBox();
            leftTopHBox.getChildren().add(grid);


            VBox leftDownHBox = new VBox();

            VBox leftVBox = new VBox();
            VBox rightVBox = new VBox();

            htmlEditor.setPrefHeight(690);

            leftVBox.getChildren().addAll(leftTopHBox, leftDownHBox);
            rightVBox.getChildren().add(htmlEditor);
            Platform.runLater(() -> {
                htmlEditor.setHtmlText("内容加载中,请稍后...");
            });

            ///========================================= B =========================================================================
            leftDownHBox.getChildren().addAll(pageButtonHBox, resultText, tableView);
//        vBox.setMinHeight(500);
            leftDownHBox.setSpacing(10);
            //
            pageButtonHBox.setPadding(new Insets(0, 0, 0, 6));
            pageButtonHBox.getChildren().addAll(previousButton, new Text("    "), nextButton, currentButton);
            resultText = new TextFlow(text);
            resultText.setPadding(new Insets(0, 0, 0, 0));
            leftDownHBox.getChildren().set(1, resultText);
            text.setFont(SystemConstants.YH);
            text.setFill(javafx.scene.paint.Color.valueOf("#CDC5BF"));
            //设置样式
            //设置大小
            //下一页
            nextButton.setPrefSize(85, 20);
            //上一页
            previousButton.setPrefSize(85, 20);
            //当前页按钮
            currentButton.setPrefSize(180, 20);
            //添加到表格中
            tableView.getColumns().addAll(nameCol, seeCol, dirCol, cpoyCol);
            tableView.setItems(dataItem);
            tableView.setStyle("-fx-opacity: 0.74;");
            tableView.setStyle(SystemConstants.YHSTR);
            tableView.setPrefHeight(400);
            tableView.setTranslateX(0);

            nameCol.setStyle(SystemConstants.YHSTR);
//        nameCol.setMinWidth(479);
            nameCol.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<TextFlowData, TextFlow>, ObservableValue<TextFlow>>() {
                public ObservableValue<TextFlow> call(TableColumn.CellDataFeatures<TextFlowData, TextFlow> p) {
                    return p.getValue().getFileName();
                }
            });

            seeCol.setStyle(SystemConstants.YHSTR);
//        seeCol.setMaxWidth(72);
            seeCol.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<TextFlowData, Button>, ObservableValue<Button>>() {
                public ObservableValue<Button> call(TableColumn.CellDataFeatures<TextFlowData, Button> p) {
                    return p.getValue().getSeeBtn();
                }
            });

//        dirCol.setMaxWidth(72);
            dirCol.setStyle(SystemConstants.YHSTR);
            dirCol.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<TextFlowData, Button>, ObservableValue<Button>>() {
                public ObservableValue<Button> call(TableColumn.CellDataFeatures<TextFlowData, Button> p) {
                    return p.getValue().getDirBtn();
                }
            });

//        cpoyCol.setMaxWidth(72);
            cpoyCol.setStyle(SystemConstants.YHSTR);
            cpoyCol.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<TextFlowData, Button>, ObservableValue<Button>>() {
                public ObservableValue<Button> call(TableColumn.CellDataFeatures<TextFlowData, Button> p) {
                    return p.getValue().getCopyBtn();
                }
            });
            currentPageNumText.setFill(Color.WHITE);
            currentPageNumText.setFont(SystemConstants.YH);
            currentPageNumText.setStyle("-fx-text-alignment:center;");
            ///========================================= B =========================================================================


            mainHBox.getChildren().addAll(leftVBox, rightVBox);


            //TODO 设置宽度和高度 300 300
            Scene scene = new Scene(mainHBox, SystemConstants.SCENE_WEIGHT, SystemConstants.scene_HIGH);

//            scene.getStylesheets().add("css/WindowsA.css");

            // 4.关键词标签
            labelA.setPrefWidth(80);
            grid.add(labelA, 0, 1);

            // 5.关键词搜索框
            grid.add(txtfieldA, 1, 1);
            txtfieldA.setFocusTraversable(false);

            // 6.搜索按钮
            btnA.setPrefSize(SystemConstants.BTN_WEIGHT, SystemConstants.BTN_HIGH);
//            grid.add(btnA, 2, 1);

            // 7.添加背景图片
//				grid.setBackground(bkA);

            // 8.过滤词标签
//            grid.add(labelB, 0, 2);

            // 9.过滤词搜索框
//            grid.add(txtfieldB, 1, 2);

            // 10.清除按钮
            btnB.setPrefSize(SystemConstants.BTN_WEIGHT, SystemConstants.BTN_HIGH);
//            grid.add(btnB, 2, 2);

            // 11.检索条件标签
            grid.add(labelC, 0, 3);
            grid.add(labelE, 0, 4);
            grid.add(labelD, 0, 5);

            GridPane grid2 = new GridPane();
            grid.add(grid2, 1, 3);

            // 12.文件类型下拉框
            cboxA.setValue(SystemConstants.QBLX);
            grid2.add(cboxA, 0, 1);

            // 13.盘符下拉框
            cboxB.setValue(SystemConstants.SYPF);
            grid2.add(cboxB, 1, 1);

            cboxC.setValue(SystemConstants.WJMPX);
            grid2.add(cboxC, 2, 1);

            GridPane grid4 = new GridPane();
            grid.add(grid4, 1, 4);
            grid4.add(startDate, 1, 0);
            startDate.setPrefSize(136, 31);

            grid4.add(endDate, 2, 0);
            endDate.setPrefSize(136, 31);

            GridPane grid3 = new GridPane();
            grid.add(grid3, 1, 5);

            // 14.扫描按钮
            btnC.setPrefSize(98, SystemConstants.BTN_HIGH);
            grid3.add(btnC, 6, 1);

            // 15.进度条
            pinA.setPrefSize(35, 35);
            grid3.add(pinA, 5, 1);

            // 16.全新扫描
            grid3.add(rbtnA, 2, 1);

            // 17.精简模式 & 自动模式
            rbtnB.setSelected(true);
            grid3.add(rbtnB, 3, 1);
            grid3.add(rbtnC, 4, 1);

            // ----------------样式属性区-------------------
            grid.setAlignment(Pos.CENTER);
            grid.setHgap(10);
            grid.setVgap(10);
            grid.setPadding(new Insets(10, 0, 0, 6));


            primaryStage.setScene(scene);
            grid.add(prompt, 1, 6);
            primaryStage.show();
        });

        // 1.定义背景大小不可变更
        primaryStage.setResizable(true);
        // 2.标题
        primaryStage.setTitle(SystemConstants.TITLE_NAME);
        //进度条可见：否
        pinA.setVisible(false);

        Search.searchBean = new SearchBean();

        // 15.提示词
        Platform.runLater(() -> {
            //TODO 初始化Base类
            BaseDocument.init();
            //加载需要跳过的文件
            FilesUtil.addSkip();
            //获取焦点
            txtfieldA.requestFocus();
            //加载scan.txt文件
            scanTxtPath = Paths.get(System.getProperty("user.dir") + "\\CONF\\scan.txt");
            //如果文件存在
            if (Files.exists(scanTxtPath)) {
                //自动模式则被选中
                rbtnC.setSelected(true);
            }
        });

        //全部类型
        ObservableList<String> optionsA = FXCollections.observableArrayList(SystemConstants.QBLX);
        //所有盘符
        ObservableList<String> optionsB = FXCollections.observableArrayList(SystemConstants.SYPF);
        //按时间排序
        ObservableList<String> optionsC = FXCollections.observableArrayList(SystemConstants.ASJPX);
        // --------属性绑定-------

        //添加Word后缀
        optionsA.addAll(WordEnums.getValues());
        optionsA.addAll(ExcelEnums.getValues());
        optionsA.addAll(PdfEnums.PDF);

        //精简模式被选中
        if (rbtnB.isSelected()) {
            //只加 TXT 后缀
            optionsA.addAll(TxtEnums.getSimleValues());
        } else {
            //加Java\JSP\CSS等后缀
            optionsA.addAll(TxtEnums.getValues());
        }
        //PPT后缀
        optionsA.addAll(PptEnums.getValues());
        //其他文件
        optionsA.addAll(SystemConstants.QT);
        //文件夹
        optionsA.addAll(SystemConstants.QT2);
        //文件名排序
        optionsC.add(SystemConstants.WJMPX);

        //文件下拉框
        cboxA.setItems(optionsA);
        //盘符下拉框
        cboxB.setItems(optionsB);
        //文件名时间排序下拉框
        cboxC.setItems(optionsC);
        //返回指示可用的文件系统的根文件对象的数组 C: //D://E://F://G:
        Stores.DIRROOTS = File.listRoots();

        //将遍历到的盘符，添加到 optionB 中
        for (File drive : Stores.DIRROOTS) {
            //将盘符转换成大写，添加到
            optionsB.addAll((drive.toString().charAt(0) + "").toUpperCase());
        }

        /**
         * 关键词搜索框
         */
        txtfieldA.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {

                Search.getSearchResult();
            }
        });

        /**
         * 关键词搜索框
         */
        txtfieldA.textProperty().addListener(new ChangeListener<String>() {

            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                Search.getSearchResult();
                if (SystemConstants.GO == 1) {
                    prompt.setText("工具初始化中,请稍等...");
                } else {
                    //获取关键词输入框的内容
                    newValue = newValue.replaceAll("[a-z]{1,5}'.*", "");
                    //如果内容长度为0
                    if (newValue.length() > 0) {
                        //如果set集合中包含搜索框的内容
                        preLoad();
                    } else {
                        dataItem.clear();
                        Search.searchBean.setBody(null);
                    }
                }

            }
        });

        /**
         * 过滤词搜索框
         */
        txtfieldB.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                Search.getSearchResult();
            }
        });

        /**
         * 过滤词搜索框
         */
        txtfieldB.textProperty().addListener(new ChangeListener<String>() {

            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {

                Search.searchBean.setBody(Search.getSearchBody());
            }
        });

        /**
         * 搜索按钮
         */
        btnA.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                //关键词分割
            }
        });

        /**
         * 重置按钮
         */
        btnB.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                SystemConstants.SYSTEM_STATUS = 4;
                txtfieldA.clear();
                txtfieldB.clear();
                startDate.setValue(null);
                endDate.setValue(null);
                //全部类型
                cboxA.setValue(SystemConstants.QBLX);
                //所有盘符
                cboxB.setValue(SystemConstants.SYPF);
                //工具初始化
                prompt.setText("");
                //全新扫描
                rbtnA.setSelected(false);
                rbtnB.setSelected(true);
                Search.searchBean.setBody(null);
            }
        });

        /**
         * 文件名时间排序下拉框
         */
        cboxC.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {

                preLoad();
            }
        });
        /**
         *  扫描按钮
         */
        btnC.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {

                ScanFileInfo.autoScan();
            }
        });
        /**
         *  文件类型下拉框
         */
        cboxA.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {

                Search.searchBean.setBody(null);
            }
        });
        /**
         *盘符下拉框
         */
        cboxB.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {

                Search.searchBean.setBody(null);
            }
        });
        /**
         * 精简模式
         */
        rbtnB.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                Search.searchBean.setBody(null);
                //精简模式按钮被选中
                if (rbtnB.isSelected()) {
                    SystemConstants.TASK_TIME = 15;

                    //设置为简洁模式
                    TxtEnums.setRUN_TXT_SET(0);

                    //根据返回的Map获取遍历出所有后缀名选项
                    TxtEnums.getValues().forEach(s -> {
                        //判断文件后缀名不为.txt
                        if (!".TXT".equals(s)) {
                            //全部类型将移除这个后缀名的选项
                            optionsA.remove(s);
                        }
                    });
                } else {
                    SystemConstants.TASK_TIME = 30;
                    TxtEnums.setRUN_TXT_SET(1);
                    TxtEnums.getValues().forEach(s -> {
                        if (!".TXT".equals(s)) {
                            optionsA.add(s);
                        }
                    });
                }
            }
        });

        /**
         * 自动模式
         */
        rbtnC.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {

                //自动模式被选中
                if (rbtnC.isSelected()) {

                    //如果这个文件不存在
                    if (!Files.exists(scanTxtPath))
                        try {
                            //创建这个文件
                            Files.createFile(scanTxtPath);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    //如果这个文件存在
                } else if (Files.exists(scanTxtPath)) {
                    try {
                        //删除这个文件
                        Files.delete(scanTxtPath);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        /**
         * 开始时间
         */
        startDate.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {

                Search.searchBean.setBody(null);
            }
        });
        /**
         * 结束时间
         */
        endDate.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {

                Search.searchBean.setBody(null);
            }
        });
        /**
         * 关闭界面
         */
        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {

            @Override
            public void handle(WindowEvent event) {
                //将跳过的文件写到\\CONF\\aa中
                FilesUtil.writerSkip();
                SystemConstants.SYSTEM_STATUS = 4;
                //关闭主界面
                primaryStage.close();
                BaseDocument.commit();
                BaseDocument.closeAll();
                //系统退出
                System.exit(0);
            }
        });

        /**
         * 点击上一页按钮
         */
        previousButton.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent t) {

                Stores.PUBLICKEY = body.getSearchKeys();
                if (pageNum > 1) {
                    pageNum--;
                    Paging paging = Stores.cachePaging.get(pageNum);
                    Document doc;
                    //循环数据条数
                    for (int i = 0; i < paging.getDocList().size(); i++) {
                        //获取查询到的每一条数据
                        doc = paging.getDocList().get(i);
                        TextFlowData md = Stores.textFlowDataMap.get(i);
                        Stores.textFlowDataMap.put(i, md);
                        dataItem.set(i, md);
                        md.init(doc);
                    }
                    currentButton.setText("当前页: " + pageNum + "/" + SystemConstants.maxPageNum);
                    doc = null;
                } else {
                    MyAlert.alertError("已经是第一页了!");
                }
            }
        });

        /**
         * 点击下一页按钮
         */
        nextButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {
                //TODO 获取关键词分割
                Stores.PUBLICKEY = body.getSearchKeys();

                //TODO 判断 当前页面 是否小于 页面最大值
                if (pageNum < SystemConstants.maxPageNum) {
                    //TODO 当前页面 +1
                    pageNum++;
                    //根据当前页面值从缓存中获取到对应的paging
                    Paging paging = Stores.cachePaging.get(pageNum);

                    if (paging == null) {
                        paging = Search.search(Search.searchBean.getQuery(), Stores.scoreDoc.get(pageNum));

                        Stores.cachePaging.put(pageNum, paging);
                    }
                    //获取所有的doc对象
                    for (int i = 0; i < paging.getDocList().size(); i++) {
                        //获取一条索引记录
                        Document doc = paging.getDocList().get(i);
                        TextFlowData md = Stores.textFlowDataMap.get(i);
                        dataItem.set(i, md);
                        md.init(doc);
                    }
                    // 加入缓存
                    Stores.scoreDoc.put(pageNum + 1, paging.getAfterScoreDoc());
                    getNextPage();
                    currentButton.setText("当前页: " + pageNum + "/" + SystemConstants.maxPageNum);

                } else {
                    MyAlert.alertError("已经是最后一页了!!!");
                }
            }
        });
    }

    private void preBody() {

        new Thread(() -> {
            //将搜索体设置进入搜索对象中
            Search.searchBean.setBody(Search.getSearchBody());
            body = Search.searchBean.getBody();
        }).start();
    }

    // -----------普通方法--------------------
    public void preLoad() {
        //TODO searchKeySet集合中包含搜索框的内容，初始化searchBean并生成一条查询记录
        Search.searchBean.setBody(Search.getSearchBody());

        body = Search.searchBean.getBody();

        try {
            Stores.PUBLICKEY = Search.searchBean.getBody().getSearchKeys();
            //搜索
            Paging paging = Search.search(Search.searchBean.getQuery(), Stores.scoreDoc.get(pageNum));
            if (!dataItem.isEmpty()) {
                dataItem.clear();
            }
            //循环获取每一条查询数据
            for (int i = 0; i < paging.getDocList().size(); i++) {
                //获取每一条数据
                Document doc = paging.getDocList().get(i);
                TextFlowData md = new TextFlowData(doc);
                dataItem.add(md);
                Stores.textFlowDataMap.put(i, md);
            }
            //存入文档的编号
            Stores.scoreDoc.put(pageNum + 1, paging.getAfterScoreDoc());
            //最大页数
            SystemConstants.maxPageNum = paging.getMaxPageNum();

            text.setText("关键词:  \"" + body.getSearchKey() + "\"  共查询到了" + (paging.getTotalResultNum()) + "条相关记录,每页显示5条。");

            currentButton.setText("当前页: " + pageNum + "/" + SystemConstants.maxPageNum);

            getNextPage();
        } catch (Exception e) {
            e.printStackTrace();
        }
        ParseConstants.init();
    }

    /**
     * 加载搜索结果页面s
     */
    private static Stage getStage() {
        Group root = new Group();
        Scene scene = new Scene(root, 758, 550);
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.setResizable(false);
        stage.setTitle("搜索结果");
        return stage;

    }

    public static void close() {

        Platform.runLater(() -> {
            if (primaryStage != null)
                primaryStage.close();
        });
    }

    private void getNextPage() {

        new Thread(() -> {
            int next = pageNum + 1;
            //获取下一条分页数据
            Paging my2 = Stores.cachePaging.get(next);
            //如果数据为空
            if (my2 == null) {
                // 缓存下一页
                Paging my3 = Search.search(Search.searchBean.getQuery(), Stores.scoreDoc.get(next));
                Stores.cachePaging.put(next, my3);
                Stores.scoreDoc.put(next + 1, my3.getAfterScoreDoc());
            }
        }).start();
    }

    public void show(String[] searchKeys, Path path) {
        Platform.runLater(() -> {

            String s = FilesUtil.yuLanFile(path, searchKeys);
            if (s == null || s.trim().length() == 0)
                htmlEditor.setHtmlText("抱歉,无法读取该文档的内容,请手动点击查看");
            else
                htmlEditor.setHtmlText(s);
        });
    }

    public class TextFlowData {

        private TextFlow textFlow;
        //查看目录
        @Getter
        private ObservableValue<Button> dirBtn;
        //复制文件
        @Getter
        private ObservableValue<Button> copyBtn;
        //内容预览
        @Getter
        private ObservableValue<Button> seeBtn;
        //文件名
        @Getter
        private ObservableValue<TextFlow> fileName;

        private Button btn = new Button("查看目录");
        private Button btn2 = new Button("内容预览");
        private Button btn3 = new Button("复制文件");

        private Document docc;

        public Path path1;

        public TextFlowData(Document doc) {
            init(doc);
            /**
             * 文件名按钮
             */
            this.fileName = new ObservableValue<TextFlow>() {

                @Override
                public void removeListener(InvalidationListener arg0) {
                }

                @Override
                public void addListener(InvalidationListener arg0) {
                }

                @Override
                public void removeListener(ChangeListener<? super TextFlow> listener) {
                }

                @Override
                public TextFlow getValue() {

                    return textFlow;
                }

                @Override
                public void addListener(ChangeListener<? super TextFlow> listener) {
                }
            };
            /**
             * 打开目录按钮
             */
            this.dirBtn = new ObservableValue<Button>() {
                @Override
                public void addListener(InvalidationListener listener) {
                }

                @Override
                public void removeListener(InvalidationListener listener) {
                }

                @Override
                public void addListener(ChangeListener<? super Button> listener) {
                }

                @Override
                public void removeListener(ChangeListener<? super Button> listener) {
                }

                @Override
                public Button getValue() {
                    //查看目录
                    btn.setOnAction(new EventHandler<ActionEvent>() {
                        @Override
                        public void handle(ActionEvent event) {

                            FilesUtil.openDir(path1);
                        }
                    });
                    return btn;
                }
            };
            /**
             * 文件预览按钮
             */
            this.seeBtn = new ObservableValue<Button>() {

                @Override
                public void addListener(InvalidationListener listener) {
                }

                @Override
                public void removeListener(InvalidationListener listener) {
                }

                @Override
                public void addListener(ChangeListener<? super Button> listener) {
                }

                @Override
                public void removeListener(ChangeListener<? super Button> listener) {
                }

                @Override
                public Button getValue() {
                    //内容预览按钮
                    btn2.setOnAction(new EventHandler<ActionEvent>() {
                        @Override
                        public void handle(ActionEvent event) {
                            //判断文件是否存在
                            if (!Files.exists(path1)) {

                                MyAlert.alertError("抱歉,文件未找到!!!");

                            } else {
                                show(Stores.PUBLICKEY, path1);
                            }
                        }
                    });
                    return btn2;
                }
            };
            /**
             * 文件复制按钮
             */
            this.copyBtn = new ObservableValue<Button>() {
                @Override
                public void removeListener(InvalidationListener listener) {
                }

                @Override
                public void addListener(InvalidationListener listener) {
                }

                @Override
                public void removeListener(ChangeListener<? super Button> listener) {
                }

                @Override
                public Button getValue() {
                    btn3.setOnAction(new EventHandler<ActionEvent>() {
                        @Override
                        public void handle(ActionEvent event) {
                            // 0表示原来的，1表示现在的，2表示都没有
                            if (!Files.exists(path1)) {
                                MyAlert.alertError("文件未找到");
                            } else {
                                try {
                                    new FilesUtil().copeFile((path1).toString());
                                    MyAlert.alert("文件已复制");
                                    // JOptionPane.showMessageDialog(null,
                                    // "复制文件成功");
                                } catch (Exception e) {
                                    MyAlert.alertError("文件复制失败");
                                }
                            }
                        }
                    });
                    return btn3;
                }

                @Override
                public void addListener(ChangeListener<? super Button> listener) {

                }
            };
        }

        private void init(Document doc) {
            //获取数据库中一条具体的记录
            docc = doc;
            //根据document获取路径
            //TODO 先从MAP中取出路径，如果MAP中路径为空
            Path path = Stores.docMap.get(docc);
            //如果path为空
            if (path == null) {
                //获取路径
                path = Paths.get(docc.get(FieldConstant.FILE_PATH));
                //存入Map中
                Stores.docMap.put(docc, path);
            }
            //当前路径
            path1 = path;
            //路径+关键词
            String str = path1 + body.getSearchKey();
            //获取一条文件记录
            TextFlow textField2 = Stores.textFlowMap.get(str);

            //如果文件为空
            if (textField2 == null) {

                textField2 = FileHighLight.fileNameHigh(
                        //文件名
                        docc.get(FieldConstant.FILE_NAME),
                        //关键词
                        body.getSearchKeys(),
                        //路径
                        path1.getParent().toString(),
                        //更新时间
                        doc.get(FieldConstant.FILE_UPDATE_TIME)
                );

                //存储 路径+关键词 / TextFlow
                Stores.textFlowMap.put(str, textField2);

                textField2.setPrefHeight(68);
            }
            this.textFlow = textField2;

            //初始化 bat命令
            FilesUtil.cmdStr = "explorer /select, " + path1;
        }
    }
}
