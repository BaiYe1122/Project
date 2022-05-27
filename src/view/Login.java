package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import static view.Start.mainFrame;

public class Login extends JFrame {
    // 用户昵称
    public static String name;
    // 提示标签
    private JLabel label;
    // 昵称输入框
    private JTextField field;
    // 点击按钮
    private JButton button;
    private JLabel label2;
    private JTextField field2;
    private JLabel label3;
    private JTextField field3;
    public Login(Start start, boolean style) {
        setTitle("聊天室");
        setBounds(500, 400, 400, 200);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setLayout(null);

        label = new JLabel();
        label.setFont(new Font("微软雅黑", Font.PLAIN, 18));
        label.setText("请输入你的昵称：");
        label.setBounds(30, 0, 150, 50);
        add(label);

        field = new JTextField();
        field.setBounds(200, 10, 150, 30);
        add(field);
        field2 = new JTextField();
        field2.setBounds(200, 40, 150, 30);
        add(field2);
        label2 = new JLabel();
        label2.setFont(new Font("微软雅黑", Font.PLAIN, 18));
        label2.setText("请输入对手昵称：");
        label2.setBounds(30, 30, 150, 50);
        add(label2);
        field3 = new JTextField();
        field3.setBounds(200, 70, 150, 30);
        add(field3);
        label3 = new JLabel();
        label3.setFont(new Font("微软雅黑", Font.PLAIN, 18));
        label3.setText("请输入服务器IP：");
        label3.setBounds(30, 60, 150, 50);
        add(label3);

        button = new JButton("加入");/**/
        button.addActionListener(e -> {
            // 对话窗体出现
            // 本窗体消失
            mainFrame = new ChessGameFrame(1000, 760, style, start,field.getText(),field2.getText(),field3.getText());
            setVisible(false);
            mainFrame.setVisible(true);
        });
        button.setBounds(100, 100, 150, 30);
        field.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == 10) {
                    button.doClick();
                }
            }
        });
        add(button);

        setVisible(true);
    }

}

