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
import java.util.LinkedList;

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
    private JPanel chatHistory;
    private JLabel news;

    public Playground(Player player,String name) throws IOException{
        this.player = player;
        this.frame = new JFrame("INFUNO");
        this.handPanel = new JPanel();
        this.drawButton = newCardButton(new Card(TYPE.UNKNOWN, COLOR.UNKNOWN),false);
        this.midPanel = new JPanel(new GridLayout(1,3));
        this.northPanel = new JPanel();
        this.westPanel = new JPanel();
        this.eastPanel = new JPanel(new BorderLayout());
        this.southPanel = new JPanel();
        this.playerList = new JList();
        this.chatHistory = new JPanel();
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
        chat.addActionListener(e -> initChat());
        JButton settings = newImageButton("media/symbols/settings.png",new Dimension(64,64));
        settings.addActionListener(e -> initSettings());

        //News
        news = new JLabel("Start",SwingConstants.CENTER);
        news.setFont(new Font("Arial",Font.PLAIN,50));
        news.setPreferredSize(new Dimension(frame.getWidth()-128-35,news.getPreferredSize().height));

        northPanel.add(news);
        northPanel.add(chat);
        northPanel.add(settings);
        northPanel.setPreferredSize(new Dimension(frame.getWidth(),northPanel.getPreferredSize().height));

        //West Panel bzw. playerList
        playerList.setSelectionBackground(null);
        playerList.setFocusable(false);
        playerList.setFont(new Font("Arial",Font.PLAIN,25));
        playerList.setBorder(new EmptyBorder(0,10,0,0));
        playerList.setPreferredSize(new Dimension(sitesWidth,frame.getHeight()));

        westPanel.add(playerList);
        westPanel.setPreferredSize(new Dimension(sitesWidth,frame.getHeight()));

        //East Panel bzw. Chat
        eastPanel.setBorder(BorderFactory.createLineBorder(Color.black));
        eastPanel.setPreferredSize(new Dimension(sitesWidth,frame.getHeight()));
        chatHistory.setPreferredSize(new Dimension(sitesWidth,frame.getHeight()));

        initChat();

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
        JPanel unoPanel = new JPanel();
        JButton uno = newImageButton("media/symbols/unoButton.png",new Dimension(100,100));
        uno.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    player.sayUno();
                } catch (RemoteException remoteException) {
                    remoteException.printStackTrace();
                }
            }
        });
        unoPanel.add(uno);
        midPanel.add(new Label());
        midPanel.add(playPanel);
        midPanel.add(unoPanel);

        refresh();
    }

    private void initChat(){
        eastPanel.removeAll();
        JPanel panel = new JPanel();
        PlaceholderInput chatInput = new PlaceholderInput("Nachricht",10,new Font("Arial",Font.ITALIC,20));
        JButton send = new JButton("Send");
        send.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    player.sendMessage(chatInput.getText());
                } catch (RemoteException remoteException) {
                    remoteException.printStackTrace();
                }
            }
        });
        panel.add(chatInput);
        panel.add(send);
        eastPanel.add(panel,BorderLayout.SOUTH);
        eastPanel.add(chatHistory,BorderLayout.CENTER);
        frame.revalidate();
    }

    private void initSettings(){
        eastPanel.removeAll();
        frame.repaint();
        frame.revalidate();
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
    @Override
    public void changeDrawPass(boolean draw){
        if (draw) {
            drawButton.setIcon(new ImageIcon(new Card(TYPE.UNKNOWN, COLOR.UNKNOWN).getPath()));
        }else {
        drawButton.setIcon(new ImageIcon("media/symbols/pass.png"));
        }
        frame.revalidate();
    }

    //Getter/Setter
    @Override
    public void setNews(String txt){
        news.setText(txt);
        frame.revalidate();
    }

    @Override
    public void refreshChat(LinkedList<ChatMessage> messages){
        chatHistory.removeAll();
        JList list = new JList();
        list.setFont(new Font("Arial",Font.BOLD,15));
        //list.setPreferredSize(new Dimension(sitesWidth,list.getPreferredSize().height));
        list.setPreferredSize(new Dimension(sitesWidth, frame.getHeight()));

        LinkedList<String> chat = new LinkedList<>();
        for (ChatMessage m : messages){
            chat.add(m.getName());
            chat.add(m.getMessage());
        }
        list.setListData(chat.toArray());

        JPanel panel = new JPanel();
        panel.setBorder(noBorder);
        panel.add(list);
        JScrollPane scrollPane = new JScrollPane(panel,ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setPreferredSize(new Dimension(chatHistory.getWidth(), eastPanel.getHeight()-100));
        chatHistory.add(scrollPane);

        /*for (ChatMessage m : messages){
            JLabel name = new JLabel(m.getName());
            name.setFont(new Font("Arial",Font.BOLD,15));
            name.setForeground(Color.BLUE);
            name.setBorder(new EmptyBorder(0,5,0,0));
            //name.setPreferredSize(new Dimension(sitesWidth,name.getPreferredSize().height));
            JLabel msg = new JLabel(m.getMessage());
            msg.setFont(new Font("Arial",Font.BOLD,15));
            msg.setForeground(Color.BLACK);
            msg.setBorder(new EmptyBorder(0,10,0,0));
            //msg.setPreferredSize(new Dimension(sitesWidth,msg.getPreferredSize().height));
            panel.add(name);
            panel.add(msg);
        }
        JScrollPane scrollPane = new JScrollPane(panel,ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setBorder(noBorder);
        scrollPane.setPreferredSize(new Dimension(chatHistory.getWidth(), eastPanel.getHeight()-100));
        chatHistory.add(scrollPane);*/

        frame.repaint();
        frame.revalidate();
    }
}