package ch.hearc.museodio.api

import android.content.Context
import android.util.Log
import android.widget.Toast
import ch.hearc.museodio.MainActivity
import ch.hearc.museodio.R
import ch.hearc.museodio.api.model.AudioNote
import ch.hearc.museodio.api.model.LoginToken
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.*
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import kotlin.math.log
import org.json.JSONArray
import org.json.JSONObject



class ServiceAPI {

    companion object{

        private var url : String = "http://10.0.2.2:8000/api"

        fun login(email: String, password: String, context: Context, callbackFn: (isLoggedIn : Boolean) -> Unit){
            val dataJson: JsonObject = JsonParser().parse("{\"email\":$email, \"password\": $password}").getAsJsonObject()

            Fuel.post(url + "/login")
                .header("Content-Type", "application/json")
                .body(dataJson.toString())
                .responseObject(LoginToken.Deserializer()){ request, response, result ->
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