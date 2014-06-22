package org.gbc.kinect;

import java.net.Socket;
import java.io.PrintWriter;
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class TcpSender {
  public static void main(String[] args) throws Exception {
    Socket socket = new Socket(args[0], Integer.parseInt(args[1]));
    PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
    BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    
    out.println(args[2]);
    out.flush();
    System.out.println(in.readLine());
  }
}