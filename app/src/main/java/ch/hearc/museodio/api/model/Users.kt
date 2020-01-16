/**
 * @author Verardo Luca, Carraux Roxane, Freiburghaus Jonas
 * HE-Arc 2019
 */

package ch.hearc.museodio.api.model

import com.github.kittinunf.fuel.core.ResponseDeserializable
import com.google.gson.Gson



class Users(val success: Array<Success>){

    data class Success(val id: Int, val firstname: String, val lastname: String)

    class Deserializer: ResponseDeserializable<Users> {
        override fun deserialize(content: String): Users = Gson().fromJson(content, Users::class.java)
    }
}