package Registrar;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class RegisterGUI extends JFrame{
    //Interface shows current content of the database (visitors + facilities) at each time
    RegistrarDB database;
    JFrame frame;
    public RegisterGUI(RegistrarDB database){
        this.database = database;

        frame = new JFrame("Registrar"); //Creates frame
        frame.setSize(1200,800);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true); //Makes frame visible
    }

    public void updateVisitors(){

    }
    public void updateFacilities(){

    }


}
