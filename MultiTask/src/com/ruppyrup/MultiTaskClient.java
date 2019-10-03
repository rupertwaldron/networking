package com.ruppyrup;

import java.io.*;
import java.net.ConnectException;
import java.net.Socket;
import java.util.Scanner;

public class MultiTaskClient {
    private String serverAddress;
    private Scanner in;
    private PrintWriter out;

    public MultiTaskClient(String serverAddress) {
        this.serverAddress = serverAddress;
    }

    private void run() throws IOException {
        try(var socket = new Socket(serverAddress, 59013)) {
            System.out.println("Enter lines of text then Ctrl+D or Ctrl+C to quit");
            var scanner = new Scanner(System.in);
            in = new Scanner(socket.getInputStream());
            out = new PrintWriter(socket.getOutputStream(), true);

            while (true) {
                out.println(scanner.nextLine());
                System.out.println(in.nextLine());
            }

        } catch (ConnectException exception) {
            System.out.println("Connection refused!");
        }
    }

    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            System.err.println("Pass the server IP as the sole command line argument");
            return;
        }
        var client = new MultiTaskClient(args[0]);
        client.run();
    }
}
