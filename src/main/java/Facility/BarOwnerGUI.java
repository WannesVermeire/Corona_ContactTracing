package Facility;

import Interfaces.RegistrarInterface;
import Visitor.VisitorGUI;
import com.google.zxing.NotFoundException;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Calendar;

import static Services.Methods.connectToRegistrar;

public class BarOwnerGUI extends JFrame {
    private JFrame frame;
    private JButton enrollButton;
    private Facility facility;

    private JButton refreshButton;
    public BarOwnerGUI(Facility facility)  throws NotFoundException, IOException {
        this.facility = facility;

        frame = new JFrame("BarOwner - " + facility.getName());
        enrollButton = new JButton("Enroll");

        refreshButton = new JButton("REFRESH");
        enrollButton.addActionListener(a -> {
            // Connect to Registrar server
            try {
                RegistrarInterface impl = connectToRegistrar();
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


        refreshButton.addActionListener(a -> {
            if (a.getSource() == refreshButton) {

                frame.dispose();
                try {
                    new BarOwnerGUI(this.facility);
                } catch (RemoteException e) {
                    throw new RuntimeException(e);
                } catch (NotFoundException e) {
                    throw new RuntimeException(e);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        frame.setLayout(new FlowLayout());
//        frame.add(refreshButton);
        frame.add(enrollButton);
        frame.add(new JLabel(new ImageIcon("QRCodes_" + facility.getName() + "/QRCode_day" + Calendar.getInstance().get(Calendar.DAY_OF_MONTH) + ".jpg")));
        frame.setSize(250,400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }


}
