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
    Server server;

    public ServerFrame(Server server){
        super("Server");
        this.server = server;

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

        JLabel ip = new JLabel("X",JLabel.CENTER);
        ip.setFont(new Font("Arial", Font.BOLD,30));
        try {
            ip.setText(Inet4Address.getLocalHost().getHostAddress().toString());
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        JPanel submitPanel = new JPanel();
        JButton submit = new JButton("LOS!");
        submit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    dispose();
                    server.startGame();
                } catch (IOException | TurnException ioException) {
                    ioException.printStackTrace();
                }
            }
        });
        submitPanel.add(submit);
        add(ip,BorderLayout.NORTH);
        add(submitPanel,BorderLayout.SOUTH);
        setVisible(true);
        selectDialog();
    }

    public void selectDialog(){
        JDialog dialog = new JDialog(this);
        dialog.setTitle("Einstellungen");
        dialog.setSize(600,400);
        dialog.setLocationRelativeTo(null);
        dialog.setLayout(new BorderLayout());
        dialog.setModal(true);
        setDefaultCloseOperation(0);

        JPanel panel = new JPanel();
        JLabel label = new JLabel("Erweiterte Version?");
        label.setFont(new Font("Arial",Font.BOLD,20));
        panel.add(label);
        JSwitchBox jSwitch = new JSwitchBox(10,server.getExtended());
        panel.add(jSwitch);

        JPanel submitPanel = new JPanel();
        JButton submit = new JButton("OK");
        submit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Thread thread = new Thread() {
                    public void run() {
                        try {
                            server.startSockets(jSwitch.isSelected());
                        } catch (IOException ioException) {
                            ioException.printStackTrace();
                        }
                    }
                };
                thread.start();
                dialog.dispose();
            }
        });
        submitPanel.add(submit);

        dialog.add(panel,BorderLayout.CENTER);
        dialog.add(submitPanel,BorderLayout.SOUTH);
        dialog.setVisible(true);
    }
}
