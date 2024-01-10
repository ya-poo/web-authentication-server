package me.yapoo.webauthn.dto

import com.upokecenter.cbor.CBORObject
import java.math.BigInteger
import java.security.AlgorithmParameters
import java.security.KeyFactory
import java.security.PublicKey
import java.security.Signature
import java.security.spec.ECGenParameterSpec
import java.security.spec.ECParameterSpec
import java.security.spec.ECPoint
import java.security.spec.ECPublicKeySpec

sealed class CredentialPublicKey {

    abstract val algorithm: COSEAlgorithmIdentifier
    abstract val publicKey: PublicKey

    abstract fun verify(signature: ByteArray, data: ByteArray): Boolean

    class SHA256withECDSA(
        x: ByteArray,
        y: ByteArray,
    ) : CredentialPublicKey() {

        override val algorithm = COSEAlgorithmIdentifier.ES256

        override val publicKey: PublicKey = KeyFactory
            .getInstance("EC")
            .generatePublic(
                ECPublicKeySpec(
                    ECPoint(
                        BigInteger(1, x),
                        BigInteger(1, y)
                    ),
                    AlgorithmParameters.getInstance("EC").apply {
                        init(ECGenParameterSpec("secp256r1"))
                    }.getParameterSpec(ECParameterSpec::class.java)
                )
            )

        override fun verify(signature: ByteArray, data: ByteArray): Boolean {
            val sig = Signature.getInstance("SHA256withECDSA")
            sig.initVerify(publicKey)
            sig.update(data)
            return sig.verify(signature)
        }
    }

    companion object {

        fun of(cbor: CBORObject): CredentialPublicKey {
            return when (val alg = cbor[3].AsInt32()) {
                COSEAlgorithmIdentifier.ES256.value -> {
                    val kty = cbor[1].AsInt32()
                    if (kty != COSEKeyType.EC2.value) {
                        throw Exception("Illegal key type: $kty")
                    }
                    val x = cbor[-2].ToObject<ByteArray>(ByteArray::class.java)
                    val y = cbor[-3].ToObject<ByteArray>(ByteArray::class.java)
                    SHA256withECDSA(x, y)
                }

                else -> throw Exception("Unsupported algorithm: $alg")
            }
        }
    }
}
