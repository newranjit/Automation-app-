package com.example.data.model

import androidx.compose.ui.graphics.Color

enum class SubApp(val label: String, val iconName: String, val category: String) {
    AI_HUB("AI Hub", "chat", "Core"),
    PHONE("Phone", "phone", "Communication"),
    MESSAGES("Messages", "message", "Communication"),
    CALENDAR("Calendar", "calendar", "Productivity"),
    BROWSER("Browser", "public", "Utilities"),
    FILES("Files", "folder", "Productivity"),
    NOTES("Notes", "edit_note", "Productivity"),
    TOOLS("Tools", "calculate", "Utilities"),
    AUTOMATIONS("Automations", "auto_mode", "Core"),
    CLOCK("Clock", "alarm", "Utilities"),
    WEATHER("Weather", "cloud", "Daily"),
    GALLERY("Gallery", "photo", "Media"),
    CAMERA("Camera", "camera", "Media"),
    RECORDER("Recorder", "mic", "Media"),
    MAPS("Maps", "map", "Daily"),
    HEALTH("Health", "favorite", "Daily"),
    SETTINGS("Settings", "settings", "System")
}

// Action Cards that render directly within the conversational thread
sealed class ActionCard {
    data class CallCard(
        val contactName: String,
        val phoneNumber: String,
        val status: CallStatus,
        val durationSeconds: Int = 0
    ) : ActionCard()

    data class MessageCard(
        val recipientName: String,
        val phoneNumber: String,
        val messageBody: String,
        val isSent: Boolean = true
    ) : ActionCard()

    data class CalendarCard(
        val eventId: String,
        val title: String,
        val date: String,
        val time: String,
        val category: String
    ) : ActionCard()

    data class BrowserCard(
        val url: String,
        val title: String,
        val summary: String = ""
    ) : ActionCard()

    data class FileCard(
        val fileId: String,
        val name: String,
        val path: String,
        val sizeFormatted: String,
        val actionType: FileActionType
    ) : ActionCard()

    data class CalcCard(
        val expression: String,
        val result: String,
        val steps: String = ""
    ) : ActionCard()

    data class AutomationCard(
        val routineName: String,
        val steps: List<String>,
        val currentStepIndex: Int,
        val isCompleted: Boolean
    ) : ActionCard()

    data class AlarmCard(
        val alarmId: String,
        val time: String,
        val label: String,
        val isEnabled: Boolean
    ) : ActionCard()

    data class WeatherCard(
        val city: String,
        val tempC: Int,
        val condition: String,
        val highLow: String,
        val humidity: Int
    ) : ActionCard()

    data class HealthCard(
        val steps: Int,
        val stepGoal: Int,
        val waterGlasses: Int,
        val calories: Int,
        val summary: String
    ) : ActionCard()

    data class MapRouteCard(
        val destination: String,
        val etaMinutes: Int,
        val distanceKm: Float,
        val routeSummary: String
    ) : ActionCard()

    data class SlmModelCard(
        val modelId: String,
        val modelName: String,
        val version: String,
        val sizeFormatted: String,
        val status: SlmDownloadState,
        val progress: Float,
        val description: String,
        val tokensPerSec: Float,
        val isCurrentActive: Boolean,
        val contextWindow: String = "8K (8,192 tokens)"
    ) : ActionCard()
}

enum class SlmDownloadState {
    NOT_DOWNLOADED,
    DOWNLOADING,
    DOWNLOADED,
    ACTIVE
}

data class SlmModelInfo(
    val id: String,
    val name: String,
    val tag: String,
    val version: String,
    val sizeFormatted: String,
    val sizeBytes: Long,
    val downloadedBytes: Long = 0L,
    val downloadProgress: Float = 0f,
    val status: SlmDownloadState,
    val description: String,
    val parameterCount: String,
    val quantization: String,
    val targetLatencyMs: Int,
    val memoryRequiredMb: Int,
    val tokensPerSec: Float,
    val contextWindow: String = "8K (8,192 tokens)",
    val isCurrentActive: Boolean = false
)

enum class CallStatus {
    DIALING, CONNECTED, ENDED
}

enum class FileActionType {
    CREATED, OPENED, DELETED, LISTED
}

data class GemmaTelemetry(
    val latencyMs: Long,
    val tokensPerSec: Float = 48.4f,
    val modelName: String = "Gemma-2B-IT (4-bit On-Device)",
    val memoryUsageMb: Int = 892
)

data class ChatMessage(
    val id: String,
    val text: String,
    val isUser: Boolean,
    val timestamp: Long = System.currentTimeMillis(),
    val actionCard: ActionCard? = null,
    val telemetry: GemmaTelemetry? = null
)

// Phone Sub-App Data
data class Contact(
    val id: String,
    val name: String,
    val phoneNumber: String,
    val email: String,
    val avatarColorHex: Long = 0xFF38BDF8,
    val isFavorite: Boolean = false
)

enum class CallType {
    INCOMING, OUTGOING, MISSED
}

data class CallLog(
    val id: String,
    val contactName: String,
    val phoneNumber: String,
    val type: CallType,
    val timestampFormatted: String,
    val durationFormatted: String
)

data class VoicemailItem(
    val id: String,
    val callerName: String,
    val phoneNumber: String,
    val dateFormatted: String,
    val durationFormatted: String,
    val transcript: String,
    val isRead: Boolean = false,
    val isPlaying: Boolean = false
)

