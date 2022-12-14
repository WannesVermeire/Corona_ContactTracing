package MixingProxy;

import Interfaces.MatchingServiceInterface;
import Interfaces.MixingProxyInterface;
import Interfaces.RegistrarInterface;
import Services.MultiLineCellRenderer;
import Visitor.Visit;
import org.springframework.cglib.proxy.Mixin;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;

import java.awt.*;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.security.PublicKey;
import java.security.SignatureException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static Services.Methods.*;
import static Services.Methods.stringToDate;


public class MixingProxyGUI extends UnicastRemoteObject implements MixingProxyInterface{

    //Interface shows queue at each time
    JFrame frame;
    JButton flushButton; //flushes the queue
    JPanel queue;
    private MixingProxyDB mixingProxyDB;
    private MatchingServiceInterface impl;
    private Map<String, Visit> capsuleMap; // key = token, data: is Visit
    private Map<String, String[]> timeStamps; // key = token, data: array van timestamps

    public MixingProxyGUI() throws Exception {

        this.impl = connectToMatchingService();
        this.capsuleMap = new HashMap<>();
        this.timeStamps = new HashMap<>();
        this.mixingProxyDB = new MixingProxyDB();


        frame = new JFrame("Mixing Proxy");
        flushButton = new JButton("Flush");
        queue = new JPanel();
        flushButton.addActionListener(a -> {
            try {flushCache();} //Flush the queue
            catch (RemoteException e) {throw new RuntimeException(e);}
        });
        updateFrame();
    }

    public void updateFrame(){
        this.capsuleMap = mixingProxyDB.getCapsuleMap();
        this.timeStamps = mixingProxyDB.getTimeStamps();

        frame.remove(queue);
        queue = new JPanel();
        queue.setLayout(new BorderLayout());
        queue.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Queue", TitledBorder.LEFT,
                TitledBorder.TOP));
        DefaultTableModel dmQueue = new DefaultTableModel() {
            public Class getColumnClass(int columnIndex) {
                return String.class;
            }
        };
        String[] queueColumns = {
                "Position",
                "Key",
                "Capsule"
        };
        String[][] queueData = new String[capsuleMap.size()][3];
        int i = 0;
        for(var entry : capsuleMap.entrySet()){
            queueData[i][0] = String.valueOf(i);
            queueData[i][1] = entry.getKey();
            String[] timeStampsValue = timeStamps.get(entry.getKey());
            queueData[i][2] = entry.getValue().toGUIString() + Arrays.toString(timeStampsValue);
            i++;
        }
        dmQueue.setDataVector(queueData, queueColumns);
        JTable queueTable = new JTable(dmQueue);
        queueTable.setRowHeight(150);
        queueTable.setDefaultRenderer(String.class, new MultiLineCellRenderer());
        JScrollPane queueScroll = new JScrollPane(queueTable);
        queue.add(queueScroll);

        frame.getContentPane().setLayout(new BoxLayout(frame.getContentPane(),BoxLayout.PAGE_AXIS));
        frame.add(queue);
        frame.add(flushButton);
        flushButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        frame.setSize(700,440);
        frame.setLocation(0,400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true); //Makes frame visible
    }

    // If all checks on the capsule data are correct we return a confirmation: sign(token)
    @Override
    public ArrayList<byte[]> verifyAndSendConfirmation(Visit visit, PublicKey publicKey) throws Exception, RemoteException {
        // 3 checks: signature, day, not yet used
        byte[] token = visit.getTokenPair().get(0);

        if(!checkSignature(visit.getTokenPair(), publicKey))
            throw new SignatureException("Invalid signature");

        if(!verifyTokenDay(visit.getScanTime(), token))
            throw new Exception("Date of token is not correct");

        if(impl.containsToken(token) || mixingProxyDB.containsCapsule(token))
            throw new Exception("Token is already used");

        System.out.println("Mixing Proxy: Capsule is valid!");

        // Save capsule
        mixingProxyDB.addCapsule(hashToString(token), visit);

        // Create confirmation
        updateTimeStamp(hashToString(token), visit.getScanTime());

        // Save this capsule
        mixingProxyDB.addCapsule(visit);

        updateFrame();

        return getSignature(stringToHash(visit.getH()), mixingProxyDB.getSecretKey());
    }

    private boolean verifyTokenDay(String scanTime, byte[] bytes) {
        int currentDay = stringToTimeStamp(scanTime).getDayOfMonth();
        String tokenDay_String = separateString(bytesToString(bytes))[1];
        int tokenDay = stringToDate(tokenDay_String).getDayOfMonth();
        return currentDay == tokenDay;
    }

    @Override
    public PublicKey getPublicKey() {
        return mixingProxyDB.getPublicKey();
    }


    // Stuur alles door naar de MatchingService
    @Override
    public void flushCache() throws RemoteException {
        System.out.println("Begin flushen");
        // Add 5 hour duration to capsules without leaving time
        for (var entry : timeStamps.entrySet()) {
            String[] array = entry.getValue();
            if (array.length==1) {
                LocalDateTime time = LocalDateTime.now().plusHours(5);
                String timeString = timeStampToString(time);
                String[] newArray = new String[2];
                newArray[0] = array[0];
                newArray[1] = timeString;
                entry.setValue(newArray);
            }
        }
        // Shuffle and flush
        while(!mixingProxyDB.isEmptyCapsules()) {
            String randomToken = mixingProxyDB.getRandomTokenCapsule();
            impl.addCapsule(randomToken, mixingProxyDB.getCapsule(randomToken));
            mixingProxyDB.removeToken(randomToken);
        }
        while(!mixingProxyDB.isEmptyTimeStamps()) {
            String randomToken = mixingProxyDB.getRandomTokenTime();
            impl.addTimeStamps(randomToken, mixingProxyDB.getTimeStamp(randomToken));
            mixingProxyDB.removeTimeStamp(randomToken);
        }
        updateFrame();
    }


    @Override
    public void updateTimeStamp(String token, String timeStamp) throws RemoteException {
        mixingProxyDB.updateTimeStamp(token, timeStamp);
        updateFrame();
    }


}
