import { registerPlugin } from '@capacitor/core';

import type { SecureSigningPlugin } from './definitions';

const SecureSigning = registerPlugin<SecureSigningPlugin>('SecureSigning', {
  web: () => import('./web').then(m => new m.SecureSigningWeb()),
});

export * from './definitions';
export { SecureSigning };
