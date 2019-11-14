/**
 * @author Verardo Luca, Carraux Roxane, Freiburghaus Jonas
 * HE-Arc 2019
 */

package ch.hearc.museodio.api

import android.content.Context
import android.util.Log
import androidx.core.text.parseAsHtml
import ch.hearc.museodio.R
import ch.hearc.museodio.api.model.AudioNote
import ch.hearc.museodio.api.model.PassportToken
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.DataPart
import com.github.kittinunf.fuel.core.FileDataPart
import com.github.kittinunf.fuel.core.InlineDataPart
import com.github.kittinunf.fuel.core.Method

import com.github.kittinunf.fuel.core.extensions.authentication
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import java.io.File

/**
 * Handles API calls
 * Singleton
 */
class ServiceAPI {

    companion object{
        //private var url : String = "http://10.0.2.2:8000/api"
        private var url : String = "https://museodio.srvz-webapp.he-arc.ch/api"

        /**
         * Login API call : /login
         * @param String email
         * @param String password
         * @param Context context
         * @param (Boolean) -> Unit callbackFn : Callback function
         */
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

        /**
         * Get all audio notes API call : /audio-notes
         * @param (AudioNote) -> Unit callbackFn : Callback function
         */
        fun fetchAllAudioNotes(callbackFn : (audioNote: AudioNote) -> Unit) {
            Fuel.get(url + "/audio-notes/")
                .responseObject(AudioNote.Deserializer()){ request, response, result ->
                    val (audioNotes, err) = result
                    audioNotes?.forEach {audioNote ->
                        callbackFn(audioNote)
                    }
                }
        }

        /**
         * Sign up API call : /register
         * @param String firstName
         * @param String lastName
         * @param String email
         * @param String password
         * @param String passwordConfirmation
         * @param Context context
         * @param (Boolean) -> Unit callbackFn : Callback function
         */
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
                .responseObject(PassportToken.Deserializer()) { request, response, result ->
                    val (loginToken, err) = result
                    var isSignedUp = false
                    if (loginToken?.success?.token != null) {
                        isSignedUp = true
                    }
                    callbackFn(isSignedUp)
                }
        }

        /**
         * Uplod audio note API call : /audio-notes/save
         * @param String bearerToken : For authentification when logged in
         * @param Double latitude
         * @param Double longitude
         * @param String fileName
         */
        fun uploadAudioNote(bearerToken: String, latitude: Double, longitude: Double, fileName:String) {
            val data: DataPart = FileDataPart.from(fileName, name = "audio", contentType = "multipart/form-data")
            val dataJson: JsonObject = JsonParser().parse("{\"latitude\":$latitude, \"longitude\": $longitude}").getAsJsonObject()

            Fuel.upload(url + "/audio-notes/save", method = Method.POST)
                .add(data, InlineDataPart(latitude.toString(), name="latitude", contentType="multipart/form-data"),InlineDataPart(longitude.toString(), name="longitude", contentType="multipart/form-data"))
                .authentication()
                .bearer(bearerToken)
                .responseString(){ result ->
                    val (res, err) = result
                    Log.i("reponse ",res)
                }
        }

        /**
         * Save Api key to shared preferences
         * @param String? apiKey: For authentification when logged in
         * @param Context context: The activity context
         */
        fun saveApiKey(apiKey: String?, context: Context) {
            apiKey ?: return
            val sharedPref = context.getSharedPreferences(
                context.getString(R.string.preference_file), Context.MODE_PRIVATE)
            with (sharedPref.edit()) {
                putString(context.getString(ch.hearc.museodio.R.string.museodio_api_key), apiKey)
                commit()
            }
        }

        /**
         * @param Context context: The activity context
         * @return String apiKey
         */
        fun loadApiKey(context: Context): String {
            val sharedPref = context.getSharedPreferences(context.getString(R.string.preference_file), Context.MODE_PRIVATE) ?: return ""
            val apiKey = sharedPref.getString(context.getString(R.string.museodio_api_key), "")
            return apiKey!!
        }
    }
}