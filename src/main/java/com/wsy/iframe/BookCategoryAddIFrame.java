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
import java.util.regex.Pattern;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wsy.mapper.BookTypeMapper;
import com.wsy.model.BookType;
@Service
@RequiredArgsConstructor
@Slf4j
public class BookCategoryAddIFrame {
    private final BookTypeMapper bookTypeMapper;
    private JTextField categoryIdField, categoryNameField;
    private JSpinner daysSpinner, fineSpinner;
    private JInternalFrame frame;
    public JInternalFrame createBookCategoryAddIFrame() throws PropertyVetoException {
        frame = new JInternalFrame("图书类别管理", true, true, true, true);
        frame.setSize(500, 350);
        frame.setLayout(new BorderLayout());
        frame.setDefaultCloseOperation(JInternalFrame.DISPOSE_ON_CLOSE);
        frame.setMaximum(true); // 设置默认最大化显示
        frame.setBackground(new Color(245, 251, 255));
        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(new Color(70, 130, 180));
        titlePanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        JLabel titleLabel = new JLabel("添加图书类别");
        titleLabel.setFont(new Font("楷体", Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE);
        titlePanel.add(titleLabel);
        frame.add(titlePanel, BorderLayout.NORTH);
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 25, 15, 25));
        mainPanel.setBackground(new Color(245, 251, 255));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.LINE_START;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel idLabel = new JLabel("类别编号：");
        idLabel.setFont(new Font("楷体", Font.BOLD, 14));
        mainPanel.add(idLabel, gbc);
        gbc.gridx = 1;
        categoryIdField = new JTextField();
        categoryIdField.setFont(new Font("楷体", Font.PLAIN, 14));
        categoryIdField.setToolTipText("格式示例: T0001 (1位字母+4位数字)");
        categoryIdField.setPreferredSize(new Dimension(200, 25));
        mainPanel.add(categoryIdField, gbc);
        gbc.gridy++;
        gbc.gridx = 0;
        JLabel nameLabel = new JLabel("类别名称：");
        nameLabel.setFont(new Font("楷体", Font.BOLD, 14));
        mainPanel.add(nameLabel, gbc);
        gbc.gridx = 1;
        categoryNameField = new JTextField();
        categoryNameField.setFont(new Font("楷体", Font.PLAIN, 14));
        categoryNameField.setToolTipText("输入类别名称，如：计算机类");
        categoryNameField.setPreferredSize(new Dimension(200, 25));
        mainPanel.add(categoryNameField, gbc);
        gbc.gridy++;
        gbc.gridx = 0;
        JLabel daysLabel = new JLabel("可借天数：");
        daysLabel.setFont(new Font("楷体", Font.BOLD, 14));
        mainPanel.add(daysLabel, gbc);
        gbc.gridx = 1;
        daysSpinner = new JSpinner(new SpinnerNumberModel(30, 1, 365, 1));
        daysSpinner.setFont(new Font("楷体", Font.PLAIN, 14));
        JSpinner.NumberEditor daysEditor = new JSpinner.NumberEditor(daysSpinner, "#");
        daysSpinner.setEditor(daysEditor);
        mainPanel.add(daysSpinner, gbc);
        gbc.gridy++;
        gbc.gridx = 0;
        JLabel fineLabel = new JLabel("罚款金额（元/天）：");
        fineLabel.setFont(new Font("楷体", Font.BOLD, 14));
        mainPanel.add(fineLabel, gbc);
        gbc.gridx = 1;
        fineSpinner = new JSpinner(new SpinnerNumberModel(0.5, 0.0, 100.0, 0.1));
        fineSpinner.setFont(new Font("楷体", Font.PLAIN, 14));
        JSpinner.NumberEditor fineEditor = new JSpinner.NumberEditor(fineSpinner, "#,##0.00");
        fineSpinner.setEditor(fineEditor);
        mainPanel.add(fineSpinner, gbc);
        frame.add(mainPanel, BorderLayout.CENTER);
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.setBackground(new Color(245, 251, 255));
        JButton addButton = createStyledButton("添加类别", new Color(56, 142, 60));
        addButton.addActionListener(e -> addBookCategory());
        JButton resetButton = createStyledButton("重置", new Color(120, 144, 156));
        resetButton.addActionListener(e -> resetForm());
        buttonPanel.add(addButton);
        buttonPanel.add(resetButton);
        frame.add(buttonPanel, BorderLayout.SOUTH);
        frame.setVisible(true);
        return frame;
    }
    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("楷体", Font.BOLD, 14));
        button.setPreferredSize(new Dimension(120, 35));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(bgColor.darker(), 1),
                BorderFactory.createEmptyBorder(5, 15, 5, 15)
        ));
        return button;
    }
    @Transactional(rollbackFor = Exception.class)
    private void addBookCategory() {
        String categoryId = categoryIdField.getText().trim().toUpperCase();
        String categoryName = categoryNameField.getText().trim();
        int borrowDays = (int) daysSpinner.getValue();
        double fineAmount = (double) fineSpinner.getValue();
        if (!isValidCategoryId(categoryId)) {
            showError("类别编号格式不正确！\n应包含1位字母和4位数字，如：TP0001");
            categoryIdField.requestFocus();
            return;
        }
        if (categoryName.isEmpty()) {
            showError("类别名称不能为空");
            categoryNameField.requestFocus();
            return;
        }
        if (borrowDays < 1 || borrowDays > 365) {
            showError("可借天数必须在1-365天之间");
            daysSpinner.requestFocus();
            return;
        }
        if (fineAmount < 0 || fineAmount > 100) {
            showError("罚款金额必须在0-100元之间");
            fineSpinner.requestFocus();
            return;
        }
        if (bookTypeMapper.selectByNumber(categoryId) != null) {
            showError("类别编号 '" + categoryId + "' 已存在！请使用不同的编号。");
            categoryIdField.requestFocus();
            return;
        }
        if (bookTypeMapper.selectByTypeName(categoryName) != null) {
            showError("类别名称 '" + categoryName + "' 已存在！请使用不同的名称。");
            categoryNameField.requestFocus();
            return;
        }
        try {
            BookType bookType = new BookType();
            bookType.setNumber(categoryId);
            bookType.setTypeName(categoryName);
            bookType.setDays(borrowDays);
            bookType.setFk((float) fineAmount);
            int rowsAffected = bookTypeMapper.insert(bookType);
            if (rowsAffected > 0) {
                String successMsg = "<html><b>图书类别添加成功！</b><br><br>" +
                        "类别编号: " + categoryId + "<br>" +
                        "类别名称: " + categoryName + "<br>" +
                        "可借天数: " + borrowDays + "天<br>" +
                        "罚款金额: ¥" + String.format("%.2f/天", fineAmount) + "</html>";
                JOptionPane.showMessageDialog(frame, successMsg,
                        "添加成功", JOptionPane.INFORMATION_MESSAGE);
                resetForm();
            } else {
                showError("添加失败，请重试！");
            }
        } catch (Exception ex) {
            log.error("添加图书类别失败", ex);
            showError("添加失败: " + ex.getMessage());
        }
    }
    private boolean isValidCategoryId(String categoryId) {
        return Pattern.matches("^[A-Z]\\d{4}$", categoryId);
    }
    private void resetForm() {
        categoryIdField.setText("");
        categoryNameField.setText("");
        daysSpinner.setValue(30);
        fineSpinner.setValue(0.5);
        categoryIdField.requestFocus();
    }
    private void showError(String message) {
        JOptionPane.showMessageDialog(frame, message, "输入错误", JOptionPane.ERROR_MESSAGE);
    }
}
