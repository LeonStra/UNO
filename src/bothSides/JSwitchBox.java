package bothSides;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

public class JSwitchBox extends AbstractButton {
    private Color colorBright = new Color(220,220,220);
    private Color colorDark = new Color(150,150,150);
    private Color black  = new Color(0,0,0,100);
    private Color white = new Color(255,255,255,100);
    private Color light = new Color(220,220,220,100);
    private Color red = new Color(160,130,130);
    private Color green = new Color(130,160,130);

    private int globalWidth = 0;
    private Dimension thumbBounds;
    private ArrayList<ActionListener> listener;


    public JSwitchBox(int width, boolean selected) {
        listener = new ArrayList<>();
        thumbBounds  = new Dimension(2*width,20);
        globalWidth =  4*width;
        setModel(new DefaultButtonModel());
        setSelected(selected);
        addMouseListener(new MouseAdapter(){
            @Override
            public void mouseReleased( MouseEvent e ) {
                if(new Rectangle(getPreferredSize()).contains(e.getPoint())){
                    setSelected(!isSelected());
                    listener.forEach(l -> l.actionPerformed(null));
                }
            }
        });
    }

    @Override
    public void addActionListener(ActionListener l) {
        listener.add(l);
    }

    @Override
    public Dimension getPreferredSize(){
        return new Dimension(globalWidth, thumbBounds.height);
    }

    @Override
    protected void paintComponent( Graphics g ){
        g.setColor( getBackground() );
        g.fillRoundRect(1, 1, getWidth()-2 - 1,getHeight()-2 ,2,2 );
        Graphics2D g2 = (Graphics2D)g;

        g2.setColor(black);
        g2.drawRoundRect(1,1,getWidth()-2 - 1, getHeight()-2 - 1,2,2 );
        g2.setColor(white);
        g2.drawRoundRect(1 + 1,1 + 1,getWidth()-2 - 3,getHeight()-2 - 3,2,2 );

        int x = 0;
        int y = 0;
        int w = thumbBounds.width;
        int h = thumbBounds.height;

        if(isSelected()) {
            setBackground(green);
            x = thumbBounds.width;
        } else{
            setBackground(red);
        }

        g2.setPaint( new GradientPaint(x, (int)(y-0.1*h), colorDark , x, (int)(y+1.2*h), light) );
        g2.fillRect( x, y, w, h );
        g2.setPaint( new GradientPaint(x, (int)(y+.65*h), light , x, (int)(y+1.3*h), colorDark) );
        g2.fillRect( x, (int)(y+.65*h), w, (int)(h-.65*h) );

        if (w>14){
            int size = 10;
            g2.setColor(colorBright);
            g2.fillRect(x+w/2-size/2,y+h/2-size/2, size, size);
            g2.setColor(new Color(120,120,120));
            g2.fillRect(x+w/2-4,h/2-4, 2, 2);
            g2.fillRect(x+w/2-1,h/2-4, 2, 2);
            g2.fillRect(x+w/2+2,h/2-4, 2, 2);
            g2.setColor(colorDark);
            g2.fillRect(x+w/2-4,h/2-2, 2, 6);
            g2.fillRect(x+w/2-1,h/2-2, 2, 6);
            g2.fillRect(x+w/2+2,h/2-2, 2, 6);
            g2.setColor(new Color(170,170,170));
            g2.fillRect(x+w/2-4,h/2+2, 2, 2);
            g2.fillRect(x+w/2-1,h/2+2, 2, 2);
            g2.fillRect(x+w/2+2,h/2+2, 2, 2);
        }

        g2.setColor(black);
        g2.drawRoundRect(x,y,w - 1,h - 1,2,2);
        g2.setColor( white );
        g2.drawRoundRect(x + 1,y + 1,w - 3,h - 3,2,2);

        g2.setColor(black.darker());
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setFont(getFont());
    }
}