package server;

import bothSides.Exceptions.TurnException;
import bothSides.JSwitchBox;

import javax.swing.*;
import java.awt.*;
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
        setLayout(new BorderLayout());
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });



        JPanel panel = new JPanel();
        JLabel label = new JLabel("Erweiterte Version?");
        label.setFont(new Font("Arial",Font.BOLD,20));
        panel.add(label);
        JSwitchBox jSwitch = new JSwitchBox(10,true);
        panel.add(jSwitch);

        JLabel ip = new JLabel("X");
        try {
            ip.setText(Inet4Address.getLocalHost().getHostAddress().toString());
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        JButton submit = new JButton("LOS!");
        submit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    dispose();
                    server.setExtended(jSwitch.isSelected());
                    server.start();
                } catch (IOException | TurnException ioException) {
                    ioException.printStackTrace();
                }
            }
        });
        add(ip,BorderLayout.NORTH);
        add(panel,BorderLayout.CENTER);
        add(submit,BorderLayout.SOUTH);
        setVisible(true);
    }
}
