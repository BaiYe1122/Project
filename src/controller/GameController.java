package controller;

import model.GameInstance;
import model.SaveProcessingException;
import view.Chessboard;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

public class GameController {
    private Chessboard chessboard;

    public GameController(Chessboard chessboard) {
        this.chessboard = chessboard;
    }

    public List<String> loadGameFromFile(File file) {

        List<GameInstance> instances = null;
        try {
            instances = GameInstance.parseInstancesFromFile(file);
            if (file.getName().endsWith("json")){
                JOptionPane.showMessageDialog(null, "104", "报错", JOptionPane.WARNING_MESSAGE);

            }
            chessboard.loadGameInstances(instances);
        } catch (SaveProcessingException e) {
            e.printStackTrace();
        }

        return null;
    }

}
