package com.zkorum.capacitor.securesigning;

import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.UnrecoverableEntryException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.spec.ECGenParameterSpec;
import java.util.Base64;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERBitString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.crypto.signers.PlainDSAEncoding;
import org.bouncycastle.math.ec.custom.sec.SecP256R1Curve;

public class SecureSigning {

    private static final String ANDROID_KEY_STORE = "AndroidKeyStore";

    public KeyPair generateKeyPair(String prefixedKey) throws SecureSigningException {
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
                    .build()
            );

            return kpg.generateKeyPair();
        } catch (NoSuchAlgorithmException | NoSuchProviderException | InvalidAlgorithmParameterException e) {
            e.printStackTrace();
            throw new SecureSigningException(SecureSigningException.ErrorKind.keyGenerationError, e);
        }
    }

    public KeyStore getKeyStore() throws SecureSigningException {
        KeyStore keyStore = null;
        try {
            keyStore = KeyStore.getInstance(ANDROID_KEY_STORE);
        } catch (KeyStoreException e) {
            e.printStackTrace();
            throw new SecureSigningException(SecureSigningException.ErrorKind.keystoreError, e);
        }
        try {
            keyStore.load(null);
        } catch (CertificateException | IOException | NoSuchAlgorithmException e) {
            e.printStackTrace();
            throw new SecureSigningException(SecureSigningException.ErrorKind.keystoreError, e);
        }
        return keyStore;
    }

    // This is inspired by:
    // - https://gist.github.com/DinoChiesa/7520e1dea6e79888acab8ea8206afe92
    // - https://stackoverflow.com/a/72415832
    // -
    // https://www.tabnine.com/code/java/classes/org.bouncycastle.math.ec.custom.sec.SecP256R1Curve?snippet=5ce6bbffe594670004db5c23
    private byte[] toP1363(byte[] asn1EncodedSignature) {
        ASN1Sequence seq = ASN1Sequence.getInstance(asn1EncodedSignature);
        BigInteger r = ((ASN1Integer) seq.getObjectAt(0)).getValue();
        BigInteger s = ((ASN1Integer) seq.getObjectAt(1)).getValue();
        BigInteger n = new SecP256R1Curve().getOrder();
        return PlainDSAEncoding.INSTANCE.encode(n, r, s);
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
            // creates DER/ASN1-encoded signature
            // (plain P1363 output is not available with AndroidKeyStore as Provider)
            Signature s = Signature.getInstance("SHA256withECDSA");
            try {
                s.initSign(((KeyStore.PrivateKeyEntry) entry).getPrivateKey());
                s.update(decodedData);
                byte[] derSignature = s.sign();
                byte[] p1363Signature = toP1363(derSignature);
                return new String(Base64.getEncoder().encode(p1363Signature), StandardCharsets.UTF_8);
            } catch (InvalidKeyException | SignatureException e) {
                e.printStackTrace();
                throw new SecureSigningException(SecureSigningException.ErrorKind.invalidData, e);
            }
        } catch (KeyStoreException | NoSuchAlgorithmException | UnrecoverableEntryException e) {
            e.printStackTrace();
            throw new SecureSigningException(SecureSigningException.ErrorKind.keystoreError, e);
        }
    }

    public boolean doesKeyPairExist(String prefixedKey) throws SecureSigningException {
        KeyStore ks = this.getKeyStore();
        KeyStore.Entry entry = null;
        try {
            entry = ks.getEntry(prefixedKey, null);
            return entry != null;
        } catch (KeyStoreException | NoSuchAlgorithmException | UnrecoverableEntryException e) {
            e.printStackTrace();
            throw new SecureSigningException(SecureSigningException.ErrorKind.keystoreError, e);
        }
    }

    public KeyPair createKeyPairIfDoesNotExist(String prefixedKey) throws SecureSigningException {
        KeyStore ks = this.getKeyStore();
        KeyStore.Entry entry = null;
        try {
            entry = ks.getEntry(prefixedKey, null);
            if (entry == null) {
                return this.generateKeyPair(prefixedKey);
            }
            if (!(entry instanceof KeyStore.PrivateKeyEntry)) {
                throw new SecureSigningException(SecureSigningException.ErrorKind.missingKey);
            }
            PrivateKey privateKey = ((KeyStore.PrivateKeyEntry) entry).getPrivateKey();
            Certificate cert = ((KeyStore.PrivateKeyEntry) entry).getCertificate();
            PublicKey publicKey = cert.getPublicKey();
            return new KeyPair(publicKey, privateKey);
        } catch (KeyStoreException | NoSuchAlgorithmException | UnrecoverableEntryException e) {
            e.printStackTrace();
            throw new SecureSigningException(SecureSigningException.ErrorKind.keystoreError, e);
        }
    }

    public byte[] ecFromPubKey(PublicKey pubKey) {
        // Extract the EC public key (65 bytes) from the DER encoded public key (91
        // bytes)
        // @see
        // https://stackoverflow.com/questions/57209127/what-should-the-length-of-public-key-on-ecdh-be
        byte[] derEncodedPublicKey = pubKey.getEncoded();
        ASN1Sequence sequence = DERSequence.getInstance(derEncodedPublicKey);
        DERBitString ecPublicKey = (DERBitString) sequence.getObjectAt(1);
        byte[] ecPublicKeyBytes = ecPublicKey.getBytes();
        return ecPublicKeyBytes;
    }

    public DeleteStatus deleteKeyPair(String prefixedKey) throws SecureSigningException {
        KeyStore ks = this.getKeyStore();
        try {
            if (ks.containsAlias(prefixedKey)) {
                ks.deleteEntry(prefixedKey);
                return DeleteStatus.DELETED;
            } else {
                return DeleteStatus.NOT_FOUND;
            }
        } catch (KeyStoreException e) {
            e.printStackTrace();
            throw new SecureSigningException(SecureSigningException.ErrorKind.keystoreError, e);
        }
    }

    public KeyPair getKeyPair(String prefixedKey) throws SecureSigningException {
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
            PrivateKey privateKey = ((KeyStore.PrivateKeyEntry) entry).getPrivateKey();
            Certificate cert = ((KeyStore.PrivateKeyEntry) entry).getCertificate();
            PublicKey publicKey = cert.getPublicKey();
            return new KeyPair(publicKey, privateKey);
        } catch (KeyStoreException | NoSuchAlgorithmException | UnrecoverableEntryException e) {
            e.printStackTrace();
            throw new SecureSigningException(SecureSigningException.ErrorKind.keystoreError, e);
        }
    }
}
