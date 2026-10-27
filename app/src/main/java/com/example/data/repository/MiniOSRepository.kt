package com.example.data.repository

import com.example.data.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.util.*

class MiniOSRepository {

    private val timeFormat = SimpleDateFormat("h:mm a", Locale.getDefault())
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    // Active screen navigation
    private val _currentApp = MutableStateFlow(SubApp.AI_HUB)
    val currentApp: StateFlow<SubApp> = _currentApp.asStateFlow()

    // Chat messages
    private val _chatMessages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val chatMessages: StateFlow<List<ChatMessage>> = _chatMessages.asStateFlow()

    // Phone state
    private val _contacts = MutableStateFlow<List<Contact>>(emptyList())
    val contacts: StateFlow<List<Contact>> = _contacts.asStateFlow()

    private val _callLogs = MutableStateFlow<List<CallLog>>(emptyList())
    val callLogs: StateFlow<List<CallLog>> = _callLogs.asStateFlow()

    private val _voicemails = MutableStateFlow<List<VoicemailItem>>(emptyList())
    val voicemails: StateFlow<List<VoicemailItem>> = _voicemails.asStateFlow()

    private val _activeCall = MutableStateFlow<ActionCard.CallCard?>(null)
    val activeCall: StateFlow<ActionCard.CallCard?> = _activeCall.asStateFlow()

    private val _isCallMuted = MutableStateFlow(false)
    val isCallMuted: StateFlow<Boolean> = _isCallMuted.asStateFlow()

    private val _isSpeakerOn = MutableStateFlow(false)
    val isSpeakerOn: StateFlow<Boolean> = _isSpeakerOn.asStateFlow()

    // Messages state
    private val _conversations = MutableStateFlow<List<Conversation>>(emptyList())
    val conversations: StateFlow<List<Conversation>> = _conversations.asStateFlow()

    private val _activeConversationMessages = MutableStateFlow<Map<String, List<SmsMessage>>>(emptyMap())
    val activeConversationMessages: StateFlow<Map<String, List<SmsMessage>>> = _activeConversationMessages.asStateFlow()

    // Calendar state
    private val _calendarEvents = MutableStateFlow<List<CalendarEvent>>(emptyList())
    val calendarEvents: StateFlow<List<CalendarEvent>> = _calendarEvents.asStateFlow()

    // Files state
    private val _files = MutableStateFlow<List<OsFile>>(emptyList())
    val files: StateFlow<List<OsFile>> = _files.asStateFlow()

    private val _currentDirectory = MutableStateFlow("~/Documents")
    val currentDirectory: StateFlow<String> = _currentDirectory.asStateFlow()

    // Notes state
    private val _notes = MutableStateFlow<List<OsNote>>(emptyList())
    val notes: StateFlow<List<OsNote>> = _notes.asStateFlow()

    // Automations state
    private val _routines = MutableStateFlow<List<AutomationRoutine>>(emptyList())
    val routines: StateFlow<List<AutomationRoutine>> = _routines.asStateFlow()

    // Browser state with Multi-Tabs & Bookmarks
    private val _browserTabs = MutableStateFlow<List<BrowserTab>>(
        listOf(
            BrowserTab("tab_1", "Wikipedia", "https://en.m.wikipedia.org"),
            BrowserTab("tab_2", "Hacker News", "https://news.ycombinator.com")
        )
    )
    val browserTabs: StateFlow<List<BrowserTab>> = _browserTabs.asStateFlow()

    private val _activeTabId = MutableStateFlow("tab_1")
    val activeTabId: StateFlow<String> = _activeTabId.asStateFlow()

    private val _browserBookmarks = MutableStateFlow<List<WebBookmark>>(
        listOf(
            WebBookmark("b1", "Wikipedia", "https://en.m.wikipedia.org"),
            WebBookmark("b2", "Hacker News", "https://news.ycombinator.com"),
            WebBookmark("b3", "GitHub", "https://github.com"),
            WebBookmark("b4", "Android Dev", "https://developer.android.com")
        )
    )
    val browserBookmarks: StateFlow<List<WebBookmark>> = _browserBookmarks.asStateFlow()

