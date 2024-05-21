import Foundation
import Capacitor

/**
 * Please read the Capacitor iOS Plugin Development Guide
 * here: https://capacitorjs.com/docs/plugins/ios
 */
@objc(SecureSigningPlugin)
public class SecureSigningPlugin: CAPPlugin, CAPBridgedPlugin {
    public let identifier = "SecureSigningPlugin"
    public let jsName = "SecureSigning"
    public let pluginMethods: [CAPPluginMethod] = [
        CAPPluginMethod(name: "generateKeyPair", returnType: CAPPluginReturnPromise),
        CAPPluginMethod(name: "sign", returnType: CAPPluginReturnPromise)
    ]
    private let implementation = SecureSigning()

    @objc func generateKeyPair(_ call: CAPPluginCall) {
        let prefixedKey = call.getString("prefixedKey") ?? ""
        call.resolve([
            "publicKey": implementation.generateKeyPair(prefixedKey)
        ])
    }

    @objc func sign(_ call: CAPPluginCall) {
        let prefixedKey = call.getString("prefixedKey") ?? ""
        let data = call.getString("data") ?? ""
        let decodedData = Data(data.utf8).base64EncodedString()
        call.resolve([
            "signature": implementation.sign(prefixedKey, decodedData)
        ])
    }
}
