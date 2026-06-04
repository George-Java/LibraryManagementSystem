package com.wsy.iframe;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.beans.PropertyVetoException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import static com.wsy.auxiliary.AuxiliaryTools.createText;
import com.wsy.mapper.BookTypeMapper;
import com.wsy.model.BookType;
public class BookCategoryUpdateIFrame {
    @Autowired
    private BookTypeMapper bookTypeMapper;
    public void setBookTypeMapper(BookTypeMapper bookTypeMapper) {
        this.bookTypeMapper = bookTypeMapper;
    }
    private JTable categoryTable;
    private DefaultTableModel tableModel;
    private JTextField categoryIdField, categoryNameField;
    private JSpinner daysSpinner, fineSpinner;
    private JInternalFrame frame;
    private final List<String> categoryIds = new ArrayList<>();
    private JButton updateButton, deleteButton;
    private JTextField searchField;
    private String originalCategoryId; // 保存原始类别编号
    private static final Color MAIN_BG_COLOR = new Color(245, 248, 252);
    private static final Color PANEL_BG_COLOR = new Color(235, 242, 250);
    private static final Color HEADER_COLOR = new Color(70, 130, 180);
    private static final Color BUTTON_COLOR = new Color(70, 130, 180);
    private static final Color DELETE_BUTTON_COLOR = new Color(220, 80, 60);
    public JInternalFrame createBookCategoryUpdateIFrame() throws PropertyVetoException {
        frame = new JInternalFrame("图书类别管理", true, true, true, true);
        frame.setSize(900, 550);
        frame.setResizable(true);
        frame.setLayout(new BorderLayout());
        frame.setDefaultCloseOperation(JInternalFrame.DISPOSE_ON_CLOSE);
        frame.setMaximum(true); // 设置默认最大化显示
        frame.getContentPane().setBackground(MAIN_BG_COLOR);
        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(new Color(60, 110, 160));
        titlePanel.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        titlePanel.setLayout(new BorderLayout());
        JLabel titleLabel = new JLabel("图书类别管理");
        titleLabel.setFont(new Font("楷体", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titlePanel.add(titleLabel, BorderLayout.CENTER);
        frame.add(titlePanel, BorderLayout.NORTH);
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        contentPanel.setBackground(MAIN_BG_COLOR);
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(0.45);
        splitPane.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        splitPane.setBackground(MAIN_BG_COLOR);
        JPanel leftPanel = createListPanel();
        splitPane.setLeftComponent(leftPanel);
        JPanel rightPanel = createFormPanel();
        splitPane.setRightComponent(rightPanel);
        contentPanel.add(splitPane, BorderLayout.CENTER);
        frame.add(contentPanel, BorderLayout.CENTER);
        loadAllCategories();
        frame.setVisible(true);
        return frame;
    }
    private JPanel createListPanel() {
        JPanel listPanel = new JPanel(new BorderLayout());
        listPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        listPanel.setBackground(PANEL_BG_COLOR);
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        searchPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        searchPanel.setBackground(PANEL_BG_COLOR);
        JLabel searchLabel = new JLabel("搜索类别：");
        searchLabel.setFont(new Font("楷体", Font.BOLD, 15));
        searchPanel.add(searchLabel);
        searchField = new JTextField(15);
        searchField.setFont(new Font("楷体", Font.PLAIN, 14));
        searchPanel.add(searchField);
        JButton searchButton = new JButton("搜索");
        styleButton(searchButton);
        searchButton.addActionListener(e -> searchCategories(searchField.getText().trim()));
        searchPanel.add(searchButton);
        JButton refreshButton = new JButton("刷新");
        styleButton(refreshButton);
        refreshButton.addActionListener(e -> {
            searchField.setText("");
            loadAllCategories();
        });
        searchPanel.add(refreshButton);
        listPanel.add(searchPanel, BorderLayout.NORTH);
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(180, 180, 180)),
                "图书类别列表",
                javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                javax.swing.border.TitledBorder.DEFAULT_POSITION,
                new Font("楷体", Font.BOLD, 14),
                new Color(70, 70, 70)
        ));
        tablePanel.setBackground(Color.WHITE);
        String[] columnNames = {"类别编号", "类别名称"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        categoryTable = new JTable(tableModel);
        categoryTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        categoryTable.setRowHeight(30);
        categoryTable.setFont(new Font("楷体", Font.PLAIN, 14));
        categoryTable.getTableHeader().setFont(new Font("楷体", Font.BOLD, 14));
        categoryTable.getTableHeader().setBackground(new Color(230, 240, 245));
        categoryTable.getTableHeader().setReorderingAllowed(false);
        categoryTable.setRowSelectionAllowed(true);
        categoryTable.setGridColor(new Color(220, 220, 220));
        categoryTable.setSelectionBackground(new Color(225, 245, 254));
        categoryTable.setSelectionForeground(Color.BLACK);
        categoryTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && categoryTable.getSelectedRow() >= 0) {
                int index = categoryTable.getSelectedRow();
                loadCategoryDetails(index);
            }
        });
        JScrollPane tableScroll = new JScrollPane(categoryTable);
        tableScroll.setBorder(BorderFactory.createEmptyBorder());
        tablePanel.add(tableScroll, BorderLayout.CENTER);
        listPanel.add(tablePanel, BorderLayout.CENTER);
        return listPanel;
    }
    private JPanel createFormPanel() {
        JPanel formPanel = new JPanel(new BorderLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(180, 180, 180)),
                "类别信息管理",
                javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                javax.swing.border.TitledBorder.DEFAULT_POSITION,
                new Font("楷体", Font.BOLD, 14),
                new Color(70, 70, 70)
        ));
        formPanel.setBackground(Color.WHITE);
        JPanel formContainer = new JPanel();
        formContainer.setLayout(new BoxLayout(formContainer, BoxLayout.Y_AXIS));
        formContainer.setBorder(new EmptyBorder(20, 25, 15, 25));
        formContainer.setBackground(Color.WHITE);
        JPanel idPanel = createFormRow("类别编号：", categoryIdField = createText());
        categoryIdField.setEditable(false); // 禁止修改类别编号
        categoryIdField.setBackground(new Color(245, 245, 245));
        formContainer.add(idPanel);
        formContainer.add(Box.createVerticalStrut(20));
        JPanel namePanel = createFormRow("类别名称：", categoryNameField = createText());
        formContainer.add(namePanel);
        formContainer.add(Box.createVerticalStrut(20));
        JPanel daysPanel = createFormRow("可借天数：", daysSpinner = new JSpinner(
                new SpinnerNumberModel(30, 1, 365, 1)));
        daysSpinner.setFont(new Font("楷体", Font.PLAIN, 14));
        formContainer.add(daysPanel);
        formContainer.add(Box.createVerticalStrut(20));
        JPanel finePanel = createFormRow("罚款金额（元/天）：", fineSpinner = new JSpinner(
                new SpinnerNumberModel(0.5, 0.01, 100.0, 0.1)));
        fineSpinner.setFont(new Font("楷体", Font.PLAIN, 14));
        JSpinner.NumberEditor fineEditor = new JSpinner.NumberEditor(fineSpinner, "#,##0.00");
        fineSpinner.setEditor(fineEditor);
        formContainer.add(finePanel);
        formContainer.add(Box.createVerticalStrut(30));
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 25, 10));
        buttonPanel.setBackground(Color.WHITE);
        updateButton = new JButton("更新信息");
        updateButton.setFont(new Font("楷体", Font.BOLD, 14));
        updateButton.setPreferredSize(new Dimension(150, 40));
        updateButton.setBackground(BUTTON_COLOR);
        updateButton.setForeground(Color.WHITE);
        updateButton.addActionListener(e -> updateCategory());
        updateButton.setEnabled(false);
        buttonPanel.add(updateButton);
        deleteButton = new JButton("删除类别");
        deleteButton.setFont(new Font("楷体", Font.BOLD, 14));
        deleteButton.setPreferredSize(new Dimension(150, 40));
        deleteButton.setBackground(DELETE_BUTTON_COLOR);
        deleteButton.setForeground(Color.WHITE);
        deleteButton.addActionListener(e -> deleteCategory());
        deleteButton.setEnabled(false);
        buttonPanel.add(deleteButton);
        formPanel.add(formContainer, BorderLayout.CENTER);
        formPanel.add(buttonPanel, BorderLayout.SOUTH);
        return formPanel;
    }
    private void styleButton(JButton button) {
        button.setFont(new Font("楷体", Font.BOLD, 13));
        button.setPreferredSize(new Dimension(100, 32));
        button.setBackground(BUTTON_COLOR);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createLineBorder(new Color(150, 150, 150), 1));
    }
    private JPanel createFormRow(String labelText, JComponent component) {
        JPanel rowPanel = new JPanel(new BorderLayout(15, 5));
        rowPanel.setBackground(Color.WHITE);
        rowPanel.setMaximumSize(new Dimension(400, 50));
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("楷体", Font.BOLD, 15));
        rowPanel.add(label, BorderLayout.WEST);
        if (component instanceof JTextField) {
            component.setFont(new Font("楷体", Font.PLAIN, 14));
            component.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(180, 180, 180)),
                    BorderFactory.createEmptyBorder(5, 8, 5, 8)
            ));
        }
        if (component instanceof JSpinner spinner) {
            JFormattedTextField textField = ((JSpinner.DefaultEditor) spinner.getEditor()).getTextField();
            textField.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(180, 180, 180)),
                    BorderFactory.createEmptyBorder(5, 8, 5, 8)
            ));
            textField.setFont(new Font("楷体", Font.PLAIN, 14));
            textField.setBackground(Color.WHITE);
        }
        JPanel compWrapper = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        compWrapper.add(component);
        compWrapper.setBackground(Color.WHITE);
        rowPanel.add(compWrapper, BorderLayout.CENTER);
        return rowPanel;
    }
    private void loadAllCategories() {
        categoryIds.clear();
        tableModel.setRowCount(0);
        try {
            List<BookType> bookTypes = bookTypeMapper.selectAll();
            for (BookType bookType : bookTypes) {
                categoryIds.add(bookType.getNumber());
                tableModel.addRow(new Object[]{
                        bookType.getNumber(),
                        bookType.getTypeName()
                });
            }
        } catch (Exception e) {
            showError("加载类别失败: " + e.getMessage());
        }
    }
    private void searchCategories(String keyword) {
        if (keyword.isEmpty()) {
            loadAllCategories();
            return;
        }
        categoryIds.clear();
        tableModel.setRowCount(0);
        try {
            List<BookType> bookTypes = bookTypeMapper.selectByKeyword(keyword);
            for (BookType bookType : bookTypes) {
                categoryIds.add(bookType.getNumber());
                tableModel.addRow(new Object[]{
                        bookType.getNumber(),
                        bookType.getTypeName()
                });
            }
        } catch (Exception e) {
            showError("搜索失败: " + e.getMessage());
        }
    }
    private void loadCategoryDetails(int rowIndex) {
        if (rowIndex < 0 || rowIndex >= categoryIds.size()) {
            return;
        }
        originalCategoryId = categoryIds.get(rowIndex);
        try {
            BookType bookType = bookTypeMapper.selectByNumber(originalCategoryId);
            if (bookType != null) {
                categoryIdField.setText(bookType.getNumber());
                categoryNameField.setText(bookType.getTypeName());
                daysSpinner.setValue(bookType.getDays());
                fineSpinner.setValue(bookType.getFk());
                updateButton.setEnabled(true);
                deleteButton.setEnabled(true);
            }
        } catch (Exception e) {
            showError("加载类别详情失败: " + e.getMessage());
        }
        categoryIdField.revalidate();
        categoryIdField.repaint();
        categoryNameField.revalidate();
        categoryNameField.repaint();
        if (frame != null) {
            frame.revalidate();
            frame.repaint();
        }
    }
    @Transactional(rollbackFor = Exception.class)
    private void updateCategory() {
        String categoryId = categoryIdField.getText().trim();
        String categoryName = categoryNameField.getText().trim();
        int borrowDays = (int) daysSpinner.getValue();
        double fineAmount = ((Number) fineSpinner.getValue()).doubleValue();
        if (categoryId.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "请先选择一个类别", "提示", JOptionPane.WARNING_MESSAGE);
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
        if (fineAmount <= 0 || fineAmount > 100) {
            showError("罚款金额必须在0.01-100元之间");
            fineSpinner.requestFocus();
            return;
        }
        try {
            // 检查类别名称是否被其他类别使用
            BookType existingBookType = bookTypeMapper.selectByTypeName(categoryName);
            if (existingBookType != null && !existingBookType.getNumber().equals(originalCategoryId)) {
                showError("类别名称 '" + categoryName + "' 已被其他类别使用\n" +
                        "冲突类别编号: " + existingBookType.getNumber());
                return;
            }
            
            BookType bookType = new BookType();
            bookType.setNumber(categoryId);
            bookType.setTypeName(categoryName);
            bookType.setDays(borrowDays);
            bookType.setFk((float) fineAmount);
            
            int rowsAffected;
            
            // 类别编号禁止修改，如用户通过其他方式改动则直接拒绝
            if (!categoryId.equals(originalCategoryId)) {
                showError("类别编号不允许修改，请保持原编号。");
                categoryIdField.setText(originalCategoryId);
                return;
            }

            // 仅更新名称、借阅天数、罚款
            rowsAffected = bookTypeMapper.update(bookType);

            // 更新表格中的类别名称
            int selectedRow = categoryTable.getSelectedRow();
            if (selectedRow >= 0) {
                tableModel.setValueAt(categoryName, selectedRow, 1);
            }
            
            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(frame,
                        "<html><b>类别信息更新成功！</b><br><br>" +
                                "类别编号: <font color='blue'>" + categoryId + "</font><br>" +
                                "类别名称: <font color='blue'>" + categoryName + "</font></html>",
                        "操作成功", JOptionPane.INFORMATION_MESSAGE);
            } else {
                showError("更新失败，请重试");
            }
        } catch (Exception e) {
            showError("更新类别失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
    @Transactional(rollbackFor = Exception.class)
    private void deleteCategory() {
        String categoryId = categoryIdField.getText().trim();
        String categoryName = categoryNameField.getText().trim();
        if (categoryId.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "请先选择一个类别", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            int bookCount = bookTypeMapper.countBooksByType(categoryId);
            if (bookCount > 0) {
                showError("<html><b>无法删除该类别！</b><br><br>" +
                        "当前有图书在使用该类别，请先修改这些图书的类别。</html>");
                return;
            }
        } catch (Exception e) {
            showError("检查类别使用情况失败: " + e.getMessage());
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(frame,
                "<html><b>确定要删除此类别吗？</b><br><br>" +
                        "类别编号: <font color='red'>" + categoryId + "</font><br>" +
                        "类别名称: <font color='red'>" + categoryName + "</font></html>",
                "确认删除", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }
        try {
            int rowsAffected = bookTypeMapper.deleteByNumber(categoryId);
            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(frame,
                        "<html><b>类别删除成功！</b><br><br>" +
                                "已删除类别编号: <font color='red'>" + categoryId + "</font><br>" +
                                "已删除类别名称: <font color='red'>" + categoryName + "</font></html>",
                        "操作成功", JOptionPane.INFORMATION_MESSAGE);
                int selectedRow = categoryTable.getSelectedRow();
                if (selectedRow >= 0) {
                    categoryIds.remove(selectedRow);
                    tableModel.removeRow(selectedRow);
                    resetForm();
                }
            } else {
                showError("删除失败，请重试");
            }
        } catch (Exception e) {
            showError("删除类别失败: " + e.getMessage());
        }
    }
    private void resetForm() {
        categoryIdField.setText("");
        categoryNameField.setText("");
        daysSpinner.setValue(30);
        fineSpinner.setValue(0.5);
        updateButton.setEnabled(false);
        deleteButton.setEnabled(false);
    }
    private void showError(String message) {
        JOptionPane.showMessageDialog(frame, message, "错误", JOptionPane.ERROR_MESSAGE);
    }
}