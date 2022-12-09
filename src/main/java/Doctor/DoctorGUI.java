package Doctor;

import Interfaces.MatchingServiceInterface;

import javax.swing.*;
import java.awt.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;


public class DoctorGUI {
    private JFrame frame;
    private JButton readLogsButton;
    private JButton sendLogsButton;
    private Doctor doctor;

    public DoctorGUI(Doctor doctor) {
        this.doctor = doctor;

        frame = new JFrame("Doctor");
        readLogsButton = new JButton("Read logs from file");
        sendLogsButton = new JButton("Send signed logs to the server");

        /******************************** 3. REGISTERING INFECTED USER **********************************/

        readLogsButton.addActionListener(a -> doctor.importVisits());

        sendLogsButton.addActionListener(a -> {
            try {
                // fire to localhost port 2300
                Registry myRegistry = LocateRegistry.getRegistry("localhost", 2300);
                // search for RegistrarService
                MatchingServiceInterface impl = (MatchingServiceInterface) myRegistry.lookup("MatchingService");

                // Send signedLogs to the server
                doctor.generateSignedLogs();
                impl.receiveSignedLogs(doctor.getSignedLogs(), doctor.getPublicKey());

            } catch (Exception e) { e.printStackTrace(); }
        });

        /******************************** 3. REGISTERING INFECTED USER **********************************/

        frame.setLayout(new FlowLayout());
        frame.add(readLogsButton);
        frame.add(sendLogsButton);
        frame.setSize(250, 100);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}
