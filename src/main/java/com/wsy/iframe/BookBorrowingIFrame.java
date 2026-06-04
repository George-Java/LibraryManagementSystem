package com.wsy.iframe;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyVetoException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import javax.swing.JButton;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import static com.wsy.auxiliary.AuxiliaryTools.createLabel;
import static com.wsy.auxiliary.AuxiliaryTools.createText;
import com.wsy.main.Main;
import com.wsy.mapper.BookInfoMapper;
import com.wsy.mapper.BorrowMapper;
import com.wsy.mapper.OperatorMapper;
import com.wsy.mapper.ReaderMapper;
import com.wsy.mapper.StockpileMapper;
import com.wsy.model.Borrow;
import com.wsy.model.Stockpile;
public class BookBorrowingIFrame {
    private DefaultTableModel tableModel;
    private final List<JTextField> topTextFields = new ArrayList<>();
    private JTextField operatorField;
    private final Map<String, String> operatorMap = new HashMap<>(); 
    private boolean isShowingBorrowRecords = false; 
    private JInternalFrame currentFrame;
    private JScrollPane currentCenterPanel;
    @Autowired
    private BorrowMapper borrowMapper;
    @Autowired
    private BookInfoMapper bookInfoMapper;
    @Autowired
    private OperatorMapper operatorMapper;
    @Autowired
    private StockpileMapper stockpileMapper;
    @Autowired
    private ReaderMapper readerMapper;
    public void setBorrowMapper(BorrowMapper borrowMapper) {
        this.borrowMapper = borrowMapper;
    }
    public void setBookInfoMapper(BookInfoMapper bookInfoMapper) {
        this.bookInfoMapper = bookInfoMapper;
    }
    public void setOperatorMapper(OperatorMapper operatorMapper) {
        this.operatorMapper = operatorMapper;
    }
    public void setStockpileMapper(StockpileMapper stockpileMapper) {
        this.stockpileMapper = stockpileMapper;
    }
    public void setReaderMapper(ReaderMapper readerMapper) {
        this.readerMapper = readerMapper;
    }
    public JInternalFrame createBookBorrowIFrame() throws PropertyVetoException {
        JInternalFrame frame = new JInternalFrame("图书借阅", true, true, true, true);
        frame.setSize(800, 500);
        frame.setResizable(true);
        frame.setLayout(new BorderLayout());
        frame.setDefaultCloseOperation(JInternalFrame.DISPOSE_ON_CLOSE);
        frame.setMaximum(true); // 设置默认最大化显示
        this.currentFrame = frame;
        try {
            loadOperatorMap();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(frame, "加载操作员信息失败: " + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
            return frame;
        }
        JPanel panelTop = new JPanel();
        panelTop.setLayout(new GridLayout(4, 4, 10, 10));
        panelTop.setPreferredSize(new Dimension(0, 150));
        String[] labels = {"读者编号：", "书籍ISBN：", "读者姓名：", "书籍名称：",
                "可借数量：", "书籍类别：", "押金：", "书籍价格："};
        for (int i = 0; i < 8; i++) {
            panelTop.add(createLabel(labels[i]));
            JTextField textField = createText();
            topTextFields.add(textField);
            panelTop.add(textField);
            if (i == 0 || i == 1) {
                int idx = i;
                textField.addFocusListener(new java.awt.event.FocusAdapter() {
                    public void focusLost(java.awt.event.FocusEvent evt) {
                        fetchAdditionalInfo(frame, idx == 0 ? "reader" : "book", textField.getText());
                    }
                });
            }
        }
        topTextFields.get(4).setEditable(false);
        topTextFields.get(6).setEditable(false);
        topTextFields.get(7).setEditable(false);
        frame.add(panelTop, BorderLayout.NORTH);
        JScrollPane panelCenter = new JScrollPane();
        panelCenter.setPreferredSize(new Dimension(0, 250));
        loadAvailableBooks(panelCenter, frame);
        frame.add(panelCenter, BorderLayout.CENTER);
        this.currentCenterPanel = panelCenter;
        JPanel panelBottom = new JPanel();
        panelBottom.setLayout(new BorderLayout());
        panelBottom.setPreferredSize(new Dimension(0, 80));
        JPanel panelWest = new JPanel();
        panelWest.setLayout(new GridLayout(2, 2, 10, 10));
        panelWest.setPreferredSize(new Dimension(400, 0));
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String[] labelsBottom = {"当前时间：", "操作员："};
        JTextField timeField = createText();
        operatorField = createText();
        timeField.setText(formatter.format(now));
        String defaultOperator = getDefaultOperatorName();
        operatorField.setText(defaultOperator);
        timeField.setEditable(false);
        panelWest.add(createLabel(labelsBottom[0]));
        panelWest.add(timeField);
        panelWest.add(createLabel(labelsBottom[1]));
        panelWest.add(operatorField);
        panelBottom.add(panelWest, BorderLayout.WEST);
        JPanel panelEast = new JPanel();
        panelEast.setLayout(new GridLayout(3, 1, 10, 10));
        panelEast.setPreferredSize(new Dimension(180, 0));
        JButton borrowButton = createButton("借出当前图书");
        JButton clearButton = createButton("清除所有记录");
        JButton recordsButton = createButton("借阅记录");
        borrowButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                borrowBook(frame);
            }
        });
        clearButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                clearTableData(frame);
            }
        });
        recordsButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (isShowingBorrowRecords) {
                    loadAvailableBooks(currentCenterPanel, currentFrame);
                    recordsButton.setText("借阅记录");
                } else {
                    loadBorrowRecords(currentCenterPanel, currentFrame);
                    recordsButton.setText("可借图书");
                }
                isShowingBorrowRecords = !isShowingBorrowRecords;
            }
        });
        panelEast.add(borrowButton);
        panelEast.add(clearButton);
        panelEast.add(recordsButton);
        panelBottom.add(panelEast, BorderLayout.EAST);
        frame.add(panelBottom, BorderLayout.SOUTH);
        frame.setVisible(true);
        return frame;
    }
    private String getDefaultOperatorName() {
        try {
            if (Main.currentOperator != null) {
                return Main.currentOperator.getUserName();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "管理员"; 
    }
    private void loadOperatorMap() throws Exception {
        List<com.wsy.model.Operator> operators = operatorMapper.selectAll();
        for (com.wsy.model.Operator operator : operators) {
            operatorMap.put(operator.getUserName(), String.valueOf(operator.getId()));
        }
    }
    private void fetchAdditionalInfo(JInternalFrame frame, String type, String value) {
        if (value == null || value.trim().isEmpty()) return;
        try {
            if ("reader".equals(type)) {
                fetchReaderInfo(value);
            } else if ("book".equals(type)) {
                fetchBookInfo(value);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(frame, "数据查询失败: " + ex.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
        }
    }
    private void fetchReaderInfo(String readerNumber) {
        com.wsy.model.Reader reader = readerMapper.selectByBarcode(readerNumber);
        if (reader != null) {
            topTextFields.get(2).setText(reader.getName());
        } else {
            topTextFields.get(2).setText("");
            JOptionPane.showMessageDialog(null, "未找到读者信息", "提示", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    private void fetchBookInfo(String bookISBN) {
        Map<String, Object> bookDetail = bookInfoMapper.selectBookDetailByISBN(bookISBN);
        if (bookDetail != null) {
            topTextFields.get(3).setText((String) bookDetail.get("bookname")); 
            topTextFields.get(5).setText((String) bookDetail.get("category")); 
            double price = (double) bookDetail.get("price");
            topTextFields.get(7).setText(String.format("%.2f", price)); 
            topTextFields.get(6).setText(String.format("%.2f", price * 0.1)); 
            Integer stock = (Integer) bookDetail.get("stockQuantity");
            if (stock == null) {
                topTextFields.get(4).setText("0");
            } else {
                topTextFields.get(4).setText(String.valueOf(stock));
            }
        } else {
            resetBookFields();
            JOptionPane.showMessageDialog(null, "未找到图书信息", "提示", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    private void resetBookFields() {
        topTextFields.get(3).setText(""); 
        topTextFields.get(4).setText(""); 
        topTextFields.get(5).setText(""); 
        topTextFields.get(6).setText(""); 
        topTextFields.get(7).setText(""); 
    }
    private void loadBorrowRecords(JScrollPane panelCenter, JInternalFrame frame) {
        try {
            JPanel tablePanel = new JPanel(new BorderLayout());
            JLabel headerLabel = new JLabel("借阅记录");
            headerLabel.setFont(new Font("宋体", Font.BOLD, 16));
            headerLabel.setForeground(Color.RED);
            headerLabel.setHorizontalAlignment(SwingConstants.CENTER);
            tablePanel.add(headerLabel, BorderLayout.NORTH);
            List<Map<String, Object>> borrowRecords = borrowMapper.selectAllBorrowRecords();
            Vector<String> columnNames = new Vector<>();
            columnNames.add("借阅编号");
            columnNames.add("读者编号");
            columnNames.add("书籍ISBN");
            columnNames.add("操作员");
            columnNames.add("借阅日期");
            columnNames.add("状态");
            Vector<Vector<Object>> rowData = new Vector<>();
            for (Map<String, Object> record : borrowRecords) {
                Vector<Object> row = new Vector<>();
                row.add(record.get("借阅编号"));
                row.add(record.get("读者编号"));
                row.add(record.get("书籍ISBN"));
                row.add(record.get("操作员"));
                row.add(record.get("借阅日期"));
                row.add(record.get("状态"));
                rowData.add(row);
            }
            tableModel = new DefaultTableModel(rowData, columnNames) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false; 
                }
            };
            JTable table = new JTable(tableModel);
            tablePanel.add(new JScrollPane(table), BorderLayout.CENTER);
            panelCenter.setViewportView(tablePanel);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(frame, "加载借阅记录失败: " + ex.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
        }
    }
    private void borrowBook(JInternalFrame frame) {
        String readerNumber = topTextFields.get(0).getText();
        String bookISBN = topTextFields.get(1).getText();
        String borrowableQtyStr = topTextFields.get(4).getText();
        if (readerNumber == null || readerNumber.trim().isEmpty()) {
            JOptionPane.showMessageDialog(frame, "读者编号不能为空", "错误", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (bookISBN == null || bookISBN.trim().isEmpty()) {
            JOptionPane.showMessageDialog(frame, "书籍ISBN不能为空", "错误", JOptionPane.ERROR_MESSAGE);
            return;
        }
        int borrowableQty;
        try {
            borrowableQty = Integer.parseInt(borrowableQtyStr);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(frame, "可借数量无效", "错误", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (borrowableQty <= 0) {
            JOptionPane.showMessageDialog(frame, "库存不足，无法借阅", "错误", JOptionPane.ERROR_MESSAGE);
            return;
        }
        String operatorName = operatorField.getText();
        String operatorIdStr = operatorMap.get(operatorName);
        if (operatorIdStr == null) {
            JOptionPane.showMessageDialog(frame, "无效的操作员", "错误", JOptionPane.ERROR_MESSAGE);
            return;
        }
        int operatorId = Integer.parseInt(operatorIdStr);
        try {
            performBorrowOperation(readerNumber, bookISBN, operatorId, borrowableQty - 1);
            JOptionPane.showMessageDialog(frame, "图书借阅成功！");
            loadBorrowRecords((JScrollPane) frame.getContentPane().getComponent(1), frame);
            resetBookFields();
            topTextFields.get(0).setText("");
            topTextFields.get(1).setText("");
            topTextFields.get(2).setText("");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(frame, "借阅失败: " + ex.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
        }
    }
    @Transactional(rollbackFor = Exception.class)
    private void performBorrowOperation(String readerNumber, String bookISBN, int operatorId, int newStock) {
        Borrow borrow = new Borrow();
        borrow.setReaderNumber(readerNumber);
        borrow.setBookISBN(bookISBN);
        borrow.setOperator(operatorId);
        borrow.setIsReturn(0);
        borrow.setBorrowDate(LocalDate.now());
        borrowMapper.insert(borrow);
        Stockpile stockpile = stockpileMapper.selectByBookISBN(bookISBN);
        if (stockpile != null) {
            stockpile.setStockQuantity(newStock);
            stockpileMapper.update(stockpile);
        } else {
            stockpile = new Stockpile();
            stockpile.setBookISBN(bookISBN);
            stockpile.setStockQuantity(newStock);
            stockpileMapper.insert(stockpile);
        }
    }
    private void loadAvailableBooks(JScrollPane panelCenter, JInternalFrame frame) {
        try {
            JPanel tablePanel = new JPanel(new BorderLayout());
            JLabel headerLabel = new JLabel("可借图书列表");
            headerLabel.setFont(new java.awt.Font("宋体", Font.BOLD, 16));
            headerLabel.setForeground(Color.RED);
            headerLabel.setHorizontalAlignment(SwingConstants.CENTER);
            tablePanel.add(headerLabel, BorderLayout.NORTH);
            List<Map<String, Object>> availableBooks = bookInfoMapper.selectAllAvailableBooks();
            Vector<String> columnNames = new Vector<>();
            columnNames.add("图书ISBN");
            columnNames.add("类别");
            columnNames.add("书名");
            columnNames.add("作者");
            columnNames.add("出版社");
            columnNames.add("出版日期");
            columnNames.add("单价");
            columnNames.add("库存量");
            Vector<Vector<Object>> rowData = new Vector<>();
            for (Map<String, Object> record : availableBooks) {
                Vector<Object> row = new Vector<>();
                row.add(record.get("bookISBN"));
                row.add(record.get("category"));
                row.add(record.get("bookname"));
                row.add(record.get("writer"));
                row.add(record.get("publisher"));
                row.add(record.get("date"));
                row.add(record.get("price"));
                row.add(record.get("stockQuantity"));
                rowData.add(row);
            }
            tableModel = new DefaultTableModel(rowData, columnNames) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false; 
                }
            };
            JTable table = new JTable(tableModel);
            table.getSelectionModel().addListSelectionListener(e -> {
                if (!e.getValueIsAdjusting()) {
                    int selectedRow = table.getSelectedRow();
                    if (selectedRow != -1) {
                        String bookISBN = (String) table.getValueAt(selectedRow, 0); 
                        String category = (String) table.getValueAt(selectedRow, 1); 
                        String bookname = (String) table.getValueAt(selectedRow, 2); 
                        Double price = (Double) table.getValueAt(selectedRow, 6); 
                        Integer stockQuantity = (Integer) table.getValueAt(selectedRow, 7); 
                        topTextFields.get(1).setText(bookISBN); 
                        topTextFields.get(3).setText(bookname); 
                        topTextFields.get(5).setText(category); 
                        topTextFields.get(7).setText(String.format("%.2f", price)); 
                        topTextFields.get(4).setText(String.valueOf(stockQuantity)); 
                        topTextFields.get(6).setText(String.format("%.2f", price * 0.1)); 
                    }
                }
            });
            tablePanel.add(new JScrollPane(table), BorderLayout.CENTER);
            panelCenter.setViewportView(tablePanel);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(frame, "加载图书信息失败: " + ex.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
        }
    }
    private JButton createButton(String label) {
        JButton button = new JButton(label);
        button.setSize(80, 40);
        return button;
    }
    private void clearTableData(JInternalFrame frame) {
        if (tableModel != null) {
            int confirm = JOptionPane.showConfirmDialog(
                    frame,
                    "确定要清除表格中的所有数据吗？",
                    "确认清除",
                    JOptionPane.YES_NO_OPTION
            );
            if (confirm == JOptionPane.YES_OPTION) {
                tableModel.setRowCount(0);
                JOptionPane.showMessageDialog(frame, "表格数据已清除！");
            }
        } else {
            JOptionPane.showMessageDialog(frame, "没有可清除的数据", "信息", JOptionPane.INFORMATION_MESSAGE);
        }
    }
}