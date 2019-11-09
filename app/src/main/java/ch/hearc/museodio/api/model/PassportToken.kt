package ch.hearc.museodio.api.model

import com.github.kittinunf.fuel.core.ResponseDeserializable
import com.google.gson.Gson

class PassportToken (val success: Success?) {
    data class Success(val token: String?)

    class Deserializer: ResponseDeserializable<PassportToken> {
        override fun deserialize(content: String): PassportToken? = Gson().fromJson(content, PassportToken::class.java)
    }
}