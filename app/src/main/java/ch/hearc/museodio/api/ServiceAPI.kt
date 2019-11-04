package ch.hearc.museodio.api

import android.content.Context
import android.util.Log
import ch.hearc.museodio.R
import ch.hearc.museodio.api.model.AudioNote
import ch.hearc.museodio.api.model.PassportToken
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.*
import com.github.kittinunf.fuel.core.extensions.authentication
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import java.io.File


class ServiceAPI {

    companion object{

        private var url : String = "http://10.0.2.2:8000/api"

        fun login(email: String, password: String, context: Context, callbackFn: (isLoggedIn : Boolean) -> Unit){
            val dataJson: JsonObject = JsonParser().parse("{\"email\":$email, \"password\": $password}").getAsJsonObject()

            Fuel.post(url + "/login")
                .header("Content-Type", "application/json")
                .body(dataJson.toString())
                .responseObject(PassportToken.Deserializer()){ request, response, result ->
                    val (loginToken, err) = result
                    var isLoggedIn = false

                    if(loginToken?.success?.token != null){
                        saveApiKey(loginToken.success.token, context)
                        isLoggedIn = true
                    }

                    callbackFn(isLoggedIn)
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

        fun signUp(firstName: String, lastName: String, email: String, password: String, passwordConfirmation: String,
                   context: Context, callbackFn: (isLoggedIn : Boolean) -> Unit){
            val dataJson: JsonObject = JsonParser().parse("{\"firstname\":$firstName," +
                                                                "\"lastname\":$lastName," +
                                                                "\"email\":$email, " +
                                                                "\"password\": $password," +
                                                                "\"password_confirmation\":$passwordConfirmation}").getAsJsonObject()

            Fuel.post(url + "/register")
                .header("Content-Type", "application/json")
                .body(dataJson.toString())
                .responseObject(PassportToken.Deserializer()){ request, response, result ->
                    val (loginToken, err) = result
                    var isSignedUp = false

                    if(loginToken?.success?.token != null) {
                        isSignedUp = true
                    }

                    callbackFn(isSignedUp)
                }
        }

        fun downloadAudioNote(fileName: String, bearerToken: String) {
            Fuel.download(url + "/audio-notes/download/${fileName}", method=Method.GET)
                .fileDestination(){ response, url ->
                    Log.i("Bonjour", response.toString())
                    File.createTempFile("temp", "tmp.mp3") }
                .authentication()
                .bearer(bearerToken)
                .responseString(){ result ->
                    val (audioNote, err) = result
                    Log.i("Bonjour", audioNote.toString())
                }
        }

        fun uploadAudioNote(bearerToken: String, latitude: Double, longitude: Double) {

            val dataJson: JsonObject = JsonParser().parse("{\"latitude\":$latitude, \"longitude\": $longitude}").getAsJsonObject()

            Fuel.upload(url + "/audio-notes/save", method = Method.POST)
                .add(FileDataPart.from("path_to_your_file", name = "image"))
                .header("Content-Type", "application/json")
                .body(dataJson.toString())
                .authentication()
                .bearer(bearerToken)
        }

        fun saveApiKey(apiKey: String?, context: Context) {
            apiKey ?: return

            val sharedPref = context.getSharedPreferences(
                context.getString(R.string.preference_file), Context.MODE_PRIVATE)
            with (sharedPref.edit()) {
                putString(context.getString(ch.hearc.museodio.R.string.museodio_api_key), apiKey)
                commit()
            }
        }

        fun loadApiKey(context: Context): String {
            val sharedPref = context.getSharedPreferences(context.getString(R.string.preference_file), Context.MODE_PRIVATE) ?: return ""
            val apiKey = sharedPref.getString(context.getString(R.string.museodio_api_key), "")

            return apiKey!!
        }
    }

}