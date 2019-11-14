/**
 * @author Verardo Luca, Carraux Roxane, Freiburghaus Jonas
 * HE-Arc 2019
 */

package ch.hearc.museodio.api.model

import com.github.kittinunf.fuel.core.ResponseDeserializable
import com.google.gson.Gson

/**
 * Data class containing the same data as the AudiNote JSON received by the API
 *
 * @property Success? success: A Success token
 */
class PassportToken (val success: Success?) {

    /**
     * Data class containing a success token
     */
    data class Success(val token: String?)

    /**
     * Class responsible of deserializing the passport token JSON
     */
    class Deserializer: ResponseDeserializable<PassportToken> {
        override fun deserialize(content: String): PassportToken? = Gson().fromJson(content, PassportToken::class.java)
    }
}