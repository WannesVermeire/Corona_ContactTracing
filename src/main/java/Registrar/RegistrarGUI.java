package Registrar;

import Facility.Facility;
import Services.MultiLineCellRenderer;
import Visitor.Visitor;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class RegistrarGUI extends JFrame{
    //Interface shows current content of the database (visitors + facilities) at each time
    JFrame frame;
    JPanel gridPanel = new JPanel();
    JPanel visitorsPanel;
    JPanel facilitiesPanel;
    RegistrarDB database;
    private Map<String, Facility> facilities; // key = CF
    private Map<String, Visitor> visitors;

    public RegistrarGUI(RegistrarDB database){
        this.database = database;
        this.facilities = new HashMap<>();
        this.visitors = new HashMap<>();

        frame = new JFrame("Registrar"); //Creates frame

        updateFrame();
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
        frame.remove(gridPanel);

        gridPanel = new JPanel();
        gridPanel.setLayout(new GridLayout(1,2));

        visitorsPanel = new JPanel();
        visitorsPanel.setLayout(new BorderLayout());

        facilitiesPanel = new JPanel();
        facilitiesPanel.setLayout(new BorderLayout());

        visitorsPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Visitors", TitledBorder.LEFT,
                TitledBorder.TOP));
        facilitiesPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Facilities", TitledBorder.LEFT,
                TitledBorder.TOP));

        DefaultTableModel dmVisitor = new DefaultTableModel() {
            public Class getColumnClass(int columnIndex) {
                return String.class;
            }
        };
        DefaultTableModel dmFacility = new DefaultTableModel() {
            public Class getColumnClass(int columnIndex) {
                return String.class;
            }
        };
        String visitorsColumns[] = {
             "Key",
             "Value"
        };
        String[][] visitorsData = new String[visitors.size()][2];
        int i = 0;
        for (var entry : visitors.entrySet()) {
            visitorsData[i][0] = entry.getKey();
            visitorsData[i][1] = entry.getValue().toGUIString();
            i++;
        }
        dmVisitor.setDataVector(visitorsData,visitorsColumns);
        JTable visitorTable = new JTable(dmVisitor);
        visitorTable.setRowHeight(120);
        visitorTable.setDefaultRenderer(String.class, new MultiLineCellRenderer());
        JScrollPane visitorScroll = new JScrollPane(visitorTable);
        visitorsPanel.add(visitorScroll);

        String facilityColumns[] = {
                "key",
                "value"
        };
        String[][] facilitiesData = new String[facilities.size()][2];;
        i=0;
        for (var entry : facilities.entrySet()) {
            facilitiesData[i][0] = entry.getKey();
            facilitiesData[i][1] = entry.getValue().toGUIString();
            i++;
        }
        dmFacility.setDataVector(facilitiesData,facilityColumns);
        JTable facilityTable = new JTable(dmFacility);
        facilityTable.setRowHeight(500);
        facilityTable.setDefaultRenderer(String.class, new MultiLineCellRenderer());
        JScrollPane facilityScroll = new JScrollPane(facilityTable);
        facilitiesPanel.add(facilityScroll);


        gridPanel.add(visitorsPanel);
        gridPanel.add(facilitiesPanel);
        frame.setLayout(new BorderLayout());
        frame.add(gridPanel);
        frame.setSize(1900,800);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true); //Makes frame visible
    }
}
