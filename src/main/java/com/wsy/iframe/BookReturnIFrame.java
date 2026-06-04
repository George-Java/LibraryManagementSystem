package com.wsy.iframe;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.beans.PropertyVetoException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.wsy.mapper.BorrowMapper;
import com.wsy.mapper.OperatorMapper;
import com.wsy.model.Borrow;
import com.wsy.model.Operator;
public class BookReturnIFrame {
    private JTable borrowTable;
    private DefaultTableModel tableModel;
    private JTextField readerIdField;
    private JTextField[] infoFields = new JTextField[5];
    private JTextField timeField;
    private JComboBox<String> operatorComboBox;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    @Autowired
    private BorrowMapper borrowMapper;
    @Autowired
    private OperatorMapper operatorMapper;
    public void setBorrowMapper(BorrowMapper borrowMapper) {
        this.borrowMapper = borrowMapper;
    }
    public void setOperatorMapper(OperatorMapper operatorMapper) {
        this.operatorMapper = operatorMapper;
    }
    private String getCurrentTime() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
    public JInternalFrame createBookReturnIFrame() throws PropertyVetoException {
        JInternalFrame frame = new JInternalFrame("图书归还管理", true, true, true, true);
        frame.setSize(820, 500);
        frame.setResizable(true);
        frame.setLayout(new BorderLayout());
        frame.setDefaultCloseOperation(JInternalFrame.DISPOSE_ON_CLOSE);
        frame.setMaximum(true);
        JPanel panelTop = new JPanel(new BorderLayout());
        panelTop.setPreferredSize(new Dimension(0, 270));
        JPanel readerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 8));
        readerPanel.add(createLabel("读者编号："));
        readerIdField = createText();
        readerIdField.setPreferredSize(new Dimension(180, 28));
        readerPanel.add(readerIdField);
        JButton filterBtn = new JButton("筛选");
        readerPanel.add(filterBtn);
        JButton showAllBtn = new JButton("全部未归还");
        readerPanel.add(showAllBtn);
        panelTop.add(readerPanel, BorderLayout.NORTH);
        String[] columnNames = {"借阅ID", "读者条码", "读者姓名", "图书名称", "操作员", "借阅日期", "允许借阅天数", "状态", "超期信息"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        borrowTable = new JTable(tableModel);
        borrowTable.setRowHeight(28);
        JScrollPane tableScroll = new JScrollPane(borrowTable);
        panelTop.add(tableScroll, BorderLayout.CENTER);
        loadBorrowData(null);
        filterBtn.addActionListener(e -> {
            String readerNum = readerIdField.getText().trim();
            loadBorrowData(readerNum.isEmpty() ? null : readerNum);
        });
        showAllBtn.addActionListener(e -> {
            readerIdField.setText("");
            loadBorrowData(null);
        });
        borrowTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) fillBorrowInfoFromTable();
        });
        JPanel infoPanel = new JPanel(new GridLayout(1, 2, 35, 8));
        JPanel leftGrid = new JPanel(new GridLayout(5, 2, 10, 10));
        String[] leftLabels = {"借书日期：", "规定天数：", "实际天数：", "超出天数：", "罚款金额："};
        for (int i = 0; i < 5; ++i) {
            leftGrid.add(createLabel(leftLabels[i]));
            infoFields[i] = createText();
            infoFields[i].setEditable(false);
            leftGrid.add(infoFields[i]);
        }
        infoPanel.add(leftGrid);
        JPanel rightGrid = new JPanel();
        rightGrid.setLayout(new BoxLayout(rightGrid, BoxLayout.Y_AXIS));
        JPanel fieldPanel = new JPanel(new GridLayout(2, 2, 7, 7));
        fieldPanel.add(createLabel("当前时间："));
        timeField = createText();
        timeField.setEditable(false);
        fieldPanel.add(timeField);
        fieldPanel.add(createLabel("操作员："));
        operatorComboBox = new JComboBox<>();
        operatorComboBox.setFont(new Font("楷体", Font.PLAIN, 14));
        fieldPanel.add(operatorComboBox);
        rightGrid.add(Box.createVerticalStrut(10));
        rightGrid.add(fieldPanel);
        rightGrid.add(Box.createVerticalStrut(18));
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 25, 0));
        JButton returnBtn = new JButton("图书归还");
        JButton exitBtn = new JButton("退出");
        btnPanel.add(returnBtn);
        btnPanel.add(exitBtn);
        rightGrid.add(btnPanel);
        infoPanel.add(rightGrid);
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, panelTop, infoPanel);
        splitPane.setDividerLocation(270);
        splitPane.setResizeWeight(0.7);
        frame.add(splitPane, BorderLayout.CENTER);
        loadOperatorUsernames();
        exitBtn.addActionListener(e -> frame.dispose());
        returnBtn.addActionListener(e -> {
            int row = borrowTable.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(frame, "请先选择一条需要归还的借阅记录", "提示", JOptionPane.WARNING_MESSAGE);
                return;
            }
            String selectedOperator = (String) operatorComboBox.getSelectedItem();
            if (selectedOperator == null || selectedOperator.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "请选择操作员！", "提示", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int borrowId = Integer.parseInt(borrowTable.getValueAt(row, 0).toString());
            int confirm = JOptionPane.showConfirmDialog(frame, "确认归还选中图书？", "确认归还", JOptionPane.YES_NO_OPTION);
            if (confirm != JOptionPane.YES_OPTION) return;
            try {
                processBookReturn(borrowId, selectedOperator, frame);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "数据库错误：" + ex.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
            }
        });
        frame.setVisible(true);
        return frame;
    }
    private void loadBorrowData(String readerNum) {
        tableModel.setRowCount(0);
        try {
            List<java.util.Map<String, Object>> borrowDetails = borrowMapper.selectBorrowDetails(readerNum);
            for (java.util.Map<String, Object> detail : borrowDetails) {
                Object[] row = new Object[9];
                row[0] = detail.get("借阅ID");
                row[1] = detail.get("读者条码");
                row[2] = detail.get("读者姓名");
                row[3] = detail.get("图书名称");
                row[4] = detail.get("操作员");
                Object borrowDate = detail.get("借阅日期");
                String formattedDate;
                if (borrowDate instanceof LocalDate) {
                    formattedDate = ((LocalDate) borrowDate).format(dateFormatter);
                } else if (borrowDate instanceof java.util.Date) {
                    java.util.Date date = (java.util.Date) borrowDate;
                    LocalDate localDate;
                    if (borrowDate instanceof java.sql.Date) {
                        localDate = new java.sql.Date(date.getTime()).toLocalDate();
                    } else {
                        localDate = date.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate();
                    }
                    formattedDate = localDate.format(dateFormatter);
                } else {
                    formattedDate = borrowDate != null ? borrowDate.toString() : "";
                }
                row[5] = formattedDate;
                row[6] = detail.get("允许借阅天数");
                row[7] = detail.get("状态");
                row[8] = detail.get("超期信息");
                tableModel.addRow(row);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "加载借阅记录失败：" + ex.getMessage(), "数据库错误", JOptionPane.ERROR_MESSAGE);
        }
    }
    private void fillBorrowInfoFromTable() {
        int row = borrowTable.getSelectedRow();
        if (row < 0) {
            clearInfoFields();
            return;
        }
        infoFields[0].setText(notNull(borrowTable.getValueAt(row, 5)));
        infoFields[1].setText(notNull(borrowTable.getValueAt(row, 6)));
        String borrowDate = notNull(borrowTable.getValueAt(row, 5));
        long days = 0;
        if (!borrowDate.isEmpty()) {
            try {
                java.time.LocalDate d = java.time.LocalDate.parse(borrowDate.substring(0, 10));
                days = java.time.temporal.ChronoUnit.DAYS.between(d, java.time.LocalDate.now());
            } catch (Exception ignore) {}
        }
        infoFields[2].setText(String.valueOf(days));
        int allowed = 0;
        try { allowed = Integer.parseInt(notNull(borrowTable.getValueAt(row, 6))); } catch (Exception ignore) {}
        long over = days - allowed;
        infoFields[3].setText(String.valueOf(Math.max(over, 0)));
        String overdueInfo = notNull(borrowTable.getValueAt(row, 8));
        String fine = "0";
        if (overdueInfo != null && overdueInfo.contains("罚金：")) {
            fine = overdueInfo.substring(overdueInfo.indexOf("罚金：") + 3).replace("元", "").replace("）", "").replace(")", "").trim();
            if (fine.isEmpty()) fine = "0";
        }
        infoFields[4].setText(fine);
        timeField.setText(getCurrentTime());
    }
    private void clearInfoFields() {
        for (JTextField field : infoFields) field.setText("");
        timeField.setText("");
    }
    private void loadOperatorUsernames() {
        operatorComboBox.removeAllItems();
        try {
            List<Operator> operators = operatorMapper.selectAll();
            for (Operator operator : operators) {
                operatorComboBox.addItem(operator.getUserName());
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "操作员用户名加载失败：" + ex.getMessage(), "数据库错误", JOptionPane.ERROR_MESSAGE);
        }
    }
    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("楷体", Font.BOLD, 15));
        return label;
    }
    private JTextField createText() {
        JTextField field = new JTextField();
        field.setFont(new Font("楷体", Font.PLAIN, 14));
        return field;
    }
    private String notNull(Object v) {
        return v == null ? "" : v.toString();
    }
    @Transactional(rollbackFor = Exception.class)
    private void processBookReturn(int borrowId, String selectedOperator, JInternalFrame frame) {
        try {
            List<Operator> operators = operatorMapper.selectByCondition("userName", selectedOperator);
            if (operators.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "操作员不存在！", "错误", JOptionPane.ERROR_MESSAGE);
                return;
            }
            int operatorId = operators.get(0).getId();
            Borrow borrow = borrowMapper.selectById(borrowId);
            if (borrow != null) {
                borrow.setIsReturn(1);
                borrow.setOperator(operatorId);
                borrowMapper.update(borrow);
                JOptionPane.showMessageDialog(frame, "图书归还成功！", "成功", JOptionPane.INFORMATION_MESSAGE);
                loadBorrowData(readerIdField.getText().trim().isEmpty() ? null : readerIdField.getText().trim());
                clearInfoFields();
            } else {
                JOptionPane.showMessageDialog(frame, "归还操作失败！", "错误", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame, "归还失败: " + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
            throw new RuntimeException(e); 
        }
    }
}