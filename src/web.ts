import { WebPlugin } from '@capacitor/core';

import type { SecureSigningPlugin } from './definitions';

export class SecureSigningWeb extends WebPlugin implements SecureSigningPlugin {
  async echo(options: { value: string }): Promise<{ value: string }> {
    console.log('ECHO', options);
    return options;
  }
}
