package ru.kubsu.borshchevyk.auth.application.port.out;

public interface SignatureVerifierPort {
    boolean verifySignature(String challenge, String signature, String publicKey);
}
