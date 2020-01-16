package ch.hearc.museodio.api.model

import com.github.kittinunf.fuel.core.ResponseDeserializable
import com.google.gson.Gson

class Friends(val success: Success){

    data class Success(val friends: Array<Friend>, val invitationsWaitingForAnswer: Array<Friend>, val invitationsToAnswer: Array<Friend>)

    data class Friend(val id: Int,
                      val firstname: String,
                      val lastname: String,
                      val email: String,
                      val created_at: String,
                      val updated_at: String,
                      val pivot: Pivot)

    data class Pivot(val user_id_1: Int, val user_id_2: Int)

    class Deserializer: ResponseDeserializable<Friends> {
        override fun deserialize(content: String): Friends = Gson().fromJson(content, Friends::class.java)
    }
}