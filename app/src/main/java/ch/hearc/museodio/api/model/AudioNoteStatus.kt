package ch.hearc.museodio.api.model

import com.github.kittinunf.fuel.core.ResponseDeserializable
import com.google.gson.Gson

/**
 * Data class containing the same data as the error when user don't have access to an audio note
 *
 * @property Error error: object error returned by the request
 */
class AudioNoteStatus(val error: Error?) {

    data class Error(val error: String?)

    /**
     * Class responsible of deserializing the audio note status JSON
     */
    class Deserializer: ResponseDeserializable<AudioNoteStatus> {
        override fun deserialize(content: String): AudioNoteStatus? = Gson().fromJson(content, AudioNoteStatus::class.java)
    }

}