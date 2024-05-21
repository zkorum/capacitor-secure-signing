package com.zkorum.capacitor.securesigning;

import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Signature;
import java.security.SignatureException;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;
import java.util.Base64;

@CapacitorPlugin(name = "SecureSigning")
public class SecureSigningPlugin extends Plugin {

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
    public void generateKeypair(PluginCall call) {
        String prefixedKey = this.getKeyParam(call, "prefixedKey");
        KeyPair kp = null;
        try {
            kp = implementation.generateKeypair(prefixedKey);
            String encodedPublicKey = Base64.getEncoder().encodeToString(kp.getPublic().getEncoded());
            JSObject ret = new JSObject();
            ret.put("publicKey", encodedPublicKey);
            call.resolve(ret);
        } catch (SecureSigningException ex) {
            call.reject(ex.getMessage(), ex.getCode());
        }
    }

    @PluginMethod
    public void sign(PluginCall call) {
        String prefixedKey = this.getKeyParam(call, "prefixedKey");
        String data = this.getKeyParam(call, "data");
        byte[] decodedData = Base64.getDecoder().decode(data);
        try {
            String encodedSignature = implementation.sign(prefixedKey, decodedData);
            JSObject ret = new JSObject();
            ret.put("signature", encodedSignature);
            call.resolve(ret);
        } catch (SecureSigningException ex) {
            call.reject(ex.getMessage(), ex.getCode());
        }
    }
}
