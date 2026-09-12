package com.wsy.iframe;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.beans.PropertyVetoException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Vector;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import static com.wsy.auxiliary.AuxiliaryTools.setWindowCenter;
import com.wsy.mapper.OperatorMapper;
import com.wsy.model.Operator;
@Service
@RequiredArgsConstructor
@Slf4j
public class UserUpdateIFrame {
    private JTable userTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JComboBox<String> searchTypeCombo;
    private final OperatorMapper operatorMapper;
    public JInternalFrame createUpdateUserIFrame() throws PropertyVetoException {
        JInternalFrame frame = new JInternalFrame("用户管理", true, true, true, true);
        frame.setLayout(new BorderLayout());
        frame.setSize(900, 550);
        frame.getContentPane().setBackground(new Color(245, 251, 255));
        frame.setDefaultCloseOperation(JInternalFrame.DISPOSE_ON_CLOSE);
        frame.setMaximum(true);
        JPanel toolPanel = createToolPanel();
        frame.add(toolPanel, BorderLayout.NORTH);
        createTable();
        JPanel buttonPanel = createButtonPanel();
        JScrollPane scrollPane = new JScrollPane(userTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        frame.add(scrollPane, BorderLayout.CENTER);
        frame.add(buttonPanel, BorderLayout.SOUTH);
        loadUserData("");
        frame.setVisible(true);
        return frame;
    }
    private JPanel createToolPanel() {
        JPanel toolPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        toolPanel.setBackground(new Color(220, 230, 240));
        toolPanel.setBorder(new EmptyBorder(5, 15, 5, 15));
        toolPanel.add(new JLabel("搜索类型:"));
        String[] searchTypes = {"全部", "编号", "姓名", "用户名", "身份证号"};
        searchTypeCombo = new JComboBox<>(searchTypes);
        searchTypeCombo.setFont(new Font("楷体", Font.PLAIN, 14));
        searchTypeCombo.setPreferredSize(new Dimension(100, 30));
        toolPanel.add(searchTypeCombo);
        toolPanel.add(new JLabel("搜索内容:"));
        searchField = new JTextField(20);
        searchField.setFont(new Font("楷体", Font.PLAIN, 14));
        searchField.setToolTipText("输入搜索内容后按Enter");
        toolPanel.add(searchField);
        JButton searchButton = new JButton("搜索");
        styleButton(searchButton, new Color(70, 130, 180));
        searchButton.addActionListener(e -> {
            String searchText = searchField.getText().trim();
            loadUserData(searchText);
        });
        searchField.addActionListener(e -> {
            String searchText = searchField.getText().trim();
            loadUserData(searchText);
        });
        toolPanel.add(searchButton);
        return toolPanel;
    }
    private void createTable() {
        String[] columnNames = {"编号", "姓名", "性别", "年龄", "电话", "身份证号", "工作日期", "管理员", "用户名"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; 
            }
        };
        userTable = new JTable(tableModel);
        userTable.setFont(new Font("楷体", Font.PLAIN, 14));
        userTable.setRowHeight(30);
        userTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        userTable.setAutoCreateRowSorter(true);
        userTable.getTableHeader().setReorderingAllowed(false);
        JTableHeader tableHeader = userTable.getTableHeader();
        tableHeader.setFont(new Font("楷体", Font.BOLD, 14));
        tableHeader.setBackground(new Color(70, 130, 180));
        tableHeader.setForeground(Color.WHITE);
        tableHeader.setReorderingAllowed(false);
        userTable.setRowHeight(28);
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < userTable.getColumnCount(); i++) {
            userTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
        userTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    updateSelectedUser();
                }
            }
        });
    }
    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 40, 15));
        buttonPanel.setBackground(new Color(245, 251, 255));
        JButton updateButton = new JButton("修改用户");
        styleButton(updateButton, new Color(25, 118, 210));
        updateButton.addActionListener(e -> updateSelectedUser());
        JButton deleteButton = new JButton("删除用户");
        styleButton(deleteButton, new Color(211, 47, 47));
        deleteButton.addActionListener(e -> deleteSelectedUser());
        JButton refreshButton = new JButton("刷新数据");
        styleButton(refreshButton, new Color(120, 144, 156));
        refreshButton.addActionListener(e -> loadUserData(""));
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(refreshButton);
        return buttonPanel;
    }
    private void styleButton(JButton button, Color bgColor) {
        button.setFont(new Font("楷体", Font.BOLD, 14));
        button.setPreferredSize(new Dimension(120, 35));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(bgColor.darker(), 1),
                BorderFactory.createEmptyBorder(5, 15, 5, 15)
        ));
    }
    private void loadUserData(String searchText) {
        tableModel.setRowCount(0); 
        List<Operator> operators;
        int searchType = searchTypeCombo.getSelectedIndex();
        if (!searchText.isEmpty() && searchType > 0) {
            String[] columns = {"id", "name", "userName", "identityCard"};
            String column = columns[searchType - 1];
            operators = operatorMapper.selectByCondition(column, searchText);
        } else {
            operators = operatorMapper.selectAll();
        }
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        for (Operator operator : operators) {
            Vector<Object> row = new Vector<>();
            row.add(operator.getId());
            row.add(operator.getName());
            row.add(operator.getSex());
            row.add(operator.getAge());
            row.add(operator.getPhone());
            row.add(operator.getIdentityCard());
            row.add(operator.getWorkDate() != null ? dateFormatter.format(operator.getWorkDate()) : "");
            row.add(operator.isAdmin() ? "是" : "否");
            row.add(operator.getUserName());
            tableModel.addRow(row);
        }
    }
    private void updateSelectedUser() {
        int selectedRow = userTable.getSelectedRow();
        if (selectedRow == -1) {
            showInfo();
            return;
        }
        int userId = (int) userTable.getValueAt(selectedRow, 0);
        Operator operator = operatorMapper.selectById(userId);
        if (operator != null) {
            JDialog editDialog = new JDialog((Frame) null, "修改用户信息", true);
            editDialog.setLayout(new BorderLayout());
            editDialog.setSize(500, 600);
            editDialog.setResizable(false);
            setWindowCenter(editDialog);
            JPanel titlePanel = new JPanel();
            titlePanel.setBackground(new Color(70, 130, 180));
            titlePanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
            JLabel titleLabel = new JLabel("修改用户信息 - ID: " + userId);
            titleLabel.setFont(new Font("楷体", Font.BOLD, 18));
            titleLabel.setForeground(Color.WHITE);
            titlePanel.add(titleLabel);
            editDialog.add(titlePanel, BorderLayout.NORTH);
            JPanel formPanel = new JPanel(new GridBagLayout());
            formPanel.setBorder(new EmptyBorder(20, 30, 20, 30));
            formPanel.setBackground(new Color(245, 251, 255));
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(8, 8, 8, 8);
            gbc.anchor = GridBagConstraints.LINE_START;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            String[] labels = {"编号：", "姓名：", "性别：", "年龄：", "电话：", "身份证号：",
                    "工作日期：", "是否为管理员：", "用户名："};
            JTextField[] fields = new JTextField[labels.length];
            gbc.gridx = 0;
            gbc.gridy = 0;
            formPanel.add(createLabel(labels[0]), gbc);
            gbc.gridx = 1;
            JTextField idField = new JTextField(String.valueOf(operator.getId()));
            idField.setEditable(false);
            idField.setFont(new Font("楷体", Font.PLAIN, 14));
            formPanel.add(idField, gbc);
            gbc.gridx = 0;
            gbc.gridy = 1;
            formPanel.add(createLabel(labels[1]), gbc);
            gbc.gridx = 1;
            fields[1] = new JTextField(operator.getName());
            fields[1].setFont(new Font("楷体", Font.PLAIN, 14));
            formPanel.add(fields[1], gbc);
            gbc.gridx = 0;
            gbc.gridy = 2;
            formPanel.add(createLabel(labels[2]), gbc);
            gbc.gridx = 1;
            JComboBox<String> genderCombo = new JComboBox<>(new String[] {"男", "女"});
            genderCombo.setSelectedItem(operator.getSex());
            genderCombo.setFont(new Font("楷体", Font.PLAIN, 14));
            genderCombo.setEnabled(false); 
            formPanel.add(genderCombo, gbc);
            gbc.gridx = 0;
            gbc.gridy = 3;
            formPanel.add(createLabel(labels[3]), gbc);
            gbc.gridx = 1;
            fields[3] = new JTextField(String.valueOf(operator.getAge()));
            fields[3].setFont(new Font("楷体", Font.PLAIN, 14));
            fields[3].setEditable(false); 
            formPanel.add(fields[3], gbc);
            gbc.gridx = 0;
            gbc.gridy = 4;
            formPanel.add(createLabel(labels[4]), gbc);
            gbc.gridx = 1;
            fields[4] = new JTextField(operator.getPhone());
            fields[4].setFont(new Font("楷体", Font.PLAIN, 14));
            formPanel.add(fields[4], gbc);
            gbc.gridx = 0;
            gbc.gridy = 5;
            formPanel.add(createLabel(labels[5]), gbc);
            gbc.gridx = 1;
            fields[5] = new JTextField(operator.getIdentityCard());
            fields[5].setFont(new Font("楷体", Font.PLAIN, 14));
            fields[5].setEditable(false); 
            formPanel.add(fields[5], gbc);
            gbc.gridx = 0;
            gbc.gridy = 6;
            formPanel.add(createLabel(labels[6]), gbc);
            gbc.gridx = 1;
            JXDatePicker workDatePicker = new JXDatePicker();
            if (operator.getWorkDate() != null) {
                workDatePicker.setDate(java.util.Date.from(operator.getWorkDate().atStartOfDay(java.time.ZoneId.systemDefault()).toInstant()));
            }
            workDatePicker.setFormats("yyyy-MM-dd");
            formPanel.add(workDatePicker, gbc);
            gbc.gridx = 0;
            gbc.gridy = 7;
            formPanel.add(createLabel(labels[7]), gbc);
            gbc.gridx = 1;
            JComboBox<String> adminCombo = new JComboBox<>(new String[]{"是", "否"});
            adminCombo.setSelectedItem(operator.isAdmin() ? "是" : "否");
            adminCombo.setFont(new Font("楷体", Font.PLAIN, 14));
            formPanel.add(adminCombo, gbc);
            gbc.gridx = 0;
            gbc.gridy = 8;
            formPanel.add(createLabel(labels[8]), gbc);
            gbc.gridx = 1;
            fields[8] = new JTextField(operator.getUserName());
            fields[8].setFont(new Font("楷体", Font.PLAIN, 14));
            formPanel.add(fields[8], gbc);
            gbc.gridx = 0;
            gbc.gridy = 9;
            formPanel.add(new JLabel("当前密码："), gbc);
            gbc.gridx = 1;
            JPasswordField passwordField = new JPasswordField();
            passwordField.setFont(new Font("楷体", Font.PLAIN, 14));
            passwordField.setToolTipText("请输入您的当前密码");
            formPanel.add(passwordField, gbc);
            gbc.gridx = 0;
            gbc.gridy = 10;
            formPanel.add(new JLabel("确认当前密码："), gbc);
            gbc.gridx = 1;
            JPasswordField confirmPasswordField = new JPasswordField();
            confirmPasswordField.setFont(new Font("楷体", Font.PLAIN, 14));
            confirmPasswordField.setToolTipText("请再次输入您的当前密码");
            formPanel.add(confirmPasswordField, gbc);
            editDialog.add(formPanel, BorderLayout.CENTER);
            JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 15));
            btnPanel.setBackground(new Color(245, 251, 255));
            JButton saveButton = new JButton("保存");
            styleButton(saveButton, new Color(25, 118, 210));
            saveButton.addActionListener(e -> saveUserChanges(
                    userId,
                    fields[1].getText().trim(),
                    (String) genderCombo.getSelectedItem(),
                    fields[3].getText().trim(),
                    fields[4].getText().trim(),
                    fields[5].getText().trim(),
                    workDatePicker.getDate(),
                    (String) adminCombo.getSelectedItem(),
                    fields[8].getText().trim(),
                    passwordField.getPassword(),
                    confirmPasswordField.getPassword(),
                    editDialog
            ));
            JButton cancelButton = new JButton("取消");
            styleButton(cancelButton, new Color(120, 144, 156));
            cancelButton.addActionListener(e -> editDialog.dispose());
            btnPanel.add(saveButton);
            btnPanel.add(cancelButton);
            editDialog.add(btnPanel, BorderLayout.SOUTH);
            editDialog.setVisible(true);
        }
    }
    private void saveUserChanges(int userId, String name, String gender, String ageStr,
                                 String phone, String idCard, java.util.Date workDate,
                                 String adminValue, String username,
                                 char[] password, char[] confirmPassword,
                                 JDialog dialog) {
        if (name.isEmpty() || ageStr.isEmpty() || idCard.isEmpty() || username.isEmpty()) {
            showError("姓名、年龄、身份证号和用户名不能为空！", "输入错误");
            return;
        }
        int age;
        try {
            age = Integer.parseInt(ageStr);
            if (age < 18 || age > 65) {
                showError("年龄必须在18-65岁之间", "输入错误");
                return;
            }
        } catch (NumberFormatException e) {
            showError("年龄必须为有效的整数", "输入错误");
            return;
        }
        if (!phone.isEmpty() && !phone.matches("\\d{11}")) {
            showError("电话必须是11位数字", "输入错误");
            return;
        }
        if (!idCard.matches("\\d{17}[\\dX]")) {
            showError("身份证号格式不正确（应为18位数字或最后一位X）", "输入错误");
            return;
        }
        String inputPassword = String.valueOf(password);
        String confirmInputPassword = String.valueOf(confirmPassword);
        if (inputPassword.isEmpty() || confirmInputPassword.isEmpty()) {
            showError("当前密码不能为空", "验证失败");
            return;
        }
        if (!inputPassword.equals(confirmInputPassword)) {
            showError("两次输入的密码不一致", "验证失败");
            return;
        }
        boolean isAdmin = "是".equals(adminValue);
        try {
            Operator currentOperator = operatorMapper.selectById(userId);
            if (currentOperator == null) {
                showError("用户不存在", "更新失败");
                return;
            }
            String checkPassword = String.valueOf(password);
            if (!checkPassword.equals(currentOperator.getPassword())) {
                showError("当前密码输入错误", "验证失败");
                return;
            }
            Operator operator = new Operator();
            operator.setId(userId);
            operator.setName(name);
            operator.setSex(gender);
            operator.setAge(age);
            operator.setPhone(phone);
            operator.setIdentityCard(idCard);
            if (workDate != null) {
                LocalDate localWorkDate;
                if (workDate instanceof java.sql.Date sqlDate) {
                    localWorkDate = sqlDate.toLocalDate();
                } else {
                    localWorkDate = workDate.toInstant()
                            .atZone(java.time.ZoneId.systemDefault())
                            .toLocalDate();
                }
                operator.setWorkDate(localWorkDate);
            } else {
                operator.setWorkDate(null);
            }
            operator.setAdmin(isAdmin);
            operator.setUserName(username);
            operator.setPassword(currentOperator.getPassword()); 
            int rowsAffected = operatorMapper.update(operator);
            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(dialog,
                        "<html><b>用户信息更新成功！</b><br><br>" +
                                "ID: " + userId + "<br>" +
                                "姓名: " + name + "<br>" +
                                "用户名: " + username + "</html>",
                        "更新成功", JOptionPane.INFORMATION_MESSAGE);
                loadUserData(""); 
                dialog.dispose(); 
            } else {
                showError("没有更新任何记录", "更新失败");
            }
        } catch (Exception ex) {
            String message = ex.getMessage();
            if (message != null && message.contains("Duplicate entry")) {
                showError("用户名或身份证号已存在！", "唯一性冲突");
            } else {
                showError("更新失败: " + (message != null ? message : "未知错误"), "数据库错误");
            }
            log.error("更新用户失败", ex);
        }
    }
    private void deleteSelectedUser() {
        int selectedRow = userTable.getSelectedRow();
        if (selectedRow == -1) {
            showInfo();
            return;
        }
        int userId = (int) userTable.getValueAt(selectedRow, 0);
        String userName = (String) userTable.getValueAt(selectedRow, 1);
        String username = (String) userTable.getValueAt(selectedRow, 8);
        int confirm = JOptionPane.showConfirmDialog(null,
                "<html><b>确定要删除用户吗？</b><br><br>" +
                        "ID: " + userId + "<br>" +
                        "姓名: " + userName + "<br>" +
                        "用户名: " + username + "</html>",
                "确认删除", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                int affectedRows = operatorMapper.deleteById(userId);
                if (affectedRows > 0) {
                    JOptionPane.showMessageDialog(null,
                            "<html><b>用户删除成功！</b><br><br>" +
                                    "ID: " + userId + "<br>" +
                                    "姓名: " + userName + "</html>",
                            "删除成功", JOptionPane.INFORMATION_MESSAGE);
                    loadUserData(""); 
                } else {
                    showError("没有删除任何记录", "删除失败");
                }
            } catch (Exception ex) {
                if (ex.getMessage().contains("foreign key constraint fails")) {
                    showError("该用户有相关操作记录，无法删除！", "操作失败");
                } else {
                    showError("删除失败: " + ex.getMessage(), "数据库错误");
                }
                log.error("删除用户失败", ex);
            }
        }
    }
    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("楷体", Font.BOLD, 14));
        return label;
    }
    private void showError(String message, String title) {
        JOptionPane.showMessageDialog(null, message, title, JOptionPane.ERROR_MESSAGE);
    }
    private void showInfo() {
        JOptionPane.showMessageDialog(null, "请先选择一个用户", "提示", JOptionPane.INFORMATION_MESSAGE);
    }
    static class JXDatePicker extends JPanel {
        private final JTextField dateField;
        private java.util.Date selectedDate;
        public JXDatePicker() {
            setLayout(new BorderLayout());
            setBackground(Color.WHITE);
            dateField = new JTextField(10);
            dateField.setFont(new Font("楷体", Font.PLAIN, 14));
            dateField.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(180, 180, 180)),
                    BorderFactory.createEmptyBorder(5, 8, 5, 8)
            ));
            add(dateField, BorderLayout.CENTER);
            JButton pickButton = new JButton("...");
            pickButton.setFont(new Font("楷体", Font.PLAIN, 12));
            pickButton.setPreferredSize(new Dimension(30, 30));
            pickButton.addActionListener(e -> showDatePickerDialog());
            add(pickButton, BorderLayout.EAST);
        }
        public java.util.Date getDate() {
            return selectedDate;
        }
        public void setDate(java.util.Date date) {
            this.selectedDate = date;
            if (date != null) {
                dateField.setText(String.format("%tF", date));
            } else {
                dateField.setText("");
            }
        }
        public void setFormats(String format) {
        }
        private void showDatePickerDialog() {
            String dateStr = JOptionPane.showInputDialog(this, "请输入日期 (YYYY-MM-DD):");
            if (dateStr != null && !dateStr.isEmpty()) {
                if (dateStr.matches("\\d{4}-\\d{2}-\\d{2}")) {
                    try {
                        selectedDate = java.sql.Date.valueOf(dateStr);
                        dateField.setText(dateStr);
                    } catch (IllegalArgumentException ex) {
                        JOptionPane.showMessageDialog(this, "日期格式错误，请使用YYYY-MM-DD格式", "错误", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "日期格式错误，请使用YYYY-MM-DD格式", "错误", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }
}
