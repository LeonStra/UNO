package client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

public class PlaceholderInput extends JTextField implements FocusListener {

    public PlaceholderInput(String txt, int columns, Font font){
        super(txt,columns);
        this.addFocusListener(this);
        this.setFont(font);
    }

    @Override
    public void focusGained(FocusEvent e) {
        this.selectAll();
    }

    @Override
    public void focusLost(FocusEvent e) {
        this.select(0,0);
    }
}
