package client;

import bothSides.Player;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.rmi.Naming;
import java.rmi.NotBoundException;

public class Client {
    public static Dimension frameDimension = new Dimension(1200,800);

    public static void main(String[] args){
        Client client = new Client();
        client.createStartFrame();
    }

    public void createStartFrame(){
        //Fenster einrichten
        JFrame startFrame = new JFrame();
        startFrame.setTitle("INFUNO");
        startFrame.setSize(Client.frameDimension);
        startFrame.setLocationRelativeTo(null);
        startFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });

        //Überschrift
        JLabel title = new JLabel("INFUNO-Warteraum");
        title.setFont(new Font("Tahoma", 0, 50));
        title.setHorizontalAlignment(SwingConstants.CENTER);
        startFrame.add(title,BorderLayout.NORTH);

        //Formular
        Font inputFont = new Font("Arial",0,20);
        int inputW = 15;

        JPanel form = new JPanel();
        form.setBorder(new EmptyBorder(50,0,0,0));

        JTextField ipInput = new JTextField("127.0.0.1",inputW);
        JTextField portInput = new JTextField("6780",inputW);
        JLabel colon = new JLabel(":");

        ipInput.setFont(inputFont);
        colon.setFont(inputFont);
        portInput.setFont(inputFont);

        JButton submit = new JButton("Bestätigen");
        submit.setPreferredSize(new Dimension(100,30));
        submit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                startFrame.dispose();
                setConn(ipInput.getText(),Integer.parseInt(portInput.getText()));
            }
        });

        form.add(ipInput);
        form.add(colon);
        form.add(portInput);
        form.add(submit);

        startFrame.add(form,BorderLayout.CENTER);
        startFrame.setVisible(true);
    }

    public void setConn(String ip, int port){
        Socket socket = null;
        try {
            socket = new Socket(ip, port);
            BufferedReader fromServer = new BufferedReader(new InputStreamReader(socket.getInputStream(), "utf-8"));
            String id = fromServer.readLine();
            socket.close();
            System.out.println("rmi://"+ip+"/player/"+id);
            Playground playground = new Playground((Player) Naming.lookup("rmi://"+ip+"/player/"+id));
        } catch (IOException | NotBoundException e) {
            e.printStackTrace();
        }
    }
}
