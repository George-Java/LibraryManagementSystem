package com.wsy.iframe;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.beans.PropertyVetoException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import com.wsy.mapper.ReaderMapper;
import com.wsy.model.Reader;
@Service
@RequiredArgsConstructor
@Slf4j
public class ReaderUpdateIFrame {
    private final ReaderMapper readerMapper;
    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private JTable readerTable;
    private DefaultTableModel tableModel;
    private JPanel formPanel;
    private String currentBarcode;
    public JInternalFrame createReaderUpdateIFrame() throws PropertyVetoException {
        JInternalFrame frame = new JInternalFrame("读者信息修改与删除", true, true, true, true);
        frame.setSize(820, 450);
        frame.setLayout(new BorderLayout());
        frame.setDefaultCloseOperation(JInternalFrame.DISPOSE_ON_CLOSE);
        frame.setMaximum(true);
        String[] columnNames = {"读者条形码", "姓名", "性别", "年龄", "职业", "证件类型",
                "证件号码", "最大借书量", "会员有效日期", "电话", "押金", "办证日期"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        readerTable = new JTable(tableModel);
        readerTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane tableScrollPane = new JScrollPane(readerTable);
        tableScrollPane.setPreferredSize(new Dimension(0, 180));
        loadReaderData();
        String[] idTypes = {"工作证", "身份证", "港澳台同胞来往大陆通行证", "学生证"};
        formPanel = ReaderAddIFrame.createPanelTop(idTypes);
        formPanel.setPreferredSize(new Dimension(0, 150));
        setFormEnabled(false);
        JPanel panelBottom = createButtons("保存修改", "删除读者", "重置");
        panelBottom.setPreferredSize(new Dimension(0, 50));
        readerTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = readerTable.getSelectedRow();
                if (selectedRow >= 0) {
                    setFormEnabled(true);
                    loadRowToForm(selectedRow);
                    currentBarcode = (String) tableModel.getValueAt(selectedRow, 0);
                }
            }
        });
        JButton saveButton = (JButton) panelBottom.getComponent(0);
        saveButton.addActionListener(e -> {
            if (currentBarcode == null) {
                JOptionPane.showMessageDialog(frame, "请先选择一个读者", "提示", JOptionPane.WARNING_MESSAGE);
                return;
            }
            java.awt.Component[] components = formPanel.getComponents();
            String name = getTextFieldValue(components, 1);
            String sex = getSelectedRadioText((JPanel) components[3]);
            String ageStr = getTextFieldValue(components, 5);
            String profession = getTextFieldValue(components, 7);
            String idType = (String) ((JComboBox<?>) components[9]).getSelectedItem();
            String idNumber = getTextFieldValue(components, 11);
            String maxNumStr = getTextFieldValue(components, 13);
            String expiryDateStr = getTextFieldValue(components, 15);
            String phone = getTextFieldValue(components, 17);
            String depositStr = getTextFieldValue(components, 19);
            String issueDateStr = getTextFieldValue(components, 21);
            String barcode = getTextFieldValue(components, 23);
            if (name.isEmpty() || idNumber.isEmpty() || barcode.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "姓名、证件号码和读者条形码不能为空！",
                        "输入错误", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (!barcode.matches("R\\d{8}")) {
                JOptionPane.showMessageDialog(frame, "读者条形码格式不正确！应以 'R' 开头后接8位数字",
                        "输入错误", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (!idNumber.matches("\\d{17}[0-9X]")) {
                JOptionPane.showMessageDialog(frame, "证件号码格式不正确！应为18位有效身份证号",
                        "输入错误", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (!phone.isEmpty() && !phone.matches("\\d{11}")) {
                JOptionPane.showMessageDialog(frame, "电话号码格式不正确！应为11位数字",
                        "输入错误", JOptionPane.ERROR_MESSAGE);
                return;
            }
            int age = 0, maxNum = 5;
            float deposit = 100.0f;
            try {
                if (!ageStr.isEmpty()) {
                    age = Integer.parseInt(ageStr);
                    if (age < 0 || age > 120) {
                        JOptionPane.showMessageDialog(frame, "年龄必须在0-120岁之间！",
                                "输入错误", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                }
                if (!maxNumStr.isEmpty()) {
                    maxNum = Integer.parseInt(maxNumStr);
                    if (maxNum < 1 || maxNum > 15) {
                        JOptionPane.showMessageDialog(frame, "最大借书量必须在1-15本之间！",
                                "输入错误", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                }
                if (!depositStr.isEmpty()) {
                    deposit = Float.parseFloat(depositStr);
                    if (deposit < 50) {
                        JOptionPane.showMessageDialog(frame, "押金不能低于50元！",
                                "输入错误", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(frame, "年龄、最大借书量或押金格式错误！",
                        "输入错误", JOptionPane.ERROR_MESSAGE);
                return;
            }
            LocalDate expiryDate;
            LocalDate issueDate;
            try {
                if (!expiryDateStr.isEmpty()) {
                    expiryDate = LocalDate.parse(expiryDateStr, dateFormatter);
                } else {
                    expiryDate = LocalDate.now().plusYears(1);
                }
                if (!issueDateStr.isEmpty()) {
                    issueDate = LocalDate.parse(issueDateStr, dateFormatter);
                } else {
                    issueDate = LocalDate.now();
                }
            } catch (DateTimeParseException ex) {
                JOptionPane.showMessageDialog(frame, "日期格式错误！请使用 yyyy-MM-dd 格式",
                        "输入错误", JOptionPane.ERROR_MESSAGE);
                return;
            }
            try {
                Reader reader = new Reader();
                reader.setBarcode(currentBarcode); 
                reader.setName(name);
                reader.setSex(sex);
                reader.setAge(age);
                reader.setProfession(profession);
                reader.setType(idType);
                reader.setIdentityCard(idNumber);
                reader.setMaxNum(maxNum);
                reader.setDate(expiryDate);
                reader.setPhone(phone);
                reader.setKeepMoney(deposit);
                reader.setDateOfIssuance(issueDate);
                int affectedRows = readerMapper.update(reader);
                if (affectedRows > 0) {
                    JOptionPane.showMessageDialog(frame, "读者信息修改成功！",
                            "成功", JOptionPane.INFORMATION_MESSAGE);
                    loadReaderData();
                    currentBarcode = barcode;
                } else {
                    JOptionPane.showMessageDialog(frame, "修改失败，可能是未找到该读者记录！",
                            "错误", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                handleDatabaseError(frame, ex);
            }
        });
        JButton deleteButton = (JButton) panelBottom.getComponent(1);
        deleteButton.addActionListener(e -> {
            if (currentBarcode == null) {
                JOptionPane.showMessageDialog(frame, "请先选择一个读者", "提示", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int selectedRow = readerTable.getSelectedRow();
            String readerName = (String) tableModel.getValueAt(selectedRow, 1);
            int confirm = JOptionPane.showConfirmDialog(frame,
                    "确定要删除读者 '" + readerName + "' (条形码: " + currentBarcode + ") 吗？",
                    "确认删除", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    int affectedRows = readerMapper.deleteByBarcode(currentBarcode);
                    if (affectedRows > 0) {
                        JOptionPane.showMessageDialog(frame, "读者删除成功！",
                                "成功", JOptionPane.INFORMATION_MESSAGE);
                        loadReaderData();
                        clearForm();
                        setFormEnabled(false);
                        currentBarcode = null;
                    } else {
                        JOptionPane.showMessageDialog(frame, "删除失败，可能是该读者不存在或存在借书记录！",
                                "错误", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception ex) {
                    handleDatabaseError(frame, ex);
                }
            }
        });
        JButton resetButton = (JButton) panelBottom.getComponent(2);
        resetButton.addActionListener(e -> {
            if (currentBarcode != null) {
                int selectedRow = readerTable.getSelectedRow();
                if (selectedRow >= 0) {
                    loadRowToForm(selectedRow);
                }
            }
        });
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, tableScrollPane, formPanel);
        splitPane.setDividerLocation(180);
        splitPane.setResizeWeight(0.4);
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.add(splitPane, BorderLayout.CENTER);
        frame.add(contentPanel, BorderLayout.CENTER);
        frame.add(panelBottom, BorderLayout.SOUTH);
        return frame;
    }
    private void loadReaderData() {
        try {
            tableModel.setRowCount(0);
            List<Reader> readers = readerMapper.selectAll();
            for (Reader reader : readers) {
                Object[] row = {
                        reader.getBarcode(),
                        reader.getName(),
                        reader.getSex(),
                        reader.getAge(),
                        reader.getProfession(),
                        reader.getType(),
                        reader.getIdentityCard(),
                        reader.getMaxNum(),
                        reader.getDate() != null ? dateFormatter.format(reader.getDate()) : "",
                        reader.getPhone(),
                        reader.getKeepMoney(),
                        reader.getDateOfIssuance() != null ? dateFormatter.format(reader.getDateOfIssuance()) : ""
                };
                tableModel.addRow(row);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "加载读者数据失败: " + ex.getMessage(),
                    "数据库错误", JOptionPane.ERROR_MESSAGE);
            log.error("加载读者数据失败", ex);
        }
    }
    private void loadRowToForm(int rowIndex) {
        java.awt.Component[] components = formPanel.getComponents();
        setTextFieldValue(components, 23, (String) tableModel.getValueAt(rowIndex, 0));
        setTextFieldValue(components, 1, (String) tableModel.getValueAt(rowIndex, 1));
        String sex = (String) tableModel.getValueAt(rowIndex, 2);
        setRadioSelection((JPanel) components[3], sex);
        Object ageValue = tableModel.getValueAt(rowIndex, 3);
        setTextFieldValue(components, 5, ageValue != null ? ageValue.toString() : "");
        setTextFieldValue(components, 7, (String) tableModel.getValueAt(rowIndex, 4));
        String idType = (String) tableModel.getValueAt(rowIndex, 5);
        setComboBoxSelection((JComboBox<?>) components[9], idType);
        setTextFieldValue(components, 11, (String) tableModel.getValueAt(rowIndex, 6));
        Object maxNumValue = tableModel.getValueAt(rowIndex, 7);
        setTextFieldValue(components, 13, maxNumValue != null ? maxNumValue.toString() : "");
        String expiryDate = (String) tableModel.getValueAt(rowIndex, 8);
        if (expiryDate != null && expiryDate.length() > 10) {
            setTextFieldValue(components, 15, expiryDate.substring(0, 10));
        } else {
            setTextFieldValue(components, 15, expiryDate != null ? expiryDate : "");
        }
        setTextFieldValue(components, 17, (String) tableModel.getValueAt(rowIndex, 9));
        Object depositValue = tableModel.getValueAt(rowIndex, 10);
        setTextFieldValue(components, 19, depositValue != null ? depositValue.toString() : "");
        setTextFieldValue(components, 21, (String) tableModel.getValueAt(rowIndex, 11));
    }
    private void setFormEnabled(boolean enabled) {
        java.awt.Component[] components = formPanel.getComponents();
        for (int i = 0; i < components.length; i++) {
            if (i == 23) {
                components[i].setEnabled(false);
            } else {
                components[i].setEnabled(enabled);
                if (components[i] instanceof JPanel) {
                    for (java.awt.Component innerComp : ((JPanel) components[i]).getComponents()) {
                        innerComp.setEnabled(enabled);
                    }
                }
            }
        }
    }
    private void clearForm() {
        java.awt.Component[] components = formPanel.getComponents();
        for (java.awt.Component component : components) {
            if (component instanceof JTextField) {
                ((JTextField) component).setText("");
            } else if (component instanceof JComboBox) {
                ((JComboBox<?>) component).setSelectedIndex(0);
            } else if (component instanceof JPanel) {
                for (java.awt.Component innerComp : ((JPanel) component).getComponents()) {
                    if (innerComp instanceof JRadioButton radio) {
                        radio.setSelected(radio.getText().equals("男"));
                    }
                }
            }
        }
    }
    private String getTextFieldValue(java.awt.Component[] components, int index) {
        java.awt.Component comp = components[index];
        if (comp instanceof JPanel) {
            // 处理日期选择器面板
            JPanel datePickerPanel = (JPanel) comp;
            for (java.awt.Component innerComp : datePickerPanel.getComponents()) {
                if (innerComp instanceof JFormattedTextField) {
                    return ((JFormattedTextField) innerComp).getText().trim();
                } else if (innerComp instanceof JTextField) {
                    return ((JTextField) innerComp).getText().trim();
                }
            }
            return "";
        } else if (comp instanceof JTextField) {
            return ((JTextField) comp).getText().trim();
        }
        return "";
    }
    private void setTextFieldValue(java.awt.Component[] components, int index, String value) {
        java.awt.Component comp = components[index];
        if (comp instanceof JPanel) {
            // 处理日期选择器面板
            JPanel datePickerPanel = (JPanel) comp;
            for (java.awt.Component innerComp : datePickerPanel.getComponents()) {
                if (innerComp instanceof JFormattedTextField) {
                    ((JFormattedTextField) innerComp).setText(value != null ? value : "");
                    return;
                } else if (innerComp instanceof JTextField) {
                    ((JTextField) innerComp).setText(value != null ? value : "");
                    return;
                }
            }
        } else if (comp instanceof JTextField) {
            ((JTextField) comp).setText(value != null ? value : "");
        }
    }
    private String getSelectedRadioText(JPanel panel) {
        for (java.awt.Component comp : panel.getComponents()) {
            if (comp instanceof JRadioButton radio) {
                if (radio.isSelected()) {
                    return radio.getText();
                }
            }
        }
        return "男";
    }
    private void setRadioSelection(JPanel panel, String value) {
        for (java.awt.Component comp : panel.getComponents()) {
            if (comp instanceof JRadioButton radio) {
                radio.setSelected(radio.getText().equals(value));
            }
        }
    }
    private void setComboBoxSelection(JComboBox<?> comboBox, String value) {
        for (int i = 0; i < comboBox.getItemCount(); i++) {
            if (comboBox.getItemAt(i).equals(value)) {
                comboBox.setSelectedIndex(i);
                return;
            }
        }
        if (comboBox.getItemCount() > 0) {
            comboBox.setSelectedIndex(0);
        }
    }
    private void handleDatabaseError(JInternalFrame frame, Exception ex) {
        String errorMsg;
        String message = ex.getMessage();
        if (message != null) {
            if (message.contains("Duplicate entry") && message.contains("identityCard")) {
                errorMsg = "证件号码已存在！";
            } else if (message.contains("Duplicate entry") && message.contains("barcode")) {
                errorMsg = "读者条形码已存在！";
            } else if (message.contains("Duplicate entry")) {
                errorMsg = "数据冲突: 唯一约束违反！";
            } else if (message.contains("foreign key constraint")) {
                errorMsg = "无法删除，该读者可能有未归还的图书！";
            } else {
                errorMsg = "数据库错误: " + message;
            }
        } else {
            errorMsg = "数据库错误: 未知错误";
        }
        JOptionPane.showMessageDialog(frame, errorMsg,
                "数据库错误", JOptionPane.ERROR_MESSAGE);
        log.error("读者数据库操作失败", ex);
    }
    private JPanel createButtons(String... labels) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        for (String label : labels) {
            JButton button = new JButton(label);
            button.setPreferredSize(new Dimension(120, 35));
            panel.add(button);
        }
        return panel;
    }
}
