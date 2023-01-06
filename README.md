# FIDO2-Server

https://www.w3.org/TR/webauthn-2/

# memo

```javascript
var publicKey = {
    "rp": {
        "id": "example.com",
        "name": "sample FIDO2 authentication server"
    },
    "user": {
        "id": new TextEncoder().encode("51a40444-524f-4c42-b702-513034df3ac2"),
        "name": "test2@example.com",
        "displayName": "test2@example.com"
    },
    "challenge": new TextEncoder().encode("7a7cd1e3-f344-4f63-b909-282e23ffe3e5"),
    "pubKeyCredParams": [
        {
            "alg": -7,
            "type": "public-key"
        }
    ],
    "timeout": 300000,
    "excludeCredentials": [],
    "authenticatorSelection": null,
    "attestation": "none",
    "extensions": {}
};

const arrayBufferToBase64String = (buf) => {
    return window.btoa(String.fromCharCode.apply(null, new Uint8Array(buf)));
}

navigator.credentials.create({publicKey})
    .then((cred) => {
        console.log(cred.id);
        console.log(cred.type);
        console.log(arrayBufferToBase64String(cred.rawId));
        console.log(arrayBufferToBase64String(cred.response.clientDataJSON));
        console.log(arrayBufferToBase64String(cred.response.attestationObject));
        console.log(String.fromCharCode.apply(null, new Uint8Array(cred.response.clientDataJSON)));
    })
    .catch((err) => {
        console.log("ERROR", err);
    });
```
