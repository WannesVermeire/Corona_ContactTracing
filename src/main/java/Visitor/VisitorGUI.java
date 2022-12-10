package Visitor;

import Interfaces.MatchingServiceInterface;
import Interfaces.RegistrarInterface;
import Interfaces.MixingProxyInterface;

import javax.swing.*;
import java.awt.*;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;

import static Services.Methods.*;

public class VisitorGUI extends JFrame {
    private JFrame frame;
    private JComboBox selectFacility;
    private JButton visitButton;
    private JButton writeToFileButton;
    private JButton checkInfectedButton;
    private JButton updateTimeStamp;
    private Visitor visitor;
    private int saveDuration = 14; // Days before we delete the capsules from visiting a facility
    private int incubation = 0;

    public VisitorGUI(Visitor visitor) throws RemoteException, NotBoundException {
        this.visitor = visitor;

        frame = new JFrame("Visitor - " + visitor.getName());

        visitButton = new JButton("Visit selected facility");
        checkInfectedButton = new JButton("Check if infected");
        writeToFileButton = new JButton("Write logs to file");
        updateTimeStamp = new JButton("Update TimeStamp");


        updateTimeStamp.addActionListener(a -> {
            ArrayList<byte[]> tokens  = visitor.getLastUsedToken();
            String timestamp = timeStampToString(LocalDateTime.now());
            visitor.updateTimeStamp(bytesToString(tokens.get(0)), timestamp);
            System.out.println("update timestamp: " + timestamp);
        });

        /************************************** 1.2 USER ENROLLMENT *************************************/
        try {
            RegistrarInterface registrar = connectToRegistrar();

            incubation = registrar.getINCUBATION_DAYS();

            // Register visitor to the registrar
            boolean registrationSuccessful = registrar.registerVisitor(visitor.getName(), visitor.getPhoneNr());
            if (registrationSuccessful) System.out.println("Visitor data after enrollment: " + visitor);
            else System.out.println("Something went wrong during enrollment of: " + visitor);

            // Get a set of signed tokens
            visitor.setTokens(registrar.getSignedTokens(visitor.getPhoneNr()));
            System.out.println("Visitor data after receiving tokens: " + visitor);


        } catch (Exception e) {
            e.printStackTrace();
        }
        /************************************* 1.2 USER ENROLLMENT **************************************/


        visitButton.addActionListener(a -> {
            /*********************************** 2. VISITING A FACILITY *************************************/
            try {
                if(selectFacility.getSelectedItem() != null) {

                    // visitor scans a QR code
                    System.out.println("select: " + (String) selectFacility.getSelectedItem());
                    Visit visit = visitor.scanQR((String) selectFacility.getSelectedItem()); // Also saves Visit in Visitor

                    MixingProxyInterface mpi = connectToMixingProxy();
                    RegistrarInterface registrar = connectToRegistrar();

                    // Create a capsule and send it to the MixingProxy to verify
                    // Capsule = timestamp, T_user_x_dayi, hash(Ri,num_CF_dayi) (hash uit de QR-code dus)
                    int today = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
                    ArrayList<byte[]> tokenPair = visitor.getAndRemoveToken(today);
                    visit.setTokenPair(tokenPair);

                    ArrayList<byte[]> signedConfirmation = mpi.verifyAndSendConfirmation(visit,  registrar.getPublicKey());
                    new Visualiser(signedConfirmation.get(0),visitor.getName());
                }

            } catch (Exception e) { e.printStackTrace(); }
            /*********************************** 2. VISITING A FACILITY *************************************/
        });

        writeToFileButton.addActionListener(a -> {
            /******************************** 3. REGISTERING INFECTED USER **********************************/
            visitor.exportVisits();
            /******************************** 3. REGISTERING INFECTED USER **********************************/
        });

        checkInfectedButton.addActionListener(a -> {
            /**************************** 4. INFORMING POSSIBLY INFECTED USERS ******************************/
            try {
                MatchingServiceInterface matchingService = connectToMatchingService();
                // Receive infected entries
                visitor.setInfectedEntries(matchingService.getInfectedEntries());
                visitor.checkIfInfected();


            } catch (Exception e) { e.printStackTrace(); }


            /**************************** 4. INFORMING POSSIBLY INFECTED USERS ******************************/
        });






        // fire to localhost port 2100
        Registry myRegistry = LocateRegistry.getRegistry("localhost", 2100);
        // search for RegistrarService
        RegistrarInterface impl = (RegistrarInterface) myRegistry.lookup("RegistrarService");

        selectFacility = new JComboBox(impl.getAllFacilityNames());

        frame.setLayout(new FlowLayout());
        frame.add(visitButton);
        frame.add(selectFacility);
        frame.add(writeToFileButton);
        frame.add(updateTimeStamp);
        frame.add(checkInfectedButton);
        frame.setSize(250, 200);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

}
