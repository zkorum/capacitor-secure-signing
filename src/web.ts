import { WebPlugin } from '@capacitor/core';

import type { SecureSigningPlugin } from './definitions';

export class SecureSigningWeb extends WebPlugin implements SecureSigningPlugin {
  async doesKeyPairExist(_options: {
    prefixedKey: string;
  }): Promise<{ isExisting: boolean }> {
    // TODO:
    return Promise.resolve({ isExisting: true });
  }
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

  async createKeyPairIfDoesNotExist(options: {
    prefixedKey: string;
  }): Promise<{ publicKey: string }> {
    // TODO:
    return Promise.resolve({ publicKey: options.prefixedKey });
  }

  async deleteKeyPair(_options: {
    prefixedKey: string;
  }): Promise<{ deleteStatus: string }> {
    // TODO:
    // Return an enum?
    return Promise.resolve({ deleteStatus: 'DELETED' });
  }

  async getKeyPair(options: {
    prefixedKey: string;
  }): Promise<{ publicKey: string }> {
    // TODO:
    return Promise.resolve({ publicKey: options.prefixedKey });
  }
}