    private val _isDesktopSite = MutableStateFlow(false)
    val isDesktopSite: StateFlow<Boolean> = _isDesktopSite.asStateFlow()

    // Clock & Alarms state
    private val _alarms = MutableStateFlow<List<AlarmItem>>(emptyList())
    val alarms: StateFlow<List<AlarmItem>> = _alarms.asStateFlow()

    private val _worldClocks = MutableStateFlow<List<WorldClockItem>>(emptyList())
    val worldClocks: StateFlow<List<WorldClockItem>> = _worldClocks.asStateFlow()

    // Weather state
    private val _weatherCities = MutableStateFlow<List<String>>(
        listOf("San Francisco, CA", "New York, NY", "London, UK", "Tokyo, JP", "Paris, FR")
    )
    val weatherCities: StateFlow<List<String>> = _weatherCities.asStateFlow()

    private val _selectedCity = MutableStateFlow("San Francisco, CA")
    val selectedCity: StateFlow<String> = _selectedCity.asStateFlow()

    private val _weatherInfo = MutableStateFlow<WeatherInfo?>(null)
    val weatherInfo: StateFlow<WeatherInfo?> = _weatherInfo.asStateFlow()

    // Media Gallery state
    private val _mediaItems = MutableStateFlow<List<MediaItem>>(emptyList())
    val mediaItems: StateFlow<List<MediaItem>> = _mediaItems.asStateFlow()

    // Voice Recorder state
    private val _audioMemos = MutableStateFlow<List<AudioMemo>>(emptyList())
    val audioMemos: StateFlow<List<AudioMemo>> = _audioMemos.asStateFlow()

    // Maps state
    private val _places = MutableStateFlow<List<MapPlace>>(emptyList())
    val places: StateFlow<List<MapPlace>> = _places.asStateFlow()

    private val _activeRoute = MutableStateFlow<MapPlace?>(null)
    val activeRoute: StateFlow<MapPlace?> = _activeRoute.asStateFlow()

    // Health & Fitness state
    private val _healthMetrics = MutableStateFlow(
        HealthMetrics(
            stepsToday = 0,
            stepGoal = 10000,
            waterGlasses = 0,
            waterGoal = 8,
            activeMinutes = 0,
            activeGoal = 60,
            caloriesBurned = 0,
            sleepHours = 0f,
            weeklyHistory = emptyList(),
            currentWorkout = null
        )
    )
    val healthMetrics: StateFlow<HealthMetrics> = _healthMetrics.asStateFlow()

    // System Settings state
    private val _settings = MutableStateFlow(OsSettings())
    val settings: StateFlow<OsSettings> = _settings.asStateFlow()

    // On-Device SLM Models State (Real Edge Models: Gemma 4 E2B, Llama 3.2 3B 128K, Qwen 2.5 3B)
    private val gemma4E2b = SlmModelInfo(
        id = "gemma_4_e2b_slm",
        name = "Gemma 4 E2B SLM",
        tag = "Google Next-Gen Edge SLM",
        version = "v4.0-E2B-Edge",
        sizeFormatted = "1.42 GB",
        sizeBytes = 1_524_629_504L,
        downloadedBytes = 1_524_629_504L,
        downloadProgress = 1.0f,
        status = SlmDownloadState.ACTIVE,
        description = "Google's edge SLM with deep multi-step reasoning, phone automation across all 17 sub-apps, and 58.4 tokens/second throughput.",
        parameterCount = "2.1B (Edge-Optimized)",
        quantization = "INT4-Quantized Edge Weights",
        targetLatencyMs = 22,
        memoryRequiredMb = 940,
        tokensPerSec = 58.4f,
        contextWindow = "8K (8,192 tokens)",
        isCurrentActive = true
    )

