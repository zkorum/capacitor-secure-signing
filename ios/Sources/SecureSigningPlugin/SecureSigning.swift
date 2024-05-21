import Foundation

@objc public class SecureSigning: NSObject {
    //TODO:
    @objc public func generateKeyPair(_ prefixedKey: String) -> String {
        print(prefixedKey)
        return prefixedKey
    }

    //TODO:
    @objc public func sign(_ prefixedKey: String, _ decodedData: Data) -> String {
        print(prefixedKey)
        return prefixedKey
    }
}
