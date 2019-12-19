/**
 * @author Verardo Luca, Carraux Roxane, Freiburghaus Jonas
 * HE-Arc 2019
 */

package ch.hearc.museodio.api.model

import ch.hearc.museodio.api.ServiceAPI
import com.github.kittinunf.fuel.core.ResponseDeserializable
import com.google.gson.Gson

/**
 * TODO:changer
 * Data class containing the same data as the AudiNote JSON received by the API
 *
 * @property String firstName: Author's firstName
 * @property String lastName: Author's lastName
 * @property Double latitude: Audio note's geographical latitude
 * @property Double longitude: Audio note's geographical longitude
 * @property String file_name: The audio note's file name
 */

class Users(val success:Success?) {


    data class Success(var id :Int?, var firstName: String?, var lastName: String?)

    /**
     * Class responsible of deserializing the audi note JSON
     */
    class Deserializer: ResponseDeserializable<Array<Users>>{
        override fun deserialize(content: String): Array<Users>? = Gson().fromJson(content, Array<Users>::class.java)
    }
}