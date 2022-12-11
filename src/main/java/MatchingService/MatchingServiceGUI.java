package MatchingService;

import Interfaces.MatchingServiceInterface;
import Interfaces.MixingProxyInterface;
import Interfaces.RegistrarInterface;
import MixingProxy.Entry;
import Services.MultiLineCellRenderer;
import Visitor.Visit;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.security.PublicKey;
import java.time.LocalDate;
import java.util.*;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static Services.Methods.*;

public class MatchingServiceGUI extends UnicastRemoteObject implements MatchingServiceInterface {
    private MatchingServiceDB matchingServiceDB;
    private JFrame frame;
    private JPanel mainPanel;
    private JScrollPane scroll;
    private JPanel facilityNymPanel; //Nym's from all facilities from the registrar
    private JButton nymJButton;
    private JButton generateButton;
    private JButton informButton;
    private JPanel entriesPanel; //Entries (Critical/informed -> booleans)
    private JPanel queuePanel; //Zelfde queue als mixingproxy
    private JPanel userLogsPanel; //Logs van infectedUser
    private Map<String, Visit> capsuleMap; // key = token, data: is Visit
    private Map<String, String[]> timeStamps; // key = token, data: array van timestamps

    private List<Visit> userLogs;
    private Map<LocalDate, byte[]> facilityNyms;
    private List<Entry> allEntries;

    public MatchingServiceGUI(MatchingServiceDB matchingServiceDB) throws RemoteException, NotBoundException {
        this.matchingServiceDB = matchingServiceDB;

        frame = new JFrame("MatchingService");
        mainPanel = new JPanel();
        scroll = new JScrollPane();
        facilityNymPanel = new JPanel();
        nymJButton = new JButton("<html>Get nyms<br />+ verify logs<br />+ mark infected</html>");
        generateButton = new JButton("Generate entries");
        informButton = new JButton("Inform left over visitors");
        entriesPanel = new JPanel();
        queuePanel = new JPanel();
        userLogsPanel = new JPanel();

        capsuleMap = new HashMap<>(); // key = token, data: is Visit
        timeStamps = new HashMap<>(); // key = token, data: array van timestamps

        userLogs = new ArrayList<>();
        facilityNyms = new HashMap<>();
        allEntries = new ArrayList<>();

        generateButton.addActionListener(a -> {
            matchingServiceDB.generateEntries();
            updateFrame();
        });

        nymJButton.addActionListener(a-> {
            /******************************** 3. REGISTERING INFECTED USER **********************************/
            // Try to connect to a different server ourselves
            try {
                RegistrarInterface impl = connectToRegistrar();
                // Download all nyms from the registrar matching the given CF's
                matchingServiceDB.addNym(impl.getAllNym(matchingServiceDB.getCFFromSignedLogs()));
                matchingServiceDB.verifyLogs();
                matchingServiceDB.markInfectedCapsules();
                matchingServiceDB.markInfectedTokens();
                updateFrame();

            } catch (Exception e) { e.printStackTrace(); }
            /******************************** 3. REGISTERING INFECTED USER **********************************/
        });

        informButton.addActionListener(a-> {
            /**************************** 4. INFORMING POSSIBLY INFECTED USERS ******************************/
            try {transferNonInformed();}
            catch (Exception e) { e.printStackTrace(); }
            /**************************** 4. INFORMING POSSIBLY INFECTED USERS ******************************/
        });

        updateFrame();
    }

