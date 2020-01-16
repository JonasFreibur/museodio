package ch.hearc.museodio.api.model

import com.github.kittinunf.fuel.core.ResponseDeserializable
import com.google.gson.Gson

class AudioNoteStatus(val error: Error?) {

    data class Error(val error: String?)

    class Deserializer: ResponseDeserializable<AudioNoteStatus> {
        override fun deserialize(content: String): AudioNoteStatus? = Gson().fromJson(content, AudioNoteStatus::class.java)
    }

}