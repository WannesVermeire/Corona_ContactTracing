package Facility;

import Interfaces.RegistrarInterface;
import com.google.zxing.NotFoundException;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Calendar;

public class BarOwnerGUI extends JFrame {
    private JFrame frame;
    private JButton enrollButton;
    private Facility facility;

    public BarOwnerGUI(Facility facility)  throws NotFoundException, IOException {
        this.facility = facility;

        frame = new JFrame("BarOwner - " + facility.getName());
        enrollButton = new JButton("Enroll");

        enrollButton.addActionListener(a -> {
            // Connect to Registrar server
            try {
                // fire to localhost port 2100
                Registry myRegistry = LocateRegistry.getRegistry("localhost", 2100);
                // search for RegistrarService
                RegistrarInterface impl = (RegistrarInterface) myRegistry.lookup("RegistrarService");

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
            try {
                facility.calculateQRCodes();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        frame.setLayout(new FlowLayout());
        frame.add(enrollButton);
        frame.add(new JLabel(new ImageIcon("QRCodes_" + facility.getName() + "/QRCode_day" + Calendar.getInstance().get(Calendar.DAY_OF_MONTH) + ".jpg")));
        frame.setSize(250,400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }


}
