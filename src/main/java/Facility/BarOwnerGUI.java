package Facility;

import Interfaces.EnrollmentInterface;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.security.PublicKey;
import java.util.ArrayList;

public class BarOwnerGUI extends JFrame {
    JFrame frame;
    JButton enrollButton;
    Facility facility;

    public BarOwnerGUI(Facility facility){
        this.facility = facility;

        frame = new JFrame("BarOwner");
        enrollButton = new JButton("Enroll");

        enrollButton.addActionListener(a -> {
            // Connect to Registrar server
            try {
                // fire to localhost port 2100
                Registry myRegistry = LocateRegistry.getRegistry("localhost", 2100);
                // search for RegistrarService
                EnrollmentInterface impl = (EnrollmentInterface) myRegistry.lookup("RegistrarService");

                /************************************* 1.1 FACILITY ENROLLMENT *************************************/
                String CF = facility.getCF();
                PublicKey publicKey = facility.getPublicKey();
                ArrayList<byte[]> signaturePair = facility.generateCFWithSignature();

                impl.registerFacility(CF, signaturePair, publicKey);
                facility.setKeyArray(impl.getKeyArray(facility.getId()));
                facility.setNymArray(impl.getNymArray(facility.getId()));

                System.out.println("Facility data after enrollment: "+facility);
                /************************************* 1.1 FACILITY ENROLLMENT *************************************/

            } catch (Exception e) { e.printStackTrace(); }

            facility.generateRandoms();
            facility.calculateQRCodes();
        });
        frame.setLayout(new FlowLayout());
        frame.add(enrollButton);
        frame.setSize(250,100);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }


}