    private val llama32LongContext = SlmModelInfo(
        id = "llama_3_2_3b_128k",
        name = "Llama 3.2 3B (128K Long Context)",
        tag = "Meta Ultra-Long 128K SLM",
        version = "v3.2-128K-Edge",
        sizeFormatted = "1.85 GB",
        sizeBytes = 1_986_500_000L,
        downloadedBytes = 1_986_500_000L,
        downloadProgress = 1.0f,
        status = SlmDownloadState.DOWNLOADED,
        description = "Meta's long-context SLM with native 128,000 token context window (GQA 8-head). Deep reasoning across long transcripts, documents, and code archives.",
        parameterCount = "3.21B (GQA 8-head)",
        quantization = "INT4-GQA Paged KV",
        targetLatencyMs = 38,
        memoryRequiredMb = 1280,
        tokensPerSec = 42.5f,
        contextWindow = "128K (131,072 tokens)",
        isCurrentActive = false
    )

    private val qwen25Edge = SlmModelInfo(
        id = "qwen_2_5_3b",
        name = "Qwen 2.5 3B (Edge Reasoning)",
        tag = "Math & Code Reasoning SLM",
        version = "v2.5-3B-Edge",
        sizeFormatted = "1.68 GB",
        sizeBytes = 1_803_886_592L,
        downloadedBytes = 1_803_886_592L,
        downloadProgress = 1.0f,
        status = SlmDownloadState.DOWNLOADED,
        description = "Alibaba's high-efficiency reasoning SLM with benchmark-leading score in Python scripting, math solving, algorithmic reasoning, and sub-app control.",
        parameterCount = "3.09B (Dense)",
        quantization = "INT4-AWQ Edge NPU",
        targetLatencyMs = 26,
        memoryRequiredMb = 1080,
        tokensPerSec = 45.2f,
        contextWindow = "32K (32,768 tokens, up to 128K)",
        isCurrentActive = false
    )

    private val _availableSlmModels = MutableStateFlow<List<SlmModelInfo>>(listOf(gemma4E2b, llama32LongContext, qwen25Edge))
    val availableSlmModels: StateFlow<List<SlmModelInfo>> = _availableSlmModels.asStateFlow()

    private val _activeSlmModelId = MutableStateFlow("gemma_4_e2b_slm")
    val activeSlmModelId: StateFlow<String> = _activeSlmModelId.asStateFlow()

    private val _activeSlmModel = MutableStateFlow(gemma4E2b)
    val activeSlmModel: StateFlow<SlmModelInfo> = _activeSlmModel.asStateFlow()

    init {
        seedInitialData()
    }

