package controller;

import model.ChessComponent;
import view.Chessboard;

import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Arrays;
import java.util.Scanner;

//1.客户端读取服务器端信息的线程
class ClientReadServer implements Runnable {
    private Socket socket;
    public Chessboard chessboard;
    /**
     * 用来存对手的名字
     */
    public static String name = "maxing_long";

    public ClientReadServer(Socket socket, Chessboard chessboard) {
        this.chessboard = chessboard;
        this.socket = socket;
    }

    @Override
    public void run() {
        //1.获取服务器端输入流

        try {
            Scanner scanner = new Scanner(socket.getInputStream());
            while (scanner.hasNext()) {
                if (Thread.currentThread().isInterrupted()) { // 当前线程已被中断，退出循环
                    break;
                }

                String[] message = scanner.next().split(",");
                System.out.println(Arrays.toString(message));
                String command = name + ":click";
                if (command.equals(message[0])) {
                    ChessComponent chessComponent = chessboard.getChessComponents()[Integer.parseInt(message[1])][Integer.parseInt(message[2])];
                    chessComponent.clickController.onClick(chessComponent);
                } else if ("".equals(message[0])) {
                }

            }
            scanner.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

//2.客户端向服务器端发送信息的线程
class ClientSendServer implements Runnable {
    private Socket socket;
    private String msg = null;
    /**
     * 存自己的名字
     */
    public static String userName = "maxing";

    public ClientSendServer(Socket socket) {
        this.socket = socket;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    @Override
    public void run() {
        try {
            //1.获取服务器端的输出流
            PrintStream printStream = new PrintStream(socket.getOutputStream());
            printStream.println("userName:" + userName);
            while (true) {
                if (msg != null) {
                    if (msg.equals("exit")) {
                        printStream.println(msg);
                        printStream.close();
                        break;
                    }
                    if (!"".equals(msg)) {
                        printStream.println(msg);
                        msg = null;
                        continue;
                    }

                }
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

