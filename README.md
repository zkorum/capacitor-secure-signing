# @zkorum/capacitor-secure-signing

Generate signing keys and use them securely.

WARNING: beta software. Only tailored for ZKorum's need for now. At this stage, API and core functionalities WILL change and WILL NOT be backward compatible.

## Install

```bash
npm install @zkorum/capacitor-secure-signing
npx cap sync
```

## API

<docgen-index>

* [`generateKeyPair(...)`](#generatekeypair)
* [`sign(...)`](#sign)

</docgen-index>

<docgen-api>
<!--Update the source file JSDoc comments and rerun docgen to update the docs below-->

### generateKeyPair(...)

```typescript
generateKeyPair(options: { prefixedKey: string; }) => Promise<{ publicKey: string; }>
```

| Param         | Type                                  |
| ------------- | ------------------------------------- |
| **`options`** | <code>{ prefixedKey: string; }</code> |

**Returns:** <code>Promise&lt;{ publicKey: string; }&gt;</code>

--------------------


### sign(...)

```typescript
sign(options: { prefixedKey: string; data: string; }) => Promise<{ signature: string; }>
```

| Param         | Type                                                |
| ------------- | --------------------------------------------------- |
| **`options`** | <code>{ prefixedKey: string; data: string; }</code> |

**Returns:** <code>Promise&lt;{ signature: string; }&gt;</code>

--------------------

</docgen-api>

## TODO

Handle the following exceptions properly: 

```java
java.security.InvalidAlgorithmParameterException: java.lang.IllegalStateException: Secure lock screen must be enabled to create keys requiring user authentication
```
=> happens when no pincode whatsover has been set => sent a specific error?

```java
File: http://192.168.1.96:9500/src/boot/passphrase.ts - Line 20 - Msg: Error while setting up key StorageError: An OS error occurred (UserNotAuthenticatedException)
```
=> happens when the user is not authenticated (or if the user authentication has expired? verify) => send specific error


Also uniform the errors with iOS.


## License

This repository is released under [LICENSE](./LICENSE).

Certain parts of code are inspired or copied from https://github.com/aparajita/capacitor-secure-storage which is MIT Licensed.
Those parts are indicated in the code as comments.


