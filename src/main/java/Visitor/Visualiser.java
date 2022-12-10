package Visitor;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDateTime;

import static Services.Methods.timeStampToString;

public class Visualiser extends JPanel {

    static int [] getallen;

    public Visualiser(byte[] data) {
        // Convert to all positive numbers
        getallen = new int[data.length];
        for (int i = 0; i < data.length; i++) {
            getallen[i]=data[i]+128;
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(new Color(getallen[0],getallen[1],getallen[2]));
        g.fillRect(50,50, 100, 100);
        g.setColor(new Color(getallen[3],getallen[4],getallen[5]));
        g.fillRect(150,50, 100, 100);
        g.setColor(new Color(getallen[6],getallen[7],getallen[8]));
        g.fillRect(250,50, 100, 100);

        g.setColor(new Color(getallen[9],getallen[10],getallen[11]));
        g.fillRect(50,150, 100, 100);
        g.setColor(new Color(getallen[12],getallen[13],getallen[14]));
        g.fillRect(150,150, 100, 100);
        g.setColor(new Color(getallen[15],getallen[16],getallen[17]));
        g.fillRect(250,150, 100, 100);

        g.setColor(new Color(getallen[18],getallen[19],getallen[20]));
        g.fillRect(50,250, 100, 100);
        g.setColor(new Color(getallen[21],getallen[22],getallen[23]));
        g.fillRect(150,250, 100, 100);
        g.setColor(new Color(getallen[24],getallen[25],getallen[26]));
        g.fillRect(250,250, 100, 100);
    }
}
