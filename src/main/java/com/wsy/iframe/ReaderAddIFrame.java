package com.wsy.iframe;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.beans.PropertyVetoException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import org.springframework.beans.factory.annotation.Autowired;

import static com.wsy.auxiliary.AuxiliaryTools.addClickListener;
import static com.wsy.auxiliary.AuxiliaryTools.createButtons;
import static com.wsy.auxiliary.AuxiliaryTools.createComboBox;
import static com.wsy.auxiliary.AuxiliaryTools.createDatePicker;
import static com.wsy.auxiliary.AuxiliaryTools.createLabel;
import static com.wsy.auxiliary.AuxiliaryTools.createRadio;
import static com.wsy.auxiliary.AuxiliaryTools.createText;
import com.wsy.mapper.ReaderMapper;
import com.wsy.model.Reader;
public class ReaderAddIFrame {
    @Autowired
    private ReaderMapper readerMapper;
    public void setReaderMapper(ReaderMapper readerMapper) {
        this.readerMapper = readerMapper;
    }
    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    public static JPanel createPanelTop(String[] s) {
        JPanel panelTop = new JPanel();
        panelTop.setLayout(new GridLayout(6, 4, 10, 10));
        
        panelTop.add(createLabel("姓名："));
        panelTop.add(createText());
        panelTop.add(createLabel("性别："));
        panelTop.add(createRadio("男", "女"));
        
        panelTop.add(createLabel("年龄："));
        panelTop.add(createText());
        panelTop.add(createLabel("职业："));
        panelTop.add(createText());
        
        panelTop.add(createLabel("有效证件："));
        panelTop.add(createComboBox(s));
        panelTop.add(createLabel("证件号码："));
        panelTop.add(createText());
        
        panelTop.add(createLabel("最大借书量："));
        panelTop.add(createText());
        panelTop.add(createLabel("会员有效日期："));
        panelTop.add(createDatePicker());
        
        panelTop.add(createLabel("电话："));
        panelTop.add(createText());
        panelTop.add(createLabel("押金："));
        panelTop.add(createText());
        
        panelTop.add(createLabel("办证日期："));
        JTextField textField = createText();
        textField.setText(LocalDate.now().format(dateFormatter));
        panelTop.add(textField);
        
        panelTop.add(createLabel("读者条形码："));
        panelTop.add(createText());
        
        return panelTop;
    }
    public JInternalFrame createReaderAddIFrame() throws PropertyVetoException {
        JInternalFrame frame = new JInternalFrame("读者相关信息添加", true, true, true, true);
        frame.setLayout(new BorderLayout());
        frame.setSize(800, 340);
        frame.setDefaultCloseOperation(JInternalFrame.DISPOSE_ON_CLOSE);
        frame.setMaximum(true);
        String[] s = {"工作证", "身份证", "港澳台同胞来往大陆通行证", "学生证"};
        JPanel panelTop = createPanelTop(s);
        JPanel panelBottom = createButtons("保存", "返回");
        addClickListener(panelBottom.getComponent(0), () -> {
            try {
                java.awt.Component[] components = panelTop.getComponents();
                String name = getTextFieldValue(components, 1); 
                String sex = getSelectedRadioText((JPanel) components[3]); 
                String ageStr = getTextFieldValue(components, 5); 
                String profession = getTextFieldValue(components, 7); 
                String type = (String) ((JComboBox<?>) components[9]).getSelectedItem(); 
                String identityCard = getTextFieldValue(components, 11); 
                String maxNumStr = getTextFieldValue(components, 13); 
                String expiryDateStr = getTextFieldValue(components, 15); 
                String phone = getTextFieldValue(components, 17); 
                String depositStr = getTextFieldValue(components, 19); 
                String issueDateStr = getTextFieldValue(components, 21); 
                String barcode = getTextFieldValue(components, 23); 
                if (name.isEmpty() || identityCard.isEmpty() || barcode.isEmpty()) {
                    JOptionPane.showMessageDialog(frame, "姓名、证件号码和读者条形码不能为空！",
                            "输入错误", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                if (!isValidIdentityCard(identityCard)) {
                    JOptionPane.showMessageDialog(frame, "证件号码格式不正确！",
                            "输入错误", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                if (!isValidBarcode(barcode)) {
                    JOptionPane.showMessageDialog(frame, "读者条形码格式不正确！应以 'R' 开头后接8位数字",
                            "输入错误", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                if (!phone.isEmpty() && !isValidPhone(phone)) {
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
                } catch (NumberFormatException e) {
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
                } catch (DateTimeParseException e) {
                    JOptionPane.showMessageDialog(frame, "日期格式错误！请使用 yyyy-MM-dd 格式",
                            "输入错误", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                try {
                    Reader reader = new Reader();
                    reader.setBarcode(barcode);
                    reader.setName(name);
                    reader.setSex(sex);
                    reader.setAge(age);
                    reader.setProfession(profession);
                    reader.setType(type);
                    reader.setIdentityCard(identityCard);
                    reader.setMaxNum(maxNum);
                    reader.setDate(expiryDate);
                    reader.setPhone(phone);
                    reader.setKeepMoney(deposit);
                    reader.setDateOfIssuance(issueDate);
                    int affectedRows = readerMapper.insert(reader);
                    if (affectedRows > 0) {
                        JOptionPane.showMessageDialog(frame, "读者信息保存成功！",
                                "成功", JOptionPane.INFORMATION_MESSAGE);
                        clearForm(panelTop);
                    } else {
                        JOptionPane.showMessageDialog(frame, "保存失败，请重试！",
                                "错误", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception ex) {
                    handleDatabaseError(frame, ex);
                }
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(frame, "发生未知错误: " + e.getMessage(),
                        "错误", JOptionPane.ERROR_MESSAGE);
            }
        });
        addClickListener(panelBottom.getComponent(1), frame::dispose);
        frame.add(panelTop, BorderLayout.CENTER);
        frame.add(panelBottom, BorderLayout.SOUTH);
        frame.setVisible(true);
        return frame;
    }
    @Deprecated
    static void setSQL(String name, String sex, String profession, String type, String identityCard, String phone, String barcode, int age, int maxNum, float deposit, LocalDate expiryDate, LocalDate issueDate, PreparedStatement stat) throws SQLException {
        stat.setString(1, barcode);
        stat.setString(2, name);
        stat.setString(3, sex);
        stat.setInt(4, age);
        stat.setString(5, profession);
        stat.setString(6, type);
        stat.setString(7, identityCard);
        stat.setInt(8, maxNum);
        stat.setDate(9, java.sql.Date.valueOf(expiryDate));
        stat.setString(10, phone);
        stat.setFloat(11, deposit);
        stat.setDate(12, java.sql.Date.valueOf(issueDate));
    }
    private void clearForm(JPanel panel) {
        for (java.awt.Component comp : panel.getComponents()) {
            if (comp instanceof JTextField) {
                ((JTextField) comp).setText("");
            } else if (comp instanceof JComboBox) {
                ((JComboBox<?>) comp).setSelectedIndex(0);
            } else if (comp instanceof JFormattedTextField) {
                ((JFormattedTextField) comp).setText("");
            } else if (comp instanceof JPanel) {
                JPanel innerPanel = (JPanel) comp;
                for (java.awt.Component inner : innerPanel.getComponents()) {
                    if (inner instanceof JTextField) {
                        ((JTextField) inner).setText("");
                    } else if (inner instanceof JFormattedTextField) {
                        ((JFormattedTextField) inner).setText("");
                    } else if (inner instanceof JRadioButton radio) {
                        radio.setSelected(radio.getText().equals("男"));
                    }
                }
            }
        }
    }
    // 辅助方法：获取文本框的值
    private String getTextFieldValue(java.awt.Component[] components, int index) {
        if (index >= components.length) {
            return "";
        }
        
        java.awt.Component comp = components[index];
        if (comp instanceof JTextField) {
            return ((JTextField) comp).getText().trim();
        } else if (comp instanceof JComboBox) {
            return ((JComboBox<?>) comp).getSelectedItem().toString().trim();
        } else if (comp instanceof JFormattedTextField) {
            return ((JFormattedTextField) comp).getText().trim();
        } else if (comp instanceof JPanel) {
            JPanel innerPanel = (JPanel) comp;
            for (java.awt.Component inner : innerPanel.getComponents()) {
                if (inner instanceof JFormattedTextField) {
                    return ((JFormattedTextField) inner).getText().trim();
                } else if (inner instanceof JTextField) {
                    return ((JTextField) inner).getText().trim();
                }
            }
        }
        return "";
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
    private void handleDatabaseError(JInternalFrame frame, Exception ex) {
        String errorMsg = "数据库错误: ";
        if (ex.getMessage() != null) {
            if (ex.getMessage().contains("Duplicate entry") && ex.getMessage().contains("identityCard")) {
                errorMsg += "证件号码已存在！";
            } else if (ex.getMessage().contains("Duplicate entry") && ex.getMessage().contains("barcode")) {
                errorMsg += "读者条形码已存在！";
            } else if (ex.getMessage().contains("Duplicate entry")) {
                errorMsg += "数据已存在！";
            } else {
                errorMsg += ex.getMessage();
            }
        } else {
            errorMsg += "未知错误";
        }
        JOptionPane.showMessageDialog(frame, errorMsg,
                "数据库错误", JOptionPane.ERROR_MESSAGE);
        ex.printStackTrace();
    }
    private boolean isValidIdentityCard(String id) {
        return id.matches("\\d{17}[0-9X]");
    }
    private boolean isValidBarcode(String barcode) {
        return barcode.matches("R\\d{8}");
    }
    private boolean isValidPhone(String phone) {
        return phone.matches("\\d{11}");
    }
}