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

* [`doesKeyPairExist(...)`](#doeskeypairexist)
* [`generateKeyPair(...)`](#generatekeypair)
* [`sign(...)`](#sign)
* [`createKeyPairIfDoesNotExist(...)`](#createkeypairifdoesnotexist)
* [`deleteKeyPair(...)`](#deletekeypair)
* [`getKeyPair(...)`](#getkeypair)

</docgen-index>

<docgen-api>
<!--Update the source file JSDoc comments and rerun docgen to update the docs below-->

### doesKeyPairExist(...)

```typescript
doesKeyPairExist(options: { prefixedKey: string; }) => Promise<{ isExisting: boolean; }>
```

| Param         | Type                                  |
| ------------- | ------------------------------------- |
| **`options`** | <code>{ prefixedKey: string; }</code> |

**Returns:** <code>Promise&lt;{ isExisting: boolean; }&gt;</code>

--------------------


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


### createKeyPairIfDoesNotExist(...)

```typescript
createKeyPairIfDoesNotExist(options: { prefixedKey: string; }) => Promise<{ publicKey: string; }>
```

| Param         | Type                                  |
| ------------- | ------------------------------------- |
| **`options`** | <code>{ prefixedKey: string; }</code> |

**Returns:** <code>Promise&lt;{ publicKey: string; }&gt;</code>

--------------------


### deleteKeyPair(...)

```typescript
deleteKeyPair(options: { prefixedKey: string; }) => Promise<{ deleteStatus: string; }>
```

| Param         | Type                                  |
| ------------- | ------------------------------------- |
| **`options`** | <code>{ prefixedKey: string; }</code> |

**Returns:** <code>Promise&lt;{ deleteStatus: string; }&gt;</code>

--------------------


### getKeyPair(...)

```typescript
getKeyPair(options: { prefixedKey: string; }) => Promise<{ publicKey: string; }>
```

| Param         | Type                                  |
| ------------- | ------------------------------------- |
| **`options`** | <code>{ prefixedKey: string; }</code> |

**Returns:** <code>Promise&lt;{ publicKey: string; }&gt;</code>

--------------------

</docgen-api>

## TODO

[x] iOS support
[ ] more options for keys / security

## Acknowledgements

<img src="https://ngi.eu/wp-content/uploads/2019/06/Logo-NGI_Explicit-with-baseline-rgb.png" width="200" alt="NGI">

This project has received funding from the European Union's Horizon Europe 2020 research and innovation program through the [NGI TRUSTCHAIN](https://trustchain.ngi.eu/) program under cascade funding agreement No. 101093274.

The NGI TRUSTCHAIN grant mainly funded the `deleteKeyPair`, `getKeyPair` functions as well as the iOS support, as well as its general usage within Agora Citizen Network.

## License

This repository is released under [LICENSE](./LICENSE).

Certain parts of code are inspired or copied from https://github.com/aparajita/capacitor-secure-storage which is MIT Licensed.
Those parts are indicated in the code as comments.


