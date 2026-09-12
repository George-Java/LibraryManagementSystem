package com.wsy.iframe;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.beans.PropertyVetoException;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JComboBox;
import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import static com.wsy.auxiliary.AuxiliaryTools.addClickListener;
import static com.wsy.auxiliary.AuxiliaryTools.createButtons;
import static com.wsy.auxiliary.AuxiliaryTools.createComboBox;
import static com.wsy.auxiliary.AuxiliaryTools.createLabel;
import static com.wsy.auxiliary.AuxiliaryTools.createText;
import com.wsy.auxiliary.JImageView;
import com.wsy.mapper.BookInfoMapper;
import com.wsy.mapper.BookTypeMapper;
import com.wsy.model.BookInfo;
import com.wsy.model.BookType;
@Service
@RequiredArgsConstructor
@Slf4j
public class BookUpdateIFrame {
    private static final ArrayList<java.awt.Component> components = new ArrayList<>();
    private static String selectedBookISBN = null; 
    private final DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private JInternalFrame frame; 
    private final Map<String, String> categoryMap = new HashMap<>(); 
    private final Map<String, String> reverseCategoryMap = new HashMap<>(); 
    private final ArrayList<String> publisherList = new ArrayList<>(); 
    private final BookInfoMapper bookInfoMapper;
    private final BookTypeMapper bookTypeMapper;
    public JInternalFrame createBookUpdateIFrame() throws IOException, PropertyVetoException {
        frame = new JInternalFrame("图书信息修改", true, true, true, true);
        frame.setSize(800, 450);
        frame.setLayout(new BorderLayout());
        frame.setDefaultCloseOperation(JInternalFrame.DISPOSE_ON_CLOSE);
        frame.setMaximum(true);
        loadCategoriesAndPublishers();
        JImageView panelTop = new JImageView("./res/bookUpdate.jpeg");
        panelTop.setPreferredSize(new Dimension(0, 100));
        frame.add(panelTop, BorderLayout.NORTH);
        JScrollPane scrollPane = new JScrollPane();
        String[] columnNames = {"图书ISBN", "类别", "书名", "作者", "出版社", "译者", "出版日期", "单价"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);
        List<BookInfo> allBooks = bookInfoMapper.selectAll();
        for (BookInfo book : allBooks) {
            String categoryName = categoryMap.get(book.getCategory());
            if (categoryName == null) {
                categoryName = "未知类别";
            }
            Object[] row = {
                book.getBookISBN(),
                categoryName,
                book.getBookname(),
                book.getWriter(),
                book.getPublisher(),
                book.getTranslator(),
                book.getDate() != null ? dateFormat.format(book.getDate()) : "",
                book.getPrice()
            };
            tableModel.addRow(row);
        }
        JTable table = new JTable(tableModel);
        int columnCount = tableModel.getColumnCount();
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = table.getSelectedRow();
                if (selectedRow >= 0) {
                    selectedBookISBN = table.getValueAt(selectedRow, 0).toString();
                    String categoryCode = findCategoryCodeByISBN(selectedBookISBN);
                    for (int i = 0; i < columnCount; i++) {
                        Object value = table.getValueAt(selectedRow, i);
                        java.awt.Component comp = components.get(i);
                        if (comp instanceof JTextField) {
                            ((JTextField) comp).setText(value != null ? value.toString() : "");
                        } else if (comp instanceof JComboBox<?> comboBox) {
                            if (i == 1) {
                                String categoryName = categoryMap.get(categoryCode);
                                if (categoryName != null) {
                                    comboBox.setSelectedItem(categoryName);
                                }
                            } else {
                                comboBox.setSelectedItem(value);
                            }
                        }
                    }
                }
            }
        });
        scrollPane.setViewportView(table);
        frame.add(scrollPane, BorderLayout.CENTER);
        JPanel container = new JPanel();
        container.setLayout(new BorderLayout());
        container.setPreferredSize(new Dimension(0, 140));
        JPanel panelCenter = new JPanel();
        panelCenter.setLayout(new GridLayout(3, 6, 10, 10));
        panelCenter.setPreferredSize(new Dimension(0, 100));
        String[] bookInfoLabels = {"图书ISBN：", "类别：", "书名：", "作者：", "出版社：", "译者：", "出版日期：", "单价："};
        String[] bookCategories = categoryMap.values().toArray(new String[0]);
        String[] publishers = publisherList.toArray(new String[0]);
        for (int i = 0; i < 8; i++) {
            panelCenter.add(createLabel(bookInfoLabels[i]));
            if (i == 1) { 
                JComboBox<String> comboBox = createComboBox(bookCategories);
                panelCenter.add(comboBox);
                components.add(comboBox);
            } else if (i == 4) { 
                JComboBox<String> comboBox = createComboBox(publishers, true);
                panelCenter.add(comboBox);
                components.add(comboBox);
            } else {
                JTextField textBookInfo = createText();
                panelCenter.add(textBookInfo);
                components.add(textBookInfo);
            }
        }
        container.add(panelCenter, BorderLayout.NORTH);
        JPanel panelBottom = createButtons("修改", "删除");
        panelBottom.setPreferredSize(new Dimension(0, 40));
        addClickListener(panelBottom.getComponent(0), () -> {
            if (selectedBookISBN == null) {
                JOptionPane.showMessageDialog(frame, "请选择一本书", "提示", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            String[] newValues = new String[components.size()];
            for (int i = 0; i < components.size(); i++) {
                if (components.get(i) instanceof JTextField textField) {
                    newValues[i] = textField.getText();
                } else if (components.get(i) instanceof JComboBox<?> comboBox) {
                    String s = comboBox.getSelectedItem().toString();
                    newValues[i] = s != null ? s : "";
                }
            }
            String selectedCategoryName = newValues[1];
            String categoryCode = reverseCategoryMap.get(selectedCategoryName);
            if (categoryCode == null) {
                JOptionPane.showMessageDialog(frame, "无效的类别", "错误", JOptionPane.ERROR_MESSAGE);
                return;
            }
            try {
                BookInfo bookInfo = new BookInfo();
                bookInfo.setBookISBN(newValues[0]);
                bookInfo.setCategory(categoryCode);
                bookInfo.setBookname(newValues[2]);
                bookInfo.setWriter(newValues[3]);
                bookInfo.setPublisher(newValues[4]);
                bookInfo.setTranslator(newValues[5]);
                LocalDate date = LocalDate.parse(newValues[6], dateFormat);
                bookInfo.setDate(date);
                bookInfo.setPrice(Double.parseDouble(newValues[7]));
                int rows = bookInfoMapper.update(bookInfo);
                if (rows > 0) {
                    JOptionPane.showMessageDialog(frame, "修改成功");
                    refreshTable(scrollPane, table);
                } else {
                    JOptionPane.showMessageDialog(frame, "修改失败，请检查数据");
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(frame, "单价必须是有效数字", "错误", JOptionPane.ERROR_MESSAGE);
            } catch (Exception e) {
                log.error("修改图书失败", e);
                JOptionPane.showMessageDialog(frame, "修改失败: " + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
            }
        });
        addClickListener(panelBottom.getComponent(1), () -> {
            if (selectedBookISBN == null) {
                JOptionPane.showMessageDialog(frame, "请先选择要删除的图书");
                return;
            }
            int confirm = JOptionPane.showConfirmDialog(
                    frame,
                    "确定要删除ISBN为 " + selectedBookISBN + " 的图书吗？",
                    "确认删除",
                    JOptionPane.YES_NO_OPTION
            );
            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    int rowsAffected = bookInfoMapper.deleteByBarcode(selectedBookISBN);
                    if (rowsAffected > 0) {
                        JOptionPane.showMessageDialog(frame, "删除成功");
                        refreshTable(scrollPane, table);
                        clearForm();
                        selectedBookISBN = null;
                    } else {
                        JOptionPane.showMessageDialog(frame, "删除失败，未找到该图书");
                    }
                } catch (Exception ex) {
                    log.error("删除图书失败", ex);
                    JOptionPane.showMessageDialog(
                            frame,
                            "删除失败: " + ex.getMessage(),
                            "错误",
                            JOptionPane.ERROR_MESSAGE
                    );
                }
            }
        });
        container.add(panelBottom, BorderLayout.SOUTH);
        frame.add(container, BorderLayout.SOUTH);
        frame.setVisible(true);
        return frame;
    }
    private void loadCategoriesAndPublishers() {
        List<BookType> categories = bookTypeMapper.selectAll();
        for (BookType category : categories) {
            String number = category.getNumber();
            String typeName = category.getTypeName();
            categoryMap.put(number, typeName);
            reverseCategoryMap.put(typeName, number);
        }
        List<BookInfo> allBooks = bookInfoMapper.selectAll();
        for (BookInfo book : allBooks) {
            String publisher = book.getPublisher();
            if (publisher != null && !publisher.isEmpty() && !publisherList.contains(publisher)) {
                publisherList.add(publisher);
            }
        }
        if (publisherList.isEmpty()) {
            publisherList.add("机械工业出版社");
            publisherList.add("清华大学出版社");
            publisherList.add("人民邮电出版社");
            publisherList.add("复旦大学出版社");
            publisherList.add("高等教育出版社");
        }
    }
    private String findCategoryCodeByISBN(String isbn) {
        try {
            BookInfo book = bookInfoMapper.selectByBookISBN(isbn);
            if (book != null) {
                return book.getCategory();
            }
        } catch (Exception e) {
            log.error("加载图书分类失败", e);
        }
        return null;
    }
    private void refreshTable(JScrollPane scrollPane, JTable oldTable) {
        try {
            List<BookInfo> allBooks = bookInfoMapper.selectAll();
            String[] columnNames = {"bookISBN", "category", "bookname", "writer", "publisher", "translator", "date", "price"};
            DefaultTableModel model = new DefaultTableModel(columnNames, 0);
            for (BookInfo book : allBooks) {
                String categoryName = categoryMap.get(book.getCategory());
                if (categoryName == null) {
                    categoryName = "未知类别";
                }
                Object[] row = {
                    book.getBookISBN(),
                    categoryName,
                    book.getBookname(),
                    book.getWriter(),
                    book.getPublisher(),
                    book.getTranslator(),
                    book.getDate() != null ? dateFormat.format(book.getDate()) : "",
                    book.getPrice()
                };
                model.addRow(row);
            }
            JTable newTable = new JTable(model);
            int columnCount = newTable.getColumnCount();
            newTable.getSelectionModel().addListSelectionListener(e -> {
                if (!e.getValueIsAdjusting()) {
                    int selectedRow = newTable.getSelectedRow();
                    if (selectedRow >= 0) {
                        selectedBookISBN = newTable.getValueAt(selectedRow, 0).toString();
                        String categoryCode = findCategoryCodeByISBN(selectedBookISBN);
                        for (int i = 0; i < columnCount; i++) {
                            Object value = newTable.getValueAt(selectedRow, i);
                            java.awt.Component comp = components.get(i);
                            if (comp instanceof JTextField) {
                                ((JTextField) comp).setText(value != null ? value.toString() : "");
                            } else if (comp instanceof JComboBox<?> comboBox) {
                                if (i == 1) {
                                    String categoryName = categoryMap.get(categoryCode);
                                    if (categoryName != null) {
                                        comboBox.setSelectedItem(categoryName);
                                    }
                                } else {
                                    comboBox.setSelectedItem(value);
                                }
                            }
                        }
                    }
                }
            });
            scrollPane.setViewportView(newTable);
            frame.revalidate();
            frame.repaint();
        } catch (Exception ex) {
            log.error("更新图书失败", ex);
            JOptionPane.showMessageDialog(
                    frame,
                    "刷新表格失败: " + ex.getMessage(),
                    "错误",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }
    private void clearForm() {
        for (java.awt.Component comp : components) {
            if (comp instanceof JTextField) {
                ((JTextField) comp).setText("");
            } else if (comp instanceof JComboBox<?> comboBox && comboBox.getItemCount() > 0) {
                comboBox.setSelectedIndex(0);
            }
        }
    }
}
