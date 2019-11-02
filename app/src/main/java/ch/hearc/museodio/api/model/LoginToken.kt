package ch.hearc.museodio.api.model

import android.util.Log
import com.github.kittinunf.fuel.core.ResponseDeserializable
import com.google.gson.Gson

class LoginToken (val success: Success?) {

    data class Success(val token: String?)

    class Deserializer: ResponseDeserializable<LoginToken> {
        override fun deserialize(content: String): LoginToken? = Gson().fromJson(content, LoginToken::class.java)
    }

}