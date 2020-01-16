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
 * @property String firstName: Author's firstName
 * @property String lastName: Author's lastName
 * @property Double latitude: Audio note's geographical latitude
 * @property Double longitude: Audio note's geographical longitude
 * @property String file_name: The audio note's file name
 */
data class AudioNote(var firstName: String,
                     var lastName: String,
                     var latitude: Double,
                     var longitude: Double,
                     var file_name: String) {
    /**
     * Class responsible of deserializing the audi note JSON
     */
    class Deserializer: ResponseDeserializable<Array<AudioNote>>{
        override fun deserialize(content: String): Array<AudioNote>? = Gson().fromJson(content, Array<AudioNote>::class.java)
    }
}