package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ai.GemmaEngine
import com.example.data.model.*
import com.example.data.repository.MiniOSRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.UUID
import kotlin.math.*

class MiniOSViewModel : ViewModel() {

    val repository = MiniOSRepository()
    private val gemmaEngine = GemmaEngine(repository)

    val currentApp: StateFlow<SubApp> = repository.currentApp
    val chatMessages: StateFlow<List<ChatMessage>> = repository.chatMessages

    // Phone
    val contacts: StateFlow<List<Contact>> = repository.contacts
    val callLogs: StateFlow<List<CallLog>> = repository.callLogs
    val voicemails: StateFlow<List<VoicemailItem>> = repository.voicemails
    val activeCall: StateFlow<ActionCard.CallCard?> = repository.activeCall
    val isCallMuted: StateFlow<Boolean> = repository.isCallMuted
    val isSpeakerOn: StateFlow<Boolean> = repository.isSpeakerOn

    // Messages
    val conversations: StateFlow<List<Conversation>> = repository.conversations
    private val _selectedConversationId = MutableStateFlow<String?>(null)
    val selectedConversationId: StateFlow<String?> = _selectedConversationId.asStateFlow()

    val currentConversationMessages: StateFlow<List<SmsMessage>> = combine(
        _selectedConversationId,
        repository.activeConversationMessages
    ) { convId, map ->
        if (convId != null) map[convId] ?: emptyList() else emptyList()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Calendar
    val calendarEvents: StateFlow<List<CalendarEvent>> = repository.calendarEvents

    // Files
    val files: StateFlow<List<OsFile>> = repository.files
    val currentDirectory: StateFlow<String> = repository.currentDirectory

    private val _viewingFile = MutableStateFlow<OsFile?>(null)
    val viewingFile: StateFlow<OsFile?> = _viewingFile.asStateFlow()

    // Notes
    val notes: StateFlow<List<OsNote>> = repository.notes

    // Automations
    val routines: StateFlow<List<AutomationRoutine>> = repository.routines

    // Browser
    val browserTabs: StateFlow<List<BrowserTab>> = repository.browserTabs
    val activeTabId: StateFlow<String> = repository.activeTabId
    val browserBookmarks: StateFlow<List<WebBookmark>> = repository.browserBookmarks
    val isDesktopSite: StateFlow<Boolean> = repository.isDesktopSite

    val currentBrowserUrl: StateFlow<String> = combine(
        browserTabs,
        activeTabId
    ) { tabs, id ->
        tabs.find { it.id == id }?.url ?: "https://en.m.wikipedia.org"
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "https://en.m.wikipedia.org")
    val browserUrl: StateFlow<String> = currentBrowserUrl

    // Clock
    val alarms: StateFlow<List<AlarmItem>> = repository.alarms
    val worldClocks: StateFlow<List<WorldClockItem>> = repository.worldClocks

    // Weather
    val weatherCities: StateFlow<List<String>> = repository.weatherCities
    val selectedCity: StateFlow<String> = repository.selectedCity
    val weatherInfo: StateFlow<WeatherInfo?> = repository.weatherInfo

    // Media Gallery
    val mediaItems: StateFlow<List<MediaItem>> = repository.mediaItems

    // Voice Recorder
    val audioMemos: StateFlow<List<AudioMemo>> = repository.audioMemos

    // Maps & Navigation
    val places: StateFlow<List<MapPlace>> = repository.places
    val activeRoute: StateFlow<MapPlace?> = repository.activeRoute

    // Health & Fitness
    val healthMetrics: StateFlow<HealthMetrics> = repository.healthMetrics

    // System Settings
    val settings: StateFlow<OsSettings> = repository.settings

    // Calculator State & History
    private val _calcExpression = MutableStateFlow("")
    val calcExpression: StateFlow<String> = _calcExpression.asStateFlow()

    private val _calcResult = MutableStateFlow("0")
    val calcResult: StateFlow<String> = _calcResult.asStateFlow()

    private val _calcHistory = MutableStateFlow<List<String>>(emptyList())
    val calcHistory: StateFlow<List<String>> = _calcHistory.asStateFlow()

    // SLM Neural Engine Models
    val availableSlmModels: StateFlow<List<SlmModelInfo>> = repository.availableSlmModels
    val activeSlmModelId: StateFlow<String> = repository.activeSlmModelId
    val activeSlmModel: StateFlow<SlmModelInfo> = repository.activeSlmModel

    private val _isProcessingAI = MutableStateFlow(false)
    val isProcessingAI: StateFlow<Boolean> = _isProcessingAI.asStateFlow()

    fun switchApp(app: SubApp) {
        repository.navigateTo(app)
    }

    fun sendChatPrompt(prompt: String) {
        if (prompt.isBlank()) return
        val userMsg = ChatMessage(
            id = UUID.randomUUID().toString(),
            text = prompt,
            isUser = true
        )
        repository.addChatMessage(userMsg)

        viewModelScope.launch {
            _isProcessingAI.value = true
            try {
                val response = gemmaEngine.processUserPrompt(prompt)
                repository.addChatMessage(response)
            } finally {
                _isProcessingAI.value = false
            }
        }
    }

    // Phone actions
    fun startCall(contactName: String, phoneNumber: String) {
        repository.startCall(contactName, phoneNumber)
    }

    fun endCall() {
        repository.endCall()
    }

    fun toggleMute() {
        repository.toggleMute()
    }

    fun toggleSpeaker() {
        repository.toggleSpeaker()
    }

    fun addContact(name: String, phone: String, email: String) {
        repository.addContact(name, phone, email)
    }

    fun toggleVoicemailPlayback(id: String) {
        repository.toggleVoicemailPlayback(id)
    }

    fun deleteVoicemail(id: String) {
        repository.deleteVoicemail(id)
    }

    // Messaging actions
    fun selectConversation(conversationId: String?) {
        _selectedConversationId.value = conversationId
    }

    fun sendSms(recipientName: String, phoneNumber: String, body: String, attachmentUrl: String? = null) {
        if (body.isBlank() && attachmentUrl == null) return
        repository.sendMessage(recipientName, phoneNumber, body, attachmentUrl)
    }

    fun addReaction(messageId: String, reaction: String) {
        val convId = _selectedConversationId.value ?: return
        repository.addReactionToMessage(convId, messageId, reaction)
    }

    // Calendar actions
    fun addCalendarEvent(title: String, date: String, time: String, category: String = "Personal") {
        repository.addCalendarEvent(title, date, time, category)
    }

    fun deleteCalendarEvent(id: String) {
        repository.deleteCalendarEvent(id)
    }

    // File actions
    fun createFile(name: String, content: String = "") {
        repository.createFile(name, repository.currentDirectory.value, content)
    }

    fun createFile(name: String, path: String, content: String) {
        repository.createFile(name, path, content)
    }

    fun openFile(file: OsFile) {
        _viewingFile.value = file
    }

    fun closeFile() {
        _viewingFile.value = null
    }

    fun saveFile(fileId: String, newContent: String) {
        repository.updateFileContent(fileId, newContent)
        _viewingFile.value = _viewingFile.value?.copy(content = newContent)
    }

    fun deleteFile(fileId: String) {
        repository.deleteFile(fileId)
        if (_viewingFile.value?.id == fileId) {
            _viewingFile.value = null
        }
    }

    // Notes actions
    fun addNote(title: String, content: String, tag: String = "General", color: Long = 0xFF1E293B, checklist: List<NoteCheckItem> = emptyList()) {
        repository.addNote(title, content, tag, color, checklist)
    }

    fun toggleChecklistItem(noteId: String, checkItemId: String) {
        repository.toggleChecklistItem(noteId, checkItemId)
    }

    fun deleteNote(id: String) {
        repository.deleteNote(id)
    }

    fun togglePinNote(id: String) {
        repository.togglePinNote(id)
    }

    // Browser actions
    fun navigateBrowser(url: String) {
        repository.setBrowserUrl(url)
    }

    fun createBrowserTab(url: String = "https://en.m.wikipedia.org") {
        repository.createBrowserTab(url)
    }

    fun switchBrowserTab(tabId: String) {
        repository.switchBrowserTab(tabId)
    }

    fun closeBrowserTab(tabId: String) {
        repository.closeBrowserTab(tabId)
    }

    fun toggleBookmark(title: String, url: String) {
        repository.toggleBookmark(title, url)
    }

    fun toggleDesktopSite() {
        repository.toggleDesktopSite()
    }

    // Routine actions
    fun runRoutine(routine: AutomationRoutine) {
        viewModelScope.launch {
            sendChatPrompt(routine.triggerPhrase)
            repository.navigateTo(SubApp.AI_HUB)
        }
    }

    // Clock actions
    fun addAlarm(time: String, label: String, repeat: String = "Every day") {
        repository.addAlarm(time, label, repeat)
    }

    fun toggleAlarm(id: String) {
        repository.toggleAlarm(id)
    }

    fun deleteAlarm(id: String) {
        repository.deleteAlarm(id)
    }

    // Weather actions
    fun selectWeatherCity(cityName: String) {
        repository.setWeatherCity(cityName)
    }

    fun toggleTempUnit() {
        repository.toggleTempUnit()
    }

    // Media Gallery actions
    fun addPhoto(title: String) {
        repository.addPhoto(title)
    }

    fun deleteMedia(id: String) {
        repository.deleteMedia(id)
    }

    // Voice Recorder actions
    fun addAudioMemo(title: String) {
        repository.addAudioMemo(title)
    }

    fun togglePlayMemo(id: String) {
        repository.togglePlayMemo(id)
    }

    fun cyclePlaybackSpeed(id: String) {
        repository.cyclePlaybackSpeed(id)
    }

    fun deleteMemo(id: String) {
        repository.deleteMemo(id)
    }

    // Maps actions
    fun selectRoute(place: MapPlace?) {
        repository.selectRoute(place)
    }

    // Health actions
    fun logWaterCup() {
        repository.logWaterCup()
    }

    fun logSteps(amount: Int) {
        repository.logSteps(amount)
    }

    fun startWorkout(type: String) {
        repository.startWorkout(type)
    }

    fun stopWorkout() {
        repository.stopWorkout()
    }

    // Settings actions
    fun toggleDarkMode() {
        repository.toggleDarkMode()
    }

    fun toggleTurboMode() {
        repository.toggleTurboMode()
    }

    fun toggleSpamShield() {
        repository.toggleSpamShield()
    }

    fun updateSettings(newSettings: OsSettings) {
        repository.updateSettings(newSettings)
    }

    // Calculator actions
    fun calcKeyPress(key: String) {
        when (key) {
            "C" -> {
                _calcExpression.value = ""
                _calcResult.value = "0"
            }
            "DEL" -> {
                if (_calcExpression.value.isNotEmpty()) {
                    _calcExpression.value = _calcExpression.value.dropLast(1)
                }
            }
            "=" -> {
                evaluateCalc()
            }
            "sin" -> applyScientificUnary { sin(Math.toRadians(it)) }
            "cos" -> applyScientificUnary { cos(Math.toRadians(it)) }
            "tan" -> applyScientificUnary { tan(Math.toRadians(it)) }
            "√" -> applyScientificUnary { sqrt(it) }
            "ln" -> applyScientificUnary { ln(it) }
            "π" -> _calcExpression.value = _calcExpression.value + "3.14159"
            else -> {
                _calcExpression.value = _calcExpression.value + key
            }
        }
    }

    private fun applyScientificUnary(op: (Double) -> Double) {
        val curr = _calcExpression.value.toDoubleOrNull() ?: _calcResult.value.toDoubleOrNull()
        if (curr != null) {
            val res = op(curr)
            val formatted = if (res % 1.0 == 0.0) res.toLong().toString() else "%.4f".format(res)
            _calcResult.value = formatted
            _calcHistory.value = listOf("f($curr) = $formatted") + _calcHistory.value
        }
    }

    private fun evaluateCalc() {
        val expr = _calcExpression.value
        try {
            val clean = expr.replace("×", "*").replace("÷", "/")
            val match = "([\\d.]+)\\s*([+\\-*/^])\\s*([\\d.]+)".toRegex().find(clean)
            if (match != null) {
                val a = match.groupValues[1].toDouble()
                val op = match.groupValues[2]
                val b = match.groupValues[3].toDouble()
                val res = when (op) {
                    "+" -> a + b
                    "-" -> a - b
                    "*" -> a * b
                    "/" -> if (b != 0.0) a / b else 0.0
                    "^" -> a.pow(b)
                    else -> 0.0
                }
                val formatted = if (res % 1.0 == 0.0) res.toLong().toString() else "%.2f".format(res)
                _calcResult.value = formatted
                _calcHistory.value = listOf("$expr = $formatted") + _calcHistory.value
            } else {
                _calcResult.value = expr
            }
        } catch (e: Exception) {
            _calcResult.value = "Error"
        }
    }

    // Maps Actions
    fun addPlace(name: String, category: String, address: String) {
        repository.addPlace(name, category, address)
    }

    // SLM Neural Engine Model Management
    private var downloadJob: kotlinx.coroutines.Job? = null

    fun startSlmDownload(modelId: String) {
        downloadJob?.cancel()
        downloadJob = viewModelScope.launch {
            repository.downloadSlmModel(modelId)
            var currentProgress = 0.08f
            while (currentProgress < 1.0f) {
                kotlinx.coroutines.delay(120)
                currentProgress += (8..15).random() / 100f
                if (currentProgress >= 1.0f) {
                    repository.completeSlmModelDownload(modelId)
                    break
                } else {
                    repository.updateSlmModelProgress(modelId, currentProgress)
                }
            }
        }
    }

    fun cancelSlmDownload(modelId: String) {
        downloadJob?.cancel()
        repository.cancelSlmDownload(modelId)
    }

    fun deleteSlmModel(modelId: String) {
        downloadJob?.cancel()
        repository.deleteSlmModel(modelId)
    }

    fun activateSlmModel(modelId: String) {
        repository.setActiveSlmModel(modelId)
    }

    fun instantInstallGemma4E2B() {
        downloadJob?.cancel()
        repository.completeSlmModelDownload("gemma_4_e2b_slm")
        repository.setActiveSlmModel("gemma_4_e2b_slm")
    }

    fun instantInstallLlama32() {
        downloadJob?.cancel()
        repository.completeSlmModelDownload("llama_3_2_3b_128k")
        repository.setActiveSlmModel("llama_3_2_3b_128k")
    }

    fun instantInstallQwen25() {
        downloadJob?.cancel()
        repository.completeSlmModelDownload("qwen_2_5_3b")
        repository.setActiveSlmModel("qwen_2_5_3b")
    }
}
