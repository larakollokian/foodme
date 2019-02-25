package ca.mcgill.ecse428.foodme.security;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

import ca.mcgill.ecse428.foodme.service.AuthenticationException;

import java.security.SecureRandom;

import java.util.Base64;
import java.util.Base64.Encoder;
import java.util.Base64.Decoder;
import org.apache.commons.text.RandomStringGenerator;
import static org.apache.commons.text.CharacterPredicates.DIGITS;
import static org.apache.commons.text.CharacterPredicates.LETTERS;


public class Password {
    
    private static final int iterations = 200;
    private static final int saltLen = 8;
    private static final int desiredKeyLen = 126;
    
    private static final Encoder base64Encoder = Base64.getEncoder();
    private static final Decoder base64Decoder = Base64.getDecoder();

    /**
	 * get salted hash given a password
	 * @param password
	 * @return salt hash password
	 */

    public static String getSaltedHash(String password) throws Exception {
        byte[] salt = SecureRandom.getInstance("SHA1PRNG").generateSeed(saltLen);
        // store the salt with the password
        return base64Encoder.encodeToString(salt) + "$" + hash(password, salt);
    }
    /**
	 * Hashing password using PBKDF2
	 * @param password
	 * @param salt
	 * @return hashed password
	 */
    private static String hash(String password, byte[] salt) throws Exception {
        if (password == null || password.length() == 0)
            throw new IllegalArgumentException("Empty passwords are not supported.");
        SecretKeyFactory f = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        SecretKey key = f.generateSecret(new PBEKeySpec(
                password.toCharArray(), salt, iterations, desiredKeyLen)
        );
        return base64Encoder.encodeToString(key.getEncoded());
    }
    /**
     * checks if password entered by user corresponds to the salt hash 
	 * @param password
	 * @param storedHash
     * @throws Exception when the password is not equal to the storedHash
	 */
    public static boolean check(String password, String storedHash) throws Exception {
        System.out.println(password);
        System.out.println(storedHash);
        String[] saltAndPass = storedHash.split("\\$");
        if (saltAndPass.length != 2) {
            throw new IllegalStateException("The stored password hash is formatted incorrectly.");
        }
        String hashOfInput = hash(password, base64Decoder.decode(saltAndPass[0]));
        System.out.println(hashOfInput);
        System.out.println(saltAndPass[1]);
        if(!hashOfInput.equals(saltAndPass[1])){
            throw new AuthenticationException("Invalid Password");
        }
        return true;
    }
    /**
     * Generate a random password 
	 * @param n
     * @return n character long random password 
	 */
    public static String generateRandomPassword(int n) {
    	RandomStringGenerator generator = new RandomStringGenerator.Builder()
    	        .withinRange('0', 'z')
    	        .filteredBy(LETTERS, DIGITS)
    	        .build();
    	String randPassword = generator.generate(n);
    	
    	return randPassword;
    }

}