package model;

import view.Chessboard;
import view.ChessboardPoint;
import controller.ClickController;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;
import java.io.IOException;

/**
 * 这个类表示国际象棋里面的车
 */
public class PawnChessComponent extends ChessComponent {
    /**
     * 黑车和白车的图片，static使得其可以被所有车对象共享
     * <br>
     * FIXME: 需要特别注意此处加载的图片是没有背景底色的！！！
     */
    private static Image PAWN_WHITE;
    private static Image PAWN_BLACK;

    /**
     * 车棋子对象自身的图片，是上面两种中的一种
     */
    private Image pawnImage;

    /**
     * 读取加载车棋子的图片
     *
     * @throws IOException
     */
    public void loadResource() throws IOException {
        if (PAWN_WHITE == null) {
            PAWN_WHITE = ImageIO.read(new File("./images/pawn-white.png"));
        }

        if (PAWN_BLACK == null) {
            PAWN_BLACK = ImageIO.read(new File("./images/pawn-black.png"));
        }
    }


    /**
     * 在构造棋子对象的时候，调用此方法以根据颜色确定rookImage的图片是哪一种
     *
     * @param color 棋子颜色
     */

    private void initiateRookImage(ChessColor color) {
        try {
            loadResource();
            if (color == ChessColor.WHITE) {
                pawnImage = PAWN_WHITE;
            } else if (color == ChessColor.BLACK) {
                pawnImage = PAWN_BLACK;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public PawnChessComponent(ChessboardPoint chessboardPoint, Point location, ChessColor color, ClickController listener, int size, Chessboard chessboard) {
        super(chessboardPoint, location, color, listener, size, chessboard);
        initiateRookImage(color);
        if (color == ChessColor.BLACK) {
            name = 'P';
        } else {
            name = 'p';
        }
    }

    /**
     * 车棋子的移动规则
     *
     * @param chessComponents 棋盘
     * @param destination     目标位置，如(0, 0), (0, 7)等等
     * @return 车棋子移动的合法性
     */

    @Override
    public boolean canMoveTo(ChessComponent[][] chessComponents, ChessboardPoint destination) {
        ChessboardPoint source = getChessboardPoint();
        int x = source.getX();
        int y = source.getY();
        int a = destination.getX();
        int b = destination.getY();

        if (chessComponents[x][y].getChessColor() == ChessColor.WHITE) {
            // 直线走棋
            if (b == y) {
                //起始可走两步
                if (x == 6) {
                    if (x - a == 2 || x - a == 1) {
                        if (x - a == 2 && chessComponents[a][b] instanceof EmptySlotComponent && chessComponents[a + 1][b] instanceof EmptySlotComponent)
                            return true;
                        if (x - a == 1 && chessComponents[a][b] instanceof EmptySlotComponent) {
                            return true;
                        }
                    }
                } else {
                    if (x - a == 1) {
                        if (chessComponents[a][b] instanceof EmptySlotComponent)
                            return true;
                    }
                }
            }
            // 斜线吃棋
            // 右上有黑子
            if (a == x - 1 && b == y + 1) {
                if (chessComponents[x - 1][y + 1].getChessColor() == ChessColor.BLACK) {
                    return true;
                }
            }
            //左上有黑子
            if (a == x - 1 && b == y - 1) {
                if (chessComponents[x - 1][y - 1].getChessColor() == ChessColor.BLACK) {
                    return true;
                }
            }
        } else if (chessComponents[x][y].getChessColor() == ChessColor.BLACK) {
            // 直线走棋
            if (b == y) {
                //起始可走两步
                if (x == 1) {
                    if (a - x == 2 || a - x == 1) {
                        if (a - x == 2 && chessComponents[a][b] instanceof EmptySlotComponent && chessComponents[a - 1][b] instanceof EmptySlotComponent) {
                            return true;
                        }
                        if (a - x == 1 && chessComponents[a][b] instanceof EmptySlotComponent) {
                            return true;
                        }
                    }
                } else {
                    if (a - x == 1) {
                        if (chessComponents[a][b] instanceof EmptySlotComponent)
                            return true;
                    }
                }
            }
            // 斜线吃棋
            // 右上有白字
            if (a == x + 1 && b == y + 1) {
                if (chessComponents[x + 1][y + 1].getChessColor() == ChessColor.WHITE) {
                    return true;
                }
            }
            //左上有白子
            if (a == x + 1 && b == y - 1) {
                if (chessComponents[x + 1][y - 1].getChessColor() == ChessColor.WHITE) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 注意这个方法，每当窗体受到了形状的变化，或者是通知要进行绘图的时候，就会调用这个方法进行画图。
     *
     * @param g 可以类比于画笔
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
//        g.drawImage(rookImage, 0, 0, getWidth() - 13, getHeight() - 20, this);
        g.drawImage(pawnImage, 0, 0, getWidth(), getHeight(), this);
        g.setColor(Color.BLACK);
        if (isSelected()) { // Highlights the model if selected.
            g.setColor(Color.CYAN);
            g.drawRoundRect(10, 10, getWidth() - 20, getHeight() - 20, 10, 10);
        }
    }
}
