package ch.hearc.museodio.api.model

import com.github.kittinunf.fuel.core.ResponseDeserializable
import com.google.gson.Gson

/**
 * Data class containing the same data as the Friends JSON received by the API
 *
 * @property  Success sucess: object success returned by the request
 */
class Friends(val success: Success){

    /**
     *  Data class to represent the object Success
     *
     * @property Array<Friend> friends: array of the friends
     * @property Array<Friend> invitationsWaitingForAnswer: array of the friends invitation that the current user did and in waiting to an answer from the other user
     * @property Array<Friend> invitationsToAnswer: array of the friends invitation in waiting to an answer from the current user
     */
    data class Success(val friends: Array<Friend>, val invitationsWaitingForAnswer: Array<Friend>, val invitationsToAnswer: Array<Friend>)

    /**
     *  Data class to represent the object Friend
     *
     * @property Int id: id of the user
     * @property String firstname: firstname of the user
     * @property String lastname:lastname of the user
     * @property String email: email of the user
     * @property String created_at: timestamp
     * @property String updated_at: timestamp
     * @property Pivot pivot: pivot of the relation
     */
    data class Friend(val id: Int,
                      val firstname: String,
                      val lastname: String,
                      val email: String,
                      val created_at: String,
                      val updated_at: String,
                      val pivot: Pivot)
    /**
     *  Data class to represent the object pivot of the two user id
     *
     * @property Int user_id_1: id of the user who made the invitation
     * @property Int user_id_2: id of the user who need to accept/refuse the invitation
     */
    data class Pivot(val user_id_1: Int, val user_id_2: Int)

    /**
     * Class responsible of deserializing the Friend status JSON
     */
    class Deserializer: ResponseDeserializable<Friends> {
        override fun deserialize(content: String): Friends = Gson().fromJson(content, Friends::class.java)
    }
}