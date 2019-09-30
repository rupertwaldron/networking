package com.ruppyrup.server;

import com.ruppyrup.model.Message;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;

import static com.ruppyrup.model.MessageType.MESSAGE;

public class DateServer {

    public static void main(String[] args) throws IOException {
        try (var listener = new ServerSocket(59090)) {
            System.out.println("The date server is running...");
            while (true) {
                try (var socket = listener.accept()) {
                    var out = new ObjectOutputStream(socket.getOutputStream());
                    out.writeObject(new Message(MESSAGE, "Hello World!"));;
                    //out.writeObject(new Date());
                   // var out = new PrintWriter(socket.getOutputStream(), true);
                    //out.println(new Date().toString());
                    System.out.println("Hit by Socket: " + socket);
                }
            }
        }
    }
}
