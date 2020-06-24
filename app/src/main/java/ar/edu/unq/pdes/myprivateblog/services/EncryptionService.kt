package ar.edu.unq.pdes.myprivateblog.services

import android.content.Context
import android.util.Base64
import androidx.core.content.edit
import ar.edu.unq.pdes.myprivateblog.R
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import java.io.InputStream
import java.io.OutputStream
import java.security.SecureRandom
import java.security.spec.KeySpec
import java.util.*
import javax.crypto.*
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec
import javax.inject.Inject

class EncryptionService @Inject constructor(
    val context: Context
) {
    private val db: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }
    private val transformationAlgorithm = "AES/CBC/PKCS5Padding"
    private val secretKeyAlgorithm = "AES"

    /**
     * Generates a new secret key
     */
    fun generateSecretKey(): SecretKey? {
        val keygen = KeyGenerator.getInstance(secretKeyAlgorithm)
        val secrand = SecureRandom.getInstance("SHA1PRNG")
        keygen.init(128, secrand)
        return keygen.generateKey()
    }

    /**
     * Encodes a secret key with Base64.
     */
    private fun encodeSecretKey(secretKey: SecretKey): String = Base64.encodeToString(secretKey.encoded, Base64.NO_WRAP)

    /**
     * Decodes a secret key with Base64
     */
    private fun decodeSecretKey(key: String): SecretKey {
        val decodedKey = Base64.decode(key, Base64.NO_WRAP)
        return SecretKeySpec(decodedKey, 0, decodedKey.size, secretKeyAlgorithm)
    }

    /**
     * Encrypts a String with a secret key
     */
    fun encrypt(secretKey: SecretKey, inputStream: InputStream, outputStream: OutputStream) {
        val skeySpec = SecretKeySpec(secretKey.encoded, secretKeyAlgorithm)
        val cipher = Cipher.getInstance(transformationAlgorithm)

        val iv = ByteArray(cipher.blockSize)
        SecureRandom().nextBytes(iv)

        cipher.init(Cipher.ENCRYPT_MODE, skeySpec, IvParameterSpec(iv))

        val cOutputStream = CipherOutputStream(outputStream, cipher)

        outputStream.write(iv)
        inputStream.copyTo(cOutputStream)
        cOutputStream.close()
    }

    /**
     * Decrypts a String with a secret key
     */
    fun decrypt(secretKey: SecretKey, inputStream: InputStream, outputStream: OutputStream) {
        val skeySpec = SecretKeySpec(secretKey.encoded, secretKeyAlgorithm)
        val cipher = Cipher.getInstance(transformationAlgorithm)

        val iv = ByteArray(cipher.blockSize)
        inputStream.read(iv)

        cipher.init(Cipher.DECRYPT_MODE, skeySpec, IvParameterSpec(iv))

        val cInputStream = CipherInputStream(inputStream, cipher)

        cInputStream.copyTo(outputStream)
    }

    /**
     * Encodes and stores the secret key in the SharedPreferences object
     */
    fun storeSecretKey(secretKey: SecretKey) {
        val encodedSecretKey = encodeSecretKey(secretKey)
        val sharedPreferences = context.getSharedPreferences(
            context.getString(R.string.preference_file_key),
            Context.MODE_PRIVATE
        )
        sharedPreferences.edit {
            this.putString(context.getString(R.string.secret_key_key), encodedSecretKey)
            this.commit()
        }
    }

    /**
     * Retrieves from the SharedPreferences object and decodes the secret key.
     * May return null if none secret key was found in the SharedPreferences object
     */
    fun retrieveSecretKey(): SecretKey? {
        val sharedPreferences = context.getSharedPreferences(
            context.getString(R.string.preference_file_key),
            Context.MODE_PRIVATE
        )
        val encodedSecretKey = sharedPreferences.getString(context.getString(R.string.secret_key_key), null)
        return if (encodedSecretKey != null) {
            decodeSecretKey(encodedSecretKey)
        } else {
            null
        }
    }

    /**
     * Clears the secret key stored in the Shared Preferences
     */
    fun clearSecretKey() {
        val sharedPreferences = context.getSharedPreferences(
            context.getString(R.string.preference_file_key),
            Context.MODE_PRIVATE
        )
        sharedPreferences.edit {
            this.putString(context.getString(R.string.secret_key_key), null)
            this.commit()
        }
    }

    /**
     * Uploads the secret key in the authenticated user's dedicated document in firestore
     */
    fun uploadSecretKey(secretKey: SecretKey): Task<Void>? {
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            val userUid = user.uid
            val encodedSecretKey = encodeSecretKey(secretKey)
            val encodedSecretKeyObject = mapOf<String, String>( "token" to encodedSecretKey )
            return db.document("users/$userUid").set(encodedSecretKeyObject, SetOptions.merge())
        }
        return null
    }

    /**
     * Downloads the secret key from the authenticated user's dedicated document in firestore
     */
    fun downloadSecretKey(callback: (SecretKey?) -> Unit) {
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            val userUid = user.uid
            db.document("users/$userUid").addSnapshotListener { documentSnapshot, firebaseFirestoreException ->
                val encodedSecretKey = documentSnapshot?.getString("token")
                val secretKey = if (encodedSecretKey != null) decodeSecretKey(encodedSecretKey) else null
                callback(secretKey)
            }
        }
    }
}
