package package.core.security

import android.content.Context
import android.os.Build
import dev.joysonic.demos.valerianademo.core.utils.error
import java.io.IOException
import java.security.KeyStore
import java.security.KeyStoreException
import java.security.NoSuchAlgorithmException
import java.security.cert.CertificateException

object EncryptionUtils {
    fun encrypt(context: Context, token: String): String? {
        val securityKey = getSecurityKey(context)
        return securityKey?.encrypt(token)
    }

    fun decrypt(context: Context, token: String): String? {
        val securityKey = getSecurityKey(context)
        return securityKey?.decrypt(token)
    }

    private fun getSecurityKey(context: Context): SecurityKey? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            EncryptionKeyGenerator.generateSecretKey(keyStore)
        } else {
            EncryptionKeyGenerator.generateKeyPairPreM(
                context,
                keyStore
            )
        }
    }

    private val keyStore: KeyStore?
        get() {
            var keyStore: KeyStore? = null
            try {
                keyStore = KeyStore.getInstance(EncryptionKeyGenerator.ANDROID_KEY_STORE)
                keyStore.load(null)
            } catch (e: KeyStoreException) {
                error<EncryptionKeyGenerator>(e)
            } catch (e: CertificateException) {
                error<EncryptionKeyGenerator>(e)
            } catch (e: NoSuchAlgorithmException) {
                error<EncryptionKeyGenerator>(e)
            } catch (e: IOException) {
                error<EncryptionKeyGenerator>(e)
            }
            return keyStore
        }

    fun clear() {
        val keyStore = keyStore
        try {
            if (keyStore!!.containsAlias(EncryptionKeyGenerator.KEY_ALIAS)) {
                keyStore.deleteEntry(EncryptionKeyGenerator.KEY_ALIAS)
            }
        } catch (e: KeyStoreException) {
            error<EncryptionKeyGenerator>(e)
        }
    }
}