package controller;

import view.Chessboard;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

public class OnlineController {
    public Thread read,send;
    private ClientReadServer clientReadServer=null;
    private ClientSendServer clientSendServer = null;
    public Chessboard chessboard;
    public OnlineController(Chessboard chessboard, String IP) throws IOException {
        this.chessboard = chessboard;
        //1.客户端连接服务器端,返回套接字Socket对象
        InetAddress ip = InetAddress.getByName(IP);
        Socket socket = new Socket(ip, 6666);
        //2.创建读取服务器端信息的线程和发送服务器端信息的线程
        clientReadServer = new ClientReadServer(socket,chessboard);
        clientSendServer = new ClientSendServer(socket);
        read = new Thread(clientReadServer);
        send = new Thread(clientSendServer);
        //3.启动线程
        read.start();
        send.start();
    }
    public void SendMessage(String msg){
        clientSendServer.setMsg(msg);
    }
    public void Click(int[] location){
        SendMessage("P:"+ClientReadServer.name+"-click,"+ location[0]+","+location[1]);
    }

    public ClientReadServer getClientReadServer() {
        return clientReadServer;
    }

    public ClientSendServer getClientSendServer() {
        return clientSendServer;
    }
    public void setUserName(String userName){
        ClientSendServer.userName= userName;
    }
    public void setOpName(String Name){
        ClientReadServer.name= Name;
    }
}