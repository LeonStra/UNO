package client;

import Exceptions.NotSuitableException;
import bothSides.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.LinkedList;

public class Playground extends UnicastRemoteObject implements View {
    private final Dimension cardDimension = new Dimension(101,151);
    private final int sitesWidth = 300;
    private final EmptyBorder noBorder = new EmptyBorder(0,0,0,0);
    private boolean dark;
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
    private JTextArea chatHistory;
    private JPanel settingPanel;
    private JLabel news;

    public Playground(Player player,String name) throws IOException{
        this.dark = false;
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
        this.chatHistory = new JTextArea();
        this.settingPanel = new JPanel();
        this.playPanel = new JPanel(new GridLayout());

        this.player.setView(this);
        this.player.setName(name);

        //Fenster Einstellungen
        frame.setSize(1200,800);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setLocationRelativeTo(null);
        frame.setLayout(new BorderLayout());
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
        eastPanel.setLayout(new BorderLayout());
        chatHistory.setEditable(false);
        chatHistory.setLineWrap(true);

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
        JPanel unoPanel = new JPanel(new GridBagLayout());
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
        eastPanel.add(new JScrollPane(chatHistory),BorderLayout.CENTER);
        eastPanel.add(panel,BorderLayout.SOUTH);
        frame.revalidate();
        frame.repaint();
    }

    private void initSettings(){
        eastPanel.removeAll();
        settingPanel.removeAll();

        JSwitchBox darkSwitch = settingToggle("Dark-Mode");
        darkSwitch.addActionListener(e -> toggleDarkMode(darkSwitch.isSelected()));
        darkSwitch.setSelected(!dark);
        JSwitchBox sort = settingToggle("Hand sortieren");
        sort.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e){
                try {
                    player.setSorting(sort.isSelected());
                } catch (RemoteException remoteException) {
                    remoteException.printStackTrace();
                }
            }
        });
        try {
            sort.setSelected(!player.getSorting());
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        JButton sources = new JButton("Quellen");
        sources.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JDialog dialog = new JDialog(frame);
                dialog.setSize(800,500);
                dialog.setLocationRelativeTo(null);
                dialog.setTitle("Quellen");
                JTextArea txt = new JTextArea();
                txt.setLineWrap(true);
                txt.setEditable(false);
                try {
                    BufferedReader reader = new BufferedReader(new FileReader("media/Quellen.txt"));
                    String line;
                    while ((line = reader.readLine()) != null){
                        txt.append(line + "\n");
                    }
                } catch (FileNotFoundException fileNotFoundException) {
                    fileNotFoundException.printStackTrace();
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
                dialog.add(txt);
                dialog.setVisible(true);
            }
        });
        eastPanel.add(settingPanel,BorderLayout.CENTER);
        eastPanel.add(sources,BorderLayout.SOUTH);
        frame.revalidate();
        frame.repaint();
    }
    private JSwitchBox settingToggle(String txt){
        JPanel panel = new JPanel();
        JLabel label = new JLabel(txt);
        label.setFont(new Font("Arial",Font.BOLD,20));
        panel.add(label);
        JSwitchBox jSwitch = new JSwitchBox(10);
        panel.add(jSwitch);
        panel.setPreferredSize(new Dimension(sitesWidth-5,panel.getPreferredSize().height));
        settingPanel.add(panel);
        return jSwitch;
    }

    private void toggleDarkMode(boolean b){
        System.out.println(b);
        dark = !dark;
        /*Dark-Mode*/
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
        chatHistory.setText("");
        for (ChatMessage m : messages){
            chatHistory.setForeground(Color.BLUE);
            chatHistory.append(m.getName() +": ");
            chatHistory.setForeground(Color.BLACK);
            chatHistory.append(m.getMessage() + "\n");
        }

        eastPanel.repaint();
        eastPanel.revalidate();
    }
}