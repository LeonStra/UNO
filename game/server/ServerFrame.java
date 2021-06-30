package server;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

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

        JButton b = new JButton("LOS!");
        b.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    dispose();
                    server.start();
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }
        });
        add(b);
        setVisible(true);
    }
}
