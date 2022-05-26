package view;

import controller.GameController;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.plaf.basic.BasicProgressBarUI;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 这个类表示游戏过程中的整个游戏界面，是一切的载体
 */
public class ChessGameFrame extends JFrame implements ActionListener {
    public Clip clip;
    private JLabel statusLabel = new JLabel("WHITE's turn");

    private Chessboard chessboard;
    private final int WIDTH;
    private final int HEIGHT;
    public final int CHESSBOARD_SIZE;
    private GameController gameController;

    JButton undoButton;
    JButton redoButton;

    public JPanel contentPane = new JPanel();

    public boolean Style;

    public JFileChooser jFileChooser1 = new JFileChooser("Chessboard");
    public JFileChooser jFileChooser2 = new JFileChooser("Chessboard");

    private Start start;


    private final ImageIcon background1 = new ImageIcon("images/Chessboard1.png");
    JLabel BackGroundPicture1 = new JLabel(background1);

    private final ImageIcon background2 = new ImageIcon("images/Chessboard2.png");
    JLabel BackGroundPicture2 = new JLabel(background2);


    public ChessGameFrame(int width, int height, boolean theme, Start start) {
        this.start = start;
        Style = theme;
        setTitle("ChessGame"); //设置标题
        this.WIDTH = width;
        this.HEIGHT = height;
        this.CHESSBOARD_SIZE = HEIGHT * 4 / 5;
        setSize(WIDTH, HEIGHT);
        setLocationRelativeTo(null); // Center the window.,
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        setLayout(null);

        this.setVisible(true);
        statusLabel.setLocation(HEIGHT, HEIGHT / 10);
        statusLabel.setSize(200, 60);
        statusLabel.setFont(new Font("Rockwell", Font.BOLD, 20));
        add(statusLabel);

        chessboard = new Chessboard(CHESSBOARD_SIZE, CHESSBOARD_SIZE, this);
        gameController = new GameController(chessboard);
        chessboard.setLocation(HEIGHT / 10, HEIGHT / 10);
        add(chessboard);

        if (theme) {
            BackGroundPicture1.setSize(1000, 760);
            this.getLayeredPane().add(BackGroundPicture1, Integer.valueOf(Integer.MIN_VALUE));
            playBGM2();
        } else {
            BackGroundPicture2.setSize(1000, 760);
            this.getLayeredPane().add(BackGroundPicture2, Integer.valueOf(Integer.MIN_VALUE));
            playBGM1();
        }
        JPanel jPanel = (JPanel) this.getContentPane();
        jPanel.setOpaque(false);


        contentPane.setLocation(HEIGHT, HEIGHT / 10 + 60);
        contentPane.setSize(200, 30);
        contentPane.setOpaque(false);
        add(contentPane);
        addWindowListener(new WindowListener());

        setOperationButtons();
        addRetractButton();
        addStopRetractingButton();
        addLoadButton();
        addSaveButton();
        addReloadButton();
        addTimeLeftButton();
    }

