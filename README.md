# @zkorum/capacitor-secure-signing

Generate signing keys and use them securely

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

## License

This repository is released under [LICENSE](./LICENSE).

Certain parts of code are inspired or copied from https://github.com/aparajita/capacitor-secure-storage which is MIT Licensed.
Those parts are indicated in the code as comments.


