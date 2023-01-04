# Web-Authentication-Server

https://www.w3.org/TR/webauthn-2/

# memo

```javascript
var publicKey = {
    "rp": {
        "id": "example.com",
        "name": "sample FIDO2 authentication server"
    },
    "user": {
        "id": Uint8Array.from("51a40444-524f-4c42-b702-513034df3ac2"),
        "name": "test2@example.com",
        "displayName": "test2@example.com"
    },
    "challenge": Uint8Array.from("7a7cd1e3-f344-4f63-b909-282e23ffe3e5"),
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
}

navigator.credentials.create({publicKey})
    .then((cred) => {
        console.log(cred.id);
        console.log(cred.type);
        console.log(cred.rawId);
        console.log(cred.response.clientDataJSON);
        console.log(cred.response.attestationObject);
    })
    .catch((err) => {
        console.log("ERROR", err);
    });
```
