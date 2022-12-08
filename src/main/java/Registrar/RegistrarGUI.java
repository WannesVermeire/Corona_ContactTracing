package Registrar;

import Facility.Facility;
import Visitor.Visitor;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Map;

public class RegistrarGUI extends JFrame{
    //Interface shows current content of the database (visitors + facilities) at each time
    JFrame frame;
    JPanel visitorsPanel;
    JPanel facilitiesPanel;
    RegistrarDB database;
    private Map<String, Facility> facilities; // key = CF
    private Map<String, Visitor> visitors;

    public RegistrarGUI(RegistrarDB database){
        this.database = database;

        frame = new JFrame("Registrar"); //Creates frame
        visitorsPanel = new JPanel();
        facilitiesPanel = new JPanel();

        frame.setLayout(new GridLayout(2,2));
        frame.add(new JLabel("Visitors"));
        frame.add(new JLabel("Facilities"));
        frame.setSize(1200,800);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true); //Makes frame visible
    }

    public void updateVisitors(){
        this.visitors = database.getVisitors();
        updateFrame();
    }
    public void updateFacilities(){
        this.facilities = database.getFacilities();
        updateFrame();
    }
    public void updateFrame(){
        visitorsPanel = new JPanel();
        for (var entry : visitors.entrySet()) {
            visitorsPanel.add(new JTextArea(entry.getKey() + ":" + entry.getValue()));
        }
        facilitiesPanel = new JPanel();
        for (var entry : facilities.entrySet()) {
            facilitiesPanel.add(new JTextArea(entry.getKey() + ":" + entry.getValue()));
        }
    }


}
