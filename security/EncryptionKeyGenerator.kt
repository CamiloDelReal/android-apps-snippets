@file:Suppress("DEPRECATION")

package package.core.security

import android.annotation.TargetApi
import android.content.Context
import android.os.Build
import android.security.KeyPairGeneratorSpec
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import dev.joysonic.demos.valerianademo.core.utils.error
import java.math.BigInteger
import java.security.*
import java.util.*
import javax.crypto.KeyGenerator
import javax.security.auth.x500.X500Principal

object EncryptionKeyGenerator {
    const val ANDROID_KEY_STORE = "AndroidKeyStore"
    const val KEY_ALIAS = "KEY_ALIAS"

    @TargetApi(Build.VERSION_CODES.M)
    fun generateSecretKey(keyStore: KeyStore?): SecurityKey? {
        try {
            if (!keyStore!!.containsAlias(KEY_ALIAS)) {
                val keyGenerator = KeyGenerator.getInstance(
                    KeyProperties.KEY_ALGORITHM_AES,
                    ANDROID_KEY_STORE
                )
                keyGenerator.init(
                    KeyGenParameterSpec.Builder(
                        KEY_ALIAS,
                        KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
                    ).setBlockModes(
                        KeyProperties.BLOCK_MODE_GCM
                    )
                        .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                        .setRandomizedEncryptionRequired(false)
                        .build()
                )
                return SecurityKey(keyGenerator.generateKey())
            }
        } catch (e: KeyStoreException) {
            error<EncryptionKeyGenerator>(e)
        } catch (e: NoSuchProviderException) {
            error<EncryptionKeyGenerator>(e)
        } catch (e: NoSuchAlgorithmException) {
            error<EncryptionKeyGenerator>(e)
        } catch (e: InvalidAlgorithmParameterException) {
            error<EncryptionKeyGenerator>(e)
        }
        try {
            val entry = keyStore!!.getEntry(
                KEY_ALIAS,
                null
            ) as KeyStore.SecretKeyEntry
            return SecurityKey(entry.secretKey)
        } catch (e: KeyStoreException) {
            error<EncryptionKeyGenerator>(e)
        } catch (e: NoSuchAlgorithmException) {
            error<EncryptionKeyGenerator>(e)
        } catch (e: UnrecoverableEntryException) {
            error<EncryptionKeyGenerator>(e)
        }
        return null
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    fun generateKeyPairPreM(
        context: Context?,
        keyStore: KeyStore?
    ): SecurityKey? {
        try {
            if (!keyStore!!.containsAlias(KEY_ALIAS)) {
                val start = Calendar.getInstance()
                val end = Calendar.getInstance()
                //1 Year validity
                end.add(Calendar.YEAR, 1)
                val spec =
                    KeyPairGeneratorSpec.Builder(context!!).setAlias(KEY_ALIAS)
                        .setSubject(X500Principal("CN=$KEY_ALIAS"))
                        .setSerialNumber(BigInteger.TEN)
                        .setStartDate(start.time)
                        .setEndDate(end.time)
                        .build()
                val kpg =
                    KeyPairGenerator.getInstance(
                        "RSA",
                        ANDROID_KEY_STORE
                    )
                kpg.initialize(spec)
                kpg.generateKeyPair()
            }
        } catch (e: KeyStoreException) {
            error<EncryptionKeyGenerator>(e)
        } catch (e: NoSuchAlgorithmException) {
            error<EncryptionKeyGenerator>(e)
        } catch (e: InvalidAlgorithmParameterException) {
            error<EncryptionKeyGenerator>(e)
        } catch (e: NoSuchProviderException) {
            error<EncryptionKeyGenerator>(e)
        }
        try {
            val entry = keyStore!!.getEntry(
                KEY_ALIAS,
                null
            ) as KeyStore.PrivateKeyEntry
            return SecurityKey(
                KeyPair(entry.certificate.publicKey, entry.privateKey)
            )
        } catch (e: KeyStoreException) {
            error<EncryptionKeyGenerator>(e)
        } catch (e: NoSuchAlgorithmException) {
            error<EncryptionKeyGenerator>(e)
        } catch (e: UnrecoverableEntryException) {
            error<EncryptionKeyGenerator>(e)
        }
        return null
    }
}