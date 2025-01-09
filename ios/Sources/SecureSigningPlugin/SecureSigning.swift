import Foundation
import Security

enum SecureSigningError: Error {
    case keyGenerationError(String)
    case keyRetrievalError(String)
    case keyDeletionError(String)
    case signingError(String)
    case keyNotFound
    case unknownError(String)
}

class SecureSigning {

    func generateKeyPair(prefixedKey: String) throws -> SecKey {
        let tag = "\(prefixedKey)".data(using: .utf8)!
        let attributes: [String: Any] = [
            kSecAttrKeyType as String: kSecAttrKeyTypeECSECPrimeRandom,
            kSecAttrKeySizeInBits as String: 256,
            kSecAttrLabel as String: prefixedKey,
            kSecPrivateKeyAttrs as String: [
                kSecAttrIsPermanent as String: true,
                kSecAttrApplicationTag as String: tag
            ]
        ]
        
        var error: Unmanaged<CFError>?
        guard let privateKey = SecKeyCreateRandomKey(attributes as CFDictionary, &error) else {
            throw SecureSigningError.keyGenerationError(error?.takeRetainedValue().localizedDescription ?? "Unknown error")
        }
        
        return privateKey
    }

    func getKeyPair(prefixedKey: String) throws -> SecKey? {
        let tag = "\(prefixedKey)".data(using: .utf8)!
        let query: [String: Any] = [
            kSecClass as String: kSecClassKey,
            kSecAttrApplicationTag as String: tag,
            kSecAttrKeyType as String: kSecAttrKeyTypeECSECPrimeRandom,
            kSecReturnRef as String: true
        ]

        var item: CFTypeRef?
        let status = SecItemCopyMatching(query as CFDictionary, &item)

        if status == errSecItemNotFound {
            return nil
        } else if status != errSecSuccess {
            throw SecureSigningError.keyRetrievalError("Failed to retrieve key with status: \(status)")
        }

        return (item as! SecKey)
    }

    func sign(prefixedKey: String, data: Data) throws -> String {
        guard let privateKey = try getKeyPair(prefixedKey: prefixedKey) else {
            throw SecureSigningError.keyNotFound
        }

        let algorithm = SecKeyAlgorithm.ecdsaSignatureMessageX962SHA256
        guard SecKeyIsAlgorithmSupported(privateKey, .sign, algorithm) else {
            throw SecureSigningError.signingError("Algorithm not supported for signing.")
        }

        var error: Unmanaged<CFError>?
        guard let signature = SecKeyCreateSignature(privateKey, algorithm, data as CFData, &error) else {
            throw SecureSigningError.signingError(error?.takeRetainedValue().localizedDescription ?? "Unknown signing error")
        }

        return (signature as Data).base64EncodedString()
    }

    func doesKeyPairExist(prefixedKey: String) -> Bool {
        let tag = "\(prefixedKey)".data(using: .utf8)!
        let query: [String: Any] = [
            kSecClass as String: kSecClassKey,
            kSecAttrApplicationTag as String: tag,
            kSecAttrKeyType as String: kSecAttrKeyTypeECSECPrimeRandom,
            kSecReturnRef as String: false
        ]

        let status = SecItemCopyMatching(query as CFDictionary, nil)
        return status == errSecSuccess
    }

    func deleteKeyPair(prefixedKey: String) -> DeleteStatus {
        let tag = "\(prefixedKey)".data(using: .utf8)!
        let query: [String: Any] = [
            kSecClass as String: kSecClassKey,
            kSecAttrApplicationTag as String: tag,
            kSecAttrKeyType as String: kSecAttrKeyTypeECSECPrimeRandom
        ]

        let status = SecItemDelete(query as CFDictionary)
        if status == errSecSuccess {
            return .deleted
        } else if status == errSecItemNotFound {
            return .notFound
        } else {
            return .notFound
        }
    }

    func createKeyPairIfDoesNotExist(prefixedKey: String) throws -> SecKey {
        if let existingKey = try getKeyPair(prefixedKey: prefixedKey) {
            return existingKey
        }
        return try generateKeyPair(prefixedKey: prefixedKey)
    }
}
