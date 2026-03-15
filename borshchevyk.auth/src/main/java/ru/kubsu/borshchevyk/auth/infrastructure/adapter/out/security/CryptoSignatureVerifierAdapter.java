package ru.kubsu.borshchevyk.auth.infrastructure.adapter.out.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.kubsu.borshchevyk.auth.application.port.out.SignatureVerifierPort;

import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Component
@Slf4j
public class CryptoSignatureVerifierAdapter implements SignatureVerifierPort {

    private static final String KEY_ALGORITHM = "RSA";
    private static final String SIGNATURE_ALGORITHM = "SHA256withRSA";

    @Override
    public boolean verifySignature(String challenge, String signature, String publicKeyBase64) {
        try {
            byte[] publicKeyBytes = Base64.getDecoder().decode(publicKeyBase64);
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicKeyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
            PublicKey publicKey = keyFactory.generatePublic(keySpec);

            Signature sig = Signature.getInstance(SIGNATURE_ALGORITHM);
            sig.initVerify(publicKey);
            sig.update(challenge.getBytes());

            byte[] signatureBytes = Base64.getDecoder().decode(signature);
            return sig.verify(signatureBytes);
        } catch (Exception e) {
            log.error("Failed to verify signature", e);
            return false;
        }
    }
}
