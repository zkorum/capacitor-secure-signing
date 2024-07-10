export enum SigningErrorType {
  /**
   * The key is null or empty.
   */
  missingKey = 'missingKey',

  /**
   * An error occurred while interacting with Keystore
   */
  keystoreError = 'keystoreError',

  /**
   * An error occurred while generating the key
   */
  keyGenerationError = 'keyGenerationError',

  /**
   * An error occurred while generating the key
   */
  capacitorError = 'capacitorError',

  /**
   * `get()` found the data, but it is corrupted.
   */
  invalidData = 'invalidData',

  /**
   * A system-level error occurred when getting/setting data from/to the store.
   */
  osError = 'osError',

  /**
   * An unclassified system-level error occurred.
   */
  unknownError = 'unknownError',

  /**
   * Secure lock screen must be enabled to create keys requiring user authentication
   * */
  secureLockScreenDisabled = 'secureLockScreenDisabled',

  /**
   * User must be authenticated to access the secure storage
   * */
  userNotAuthenticated = 'userNotAuthenticated',
}

/**
 * If one of the storage functions throws, it will throw a SigningError which
 * will have a .code property that can be tested against SigningErrorType,
 * and a .message property will have a message suitable for debugging purposes.
 *
 * @modified 5.0.0
 */
export class SigningError extends Error {
  code: SigningErrorType;

  constructor(message: string, code: SigningErrorType) {
    super(message);
    this.name = this.constructor.name;
    this.code = code;
  }
}

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
