package model;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 将所有方法都封装到这里，这样最后暴露给Chessboard的时候，就算你需要修改Instance的存储方式，Chessboard中也不会有问题。
 */
public final class GameInstance {
    private static final HashMap<Character, Class<? extends ChessComponent>> classMap = new HashMap<>() {
        {
            put('k', KingChessComponent.class);
            put('b', BishopChessComponent.class);
            put('n', KnightChessComponent.class);
            put('r', RookChessComponent.class);
            put('p', PawnChessComponent.class);
            put('q', QueenChessComponent.class);
            put('_', EmptySlotComponent.class);
        }
    };

    public List<String> chessboard = new ArrayList<>();

    public GameInstance() {

    }

    public ChessColor getColor(int row, int col) {
        char c = chessboard.get(row).charAt(col);
        if (c == '_') {
            return ChessColor.NONE;
        } else {
            return Character.isUpperCase(c) ? ChessColor.BLACK : ChessColor.WHITE;
        }
    }

    public Class<? extends ChessComponent> getChess(int row, int col) {
        char c = Character.toLowerCase(chessboard.get(row).charAt(col));
        return classMap.get(c);
    }

    public ChessColor getCurrentColor() {
        int finalIndex = chessboard.size() - 1;
        if (Objects.equals(chessboard.get(finalIndex), "w")) {
            return ChessColor.WHITE;
        }
        if (Objects.equals(chessboard.get(finalIndex), "b")) {
            return ChessColor.BLACK;
        }
        return null;
    }

    public GameInstance(List<String> chessboard) {
        this.chessboard = new ArrayList<>(chessboard);
    }

    public List<String> getChessboard() {
        return chessboard;
    }

    public void setChessboard(List<String> chessboard) {
        this.chessboard = chessboard;
    }

    /**
     * For writing the game into a valid String form
     */
    @Override
    public String toString() {
        return String.join("\n", chessboard) + '\n'; //Check the reference of the method.
    }


    public static GameInstance readInstance(List<String> instance) {
        return new GameInstance(instance);
    }

    //    @SuppressWarnings("all")
    public static List<GameInstance> parseInstancesFromList(List<String> instances) throws SaveProcessingException {
        List<GameInstance> gameInstances = new ArrayList<>();

        List<String> tmpChessboard = new ArrayList<>();

        int rowCount = 0;
        for (String s : instances) {

            if (rowCount == 0 && s.length() == 0) {
                continue;
            }

            ++rowCount;

            if (rowCount % 9 != 0) {
                if (s.length() != 8) {
                    JOptionPane.showMessageDialog(null, "101", "报错", JOptionPane.WARNING_MESSAGE);
                    throw new SaveProcessingException("101 Error occurred when reading save file: column count different from 8.");
                }

                OptionalInt result = s.chars().filter(e -> !classMap.containsKey(Character.toLowerCase((char) e))).findAny();

                if (result.isPresent()) {
                    JOptionPane.showMessageDialog(null, "102", "报错", JOptionPane.WARNING_MESSAGE);

                    throw new SaveProcessingException("102 Error occurred when reading save file: invalid char: " + (char) result.getAsInt());
                }
            }

            tmpChessboard.add(s);

            if (rowCount == 9) {
                if (s.length() != 1) {
                    throw new SaveProcessingException("101 Error occurred when reading save file: column count different from 8.");
                }
                if (!(s.equals("w") || s.equals("b"))) {
                    JOptionPane.showMessageDialog(null, "103", "报错", JOptionPane.WARNING_MESSAGE);
                    throw new SaveProcessingException("103 Error occurred when reading save file: invalid current color.");
                }
                gameInstances.add(new GameInstance(tmpChessboard));
                tmpChessboard = new ArrayList<>(); // Don't use clear()
                rowCount = 0;
            }
        }

        if (rowCount != 9) {
            JOptionPane.showMessageDialog(null, "103", "报错", JOptionPane.WARNING_MESSAGE);
            throw new SaveProcessingException("103 Error occurred when reading save file: invalid current color.");
        }

        return gameInstances;
    }

    public static List<GameInstance> parseInstancesFromFile(File file) throws SaveProcessingException {
        try {
            List<String> savedGame = Files.readAllLines(file.toPath());
            return parseInstancesFromList(savedGame);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new ArrayList<>();
    }

    /**
     * 存储棋盘
     */
    public static GameInstance getChessboardGraph(ChessComponent[][] chessComponents, ChessColor currentColor) {
        List<String> chessboard = new ArrayList<>();

        int rowLength = chessComponents.length;
        int colLength = chessComponents[0].length;

        for (int i = 0; i < rowLength; i++) {
            StringBuilder s = new StringBuilder();
            for (int j = 0; j < colLength; j++) {
                s.append(chessComponents[i][j].name);
            }
            chessboard.add(s.toString());
        }
        chessboard.add(currentColor == ChessColor.BLACK ? "b" : "w");
        return new GameInstance(chessboard);
    }

}