package Doctor;

import Visitor.Visitor;
import Visitor.SignedTokenList;

import javax.crypto.SecretKey;
import java.security.KeyPair;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

import static Services.Methods.*;

public class Doctor {
    private String name;
    private KeyPair keyPair;

    public Doctor(String name) {
        this.name = name;
        this.keyPair = getKeyPair();
    }

    public ArrayList getSignedLogs(Visitor visitor){
        ArrayList signedLogs = new ArrayList();
        //1: Get logs from the infected visitor (every token is simultaneously added with visits -> same index for visits and tokens)
        ArrayList<byte[]>[] tokens = visitor.getUsedTokens();
        Map<String, String[]> logs  = visitor.getVisits();
        int i = 0;
        for (Map.Entry<String, String[]> entry : logs.entrySet()) {
            ArrayList<byte[]> token = tokens[i]; //Signed token Todo: is the data alone enough?
            String H = entry.getValue()[2];
            String R_i = entry.getValue()[0];

            //2: Get time intervals that were stored on the smarthphone
            String timeInterval = entry.getValue()[3];
            String[] data = new String[] {bytesToString(token.get(0)),H,R_i, timeInterval};
            String joinedData = joinStrings(data);

            //3: Sign the tokens + intervals
            ArrayList<byte[]> signature = getSignature(stringToBytes(joinedData),keyPair.getPrivate());

            signedLogs.add(signature);
            i++;
        }
        return signedLogs;
    }
}
