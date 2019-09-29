package com.ruppyrup;

//Here’s an old client cobbled together in 2002, using Swing.

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Map;
import java.util.Scanner;
import java.util.function.Consumer;

/**
 * A simple Swing-based client for the chat server. Graphically it is a frame with a text
 * field for entering messages and a textarea to see the whole dialog.
 *
 * The client follows the following Chat Protocol. When the server sends "SUBMITNAME" the
 * client replies with the desired screen name. The server will keep sending "SUBMITNAME"
 * requests as long as the client submits screen names that are already in use. When the
 * server sends a line beginning with "NAMEACCEPTED" the client is now allowed to start
 * sending the server arbitrary strings to be broadcast to all chatters connected to the
 * server. When the server sends a line beginning with "MESSAGE" then all characters
 * following this string should be displayed in its message area.
 */
public class ChatClient {

    private String serverAddress;
    private Scanner in;
    private PrintWriter out;
    private JFrame frame;
    private JTextField textField;
    private JTextPane textPane;
    private StyledDocument document;
    private Style style;
    private Map<Character, Color> colorMap = Map.of(
        'b', Color.BLUE,
        'r', Color.RED,
        'o', Color.ORANGE,
        'g', Color.GREEN,
        'y', Color.YELLOW,
        'p', Color.PINK
    );
    private Consumer<String> processColorStrings;

    /**
     * Constructs the client by laying out the GUI and registering a listener with the
     * textfield so that pressing Return in the listener sends the textfield contents
     * to the server. Note however that the textfield is initially NOT editable, and
     * only becomes editable AFTER the client receives the NAMEACCEPTED message from
     * the server.
     */
    public ChatClient(String serverAddress) {
        this.serverAddress = serverAddress;
        setupFrame();
    }

    private String getName() {
        return JOptionPane.showInputDialog(
            frame,
            "Choose a screen name:",
            "Screen name selection",
            JOptionPane.PLAIN_MESSAGE
        );
    }

    private void run() throws IOException, BadLocationException {
        try(var socket = new Socket(serverAddress, 59001)) {
            in = new Scanner(socket.getInputStream());
            out = new PrintWriter(socket.getOutputStream(), true);

            while (in.hasNextLine()) {
                var line = in.nextLine();

                var command = line.substring(0, 10);
                System.out.println(command);
                switch (command) {
                    case "SUBMITNAME": {
                        out.println(getName());
                        break;
                    }
                    case "NAMEACCEPT": {
                        this.frame.setTitle("Chatter - " + line.substring(11));
                        textField.setEditable(true);
                        break;
                    }
                    case "MESSAGEGOO" : {
                        int startOfSubString = 11;
                        String stringToAdd = line.substring(startOfSubString);

                        if (!stringToAdd .contains("^")) {
                            sendChatter(Color.BLUE, stringToAdd + "\n");
                            break;
                        }

                        processColor(stringToAdd);
                        break;
                    }
                    case "INFOMATION" : {
                        sendChatter(Color.RED, line.substring(11) + "\n");
                        break;
                    }
                    default: {
                        sendChatter(Color.magenta, "Something has gone wrong\n");
                    }
                }
            }
        } finally {
            frame.setVisible(false);
            frame.dispose();
        }
    }

    private void processColor(String stringToAdd) throws BadLocationException {
        String[] splitString = stringToAdd.split("\\^");

        boolean isId = true;
        for (String subString : splitString) {
            Color textColor = Color.BLUE;
            if (isId) {
                sendChatter(textColor, subString);
                isId = false;
                continue;
            }
            char color = subString.charAt(0);
            if (colorMap.containsKey(color)) {
                textColor = colorMap.get(color);
            }
            sendChatter(textColor, subString.substring(1));
        }
        document.insertString(document.getLength(), "\n", style);
    }

    private void sendChatter(Color blue, String s) throws BadLocationException {
        StyleConstants.setForeground(style, blue);
        document.insertString(document.getLength(), s, style);
    }

    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            System.err.println("Pass the server IP as the sole command line argument");
            return;
        }
        var client = new ChatClient(args[0]);
        client.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        client.frame.setVisible(true);
        client.run();
    }

    private void setupFrame() {
        frame = new JFrame("Chatter");
        textField = new JTextField(50);
        textPane = new JTextPane();
        document = textPane.getStyledDocument();
        style = textPane.addStyle("style1", null);
        textField.setEditable(false);
        textPane.setEditable(false);
        textPane.setText("You are now in the chat room... \n");
        textPane.setPreferredSize(new Dimension(300, 300));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(textPane);
        frame.getContentPane().add(textField, BorderLayout.SOUTH);
        frame.getContentPane().add(new JScrollPane(textPane), BorderLayout.CENTER);
        frame.pack();
        frame.setVisible(true);

        // Send on enter then clear to prepare for next message
        textField.addActionListener(e -> {
            out.println(textField.getText());
            textField.setText("");
        });
    }
}
