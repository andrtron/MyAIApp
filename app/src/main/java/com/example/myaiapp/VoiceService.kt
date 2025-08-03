package com.example.myaiapp

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.speech.tts.TextToSpeech
import org.vosk.Model
import org.vosk.Recognizer
import org.vosk.android.SpeechService
import ai.picovoice.porcupine.PorcupineManager

class VoiceService : Service(), TextToSpeech.OnInitListener {
    private lateinit var tts: TextToSpeech
    private var porcupine: PorcupineManager? = null
    private var speechService: SpeechService? = null

    override fun onCreate() {
        super.onCreate()
        tts = TextToSpeech(this, this)
        initWakeWord()
    }

    private fun initWakeWord() {
        // Путь к модели «айла» (нужно положить файл в assets и скопировать путь)
        val keywordPath = "path/to/russian-porcupine.ppn"
        porcupine = PorcupineManager.create(this, keywordPath, 1) { _ ->
            startRecognition()
        }
        porcupine?.start()
    }

    private fun startRecognition() {
        val model = Model("model/vosk-model-small-ru")
        val rec = Recognizer(model, 16000f)
        speechService = SpeechService(rec, 16000f) { hyp ->
            val text = hyp.text
            when {
                text.contains("айла стоп", true) -> stopSelf()
                else -> handleQuery(text)
            }
        }
        speechService?.startListening()
    }

    private fun handleQuery(query: String) {
        val response = if (offlineCanAnswer(query)) offlineAnswer(query)
                       else fetchOnline(query)
        speak(response)
        broadcastChat(query, response)
    }

    private fun offlineCanAnswer(q: String): Boolean = false
    private fun offlineAnswer(q: String): String = "Не знаю"
    private fun fetchOnline(q: String): String {
        return "Онлайн ответ"
    }

    private fun speak(text: String) {
        tts.speak(text, TextToSpeech.QUEUE_ADD, null, null)
    }

    private fun broadcastChat(user: String, bot: String) {
        Intent("CHAT_UPDATE").apply {
            putExtra("user", user)
            putExtra("bot", bot)
            sendBroadcast(this)
        }
    }

    override fun onInit(status: Int) {}
    override fun onDestroy() {
        super.onDestroy()
        porcupine?.stop()
        speechService?.stop()
        tts.shutdown()
    }
    override fun onBind(intent: Intent?): IBinder? = null
}
