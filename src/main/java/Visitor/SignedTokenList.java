package Visitor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SignedTokenList implements Serializable {
    private List<byte[]>[] signatures;
    private List<byte[]>[] tokens;

    public SignedTokenList(List<byte[]>[] signedTokens, List<byte[]>[] unsignedTokens) {
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

    @Override
    public String toString() {
        return "SignedTokenList{" +
                "signatures=" + Arrays.toString(signatures) +
                ", tokens=" + Arrays.toString(tokens) +
                '}';
    }
}