    private fun seedInitialData() {
        // Clean initial state: zero fake or demo data
        _contacts.value = emptyList()
        _callLogs.value = emptyList()
        _voicemails.value = emptyList()
        _conversations.value = emptyList()
        _activeConversationMessages.value = emptyMap()
        _calendarEvents.value = emptyList()
        _files.value = emptyList()
        _notes.value = emptyList()
        _alarms.value = emptyList()
        _mediaItems.value = emptyList()
        _audioMemos.value = emptyList()
        _places.value = emptyList()

        // Built-in Automations
        _routines.value = listOf(
            AutomationRoutine(
                "r1",
                "Morning Briefing",
                "Runs daily agenda, pulls calendar meetings, and opens the morning news.",
                "wb_sunny",
                listOf(
                    "Pulls upcoming meetings for today",
                    "Checks unread messages from contacts",
                    "Launches browser to morning briefing"
                ),
                "run morning routine"
            ),
            AutomationRoutine(
                "r2",
                "Work Focus Mode",
                "Silences interruptions, opens today's top notes, and prepares work tasks.",
                "laptop",
                listOf(
                    "Engages Do Not Disturb state",
                    "Opens top pinned Architecture notes",
                    "Queues next calendar sync"
                ),
                "focus mode"
            ),
            AutomationRoutine(
                "r3",
                "Battery Saver Shield",
                "Optimizes on-device resources, throttles background polling, and dims display.",
                "battery_saver",
                listOf(
                    "Dims display tone",
                    "Pauses non-critical file indexers",
                    "Keeps Gemma in low-power standby"
                ),
                "battery saver"
            )
        )

        _worldClocks.value = listOf(
            WorldClockItem("w1", "Cupertino (PST)", "-7h", "10:48 AM"),
            WorldClockItem("w2", "New York (EST)", "-4h", "1:48 PM"),
            WorldClockItem("w3", "London (GMT)", "+0h", "6:48 PM"),
            WorldClockItem("w4", "Tokyo (JST)", "+9h", "2:48 AM Tomorrow")
        )

        // Initial Weather Engine
        _weatherInfo.value = WeatherInfo(
            cityName = "San Francisco, CA",
            tempC = 19,
            condition = "Partly Cloudy",
            highC = 22,
            lowC = 14,
            humidityPercent = 64,
            windSpeedKmh = 16,
            uvIndex = 5,
            airQuality = "Good (32 AQI)",
            sunrise = "6:42 AM",
            sunset = "7:18 PM",
            pressureHpa = 1014,
            visibilityKm = 10,
            hourlyList = listOf(
                HourlyForecast("Now", 19, "partly_cloudy"),
                HourlyForecast("12 PM", 21, "sunny"),
                HourlyForecast("2 PM", 22, "sunny"),
                HourlyForecast("4 PM", 20, "cloudy"),
                HourlyForecast("6 PM", 18, "cloudy"),
                HourlyForecast("8 PM", 16, "clear"),
                HourlyForecast("10 PM", 15, "clear")
            ),
            dailyList = listOf(
                DailyForecast("Today", "Partly Cloudy", 22, 14),
                DailyForecast("Tomorrow", "Sunny", 24, 15),
                DailyForecast("Wednesday", "Clear", 23, 14),
                DailyForecast("Thursday", "Breezy", 20, 13),
                DailyForecast("Friday", "Sunny", 25, 16)
            )
        )

        // Clean Initial Welcome Message
        _chatMessages.value = listOf(
            ChatMessage(
                id = "init_welcome",
                text = "Welcome to **MiniOS AI** — your unified personal operating system.\n\nPowered by on-device **Gemma SLM**. All 17 native sub-apps are initialized and ready without demo data. Ask anything or tap a sub-app below.",
                isUser = false,
                telemetry = GemmaTelemetry(
                    latencyMs = 32,
                    tokensPerSec = 48.0f,
                    modelName = "Gemma-2B (On-Device Neural Kernel)"
                )
            )
        )
    }

    // App Navigation
    fun navigateTo(app: SubApp) {
        _currentApp.value = app
    }

    // Chat operations
    fun addChatMessage(message: ChatMessage) {
        _chatMessages.value = _chatMessages.value + message
    }

    fun clearChat() {
        _chatMessages.value = emptyList()
    }

    // Phone operations
    fun startCall(contactName: String, phoneNumber: String) {
        _activeCall.value = ActionCard.CallCard(
            contactName = contactName,
            phoneNumber = phoneNumber,
            status = CallStatus.CONNECTED,
            durationSeconds = 0
        )
        val newLog = CallLog(
            id = UUID.randomUUID().toString(),
            contactName = contactName,
            phoneNumber = phoneNumber,
            type = CallType.OUTGOING,
            timestampFormatted = "Just now",
            durationFormatted = "Ongoing"
        )
        _callLogs.value = listOf(newLog) + _callLogs.value
    }

    fun endCall() {
        val current = _activeCall.value
        if (current != null) {
            _activeCall.value = current.copy(status = CallStatus.ENDED)
        }
        _isCallMuted.value = false
        _isSpeakerOn.value = false
    }

    fun dismissCallCard() {
        _activeCall.value = null
    }

