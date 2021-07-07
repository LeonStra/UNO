package client;

import Exceptions.NotSuitableException;
import bothSides.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class Playground extends UnicastRemoteObject implements View {
    private final Dimension cardDimension = new Dimension(101,151);
    private final int sitesWidth = 300;
    private final EmptyBorder noBorder = new EmptyBorder(0,0,0,0);
    private Player player;

    //GUI
    private JFrame frame;
    private JPanel northPanel;
    private JPanel westPanel;
    private JPanel midPanel;
    private JPanel eastPanel;
    private JPanel southPanel;
    private JPanel handPanel;
    private JButton drawButton;
    private JList playerList;
    private JPanel playPanel;
    private JLabel news;

    public Playground(Player player,String name) throws IOException{
        this.player = player;
        this.frame = new JFrame("INFUNO");
        this.handPanel = new JPanel();
        this.drawButton = newCardButton(new Card(TYPE.UNKNOWN, COLOR.UNKNOWN),false);
        this.midPanel = new JPanel(new GridLayout(1,3));
        this.northPanel = new JPanel();
        this.westPanel = new JPanel();
        this.eastPanel = new JPanel();
        this.southPanel = new JPanel();
        this.playerList = new JList();
        this.playPanel = new JPanel(new GridLayout());

        this.player.setView(this);
        this.player.setName(name);

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

        //Zusammensetzen
        southPanel.add(handPanel);
        frame.add(northPanel, BorderLayout.NORTH);
        frame.add(westPanel, BorderLayout.WEST);
        frame.add(eastPanel, BorderLayout.EAST);
        frame.add(southPanel, BorderLayout.SOUTH);
        frame.add(midPanel, BorderLayout.CENTER);
        frame.setVisible(true);
        init();
    }

    private void init() throws RemoteException {
        //North Panel
        JButton chat = newImageButton("media/symbols/chat.png",new Dimension(64,64));
        JButton settings = newImageButton("media/symbols/settings.png",new Dimension(64,64));

        //News
        news = new JLabel("Start",SwingConstants.CENTER);
        news.setFont(new Font("Arial",0,50));
        news.setPreferredSize(new Dimension(frame.getWidth()-128-35,news.getPreferredSize().height));

        northPanel.add(news);
        northPanel.add(chat);
        northPanel.add(settings);
        northPanel.setPreferredSize(new Dimension(frame.getWidth(),northPanel.getPreferredSize().height));

        //West Panel bzw. playerList
        playerList.setSelectionBackground(null);
        playerList.setFocusable(false);
        playerList.setFont(new Font("Arial",0,25));
        playerList.setBorder(new EmptyBorder(0,10,0,0));
        playerList.setPreferredSize(new Dimension(sitesWidth,frame.getHeight()));

        westPanel.add(playerList);
        westPanel.setPreferredSize(new Dimension(sitesWidth,frame.getHeight()));

        //East Panel bzw. Chat
        eastPanel.setPreferredSize(new Dimension(sitesWidth,frame.getHeight()));
        eastPanel.setBackground(Color.WHITE);

        //South Panel
        handPanel.setBorder(noBorder);

        //Nachziehstapel
        drawButton.setBorder(noBorder);
        drawButton.setPreferredSize(cardDimension);
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
        southPanel.add(drawButton);

        //Center Panel
        JButton uno = newImageButton("media/symbols/unoButton.png",new Dimension(100,100));
        uno.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                /*
                * UNO-Button gedrückt
                * */
            }
        });
        midPanel.add(new Label());
        midPanel.add(playPanel);
        midPanel.add(uno);

        refresh();
    }

    //Einrichten einer Handkarte
    private JButton newCardButton(Card card,boolean play){
        JButton b = newImageButton(card.getPath(),cardDimension);
        if (play) {
            b.addActionListener(new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    try {
                        player.play(card);
                    } catch (RemoteException remoteException) {
                        setNews("Verbindungsfehler");
                    } catch (NotSuitableException nse) {
                        setNews("Passt nicht");
                        //Fehler anzeigen
                    }
                }
            });
        }
        return b;
    }

    private JButton newImageButton(String path,Dimension dimension){
        JButton b = new JButton();
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        b.setContentAreaFilled(false);
        b.setIcon(new ImageIcon(path));
        b.setBorder(noBorder);
        b.setPreferredSize(dimension);
        return b;
    }

    //Aktualisieren/Einrichten der Ansicht
    public void refresh() throws RemoteException{
        handPanel.removeAll();
        playPanel.removeAll();

        //Spielerliste
        playerList.setListData(player.getNameList().toArray());

        //Handkarten
        JPanel cardPanel = new JPanel();
        for (Card card : player.getHand()){
            cardPanel.add(newCardButton(card,true));
        }
        JScrollPane handScroll = new JScrollPane(cardPanel,ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        handPanel.add(handScroll);

        handPanel.setPreferredSize(new Dimension(frame.getWidth()-cardDimension.width-35,handPanel.getPreferredSize().height));
        handScroll.setBorder(noBorder);
        handScroll.setPreferredSize(handPanel.getPreferredSize());

        //Ablagestapel
        JButton playPile = newCardButton(player.getTop(),false);
        playPanel.add(playPile);

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

    //Pass Button aktivieren
    public void changeDrawPass(boolean draw){
        if (draw) {
            drawButton.setIcon(new ImageIcon(new Card(TYPE.UNKNOWN, COLOR.UNKNOWN).getPath()));
        }else {
        drawButton.setIcon(new ImageIcon("media/symbols/pass.png"));
        }
        frame.revalidate();
    }

    //Getter/Setter
    public void setNews(String txt){
        news.setText(txt);
        frame.revalidate();
    }
}