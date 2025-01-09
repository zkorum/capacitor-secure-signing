package com.zkorum.capacitor.securesigning;

import android.security.keystore.UserNotAuthenticatedException;
import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyPair;
import java.security.KeyStoreException;
import java.util.Base64;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERBitString;
import org.bouncycastle.asn1.DERSequence;

interface StorageOp {
    void run() throws SecureSigningException, GeneralSecurityException, IOException;
}

@CapacitorPlugin(name = "SecureSigning")
public class SecureSigningPlugin extends Plugin {

    // @Override
    // public void load() {
    // super.load();
    // implementation.init();
    // }

    private String getKeyParam(PluginCall call, String keyToGet) {
        String key = call.getString(keyToGet);

        if (key != null && !key.isEmpty()) {
            return key;
        }
        SecureSigningException.reject(call, SecureSigningException.ErrorKind.capacitorError);

        return null;
    }

    private final SecureSigning implementation = new SecureSigning();

    @PluginMethod
    public void generateKeyPair(PluginCall call) {
        String prefixedKey = this.getKeyParam(call, "prefixedKey");
        if (prefixedKey == null || prefixedKey.isEmpty()) {
            call.reject("prefixedKey is null or empty");
            return;
        }
        tryStorageOp(
            call,
            () -> {
                KeyPair kp = implementation.generateKeyPair(prefixedKey);
                byte[] ecPublicKeyBytes = implementation.ecFromPubKey(kp.getPublic());
                // Encode to Base64 and send
                String encodedPublicKey = new String(Base64.getEncoder().encode(ecPublicKeyBytes), StandardCharsets.UTF_8);
                JSObject ret = new JSObject();
                ret.put("publicKey", encodedPublicKey);
                call.resolve(ret);
            }
        );
    }

    @PluginMethod
    public void doesKeyPairExist(PluginCall call) {
        String prefixedKey = this.getKeyParam(call, "prefixedKey");
        if (prefixedKey == null || prefixedKey.isEmpty()) {
            call.reject("prefixedKey is null or empty");
            return;
        }
        tryStorageOp(
            call,
            () -> {
                boolean isExisting = implementation.doesKeyPairExist(prefixedKey);
                JSObject ret = new JSObject();
                ret.put("isExisting", isExisting);
                call.resolve(ret);
            }
        );
    }

    @PluginMethod
    public void sign(PluginCall call) {
        String prefixedKey = this.getKeyParam(call, "prefixedKey");
        String data = this.getKeyParam(call, "data");
        if (prefixedKey == null || prefixedKey.isEmpty() || data == null || data.isEmpty()) {
            call.reject(String.format("prefixedKey '%s' or data '%s' is null or empty", prefixedKey, data));
            return;
        }
        byte[] decodedData = Base64.getDecoder().decode(data.getBytes(StandardCharsets.UTF_8));
        tryStorageOp(
            call,
            () -> {
                String encodedSignature = implementation.sign(prefixedKey, decodedData);
                JSObject ret = new JSObject();
                ret.put("signature", encodedSignature);
                call.resolve(ret);
            }
        );
    }

    private void tryStorageOp(PluginCall call, StorageOp op) {
        SecureSigningException exception;
        try {
            op.run();
            return;
        } catch (SecureSigningException e) {
            exception = e;
        } catch (InvalidAlgorithmParameterException e) {
            // java.lang.IllegalStateException: Secure lock screen must be enabled to create keys requiring user authentication
            exception = new SecureSigningException(SecureSigningException.ErrorKind.secureLockScreenDisabled, e);
        } catch (UserNotAuthenticatedException e) {
            exception = new SecureSigningException(SecureSigningException.ErrorKind.userNotAuthenticated, e);
        } catch (GeneralSecurityException | IOException e) {
            exception = new SecureSigningException(SecureSigningException.ErrorKind.osError, e);
        } catch (Exception e) {
            exception = new SecureSigningException(SecureSigningException.ErrorKind.unknownError, e);
        }

        exception.rejectCall(call);
    }

    @PluginMethod
    public void createKeyPairIfDoesNotExist(PluginCall call) {
        String prefixedKey = this.getKeyParam(call, "prefixedKey");
        if (prefixedKey == null || prefixedKey.isEmpty()) {
            call.reject("prefixedKey is null or empty");
            return;
        }
        tryStorageOp(
            call,
            () -> {
                KeyPair kp = implementation.createKeyPairIfDoesNotExist(prefixedKey);
                byte[] ecPublicKeyBytes = implementation.ecFromPubKey(kp.getPublic());

                // Encode to Base64 and send
                String encodedPublicKey = new String(Base64.getEncoder().encode(ecPublicKeyBytes), StandardCharsets.UTF_8);
                JSObject ret = new JSObject();
                ret.put("publicKey", encodedPublicKey);
                call.resolve(ret);
            }
        );
    }

    @PluginMethod
    public void deleteKeyPair(PluginCall call) {
        String prefixedKey = this.getKeyParam(call, "prefixedKey");
        if (prefixedKey == null || prefixedKey.isEmpty()) {
            call.reject("prefixedKey is null or empty");
            return;
        }
        tryStorageOp(
            call,
            () -> {
                DeleteStatus deleteStatus = implementation.deleteKeyPair(prefixedKey);
                JSObject ret = new JSObject();
                ret.put("deleteStatus", deleteStatus.toString());
                call.resolve(ret);
            }
        );
    }

    @PluginMethod
    public void getKeyPair(PluginCall call) {
        String prefixedKey = this.getKeyParam(call, "prefixedKey");
        if (prefixedKey == null || prefixedKey.isEmpty()) {
            call.reject("prefixedKey is null or empty");
            return;
        }
        tryStorageOp(
            call,
            () -> {
                KeyPair kp = implementation.getKeyPair(prefixedKey);
                byte[] ecPublicKeyBytes = implementation.ecFromPubKey(kp.getPublic());

                // Encode to Base64 and send
                String encodedPublicKey = new String(Base64.getEncoder().encode(ecPublicKeyBytes), StandardCharsets.UTF_8);
                JSObject ret = new JSObject();
                ret.put("publicKey", encodedPublicKey);
                call.resolve(ret);
            }
        );
    }
}
