package com.ruppyrup;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.Executors;

public class MultiTaskServer {
    private static final Set<PrintWriter> writers = new HashSet<>();

    public static void main(String[] args) throws Exception {
        System.out.println("Multi-Task Server is running...");
        var pool = Executors.newFixedThreadPool(500);
        try (var listener = new ServerSocket(59013)) {
            while (true) {
                pool.execute(new Handler(listener.accept()));
            }
        } catch (NoSuchElementException e) {
            System.out.println("Socket lost: " + e);
        }
    }

    private static class Handler implements Runnable {
        private Socket socket;
        private Scanner in;
        private PrintWriter out;

        public Handler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            System.out.println("Connected: " + socket);
            try {
                in = new Scanner(socket.getInputStream());
                out = new PrintWriter(socket.getOutputStream(), true);
                writers.add(out);
                while (true) {
                    String input = in.nextLine();
                    //System.out.println(input + " received from client: " + socket.getInetAddress() + " : " + socket.getPort());
                    for (PrintWriter writer : writers) {
                        writer.println(input);
                    }
                }
            } catch (Exception e) {
                System.out.println("Error:" + socket);
            } finally {
                try { socket.close();
                } catch (IOException e) {
                    System.out.println("Couldn't close socket");
                }
                System.out.println("Closed: " + socket);
            }
        }
    }
}
