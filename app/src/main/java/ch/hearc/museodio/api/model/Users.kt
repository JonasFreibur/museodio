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

class Users(val success: Success?) {

    /**
     * Data class containing a success token
     */
    data class Success(val arrayUsers:Array<User>?)

    /**
     * Class responsible of deserializing the passport token JSON
     */
    class Deserializer: ResponseDeserializable<Users> {
        override fun deserialize(content: String): Users? = Gson().fromJson(content, Users::class.java)
    }
}

class User(val id:Int, val firstname: String,val lastname:String)
{

    /**
     * Class responsible of deserializing the passport token JSON
     */
    class Deserializer: ResponseDeserializable<Array<User>> {
        override fun deserialize(content: String): Array<User>? = Gson().fromJson(content, Array<User>::class.java)
    }
}