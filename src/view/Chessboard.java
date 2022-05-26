package view;


import model.*;
import controller.ClickController;

import javax.swing.*;
import java.awt.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.IntStream;

public class Chessboard extends JComponent {
    public final ChessGameFrame chessGameFrame;
    /**
     * CHESSBOARD_SIZE： 棋盘是8 * 8的
     * <br>
     * BACKGROUND_COLORS: 棋盘的两种背景颜色
     * <br>
     * chessListener：棋盘监听棋子的行动
     * <br>
     * chessboard: 表示8 * 8的棋盘
     * <br>
     * currentColor: 当前行棋方
     */
    private static final int CHESSBOARD_SIZE = 8;
    private final ChessComponent[][] chessComponents = new ChessComponent[CHESSBOARD_SIZE][CHESSBOARD_SIZE];
    private ChessColor currentColor = ChessColor.WHITE;
    //all chessComponents in this chessboard are shared only one model controller
    public final ClickController clickController = new ClickController(this);
    private final int CHESS_SIZE;

    private List<GameInstance> instances = new ArrayList<>();
    private int instanceCursor;

    public int getInstanceCursor() {
        return instanceCursor;
    }

    private void initInstancesListToDefault() {
        if (instances.size() != 0) instances.clear();
        instanceCursor = -1;
        saveCurrentInstance();
    }

    public List<ChessboardPoint> getPoints(ChessComponent chessComponent) {
        List<ChessboardPoint> points = new ArrayList<>();
        for (int i = 0; i <= 7; i++) {
            for (int j = 0; j <= 7; j++) {
                if (chessComponent.canMoveTo(chessComponents, new ChessboardPoint(i, j)) && chessComponents[i][j].getChessColor() != currentColor) {
                    points.add(new ChessboardPoint(i, j));
                }
            }
        }
        return points;
    }

    public Chessboard(int width, int height, ChessGameFrame chessGameFrame) {
        this.chessGameFrame = chessGameFrame;
        setLayout(null); // Use absolute layout.
        setSize(width, height);
        CHESS_SIZE = width / 8;

        // FIXME: Initialize chessboard for testing only.
        reload();

        initInstancesListToDefault();
    }

    public void reload() {
        initiateEmptyChessboard();
        initKingOnBoard(0, 4, ChessColor.BLACK);
        initKingOnBoard(7, 4, ChessColor.WHITE);

        initQueenOnBoard(0, 3, ChessColor.BLACK);
        initQueenOnBoard(7, 3, ChessColor.WHITE);

        initBishopOnBoard(0, 2, ChessColor.BLACK);
        initBishopOnBoard(0, 5, ChessColor.BLACK);
        initBishopOnBoard(7, 2, ChessColor.WHITE);
        initBishopOnBoard(7, 5, ChessColor.WHITE);

        initKnightOnBoard(0, 1, ChessColor.BLACK);
        initKnightOnBoard(0, 6, ChessColor.BLACK);
        initKnightOnBoard(7, 1, ChessColor.WHITE);
        initKnightOnBoard(7, 6, ChessColor.WHITE);

        initRookOnBoard(0, 0, ChessColor.BLACK);
        initRookOnBoard(0, 7, ChessColor.BLACK);
        initRookOnBoard(7, 0, ChessColor.WHITE);
        initRookOnBoard(7, 7, ChessColor.WHITE);


        IntStream.range(0, 8).forEach(e -> {
            initPawnOnBoard(6, e, ChessColor.WHITE);
            initPawnOnBoard(1, e, ChessColor.BLACK);
        });

        initInstancesListToDefault();
    }

    public ChessComponent[][] getChessComponents() {
        return chessComponents;
    }

    public ChessColor getCurrentColor() {
        return currentColor;
    }

    public void putChessOnBoard(ChessComponent chessComponent) {
        int row = chessComponent.getChessboardPoint().getX(), col = chessComponent.getChessboardPoint().getY();

        if (chessComponents[row][col] != null) {
            remove(chessComponents[row][col]);
        }
        add(chessComponents[row][col] = chessComponent);
    }

    public void swapChessComponents(ChessComponent chess1, ChessComponent chess2) {
        // Note that chess1 has higher priority, 'destroys' chess2 if exists.
        if (!(chess2 instanceof EmptySlotComponent)) {
            remove(chess2);
            add(chess2 = new EmptySlotComponent(chess2.getChessboardPoint(), chess2.getLocation(), clickController, CHESS_SIZE, this));
        }
        chess1.swapLocation(chess2);
        int row1 = chess1.getChessboardPoint().getX(), col1 = chess1.getChessboardPoint().getY();
        chessComponents[row1][col1] = chess1;
        int row2 = chess2.getChessboardPoint().getX(), col2 = chess2.getChessboardPoint().getY();
        chessComponents[row2][col2] = chess2;

        chess1.repaint();
        chess2.repaint();
    }

    public void swapColor() {
        currentColor = currentColor == ChessColor.BLACK ? ChessColor.WHITE : ChessColor.BLACK;
        chessGameFrame.setStatusLabel(currentColor + "'s turn");
        chessGameFrame.reloadProgressBar();
    }