    fun toggleMute() {
        _isCallMuted.value = !_isCallMuted.value
    }

    fun toggleSpeaker() {
        _isSpeakerOn.value = !_isSpeakerOn.value
    }

    fun addContact(name: String, phoneNumber: String, email: String) {
        val newContact = Contact(
            id = UUID.randomUUID().toString(),
            name = name,
            phoneNumber = phoneNumber,
            email = email,
            avatarColorHex = 0xFF00E5FF,
            isFavorite = false
        )
        _contacts.value = _contacts.value + newContact
    }

    fun toggleVoicemailPlayback(id: String) {
        _voicemails.value = _voicemails.value.map {
            if (it.id == id) it.copy(isPlaying = !it.isPlaying, isRead = true) else it.copy(isPlaying = false)
        }
    }

    fun deleteVoicemail(id: String) {
        _voicemails.value = _voicemails.value.filter { it.id != id }
    }

    // Messages operations
    fun sendMessage(recipientName: String, phoneNumber: String, body: String, attachmentUrl: String? = null) {
        val existingConv = _conversations.value.find { 
            it.contactName.equals(recipientName, ignoreCase = true) || it.phoneNumber == phoneNumber 
        }

        val convId = existingConv?.id ?: run {
            val newId = UUID.randomUUID().toString()
            val newConv = Conversation(
                id = newId,
                contactName = recipientName,
                phoneNumber = phoneNumber,
                lastMessage = body,
                timestampFormatted = timeFormat.format(Date()),
                unreadCount = 0
            )
            _conversations.value = listOf(newConv) + _conversations.value
            newId
        }

        val newMsg = SmsMessage(
            id = UUID.randomUUID().toString(),
            conversationId = convId,
            senderName = "Me",
            body = body,
            timestampFormatted = timeFormat.format(Date()),
            isFromMe = true,
            attachmentUrl = attachmentUrl
        )

        val updatedMap = _activeConversationMessages.value.toMutableMap()
        val currentList = updatedMap[convId] ?: emptyList()
        updatedMap[convId] = currentList + newMsg
        _activeConversationMessages.value = updatedMap

        _conversations.value = _conversations.value.map {
            if (it.id == convId) {
                it.copy(lastMessage = body, timestampFormatted = timeFormat.format(Date()))
            } else it
        }
    }

    fun addReactionToMessage(convId: String, msgId: String, reaction: String) {
        val currentMap = _activeConversationMessages.value.toMutableMap()
        val list = currentMap[convId] ?: return
        currentMap[convId] = list.map {
            if (it.id == msgId) it.copy(reaction = if (it.reaction == reaction) null else reaction) else it
        }
        _activeConversationMessages.value = currentMap
    }

    // Calendar operations
    fun addCalendarEvent(title: String, date: String, time: String, category: String = "Personal"): CalendarEvent {
        val event = CalendarEvent(
            id = UUID.randomUUID().toString(),
            title = title,
            dateIso = date,
            timeFormatted = time,
            category = category,
            location = "MiniOS Device",
            colorHex = when (category.lowercase()) {
                "work" -> 0xFF00E5FF
                "health" -> 0xFF10B981
                "urgent" -> 0xFFF43F5E
                else -> 0xFF6366F1
            }
        )
        _calendarEvents.value = listOf(event) + _calendarEvents.value
        return event
    }

    fun deleteCalendarEvent(id: String) {
        _calendarEvents.value = _calendarEvents.value.filter { it.id != id }
    }

    // Files operations
    fun createFile(name: String, directory: String = "~/Documents", content: String = ""): OsFile {
        val ext = name.substringAfterLast('.', "txt")
        val newFile = OsFile(
            id = UUID.randomUUID().toString(),
            name = name,
            path = directory,
            isDirectory = false,
            sizeBytes = (content.length * 2).toLong().coerceAtLeast(64),
            content = content,
            modifiedFormatted = "Just now",
            fileType = ext
        )
        _files.value = listOf(newFile) + _files.value
        return newFile
    }

