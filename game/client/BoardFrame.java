package client;

import bothSides.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

public class BoardFrame extends JFrame {
    private final Dimension cardDimension = new Dimension(101,151);
    private Player player;

    public BoardFrame(Player player) throws IOException, InterruptedException{
        super("UNO");

        this.player = player;

        //Fenster Einstellungen
        setSize(1200,800);
        setLocationRelativeTo(null);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
        refresh();
    }

    //Einrichten einer Handkarte
    private JButton newButton(Card card){
        JButton b = new JButton();
        b.setPreferredSize(cardDimension);
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        b.setContentAreaFilled(false);
        b.setIcon(new ImageIcon(card.getPath()));
        b.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    player.play(card);
                    refresh();
                } catch (InterruptedException interruptedException) {
                    interruptedException.printStackTrace();
                } catch (RemoteException remoteException) {
                    remoteException.printStackTrace();
                }
            }
        });
        return b;
    }

    //Aktualisieren/Einrichten der Ansicht
    public void refresh() throws RemoteException, InterruptedException {
        revalidate();
        setLayout(new BorderLayout());

        //Handkarten
        JPanel southPanel = new JPanel();

        JPanel handPanel = new JPanel();
        handPanel.setBorder(new EmptyBorder(0,0,0,0));
        for (Card card : player.getHand()){
            handPanel.add(newButton(card));
        }
        handPanel.setPreferredSize(new Dimension(this.getWidth()-cardDimension.width-35,handPanel.getPreferredSize().height));
        southPanel.add(handPanel);

        //Nachziehstapel
        JPanel drawPanel = new JPanel();
        drawPanel.setBorder(new EmptyBorder(0,0,0,0));
        JButton b = newButton(new Card(TYPE.UNKNOWN, COLOR.UNKNOWN));
        b.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    player.drawCard();
                } catch (InterruptedException interruptedException) {
                    interruptedException.printStackTrace();
                } catch (RemoteException remoteException) {
                    remoteException.printStackTrace();
                }
            }
        });
        drawPanel.add(b);
        drawPanel.setPreferredSize(new Dimension(cardDimension.width,drawPanel.getPreferredSize().height));
        southPanel.add(drawPanel);

        add(southPanel, BorderLayout.SOUTH);

        //
        JPanel playPanel = new JPanel();
        playPanel.setLayout(new GridLayout(2,1));

        //Spieler
        JPanel playerPanel = new JPanel();
        //...
        playPanel.add(playerPanel);

        //Ablagestapel342x512
        JButton playPile = newButton(player.getTop());
        //playPanel.add();

        add(playPanel, BorderLayout.CENTER);

        setVisible(true);
    }
}
