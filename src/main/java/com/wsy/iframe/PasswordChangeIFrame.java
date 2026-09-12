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
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import com.wsy.main.Main;
import com.wsy.mapper.OperatorMapper;
import com.wsy.model.Operator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
@Service
@RequiredArgsConstructor
@Slf4j
public class PasswordChangeIFrame {
    private final OperatorMapper operatorMapper;
    private static JPasswordField oldPasswordField;
    private static JPasswordField newPasswordField;
    private static JPasswordField confirmPasswordField;
    private static JComponent usernameInputComponent;
    public JInternalFrame createPasswordChangeIFrame() throws PropertyVetoException {
        JInternalFrame frame = new JInternalFrame("密码更改", true, true, true, true);
        frame.setSize(500, 350);
        frame.setLayout(new BorderLayout());
        frame.getContentPane().setBackground(new Color(240, 248, 255));
        frame.setDefaultCloseOperation(JInternalFrame.DISPOSE_ON_CLOSE);
        frame.setMaximum(true);
        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(new Color(70, 130, 180));
        titlePanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        JLabel titleLabel = new JLabel("更改系统密码");
        titleLabel.setFont(new Font("楷体", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);
        titlePanel.add(titleLabel);
        frame.add(titlePanel, BorderLayout.NORTH);
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(new EmptyBorder(20, 50, 20, 50));
        formPanel.setBackground(new Color(245, 251, 255));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.LINE_START;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel userLabel = new JLabel("用户名:");
        userLabel.setFont(new Font("楷体", Font.BOLD, 14));
        formPanel.add(userLabel, gbc);
        gbc.gridx = 1;
        if (Main.currentOperator.isAdmin()) {
            List<Operator> allOperators = operatorMapper.selectAll();
            DefaultComboBoxModel<String> userModel = new DefaultComboBoxModel<>();
            for (Operator op : allOperators) {
                userModel.addElement(op.getUserName());
            }
            JComboBox<String> userComboBox = new JComboBox<>(userModel);
            userComboBox.setFont(new Font("楷体", Font.PLAIN, 14));
            usernameInputComponent = userComboBox;
        } else {
            JTextField usernameField = new JTextField(Main.currentOperator.getUserName());
            usernameField.setFont(new Font("楷体", Font.PLAIN, 14));
            usernameField.setEditable(false);
            usernameInputComponent = usernameField;
        }
        formPanel.add(usernameInputComponent, gbc);
        gbc.gridy++;
        gbc.gridx = 0;
        JLabel oldPasswordLabel = new JLabel("旧密码:");
        oldPasswordLabel.setFont(new Font("楷体", Font.BOLD, 14));
        formPanel.add(oldPasswordLabel, gbc);
        gbc.gridx = 1;
        oldPasswordField = new JPasswordField();
        oldPasswordField.setFont(new Font("楷体", Font.PLAIN, 14));
        formPanel.add(oldPasswordField, gbc);
        gbc.gridy++;
        gbc.gridx = 0;
        JLabel newPasswordLabel = new JLabel("新密码:");
        newPasswordLabel.setFont(new Font("楷体", Font.BOLD, 14));
        formPanel.add(newPasswordLabel, gbc);
        gbc.gridx = 1;
        newPasswordField = new JPasswordField();
        newPasswordField.setFont(new Font("楷体", Font.PLAIN, 14));
        formPanel.add(newPasswordField, gbc);
        gbc.gridy++;
        gbc.gridx = 0;
        JLabel confirmPasswordLabel = new JLabel("确认密码:");
        confirmPasswordLabel.setFont(new Font("楷体", Font.BOLD, 14));
        formPanel.add(confirmPasswordLabel, gbc);
        gbc.gridx = 1;
        confirmPasswordField = new JPasswordField();
        confirmPasswordField.setFont(new Font("楷体", Font.PLAIN, 14));
        formPanel.add(confirmPasswordField, gbc);
        gbc.gridy++;
        gbc.gridx = 1;
        JLabel strengthLabel = new JLabel("密码长度应在6-20位，建议包含字母和数字");
        strengthLabel.setFont(new Font("楷体", Font.ITALIC, 12));
        strengthLabel.setForeground(new Color(100, 100, 100));
        formPanel.add(strengthLabel, gbc);
        frame.add(formPanel, BorderLayout.CENTER);
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 15));
        buttonPanel.setBackground(new Color(245, 251, 255));
        JButton changeButton = new JButton("确认更改");
        styleButton(changeButton, new Color(56, 142, 60));
        changeButton.addActionListener(e -> {
            String username;
            if (usernameInputComponent instanceof JTextField) {
                username = ((JTextField) usernameInputComponent).getText().trim();
            } else {
                username = (String) ((JComboBox<?>) usernameInputComponent).getSelectedItem();
            }
            changePassword(
                    username,
                    new String(oldPasswordField.getPassword()),
                    new String(newPasswordField.getPassword()),
                    new String(confirmPasswordField.getPassword()),
                    frame
            );
        });
        JButton cancelButton = new JButton("取消");
        styleButton(cancelButton, new Color(120, 144, 156));
        cancelButton.addActionListener(e -> frame.dispose());
        buttonPanel.add(changeButton);
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
    private void changePassword(String username, String oldPassword,
                                String newPassword, String confirmPassword,
                                JInternalFrame frame) {
        if (username.isEmpty() || oldPassword.isEmpty() ||
                newPassword.isEmpty() || confirmPassword.isEmpty()) {
            showError("所有字段都必须填写！", "输入错误");
            return;
        }
        if (!newPassword.equals(confirmPassword)) {
            showError("新密码与确认密码不一致！", "密码不匹配");
            newPasswordField.setText("");
            confirmPasswordField.setText("");
            newPasswordField.requestFocus();
            return;
        }
        if (newPassword.length() < 6 || newPassword.length() > 20) {
            showError("新密码长度应在6-20位之间！", "密码长度错误");
            newPasswordField.setText("");
            confirmPasswordField.setText("");
            newPasswordField.requestFocus();
            return;
        }
        try {
            List<Operator> operators = operatorMapper.selectByCondition("userName", username);
            if (operators == null || operators.isEmpty()) {
                showError("用户名不存在！", "验证失败");
                return;
            }
            Operator operator = operators.get(0);
            if (!operator.getPassword().equals(oldPassword)) {
                showError("旧密码不正确！", "验证失败");
                oldPasswordField.setText("");
                oldPasswordField.requestFocus();
                return;
            }
            int rowsAffected = operatorMapper.updatePassword(operator.getId(), newPassword);
            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(frame,
                        "<html><b>密码更新成功！</b><br><br>用户名: " + username + "</html>",
                        "操作成功", JOptionPane.INFORMATION_MESSAGE);
                oldPasswordField.setText("");
                newPasswordField.setText("");
                confirmPasswordField.setText("");
            } else {
                showError("密码更新失败，请重试！", "更新失败");
            }
        } catch (Exception ex) {
            showError("系统错误: " + ex.getMessage(), "系统错误");
            log.error("修改密码失败", ex);
            return;
        }
    }
    private void showError(String message, String title) {
        JOptionPane.showMessageDialog(null, message, title, JOptionPane.ERROR_MESSAGE);
    }
}
