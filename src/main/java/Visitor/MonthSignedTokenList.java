package Visitor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MonthSignedTokenList implements Serializable {
    private List<byte[]>[] signatures;
    private List<byte[]>[] tokens;

    public MonthSignedTokenList(List<byte[]>[] signedTokens, List<byte[]>[] unsignedTokens) {
        this.signatures = signedTokens;
        this.tokens = unsignedTokens;
    }

    // returns a list with size 2: {unsignedData, signedData}
    public ArrayList<byte[]> getAndRemoveSignatureToken(int today) {
        ArrayList<byte[]> signaturePair = new ArrayList<>();

        signaturePair.add(tokens[today-1].get(0));
        tokens[today-1].remove(0);

        signaturePair.add(signatures[today-1].get(0));
        signatures[today-1].remove(0);

        return signaturePair;
    }

    public boolean containsToken(byte[] token) {
        for (List<byte[]> list : tokens) {
            for (byte[] t : list) {
                if (Arrays.equals(t, token)) return true;
            }
        }
        return false;
    }
    @Override
    public String toString() {
        String sign = "null";
        if (signatures!=null) {
            sign = "List of "+signatures.length+" smaller lists, containing "+signatures[0].size()+" individual signatures";
        }
        String token = "null";
        if (signatures!=null) {
            token = "List of "+tokens.length+" smaller lists, containing "+tokens[0].size()+" individual tokens";
        }
        return "SignedTokenList{" +
                "tokens=" + token +
                ", signatures=" + sign +
                '}';
    }
}
