package controller;


import model.ChessComponent;
import view.Chessboard;
import view.ChessboardPoint;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ClickController {
    public Clip clip;

    private final Chessboard chessboard;
    private ChessComponent first;

    public boolean isClickEnabled() {
        return isClickEnabled;
    }

    public void setClickEnabled(boolean clickEnabled) {
        isClickEnabled = clickEnabled;
    }

    private boolean isClickEnabled = true;

    public List<String> Step = new ArrayList<>();


    public ClickController(Chessboard chessboard) {
        this.chessboard = chessboard;
    }

    public void onClick(ChessComponent chessComponent) {
        if (!isClickEnabled) {
            return;
        }
        if (first == null) {
            if (handleFirst(chessComponent)) {
                chessComponent.setSelected(true);
                first = chessComponent;
                first.repaint();
                List<ChessboardPoint> chessboardPoints = chessboard.getPoints(chessComponent);
                for (ChessboardPoint chess :
                        chessboardPoints) {
                    chessboard.getChessComponents()[chess.getX()][chess.getY()].CanMovePoints = true;
                    chessboard.getChessComponents()[chess.getX()][chess.getY()].repaint();
                }
            }
        } else {
            if (first == chessComponent) { // 再次点击取消选取
                chessComponent.setSelected(false);
                ChessComponent recordFirst = first;
                first = null;
                List<ChessboardPoint> chessboardPoints = chessboard.getPoints(chessComponent);
                for (ChessboardPoint chess :
                        chessboardPoints) {
                    chessboard.getChessComponents()[chess.getX()][chess.getY()].CanMovePoints = false;
                    chessboard.getChessComponents()[chess.getX()][chess.getY()].repaint();
                }
                recordFirst.repaint();
            } else if (handleSecond(chessComponent)) {
                playBGM2();
                chessboard.swapChessComponents(first, chessComponent);
                chessboard.swapColor();

                chessboard.saveCurrentInstance();

                first.setSelected(false);
                for (int i = 0; i < 8; i++) {
                    for (int j = 0; j < 8; j++) {
                        chessboard.getChessComponents()[i][j].CanMovePoints = false;
                        chessboard.getChessComponents()[i][j].repaint();
                    }
                }
                first = null;
            }
        }

    }

    public void playBGM2() {
        try {
            File musicPath;
            musicPath = new File("Music/棋子.wav");
            if (musicPath.exists()) {
                AudioInputStream audioInput = AudioSystem.getAudioInputStream(musicPath);
                clip = AudioSystem.getClip();
                clip.open(audioInput);
                FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
                gainControl.setValue(-20.0f);//设置音量，范围为 -60.0f 到 6.0f
                clip.start();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * @param chessComponent 目标选取的棋子
     * @return 目标选取的棋子是否与棋盘记录的当前行棋方颜色相同
     */

    private boolean handleFirst(ChessComponent chessComponent) {
        return chessComponent.getChessColor() == chessboard.getCurrentColor();
    }

    /**
     * @param chessComponent first棋子目标移动到的棋子second
     * @return first棋子是否能够移动到second棋子位置
     */

    private boolean handleSecond(ChessComponent chessComponent) {
        return chessComponent.getChessColor() != chessboard.getCurrentColor() && first.canMoveTo(chessboard.getChessComponents(), chessComponent.getChessboardPoint());
    }

}