    fun updateFileContent(fileId: String, newContent: String) {
        _files.value = _files.value.map {
            if (it.id == fileId) {
                it.copy(
                    content = newContent,
                    sizeBytes = (newContent.length * 2).toLong(),
                    modifiedFormatted = "Just now"
                )
            } else it
        }
    }

    fun deleteFile(fileId: String) {
        _files.value = _files.value.filter { it.id != fileId }
    }

    // Notes operations
    fun addNote(title: String, content: String, tag: String = "General", color: Long = 0xFF1E293B, checklist: List<NoteCheckItem> = emptyList()): OsNote {
        val note = OsNote(
            id = UUID.randomUUID().toString(),
            title = title,
            content = content,
            tag = tag,
            isPinned = false,
            modifiedFormatted = "Just now",
            colorHex = color,
            checklist = checklist
        )
        _notes.value = listOf(note) + _notes.value
        return note
    }

    fun toggleChecklistItem(noteId: String, checkItemId: String) {
        _notes.value = _notes.value.map { note ->
            if (note.id == noteId) {
                val updatedChecklist = note.checklist.map { item ->
                    if (item.id == checkItemId) item.copy(isChecked = !item.isChecked) else item
                }
                note.copy(checklist = updatedChecklist)
            } else note
        }
    }

    fun deleteNote(id: String) {
        _notes.value = _notes.value.filter { it.id != id }
    }

    fun togglePinNote(id: String) {
        _notes.value = _notes.value.map {
            if (it.id == id) it.copy(isPinned = !it.isPinned) else it
        }
    }

    // Browser operations
    fun setBrowserUrl(url: String) {
        val formatted = if (!url.startsWith("http://") && !url.startsWith("https://")) {
            if (url.contains(".") && !url.contains(" ")) "https://$url" else "https://duckduckgo.com/?q=${url.replace(" ", "+")}"
        } else url

        val activeId = _activeTabId.value
        _browserTabs.value = _browserTabs.value.map {
            if (it.id == activeId) it.copy(url = formatted, title = formatted.substringAfter("://").substringBefore("/")) else it
        }
    }

    fun createBrowserTab(url: String = "https://en.m.wikipedia.org") {
        val newTab = BrowserTab(
            id = "tab_${System.currentTimeMillis()}",
            title = "New Tab",
            url = url
        )
        _browserTabs.value = _browserTabs.value + newTab
        _activeTabId.value = newTab.id
    }

    fun switchBrowserTab(tabId: String) {
        _activeTabId.value = tabId
    }

    fun closeBrowserTab(tabId: String) {
        if (_browserTabs.value.size > 1) {
            _browserTabs.value = _browserTabs.value.filter { it.id != tabId }
            if (_activeTabId.value == tabId) {
                _activeTabId.value = _browserTabs.value.first().id
            }
        }
    }

    fun toggleBookmark(title: String, url: String) {
        val existing = _browserBookmarks.value.find { it.url == url }
        if (existing != null) {
            _browserBookmarks.value = _browserBookmarks.value.filter { it.id != existing.id }
        } else {
            val bm = WebBookmark(UUID.randomUUID().toString(), title, url)
            _browserBookmarks.value = _browserBookmarks.value + bm
        }
    }

    fun toggleDesktopSite() {
        _isDesktopSite.value = !_isDesktopSite.value
    }

    // Clock operations
    fun addAlarm(time: String, label: String, repeat: String = "Every day"): AlarmItem {
        val alarm = AlarmItem(UUID.randomUUID().toString(), time, label, true, repeat)
        _alarms.value = listOf(alarm) + _alarms.value
        return alarm
    }

    fun toggleAlarm(id: String) {
        _alarms.value = _alarms.value.map {
            if (it.id == id) it.copy(isEnabled = !it.isEnabled) else it
        }
    }

