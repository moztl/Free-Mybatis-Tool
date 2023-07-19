package com.tianlei.mybatis.ui;


import com.google.common.base.Joiner;
import com.intellij.database.psi.DbTable;
import com.intellij.ide.util.PackageChooserDialog;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.ui.TextBrowseFolderListener;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiPackage;
import com.intellij.ui.components.*;
import com.intellij.util.ui.JBUI;
import com.tianlei.mybatis.generate.MybatisGenerator;
import com.tianlei.mybatis.model.Config;
import com.tianlei.mybatis.model.TableInfo;
import com.tianlei.mybatis.setting.PersistentConfig;
import com.tianlei.mybatis.util.JTextFieldHintListener;
import com.tianlei.mybatis.util.StringUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 插件主界面
 */
public class MybatisGeneratorMainUI extends JFrame {


    private AnActionEvent anActionEvent;
    private Project project;
    private PersistentConfig persistentConfig;
    private PsiElement[] psiElements;
    private Map<String, Config> initConfigMap;
    private Map<String, Config> historyConfigList;
    private Config config;


    private JPanel contentPane = new JBPanel<>();
    private final JButton buttonOK = new JButton("ok");
    private final JButton buttonCancel = new JButton("cancel");
    private final JButton deleteConfigBtn = new JButton("DELETE");


    private final JTextField tableNameField = new JTextField(10);

    private final JTextField modelNameField = new JTextField(10);
    private final JBTextField modelPackageField = new JBTextField(12);

    private final JTextField daoNameField = new JTextField(10);
    private final JBTextField daoPackageField = new JBTextField(12);

    private final JTextField daoPostfixField = new JTextField(10);

    private final JBTextField xmlPackageField = new JBTextField(12);
    private final JTextField keyField = new JTextField(10);

    private TextFieldWithBrowseButton projectFolderBtn = new TextFieldWithBrowseButton();
    private final JTextField modelMvnField = new JBTextField(15);
    private final JTextField daoMvnField = new JBTextField(15);
    private final JTextField xmlMvnField = new JBTextField(15);

    private final JCheckBox commentBox = new JCheckBox("Comment(实体注释)");
    private final JCheckBox overrideXMLBox = new JCheckBox("Overwrite-Xml");
    private final JCheckBox overrideJavaBox = new JCheckBox("Overwrite-Java");
    private final JCheckBox needToStringHashcodeEqualsBox = new JCheckBox("toString/hashCode/equals");
    private final JCheckBox useSchemaPrefixBox = new JCheckBox("Use-Schema(使用Schema前缀)");
    private final JCheckBox annotationDAOBox = new JCheckBox("Repository-Annotation(Repository注解)");
    private final JCheckBox useDAOExtendStyleBox = new JCheckBox("Parent-Interface(Dao公共父接口)");
    private final JCheckBox jsr310SupportBox = new JCheckBox("JSR310: Date and Time API");
    private final JCheckBox annotationBox = new JCheckBox("JPA-Annotation(JPA注解)");
    private final JCheckBox useActualColumnNamesBox = new JCheckBox("Actual-Column(实际的列名)");
    private final JCheckBox useTableNameAliasBox = new JCheckBox("Use-Alias(启用别名查询)");
    private final JCheckBox useExampleBox = new JCheckBox("Use-Example");
    private final JCheckBox offsetLimitBox = new JCheckBox("Page(分页，需开启Use-Example)");
    private final JCheckBox needForUpdateBox = new JCheckBox("Add-ForUpdate(需开启Use-Example)");
    private final JCheckBox useLombokBox = new JCheckBox("Use-Lombok");


