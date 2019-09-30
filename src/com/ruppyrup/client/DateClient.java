package com.ruppyrup.client;

import com.ruppyrup.model.Message;

import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.Date;

public class DateClient {
    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("Pass the server IP as the sole command line argument");
            return;
        }
        try (var socket = new Socket(args[0], 59090)) {
            var in = new ObjectInputStream(socket.getInputStream());
//            Date test = (Date) in.readObject();
            var message = (Message) in.readObject();
            System.out.println("Server response: \n" + message.toString());
        } catch (Exception e) {
            System.out.println("Error : " + e.getMessage());
        }
    }
}
