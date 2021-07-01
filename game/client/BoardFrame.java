package client;

import bothSides.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.rmi.RemoteException;

public class BoardFrame extends JFrame {
    private final Dimension cardDimension = new Dimension(101,151);
    private Player player;

    private JPanel southPanel;
    private JPanel handPanel;
    private JPanel drawPanel;
    private JPanel playPanel;

    public BoardFrame(Player player) throws IOException, InterruptedException{
        super("UNO");

        this.player = player;
        this.southPanel = new JPanel();
        this.handPanel = new JPanel();
        this.drawPanel= new JPanel();
        this.playPanel = new JPanel();

        //Fenster Einstellungen
        setSize(1200,800);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                try {
                    player.leaveGame();
                } catch (RemoteException remoteException) {
                    remoteException.printStackTrace();
                }
                System.exit(0);
            }
        });

        handPanel.setBorder(new EmptyBorder(0,0,0,0));
        drawPanel.setBorder(new EmptyBorder(0,0,0,0));
        playPanel.setLayout(new GridLayout(2,1));

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

        refresh();
        southPanel.add(handPanel);
        southPanel.add(drawPanel);
        add(southPanel, BorderLayout.SOUTH);
        add(playPanel, BorderLayout.CENTER);
        setVisible(true);
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
                        refresh();
                    } catch (InterruptedException | RemoteException interruptedException) {
                        interruptedException.printStackTrace();
                        //Server Probleme
                    }
                }
            });
        }
        return b;
    }

    //Aktualisieren/Einrichten der Ansicht
    public void refresh() throws RemoteException, InterruptedException {
        handPanel.removeAll();
        playPanel.removeAll();

        //Handkarten
        System.out.println(player.getHand());
        //handPanel.removeAll();
        for (Card card : player.getHand()){
            handPanel.add(newCardButton(card,true));
        }

        //Ablagestapel
        JButton playPile = newCardButton(player.getTop(),false);
        playPanel.add(playPile);

        handPanel.setPreferredSize(new Dimension(this.getWidth()-cardDimension.width-35,handPanel.getPreferredSize().height));
        drawPanel.setPreferredSize(new Dimension(cardDimension.width,drawPanel.getPreferredSize().height));

        revalidate();
        repaint();
    }
}