    public MybatisGeneratorMainUI(AnActionEvent anActionEvent) throws HeadlessException {
        this.anActionEvent = anActionEvent;
        this.project = anActionEvent.getData(PlatformDataKeys.PROJECT);
        this.persistentConfig = PersistentConfig.getInstance(project);
        this.psiElements = anActionEvent.getData(LangDataKeys.PSI_ELEMENT_ARRAY);

        initConfigMap = persistentConfig.getInitConfig();
        historyConfigList = persistentConfig.getHistoryConfigList();


        setTitle("MyBatis Generate Tool");
        if (psiElements.length > 1) {
            setPreferredSize(new Dimension(1200, 550));//设置大小
        } else {
            setPreferredSize(new Dimension(1200, 650));//设置大小
        }
        setLocation(120, 100);
        pack();
        setVisible(true);
        getRootPane().setDefaultButton(buttonOK);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        PsiElement psiElement = psiElements[0];
        TableInfo tableInfo = new TableInfo((DbTable) psiElement);
        String tableName = tableInfo.getTableName();
        String modelName = StringUtils.dbStringToCamelStyle(tableName);
        String primaryKey = "";
        if (tableInfo.getPrimaryKeys().size() > 0) {
            primaryKey = tableInfo.getPrimaryKeys().get(0);
        }
        String projectFolder = project.getBasePath();

        boolean multiTable;
        if (psiElements.length > 1) { // 多表时，只使用默认配置
            multiTable = true;
            if (initConfigMap != null) {
                config = initConfigMap.get("initConfig");
            }
        } else {
            multiTable = false;
            if (initConfigMap != null) { // 单表时，优先使用已经存在的配置
                config = initConfigMap.get("initConfig");
            }
            if (historyConfigList == null) {
                historyConfigList = new HashMap<>();
            } else {
                if (historyConfigList.containsKey(tableName)) {
                    config = historyConfigList.get(tableName);
                }
            }
        }

        // project panel
        JPanel projectFolderPanel = initProjectPanel(projectFolder);
        // table setting
        JPanel tablePanel = initTableSetting(tableName, primaryKey);
        // model setting
        JPanel modelPanel = initModelSetting(multiTable, modelName);
        // dao setting
        JPanel daoPanel = initDaoSetting(multiTable, modelName);
        // xml mapper setting
        JPanel xmlMapperPanel = initXmlMapperSetting();
        // options
        JBPanel optionsPanel = initOptionsPanel();

        // main
        JPanel mainPanel = new JPanel();
        BoxLayout lo = new BoxLayout(mainPanel, BoxLayout.Y_AXIS);
        mainPanel.setLayout(lo);
        mainPanel.setBorder(new EmptyBorder(10, 30, 5, 40));
        mainPanel.add(projectFolderPanel);
        if (!multiTable) {
            mainPanel.add(tablePanel);
        }
        mainPanel.add(modelPanel);
        mainPanel.add(daoPanel);
        mainPanel.add(xmlMapperPanel);
        mainPanel.add(optionsPanel);

        // historyConfig panel
        JPanel historyConfigPanel = initHistoryConfigPanel();

        // 确认和取消按钮
        JPanel paneBottom = new JPanel();
        paneBottom.setLayout(new FlowLayout(2));
        paneBottom.add(buttonOK);
        paneBottom.add(buttonCancel);

        contentPane.setBorder(JBUI.Borders.empty(5));
        contentPane.setLayout(new BorderLayout());
        contentPane.add(mainPanel, BorderLayout.CENTER);
        contentPane.add(paneBottom, BorderLayout.SOUTH);
        contentPane.add(historyConfigPanel, BorderLayout.WEST);
        setContentPane(contentPane);

        buttonOK.addActionListener(e -> onOK());
        buttonCancel.addActionListener(e -> onCancel());
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });
        contentPane.registerKeyboardAction(e -> onCancel(), KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    private void onOK() {
        try {
            dispose();
            List<String> result = new ArrayList<>();
            if (psiElements.length == 1) {
                Config generator_config = new Config();
                generator_config.setName(tableNameField.getText());
                generator_config.setTableName(tableNameField.getText());
                generator_config.setProjectFolder(projectFolderBtn.getText());

                generator_config.setModelPackage(modelPackageField.getText());
                generator_config.setDaoPackage(daoPackageField.getText());
                generator_config.setXmlPackage(xmlPackageField.getText());
                generator_config.setDaoName(daoNameField.getText());
                generator_config.setModelName(modelNameField.getText());
                generator_config.setPrimaryKey(keyField.getText());

                generator_config.setOffsetLimit(offsetLimitBox.getSelectedObjects() != null);
                generator_config.setComment(commentBox.getSelectedObjects() != null);
                generator_config.setOverrideXML(overrideXMLBox.getSelectedObjects() != null);
                generator_config.setOverrideJava(overrideJavaBox.getSelectedObjects() != null);
                generator_config.setNeedToStringHashcodeEquals(needToStringHashcodeEqualsBox.getSelectedObjects() != null);
                generator_config.setUseSchemaPrefix(useSchemaPrefixBox.getSelectedObjects() != null);
                generator_config.setNeedForUpdate(needForUpdateBox.getSelectedObjects() != null);
                generator_config.setAnnotationDAO(annotationDAOBox.getSelectedObjects() != null);
                generator_config.setUseDAOExtendStyle(useDAOExtendStyleBox.getSelectedObjects() != null);
                generator_config.setJsr310Support(jsr310SupportBox.getSelectedObjects() != null);
                generator_config.setAnnotation(annotationBox.getSelectedObjects() != null);
                generator_config.setUseActualColumnNames(useActualColumnNamesBox.getSelectedObjects() != null);
                generator_config.setUseTableNameAlias(useTableNameAliasBox.getSelectedObjects() != null);
                generator_config.setUseExample(useExampleBox.getSelectedObjects() != null);
                generator_config.setUseLombokPlugin(useLombokBox.getSelectedObjects() != null);

                generator_config.setModelMvnPath(modelMvnField.getText());
                generator_config.setDaoMvnPath(daoMvnField.getText());
                generator_config.setXmlMvnPath(xmlMvnField.getText());

                result = new MybatisGenerator(generator_config).execute(anActionEvent, true, psiElements[0]);
            } else {

                for (PsiElement psiElement : psiElements) {
                    TableInfo tableInfo = new TableInfo((DbTable) psiElement);
                    String tableName = tableInfo.getTableName();
                    String modelName = StringUtils.dbStringToCamelStyle(tableName);
                    String primaryKey = "";
                    if (tableInfo.getPrimaryKeys() != null && tableInfo.getPrimaryKeys().size() != 0) {
                        primaryKey = tableInfo.getPrimaryKeys().get(0);
                    }
                    Config generator_config = new Config();
                    generator_config.setName(tableName);
                    generator_config.setTableName(tableName);
                    generator_config.setProjectFolder(projectFolderBtn.getText());

                    generator_config.setModelPackage(modelPackageField.getText());
                    generator_config.setDaoPackage(daoPackageField.getText());
                    generator_config.setXmlPackage(xmlPackageField.getText());
                    generator_config.setDaoName(modelName + daoPostfixField.getText());
                    generator_config.setModelName(modelName);
                    generator_config.setPrimaryKey(primaryKey);

                    generator_config.setOffsetLimit(offsetLimitBox.getSelectedObjects() != null);
                    generator_config.setComment(commentBox.getSelectedObjects() != null);
                    generator_config.setOverrideXML(overrideXMLBox.getSelectedObjects() != null);
                    generator_config.setOverrideJava(overrideJavaBox.getSelectedObjects() != null);
                    generator_config.setNeedToStringHashcodeEquals(needToStringHashcodeEqualsBox.getSelectedObjects() != null);
                    generator_config.setUseSchemaPrefix(useSchemaPrefixBox.getSelectedObjects() != null);
                    generator_config.setNeedForUpdate(needForUpdateBox.getSelectedObjects() != null);
                    generator_config.setAnnotationDAO(annotationDAOBox.getSelectedObjects() != null);
                    generator_config.setUseDAOExtendStyle(useDAOExtendStyleBox.getSelectedObjects() != null);
                    generator_config.setJsr310Support(jsr310SupportBox.getSelectedObjects() != null);
                    generator_config.setAnnotation(annotationBox.getSelectedObjects() != null);
                    generator_config.setUseActualColumnNames(useActualColumnNamesBox.getSelectedObjects() != null);
                    generator_config.setUseTableNameAlias(useTableNameAliasBox.getSelectedObjects() != null);
                    generator_config.setUseExample(useExampleBox.getSelectedObjects() != null);
                    generator_config.setUseLombokPlugin(useLombokBox.getSelectedObjects() != null);

                    generator_config.setModelMvnPath(modelMvnField.getText());
                    generator_config.setDaoMvnPath(daoMvnField.getText());
                    generator_config.setXmlMvnPath(xmlMvnField.getText());
                    boolean needSaveConfig = historyConfigList == null || !historyConfigList.containsKey(tableName);
                    result = new MybatisGenerator(generator_config).execute(anActionEvent, needSaveConfig, psiElement);
                }

            }
            if (!result.isEmpty()) {
                Messages.showMessageDialog(Joiner.on("\n").join(result), "Warning", Messages.getWarningIcon());
            }

        } catch (Exception e1) {
            Messages.showMessageDialog(e1.getMessage(), "Error", Messages.getErrorIcon());
        } finally {
            dispose();
        }
    }

    private void onCancel() {
        dispose();
    }

    /**
     * project panel
     */
    private JPanel initProjectPanel(String projectFolder) {
        JPanel projectFolderPanel = new JPanel();
        projectFolderPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        JLabel projectLabel = new JLabel("Project Folder:");
        projectFolderPanel.add(projectLabel);
        projectFolderBtn.setTextFieldPreferredWidth(45);
        if (config != null && !StringUtils.isEmpty(config.getProjectFolder())) {
            projectFolderBtn.setText(config.getProjectFolder());
        } else {
            projectFolderBtn.setText(projectFolder);
        }
        projectFolderBtn.addBrowseFolderListener(new TextBrowseFolderListener(
                FileChooserDescriptorFactory.createSingleFileOrFolderDescriptor()) {
            @Override
            public void actionPerformed(ActionEvent e) {
                super.actionPerformed(e);
                projectFolderBtn.setText(projectFolderBtn.getText().replaceAll("\\\\", "/"));
            }
        });
        projectFolderPanel.add(projectFolderBtn);
        return projectFolderPanel;
    }

    /**
     * table setting
     */
    private JPanel initTableSetting(String tableName, String primaryKey) {

        JPanel tableNameFieldPanel = new JPanel();
        tableNameFieldPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        JLabel tablejLabel = new JLabel("Table Name:");
        tablejLabel.setSize(new Dimension(20, 30));
        tableNameFieldPanel.add(tablejLabel);
        if (psiElements.length > 1) {
            tableNameField.addFocusListener(new JTextFieldHintListener(tableNameField, "eg:db_table"));
        } else {
            tableNameField.setText(tableName);
        }
        tableNameFieldPanel.add(tableNameField);

        JPanel keyFieldPanel = new JPanel();
        keyFieldPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        keyFieldPanel.add(new JLabel("Primary Key(optional):"));
        if (psiElements.length > 1) {
            keyField.addFocusListener(new JTextFieldHintListener(keyField, "eg:primary key"));
        } else {
            keyField.setText(primaryKey);
        }
        keyFieldPanel.add(keyField);

        JPanel tablePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        tablePanel.setBorder(BorderFactory.createTitledBorder("Table Setting"));
        tablePanel.add(tableNameFieldPanel);
        tablePanel.add(keyFieldPanel);
        return tablePanel;
    }

    /**
     * model setting
     */
    private JPanel initModelSetting(boolean multiTable, String modelName) {
        JPanel modelPanel = new JPanel();
        modelPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        modelPanel.setBorder(BorderFactory.createTitledBorder("Model Setting"));
        JPanel modelNameFieldPanel = new JPanel();
        modelNameFieldPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        if (!multiTable) {
            modelNameFieldPanel.add(new JLabel("name:"));
            modelNameField.setText(modelName);
            modelNameFieldPanel.add(modelNameField);

        }
        JBLabel labelLeft4 = new JBLabel("package:");
        modelNameFieldPanel.add(labelLeft4);
        if (config != null && !StringUtils.isEmpty(config.getModelPackage())) {
            modelPackageField.setText(config.getModelPackage());
        } else {
            modelPackageField.setText("generate");
        }
        modelNameFieldPanel.add(modelPackageField);
        JButton modelPackageFieldBtn = new JButton("...");
        modelPackageFieldBtn.addActionListener(actionEvent -> {
            final PackageChooserDialog chooser = new PackageChooserDialog("chooser model package", project);
            chooser.selectPackage(modelPackageField.getText());
            chooser.show();
            final PsiPackage psiPackage = chooser.getSelectedPackage();
            String packageName = psiPackage == null ? null : psiPackage.getQualifiedName();
            modelPackageField.setText(packageName);
            MybatisGeneratorMainUI.this.toFront();
        });
        modelNameFieldPanel.add(modelPackageFieldBtn);
        modelNameFieldPanel.add(new JLabel("path:"));
        modelMvnField.setText("src/main/java");
        modelNameFieldPanel.add(modelMvnField);

        modelPanel.add(modelNameFieldPanel);
        return modelPanel;
    }

    /**
     * dao setting
     */
    private JPanel initDaoSetting(boolean multiTable, String modelName) {
        JPanel daoPanel = new JPanel();
        daoPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        daoPanel.setBorder(BorderFactory.createTitledBorder("Dao Setting"));

        JPanel daoNameFieldPanel = new JPanel();
        daoNameFieldPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        if (multiTable) { //多表
            if (config != null && !StringUtils.isEmpty(config.getDaoPostfix())) {
                daoPostfixField.setText(config.getDaoPostfix());
            } else {
                daoPostfixField.setText("Dao");
            }
            daoNameFieldPanel.add(new JLabel("dao postfix:"));
            daoNameFieldPanel.add(daoPostfixField);
        } else {//单表
            if (config != null && !StringUtils.isEmpty(config.getDaoPostfix())) {
                daoNameField.setText(modelName + config.getDaoPostfix());
            } else {
                daoNameField.setText(modelName + "Dao");
            }

            daoNameFieldPanel.add(new JLabel("name:"));
            daoNameFieldPanel.add(daoNameField);

        }
        daoPanel.add(daoNameFieldPanel);


        JLabel labelLeft5 = new JLabel("package:");
        daoPanel.add(labelLeft5);
        if (config != null && !StringUtils.isEmpty(config.getDaoPackage())) {
            daoPackageField.setText(config.getDaoPackage());
        } else {
            daoPackageField.setText("generate");
        }
        daoPanel.add(daoPackageField);
        JButton packageBtn2 = new JButton("...");
        packageBtn2.addActionListener(actionEvent -> {
            final PackageChooserDialog chooser = new PackageChooserDialog("choose dao package", project);
            chooser.selectPackage(daoPackageField.getText());
            chooser.show();
            final PsiPackage psiPackage = chooser.getSelectedPackage();
            String packageName = psiPackage == null ? null : psiPackage.getQualifiedName();
            daoPackageField.setText(packageName);
            MybatisGeneratorMainUI.this.toFront();
        });
        daoPanel.add(packageBtn2);
        daoPanel.add(new JLabel("path:"));
        daoMvnField.setText("src/main/java");
        daoPanel.add(daoMvnField);
        return daoPanel;
    }

    /**
     * xml mapper setting
     */
    private JPanel initXmlMapperSetting() {
        JPanel xmlMapperPanel = new JPanel();
        xmlMapperPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        xmlMapperPanel.setBorder(BorderFactory.createTitledBorder("Xml Mapper Setting"));

        JPanel xmlFieldPanel = new JPanel();
        xmlFieldPanel.setLayout(new FlowLayout(FlowLayout.CENTER));

        JLabel labelLeft6 = new JLabel("package:");
        xmlFieldPanel.add(labelLeft6);
        if (config != null && !StringUtils.isEmpty(config.getXmlPackage())) {
            xmlPackageField.setText(config.getXmlPackage());
        } else {
            xmlPackageField.setText("generator");
        }
        xmlFieldPanel.add(xmlPackageField);
        xmlFieldPanel.add(new JLabel("path:"));
        xmlMvnField.setText("src/main/resources");
        xmlFieldPanel.add(xmlMvnField);

        xmlMapperPanel.add(xmlFieldPanel);
        return xmlMapperPanel;
    }

    /**
     * options
     */
    private JBPanel initOptionsPanel() {
        JBPanel optionsPanel = new JBPanel(new GridLayout(5, 5, 5, 5));
        optionsPanel.setBorder(BorderFactory.createTitledBorder("Options"));
        if (config == null) {
            /**
             * Default selected
             **/
            useLombokBox.setSelected(true);
            commentBox.setSelected(true);
            overrideJavaBox.setSelected(true);
            useSchemaPrefixBox.setSelected(true);
            annotationDAOBox.setSelected(true);
        } else {
            offsetLimitBox.setSelected(config.isOffsetLimit());
            commentBox.setSelected(config.isComment());
            overrideXMLBox.setSelected(config.isOverrideXML());
            overrideJavaBox.setSelected(config.isOverrideJava());
            needToStringHashcodeEqualsBox.setSelected(config.isNeedToStringHashcodeEquals());
            useSchemaPrefixBox.setSelected(config.isUseSchemaPrefix());
            needForUpdateBox.setSelected(config.isNeedForUpdate());
            annotationDAOBox.setSelected(config.isAnnotationDAO());
            useDAOExtendStyleBox.setSelected(config.isUseDAOExtendStyle());
            jsr310SupportBox.setSelected(config.isJsr310Support());
            annotationBox.setSelected(config.isAnnotation());
            useActualColumnNamesBox.setSelected(config.isUseActualColumnNames());
            useTableNameAliasBox.setSelected(config.isUseTableNameAlias());
            useExampleBox.setSelected(config.isUseExample());
            useLombokBox.setSelected(config.isUseLombokPlugin());
        }
        optionsPanel.add(useLombokBox);
        optionsPanel.add(commentBox);
        optionsPanel.add(overrideJavaBox);
        optionsPanel.add(useSchemaPrefixBox);
        optionsPanel.add(annotationDAOBox);
        optionsPanel.add(needToStringHashcodeEqualsBox);
        optionsPanel.add(useDAOExtendStyleBox);
        optionsPanel.add(jsr310SupportBox);
        optionsPanel.add(annotationBox);
        optionsPanel.add(useActualColumnNamesBox);
        optionsPanel.add(useTableNameAliasBox);
        optionsPanel.add(useExampleBox);
        optionsPanel.add(offsetLimitBox);
        optionsPanel.add(needForUpdateBox);
        return optionsPanel;
    }

    /**
     * historyConfig panel
     */
    private JPanel initHistoryConfigPanel() {
        this.getContentPane().add(Box.createVerticalStrut(10));
        final DefaultListModel defaultListModel = new DefaultListModel();

        if (historyConfigList == null) {
            historyConfigList = new HashMap<>();
        }
        for (String historyConfigName : historyConfigList.keySet()) {
            defaultListModel.addElement(historyConfigName);
        }
        Map<String, Config> finalHistoryConfigList = historyConfigList;

        final JBList configJBList = new JBList(defaultListModel);
        configJBList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        configJBList.setSelectedIndex(0);
        configJBList.setVisibleRowCount(25);
        JBScrollPane ScrollPane = new JBScrollPane(configJBList);


        JPanel btnPanel = new JPanel();
        btnPanel.setLayout(new BoxLayout(btnPanel, BoxLayout.X_AXIS));
        btnPanel.add(new JLabel("      "));//用来占位置
        btnPanel.add(deleteConfigBtn);
        configJBList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (configJBList.getSelectedIndex() != -1) {
                    if (e.getClickCount() == 2) { //双击事件
                        String configName = (String) configJBList.getSelectedValue();
                        Config selectedConfig = finalHistoryConfigList.get(configName);
                        modelPackageField.setText(selectedConfig.getModelPackage());
                        daoPackageField.setText(selectedConfig.getDaoPackage());
                        xmlPackageField.setText(selectedConfig.getXmlPackage());
                        projectFolderBtn.setText(selectedConfig.getProjectFolder());
                        offsetLimitBox.setSelected(selectedConfig.isOffsetLimit());
                        commentBox.setSelected(selectedConfig.isComment());
                        overrideXMLBox.setSelected(selectedConfig.isOverrideXML());
                        overrideJavaBox.setSelected(selectedConfig.isOverrideJava());
                        needToStringHashcodeEqualsBox.setSelected(selectedConfig.isNeedToStringHashcodeEquals());
                        useSchemaPrefixBox.setSelected(selectedConfig.isUseSchemaPrefix());
                        needForUpdateBox.setSelected(selectedConfig.isNeedForUpdate());
                        annotationDAOBox.setSelected(selectedConfig.isAnnotationDAO());
                        useDAOExtendStyleBox.setSelected(selectedConfig.isUseDAOExtendStyle());
                        jsr310SupportBox.setSelected(selectedConfig.isJsr310Support());
                        annotationBox.setSelected(selectedConfig.isAnnotation());
                        useActualColumnNamesBox.setSelected(selectedConfig.isUseActualColumnNames());
                        useTableNameAliasBox.setSelected(selectedConfig.isUseTableNameAlias());
                        useExampleBox.setSelected(selectedConfig.isUseExample());
                        useLombokBox.setSelected(selectedConfig.isUseLombokPlugin());
                    }
                }
            }
        });

        deleteConfigBtn.addActionListener(e -> {
            finalHistoryConfigList.remove(configJBList.getSelectedValue());
            defaultListModel.removeAllElements();
            for (String historyConfigName : finalHistoryConfigList.keySet()) {
                defaultListModel.addElement(historyConfigName);
            }
        });

        JPanel historyConfigPanel = new JPanel();
        historyConfigPanel.setLayout(new BoxLayout(historyConfigPanel, BoxLayout.Y_AXIS));
        historyConfigPanel.setPreferredSize(new Dimension(250, 0));
        historyConfigPanel.setBorder(BorderFactory.createTitledBorder("config history"));
        historyConfigPanel.add(ScrollPane);
        historyConfigPanel.add(btnPanel);
        return historyConfigPanel;
    }

}
