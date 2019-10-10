package general;

import org.bouncycastle.crypto.digests.SHA256Digest;
import org.bouncycastle.crypto.generators.PKCS5S2ParametersGenerator;
import org.bouncycastle.crypto.params.KeyParameter;

import javax.xml.bind.DatatypeConverter;
import java.security.SecureRandom;

public class PBKDF2WithHmacSHA256 {

    private static SecureRandom random = new SecureRandom();

    public static String hash(String password, String salt, int iterations, int derivedKeyLength)  {
        try {
            PKCS5S2ParametersGenerator gen = new PKCS5S2ParametersGenerator(new SHA256Digest());
            gen.init(password.getBytes("UTF-8"), salt.getBytes("UTF-8"), iterations);
            byte[] dk = ((KeyParameter) gen.generateDerivedParameters(derivedKeyLength*8)).getKey();

            return DatatypeConverter.printHexBinary(dk);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public static String createSalt(int bytes) {
        byte[] salt = new byte[bytes];
        random.nextBytes(salt);
        return DatatypeConverter.printHexBinary(salt);
    }

}