    public void updateFrame(){
        JPanel buttons = new JPanel();
        buttons.setLayout(new BoxLayout(buttons, BoxLayout.PAGE_AXIS));
        buttons.add(generateButton);
        buttons.add(nymJButton);
        buttons.add(informButton);


        capsuleMap = matchingServiceDB.getCapsuleMap(); // key = token, data: is Visit
        timeStamps = matchingServiceDB.getTimeStamps(); // key = token, data: array van timestamps
        userLogs = matchingServiceDB.getUserLogs();
        facilityNyms = matchingServiceDB.getFacilityNyms();
        allEntries = matchingServiceDB.getAllEntries();

        frame.remove(scroll);
        mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel,BoxLayout.LINE_AXIS));
        mainPanel.add(buttons);


        facilityNymPanel = new JPanel();
        facilityNymPanel.setLayout(new BorderLayout());
        facilityNymPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Facilities pseudonyms", TitledBorder.LEFT,
                TitledBorder.TOP));
        DefaultTableModel dmfacilityNyms = new DefaultTableModel() {
            public Class getColumnClass(int columnIndex) {
                return String.class;
            }
        };
        String[] facilityNymColumns = {
                "Date",
                "Pseudonym"
        };
        String[][] facilityNymData = new String[facilityNyms.size()][2];
        int i = 0;
        for(var entry : facilityNyms.entrySet()){
            facilityNymData[i][0] = String.valueOf(entry.getKey());
            facilityNymData[i][1] = hashToString(entry.getValue());
            i++;
        }
        dmfacilityNyms.setDataVector(facilityNymData, facilityNymColumns);
        JTable facilityNymsTable = new JTable(dmfacilityNyms);
        facilityNymsTable.setRowHeight(150);
        facilityNymsTable.setDefaultRenderer(String.class, new MultiLineCellRenderer());
        JScrollPane facilityNymsTableScroll = new JScrollPane(facilityNymsTable);
        facilityNymPanel.add(facilityNymsTableScroll);

        entriesPanel = new JPanel();
        entriesPanel.setLayout(new BorderLayout());
        entriesPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Entries", TitledBorder.LEFT,
                TitledBorder.TOP));
        DefaultTableModel dmEntries = new DefaultTableModel() {
            public Class getColumnClass(int columnIndex) {
                return String.class;
            }
        };
        String[] entryColumns = {
                "Token",
                "Hash",
                "Critical",
                "Informed",
                "BeginTimeWindow",
                "EndTimeWindow"
        };

        String[][] entryData = new String[allEntries.size()][6];
        i = 0;
        for(Entry e : allEntries){
            entryData[i][0] = hashToString(e.getToken());
            entryData[i][1] = hashToString(e.getHash());
            entryData[i][2] = String.valueOf(e.isCritical());
            entryData[i][3] = String.valueOf(e.isInformed());
            entryData[i][4] = timeStampToString(e.getBeginTimeWindow());
            entryData[i][5] = timeStampToString(e.getEndTimeWindow());
            i++;
        }
        dmEntries.setDataVector(entryData, entryColumns);
        JTable entriesTable = new JTable(dmEntries);
        entriesTable.setRowHeight(150);
        entriesTable.setDefaultRenderer(String.class, new MultiLineCellRenderer());
        JScrollPane entriesTableScroll = new JScrollPane(entriesTable);
        entriesPanel.add(entriesTableScroll);




        queuePanel = new JPanel();
        queuePanel.setLayout(new BorderLayout());
        queuePanel.setBorder(BorderFactory.createTitledBorder(
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
        i = 0;
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
        queuePanel.add(queueScroll);

        userLogsPanel = new JPanel();
        userLogsPanel.setLayout(new BorderLayout());
        userLogsPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Infected UserLogs", TitledBorder.LEFT,
                TitledBorder.TOP));
        DefaultTableModel dmuserLogs = new DefaultTableModel() {
            public Class getColumnClass(int columnIndex) {
                return String.class;
            }
        };
        String[] userLogsColumns = {
                "userLogs"
        };
        String[][] userLogsData = new String[userLogs.size()][1];
        i = 0;
        for(Visit v : userLogs){
            userLogsData[i][0] = v.toGUIString();
            i++;
        }
        dmuserLogs.setDataVector(userLogsData, userLogsColumns);
        JTable userLogsTable = new JTable(dmuserLogs);
        userLogsTable.setRowHeight(150);
        userLogsTable.setDefaultRenderer(String.class, new MultiLineCellRenderer());
        JScrollPane userLogsTableScroll = new JScrollPane(userLogsTable);
        userLogsPanel.add(userLogsTableScroll);

        mainPanel.add(facilityNymPanel);
        mainPanel.add(entriesPanel);
        mainPanel.add(queuePanel);
        mainPanel.add(userLogsPanel);

        scroll = new JScrollPane(mainPanel);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        frame.add(scroll);

        frame.setSize(836,440);
        frame.setLocation(700,400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true); //Makes frame visible

    }

    @Override
    public boolean containsToken(byte[] token)  {
        return matchingServiceDB.hasCapsule(token);
    }
    @Override
    public void addCapsule(byte[] token, Visit capsule) throws RemoteException {
        matchingServiceDB.addCapsule(token, capsule);
        updateFrame();
    }
    @Override
    public void addCapsule(String token, Visit capsule) throws RemoteException {
        matchingServiceDB.addCapsule(token, capsule);
        updateFrame();
    }
    public Visit getCapsule(String token) throws RemoteException {
        return matchingServiceDB.getCapsule(token);
    }

    @Override
    public void clearDB(int INCUBATION_DAYS) throws RemoteException {
        int today = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
        matchingServiceDB.flushDB(today, INCUBATION_DAYS);
        updateFrame();
    }

    @Override
    public void addTimeStamps(String token, String[] timeStamp) {
        matchingServiceDB.addTimeStamps(token, timeStamp);
        updateFrame();
    }
    /******************************** 3. REGISTERING INFECTED USER **********************************/
    public void receiveSignedLogs(ArrayList<List<byte[]>> signedLogs, PublicKey publicKey) throws RemoteException {
        try{connectToMixingProxy().flushCache();}
        catch (Exception e) { e.printStackTrace(); }
        matchingServiceDB.addSignedLogs(signedLogs, publicKey);
        updateFrame();
    }
    /******************************** 3. REGISTERING INFECTED USER **********************************/


    /**************************** 4. INFORMING POSSIBLY INFECTED USERS ******************************/
    public List<Entry> getInfectedEntries() {
        return matchingServiceDB.getInfectedEntries();
    }

    @Override
    public void notifyReceived(String token) throws RemoteException {
        System.out.println("Matching service: Token ontvangen die ontvangst besmette entry bevestigd.");
        matchingServiceDB.notifyReceived(token);
        updateFrame();
    }

    @Override
    public void transferNonInformed() throws RemoteException {
        List<Entry> nonInformed = matchingServiceDB.getNonInformedEntries();
        if(nonInformed.isEmpty()) {
            System.out.println("Everyone was informed");
        }
        else {
            try {
                System.out.println("Transferred all non-informed tokens to the Mixing Proxy");
                connectToRegistrar().notifyNonInformed(nonInformed);
            } catch (Exception e) { e.printStackTrace(); }
        }
        updateFrame();
    }

    /**************************** 4. INFORMING POSSIBLY INFECTED USERS ******************************/
}

