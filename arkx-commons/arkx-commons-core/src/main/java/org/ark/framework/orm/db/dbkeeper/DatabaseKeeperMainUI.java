// package org.ark.framework.orm.db.dbkeeper;
//
// import java.awt.BorderLayout;
// import java.awt.Color;
// import java.awt.event.ActionEvent;
// import java.awt.event.ActionListener;
// import java.awt.event.WindowAdapter;
// import java.awt.event.WindowEvent;
// import java.io.File;
// import java.io.FileInputStream;
// import java.io.FileNotFoundException;
// import java.io.FileOutputStream;
// import java.io.IOException;
// import java.io.InputStream;
// import java.io.OutputStream;
// import java.text.SimpleDateFormat;
// import java.util.Date;
// import java.util.Properties;
//
// import javax.swing.JButton;
// import javax.swing.JCheckBox;
// import javax.swing.JFrame;
// import javax.swing.JLabel;
// import javax.swing.JPanel;
// import javax.swing.JTextField;
//
// public class DatabaseKeeperMainUI extends JFrame implements
// IGenerateConfigProvider {
// /**
// *
// */
// private static final long serialVersionUID = 1L;
// private JCheckBox ckCreatePackage;
// Properties dbConfigProperties = new Properties();
// String configFile = "config.ini";
// private JLabel lblMessage; // “执行“按钮旁边的提示文字框
//
// public DatabaseKeeperMainUI() { // swing 初始化界面
//
// setResizable(false);
//
// setTitle("MySQL生成javabean小工具");
// setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
//
// setBounds(100, 100, 484, 324);
//
// JPanel panel = new JPanel();
// getContentPane().add(panel, BorderLayout.CENTER);
// panel.setLayout(null);
//
// txtLocalhost = new JTextField();
// txtLocalhost.setBounds(146, 10, 147, 21);
// panel.add(txtLocalhost);
// txtLocalhost.setColumns(10);
//
// JLabel lblIp = new JLabel("IP:");
// lblIp.setBounds(80, 13, 30, 15);
// panel.add(lblIp);
//
// JLabel label = new JLabel("数据库:");
// label.setBounds(80, 42, 54, 15);
// panel.add(label);
//
// txtDatabase = new JTextField();
// txtDatabase.setBounds(146, 39, 147, 21);
// panel.add(txtDatabase);
// txtDatabase.setColumns(10);
//
// JLabel label_1 = new JLabel("表名:");
// label_1.setBounds(80, 127, 54, 15);
// panel.add(label_1);
//
// txtTableNames = new JTextField();
// txtTableNames.setBounds(146, 124, 147, 21);
// panel.add(txtTableNames);
// txtTableNames.setColumns(10);
//
// JLabel label_2 = new JLabel("包名:");
// label_2.setBounds(79, 156, 54, 15);
// panel.add(label_2);
//
// txtPackageName = new JTextField();
// txtPackageName.setBounds(146, 155, 147, 21);
// panel.add(txtPackageName);
// txtPackageName.setColumns(10);
//
// JLabel lblNewLabel = new JLabel("输出目录：");
// lblNewLabel.setBounds(80, 190, 65, 15);
// panel.add(lblNewLabel);
//
// txtDir = new JTextField();
// txtDir.setBounds(146, 186, 147, 21);
// panel.add(txtDir);
// txtDir.setColumns(10);
//
// ckCreatePackage = new JCheckBox("生成包结构目录");
// ckCreatePackage.setSelected(true);
// ckCreatePackage.setBounds(145, 213, 147, 23);
// panel.add(ckCreatePackage);
//
// JLabel lblNewLabel_1 = new JLabel("可以指定表名，也可以不指定");
// lblNewLabel_1.setBounds(303, 127, 176, 15);
// panel.add(lblNewLabel_1);
//
// JLabel lblNewLabel_2 = new JLabel("* 数据库名");
// lblNewLabel_2.setForeground(Color.RED);
// lblNewLabel_2.setBounds(303, 42, 66, 15);
// panel.add(lblNewLabel_2);
//
// JLabel lblNewLabel_3 = new JLabel("* 包结构");
// lblNewLabel_3.setForeground(Color.RED);
// lblNewLabel_3.setBounds(303, 158, 79, 15);
// panel.add(lblNewLabel_3);
//
// JButton button = new JButton("执行");
// button.addActionListener(new ActionListener() {
// public void actionPerformed(ActionEvent e) {
// go();
// }
// });
// button.setBounds(145, 242, 93, 23);
// panel.add(button);
//
// txtPassword = new JTextField();
// txtPassword.setBounds(145, 93, 147, 21);
// panel.add(txtPassword);
// txtPassword.setColumns(10);
//
// txtUserName = new JTextField();
// txtUserName.setText("root");
// txtUserName.setBounds(145, 66, 148, 21);
// panel.add(txtUserName);
// txtUserName.setColumns(10);
//
// JLabel label_3 = new JLabel("用户名:");
// label_3.setBounds(80, 69, 54, 15);
// panel.add(label_3);
//
// JLabel label_4 = new JLabel("密码:");
// label_4.setBounds(80, 96, 54, 15);
// panel.add(label_4);
//
// lblMessage = new JLabel("");
// lblMessage.setForeground(Color.RED);
// lblMessage.setBounds(248, 242, 204, 23);
// panel.add(lblMessage);
//
// addWindowListener(new WindowAdapter() {
//
// public void windowClosing(WindowEvent e) {
// super.windowClosing(e);
// export(); // *a*
// System.exit(0);
// }
//
// });
//
// inport(); // *2*
// }
//
// private JTextField txtLocalhost;
// private JTextField txtDatabase;
// private JTextField txtTableNames;
// private JTextField txtPackageName;
// private JTextField txtDir;
// private JTextField txtPassword;
// private JTextField txtUserName;
//
// private void inport() { // *2*
// File config = new File(configFile);
// System.out.println(config.getAbsolutePath());
// if (config.exists()) {
// try {
// // 设置 config 文件
// InputStream is = new FileInputStream(config);
// dbConfigProperties.load(is);
// is.close();
// } catch (FileNotFoundException e) {
// e.printStackTrace();
// } catch (IOException e) {
// e.printStackTrace();
// }
// } else {
// try {
// config.createNewFile();
// } catch (IOException e) {
// e.printStackTrace();
// }
// }
//
// setUIVal();
//
// }
//
// private void setUIVal() {
// txtLocalhost.setText(dbConfigProperties.getProperty("host",
// "192.168.1.117"));
// txtDatabase.setText(dbConfigProperties.getProperty("database", "ark"));
// txtUserName.setText(dbConfigProperties.getProperty("user", "root"));
// txtPassword.setText(dbConfigProperties.getProperty("pass", "darkness"));
// txtPackageName.setText(dbConfigProperties.getProperty("packname",
// "org.ark"));
// txtDir.setText(dbConfigProperties.getProperty("dirstr", "C:/Documents and
// Settings/kokrange/Desktop/Tst"));
// txtTableNames.setText(dbConfigProperties.getProperty("tablename", ""));
// }
//
// private void export() {
// String host = txtLocalhost.getText();
// String database = txtDatabase.getText();
// String user = txtUserName.getText();
// String pass = txtPassword.getText();
// String packname = txtPackageName.getText();
// String dirstr = txtDir.getText();// 空表示当前目录
// String tablename = txtTableNames.getText();
//
// dbConfigProperties.setProperty("host", host);
// dbConfigProperties.setProperty("database", database);
// dbConfigProperties.setProperty("user", user);
// dbConfigProperties.setProperty("pass", pass);
// dbConfigProperties.setProperty("packname", packname);
// dbConfigProperties.setProperty("dirstr", dirstr);
// dbConfigProperties.setProperty("tablename", tablename);
//
// try {
// OutputStream out = new FileOutputStream(configFile);
// dbConfigProperties.store(out, "退出保存文件," + sdf.format(new Date()));
// } catch (FileNotFoundException e) {
// e.printStackTrace();
// } catch (IOException e) {
// e.printStackTrace();
// }
//
// }
//
// static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//
// public void setTips(String msg) {
// lblMessage.setText(msg);
// }
//
// public String getHost() {
// return txtLocalhost.getText();
// }
//
// public String getDatabase() {
// return txtDatabase.getText();
// }
//
// public String getUserName() {
// return txtUserName.getText();
// }
//
// public String getPassword() {
// return txtPassword.getText();
// }
//
// public String getTableNames() {
// return txtTableNames.getText();
// }
//
// public String getPackageName() {
// return txtPackageName.getText();
// }
//
// public String getDir() {
// return txtDir.getText();// 空表示当前目录
// }
//
// public boolean isCreatePackage() {
// return ckCreatePackage.getSelectedObjects() != null;
// }
//
// private void go() {
//
// String database = txtDatabase.getText();
//
// if (database.length() == 0) {
// setTips("数据库名必填");
// return;
// }
//
// try {
// new GenerateService(this).generate();
// } catch (ServiceException e) {
// setTips(e.getMessage());
// }
// }
//
// }
