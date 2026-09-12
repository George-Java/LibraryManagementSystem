package com.wsy.iframe;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.beans.PropertyVetoException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wsy.mapper.BookInfoMapper;
import com.wsy.mapper.BookTypeMapper;
import com.wsy.mapper.OperatorMapper;
import com.wsy.mapper.OrderMapper;
import com.wsy.mapper.StockpileMapper;
import com.wsy.model.BookInfo;
import com.wsy.model.BookType;
import com.wsy.model.Operator;
import com.wsy.model.Order;
import com.wsy.model.Stockpile;
@Service
@RequiredArgsConstructor
@Slf4j
public class BookOrderingIFrame {
    private final BookTypeMapper bookTypeMapper;
    private final OrderMapper orderMapper;
    private final BookInfoMapper bookInfoMapper;
    private final OperatorMapper operatorMapper;
    private final StockpileMapper stockpileMapper;
    private final List<java.awt.Component> bookInfoComponents = new ArrayList<>();
    private final List<java.awt.Component> orderInfoComponents = new ArrayList<>();
    private JRadioButton yesRadio;
    private final Map<String, String> categoryMap = new HashMap<>(); 
    private final Map<String, Integer> operatorMap = new HashMap<>(); 
    public JInternalFrame createBookOrderingIFrame() throws PropertyVetoException {
        bookInfoComponents.clear();
        orderInfoComponents.clear();
        loadCategoriesAndOperators();
        JInternalFrame frame = new JInternalFrame("新书订购管理", true, true, true, true);
        frame.setSize(700, 400);
        frame.setLayout(new BorderLayout());
        frame.setDefaultCloseOperation(JInternalFrame.DISPOSE_ON_CLOSE);
        frame.setMaximum(true); // 设置默认最大化显示
        
        // 创建主面板容器
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // 创建控件主面板 - 使用GridLayout(4, 4)，4行4列，调整间距
        JPanel mainPanel = new JPanel(new GridLayout(4, 4, 15, 20)); // 调整水平和垂直间距
        
        String[] bookLabels = {"书籍ISBN：", "图书名称：", "图书类别：", "作者：", "出版社：", "译者：", "出版日期：", "图书价格："};
        String[] orderLabels = {"订购日期：", "订购数量：", "操作员：", "是否验收：", "折扣："};
        String[] bookCategories = categoryMap.keySet().toArray(new String[0]);
        String[] publishers = loadPublishers();
        String[] operators = operatorMap.keySet().toArray(new String[0]);
        
        // 统一控件高度
        Dimension controlSize = new Dimension(120, 25);
        
        // 所有控件标签和控件组合
        List<String> allLabels = new ArrayList<>();
        allLabels.addAll(Arrays.asList(bookLabels));
        allLabels.addAll(Arrays.asList(orderLabels));
        
        // 添加所有控件（图书信息和订购信息合并）
        for (int i = 0; i < allLabels.size(); i++) {
            String labelText = allLabels.get(i);
            
            // 创建每组的容器面板 - 使用FlowLayout左对齐
            JPanel groupPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
            groupPanel.setPreferredSize(new Dimension(180, 35)); // 统一每组面板大小
            
            // 创建标签 - 左对齐，增加标签宽度确保完全显示
            JLabel label = new JLabel(labelText);
            label.setPreferredSize(new Dimension(100, 25)); // 增加标签宽度
            label.setHorizontalAlignment(SwingConstants.LEFT);
            groupPanel.add(label);
            
            // 根据标签添加对应的控件
            if (labelText.equals("图书类别：")) {
                JComboBox<String> comboBox = new JComboBox<>(bookCategories);
                comboBox.setPreferredSize(controlSize);
                comboBox.setMaximumSize(controlSize);
                groupPanel.add(comboBox);
                bookInfoComponents.add(comboBox);
            } else if (labelText.equals("出版社：")) {
                JComboBox<String> comboBox = new JComboBox<>(publishers);
                comboBox.setPreferredSize(controlSize);
                comboBox.setMaximumSize(controlSize);
                comboBox.setEditable(true); // 设置为可编辑，允许用户输入新出版社
                groupPanel.add(comboBox);
                bookInfoComponents.add(comboBox);
            } else if (labelText.equals("操作员：")) {
                JComboBox<String> comboBox = new JComboBox<>(operators);
                comboBox.setPreferredSize(controlSize);
                comboBox.setMaximumSize(controlSize);
                groupPanel.add(comboBox);
                orderInfoComponents.add(comboBox);
            } else if (labelText.equals("是否验收：")) {
                // 创建单选按钮组面板
                JPanel radioPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
                JRadioButton yes = new JRadioButton("是");
                JRadioButton no = new JRadioButton("否");
                yesRadio = yes;
                ButtonGroup group = new ButtonGroup();
                group.add(yes);
                group.add(no);
                no.setSelected(true);
                radioPanel.add(yes);
                radioPanel.add(no);
                groupPanel.add(radioPanel);
                orderInfoComponents.add(yesRadio);
            } else {
                JTextField textField = new JTextField();
                textField.setPreferredSize(controlSize);
                textField.setMaximumSize(controlSize);
                groupPanel.add(textField);
                if (i < bookLabels.length) {
                    bookInfoComponents.add(textField);
                } else {
                    orderInfoComponents.add(textField);
                }
            }
            
            mainPanel.add(groupPanel);
        }
        
        // 填充剩余的格子 - 4行4列=16个格子，总共13个控件组，需要填充3个
        int remaining = 16 - allLabels.size();
        for (int i = 0; i < remaining; i++) {
            mainPanel.add(new JPanel());
        }
        
        // 按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        JButton addButton = new JButton("添加");
        JButton resetButton = new JButton("重置");
        JButton exitButton = new JButton("退出");
        buttonPanel.add(addButton);
        buttonPanel.add(resetButton);
        buttonPanel.add(exitButton);
        
        // 添加面板到框架
        contentPanel.add(mainPanel, BorderLayout.CENTER);
        contentPanel.add(buttonPanel, BorderLayout.SOUTH);
        frame.add(contentPanel, BorderLayout.CENTER);
        
        addButton.addActionListener(e -> {
            if (!validateRequiredFields(frame)) return;
            String[] bookInfo = extractBookInfo();
            String[] orderInfo = extractOrderInfo();
            try {
                processOrder(bookInfo, orderInfo, frame);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(frame, "数字格式错误: " + ex.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
            } catch (DateTimeParseException ex) {
                JOptionPane.showMessageDialog(frame, "日期格式错误: " + ex.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "订购失败: " + ex.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        exitButton.addActionListener(e -> frame.dispose());
        
        // 重置按钮事件监听器
        resetButton.addActionListener(e -> {
            // 重置图书信息控件
            for (java.awt.Component comp : bookInfoComponents) {
                if (comp instanceof JTextField) {
                    ((JTextField) comp).setText("");
                } else if (comp instanceof JComboBox<?>) {
                    ((JComboBox<?>) comp).setSelectedIndex(0);
                }
            }
            
            // 重置订购信息控件
            for (java.awt.Component comp : orderInfoComponents) {
                if (comp instanceof JTextField) {
                    ((JTextField) comp).setText("");
                } else if (comp instanceof JComboBox<?>) {
                    ((JComboBox<?>) comp).setSelectedIndex(0);
                }
            }
            
            // 重置单选按钮
            if (yesRadio != null) {
                // 获取"否"单选按钮并选中它
                java.awt.Component radioPanel = yesRadio.getParent();
                if (radioPanel instanceof JPanel) {
                    for (java.awt.Component comp : ((JPanel) radioPanel).getComponents()) {
                        if (comp instanceof JRadioButton radio && radio.getText().equals("否")) {
                            radio.setSelected(true);
                            break;
                        }
                    }
                }
            }
        });
        
        frame.setVisible(true);
        return frame;
    }
    private void loadCategoriesAndOperators() {
        try {
            List<BookType> bookTypes = bookTypeMapper.selectAll();
            for (BookType bookType : bookTypes) {
                categoryMap.put(bookType.getTypeName(), bookType.getNumber());
            }
            List<Operator> operators = operatorMapper.selectAll();
            for (Operator operator : operators) {
                operatorMap.put(operator.getUserName(), operator.getId());
            }
        } catch (Exception e) {
            log.error("加载图书类别失败", e);
            categoryMap.put("计算机", "TP0001");
            categoryMap.put("文学", "I00001");
            operatorMap.put("管理员", 101);
        }
    }
    private String[] loadPublishers() {
        List<String> publishers = new ArrayList<>();
        try {
            publishers = bookInfoMapper.selectAllPublishers();
            if (publishers.isEmpty()) {
                publishers.add("机械工业出版社");
                publishers.add("清华大学出版社");
                publishers.add("人民邮电出版社");
            }
        } catch (Exception e) {
            log.error("加载出版社失败", e);
            publishers.add("机械工业出版社");
            publishers.add("清华大学出版社");
            publishers.add("人民邮电出版社");
        }
        return publishers.toArray(new String[0]);
    }
    private boolean validateRequiredFields(JInternalFrame frame) {
        String bookISBN = getComponentValue(bookInfoComponents.get(0));
        String bookName = getComponentValue(bookInfoComponents.get(1));
        String orderQuantity = getComponentValue(orderInfoComponents.get(1)); 
        String orderDate = getComponentValue(orderInfoComponents.get(0)); 
        if (bookISBN == null || bookISBN.trim().isEmpty()) {
            JOptionPane.showMessageDialog(frame, "书籍ISBN不能为空", "错误", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if (bookISBN.length() != 13) {
            JOptionPane.showMessageDialog(frame, "书籍ISBN必须为13位", "错误", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if (bookName == null || bookName.trim().isEmpty()) {
            JOptionPane.showMessageDialog(frame, "图书名称不能为空", "错误", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if (orderDate == null || orderDate.trim().isEmpty()) {
            JOptionPane.showMessageDialog(frame, "订购日期不能为空", "错误", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if (orderQuantity == null || orderQuantity.trim().isEmpty() || !orderQuantity.matches("\\d+")) {
            JOptionPane.showMessageDialog(frame, "订购数量必须为有效整数", "错误", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }
    private String[] extractBookInfo() {
        return new String[]{
                getComponentValue(bookInfoComponents.get(0)), 
                getComponentValue(bookInfoComponents.get(1)), 
                getComponentValue(bookInfoComponents.get(2)), 
                getComponentValue(bookInfoComponents.get(3)), 
                getComponentValue(bookInfoComponents.get(4)), 
                getComponentValue(bookInfoComponents.get(5)), 
                getComponentValue(bookInfoComponents.get(6)), 
                getComponentValue(bookInfoComponents.get(7))  
        };
    }
    private String[] extractOrderInfo() {
        String operatorName = "";
        if (orderInfoComponents.get(2) instanceof JComboBox<?> comboBox) {
            operatorName = comboBox.getSelectedItem() != null ?
                    comboBox.getSelectedItem().toString() : "";
        }
        return new String[]{
                getComponentValue(orderInfoComponents.get(0)), 
                getComponentValue(orderInfoComponents.get(1)), 
                operatorName, 
                yesRadio.isSelected() ? "1" : "0", 
                getComponentValue(orderInfoComponents.get(4))  
        };
    }
    @Transactional(rollbackFor = Exception.class)
    private void processOrder(String[] bookInfo, String[] orderInfo, JInternalFrame frame)
            throws NumberFormatException, DateTimeParseException {
        try {
            String categoryName = bookInfo[2];
            String categoryCode = categoryMap.get(categoryName);
            if (categoryCode == null) {
                JOptionPane.showMessageDialog(frame, "无效的图书类别: " + categoryName, "错误", JOptionPane.ERROR_MESSAGE);
                return;
            }
            String operatorName = orderInfo[2];
            Integer operatorId = operatorMap.get(operatorName);
            if (operatorId == null) {
                JOptionPane.showMessageDialog(frame, "操作员不存在: " + operatorName, "错误", JOptionPane.ERROR_MESSAGE);
                return;
            }
            String bookISBN = bookInfo[0];
            BookInfo existingBook = bookInfoMapper.selectByBookISBN(bookISBN);
            if (existingBook == null) {
                BookInfo newBook = new BookInfo();
                newBook.setBookISBN(bookISBN);
                newBook.setCategory(categoryCode);
                newBook.setBookname(bookInfo[1]);
                newBook.setWriter(bookInfo[3]);
                newBook.setPublisher(bookInfo[4]);
                newBook.setTranslator(bookInfo[5]);
                DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                LocalDate pubDate = LocalDate.parse(bookInfo[6], dateFormat);
                newBook.setDate(pubDate);
                double price = bookInfo[7] != null && !bookInfo[7].isEmpty() ?
                        Double.parseDouble(bookInfo[7]) : 0.0;
                newBook.setPrice(price);
                bookInfoMapper.insert(newBook);
                Stockpile stockpile = new Stockpile();
                stockpile.setBookISBN(bookISBN);
                stockpile.setStockQuantity(0);
                stockpileMapper.insert(stockpile);
            } else {
                // 已存在同 ISBN 的图书时，同步最新的类别与基础信息
                boolean needUpdate = false;
                if (categoryCode != null && !categoryCode.equals(existingBook.getCategory())) {
                    existingBook.setCategory(categoryCode);
                    needUpdate = true;
                }
                String newName = bookInfo[1];
                if (newName != null && !newName.isEmpty() && !newName.equals(existingBook.getBookname())) {
                    existingBook.setBookname(newName);
                    needUpdate = true;
                }
                String newWriter = bookInfo[3];
                if (newWriter != null && !newWriter.isEmpty() && (existingBook.getWriter() == null || !newWriter.equals(existingBook.getWriter()))) {
                    existingBook.setWriter(newWriter);
                    needUpdate = true;
                }
                String newPublisher = bookInfo[4];
                if (newPublisher != null && !newPublisher.isEmpty() && (existingBook.getPublisher() == null || !newPublisher.equals(existingBook.getPublisher()))) {
                    existingBook.setPublisher(newPublisher);
                    needUpdate = true;
                }
                String newTranslator = bookInfo[5];
                if (newTranslator != null && !newTranslator.isEmpty() && (existingBook.getTranslator() == null || !newTranslator.equals(existingBook.getTranslator()))) {
                    existingBook.setTranslator(newTranslator);
                    needUpdate = true;
                }
                if (bookInfo[6] != null && !bookInfo[6].isEmpty()) {
                    DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                    LocalDate pubDate = LocalDate.parse(bookInfo[6], dateFormat);
                    if (existingBook.getDate() == null || !pubDate.equals(existingBook.getDate())) {
                        existingBook.setDate(pubDate);
                        needUpdate = true;
                    }
                }
                if (bookInfo[7] != null && !bookInfo[7].isEmpty()) {
                    double price = Double.parseDouble(bookInfo[7]);
                    if (existingBook.getPrice() == null || existingBook.getPrice() != price) {
                        existingBook.setPrice(price);
                        needUpdate = true;
                    }
                }
                if (needUpdate) {
                    bookInfoMapper.update(existingBook);
                }
            }
            Order order = new Order();
            order.setBookISBN(bookISBN);
            DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate orderDate = LocalDate.parse(orderInfo[0], dateFormat);
            order.setDate(orderDate);
            order.setNumber(Integer.parseInt(orderInfo[1]));
            order.setOperator(operatorId);
            order.setCheckAndAccept("1".equals(orderInfo[3]) ? 1 : 0);
            float discount = orderInfo[4] != null && !orderInfo[4].isEmpty() ?
                    Float.parseFloat(orderInfo[4]) : 0.0f;
            order.setDiscount(discount);
            orderMapper.insert(order);
            if ("1".equals(orderInfo[3])) {
                int orderQuantity = Integer.parseInt(orderInfo[1]);
                // 使用 ISBN 查询库存，避免 selectByBarcode 取不到记录导致重复插入或不更新
                Stockpile stockpile = stockpileMapper.selectByBookISBN(bookISBN);
                if (stockpile != null) {
                    stockpile.setStockQuantity(stockpile.getStockQuantity() + orderQuantity);
                    stockpileMapper.update(stockpile);
                } else {
                    stockpile = new Stockpile();
                    stockpile.setBookISBN(bookISBN);
                    stockpile.setStockQuantity(orderQuantity);
                    stockpileMapper.insert(stockpile);
                }
            }
            JOptionPane.showMessageDialog(frame, "新书订购成功！");
        } catch (DateTimeParseException e) {
            throw e;
        } catch (NumberFormatException e) {
            throw e;
        } catch (Exception e) {
            log.error("图书订购失败", e);
            JOptionPane.showMessageDialog(frame, "订购失败: " + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
            throw new RuntimeException(e); 
        }
    }
    private String getComponentValue(java.awt.Component comp) {
        String value = "";
        if (comp instanceof JTextField) {
            value = ((JTextField) comp).getText();
        } else if (comp instanceof JComboBox) {
            Object selectedItem = ((JComboBox<?>) comp).getSelectedItem();
            value = selectedItem != null ? selectedItem.toString() : "";
        } else if (comp instanceof JRadioButton) {
            return ((JRadioButton) comp).isSelected() ? "1" : "0";
        }
        return value != null ? value.trim() : "";
    }
}
