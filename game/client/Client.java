package client;

import bothSides.Player;
import server.PlacholderInput;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
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
        int width = 15;

        JPanel form = new JPanel();
        form.setBorder(new EmptyBorder(50,0,0,0));

        //Eingabe zur Verbindung
        PlacholderInput ipInput = new PlacholderInput("127.0.0.1",width,inputFont);
        JLabel colon = new JLabel(":");
        colon.setFont(inputFont);
        PlacholderInput portInput = new PlacholderInput("6780",width,inputFont);

        //Name
        PlacholderInput nameInput = new PlacholderInput("Name",width,inputFont);

        //BestätigungsButton
        JButton submit = new JButton("Bestätigen");
        submit.setPreferredSize(new Dimension(100,30));
        submit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                startFrame.dispose();
                setConn(ipInput.getText(),Integer.parseInt(portInput.getText()),nameInput.getText());
            }
        });

        JLabel ipLabel = new JLabel("Verbindung:",JLabel.CENTER);
        ipLabel.setBorder(new EmptyBorder(25,0,10,0));
        ipLabel.setPreferredSize(new Dimension(startFrame.getWidth(),ipLabel.getPreferredSize().height));
        ipLabel.setFont(inputFont);

        JLabel placeholder = new JLabel("");
        placeholder.setPreferredSize(new Dimension(startFrame.getWidth(),0));

        form.add(nameInput);
        form.add(ipLabel);
        form.add(ipInput);
        form.add(colon);
        form.add(portInput);
        form.add(placeholder);
        form.add(submit);

        startFrame.add(form,BorderLayout.CENTER);
        startFrame.setVisible(true);
        SwingUtilities.invokeLater(() -> startFrame.requestFocus());
    }

    public void setConn(String ip, int port, String name){
        Socket socket = null;
        try {
            socket = new Socket(ip, port);
            BufferedReader fromServer = new BufferedReader(new InputStreamReader(socket.getInputStream(), "utf-8"));
            String id = fromServer.readLine();
            socket.close();
            System.out.println("rmi://"+ip+"/player/"+id);
            Playground playground = new Playground((Player) Naming.lookup("rmi://"+ip+"/player/"+id),name);
        } catch (IOException | NotBoundException e) {
            e.printStackTrace();
        }
    }
}
