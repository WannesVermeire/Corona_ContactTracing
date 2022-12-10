package Registrar;


import Facility.Facility;
import Interfaces.RegistrarInterface;
import Visitor.MonthSignedTokenList;
import Visitor.Visitor;

import static Services.Methods.*;
import javax.crypto.SecretKey;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.security.*;
import java.time.LocalDate;
import java.util.*;

//Interface which facilities and visitors can use to enroll in the registrar
public class RegistrarInterfaceImpl extends UnicastRemoteObject implements RegistrarInterface {
    private final int INCUBATION_DAYS = 10;
    private SecretKey s;
    private RegistrarDB registrarDB;
    private RegistrarGUI registrarGUI;

    public RegistrarInterfaceImpl(SecretKey s, RegistrarDB registrarDB) throws RemoteException {
        this.s = s;
        this.registrarDB = registrarDB;
        this.registrarGUI = new RegistrarGUI(registrarDB);
    }

    public int getINCUBATION_DAYS() { return INCUBATION_DAYS; }

    @Override
    public PublicKey getPublicKey() throws RemoteException {
        return registrarDB.getPublicKey();
    }

    @Override
    public String[] getAllFacilityNames() throws RemoteException {
        return registrarDB.getAllFacilityNames();
    }

    /************************************* 1.1 FACILITY ENROLLMENT *************************************/
    // Verify signature on CF and add to database if correct
    public void registerFacility(String CF, ArrayList<byte[]> signaturePair, PublicKey publicKey) {
        Facility facility;
        boolean valid = checkSignature(signaturePair, publicKey);
        if (valid) {
            String[] CFcontent = separateString(CF);
            facility = new Facility(CFcontent[0], CFcontent[1], CFcontent[2], CFcontent[3], publicKey);
            registrarDB.addFacility(facility);
            System.out.println("Registrar added tot database: "+facility);

            registrarGUI.updateFacilities();
        }
        else {
            System.out.println("The provided CF was not from a reliable source.");
        }
    }

    // Generate secret key s_CF_dayi for each day of this month
    public List<SecretKey> getKeyArray(String id)  {
        List<SecretKey> keyArray = new ArrayList<>();
        Facility facility = registrarDB.findFacilityById(id);

        // Get the first day of the month and nrOfDays of month
        LocalDate today = LocalDate.now();
        int nrOfDays = today.getMonth().length(LocalDate.EPOCH.isLeapYear());
        LocalDate day = today.withDayOfMonth(1);

        String key = s.toString();
        String CF = facility.getCF();

        for (int i=0; i<nrOfDays; i++) {
            String dayi = dateToString(day);
            String[] data = {key, CF, dayi};
            String dataString = joinStrings(data);
            SecretKey s_cf_day_i = KDF(dataString);

            keyArray.add(s_cf_day_i);
            day = day.plusDays(1);
        }
        facility.setKeyArray(keyArray);
        return keyArray;
    }

    // Generate day-specific pseudonym nym_CF_dayi for each day of this month
    public List<byte[]> getNymArray(String id) {
        List<byte[]> nymArray = new ArrayList<>();
        Facility facility = registrarDB.findFacilityById(id);
        List<SecretKey> keyArray = facility.getKeyArray();

        // Get the first day of the month and nrOfDays of month
        LocalDate today = LocalDate.now();
        int nrOfDays = today.getMonth().length(LocalDate.EPOCH.isLeapYear());
        LocalDate day = today.withDayOfMonth(1);

        String CF = facility.getCF();
        for (int i = 0; i < nrOfDays; i++) {
            String key = keyArray.get(i).toString();
            String dayi = dateToString(day);
            String[] data = {key, CF, dayi};
            String dataString = joinStrings(data);
            byte[] nym_cf_dayi = hash(dataString);

            nymArray.add(nym_cf_dayi);
            day = day.plusDays(1);
        }
        facility.setNymArray(nymArray);
        return nymArray;

    }
    /************************************* 1.1 FACILITY ENROLLMENT *************************************/


    /************************************* 1.2 USER ENROLLMENT *************************************/
    public boolean registerVisitor(String name, String phoneNr) {
        if (registrarDB.visitorExists(phoneNr) == false) {
            Visitor visitor = new Visitor(name, phoneNr);
            registrarDB.addVisitor(visitor);

            registrarGUI.updateVisitors();
            return true;
        }
        else {
            System.out.println("Visitor with phoneNr: "+phoneNr+" already existed.");
            return false;
        }
    }

    // Generates the tokens, adds them to the visitor in the registrarDB
    // Als returns them to the visitor, so he can save them locally as well
    public MonthSignedTokenList getSignedTokens(String phoneNr) {
        Visitor visitor = registrarDB.findVisitorByPhoneNr(phoneNr);
        MonthSignedTokenList monthSignedTokenList = null;

        try {
            SecureRandom random = SecureRandom.getInstance("SHA1PRNG", "SUN");
            PrivateKey key = registrarDB.getPrivate();
            List<byte[]>[] signedTokenList = new List[31];
            List<byte[]> signedTokenListPerDay;

            List<byte[]>[] unsignedTokenList = new List[31];
            List<byte[]> unsignedTokenListPerDay;

            LocalDate date = LocalDate.now();
            for(int j=0; j<31; j++) {
                signedTokenListPerDay = new ArrayList<>();
                unsignedTokenListPerDay = new ArrayList<>();
                for (int i = 0; i < 48; i++) {
                    String[] dataStrings = new String[]{Long.toString(random.nextLong()), dateToString(date)};
                    String dataString = joinStrings(dataStrings);
                    byte[] data = stringToBytes(dataString);
                    unsignedTokenListPerDay.add(data);
                    signedTokenListPerDay.add(getSignature(data, key).get(1));
                    date.plusDays(1);
                }
                signedTokenList[j] = signedTokenListPerDay;
                unsignedTokenList[j] = unsignedTokenListPerDay;


            }

            monthSignedTokenList = new MonthSignedTokenList(signedTokenList, unsignedTokenList);

        } catch (NoSuchAlgorithmException | NoSuchProviderException e) {e.printStackTrace();}

        visitor.setTokens(monthSignedTokenList);
        registrarGUI.updateVisitors();
        return monthSignedTokenList;
    }
    /************************************* 1.2 USER ENROLLMENT *************************************/


    /******************************** 3. REGISTERING INFECTED USER **********************************/
    public Map<LocalDate, byte[]> getAllNym(List<String> CFList) {
        return registrarDB.getAllNym(CFList);
    }
    /******************************** 3. REGISTERING INFECTED USER **********************************/
    @Override
    public String getTelNrUser(byte[] token) throws Exception {
        return registrarDB.getTelNrUser(token);
    }
}
