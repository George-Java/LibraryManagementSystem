package com.wsy.iframe;

import com.wsy.auxiliary.JImageView;
import com.wsy.mapper.BookInfoMapper;
import com.wsy.mapper.BookTypeMapper;
import com.wsy.mapper.StockpileMapper;
import com.wsy.model.BookInfo;
import com.wsy.model.BookType;
import com.wsy.model.Stockpile;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyVetoException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.wsy.auxiliary.AuxiliaryTools.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookAdditionIFrame {
    private final BookInfoMapper bookInfoMapper;
    private final BookTypeMapper bookTypeMapper;
    private final StockpileMapper stockpileMapper;

    private final List<java.awt.Component> inputComponents = new ArrayList<>();
    private final Map<String, String> categoryMap = new HashMap<>();
    private final Map<String, String> publisherMap = new HashMap<>();

    public JInternalFrame createAddBookIFrame() throws PropertyVetoException {
        inputComponents.clear();
        loadCategoriesAndPublishers();
        JInternalFrame frame = new JInternalFrame("图书信息添加", true, true, true, true);
        frame.setSize(640, 360);
        frame.setLayout(new BorderLayout());
        frame.setDefaultCloseOperation(JInternalFrame.DISPOSE_ON_CLOSE);
        frame.setMaximum(true); // 设置默认最大化显示
        // 创建图片面板
        JImageView panelTop = new JImageView("./res/bookAdditon.jpeg");

        // 创建输入控件面板
        JPanel panelCenter = new JPanel();
        panelCenter.setLayout(new GridLayout(4, 4, 10, 10));
        String[] addBookLabels = {"图书ISBN：", "类别：", "书名：", "作者：", "出版社：", "译者：", "出版日期：", "单价："};
        String[] bookCategoryNames = categoryMap.keySet().toArray(new String[0]);
        String[] publisherNames = publisherMap.keySet().toArray(new String[0]);
        for (int i = 0; i < 8; i++) {
            panelCenter.add(createLabel(addBookLabels[i]));
            if (i == 1) {
                JComboBox<String> comboBox = createComboBox(bookCategoryNames);
                panelCenter.add(comboBox);
                inputComponents.add(comboBox);
            } else if (i == 4) {
                JComboBox<String> comboBox = createComboBox(publisherNames, true);
                panelCenter.add(comboBox);
                inputComponents.add(comboBox);
            } else {
                JTextField textField = createText();
                panelCenter.add(textField);
                inputComponents.add(textField);
            }
        }

        // 创建按钮面板
        JPanel panelBottom = createButtons("添加", "关闭");
        panelBottom.setPreferredSize(new Dimension(0, 40));
        panelBottom.getComponent(0).addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                String bookISBN = getComponentValue(0);
                String bookname = getComponentValue(2);
                if (bookISBN.trim().isEmpty()) {
                    JOptionPane.showMessageDialog(frame, "图书ISBN不能为空", "错误", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                if (bookISBN.length() != 13) {
                    JOptionPane.showMessageDialog(frame, "图书ISBN必须为13位", "错误", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                if (bookname.trim().isEmpty()) {
                    JOptionPane.showMessageDialog(frame, "书名不能为空", "错误", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                String[] values = new String[8];
                for (int i = 0; i < inputComponents.size(); i++) {
                    values[i] = getComponentValue(i);
                }
                String categoryName = values[1];
                String categoryCode = categoryMap.get(categoryName);
                if (categoryCode == null) {
                    JOptionPane.showMessageDialog(frame, "无效的图书类别", "错误", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                String publisherDisplay = values[4];
                String actualPublisher = publisherMap.get(publisherDisplay);
                if (actualPublisher == null) {
                    actualPublisher = publisherDisplay;
                }
                values[4] = actualPublisher;
                if (addBookToDatabase(bookISBN, categoryCode, values)) {
                    JOptionPane.showMessageDialog(frame, "图书添加成功！");
                    clearForm();
                    frame.requestFocus();
                } else {
                    JOptionPane.showMessageDialog(frame, "添加图书失败，请重试", "错误", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        panelBottom.getComponent(1).addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                frame.dispose();
            }
        });

        // 创建下方面板（输入控件 + 按钮）
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(panelCenter, BorderLayout.CENTER);
        bottomPanel.add(panelBottom, BorderLayout.SOUTH);

        // 使用JSplitPane替代BorderLayout，实现可调整比例的布局
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, panelTop, bottomPanel);
        splitPane.setResizeWeight(0.6); // 设置调整比例，图片部分优先分配空间
        splitPane.setOneTouchExpandable(true); // 显示快速调整按钮
        splitPane.setContinuousLayout(true); // 调整时实时重绘

        // 显式设置初始分割位置，确保图片部分占窗口高度的3/5
        frame.add(splitPane, BorderLayout.CENTER);
        frame.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                int height = frame.getContentPane().getHeight();
                if (height > 0) {
                    // 计算分割位置：图片部分占3/5高度
                    int dividerLocation = (int) (height * 0.6);
                    splitPane.setDividerLocation(dividerLocation);
                }
            }
        });
        frame.setVisible(true);
        return frame;
    }

    private void loadCategoriesAndPublishers() {
        try {
            List<BookType> bookTypes = bookTypeMapper.selectAll();
            for (BookType type : bookTypes) {
                categoryMap.put(type.getTypeName(), type.getNumber());
            }
            List<BookInfo> books = bookInfoMapper.selectAll();
            for (BookInfo book : books) {
                if (book.getPublisher() != null && !book.getPublisher().trim().isEmpty()) {
                    publisherMap.put(book.getPublisher(), book.getPublisher());
                }
            }
            if (publisherMap.isEmpty()) {
                String[] defaultPublishers = {
                        "机械工业出版社", "清华大学出版社", "人民邮电出版社",
                        "复旦大学出版社", "高等教育出版社"
                };
                for (String pub : defaultPublishers) {
                    publisherMap.put(pub, pub);
                }
            }
        } catch (Exception e) {
            log.error("Failed to load book categories and publishers", e);
            categoryMap.put("计算机", "TP0001");
            categoryMap.put("文学", "I00001");
            categoryMap.put("数理化", "O00001");
            categoryMap.put("经济管理", "F00001");
            publisherMap.put("机械工业出版社", "机械工业出版社");
            publisherMap.put("清华大学出版社", "清华大学出版社");
            publisherMap.put("人民邮电出版社", "人民邮电出版社");
            publisherMap.put("复旦大学出版社", "复旦大学出版社");
            publisherMap.put("高等教育出版社", "高等教育出版社");
        }
    }

    private String getComponentValue(int index) {
        java.awt.Component comp = inputComponents.get(index);
        String value = "";
        if (comp instanceof JTextField) {
            value = ((JTextField) comp).getText();
        } else if (comp instanceof JComboBox) {
            Object selectedItem = ((JComboBox<?>) comp).getSelectedItem();
            value = selectedItem != null ? selectedItem.toString() : "";
        }
        return value != null ? value.trim() : "";
    }

    private boolean addBookToDatabase(String bookISBN, String categoryCode, String[] values) {
        try {
            BookInfo bookInfo = new BookInfo();
            bookInfo.setBookISBN(bookISBN);
            bookInfo.setCategory(categoryCode);
            bookInfo.setBookname(values[2]);
            bookInfo.setWriter(values[3]);
            bookInfo.setPublisher(values[4]);
            bookInfo.setTranslator(values[5]);
            try {
                DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                bookInfo.setDate(LocalDate.parse(values[6], dateFormatter));
            } catch (DateTimeParseException e) {
                JOptionPane.showMessageDialog(null, "出版日期格式错误，应为yyyy-MM-dd", "错误", JOptionPane.ERROR_MESSAGE);
                return false;
            }
            bookInfo.setPrice(Double.parseDouble(values[7]));
            bookInfoMapper.insert(bookInfo);
            Stockpile stockpile = new Stockpile();
            stockpile.setBookISBN(bookISBN);
            stockpile.setStockQuantity(0);
            stockpileMapper.insert(stockpile);
            return true;
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "单价必须是有效数字", "错误", JOptionPane.ERROR_MESSAGE);
            return false;
        } catch (Exception e) {
            log.error("Failed to add book to database", e);
            String message = e.getMessage() == null ? "" : e.getMessage();
            if (message.contains("Duplicate entry") && message.contains("bookISBN")) {
                JOptionPane.showMessageDialog(null, "图书ISBN已存在", "错误", JOptionPane.ERROR_MESSAGE);
            } else if (message.contains("cannot be null")) {
                JOptionPane.showMessageDialog(null, "缺少必填字段", "错误", JOptionPane.ERROR_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(null, "数据库错误: " + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
            }
            return false;
        }
    }

    private void clearForm() {
        for (java.awt.Component comp : inputComponents) {
            if (comp instanceof JTextField) {
                ((JTextField) comp).setText("");
            } else if (comp instanceof JComboBox && ((JComboBox<?>) comp).getItemCount() > 0) {
                ((JComboBox<?>) comp).setSelectedIndex(0);
            }
        }
        if (!inputComponents.isEmpty() && inputComponents.get(0) instanceof JTextField) {
            inputComponents.get(0).requestFocus();
        }
    }
}
