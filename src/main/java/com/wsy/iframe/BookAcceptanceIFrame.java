package com.wsy.iframe;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.beans.PropertyVetoException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import static com.wsy.auxiliary.AuxiliaryTools.createLabel;
import static com.wsy.auxiliary.AuxiliaryTools.createRadio;
import static com.wsy.auxiliary.AuxiliaryTools.createText;
import com.wsy.mapper.OperatorMapper;
import com.wsy.mapper.OrderMapper;
import com.wsy.mapper.StockpileMapper;
import com.wsy.model.Operator;
import com.wsy.model.Stockpile;
public class BookAcceptanceIFrame {
    private final List<java.awt.Component> formComponents = new ArrayList<>();
    private String selectedBookISBN;
    private LocalDate selectedOrderDate;
    private JTable orderTable;
    private DefaultTableModel tableModel;
    private JInternalFrame frame;
    private final Map<Integer, String> operatorMap = new HashMap<>(); 
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private OperatorMapper operatorMapper;
    @Autowired
    private StockpileMapper stockpileMapper;
    private JButton acceptButton; // 类级别保存验收按钮引用
    private JButton cancelButton; // 取消验收按钮（仅未验收时可用）
    public void setOrderMapper(OrderMapper orderMapper) {
        this.orderMapper = orderMapper;
    }
    public void setOperatorMapper(OperatorMapper operatorMapper) {
        this.operatorMapper = operatorMapper;
    }
    public void setStockpileMapper(StockpileMapper stockpileMapper) {
        this.stockpileMapper = stockpileMapper;
    }
    private double convertToDouble(Object value) {
        if (value instanceof Float) {
            return ((Float) value).doubleValue();
        } else if (value instanceof Double) {
            return (Double) value;
        } else if (value instanceof Number) {
            return ((Number) value).doubleValue();
        } else {
            return 0.0;
        }
    }
    public JInternalFrame createBookAcceptanceIFrame() throws PropertyVetoException {
        loadOperatorMap();
        frame = new JInternalFrame("图书验收", true, true, true, true);
        frame.setSize(800, 600);
        frame.setResizable(true);
        frame.setLayout(new BorderLayout());
        frame.setDefaultCloseOperation(JInternalFrame.DISPOSE_ON_CLOSE);
        frame.setMaximum(true);
        JPanel panelTop = new JPanel(new BorderLayout());
        panelTop.setPreferredSize(new Dimension(0, 300));
        String[] columnNames = {"订购日期", "书籍ISBN", "书籍名称", "图书类别", "订购数量", "折扣", "图书价格", "订购价格", "操作员"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        orderTable = new JTable(tableModel);
        orderTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        loadUnacceptedOrders();
        // 不再自动因列表点击/选择加载，改由 ISBN 失焦触发
        JScrollPane scrollPane = new JScrollPane(orderTable);
        panelTop.add(scrollPane, BorderLayout.CENTER);
        frame.add(panelTop, BorderLayout.NORTH);
        JPanel panelCenter = new JPanel();
        panelCenter.setLayout(new GridLayout(5, 4, 10, 10));
        panelCenter.setPreferredSize(new Dimension(0, 200));
        String[] labels = {"订购日期：", "书籍ISBN：", "书籍名称：", "图书类别：",
                "订购数量：", "折扣：", "图书原价格：", "订购价格：",
                "操作员：", "是否验收："};
        JRadioButton yesRadio = null;
        JRadioButton noRadio = null;
        JTextField isbnField = null;
        for (int i = 0; i < 10; i++) {
            panelCenter.add(createLabel(labels[i]));
            if (i == 9) { 
                JPanel radioPanel = createRadio("是", "否");
                yesRadio = (JRadioButton) radioPanel.getComponent(0);
                noRadio = (JRadioButton) radioPanel.getComponent(1);
                formComponents.add(yesRadio); 
                formComponents.add(noRadio); 
                panelCenter.add(radioPanel);
            } else {
                JTextField textField = createText();
                // 仅允许 ISBN 手工输入触发自动填充，其余字段保持只读
                if (i == 1) {
                    textField.setEditable(true);
                    isbnField = textField;
                } else {
                    textField.setEditable(false);
                }
                formComponents.add(textField);
                panelCenter.add(textField);
            }
        }
        frame.add(panelCenter, BorderLayout.CENTER);
        JPanel panelBottom = new JPanel();
        panelBottom.setLayout(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        panelBottom.setPreferredSize(new Dimension(0, 60));
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        this.acceptButton = new JButton("验收");
        this.acceptButton.addActionListener(e -> acceptOrder());
        this.cancelButton = new JButton("取消验收");
        this.cancelButton.addActionListener(e -> cancelOrder());
        JButton exitButton = new JButton("退出");
        exitButton.addActionListener(e -> frame.dispose());
        buttonsPanel.add(acceptButton);
        buttonsPanel.add(cancelButton);
        buttonsPanel.add(exitButton);
        panelBottom.add(buttonsPanel);
        frame.add(panelBottom, BorderLayout.SOUTH);
        
        // 添加单选按钮事件监听，控制验收按钮可用性
        ActionListener radioListener = e -> {
            updateButtonState();
        };
        if (yesRadio != null && noRadio != null) {
            yesRadio.addActionListener(radioListener);
            noRadio.addActionListener(radioListener);
            updateButtonState();
        }
        // ISBN 失焦自动按输入填充
        if (isbnField != null) {
            JTextField finalIsbnField = isbnField;
            isbnField.addFocusListener(new java.awt.event.FocusAdapter() {
                @Override
                public void focusLost(java.awt.event.FocusEvent e) {
                    loadFormByISBN(finalIsbnField.getText());
                }
            });
        }
        frame.setVisible(true);
        return frame;
    }
    private void loadOperatorMap() {
        try {
            List<Operator> operators = operatorMapper.selectAll();
            for (Operator operator : operators) {
                operatorMap.put(operator.getId(), operator.getUserName());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void loadUnacceptedOrders() {
        try {
            List<Map<String, Object>> unacceptedOrders = orderMapper.selectUnacceptedOrders();
            tableModel.setRowCount(0);
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            for (Map<String, Object> order : unacceptedOrders) {
                Object orderDateObj = order.get("orderDate");
                LocalDate orderDate;
                if (orderDateObj instanceof LocalDate) {
                    orderDate = (LocalDate) orderDateObj;
                } else if (orderDateObj instanceof java.util.Date) {
                    java.util.Date date = (java.util.Date) orderDateObj;
                    if (orderDateObj instanceof java.sql.Date) {
                        orderDate = new java.sql.Date(date.getTime()).toLocalDate();
                    } else {
                        orderDate = date.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate();
                    }
                } else {
                    orderDate = LocalDate.now();
                }
                String bookISBN = (String) order.get("bookISBN");
                String bookName = (String) order.get("bookname");
                String category = (String) order.get("category");
                int orderQuantity = ((Number) order.get("orderQuantity")).intValue();
                double discount = convertToDouble(order.get("discount"));
                double price = convertToDouble(order.get("price"));
                int operatorId = ((Number) order.get("operator")).intValue();
                String operatorName = operatorMap.getOrDefault(operatorId, "未知");
                double orderPrice = price * orderQuantity * (1 - discount);
                String formattedOrderDate = orderDate.format(dateFormatter);
                Object[] rowData = {
                        formattedOrderDate,
                        bookISBN,
                        bookName,
                        category,
                        orderQuantity,
                        String.format("%.2f", discount), 
                        price,
                        String.format("%.2f", orderPrice), 
                        operatorName
                };
                tableModel.addRow(rowData);
            }
            // 确保模型刷新，清空选中与表单。由 ISBN 失焦触发填充。
            tableModel.fireTableDataChanged();
            orderTable.clearSelection();
            clearForm();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(frame, "加载订单数据失败: " + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
        }
    }
    private void fillFormFromModelRow(int modelRowIndex) {
        try {
            int rowCount = tableModel.getRowCount();
            if (modelRowIndex < 0 || modelRowIndex >= rowCount) return;
            selectedBookISBN = (String) tableModel.getValueAt(modelRowIndex, 1);
            String orderDateStr = (String) tableModel.getValueAt(modelRowIndex, 0);
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            selectedOrderDate = LocalDate.parse(orderDateStr, dateFormatter);
            ((JTextField) formComponents.get(0)).setText(selectedOrderDate.format(dateFormatter));
            ((JTextField) formComponents.get(1)).setText(selectedBookISBN);
            ((JTextField) formComponents.get(2)).setText((String) tableModel.getValueAt(modelRowIndex, 2));
            ((JTextField) formComponents.get(3)).setText((String) tableModel.getValueAt(modelRowIndex, 3));
            ((JTextField) formComponents.get(4)).setText(tableModel.getValueAt(modelRowIndex, 4).toString());
            ((JTextField) formComponents.get(5)).setText(tableModel.getValueAt(modelRowIndex, 5).toString());
            ((JTextField) formComponents.get(6)).setText(tableModel.getValueAt(modelRowIndex, 6).toString());
            ((JTextField) formComponents.get(7)).setText((String) tableModel.getValueAt(modelRowIndex, 7));
            ((JTextField) formComponents.get(8)).setText((String) tableModel.getValueAt(modelRowIndex, 8));
            ((JRadioButton) formComponents.get(10)).setSelected(true);
            updateAcceptButtonState();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(frame, "加载表单数据失败: " + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadFormByISBN(String isbnInput) {
        String isbn = isbnInput == null ? "" : isbnInput.trim();
        if (isbn.isEmpty()) {
            clearForm();
            return;
        }
        int rowCount = tableModel.getRowCount();
        for (int i = 0; i < rowCount; i++) {
            Object val = tableModel.getValueAt(i, 1);
            if (val != null && isbn.equals(val.toString().trim())) {
                orderTable.clearSelection();
                orderTable.addRowSelectionInterval(i, i);
                fillFormFromModelRow(i);
                return;
            }
        }
        JOptionPane.showMessageDialog(frame, "未找到该ISBN的未验收订单: " + isbn, "提示", JOptionPane.WARNING_MESSAGE);
        clearForm();
    }
    private void updateAcceptButtonState() {
        // 更新按钮可用性：'是' -> 只能验收；'否' -> 只能取消
        updateButtonState();
    }
    private void updateButtonState() {
        boolean isAccepted = ((JRadioButton) formComponents.get(9)).isSelected(); // "是"
        boolean isNotAccepted = ((JRadioButton) formComponents.get(10)).isSelected(); // "否"
        if (acceptButton != null) {
            acceptButton.setEnabled(isAccepted);
        }
        if (cancelButton != null) {
            cancelButton.setEnabled(isNotAccepted);
        }
    }
    @Transactional
    private void acceptOrder() {
        if (selectedBookISBN == null || selectedOrderDate == null) {
            JOptionPane.showMessageDialog(frame, "请先选择要验收的订单", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }
        // 检查是否选择了"是"
        boolean isAccepted = ((JRadioButton) formComponents.get(9)).isSelected();
        if (!isAccepted) {
            JOptionPane.showMessageDialog(frame, "请选择'是'才能进行验收", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            int rowsAffected = orderMapper.updateOrderAcceptStatus(selectedBookISBN, selectedOrderDate);
            if (rowsAffected == 0) {
                JOptionPane.showMessageDialog(frame, "验收失败: 未找到订单", "错误", JOptionPane.ERROR_MESSAGE);
                return;
            }
            int orderQuantity = Integer.parseInt(((JTextField) formComponents.get(4)).getText());
            Stockpile stockpile = stockpileMapper.selectByBookISBN(selectedBookISBN);
            if (stockpile != null) {
                stockpile.setStockQuantity(stockpile.getStockQuantity() + orderQuantity);
                stockpileMapper.update(stockpile);
            } else {
                Stockpile newStockpile = new Stockpile();
                newStockpile.setBookISBN(selectedBookISBN);
                newStockpile.setStockQuantity(orderQuantity);
                stockpileMapper.insert(newStockpile);
            }
            loadUnacceptedOrders(); 
            clearForm(); 
            JOptionPane.showMessageDialog(frame, "图书验收成功！库存已更新。", "成功", JOptionPane.INFORMATION_MESSAGE);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(frame, "订购数量格式错误", "错误", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(frame, "验收失败: " + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
        }
    }
    @Transactional
    private void cancelOrder() {
        if (selectedBookISBN == null || selectedOrderDate == null) {
            JOptionPane.showMessageDialog(frame, "请先选择要取消的订单", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }
        boolean isNotAccepted = ((JRadioButton) formComponents.get(10)).isSelected();
        if (!isNotAccepted) {
            JOptionPane.showMessageDialog(frame, "请选择'否'后才能取消验收/订购", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(frame,
                "确定取消该订购记录吗？取消后该订单将被移除。", "确认取消",
                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }
        try {
            int rowsAffected = orderMapper.deleteUnacceptedOrder(selectedBookISBN, selectedOrderDate);
            if (rowsAffected == 0) {
                JOptionPane.showMessageDialog(frame, "取消失败：订单不存在或已验收", "提示", JOptionPane.WARNING_MESSAGE);
                return;
            }
            loadUnacceptedOrders();
            clearForm();
            JOptionPane.showMessageDialog(frame, "已取消该订购记录。", "成功", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(frame, "取消验收失败: " + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
        }
    }
    private void clearForm() {
        selectedBookISBN = null;
        selectedOrderDate = null;
        for (java.awt.Component comp : formComponents) {
            if (comp instanceof JTextField) {
                ((JTextField) comp).setText("");
            }
        }
        if (formComponents.size() > 9) {
            ((JRadioButton) formComponents.get(9)).setSelected(true); 
            // 更新验收按钮状态
            updateButtonState();
        }
    }
}