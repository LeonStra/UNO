package client;

import Exceptions.NotSuitableException;
import bothSides.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.ListUI;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

public class Playground extends UnicastRemoteObject implements View{
    private final Dimension cardDimension = new Dimension(101,151);
    private Player player;

    private JFrame frame;
    private JPanel northPanel;
    private JPanel southPanel;
    private JPanel handPanel;
    private JPanel drawPanel;
    private JPanel playPanel;
    private JPanel westPanel;
    private JLabel news;

    public Playground(Player player) throws IOException{
        this.player = player;
        this.frame = new JFrame("INFUNO");
        this.southPanel = new JPanel();
        this.handPanel = new JPanel();
        this.drawPanel= new JPanel();
        this.playPanel = new JPanel();
        this.northPanel = new JPanel();
        this.westPanel = new JPanel();

        this.player.setView(this);

        //Fenster Einstellungen
        frame.setSize(1200,800);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setLocationRelativeTo(null);
        frame.setLayout(new BorderLayout());
        //frame.setUndecorated(true); //Ohne SchließButton etc.
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                try {
                    player.leaveGame();
                    System.exit(0);
                } catch (RemoteException remoteException) {
                    remoteException.printStackTrace();
                }
                System.exit(0);
            }
        });

        //Panel Einstellungen
        EmptyBorder noBorder = new EmptyBorder(0,0,0,0);
        handPanel.setBorder(noBorder);
        drawPanel.setBorder(noBorder);
        playPanel.setLayout(new GridLayout(1,2));

        //Nachziehstapel
        JButton drawButton = newCardButton(new Card(TYPE.UNKNOWN, COLOR.UNKNOWN),false);
        drawButton.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    player.drawCard();
                } catch (RemoteException remoteException) {
                    remoteException.printStackTrace();
                }
            }
        });
        drawPanel.add(drawButton);

        //Zusammensetzen
        southPanel.add(handPanel);
        southPanel.add(drawPanel);
        frame.add(northPanel, BorderLayout.NORTH);
        frame.add(westPanel, BorderLayout.WEST);
        frame.add(southPanel, BorderLayout.SOUTH);
        frame.add(playPanel, BorderLayout.CENTER);
        frame.setVisible(true);
        refresh();
        init();
    }

    private void init() throws RemoteException {
        //West Panel
        westPanel.setPreferredSize(new Dimension(200,frame.getHeight()));
        westPanel.setBackground(Color.YELLOW);
        System.out.println(player.getNameList().toArray());
        JList list = new JList(player.getNameList().toArray());
        list.setFont(new Font("Arial",0,25));
        list.setPreferredSize(new Dimension(200,list.getPreferredSize().height));
        list.setSelectionBackground(null);
        list.setFocusable(false);
        westPanel.add(list);


        //North Panel
        news = new JLabel("Start",SwingConstants.CENTER);
        news.setFont(new Font("Arial",0,50));
        news.setPreferredSize(new Dimension(frame.getWidth()-150,news.getPreferredSize().height));
        northPanel.add(news);

        JButton chat = new JButton();
        chat.setBorderPainted(false);
        chat.setBorder(new EmptyBorder(0,0,0,0));
        chat.setFocusPainted(false);
        chat.setContentAreaFilled(false);
        chat.setIcon(new ImageIcon("media/symbols/chat.jpg"));
        northPanel.add(chat);

        JButton settings = new JButton();
        settings.setBorderPainted(false);
        settings.setBorder(new EmptyBorder(0,0,0,0));
        settings.setFocusPainted(false);
        settings.setContentAreaFilled(false);
        settings.setIcon(new ImageIcon("media/symbols/settings.png"));
        northPanel.add(settings);

        northPanel.setPreferredSize(new Dimension(frame.getWidth(),northPanel.getPreferredSize().height));
    }
    //Einrichten einer Handkarte
    private JButton newCardButton(Card card,boolean play){
        JButton b = new JButton();
        b.setPreferredSize(cardDimension);
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        b.setContentAreaFilled(false);
        b.setIcon(new ImageIcon(card.getPath()));
        if (play) {
            b.addActionListener(new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    try {
                        player.play(card);
                    } catch (RemoteException remoteException) {
                        remoteException.printStackTrace();
                    } catch (NotSuitableException nse) {
                        nse.printStackTrace();
                        //Fehler anzeigen
                    }
                }
            });
        }
        return b;
    }

    //Aktualisieren/Einrichten der Ansicht
    public void refresh() throws RemoteException{
        handPanel.removeAll();
        playPanel.removeAll();

        //Handkarten
        //handPanel.removeAll();
        for (Card card : player.getHand()){
            handPanel.add(newCardButton(card,true));
        }

        //Ablagestapel
        JButton playPile = newCardButton(player.getTop(),false);
        playPanel.add(playPile);

        handPanel.setPreferredSize(new Dimension(frame.getWidth()-cardDimension.width-35,handPanel.getPreferredSize().height));
        drawPanel.setPreferredSize(new Dimension(cardDimension.width,cardDimension.height));

        frame.revalidate();
        frame.repaint();
    }

    @Override
    public void drawOrCounter(int drawCount){
        SwingUtilities.invokeLater(() -> drawDialog(drawCount));
    }

    @Override
    public void selectColor() throws RemoteException {
        SwingUtilities.invokeLater(() -> colorDialog());
    }

    //Dialogs
    //Dialog zur Entscheidung, ob man zieht
    private void drawDialog(int drawCount){
        UnoDialog drawDialog = new UnoDialog(frame,"Ziehen", "Kartenumfang: " + drawCount);

        JPanel buttonPanel = new JPanel();
        JButton draw = new JButton("Ziehen");
        draw.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    drawDialog.dispose();
                    player.takeDrawCount();
                } catch (RemoteException remoteException) {
                    remoteException.printStackTrace();
                }
            }
        });
        JButton play = new JButton("Spielen");
        play.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                drawDialog.dispose();
            }
        });
        buttonPanel.add(draw);
        buttonPanel.add(play);

        drawDialog.add(buttonPanel,BorderLayout.CENTER);
        drawDialog.setVisible(true);
    }

    //Dialog zum Farbe wünschen
    private void colorDialog(){
        UnoDialog colorDialog = new UnoDialog(frame,"Wünschen", "Wähle deine Wunsch-Farbe",new Dimension(800,200));
        JPanel panel = new JPanel();

        for(COLOR c : COLOR.values()) {
            if (COLOR.getExcluded().contains(c)) {continue;}
            System.out.println("media/colorCircles/"+c.getShortcut()+"Circle.png");
            JButton b = colorDialog.initPicButton("", "media/colorCircles/"+c.getShortcut()+"Circle.png");
            panel.add(b);
            b.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    try {
                        colorDialog.dispose();
                        player.wish(c);
                    } catch (RemoteException remoteException) {
                        remoteException.printStackTrace();
                    }
                }
            });
        }
        colorDialog.add(panel);
        colorDialog.setVisible(true);
    }

    //Getter/Setter
    public void setNews(String txt){
        news.setText(txt);
        frame.revalidate();
    }
}