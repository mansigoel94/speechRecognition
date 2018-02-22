package com.example.mansigoel.speechrecognition

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_offline.*
import java.util.*

class OfflineActivity : AppCompatActivity(),RecognitionListener {

    private var speech: SpeechRecognizer? = null
    private var recognizerIntent: Intent? = null
    private val LOG_TAG = "OfflineActivity"
    private val REQUEST_RECORD_PERMISSION = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_offline)

        progressBar!!.visibility = View.INVISIBLE
        speech = SpeechRecognizer.createSpeechRecognizer(this)
        Log.i(LOG_TAG, "isRecognitionAvailable: " + SpeechRecognizer.isRecognitionAvailable(this))
        speech!!.setRecognitionListener(this)

        recognizerIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        recognizerIntent!!.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE,
                Locale.getDefault())
        recognizerIntent!!.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        recognizerIntent!!.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3)

        iv_mic.setOnClickListener {
            progressBar!!.visibility = View.VISIBLE
            progressBar!!.isIndeterminate = true
            ActivityCompat.requestPermissions(this@OfflineActivity,
                    arrayOf(Manifest.permission.RECORD_AUDIO),
                    REQUEST_RECORD_PERMISSION)
        }

        /*  toggleButton!!.setOnCheckedChangeListener { buttonView, isChecked ->
              if (isChecked) {
                  progressBar!!.visibility = View.VISIBLE
                  progressBar!!.isIndeterminate = true
                  ActivityCompat.requestPermissions(this@OfflineActivity,
                          arrayOf(Manifest.permission.RECORD_AUDIO),
                          REQUEST_RECORD_PERMISSION)
              } else {
                  progressBar!!.isIndeterminate = false
                  progressBar!!.visibility = View.INVISIBLE
                  speech!!.stopListening()
              }
          }*/
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_RECORD_PERMISSION -> if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                speech!!.startListening(recognizerIntent)
            } else {
                Toast.makeText(this@OfflineActivity, "Permission Denied!", Toast
                        .LENGTH_SHORT).show()
            }
        }
    }

    //this function was destroying speech so on resuming activity speech recognition was not working again
    /* override fun onStop() {
         super.onStop()
         if (speech != null) {
             speech!!.destroy()
             Log.i(LOG_TAG, "destroy")
         }
     }*/

    override fun onBeginningOfSpeech() {
        Log.i(LOG_TAG, "onBeginningOfSpeech")
        progressBar!!.isIndeterminate = false
        progressBar!!.max = 10
    }

    override fun onBufferReceived(buffer: ByteArray) {
        Log.i(LOG_TAG, "onBufferReceived: " + buffer)
    }

    override fun onEndOfSpeech() {
        Log.i(LOG_TAG, "onEndOfSpeech")
        progressBar!!.isIndeterminate = true
        progressBar.visibility=View.INVISIBLE
    }

    override fun onError(errorCode: Int) {
        val errorMessage = getErrorText(errorCode)
        Log.d(LOG_TAG, "FAILED " + errorMessage)
        txtSpeechInput!!.text = errorMessage
    }

    override fun onEvent(arg0: Int, arg1: Bundle) {
        Log.i(LOG_TAG, "onEvent")
    }

    override fun onPartialResults(arg0: Bundle) {
        Log.i(LOG_TAG, "onPartialResults")
    }

    override fun onReadyForSpeech(arg0: Bundle) {
        Log.i(LOG_TAG, "onReadyForSpeech")
    }

    override fun onResults(results: Bundle) {
        Log.i(LOG_TAG, "onResults")
        val matches = results
                .getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
        var text = matches[0]
      /*  for (result in matches!!)
            text += result + "\n"
*/
        txtSpeechInput!!.text = text
    }

    override fun onRmsChanged(rmsdB: Float) {
        Log.i(LOG_TAG, "onRmsChanged: " + rmsdB)
        progressBar!!.progress = rmsdB.toInt()
    }

       companion object {

           private val REQUEST_RECORD_PERMISSION = 100

           fun getErrorText(errorCode: Int): String {
               val message: String
               when (errorCode) {
                   SpeechRecognizer.ERROR_AUDIO -> message = "Audio recording error"
                   SpeechRecognizer.ERROR_CLIENT -> message = "Client side error"
                   SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> message = "Insufficient permissions"
                   SpeechRecognizer.ERROR_NETWORK -> message = "Network error"
                   SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> message = "Network timeout"
                   SpeechRecognizer.ERROR_NO_MATCH -> message = "No match"
                   SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> message = "RecognitionService busy"
                   SpeechRecognizer.ERROR_SERVER -> message = "error from server"
                   SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> message = "No speech input"
                   else -> message = "Didn't understand, please try again."
               }
               return message
           }
       }
}
