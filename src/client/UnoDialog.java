package client;

import javax.swing.*;
import java.awt.*;

public class UnoDialog extends JDialog{

    public UnoDialog(JFrame frame,String title,String labelText){
        //Dialog einrichten
        super(frame,true);
        setSize(400,200);
        setLocationRelativeTo(frame);
        setTitle(title);
        setLayout(new BorderLayout());
        setDefaultCloseOperation(0);

        //Label
        JLabel label = new JLabel(labelText,JLabel.CENTER);
        label.setFont(new Font("Arial",0,20));

        add(label,BorderLayout.NORTH);
    }

    public UnoDialog(JFrame frame,String title,String labelText,Dimension dimension){
        this(frame,title,labelText);
        setSize(dimension);
    }

    public JButton initButton(String text){
        JButton b = new JButton(text);
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        b.setContentAreaFilled(false);
        return b;
    }

    public JButton initPicButton(String text, String path){
        JButton b = initButton(text);
        b.setIcon(new ImageIcon(path));
        return b;
    }
}