// Messages Sub-App Data
data class Conversation(
    val id: String,
    val contactName: String,
    val phoneNumber: String,
    val lastMessage: String,
    val timestampFormatted: String,
    val unreadCount: Int = 0,
    val avatarColorHex: Long = 0xFF818CF8,
    val isPinned: Boolean = false
)

data class SmsMessage(
    val id: String,
    val conversationId: String,
    val senderName: String,
    val body: String,
    val timestampFormatted: String,
    val isFromMe: Boolean,
    val reaction: String? = null,
    val attachmentUrl: String? = null
)

// Calendar Sub-App Data
data class CalendarEvent(
    val id: String,
    val title: String,
    val dateIso: String,
    val timeFormatted: String,
    val category: String,
    val location: String = "Offline",
    val colorHex: Long = 0xFF00E5FF,
    val reminderMinutes: Int = 15
)

// Browser Sub-App Data
data class BrowserTab(
    val id: String,
    val title: String,
    val url: String
)

data class WebBookmark(
    val id: String,
    val title: String,
    val url: String
)

// File Manager Sub-App Data
data class OsFile(
    val id: String,
    val name: String,
    val path: String,
    val isDirectory: Boolean,
    val sizeBytes: Long,
    val content: String = "",
    val modifiedFormatted: String,
    val fileType: String
)

// Notes Sub-App Data
data class NoteCheckItem(
    val id: String,
    val text: String,
    val isChecked: Boolean = false
)

data class OsNote(
    val id: String,
    val title: String,
    val content: String,
    val tag: String,
    val isPinned: Boolean = false,
    val modifiedFormatted: String,
    val colorHex: Long = 0xFF1E293B,
    val checklist: List<NoteCheckItem> = emptyList()
)

// Automations Sub-App Data
data class AutomationRoutine(
    val id: String,
    val title: String,
    val description: String,
    val icon: String,
    val steps: List<String>,
    val triggerPhrase: String,
    val isRunning: Boolean = false
)

// Clock Sub-App Data
data class AlarmItem(
    val id: String,
    val timeFormatted: String,
    val label: String,
    val isEnabled: Boolean,
    val repeatDays: String
)

data class WorldClockItem(
    val id: String,
    val cityName: String,
    val timezoneOffset: String,
    val timeFormatted: String
)

// Weather Sub-App Data
data class HourlyForecast(
    val timeLabel: String,
    val tempC: Int,
    val iconName: String
)

data class DailyForecast(
    val dayLabel: String,
    val condition: String,
    val maxTempC: Int,
    val minTempC: Int
)

data class WeatherInfo(
    val cityName: String,
    val tempC: Int,
    val condition: String,
    val highC: Int,
    val lowC: Int,
    val humidityPercent: Int,
    val windSpeedKmh: Int,
    val uvIndex: Int,
    val airQuality: String,
    val sunrise: String = "6:42 AM",
    val sunset: String = "7:18 PM",
    val pressureHpa: Int = 1014,
    val visibilityKm: Int = 10,
    val hourlyList: List<HourlyForecast>,
    val dailyList: List<DailyForecast>
)

// Media Gallery Sub-App Data
data class MediaItem(
    val id: String,
    val title: String,
    val album: String,
    val dateFormatted: String,
    val colorTintHex: Long = 0xFF00E5FF,
    val category: String = "Photo"
)

// Voice Recorder Sub-App Data
data class AudioMemo(
    val id: String,
    val title: String,
    val durationFormatted: String,
    val recordedDate: String,
    val sizeFormatted: String,
    val transcript: String = "Auto-transcribed audio note by Gemma 2B.",
    val playbackSpeed: Float = 1.0f,
    val isPlaying: Boolean = false
)

// Maps & Navigation Sub-App Data
data class MapPlace(
    val id: String,
    val name: String,
    val category: String,
    val address: String,
    val distanceFormatted: String,
    val etaMinutes: Int
)

// Health & Fitness Sub-App Data
data class DailyStepHistory(
    val dayLabel: String,
    val steps: Int,
    val goal: Int = 10000
)

data class ActiveWorkoutSession(
    val type: String, // "Outdoor Run", "Cycling", "Gym Training"
    val durationSeconds: Int = 0,
    val caloriesBurned: Int = 0,
    val isActive: Boolean = false
)

data class HealthMetrics(
    val stepsToday: Int,
    val stepGoal: Int,
    val waterGlasses: Int,
    val waterGoal: Int,
    val activeMinutes: Int,
    val activeGoal: Int,
    val caloriesBurned: Int,
    val sleepHours: Float,
    val weeklyHistory: List<DailyStepHistory> = emptyList(),
    val currentWorkout: ActiveWorkoutSession? = null
)

// System Settings Data
data class OsSettings(
    val isDarkTheme: Boolean = true,
    val isGemmaTurboMode: Boolean = true,
    val volumeLevel: Float = 0.8f,
    val brightnessLevel: Float = 0.75f,
    val isWifiEnabled: Boolean = true,
    val isBluetoothEnabled: Boolean = true,
    val isLocationEnabled: Boolean = true,
    val isCelsius: Boolean = true,
    val spamCallFilterEnabled: Boolean = true,
    val osVersion: String = "MiniOS 2.0 LTS (Gemma 2B Powered)"
)