    fun deleteAlarm(id: String) {
        _alarms.value = _alarms.value.filter { it.id != id }
    }

    // Weather operations
    fun setWeatherCity(cityName: String) {
        val resolved = _weatherCities.value.find { it.contains(cityName, ignoreCase = true) } ?: cityName
        _selectedCity.value = resolved
        val (temp, cond) = when {
            resolved.contains("New York", ignoreCase = true) -> 16 to "Sunny"
            resolved.contains("London", ignoreCase = true) -> 13 to "Overcast"
            resolved.contains("Tokyo", ignoreCase = true) -> 21 to "Clear"
            resolved.contains("Paris", ignoreCase = true) -> 15 to "Showers"
            else -> 19 to "Partly Cloudy"
        }
        _weatherInfo.value = _weatherInfo.value?.copy(cityName = resolved, tempC = temp, condition = cond)
    }

    // Media Gallery operations
    fun addPhoto(title: String, album: String = "Camera"): MediaItem {
        val item = MediaItem(
            id = UUID.randomUUID().toString(),
            title = title,
            album = album,
            dateFormatted = "Just now",
            colorTintHex = listOf(0xFF00E5FF, 0xFF6366F1, 0xFF10B981, 0xFFF59E0B, 0xFFEC4899).random()
        )
        _mediaItems.value = listOf(item) + _mediaItems.value
        return item
    }

    fun deleteMedia(id: String) {
        _mediaItems.value = _mediaItems.value.filter { it.id != id }
    }

    // Voice Recorder operations
    fun addAudioMemo(title: String, duration: String = "01:15"): AudioMemo {
        val memo = AudioMemo(
            id = UUID.randomUUID().toString(),
            title = title,
            durationFormatted = duration,
            recordedDate = "Just now",
            sizeFormatted = "950 KB",
            transcript = "On-device memo recorded by user in MiniOS."
        )
        _audioMemos.value = listOf(memo) + _audioMemos.value
        return memo
    }

    fun togglePlayMemo(id: String) {
        _audioMemos.value = _audioMemos.value.map {
            if (it.id == id) it.copy(isPlaying = !it.isPlaying) else it.copy(isPlaying = false)
        }
    }

    fun cyclePlaybackSpeed(id: String) {
        _audioMemos.value = _audioMemos.value.map {
            if (it.id == id) {
                val nextSpeed = when (it.playbackSpeed) {
                    1.0f -> 1.5f
                    1.5f -> 2.0f
                    else -> 1.0f
                }
                it.copy(playbackSpeed = nextSpeed)
            } else it
        }
    }

    fun deleteMemo(id: String) {
        _audioMemos.value = _audioMemos.value.filter { it.id != id }
    }

    // Maps operations
    fun selectRoute(place: MapPlace?) {
        _activeRoute.value = place
    }

    // Health operations
    fun logWaterCup() {
        val current = _healthMetrics.value
        _healthMetrics.value = current.copy(waterGlasses = current.waterGlasses + 1)
    }

    fun logSteps(amount: Int) {
        val current = _healthMetrics.value
        _healthMetrics.value = current.copy(
            stepsToday = current.stepsToday + amount,
            caloriesBurned = current.caloriesBurned + (amount * 0.04).toInt()
        )
    }

    fun startWorkout(type: String) {
        val current = _healthMetrics.value
        _healthMetrics.value = current.copy(
            currentWorkout = ActiveWorkoutSession(type = type, durationSeconds = 0, caloriesBurned = 0, isActive = true)
        )
    }

    fun stopWorkout() {
        val current = _healthMetrics.value
        val session = current.currentWorkout
        if (session != null) {
            _healthMetrics.value = current.copy(
                caloriesBurned = current.caloriesBurned + session.caloriesBurned,
                activeMinutes = current.activeMinutes + (session.durationSeconds / 60),
                currentWorkout = null
            )
        }
    }

    // Settings operations
    fun updateSettings(newSettings: OsSettings) {
        _settings.value = newSettings
    }