    public void playBGM1() {
        try {
            File musicPath;
            musicPath = new File("Music/BGM1.wav");
            if (musicPath.exists()) {
                AudioInputStream audioInput = AudioSystem.getAudioInputStream(musicPath);
                clip = AudioSystem.getClip();
                clip.open(audioInput);
                FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
                gainControl.setValue(-8.0f);//设置音量，范围为 -60.0f 到 6.0f
                clip.start();
                clip.loop(Clip.LOOP_CONTINUOUSLY);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void playBGM2() {
        try {
            File musicPath;
            musicPath = new File("Music/BGM2.wav");
            if (musicPath.exists()) {
                AudioInputStream audioInput = AudioSystem.getAudioInputStream(musicPath);
                clip = AudioSystem.getClip();
                clip.open(audioInput);
                FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
                gainControl.setValue(-8.0f);//设置音量，范围为 -60.0f 到 6.0f
                clip.start();
                clip.loop(Clip.LOOP_CONTINUOUSLY);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    class WindowListener extends WindowAdapter {
        public void windowClosing(WindowEvent e) {
            start.setVisible(true);
            clip.stop();
        }
    }

    public void updateGUI() {
        reloadProgressBar();
    }

    public void setStatusLabel(String s) {
        statusLabel.setText(s);
        statusLabel.repaint();
    }

    public List<String> Steps = new ArrayList<>();

    private void setOperationButtons() {
        undoButton = new JButton("Undo");
        undoButton.setLocation(HEIGHT + 100, HEIGHT / 10 + 120);
        undoButton.setSize(100, 60);
        undoButton.setFont(new Font("Rockwell", Font.BOLD, 15));
        add(undoButton);

        redoButton = new JButton("Redo");
        redoButton.setLocation(HEIGHT, HEIGHT / 10 + 120); //todo: location
        redoButton.setSize(100, 60);
        redoButton.setFont(new Font("Rockwell", Font.BOLD, 15));
        add(redoButton);
        undoButton.addActionListener(e -> {
            chessboard.undo();
            undoButton.setEnabled(chessboard.canUndo());
            redoButton.setEnabled(chessboard.canRedo());
        });
        redoButton.addActionListener(e -> {
            chessboard.redo();
            undoButton.setEnabled(chessboard.canUndo());
            redoButton.setEnabled(chessboard.canRedo());
        });
    }//👍

    private Thread retractUpdateThread;

    private JButton retractButton;

    private void addRetractButton() {
        retractButton = new JButton("Retract");
        retractButton.addActionListener(e -> {
            if (retractUpdateThread == null || !retractUpdateThread.isAlive()) {
                retractUpdateThread = createNewRetractUpdateThread();
                retractUpdateThread.start();
            }
        });
        retractButton.setLocation(HEIGHT, HEIGHT / 10 + 180);
        retractButton.setSize(100, 60);
        retractButton.setFont(new Font("Rockwell", Font.BOLD, 15));
        add(retractButton);
    }//👍

    private JButton stopRetractButton;

    private void addStopRetractingButton() {
        stopRetractButton = new JButton("Stop Ret.");
        stopRetractButton.addActionListener(e -> {
            if (retractUpdateThread != null && retractUpdateThread.isAlive()) {
                retractUpdateThread.interrupt();
            }
        });
        stopRetractButton.setLocation(HEIGHT+100, HEIGHT / 10 + 180);
        stopRetractButton.setSize(100, 60);
        stopRetractButton.setFont(new Font("Rockwell", Font.BOLD, 15));
        add(stopRetractButton);
    }

    private Thread createNewRetractUpdateThread() {
        return new Thread(() -> {
            setEnablingExceptRetract(false);
            chessboard.loadInstance(0);
            try {
                while (chessboard.canRedo()) {
                    chessboard.redo();
                    Thread.sleep(1000);
                }
            } catch (InterruptedException e) {
                System.out.println("Retracting process interrupted.");
                e.printStackTrace();
            } finally {
                setEnablingExceptRetract(true);
            }
        });

    }

    private void setEnablingExceptRetract(boolean isEnabled) {
        chessboard.clickController.setClickEnabled(isEnabled);
        loadGameButton.setEnabled(isEnabled);
        saveGameButton.setEnabled(isEnabled);
        redoButton.setEnabled(isEnabled);
        undoButton.setEnabled(isEnabled);
        reloadButton.setEnabled(isEnabled);
        timeLeftButton.setEnabled(isEnabled);
    }

    final JProgressBar progressBar = new JProgressBar();

    JButton timeLeftButton;

    private void addTimeLeftButton() {
        timeLeftButton = new JButton("TimeLeft");
        timeLeftButton.addActionListener((e) -> {
            contentPane.removeAll();
            contentPane.repaint();
            contentPane.add(progressBar);
            progressBar.setStringPainted(true);
            progressBar.setBorderPainted(true);
            progressBar.setLocation(HEIGHT, HEIGHT / 10);
            progressBar.setUI(new BasicProgressBarUI() {
                protected Color getSelectionBackground() {
                    return Color.black;
                }

                protected Color getSelectionForeground() {
                    return Color.black;
                }
            });
            reloadProgressBar();
            repaint();
        });
        timeLeftButton.setLocation(HEIGHT, HEIGHT / 10 + 480);
        timeLeftButton.setSize(200, 60);
        timeLeftButton.setFont(new Font("Rockwell", Font.BOLD, 20));
        add(timeLeftButton);
    }

    private Thread progressUpdateThread;

    public void reloadProgressBar() {
        if (progressUpdateThread != null) {
            progressUpdateThread.interrupt();
        }
        progressUpdateThread = createNewProgressUpdateThread();
        progressUpdateThread.start();
    }

    private Thread createNewProgressUpdateThread() {
        return new Thread(() -> {
            final int TIME_LIMIT = 300;
            int progress = 100;
            for (int i = TIME_LIMIT; i >= 0; i--) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    progressBar.setForeground(Color.WHITE);
                    return;
                }
                progress = (int) (((double) i / TIME_LIMIT) * 100);
                progressBar.setValue(progress);
                progressBar.setString((double) i / 10 + " s");
                progressBar.repaint();

                if (progress > 70) {
                    progressBar.setForeground(Color.GREEN);
                } else if (progress > 20) {
                    progressBar.setForeground(Color.YELLOW);
                } else {
                    progressBar.setForeground(Color.RED);
                }
            }
            progressBar.setString("Time Out");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
                progressBar.setForeground(Color.WHITE);
                return;
            }
            progressBar.setForeground(Color.WHITE);
            chessboard.swapColor();
        });
    }

    private JButton reloadButton;

    private void addReloadButton() {
        reloadButton = new JButton("Reload");
        reloadButton.setLocation(HEIGHT, HEIGHT / 10 + 360);
        reloadButton.setSize(200, 60);
        reloadButton.setFont(new Font("Rockwell", Font.BOLD, 20));
        add(reloadButton);
        reloadButton.addActionListener(e -> {
            chessboard.reload();
            chessboard.repaint();
            reloadProgressBar();
        });

    }

    private JButton loadGameButton;

    private void addLoadButton() {
        loadGameButton = new JButton("Load");
        loadGameButton.setLocation(HEIGHT, HEIGHT / 10 + 240);
        loadGameButton.setSize(100, 60);
        loadGameButton.setFont(new Font("Rockwell", Font.BOLD, 20));
        add(loadGameButton);

        jFileChooser1.addActionListener(this);
        jFileChooser1.setFileSelectionMode(JFileChooser.FILES_ONLY);
        this.add(jFileChooser1);

        jFileChooser1.setFileFilter(new FileFilter() {
            @Override
            public boolean accept(File f) {
                return f.getName().endsWith("txt");
            }

            @Override
            public String getDescription() {
                return ".txt";
            }
        });

        loadGameButton.addActionListener(e -> {
            jFileChooser1.setDialogTitle("Load from JFileChooser");
            int type1 = jFileChooser1.showOpenDialog(null);
            if (type1 == JFileChooser.APPROVE_OPTION) {
                File file = jFileChooser1.getSelectedFile();
                gameController.loadGameFromFile(file);
                if (undoButton != null) undoButton.setEnabled(chessboard.canUndo());
                if (redoButton != null) redoButton.setEnabled(chessboard.canRedo());
            }
        });
    }

    private JButton saveGameButton;

    private void addSaveButton() {
        saveGameButton = new JButton("Save");
        saveGameButton.setLocation(HEIGHT + 100, HEIGHT / 10 + 240);
        saveGameButton.setSize(100, 60);
        saveGameButton.setFont(new Font("Rockwell", Font.BOLD, 20));
        add(saveGameButton);

        jFileChooser2.addActionListener(this);
        jFileChooser2.setFileSelectionMode(JFileChooser.FILES_ONLY);
        this.add(jFileChooser2);

        saveGameButton.addActionListener(e -> {
            jFileChooser2.setDialogTitle("Save by JFileChooser");
            int type2 = jFileChooser2.showSaveDialog(null);
            if (type2 == JFileChooser.APPROVE_OPTION) {
                File file = jFileChooser2.getSelectedFile();
                FileOutputStream fos = null;
                try {
                    BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file));
                    chessboard.getInstances().forEach(instance -> {
                        System.out.println("Writing instance");
                        try {
                            bufferedWriter.write(instance.toString());
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                    });
                    bufferedWriter.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        });
    }

    @Override
    public void actionPerformed(ActionEvent e) {
    }

}
