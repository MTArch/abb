package in.gov.abdm.abha.enrollment.utilities.rsa;

import in.gov.abdm.abha.enrollment.constants.StringConstants;
import in.gov.abdm.abha.enrollment.utilities.Common;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Slf4j
@Component
@NoArgsConstructor
public class RSAUtil {

    private static final String NO_SUCH_ALGORITHM_EXCEPTION_OCCURRED_DURING_GETTING_PUBLIC_KEY = "No such Algorithm exception occurred during getting public key:";
    private static final String INVALID_KEY_EXCEPTION_OCCURRED_DURING_GETTING_PUBLIC_KEY = "Invalid key exception occurred during getting public key:";
    private static final String NO_SUCH_ALGORITHM_EXCEPTION_OCCURRED_DURING_GETTING_PRIVATE_KEY = "No such Algorithm exception occurred during getting private key:";
    private static final String INVALID_KEY_EXCEPTION_OCCURRED_DURING_GETTING_PRIVATE_KEY = "Invalid key exception occurred during getting private key:";
    private static final String FAILED_TO_DECRYPT_DUE_TO = "Failed to decrypt, due to : ";
    private static final String FAILED_TO_ENCRYPT_DUE_TO = "Failed to encrypt, due to : ";
    private String privateKeyContent;
    private String publicKeyContent;

    @Value("${rsa.transformation.algorithm: RSA/ECB/OAEPWithSHA-1AndMGF1Padding}")
    private String rsaTransformationAlgorithm;

    @Value("${rsa.algorithm: RSA}")
    private String rsaAlgorithm;

    @Autowired
    RSAUtil(@Value("${rsa.private.key: nha_rsa_private_key.pem}") String rsaPrivateKeyFileName
            , @Value("${rsa.public.key: nha_rsa_public_key.pem}") String rsaPublicKeyFileName) {
        privateKeyContent = Common.loadFileData(rsaPrivateKeyFileName);
        publicKeyContent = Common.loadFileData(rsaPublicKeyFileName);
    }

    public String encrypt(String data) {
        try {
            Cipher cipher = Cipher.getInstance(rsaTransformationAlgorithm);
            cipher.init(Cipher.ENCRYPT_MODE, getPublicKey());
            return Base64.getEncoder().encodeToString(cipher.doFinal(data.getBytes()));
        } catch (InvalidKeyException | NoSuchPaddingException | BadPaddingException | NoSuchAlgorithmException |
                 IllegalBlockSizeException exception) {
            log.error(FAILED_TO_ENCRYPT_DUE_TO + data + StringConstants.COLON, exception);
        }
        return StringConstants.EMPTY;
    }

    public String decrypt(String data) {
        if (data.isEmpty()) {
            return StringConstants.EMPTY;
        } else {
            try {
                Cipher cipher = Cipher.getInstance(rsaTransformationAlgorithm);
                cipher.init(Cipher.DECRYPT_MODE, getPrivateKey());
                return new String(cipher.doFinal(Base64.getDecoder().decode(data.getBytes())));
            } catch (IllegalBlockSizeException | InvalidKeyException | BadPaddingException | NoSuchAlgorithmException |
                     NoSuchPaddingException exception) {
                log.error(FAILED_TO_DECRYPT_DUE_TO + data + StringConstants.COLON, exception.getMessage());
            }
        }
        return StringConstants.EMPTY;
    }

    private PublicKey getPublicKey() {
        try {
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(Base64.getDecoder().decode(publicKeyContent.getBytes()));
            KeyFactory keyFactory = KeyFactory.getInstance(rsaAlgorithm);
            return keyFactory.generatePublic(keySpec);
        } catch (NoSuchAlgorithmException e) {
            log.error(NO_SUCH_ALGORITHM_EXCEPTION_OCCURRED_DURING_GETTING_PUBLIC_KEY, e.getMessage());
        } catch (InvalidKeySpecException e) {
            log.error(INVALID_KEY_EXCEPTION_OCCURRED_DURING_GETTING_PUBLIC_KEY, e.getMessage());
        }
        return null;
    }

    private PrivateKey getPrivateKey() {
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(Base64.getDecoder().decode(privateKeyContent.getBytes()));
        try {
            return KeyFactory.getInstance(rsaAlgorithm).generatePrivate(keySpec);
        } catch (NoSuchAlgorithmException e) {
            log.error(NO_SUCH_ALGORITHM_EXCEPTION_OCCURRED_DURING_GETTING_PRIVATE_KEY, e);
        } catch (InvalidKeySpecException e) {
            log.error(INVALID_KEY_EXCEPTION_OCCURRED_DURING_GETTING_PRIVATE_KEY, e);
        }
        return null;
    }

    public PrivateKey getJWTPrivateKey() {
        return getPrivateKey();
    }

    public boolean isRSAEncrypted(String encryptedValue) {
        return !decrypt(encryptedValue).isEmpty();
    }
}
