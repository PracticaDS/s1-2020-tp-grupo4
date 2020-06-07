package ar.edu.unq.pdes.myprivateblog

import android.content.Context
import ar.edu.unq.pdes.myprivateblog.services.EncryptionService
import org.hamcrest.core.IsInstanceOf.instanceOf
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
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

    @Test
    fun whenEncryptingAValue_itShouldBeTheSameAfterDecrypting() {
        val someString = "I'm a happy string"
        val secretKey = encryptionService.generateSecretKey()!!
        val encryptedString = encryptionService.encrypt(secretKey, someString)
        val decryptedString = encryptionService.decrypt(secretKey, encryptedString)

        assertEquals(someString, decryptedString)
    }
}