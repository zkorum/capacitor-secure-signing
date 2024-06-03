package com.zkorum.capacitor.securesigning;

import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;
import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.util.Base64;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERBitString;
import org.bouncycastle.asn1.DERSequence;

@CapacitorPlugin(name = "SecureSigning")
public class SecureSigningPlugin extends Plugin {

    // @Override
    // public void load() {
    //     super.load();
    //     implementation.init();
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
        try {
            KeyPair kp = implementation.generateKeyPair(prefixedKey);
            byte[] derEncodedPublicKey = kp.getPublic().getEncoded();
            // Extract the EC public key (65 bytes) from the DER encoded public key (91
            // bytes)
            // @see
            // https://stackoverflow.com/questions/57209127/what-should-the-length-of-public-key-on-ecdh-be
            ASN1Sequence sequence = DERSequence.getInstance(derEncodedPublicKey);
            DERBitString ecPublicKey = (DERBitString) sequence.getObjectAt(1);
            byte[] ecPublicKeyBytes = ecPublicKey.getBytes();

            // Encode to Base64 and send
            String encodedPublicKey = new String(Base64.getEncoder().encode(ecPublicKeyBytes), StandardCharsets.UTF_8);
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
        // TODO: verify that data is not null, else throw a specific error
        assert data != null;
        byte[] decodedData = Base64.getDecoder().decode(data.getBytes(StandardCharsets.UTF_8));
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
