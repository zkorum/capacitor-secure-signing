package com.zkorum.capacitor.securesigning;

import android.provider.Settings;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import com.getcapacitor.JSObject;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Signature;
import java.security.SignatureException;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;
import java.security.spec.ECGenParameterSpec;
import java.util.Base64;

public class SecureSigning {

    private static final String ANDROID_KEY_STORE = "AndroidKeyStore";

    public KeyPair generateKeypair(String prefixedKey) throws SecureSigningException {
        /*
         * Generate a new EC key pair entry in the Android Keystore by
         * using the KeyPairGenerator API. The private key can only be
         * used for signing or verification and only with SHA-256 or
         * SHA-512 as the message digest.
         */
        KeyPairGenerator kpg = null;
        try {
            kpg = KeyPairGenerator.getInstance(KeyProperties.KEY_ALGORITHM_EC, ANDROID_KEY_STORE);
            kpg.initialize(
                new KeyGenParameterSpec.Builder(prefixedKey, KeyProperties.PURPOSE_SIGN | KeyProperties.PURPOSE_VERIFY)
                    .setAlgorithmParameterSpec(new ECGenParameterSpec("secp256r1"))
                    .setDigests(KeyProperties.DIGEST_SHA256, KeyProperties.DIGEST_SHA512)
                    .setUserAuthenticationRequired(true)
                    .setUserAuthenticationParameters(15 * 60, KeyProperties.AUTH_BIOMETRIC_STRONG | KeyProperties.AUTH_DEVICE_CREDENTIAL)
                    .build()
            );

            return kpg.generateKeyPair();
        } catch (NoSuchAlgorithmException | NoSuchProviderException | InvalidAlgorithmParameterException e) {
            throw new SecureSigningException(SecureSigningException.ErrorKind.keyGenerationError, e);
        }
    }

    public KeyStore getKeyStore() throws SecureSigningException {
        KeyStore keyStore = null;
        try {
            keyStore = KeyStore.getInstance(ANDROID_KEY_STORE);
        } catch (KeyStoreException e) {
            throw new SecureSigningException(SecureSigningException.ErrorKind.keystoreError, e);
        }
        try {
            keyStore.load(null);
        } catch (CertificateException | IOException | NoSuchAlgorithmException e) {
            throw new SecureSigningException(SecureSigningException.ErrorKind.keystoreError, e);
        }
        return keyStore;
    }

    public String sign(String prefixedKey, byte[] decodedData) throws SecureSigningException {
        KeyStore ks = this.getKeyStore();
        KeyStore.Entry entry = null;
        try {
            entry = ks.getEntry(prefixedKey, null);
            if (entry == null) {
                throw new SecureSigningException(SecureSigningException.ErrorKind.missingKey);
            }
            if (!(entry instanceof KeyStore.PrivateKeyEntry)) {
                throw new SecureSigningException(SecureSigningException.ErrorKind.missingKey);
            }
            Signature s = Signature.getInstance("SHA256withECDSA");
            try {
                s.initSign(((KeyStore.PrivateKeyEntry) entry).getPrivateKey());
                s.update(decodedData);
                byte[] signature = s.sign();
                return Base64.getEncoder().encodeToString(signature);
            } catch (InvalidKeyException | SignatureException e) {
                throw new SecureSigningException(SecureSigningException.ErrorKind.invalidData);
            }
        } catch (KeyStoreException | NoSuchAlgorithmException | UnrecoverableEntryException e) {
            throw new SecureSigningException(SecureSigningException.ErrorKind.keystoreError, e);
        }
    }
}
