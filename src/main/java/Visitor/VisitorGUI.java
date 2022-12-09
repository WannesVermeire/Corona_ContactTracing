package Visitor;

import Interfaces.RegistrarInterface;
import Interfaces.MixingProxyInterface;

import javax.swing.*;
import java.awt.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

import static Services.Methods.stringToHash;

public class VisitorGUI extends JFrame {
    private JFrame frame;
    private JButton enrollButton;
    private JButton visitButton;
    private JButton writeToFileButton;
    private Visitor visitor;
    private int saveDuration = 14; // Days before we delete the capsules from visiting a facility
    private int incubation = 0;
    private PublicKey publicKeyRegistrar = null;

    public VisitorGUI(Visitor visitor) {
        this.visitor = visitor;

        frame = new JFrame("Visitor");
        enrollButton = new JButton("Enroll");
        visitButton = new JButton("Visit the facility");
        writeToFileButton = new JButton("Write logs to file");


        enrollButton.addActionListener(a -> {
            /************************************** 1.2 USER ENROLLMENT *************************************/
            try {
                // fire to localhost port 2100
                Registry myRegistry = LocateRegistry.getRegistry("localhost", 2100);
                // search for RegistrarService
                RegistrarInterface impl = (RegistrarInterface) myRegistry.lookup("RegistrarService");

                incubation = impl.getINCUBATION_DAYS();

                // Register visitor to the registrar
                boolean registrationSuccessful = impl.registerVisitor(visitor.getName(), visitor.getPhoneNr());
                if (registrationSuccessful) System.out.println("Visitor data after enrollment: " + visitor);
                else System.out.println("Something went wrong during enrollment of: " + visitor);

                // Get a set of signed tokens
                visitor.setTokens(impl.getSignedTokens(visitor.getPhoneNr()));
                System.out.println("Visitor data after receiving tokens: " + visitor);

                // Key needed in 2. Visiting a facility
                publicKeyRegistrar = impl.getPublicKey();
            } catch (Exception e) {
                e.printStackTrace();
            }
            /************************************* 1.2 USER ENROLLMENT **************************************/
        });

        visitButton.addActionListener(a -> {
            /*********************************** 2. VISITING A FACILITY *************************************/
            try {
                // visitor scans a QR code
                Visit visit = visitor.scanQR(); // Also saves Visit in Visitor

                // fire to localhost port 2200
                Registry mixingProxyRegistry = LocateRegistry.getRegistry("localhost", 2200);
                // search for MixingProxyService
                MixingProxyInterface mpi = (MixingProxyInterface) mixingProxyRegistry.lookup("MixingProxyService");

                // Create a capsule and send it to the MixingProxy to verify
                // Capsule = timestamp, T_user_x_dayi, hash(Ri,num_CF_dayi) (hash uit de QR-code dus)
                int today = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
                ArrayList<byte[]> tokenPair = visitor.getAndRemoveToken(today);
                visit.setTokenPair(tokenPair);

                ArrayList<byte[]> signedConfirmation = mpi.verifyAndSendConfirmation(visitor, publicKeyRegistrar, visit.getScanTime(), visitor.getAndRemoveToken(today), stringToHash(visit.getH()));
                Visualiser visualiser = new Visualiser(signedConfirmation.get(0));

            } catch (Exception e) { e.printStackTrace(); }
            /*********************************** 2. VISITING A FACILITY *************************************/
        });

        writeToFileButton.addActionListener(a -> {
            /******************************** 3. REGISTERING INFECTED USER **********************************/
            visitor.exportVisits();
            /******************************** 3. REGISTERING INFECTED USER **********************************/
        });


        frame.setLayout(new FlowLayout());
        frame.add(enrollButton);
        frame.add(visitButton);
        frame.add(writeToFileButton);
        frame.setSize(250, 100);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

}
