export interface SecureSigningPlugin {
  generateKeyPair(options: {
    prefixedKey: string;
  }): Promise<{ publicKey: string }>;
  sign(options: {
    prefixedKey: string;
    data: string;
  }): Promise<{ signature: string }>;
}
