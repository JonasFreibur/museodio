package ch.hearc.museodio.api.model

import com.github.kittinunf.fuel.core.ResponseDeserializable
import com.google.gson.Gson

data class AudioNote(var firtsName: String,
                     var lastName: String,
                     var latitude: Double,
                     var longitude: Double,
                     var file_name: String) {

    class Deserializer: ResponseDeserializable<Array<AudioNote>>{
        override fun deserialize(content: String): Array<AudioNote>? = Gson().fromJson(content, Array<AudioNote>::class.java)
    }

}