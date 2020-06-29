package ar.edu.unq.pdes.myprivateblog

import android.content.Context
import ar.edu.unq.pdes.myprivateblog.services.EncryptionService
import org.apache.tools.ant.filters.StringInputStream
import org.hamcrest.core.IsInstanceOf.instanceOf
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.nio.charset.Charset
import javax.crypto.SecretKey

class EncryptionServiceTest {

    private lateinit var encryptionService: EncryptionService
    private lateinit var context: Context

    @Before
    fun setup() {
        context = mock(Context::class.java)
        encryptionService = EncryptionService(context)
    }

    @Test
    fun whenGeneratingAKey_itShouldReturnASecretKey() {
        assertThat(encryptionService.generateSecretKey()!!, instanceOf(SecretKey::class.java))
    }

    @Test
    fun whenGeneratingKeys_theyShouldBeDifferentEachTime() {
        val secretKey0 = encryptionService.generateSecretKey()!!.encoded
        val secretKey1 = encryptionService.generateSecretKey()!!.encoded
        val secretKey2 = encryptionService.generateSecretKey()!!.encoded
        assertNotEquals(secretKey0, secretKey1)
        assertNotEquals(secretKey0, secretKey2)
        assertNotEquals(secretKey1, secretKey2)
    }

    @ExperimentalStdlibApi
    @Test
    fun whenEncryptingAValue_itShouldBeTheSameAfterDecrypting() {
        val someString = "I'm a happy string"
        val secretKey = encryptionService.generateSecretKey()!!

        val inputStream = ByteArrayInputStream(someString.encodeToByteArray())
        val outputStream = ByteArrayOutputStream()

        outputStream.close()

        encryptionService.encrypt(secretKey, inputStream, outputStream)
        val encrypted = outputStream.toByteArray()

        val inputStreamEncrypted = ByteArrayInputStream(encrypted)
        val outputStreamEncrypted = ByteArrayOutputStream()

        outputStreamEncrypted.close()

        encryptionService.decrypt(secretKey, inputStreamEncrypted, outputStreamEncrypted)

        val encryptedByteArray = outputStreamEncrypted.toByteArray()

        assertEquals(someString, encryptedByteArray.decodeToString())
    }
}