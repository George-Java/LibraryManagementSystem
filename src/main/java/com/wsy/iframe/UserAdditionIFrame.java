package com.wsy.iframe;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.beans.PropertyVetoException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import org.springframework.beans.factory.annotation.Autowired;

import static com.wsy.auxiliary.AuxiliaryTools.createText;
import com.wsy.mapper.OperatorMapper;
import com.wsy.model.Operator;
public class UserAdditionIFrame {
    @Autowired
    private OperatorMapper operatorMapper;
    public void setOperatorMapper(OperatorMapper operatorMapper) {
        this.operatorMapper = operatorMapper;
    }
    private JTextField nameField, ageField, phoneField, idCardField;
    private JPasswordField pwdField, confirmPwdField;
    private JComboBox<String> genderComboBox;
    private JCheckBox adminCheckBox;
    private JXDatePicker workDatePicker;
    private JInternalFrame frame;
    public JInternalFrame createAddUserIFrame() throws PropertyVetoException {
        frame = new JInternalFrame("添加系统用户", true, true, true, true);
        frame.setSize(600, 500);
        frame.setLayout(new BorderLayout());
        frame.setDefaultCloseOperation(JInternalFrame.DISPOSE_ON_CLOSE);
        frame.getContentPane().setBackground(new Color(240, 248, 255));
        frame.setMaximum(true);
        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(new Color(70, 130, 180));
        titlePanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        JLabel titleLabel = new JLabel("添加新用户");
        titleLabel.setFont(new Font("楷体", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);
        titlePanel.add(titleLabel);
        frame.add(titlePanel, BorderLayout.NORTH);
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new GridBagLayout());
        formPanel.setBorder(new EmptyBorder(20, 30, 20, 30));
        formPanel.setBackground(new Color(245, 251, 255));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.LINE_START;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(createLabel("姓名:"), gbc);
        gbc.gridx = 1;
        nameField = createText();
        nameField.setFont(new Font("楷体", Font.PLAIN, 14));
        formPanel.add(nameField, gbc);
        gbc.gridx = 2;
        formPanel.add(createLabel("(必填)"), gbc);
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(createLabel("性别:"), gbc);
        gbc.gridx = 1;
        genderComboBox = new JComboBox<>(new String[]{"男", "女"});
        genderComboBox.setFont(new Font("楷体", Font.PLAIN, 14));
        genderComboBox.setBackground(Color.WHITE);
        formPanel.add(genderComboBox, gbc);
        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(createLabel("年龄:"), gbc);
        gbc.gridx = 1;
        ageField = createText();
        ageField.setFont(new Font("楷体", Font.PLAIN, 14));
        formPanel.add(ageField, gbc);
        gbc.gridx = 2;
        formPanel.add(createLabel("(18-65岁)"), gbc);
        gbc.gridx = 0;
        gbc.gridy = 3;
        formPanel.add(createLabel("电话:"), gbc);
        gbc.gridx = 1;
        phoneField = createText();
        phoneField.setFont(new Font("楷体", Font.PLAIN, 14));
        formPanel.add(phoneField, gbc);
        gbc.gridx = 2;
        formPanel.add(createLabel("(11位手机号)"), gbc);
        gbc.gridx = 0;
        gbc.gridy = 4;
        formPanel.add(createLabel("身份证号:"), gbc);
        gbc.gridx = 1;
        idCardField = createText();
        idCardField.setFont(new Font("楷体", Font.PLAIN, 14));
        formPanel.add(idCardField, gbc);
        gbc.gridx = 2;
        formPanel.add(createLabel("(必填，唯一)"), gbc);
        gbc.gridx = 0;
        gbc.gridy = 5;
        formPanel.add(createLabel("工作日期:"), gbc);
        gbc.gridx = 1;
        workDatePicker = new JXDatePicker();
        workDatePicker.setFormats("yyyy-MM-dd");
        workDatePicker.setBackground(Color.WHITE);
        formPanel.add(workDatePicker, gbc);
        gbc.gridx = 2;
        formPanel.add(createLabel("(必填)"), gbc);
        gbc.gridx = 0;
        gbc.gridy = 6;
        formPanel.add(createLabel("管理员:"), gbc);
        gbc.gridx = 1;
        adminCheckBox = new JCheckBox("是管理员?");
        adminCheckBox.setFont(new Font("楷体", Font.PLAIN, 14));
        adminCheckBox.setBackground(new Color(245, 251, 255));
        formPanel.add(adminCheckBox, gbc);
        gbc.gridx = 0;
        gbc.gridy = 7;
        formPanel.add(createLabel("用户名:"), gbc);
        gbc.gridx = 1;
        JTextField usernameField = createText();
        usernameField.setFont(new Font("楷体", Font.PLAIN, 14));
        formPanel.add(usernameField, gbc);
        gbc.gridx = 2;
        formPanel.add(createLabel("(必填，唯一)"), gbc);
        gbc.gridx = 0;
        gbc.gridy = 8;
        formPanel.add(createLabel("密码:"), gbc);
        gbc.gridx = 1;
        pwdField = new JPasswordField();
        pwdField.setFont(new Font("楷体", Font.PLAIN, 14));
        formPanel.add(pwdField, gbc);
        gbc.gridx = 2;
        formPanel.add(createLabel("(6-20位)"), gbc);
        gbc.gridx = 0;
        gbc.gridy = 9;
        formPanel.add(createLabel("确认密码:"), gbc);
        gbc.gridx = 1;
        confirmPwdField = new JPasswordField();
        confirmPwdField.setFont(new Font("楷体", Font.PLAIN, 14));
        formPanel.add(confirmPwdField, gbc);
        gbc.gridx = 2;
        formPanel.add(createLabel("(必填)"), gbc);
        frame.add(formPanel, BorderLayout.CENTER);
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 20));
        buttonPanel.setBackground(new Color(245, 251, 255));
        JButton addButton = new JButton("添加用户");
        styleButton(addButton, new Color(70, 130, 180));
        addButton.addActionListener(e -> addUser(
                nameField.getText().trim(),
                (String) genderComboBox.getSelectedItem(),
                ageField.getText().trim(),
                phoneField.getText().trim(),
                idCardField.getText().trim(),
                workDatePicker.getDate(),
                adminCheckBox.isSelected(),
                usernameField.getText().trim(),
                new String(pwdField.getPassword()),
                new String(confirmPwdField.getPassword())
        ));
        JButton resetButton = new JButton("重置");
        styleButton(resetButton, new Color(100, 100, 100));
        resetButton.addActionListener(e -> resetForm());
        JButton cancelButton = new JButton("取消");
        styleButton(cancelButton, new Color(220, 80, 60));
        cancelButton.addActionListener(e -> frame.dispose());
        buttonPanel.add(addButton);
        buttonPanel.add(resetButton);
        buttonPanel.add(cancelButton);
        frame.add(buttonPanel, BorderLayout.SOUTH);
        frame.setVisible(true);
        return frame;
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
    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("楷体", Font.BOLD, 14));
        return label;
    }
    private void addUser(String name, String gender, String ageStr, String phone,
                         String idCard, java.util.Date workDate, boolean isAdmin,
                         String appUsername, String appPassword, String confirmPassword) {
        if (!validateInput(name, gender, ageStr, phone, idCard, workDate,
                appUsername, appPassword, confirmPassword)) {
            return;
        }
        int age = Integer.parseInt(ageStr);
        List<Operator> existingUsers = operatorMapper.selectByCondition("identityCard", idCard);
        if (existingUsers != null && !existingUsers.isEmpty()) {
            JOptionPane.showMessageDialog(frame,
                    "身份证号 " + idCard + " 已被使用！",
                    "添加失败", JOptionPane.ERROR_MESSAGE);
            idCardField.requestFocus();
            return;
        }
        existingUsers = operatorMapper.selectByCondition("userName", appUsername);
        if (existingUsers != null && !existingUsers.isEmpty()) {
            JOptionPane.showMessageDialog(frame,
                    "用户名 " + appUsername + " 已被使用！请使用不同的用户名。",
                    "添加失败", JOptionPane.ERROR_MESSAGE);
            return;
        }
        Operator operator = new Operator();
        operator.setName(name);
        operator.setSex(gender);
        operator.setAge(age);
        operator.setPhone(phone);
        operator.setIdentityCard(idCard);
        if (workDate != null) {
            LocalDate localWorkDate;
            if (workDate instanceof java.sql.Date) {
                localWorkDate = ((java.sql.Date) workDate).toLocalDate();
            } else {
                localWorkDate = workDate.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate();
            }
            operator.setWorkDate(localWorkDate);
        } else {
            operator.setWorkDate(null);
        }
        operator.setAdmin(isAdmin);
        operator.setUserName(appUsername);
        operator.setPassword(appPassword);
        int rowsAffected = operatorMapper.insert(operator);
        if (rowsAffected > 0) {
            JOptionPane.showMessageDialog(frame,
                    "<html><b>用户添加成功！</b><br><br>" +
                            "姓名: " + name + "<br>" +
                            "用户名: " + appUsername + "</html>",
                    "添加成功", JOptionPane.INFORMATION_MESSAGE);
            resetForm();
        } else {
            JOptionPane.showMessageDialog(frame, "用户添加失败", "添加失败", JOptionPane.ERROR_MESSAGE);
        }
    }
    private boolean validateInput(String name, String gender, String ageStr,
                                  String phone, String idCard, java.util.Date workDate,
                                  String username, String password, String confirmPassword) {
        if (name.isEmpty()) {
            showError("姓名不能为空");
            nameField.requestFocus();
            return false;
        }
        try {
            int age = Integer.parseInt(ageStr);
            if (age < 18 || age > 65) {
                showError("年龄必须在18-65岁之间");
                ageField.requestFocus();
                return false;
            }
        } catch (NumberFormatException e) {
            showError("年龄必须是数字");
            ageField.requestFocus();
            return false;
        }
        if (!phone.matches("\\d{11}")) {
            showError("电话必须是11位数字");
            phoneField.requestFocus();
            return false;
        }
        if (idCard.isEmpty() || !idCard.matches("\\d{17}[\\dX]")) {
            showError("身份证号格式不正确（应为18位数字或最后一位X）");
            idCardField.requestFocus();
            return false;
        }
        if (workDate == null) {
            showError("请选择工作日期");
            return false;
        }
        if (username.length() < 4 || username.length() > 20) {
            showError("用户名长度应在4-20个字符之间");
            return false;
        }
        if (password.length() < 6 || password.length() > 20) {
            showError("密码长度应在6-20个字符之间");
            pwdField.requestFocus();
            return false;
        }
        if (!password.equals(confirmPassword)) {
            showError("两次输入的密码不一致");
            pwdField.setText("");
            confirmPwdField.setText("");
            pwdField.requestFocus();
            return false;
        }
        return true;
    }
    private boolean isUnique(Connection conn, String column, String value) throws SQLException {
        String sql = "SELECT COUNT(*) FROM tb_operator WHERE " + column + " = ?";
        try (PreparedStatement stat = conn.prepareStatement(sql)) {
            stat.setString(1, value);
            try (ResultSet rs = stat.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }
    private void showError(String message) {
        JOptionPane.showMessageDialog(frame, message, "输入错误", JOptionPane.ERROR_MESSAGE);
    }
    private void resetForm() {
        nameField.setText("");
        genderComboBox.setSelectedIndex(0);
        ageField.setText("");
        phoneField.setText("");
        idCardField.setText("");
        workDatePicker.setDate(new java.util.Date());
        adminCheckBox.setSelected(false);
        pwdField.setText("");
        confirmPwdField.setText("");
        nameField.requestFocus();
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
                        JOptionPane.showMessageDialog(this, "日期格式错误", "错误", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "请使用YYYY-MM-DD格式", "错误", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }
}