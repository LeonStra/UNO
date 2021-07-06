package server;

import Exceptions.TurnException;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.UnknownHostException;

public class ServerFrame extends JFrame {

    public ServerFrame(Server server){
        super("Server");

        //Fenster Einstellungen
        setSize(1200,800);
        setLocationRelativeTo(null);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });

        JPanel panel = new JPanel();

        JLabel ip = new JLabel("X");
        try {
            ip = new JLabel(Inet4Address.getLocalHost().getHostAddress().toString());
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        JButton b = new JButton("LOS!");
        b.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    dispose();
                    server.start();
                } catch (IOException | TurnException ioException) {
                    ioException.printStackTrace();
                }
            }
        });
        panel.add(ip);
        panel.add(b);
        add(panel);
        setVisible(true);
    }
}
