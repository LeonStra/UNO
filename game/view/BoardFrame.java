package view;

import cards.Card;
import main.Player;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;

public class BoardFrame extends JFrame {
    private final Dimension cardDimension = new Dimension(101,151);
    private Player player;

    public BoardFrame(Player player){
        super("UNO");
        this.player = player;
        setLayout(new BorderLayout());

        //this.hand = new PlayerHand();
        //add(hand, BorderLayout.SOUTH);
        JPanel panel = new JPanel();

        for (Card card : player.getHand()){
            panel.add(newButton(card));
        }

        add(panel, BorderLayout.SOUTH);

        setSize(1200,800);
        setLocationRelativeTo(null);
        setVisible(true);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
    }

    //Einrichten der Karte
    public JButton newButton(Card card){
        JButton b = new JButton();
        b.setPreferredSize(cardDimension);
        b.setIcon(new ImageIcon(card.getPath()));
        b.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                player.play(card);
            }
        });
        return b;
    }

    //Aktualisieren der Ansicht
    public void refresh(){

    }
}
