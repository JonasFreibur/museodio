package ch.hearc.museodio.api

import android.util.Log
import ch.hearc.museodio.api.model.AudioNote
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.Headers

class ServiceAPI {

    companion object{

        private var url : String = "http://10.0.2.2:8000/api"

        fun login(email: String, password: String){
            Log.i("Bonjour", email + " " + password)
            val bodyData = "{'email' : 'test@test.com', 'password' : 'test1234'}"
            Log.i("BonjourBody", bodyData)
            Fuel.post(url + "/login")
                .header(
                    Headers.CONTENT_TYPE, "application/json")
                .body(bodyData)
                .responseString() { result ->
                    val (data, error) = result

                    Log.i("Bonjour", data)
                }
        }

        fun fetchAllAudioNotes(callbackFn : (audioNote: AudioNote) -> Unit) {
            Fuel.get(url + "/audio-notes/")
                .responseObject(AudioNote.Deserializer()){ request, response, result ->
                    val (audioNotes, err) = result
                    audioNotes?.forEach {audioNote ->
                        callbackFn(audioNote)
                    }
                }
        }
    }

}