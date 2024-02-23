package de.mcc.grpc.auth;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;

public class AuthEncoder {
    /**
     * Generate the auth string that needs to be sent with every request
     * @param email: Your E-Mail Adress in the ISIS Course
     * @param matrikelNr: The MatrikelNr you entered in the ISIS Questionnaire
     * @return a String that needs to be set as authString in every request
     */
    public static String generateAuthString(String email, String matrikelNr) {
        matrikelNr = matrikelNr.replaceFirst("^0+(?!$)", "");
        return Hex.encodeHexString(DigestUtils.sha256(email + matrikelNr));
    }
}
