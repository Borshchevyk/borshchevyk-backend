package ru.kubsu.borshchevyk.auth.application.port.out;

/**
 * Port for verifying cryptographic signatures.
 */
public interface SignatureVerifierPort {
    /**
     * Verifies that the given signature is valid for the provided challenge and public key.
     *
     * @param challenge the challenge that was signed
     * @param signature the signature to verify
     * @param publicKey the public key to use for verification
     * @return true if the signature is valid, false otherwise
     */
    void verifySignature(String challenge, String signature, String publicKey);
}
