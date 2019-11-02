package ch.hearc.museodio.api

import android.util.Log
import ch.hearc.museodio.api.model.AudioNote
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.Headers
import com.google.gson.Gson

class ServiceAPI {

    companion object{

        private var url : String = "http://10.0.2.2:8000/api"

        fun login(email: String, password: String) {
            Log.i("Bonjour", email + " " + password)
            val bodyData = "{'email' : 'test@test.com', 'password' : 'test1234'}"
            Log.i("BonjourBody", bodyData)
            Fuel.post(url + "/login")
                .header(
                    Headers.CONTENT_TYPE, "application/json"
                )
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

        fun saveAudioNote(audioNote: AudioNote)
        {
            Log.i("audio save",audioNote.toString())
            //Fuel.put(url+ "/audio-notes/").body(Gson().toJson(audioNote).toString())

            /*val post = Post(1, 1, "Lorem", "Lorem Ipse dolor sit amet")

            val (request, response, result)
              = Fuel.post("https://jsonplaceholder.typicode.com/posts")
                .header("Content-Type" to "application/json")
                .body(Gson().toJson(post).toString())
            */

        }
    }

}