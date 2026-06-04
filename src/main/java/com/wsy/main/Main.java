package com.wsy.main;

import com.wsy.auxiliary.JImageView;
import com.wsy.iframe.*;
import com.wsy.mapper.OperatorMapper;
import com.wsy.model.Operator;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.beans.PropertyVetoException;
import java.io.IOException;
import java.util.Objects;

import static com.wsy.auxiliary.AuxiliaryTools.*;

public class Main {
    private JFrame home;
    private JPanel mainPanel;
    private JDesktopPane mainWindow;
    private JMenuBar menuBar;
    private ApplicationContext context;
    public static Operator currentOperator;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
            Main main = context.getBean(Main.class);
            main.context = context;
            main.showLogin();
        });
    }

    private void showLogin() {
        home = new JFrame("图书馆管理系统");
        home.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        home.setSize(560, 340);
        setWindowCenter(home);
        home.setLayout(new BorderLayout());
        JPanel loginPanel = createLoginPanel();
        home.setContentPane(loginPanel);
        home.setJMenuBar(null);
        home.setVisible(true);
    }

    /**
     * 通用方法：创建并显示内部框架（完整模式）
     */
    private <T> void createAndShowInternalFrame(T frameInstance, InternalFrameCreator<T> creator) {
        JInternalFrame internalFrame;
        try {
            internalFrame = creator.create(frameInstance);
        } catch (PropertyVetoException | IOException e) {
            throw new RuntimeException(e);
        }
        mainWindow.add(internalFrame);
        Dimension desktopSize = mainWindow.getSize();
        Dimension jInternalFrameSize = internalFrame.getSize();
        internalFrame.setLocation(
                (desktopSize.width - jInternalFrameSize.width) / 2,
                (desktopSize.height - jInternalFrameSize.height) / 2
        );
        internalFrame.setVisible(true);
        try {
            internalFrame.setSelected(true);
        } catch (PropertyVetoException e) {
            e.printStackTrace();
        }
    }

    /**
     * 通用方法：创建并显示内部框架（简化模式）
     */
    private <T> void createAndShowInternalFrameSimple(T frameInstance, InternalFrameCreator<T> creator) {
        JInternalFrame internalFrame;
        try {
            internalFrame = creator.create(frameInstance);
        } catch (PropertyVetoException | IOException e) {
            throw new RuntimeException(e);
        }
        mainWindow.add(internalFrame);
        JInternalFrameCenter(internalFrame);
    }

    /**
     * 内部框架创建器接口
     */
    private interface InternalFrameCreator<T> {
        JInternalFrame create(T instance) throws PropertyVetoException, IOException;
    }

    /**
     * 通用方法：为菜单项添加内部框架创建事件监听器（完整模式）
     */
    private <T> void addIFrameActionListener(JMenuItem menuItem, Class<T> frameClass, InternalFrameCreator<T> creator) {
        addActionListener(menuItem, () -> {
            T frameInstance = context.getBean(frameClass);
            createAndShowInternalFrame(frameInstance, creator);
        });
    }

    /**
     * 通用方法：为菜单项添加内部框架创建事件监听器（简化模式）
     */
    private <T> void addIFrameActionListenerSimple(JMenuItem menuItem, Class<T> frameClass, InternalFrameCreator<T> creator) {
        addActionListener(menuItem, () -> {
            T frameInstance = context.getBean(frameClass);
            createAndShowInternalFrameSimple(frameInstance, creator);
        });
    }

    /**
     * 通用方法：为工具栏按钮添加内部框架创建事件监听器（完整模式）
     */
    private <T> void addIFrameToolBarListener(int componentIndex, JToolBar toolBar, Class<T> frameClass, InternalFrameCreator<T> creator) {
        addClickListener(toolBar.getComponentAtIndex(componentIndex), () -> {
            T frameInstance = context.getBean(frameClass);
            createAndShowInternalFrame(frameInstance, creator);
        });
    }

    /**
     * 通用方法：为工具栏按钮添加内部框架创建事件监听器（简化模式）
     */
    private <T> void addIFrameToolBarListenerSimple(int componentIndex, JToolBar toolBar, Class<T> frameClass, InternalFrameCreator<T> creator) {
        addClickListener(toolBar.getComponentAtIndex(componentIndex), () -> {
            T frameInstance = context.getBean(frameClass);
            createAndShowInternalFrameSimple(frameInstance, creator);
        });
    }

    private JPanel createLoginPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        JPanel panelTop = new JPanel(new BorderLayout());
        panelTop.setPreferredSize(new Dimension(0, 150));
        String filePath = "res/rfwDB3L.png";
        JImageView imageView = new JImageView(filePath);
        panelTop.add(imageView, BorderLayout.CENTER);
        panel.add(panelTop, BorderLayout.NORTH);
        JPanel panelCenterWrap = new JPanel(new GridBagLayout());
        panelCenterWrap.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        panelCenterWrap.add(buildLoginForm(), gbc);
        panel.add(panelCenterWrap, BorderLayout.CENTER);
        JPanel panelBottom = createButtons("登录", "重置");
        panelBottom.setPreferredSize(new Dimension(0, 40));
        panel.add(panelBottom, BorderLayout.SOUTH);
        JTextField usernameField = usernameFieldRef;
        JPasswordField passwordField = passwordFieldRef;
        addClickListener(panelBottom.getComponent(0), () -> {
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword()).trim();
            if (username.isEmpty()) {
                JOptionPane.showMessageDialog(home, "用户名不能为空！", "错误", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (password.isEmpty()) {
                JOptionPane.showMessageDialog(home, "密码不能为空！", "错误", JOptionPane.ERROR_MESSAGE);
                return;
            }
            try {
                OperatorMapper operatorMapper = context.getBean(OperatorMapper.class);
                Operator operator = operatorMapper.login(username, password);
                if (operator != null) {
                    currentOperator = operator;
                    JOptionPane.showMessageDialog(home, "登录成功！");
                    showMainPanel();
                } else {
                    JOptionPane.showMessageDialog(home, "用户名或密码错误！", "登录失败", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(home, "登录失败: " + ex.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        });
        addClickListener(panelBottom.getComponent(1), () -> {
            usernameField.setText("");
            passwordField.setText("");
        });
        return panel;
    }

    private JTextField usernameFieldRef;
    private JPasswordField passwordFieldRef;

    private JPanel buildLoginForm() {
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(true);
        formPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        int maxInputWidth = 220;
        JLabel usernameLabel = createLabel("账号：");
        JLabel passwordLabel = createLabel("密码：");
        usernameFieldRef = createText();
        passwordFieldRef = createPasswordField();
        usernameFieldRef.setPreferredSize(new Dimension(maxInputWidth, 28));
        passwordFieldRef.setPreferredSize(new Dimension(maxInputWidth, 28));
        usernameFieldRef.setMinimumSize(new Dimension(120, 28));
        passwordFieldRef.setMinimumSize(new Dimension(120, 28));
        usernameFieldRef.setMaximumSize(new Dimension(maxInputWidth, 28));
        passwordFieldRef.setMaximumSize(new Dimension(maxInputWidth, 28));
        gbc.insets = new Insets(7, 7, 7, 7);
        gbc.anchor = GridBagConstraints.EAST;
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(usernameLabel, gbc);
        gbc.gridy = 1;
        formPanel.add(passwordLabel, gbc);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridx = 1;
        gbc.gridy = 0;
        formPanel.add(usernameFieldRef, gbc);
        gbc.gridy = 1;
        formPanel.add(passwordFieldRef, gbc);
        return formPanel;
    }

    private void showMainPanel() {
        home.setSize(1280, 720);
        setWindowCenter(home);
        if (mainPanel == null) {
            mainPanel = createMainPanel();
        }
        home.setJMenuBar(menuBar);
        home.setContentPane(mainPanel);
        home.validate();
        home.repaint();
    }

    private JPanel createMainPanel() {
        JPanel root = new JPanel(new BorderLayout());
        mainWindow = getJDesktopPane();
        setWindowCenter(mainWindow);
        menuBar = new JMenuBar();
        String[] menuBarLabels = {"基础数据维护", "新书订购管理", "借阅管理", "系统维护"};
        initMenuBar(menuBar, menuBarLabels);
        JMenu firstContainer = menuBar.getMenu(0);
        String[] firstContainerLabels = {"读者信息管理", "图书类别管理", "图书信息管理"};
        addMenuToMenu(firstContainer, firstContainerLabels);
        addMenuItem(firstContainer, "退出系统");
        String[] readerManageLabels = {"读者添加", "读者信息修改与删除"};
        addMenuItems((JMenu) firstContainer.getMenuComponent(0), readerManageLabels);
        JMenu readerInfoManage = (JMenu) firstContainer.getMenuComponent(0);
        addIFrameActionListener(readerInfoManage.getItem(0), ReaderAddIFrame.class, ReaderAddIFrame::createReaderAddIFrame);
        addIFrameActionListener(readerInfoManage.getItem(1), ReaderUpdateIFrame.class, ReaderUpdateIFrame::createReaderUpdateIFrame);
        String[] bookCategoryManageLabels = {"图书类别添加", "图书类别修改"};
        JMenu bookCategoryManage = (JMenu) firstContainer.getMenuComponent(1);
        addMenuItems(bookCategoryManage, bookCategoryManageLabels);
        addIFrameActionListenerSimple(bookCategoryManage.getItem(0), BookCategoryAddIFrame.class, BookCategoryAddIFrame::createBookCategoryAddIFrame);
        addIFrameActionListenerSimple(bookCategoryManage.getItem(1), BookCategoryUpdateIFrame.class, BookCategoryUpdateIFrame::createBookCategoryUpdateIFrame);
        String[] bookInfoManageLabels = {"图书添加", "图书信息修改与删除"};
        JMenu bookInfoManage = (JMenu) firstContainer.getMenuComponent(2);
        addMenuItems(bookInfoManage, bookInfoManageLabels);
        addIFrameActionListenerSimple(bookInfoManage.getItem(0), BookAdditionIFrame.class, BookAdditionIFrame::createAddBookIFrame);
        addIFrameActionListenerSimple(bookInfoManage.getItem(1), BookUpdateIFrame.class, BookUpdateIFrame::createBookUpdateIFrame);
        addActionListener(firstContainer.getItem(3), () -> {
            int op = JOptionPane.showConfirmDialog(home, "确定要退出系统吗？", "退出", JOptionPane.YES_NO_OPTION);
            if (op == JOptionPane.YES_OPTION) {
                System.exit(0);
            }
        });
        JMenu secondContainer = menuBar.getMenu(1);
        String[] secondContainerLabels = {"新书订购", "新书验收"};
        addMenuItems(secondContainer, secondContainerLabels);
        addIFrameActionListenerSimple(secondContainer.getItem(0), BookOrderingIFrame.class, BookOrderingIFrame::createBookOrderingIFrame);
        addIFrameActionListenerSimple(secondContainer.getItem(1), BookAcceptanceIFrame.class, BookAcceptanceIFrame::createBookAcceptanceIFrame);
        JMenu thirdContainer = menuBar.getMenu(2);
        String[] thirdContainerLabels = {"图书借阅", "图书归还", "图书搜索"};
        addMenuItems(thirdContainer, thirdContainerLabels);
        addIFrameActionListenerSimple(thirdContainer.getItem(0), BookBorrowingIFrame.class, BookBorrowingIFrame::createBookBorrowIFrame);
        addIFrameActionListenerSimple(thirdContainer.getItem(1), BookReturnIFrame.class, BookReturnIFrame::createBookReturnIFrame);
        addIFrameActionListenerSimple(thirdContainer.getItem(2), BookSearchIFrame.class, BookSearchIFrame::createBookSearchIFrame);
        JMenu fourthContainer = menuBar.getMenu(3);
        String[] fourthContainerLabels = {"用户管理"};
        addMenuToMenu(fourthContainer, fourthContainerLabels);
        String[] userManageLabels = {"用户添加", "用户修改与删除"};
        JMenu userManagement = (JMenu) fourthContainer.getMenuComponent(0);
        addMenuItems(userManagement, userManageLabels);
        JMenuItem addUserItem = userManagement.getItem(0);
        JMenuItem updateUserItem = userManagement.getItem(1);
        addUserItem.setEnabled(currentOperator.isAdmin());
        updateUserItem.setEnabled(currentOperator.isAdmin());
        addIFrameActionListenerSimple(addUserItem, UserAdditionIFrame.class, UserAdditionIFrame::createAddUserIFrame);
        addIFrameActionListenerSimple(updateUserItem, UserUpdateIFrame.class, UserUpdateIFrame::createUpdateUserIFrame);
        addMenuItem(fourthContainer, "更改密码");
        addIFrameActionListenerSimple(fourthContainer.getItem(1), PasswordChangeIFrame.class, PasswordChangeIFrame::createPasswordChangeIFrame);
        String[] toolBarLabels = {"图书添加", "图书信息修改与删除", "图书类别添加",
                "图书借阅", "图书订购", "图书验收",
                "添加读者", "读者修改与删除", "退出系统"};
        JToolBar toolBar = initJToolBar(toolBarLabels);

        // 图书添加
        addIFrameToolBarListenerSimple(0, toolBar, BookAdditionIFrame.class, BookAdditionIFrame::createAddBookIFrame);

        // 图书信息修改与删除
        addIFrameToolBarListenerSimple(2, toolBar, BookUpdateIFrame.class, BookUpdateIFrame::createBookUpdateIFrame);

        // 图书类别添加
        addIFrameToolBarListenerSimple(4, toolBar, BookCategoryAddIFrame.class, BookCategoryAddIFrame::createBookCategoryAddIFrame);

        // 图书借阅
        addIFrameToolBarListenerSimple(6, toolBar, BookBorrowingIFrame.class, BookBorrowingIFrame::createBookBorrowIFrame);

        // 图书订购
        addIFrameToolBarListenerSimple(8, toolBar, BookOrderingIFrame.class, BookOrderingIFrame::createBookOrderingIFrame);

        // 图书验收
        addIFrameToolBarListenerSimple(10, toolBar, BookAcceptanceIFrame.class, BookAcceptanceIFrame::createBookAcceptanceIFrame);

        // 添加读者
        addIFrameToolBarListener(12, toolBar, ReaderAddIFrame.class, ReaderAddIFrame::createReaderAddIFrame);

        // 读者修改与删除
        addIFrameToolBarListener(14, toolBar, ReaderUpdateIFrame.class, ReaderUpdateIFrame::createReaderUpdateIFrame);

        // 退出系统
        addClickListener(toolBar.getComponentAtIndex(16), () -> {
            int op = JOptionPane.showConfirmDialog(home, "确定要退出系统吗？", "退出", JOptionPane.YES_NO_OPTION);
            if (op == JOptionPane.YES_OPTION) {
                System.exit(0);
            }
        });
        root.add(toolBar, BorderLayout.NORTH);
        root.add(mainWindow, BorderLayout.CENTER);
        return root;
    }

    private JDesktopPane getJDesktopPane() {
        JDesktopPane mainWindow = new JDesktopPane() {
            private Image background;

            {
                try {
                    background = ImageIO.read(Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream("res/Main.png")));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (background != null) {
                    g.drawImage(background, 0, 0, getWidth(), getHeight(), this);
                }
            }
        };
        mainWindow.setSize(1280, 720);
        return mainWindow;
    }
}