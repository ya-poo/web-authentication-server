# FIDO2-Server

https://www.w3.org/TR/webauthn-3/

# memo

## Registration

```javascript
var publicKey = {
    "rp": {
        "id": "localhost",
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
        console.log(JSON.stringify({
            clientDataJSON: arrayBufferToBase64String(cred.response.clientDataJSON),
            attestationObject: arrayBufferToBase64String(cred.response.attestationObject)
        }));
    })
    .catch((err) => {
        console.log("ERROR", err);
    });
```

## Authentication

```javascript
var publicKey = {
    "challenge": new TextEncoder().encode("220a8aaf-62dc-4bd1-a026-30f2499f8915"),
    "timeout": 300000,
    "rpid": "localhost",
    "allowCredentials": [],
    "userVerificationRequirement": "preferred",
    "extensions": {}
}

const arrayBufferToBase64String = (buf) => {
    return window.btoa(String.fromCharCode.apply(null, new Uint8Array(buf)));
}

navigator.credentials.get({publicKey})
    .then((cred) => {
        console.log(JSON.stringify({
            id: arrayBufferToBase64String(cred.rawId),
            rawId: arrayBufferToBase64String(cred.rawId),
            response: {
                authenticatorData: arrayBufferToBase64String(cred.response.authenticatorData),
                signature: arrayBufferToBase64String(cred.response.signature),
                userHandle: arrayBufferToBase64String(cred.response.userHandle),
                clientDataJSON: arrayBufferToBase64String(cred.response.clientDataJSON),
            }
        }));
    })
    .catch((err) => {
        console.log("ERROR", err);
    });
```