    fun toggleDarkMode() {
        val current = _settings.value
        _settings.value = current.copy(isDarkTheme = !current.isDarkTheme)
    }

    fun toggleTurboMode() {
        val current = _settings.value
        _settings.value = current.copy(isGemmaTurboMode = !current.isGemmaTurboMode)
    }

    fun toggleTempUnit() {
        val current = _settings.value
        _settings.value = current.copy(isCelsius = !current.isCelsius)
    }

    fun toggleSpamShield() {
        val current = _settings.value
        _settings.value = current.copy(spamCallFilterEnabled = !current.spamCallFilterEnabled)
    }

    // Maps operations
    fun addPlace(name: String, category: String, address: String, distance: String = "1.0 km", etaMinutes: Int = 10): MapPlace {
        val place = MapPlace(UUID.randomUUID().toString(), name, category, address, distance, etaMinutes)
        _places.value = listOf(place) + _places.value
        return place
    }

    // SLM Neural Engine Model Management
    fun downloadSlmModel(modelId: String) {
        _availableSlmModels.value = _availableSlmModels.value.map { model ->
            if (model.id == modelId) {
                model.copy(
                    status = SlmDownloadState.DOWNLOADING,
                    downloadProgress = 0.08f,
                    downloadedBytes = (model.sizeBytes * 0.08f).toLong()
                )
            } else model
        }
    }

    fun updateSlmModelProgress(modelId: String, progress: Float) {
        val clamped = progress.coerceIn(0f, 1f)
        _availableSlmModels.value = _availableSlmModels.value.map { model ->
            if (model.id == modelId) {
                val newStatus = if (clamped >= 1f) SlmDownloadState.DOWNLOADED else SlmDownloadState.DOWNLOADING
                model.copy(
                    status = newStatus,
                    downloadProgress = clamped,
                    downloadedBytes = (model.sizeBytes * clamped).toLong()
                )
            } else model
        }
    }

    fun completeSlmModelDownload(modelId: String) {
        _availableSlmModels.value = _availableSlmModels.value.map { model ->
            if (model.id == modelId) {
                model.copy(
                    status = SlmDownloadState.DOWNLOADED,
                    downloadProgress = 1.0f,
                    downloadedBytes = model.sizeBytes
                )
            } else model
        }
    }

    fun cancelSlmDownload(modelId: String) {
        _availableSlmModels.value = _availableSlmModels.value.map { model ->
            if (model.id == modelId) {
                model.copy(
                    status = SlmDownloadState.NOT_DOWNLOADED,
                    downloadProgress = 0f,
                    downloadedBytes = 0L
                )
            } else model
        }
    }

    fun deleteSlmModel(modelId: String) {
        if (_activeSlmModelId.value == modelId) {
            setActiveSlmModel("gemma_4_e2b_slm")
        }
        _availableSlmModels.value = _availableSlmModels.value.map { model ->
            if (model.id == modelId) {
                model.copy(
                    status = SlmDownloadState.NOT_DOWNLOADED,
                    downloadProgress = 0f,
                    downloadedBytes = 0L,
                    isCurrentActive = false
                )
            } else model
        }
        _activeSlmModel.value = _availableSlmModels.value.find { it.isCurrentActive } ?: gemma4E2b
    }

    fun setActiveSlmModel(modelId: String) {
        _activeSlmModelId.value = modelId
        _availableSlmModels.value = _availableSlmModels.value.map { model ->
            if (model.id == modelId) {
                model.copy(status = SlmDownloadState.ACTIVE, isCurrentActive = true)
            } else {
                val updatedStatus = if (model.status == SlmDownloadState.ACTIVE) SlmDownloadState.DOWNLOADED else model.status
                model.copy(status = updatedStatus, isCurrentActive = false)
            }
        }
        _activeSlmModel.value = _availableSlmModels.value.find { it.id == modelId } ?: gemma4E2b
    }
}
