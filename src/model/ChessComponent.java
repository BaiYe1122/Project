package model;


import view.Chessboard;
import view.ChessboardPoint;
import controller.ClickController;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;
import java.io.IOException;

import javax.swing.*;
import java.awt.event.MouseEvent;


/**
 * 这个类是一个抽象类，主要表示8*8棋盘上每个格子的棋子情况，当前有两个子类继承它，分别是EmptySlotComponent(空棋子)和RookChessComponent(车)。
 */
public abstract class ChessComponent extends JComponent {
    public char name;
    private static Image BlackGrid;
    private static Image WhiteGrid;
    private boolean ShowMouse = false;
    private boolean theme;
    public boolean CanMovePoints = false;
    private Chessboard chessboard;
    private ClickController clickController;
    private ChessboardPoint chessboardPoint;
    protected final ChessColor chessColor;
    private boolean selected;

    protected ChessComponent(ChessboardPoint chessboardPoint, Point location, ChessColor chessColor, ClickController clickController, int size, Chessboard chessboard) {
        enableEvents(AWTEvent.MOUSE_EVENT_MASK);
        setLocation(location);
        setSize(size, size);
        this.chessboardPoint = chessboardPoint;
        this.chessColor = chessColor;
        this.selected = false;
        this.clickController = clickController;
        this.chessboard = chessboard;
        theme = chessboard.chessGameFrame.Style;
    }

    public ChessboardPoint getChessboardPoint() {
        return chessboardPoint;
    }

    public void setChessboardPoint(ChessboardPoint chessboardPoint) {
        this.chessboardPoint = chessboardPoint;
    }

    public ChessColor getChessColor() {
        return chessColor;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public void swapLocation(ChessComponent another) {
        ChessboardPoint chessboardPoint1 = getChessboardPoint(), chessboardPoint2 = another.getChessboardPoint();
        Point point1 = getLocation(), point2 = another.getLocation();
        setChessboardPoint(chessboardPoint2);
        setLocation(point2);
        another.setChessboardPoint(chessboardPoint1);
        another.setLocation(point1);
    }

    @Override
    protected void processMouseEvent(MouseEvent e) {
        super.processMouseEvent(e);

        if (e.getID() == MouseEvent.MOUSE_PRESSED) {
            System.out.printf("Click [%d,%d]\n", chessboardPoint.getX(), chessboardPoint.getY());
            clickController.onClick(this);
            repaint();
        }

        if (e.getID() == MouseEvent.MOUSE_ENTERED) {
            ShowMouse = true;
            repaint();
        }
        if (e.getID() == MouseEvent.MOUSE_EXITED) {
            ShowMouse = false;
            repaint();
        }
    }

    public abstract boolean canMoveTo(ChessComponent[][] chessboard, ChessboardPoint destination);


    public abstract void loadResource() throws IOException;

    @Override
    protected void paintComponent(Graphics g) {

        try {
            BlackGrid = ImageIO.read(new File("images/Blackgrid.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            WhiteGrid = ImageIO.read(new File("images/Whitegrid.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }


        super.paintComponents(g);
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, this.getWidth(), this.getHeight());



        if (theme) {
            g.setColor(Color.GRAY);
            g.fillRoundRect(10, 10, this.getWidth() - 5, this.getHeight() - 5, 10, 10);
            if ((chessboardPoint.getX() + chessboardPoint.getY()) % 2 == 0) {
                g.setColor(Color.WHITE);
                g.fillRoundRect(3, 3, getWidth() - 6, getHeight() - 6, 10, 10);
            } else {
                g.setColor(Color.BLACK);
                g.fillRoundRect(3, 3, getWidth() - 6, getHeight() - 6, 10, 10);
            }
        } else {
            g.setColor(Color.BLACK);
            g.fillRoundRect(15, 15, this.getWidth() - 6, this.getHeight() - 6, 10, 10);
            if ((chessboardPoint.getX() + chessboardPoint.getY()) % 2 == 0) {
                g.drawImage(BlackGrid, 3, 3, getWidth() - 6, getHeight() - 6, this);
            } else {
                g.drawImage(WhiteGrid, 3, 3, getWidth() - 6, getHeight() - 6, this);
            }
        }
        if (CanMovePoints){
            repaint();
            g.setColor(new Color(29, 188, 211, 103));
            g.fillOval(3,3,getWidth()-6,getHeight()-6);
        }
        if (ShowMouse) {
            g.setColor(new Color(42, 169, 206, 106));
            g.fillRect(0, 0, this.getWidth(), this.getHeight());
        }


    }
}
