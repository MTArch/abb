package in.gov.abdm.abha.enrollment.utilities;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;

@Component
@Slf4j
public class EnrolmentCipher {

	private static final String ALGO = "AES/CBC/PKCS5Padding";
	
	@Value("${cipher.secretKey}")
	private String secretKey;

	private IvParameterSpec ivParameterSpec;
	private SecretKeySpec secretKeySpec;

	public String decrypt(String strToDecrypt) {
		return decrypt(strToDecrypt, secretKey);
	}

	public String decrypt(String strToDecrypt, String secret) {
		try {
			initialize(secret);
			Cipher cipher = Cipher.getInstance(ALGO);
			cipher.init(2, secretKeySpec, ivParameterSpec);
			byte[] original = cipher.doFinal(Base64.getDecoder().decode(strToDecrypt));

			return new String(original);
		} catch (Exception exp) {
			log.error("Error while decrypting", exp);
		}
		return null;
	}

	public String encrypt(String strToEncrypt) {
		return encrypt(strToEncrypt, secretKey);
	}

	public String encrypt(String strToEncrypt, String secret) {
		try {
			initialize(secret);
			Cipher cipher = Cipher.getInstance(ALGO);
			cipher.init(1, secretKeySpec, ivParameterSpec);
			return Base64.getEncoder().encodeToString(cipher.doFinal(strToEncrypt.getBytes(StandardCharsets.UTF_8)));
		} catch (Exception exp) {
			log.error("Error while encrypting", exp);
		}
		return null;
	}

	private void initialize(String myKey) {
		MessageDigest sha = null;
		try {
			byte[] key = myKey.getBytes(StandardCharsets.UTF_8);
			sha = MessageDigest.getInstance("SHA-256");
			key = sha.digest(key);
			key = Arrays.copyOf(key, 16);
			secretKeySpec = new SecretKeySpec(key, "AES");
			ivParameterSpec = new IvParameterSpec(key);
		} catch (NoSuchAlgorithmException exp) {
			log.error("No such algo found exception.", exp);
		}
	}
}