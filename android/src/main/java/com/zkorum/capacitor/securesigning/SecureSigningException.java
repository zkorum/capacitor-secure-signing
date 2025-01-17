// MIT Licensed from https://github.com/aparajita/capacitor-secure-storage
package com.zkorum.capacitor.securesigning;

import androidx.annotation.Nullable;
import com.getcapacitor.PluginCall;
import java.util.HashMap;

public class SecureSigningException extends Exception {

    private static final HashMap<ErrorKind, String> errorMap;

    static {
        errorMap = new HashMap<>();
        errorMap.put(ErrorKind.keystoreError, "KeyStore related error: %s - %s");
        errorMap.put(ErrorKind.missingKey, "Empty key or missing key param: %s -%s");
        errorMap.put(ErrorKind.invalidData, "The data in the store is in an invalid format: %s -%s");
        errorMap.put(ErrorKind.keyGenerationError, "Error while generating the key: %s - %s");
        errorMap.put(ErrorKind.capacitorError, "Error while fetching keys in capacitor plugins: %s - %s");
        errorMap.put(ErrorKind.secureLockScreenDisabled, "Secure lock screen is disabled: %s - %s");
        errorMap.put(ErrorKind.userNotAuthenticated, "User is not authenticated: %s - %s");
        errorMap.put(ErrorKind.osError, "An OS error occurred: %s - %s");
        errorMap.put(ErrorKind.unknownError, "An unknown error occurred: %s - %s");
    }

    private String message = "";
    private String code = "";

    SecureSigningException(ErrorKind kind) {
        init(kind, null);
    }

    SecureSigningException(ErrorKind kind, Throwable osException) {
        init(kind, osException);
    }

    public static void reject(PluginCall call, ErrorKind kind) {
        reject(call, kind, null);
    }

    public static void reject(PluginCall call, ErrorKind kind, @Nullable Throwable osException) {
        SecureSigningException ex = new SecureSigningException(kind, osException);
        call.reject(ex.message, ex.code);
    }

    void init(ErrorKind kind, @Nullable Throwable osException) {
        String message = errorMap.get(kind);
        if (message != null) {
            if (osException != null) {
                this.message = String.format(message, osException.getClass().getSimpleName(), osException.getMessage());
            }
        }
        this.code = kind.toString();
    }

    public String getMessage() {
        return this.message;
    }

    public String getCode() {
        return this.code;
    }

    public void rejectCall(PluginCall call) {
        call.reject(this.message, this.code);
    }

    public enum ErrorKind {
        keystoreError,
        missingKey,
        invalidData,
        keyGenerationError,
        capacitorError,
        secureLockScreenDisabled,
        userNotAuthenticated,
        osError,
        unknownError
    }
}
