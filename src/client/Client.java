package client;

import bothSides.Player;
import server.PlayerImpl;
import server.Server;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.Remote;

public class Client {
    private final Dimension frameDimension = new Dimension(1200,800);

    public Client(){
        createStartFrame();
    }

    public static void main(String[] args){
        Client client = new Client();
    }

    public void createStartFrame(){
        //Fenster einrichten
        JFrame startFrame = new JFrame();
        startFrame.setTitle("INFUNO");
        startFrame.setSize(frameDimension);
        startFrame.setLocationRelativeTo(null);
        startFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });

        //Überschrift
        JLabel title = new JLabel("INFUNO-Warteraum");
        title.setFont(new Font("Arial", Font.BOLD, 50));
        title.setHorizontalAlignment(SwingConstants.CENTER);
        startFrame.add(title,BorderLayout.NORTH);

        //Formular
        Font inputFont = new Font("Arial",Font.PLAIN,20);
        int width = 15;

        JPanel form = new JPanel();
        form.setBorder(new EmptyBorder(50,0,0,0));

        //Eingabe zur Verbindung
        PlaceholderInput ipInput = new PlaceholderInput("127.0.0.1",width,inputFont);
        JLabel colon = new JLabel(":");
        colon.setFont(inputFont);
        PlaceholderInput portInput = new PlaceholderInput("259",width,inputFont);

        //Name
        PlaceholderInput nameInput = new PlaceholderInput("Name",width,inputFont);

        //Bestätigungsbutton
        JButton submit = new JButton("Bestätigen");
        submit.setPreferredSize(new Dimension(100,30));
        submit.addActionListener(e -> {
            startFrame.dispose();
            setConn(ipInput.getText(),Integer.parseInt(portInput.getText()),nameInput.getText());
        });
        JButton server = new JButton("Host");
        server.setPreferredSize(new Dimension(100,30));
        server.addActionListener(e -> {

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
        try {
            Socket socket = new Socket(ip, port);
            BufferedReader fromServer = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
            String id = fromServer.readLine();
            socket.close();
            System.out.println("rmi://"+ip+"/player/"+id);
            Remote r = Naming.lookup("rmi://"+ip+"/player/"+id);
            Player p = (Player)r;
            System.out.println(p.getTop());
            //new Playground(p ,name);
        } catch (IOException | NotBoundException e) {
            e.printStackTrace();
        }
    }
}
