package client;

import bothSides.Exceptions.NotSuitableException;
import bothSides.Exceptions.TurnException;
import bothSides.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.nimbus.NimbusLookAndFeel;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class Playground extends UnicastRemoteObject implements View {
    private final Dimension cardDimension = new Dimension(101,151);
    private final int sitesWidth = 300;
    private final EmptyBorder noBorder = new EmptyBorder(0,0,0,0);
    private boolean dark;
    private Player player;
    private ArrayList<JDialog> dialogs;

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
    private JButton buzzer;

    //Initialisierung des Fensters
    public Playground(Player player,String name) throws IOException{
        this.dark = false;
        this.player = player;
        this.dialogs = new ArrayList<>();
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
        this.buzzer = newImageButton("media/symbols/buzzer.png",new Dimension(64,64));

        this.player.setView(this);
        this.player.setName(name);

        //Fenster Einstellungen
        frame.setSize(1200,800);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setLocationRelativeTo(null);
        frame.setIconImage(new ImageIcon("media/symbols/logo.png").getImage());
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

    //Komponenten initialisieren
    private void init() throws RemoteException {
        //North Panel
        int northB = 64;
        JButton chat = newImageButton("media/symbols/chat.png",new Dimension(northB,northB));
        chat.addActionListener(e -> initChat());
        JButton settings = newImageButton("media/symbols/settings.png",new Dimension(northB,northB));
        settings.addActionListener(e -> initSettings());

        //News
        news = new JLabel("Start",SwingConstants.CENTER);
        news.setFont(new Font("Arial",Font.PLAIN,50));
        news.setPreferredSize(new Dimension(frame.getWidth()-(northB*4)-50,news.getPreferredSize().height));

        JPanel placeholder = new JPanel();
        placeholder.setPreferredSize(new Dimension(2*northB,northB));

        northPanel.add(placeholder);
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
        JPanel leftPanel = new JPanel(new GridBagLayout());
        leftPanel.add(buzzer);
        buzzer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    ((ExtPlayer) player).buzzed();
                } catch (RemoteException remoteException) {
                    remoteException.printStackTrace();
                }
            }
        });
        buzzer.setVisible(false);
        midPanel.add(leftPanel);
        midPanel.add(playPanel);
        midPanel.add(unoPanel);

        refresh();
    }

    //Chat initialisieren
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

    //Einstellungen initialisieren
    private void initSettings(){
        eastPanel.removeAll();
        settingPanel.removeAll();

        JSwitchBox darkSwitch = createSettingToggle("Dark-Mode",dark);
        darkSwitch.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dark = darkSwitch.isSelected();
                toggleDarkMode();
            }
        });

        try {
            JSwitchBox sortSwitch = createSettingToggle("Hand sortieren", player.getSorting());
            sortSwitch.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    try {
                        player.setSorting(sortSwitch.isSelected());
                    } catch (RemoteException remoteException) {
                        remoteException.printStackTrace();
                    }
                    toggleDarkMode();
                }
            });
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
                } catch (IOException fileNotFoundException) {
                    fileNotFoundException.printStackTrace();
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

    //Einstellungsmöglichkeit einrichten
    private JSwitchBox createSettingToggle(String txt, boolean selected){
        JPanel panel = new JPanel();
        JLabel label = new JLabel(txt);
        label.setFont(new Font("Arial",Font.BOLD,20));
        panel.add(label);
        JSwitchBox jSwitch = new JSwitchBox(10,selected);

        panel.add(jSwitch);
        panel.setPreferredSize(new Dimension(sitesWidth-5,panel.getPreferredSize().height));
        settingPanel.add(panel);
        return jSwitch;
    }

    //Dark/Light-Mode Wechsel
    private void toggleDarkMode(){
        try {
            if (dark){
                UIManager.setLookAndFeel(new NimbusLookAndFeel());
                UIManager.put("control", new Color(50, 50, 50));
                UIManager.put("info", new Color(50, 50, 50));
                UIManager.put("nimbusBase", new Color(36, 35, 35));
                UIManager.put("nimbusAlertYellow", new Color(248, 187, 0));
                UIManager.put("nimbusDisabledText", new Color(128, 128, 128));
                UIManager.put("nimbusFocus", new Color(115, 164, 209));
                UIManager.put("nimbusGreen", new Color(176, 179, 50));
                UIManager.put("nimbusInfoBlue", new Color(66, 139, 221));
                UIManager.put("nimbusLightBackground", new Color(18, 30, 49));
                UIManager.put("nimbusOrange", new Color(191, 98, 4));
                UIManager.put("nimbusRed", new Color(169, 46, 34));
                UIManager.put("nimbusSelectedText", new Color(255, 255, 255));
                UIManager.put("nimbusSelectionBackground", new Color(104, 93, 156));
                UIManager.put("text", new Color(230, 230, 230));
            } else{
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            }
            SwingUtilities.updateComponentTreeUI(frame);
        } catch (UnsupportedLookAndFeelException exc) {
            System.err.println("Nimbus: Unsupported Look and feel!");

        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
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
                    } catch (TurnException te) {
                        setNews("Nicht dein Zug");
                    }
                }
            });
        }
        return b;
    }

    //Einrichten eines Bild-Buttons
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

    //Dialog-Befehle
    @Override
    public void drawOrCounter(int drawCount){
        SwingUtilities.invokeLater(() -> drawDialog(drawCount));
    }

    @Override
    public void selectColor() throws RemoteException {
        SwingUtilities.invokeLater(() -> colorDialog());
    }

    @Override
    public void takeFromPlayPile() throws RemoteException {
        SwingUtilities.invokeLater(() -> takeFromPlayPileDialog());
    }

    @Override
    public void wishCard() throws RemoteException{
        SwingUtilities.invokeLater(()-> {
            try {
                wishCardDialog();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void bumms() throws RemoteException {
        SwingUtilities.invokeLater(() -> bummsDialog());
    }

    @Override
    public void toggleBuzzer(boolean b) throws RemoteException {
        buzzer.setVisible(b);
    }

    @Override
    public void fourThrowIn() throws RemoteException {
        SwingUtilities.invokeLater(() -> fourDialog());
    }

    //Dialog-Ausführung
    //Alle Dialoge schließen
    public void closeDialogs(){
        for (JDialog d : dialogs){
            d.dispose();
        }
        dialogs.removeAll(dialogs);
    }

    //Dialog zur Entscheidung, ob man zieht
    private void drawDialog(int drawCount){
        UnoDialog drawDialog = new UnoDialog(frame,"Ziehen", "Kartenumfang: " + drawCount);
        dialogs.add(drawDialog);
        JPanel buttonPanel = new JPanel();
        JButton draw = new JButton("Ziehen");
        draw.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    drawDialog.dispose();
                    dialogs.remove(drawDialog);
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
                dialogs.remove(drawDialog);
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
        dialogs.add(colorDialog);
        JPanel panel = new JPanel();

        for(COLOR c : COLOR.values()) {
            if (COLOR.getExcluded().contains(c)) {continue;}
            JButton b = colorDialog.initPicButton("", "media/colorCircles/"+c.getShortcut()+"Circle.png");
            panel.add(b);
            b.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    try {
                        colorDialog.dispose();
                        dialogs.remove(colorDialog);
                        player.wish(c);
                    } catch (RemoteException remoteException) {
                        remoteException.printStackTrace();
                    }
                }
            });
        }
        colorDialog.add(panel,BorderLayout.CENTER);
        colorDialog.setVisible(true);
    }

    //2
    private void takeFromPlayPileDialog(){
        UnoDialog dialog = new UnoDialog(frame,"Wähle eine Karte","Wähle deine Karte",new Dimension(800,700));
        dialogs.add(dialog);

        JPanel cardPanel = new JPanel();
        JPanel colorPanel = new JPanel();
        colorPanel.setPreferredSize(new Dimension(130,colorPanel.getPreferredSize().height));
        colorPanel.setBorder(new EmptyBorder(0,10,0,0));

        //Alle Farben
        for(COLOR color : COLOR.values()) {
            if (COLOR.getExcluded().contains(color)) {continue;}
            JButton b = dialog.initPicButton("", "media/colorCircles/"+color.getShortcut()+"Circle.png");
            colorPanel.add(b);
            b.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    cardPanel.removeAll();
                    try {
                        for ( Card c : ((ExtPlayer) player).getPlayPile()){
                            JButton cardButton = new JButton();
                            if (c.getColor().equals(color)){
                                cardButton = newCardButton(c,false);
                                cardPanel.add(cardButton);
                            }

                            cardButton.addActionListener(new ActionListener() {
                                @Override
                                public void actionPerformed(ActionEvent e) {
                                    try {
                                        dialog.dispose();
                                        dialogs.remove(dialog);
                                        ((ExtPlayer) player).chooseFromPlayPile(c);
                                    } catch (RemoteException remoteException) {
                                        remoteException.printStackTrace();
                                    }
                                }
                            });
                        }
                    } catch (RemoteException remoteException) {
                        remoteException.printStackTrace();
                    }
                    dialog.repaint();
                    dialog.revalidate();
                }
            });
        }

        dialog.add(colorPanel, BorderLayout.WEST);
        dialog.add(cardPanel, BorderLayout.CENTER);
        dialog.setVisible(true);
    }

    //Dialog zum Karte wünschen
    private void wishCardDialog() throws RemoteException {
        UnoDialog wishDialog = new UnoDialog(frame,"Karte wünschen","Wünsche dir eine Karte");
        dialogs.add(wishDialog);
        List<String> playersData = player.getNameList();
        playersData.remove(player.getName());
        List<TYPE> typesData = new ArrayList(Arrays.asList(TYPE.values()));
        typesData.remove(TYPE.UNKNOWN);
        List<COLOR> colorsData = new ArrayList(Arrays.asList(COLOR.values()));
        colorsData.removeAll(COLOR.getExcluded());

        JComboBox players = new JComboBox(playersData.toArray());
        JComboBox types = new JComboBox(typesData.toArray());
        JComboBox colors = new JComboBox(colorsData.toArray());
        JButton submit = new JButton("Ok");
        submit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    wishDialog.dispose();
                    dialogs.remove(wishDialog);
                    ((ExtPlayer)(player)).wishCard(new Card((TYPE) types.getSelectedItem(),(COLOR) colors.getSelectedItem()),(String) players.getSelectedItem());
                } catch (RemoteException remoteException) {
                    remoteException.printStackTrace();
                }
            }
        });
        JPanel panel = new JPanel();
        panel.add(players);
        panel.add(types);
        panel.add(colors);
        wishDialog.add(panel,BorderLayout.CENTER);
        wishDialog.add(submit,BorderLayout.SOUTH);
        wishDialog.setVisible(true);
    }

    private void bummsDialog(){
        UnoDialog dialog = new UnoDialog(frame,"BUMMMMMMMMS","Wie viele 8er sind im Ablagestapel?");
        dialogs.add(dialog);
        JSlider slider= new JSlider(JSlider.HORIZONTAL,1,8,1);
        slider.setMinorTickSpacing(1);
        slider.setMajorTickSpacing(1);
        slider.setPaintTicks(true);
        slider.setPaintLabels(true);
        JButton submit = new JButton("OK");
        submit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dialog.dispose();
                dialogs.remove(dialog);
                try {
                    ((ExtPlayer)player).bummsReturn(slider.getValue());
                } catch (RemoteException remoteException) {
                    remoteException.printStackTrace();
                }
            }
        });
        dialog.add(slider,BorderLayout.CENTER);
        dialog.add(submit,BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void fourDialog(){
        UnoDialog dialog = new UnoDialog(frame,"Ziehen", "Willst du deine 4 reinwerfen?");
        dialogs.add(dialog);
        JPanel buttonPanel = new JPanel();
        JButton draw = new JButton("Ja");
        draw.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    dialog.dispose();
                    dialogs.remove(dialog);
                    ((ExtPlayer)player).throwIn();
                } catch (RemoteException | NotSuitableException remoteException) {
                    remoteException.printStackTrace();
                }
            }
        });
        JButton play = new JButton("Nein");
        play.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dialog.dispose();
                dialogs.remove(dialog);
            }
        });
        buttonPanel.add(draw);
        buttonPanel.add(play);

        dialog.add(buttonPanel,BorderLayout.CENTER);
        dialog.setVisible(true);
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