    public void initiateEmptyChessboard() {
        for (int i = 0; i < chessComponents.length; i++) {
            for (int j = 0; j < chessComponents[i].length; j++) {
                putChessOnBoard(new EmptySlotComponent(new ChessboardPoint(i, j), calculatePoint(i, j), clickController, CHESS_SIZE, this));
            }
        }
    }

    private void initKingOnBoard(int row, int col, ChessColor color) {
        ChessComponent chessComponent = new KingChessComponent(new ChessboardPoint(row, col), calculatePoint(row, col), color, clickController, CHESS_SIZE, this);
        chessComponent.setVisible(true);
        putChessOnBoard(chessComponent);
    }//初始化king

    private void initQueenOnBoard(int row, int col, ChessColor color) {
        ChessComponent chessComponent = new QueenChessComponent(new ChessboardPoint(row, col), calculatePoint(row, col), color, clickController, CHESS_SIZE, this);
        chessComponent.setVisible(true);
        putChessOnBoard(chessComponent);
    }//初始化queen

    private void initBishopOnBoard(int row, int col, ChessColor color) {
        ChessComponent chessComponent = new BishopChessComponent(new ChessboardPoint(row, col), calculatePoint(row, col), color, clickController, CHESS_SIZE, this);
        chessComponent.setVisible(true);
        putChessOnBoard(chessComponent);
    }//初始化bishop

    private void initKnightOnBoard(int row, int col, ChessColor color) {
        ChessComponent chessComponent = new KnightChessComponent(new ChessboardPoint(row, col), calculatePoint(row, col), color, clickController, CHESS_SIZE, this);
        chessComponent.setVisible(true);
        putChessOnBoard(chessComponent);
    }//初始化knight

    private void initRookOnBoard(int row, int col, ChessColor color) {
        ChessComponent chessComponent = new RookChessComponent(new ChessboardPoint(row, col), calculatePoint(row, col), color, clickController, CHESS_SIZE, this);
        chessComponent.setVisible(true);
        putChessOnBoard(chessComponent);
    }//初始化rook

    private void initPawnOnBoard(int row, int col, ChessColor color) {
//        ChessComponent chessComponent = new PawnChessComponent(new ChessboardPoint(row, col), calculatePoint(row, col), color, clickController, CHESS_SIZE, this);
//        chessComponent.setVisible(true);
//        putChessOnBoard(chessComponent);
        initChessComponentOnBoard(row,col,color,PawnChessComponent.class);
    }//初始化pawn

    private <T extends ChessComponent> void initChessComponentOnBoard(int row, int col, ChessColor color, Class<T> chessClazz) {
        try {

            //Using reflection
            Constructor<T> constructor = chessClazz.getConstructor(ChessboardPoint.class,
                    Point.class,
                    ChessColor.class,
                    ClickController.class,
                    int.class,
                    Chessboard.class);

            //Using constructor to create new instance
            T chess = constructor.newInstance(new ChessboardPoint(row, col),
                    calculatePoint(row, col),
                    color,
                    clickController,
                    CHESS_SIZE,
                    this);

            chess.setVisible(true);
            putChessOnBoard(chess);

        } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    }

    private Point calculatePoint(int row, int col) {
        return new Point(col * CHESS_SIZE, row * CHESS_SIZE);
    }


    public List<GameInstance> getInstances() {
        return instances;
    }

    public void setInstances(List<GameInstance> instances) {
        this.instances = new ArrayList<>(instances);
    }

    public void loadGameInstances(List<GameInstance> targetInstances) {
        instances = new ArrayList<>(targetInstances);
        instanceCursor = instances.size() - 1;
        loadInstanceOnCursor();
    }

    public boolean canUndo() {
        return instanceCursor > 0;
    }

    public void undo() {
        if (instanceCursor >= 0) {
            System.out.println("Undo.");
            instanceCursor--;
            loadInstanceOnCursor();
        }
    }

    public boolean canRedo() {
        return instanceCursor < instances.size() - 1;
    }

    public void redo() {
        if (instanceCursor < instances.size() - 1) {
            System.out.println("Redo.");
            instanceCursor++;
            loadInstanceOnCursor();
        }
    }

    public void saveCurrentInstance() {
        instances.subList(instanceCursor + 1, instances.size()).clear();
        instances.add(GameInstance.getChessboardGraph(chessComponents, currentColor));
        instanceCursor++;
    }

    /**
     *
     */
    public void loadInstanceOnCursor() {
        loadInstance(instanceCursor);
    }

    /**
     * Load from the instances saved in this chessboard
     *
     * @param position target position
     */
    public void loadInstance(int position) {
        if (position < 0 || position >= instances.size())
            return;
        instanceCursor = position;
        loadInstance(instances.get(position));
    }

    /**
     * Load from any GameInstance
     *
     * @param instance target
     */
    public void loadInstance(GameInstance instance) {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                initChessComponentOnBoard(i, j, instance.getColor(i, j), instance.getChess(i, j));
            }
        }
        currentColor = instance.getCurrentColor();
        repaint();
    }//传入棋盘

}
