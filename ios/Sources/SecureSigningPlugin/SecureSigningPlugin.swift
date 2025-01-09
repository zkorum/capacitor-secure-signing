import Capacitor

@objc(SecureSigningPlugin)
public class SecureSigningPlugin: CAPPlugin {

    private let secureSigning = SecureSigning()

    @objc func generateKeyPair(_ call: CAPPluginCall) {
        guard let prefixedKey = call.getString("prefixedKey") else {
            call.reject("Missing 'prefixedKey'")
            return
        }
        do {
            let privateKey = try secureSigning.generateKeyPair(prefixedKey: prefixedKey)
            let publicKey = SecKeyCopyPublicKey(privateKey)!
            let publicKeyData = try secureSigning.encodePublicKey(publicKey)
            call.resolve(["publicKey": publicKeyData.base64EncodedString()])
        } catch {
            call.reject("Error generating key pair: \(error.localizedDescription)")
        }
    }

    @objc func doesKeyPairExist(_ call: CAPPluginCall) {
        guard let prefixedKey = call.getString("prefixedKey") else {
            call.reject("Missing 'prefixedKey'")
            return
        }
        let exists = secureSigning.doesKeyPairExist(prefixedKey: prefixedKey)
        call.resolve(["isExisting": exists])
    }

    @objc func sign(_ call: CAPPluginCall) {
        guard let prefixedKey = call.getString("prefixedKey"),
              let dataBase64 = call.getString("data"),
              let data = Data(base64Encoded: dataBase64) else {
            call.reject("Missing or invalid 'prefixedKey' or 'data'")
            return
        }
        do {
            let signature = try secureSigning.sign(prefixedKey: prefixedKey, data: data)
            call.resolve(["signature": signature])
        } catch {
            call.reject("Error signing data: \(error.localizedDescription)")
        }
    }

    @objc func deleteKeyPair(_ call: CAPPluginCall) {
        guard let prefixedKey = call.getString("prefixedKey") else {
            call.reject("Missing 'prefixedKey'")
            return
        }
        let status = secureSigning.deleteKeyPair(prefixedKey: prefixedKey)
        call.resolve(["deleteStatus": status == .deleted ? "DELETED" : "NOT_FOUND"])
    }
}
