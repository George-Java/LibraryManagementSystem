package com.wsy.auxiliary;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.SimpleDateFormat;
import java.util.Date;

@org.springframework.stereotype.Component
public class AuxiliaryTools {
    public static void setWindowCenter(Component window) {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        if (window instanceof JFrame frame) {
            int x = (int) (screenSize.getWidth() - frame.getWidth()) / 2;
            int y = (int) (screenSize.getHeight() - frame.getHeight()) / 2;
            frame.setLocation(x, y);
        } else if (window instanceof JDesktopPane desktop) {
            int x = (int) (screenSize.getWidth() - desktop.getWidth()) / 2;
            int y = (int) (screenSize.getHeight() - desktop.getHeight()) / 2;
            desktop.setLocation(x, y);
        }
    }

    public static void JInternalFrameCenter(JInternalFrame jFrame) {
        int x = (jFrame.getDesktopPane().getWidth() - jFrame.getWidth()) / 2;
        int y = (jFrame.getDesktopPane().getHeight() - jFrame.getHeight()) / 2;
        jFrame.setLocation(x, y);
    }

    public static void initMenuBar(JMenuBar menuBar, String[] s) {
        for (String str : s) {
            JMenu menu = new JMenu(str);
            menuBar.add(menu);
        }
    }

    public static void addMenuToMenu(JMenu container, String[] s) {
        for (String str : s) {
            JMenu menu = new JMenu(str);
            container.add(menu);
        }
    }

    public static void addMenuItem(JMenu menu, String str) {
        menu.add(new JMenuItem(str));
    }

    public static void addMenuItems(JMenu container, String[] s) {
        for (String str : s) {
            JMenuItem menuItem = new JMenuItem(str);
            container.add(menuItem);
        }
    }

    public static JToolBar initJToolBar(String[] s) {
        JToolBar toolBar = new JToolBar();
        for (String str : s) {
            JButton button = new JButton(str);
            button.setSize(80, 40);
            toolBar.add(button);
            toolBar.addSeparator();
        }
        return toolBar;
    }

    public static JLabel createLabel(String str) {
        JLabel label = new JLabel(str);
        label.setFont(new Font("楷体", Font.BOLD, 16));
        return label;
    }

    public static JTextField createText() {
        JTextField textField = new JTextField();
        textField.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        return textField;
    }

    public static JPasswordField createPasswordField() {
        JPasswordField passwordField = new JPasswordField();
        passwordField.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        return passwordField;
    }

    public static JPanel createRadio(String s1, String s2) {
        JPanel panel = new JPanel();
        JRadioButton firstButton = new JRadioButton(s1);
        JRadioButton secondButton = new JRadioButton(s2);
        firstButton.setSelected(true);
        ButtonGroup gender = new ButtonGroup();
        gender.add(firstButton);
        gender.add(secondButton);
        panel.add(firstButton);
        panel.add(secondButton);
        return panel;
    }

    public static JComboBox<String> createComboBox(String[] str) {
        return createComboBox(str, false);
    }

    public static JComboBox<String> createComboBox(String[] str, boolean editable) {
        JComboBox<String> comboBox = new JComboBox<>(str);
        comboBox.setEditable(editable);
        return comboBox;
    }

    public static JPanel createButtons(String... labels) {
        JPanel panel = new JPanel();
        for (String label : labels) {
            JButton button = new JButton(label);
            button.setSize(80, 40);
            panel.add(button);
        }
        return panel;
    }

    public static void addClickListener(Component copt, Runnable action) {
        copt.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                action.run();
            }
        });
    }

    public static void addActionListener(JMenuItem menuItem, Runnable action) {
        menuItem.addActionListener(e -> action.run());
    }

    public static JTable createTable(Object[][] rowData, String[] columnNames) {
        return new JTable(rowData, columnNames);
    }

    public static JPanel createDatePicker() {
        JPanel panel = new JPanel(new BorderLayout());

        // 创建日期格式化文本框
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        JFormattedTextField dateTextField = new JFormattedTextField(dateFormat);
        dateTextField.setBorder(BorderFactory.createLineBorder(Color.BLACK));

        // 创建日历图标按钮
        JButton calendarButton = new JButton("📅");

        // 创建日期选择器（使用JSpinner实现）
        SpinnerDateModel dateModel = new SpinnerDateModel();
        JSpinner dateSpinner = new JSpinner(dateModel);
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(dateSpinner, "yyyy-MM-dd");
        dateSpinner.setEditor(dateEditor);

        // 将日期选择器添加到弹出菜单
        JPopupMenu popupMenu = new JPopupMenu();
        popupMenu.add(dateSpinner);

        // 按钮点击事件：显示日期选择器
        calendarButton.addActionListener(e -> {
            popupMenu.show(calendarButton, 0, calendarButton.getHeight());
        });

        // 日期选择器值变化事件：更新文本框
        dateSpinner.addChangeListener(e -> {
            Date selectedDate = (Date) dateSpinner.getValue();
            dateTextField.setText(dateFormat.format(selectedDate));
            popupMenu.setVisible(false);
        });

        // 组装面板
        panel.add(dateTextField, BorderLayout.CENTER);
        panel.add(calendarButton, BorderLayout.EAST);

        return panel;
    }
}