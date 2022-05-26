package view;

import javax.swing.*;
import java.awt.*;

public class Start extends JFrame {
    static ChessGameFrame mainFrame;
    public boolean Style = true;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Start a = new Start();
        });
    }

    public Start() {
        setLocation(300,200);
        setSize(900, 600);
        setLayout(null);

        JButton StartButton = new JButton("Play");
        StartButton.setLocation(0, 0);
        StartButton.setSize(300, 600);
        StartButton.setFont(new Font("Rockwell", Font.BOLD, 50));
        StartButton.addActionListener(e -> {
            mainFrame = new ChessGameFrame(1000, 760, Style,this);
            setVisible(false);
            mainFrame.setVisible(true);
        });

        add(StartButton);

        JButton Theme = new JButton("Theme");
        Theme.setLocation(300, 0);
        Theme.setSize(300, 600);
        Theme.setFont(new Font("Rockwell", Font.BOLD, 50));
        Theme.addActionListener((e) -> {
            int theme = JOptionPane.showOptionDialog(null, "Choose a theme", " ", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, new String[]{"Landscape", "Sci-fi"}, "LandScape");
            if (theme == 0) {
                Style = true;
            } else Style = false;
        });
        add(Theme);

        JButton ExitButton = new JButton("Close");
        ExitButton.setLocation(600, 0);
        ExitButton.setSize(300, 600);
        ExitButton.setFont(new Font("Rockwell", Font.BOLD, 50));
        add(ExitButton);
        ExitButton.addActionListener(e -> {
            System.exit(0);
        });

        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}
