package com.wsy.iframe;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.beans.PropertyVetoException;
import java.text.MessageFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;

import org.springframework.beans.factory.annotation.Autowired;

import static com.wsy.auxiliary.AuxiliaryTools.createButtons;
import static com.wsy.auxiliary.AuxiliaryTools.createLabel;
import static com.wsy.auxiliary.AuxiliaryTools.createText;
import com.wsy.mapper.BookInfoMapper;
import com.wsy.model.BookInfo;
public class BookSearchIFrame {
    @Autowired
    private BookInfoMapper bookInfoMapper;
    public void setBookInfoMapper(BookInfoMapper bookInfoMapper) {
        this.bookInfoMapper = bookInfoMapper;
    }
    private JTable table;
    private DefaultTableModel tableModel;
    private JInternalFrame frame;
    private JTextField searchField;
    private JTextArea detailArea;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private String formatDate(Object dateObj) {
        if (dateObj == null) {
            return "";
        }
        if (dateObj instanceof LocalDate) {
            return ((LocalDate) dateObj).format(dateFormatter);
        } else if (dateObj instanceof java.util.Date) {
            java.util.Date date = (java.util.Date) dateObj;
            LocalDate localDate;
            if (dateObj instanceof java.sql.Date) {
                localDate = new java.sql.Date(date.getTime()).toLocalDate();
            } else {
                localDate = date.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate();
            }
            return localDate.format(dateFormatter);
        } else {
            return dateObj.toString();
        }
    }
    public JInternalFrame createBookSearchIFrame() throws PropertyVetoException {
        frame = new JInternalFrame("图书搜索", true, true, true, true);
        frame.setSize(800, 500);
        frame.setResizable(true);
        frame.setLayout(new BorderLayout());
        frame.setDefaultCloseOperation(JInternalFrame.DISPOSE_ON_CLOSE);
        frame.setMaximum(true);
        JPanel panelTop = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        panelTop.setPreferredSize(new Dimension(0, 60));
        panelTop.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panelTop.add(createLabel("搜索关键词："));
        searchField = createText();
        searchField.setPreferredSize(new Dimension(250, 30));
        panelTop.add(searchField);
        JComboBox<String> searchOptions = new JComboBox<>(new String[]{"按书名搜索", "按ISBN搜索", "按作者搜索"});
        panelTop.add(searchOptions);
        frame.add(panelTop, BorderLayout.NORTH);
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setResizeWeight(0.7); 
        String[] columnNames = {"ISBN", "书名", "作者", "出版社", "出版日期"};
        tableModel = new DefaultTableModel(columnNames, 0);
        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane tableScroll = new JScrollPane(table);
        splitPane.setTopComponent(tableScroll);
        JPanel detailPanel = new JPanel(new BorderLayout());
        detailPanel.setBorder(BorderFactory.createTitledBorder("图书详情"));
        detailArea = new JTextArea();
        detailArea.setEditable(false);
        detailArea.setFont(new Font("宋体", Font.PLAIN, 14));
        detailArea.setLineWrap(true);
        detailArea.setWrapStyleWord(true);
        JScrollPane detailScroll = new JScrollPane(detailArea);
        detailPanel.add(detailScroll);
        splitPane.setBottomComponent(detailPanel);
        frame.add(splitPane, BorderLayout.CENTER);
        JPanel panelBottom = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        panelBottom.setPreferredSize(new Dimension(0, 50));
        JPanel buttonsPanel = createButtons("搜索", "重置");
        JButton searchButton = (JButton) buttonsPanel.getComponent(0);
        JButton resetButton = (JButton) buttonsPanel.getComponent(1);
        searchButton.addActionListener(e -> {
            String searchType = (String) searchOptions.getSelectedItem();
            String keyword = searchField.getText().trim();
            if (keyword.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "请输入搜索关键词", "提示", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (searchType != null) {
                searchBooks(searchType, keyword);
            }
        });
        resetButton.addActionListener(e -> {
            searchField.setText("");
            tableModel.setRowCount(0);
            detailArea.setText("");
        });
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && table.getSelectedRow() >= 0) {
                int rowIndex = table.getSelectedRow();
                String isbn = (String) tableModel.getValueAt(rowIndex, 0);
                showBookDetails(isbn);
            }
        });
        panelBottom.add(buttonsPanel);
        frame.add(panelBottom, BorderLayout.SOUTH);
        frame.setVisible(true);
        return frame;
    }
    private void searchBooks(String searchType, String keyword) {
        try {
            List<BookInfo> books;
            switch (searchType) {
                case "按书名搜索":
                    books = bookInfoMapper.searchByTitle(keyword);
                    break;
                case "按ISBN搜索":
                    books = bookInfoMapper.searchByISBN(keyword);
                    break;
                case "按作者搜索":
                    books = bookInfoMapper.searchByAuthor(keyword);
                    break;
                default:
                    books = List.of();
            }
            tableModel.setRowCount(0);
            for (BookInfo b : books) {
                Object[] row = {
                    b.getBookISBN(),
                    b.getBookname(),
                    b.getWriter(),
                    b.getPublisher(),
                    b.getDate() != null ? b.getDate().format(dateFormatter) : ""
            };
                tableModel.addRow(row);
            }
            if (tableModel.getRowCount() == 0) {
                JOptionPane.showMessageDialog(frame, "没有找到匹配的图书", "结果", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(frame, "搜索失败: " + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
        }
    }
    private void showBookDetails(String isbn) {
        try {
            Map<String, Object> bookDetail = bookInfoMapper.selectBookDetailByISBN(isbn);
            if (bookDetail != null) {
                String translator = (String) bookDetail.get("translator");
                if (translator == null || translator.trim().isEmpty()) {
                    translator = "无";
                }
                String details = MessageFormat.format(
                        """
                                ISBN: {0}
                                书名: {1}
                                类别: {2}
                                作者: {3}
                                译者: {4}
                                出版社: {5}
                                出版日期: {6}
                                价格: ¥{7, number, #,##0.00}
                                库存: {8} 本""",
                        bookDetail.get("bookISBN"),
                        bookDetail.get("bookname"),
                        bookDetail.get("categoryName"), 
                        bookDetail.get("writer"),
                        translator,  
                        bookDetail.get("publisher"),
                        formatDate(bookDetail.get("date")),
                        bookDetail.get("price"),
                        bookDetail.get("stockQuantity")
                );
                detailArea.setText(details);
            }
        } catch (Exception e) {
            detailArea.setText("获取图书详情失败: " + e.getMessage());
        }
    }
}