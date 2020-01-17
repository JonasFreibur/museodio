/**
 * @author Verardo Luca, Carraux Roxane, Freiburghaus Jonas
 * HE-Arc 2019
 */

package ch.hearc.museodio.api.model

import com.github.kittinunf.fuel.core.ResponseDeserializable
import com.google.gson.Gson


/**
 * Data class containing the same data as the Users JSON received by the API
 *
 * @property Success sucess: object success returned by the request
 */
class Users(val success: Array<Success>){

    /**
     * Data class to represent the object success
     *
     * @property Int id: id of the user
     * @property String firstname: firstname of the user
     * @property String lastname:lastname of the us
     */
    data class Success(val id: Int, val firstname: String, val lastname: String)

    /**
     * Class responsible of deserializing the Users status JSON
     */
    class Deserializer: ResponseDeserializable<Users> {
        override fun deserialize(content: String): Users = Gson().fromJson(content, Users::class.java)
    }
}