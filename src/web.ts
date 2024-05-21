import { WebPlugin } from '@capacitor/core';

import type { SecureSigningPlugin } from './definitions';

export class SecureSigningWeb extends WebPlugin implements SecureSigningPlugin {
  async sign(options: {
    prefixedKey: string;
    data: string;
  }): Promise<{ signature: string }> {
    // TODO:
    return Promise.resolve({ signature: options.prefixedKey });
  }
  async generateKeyPair(options: {
    prefixedKey: string;
  }): Promise<{ publicKey: string }> {
    // TODO:
    return Promise.resolve({ publicKey: options.prefixedKey });
  }
}
