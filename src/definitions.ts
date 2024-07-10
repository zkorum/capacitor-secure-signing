export interface SecureSigningPlugin {
  doesKeyPairExist(options: {
    prefixedKey: string;
  }): Promise<{ isExisting: boolean }>;
  generateKeyPair(options: {
    prefixedKey: string;
  }): Promise<{ publicKey: string }>;
  sign(options: {
    prefixedKey: string;
    data: string;
  }): Promise<{ signature: string }>;
  createKeyPairIfDoesNotExist(options: {
    prefixedKey: string;
  }): Promise<{ publicKey: string }>;
}
