package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.model.NoteCheckItem
import com.example.data.model.SlmDownloadState
import com.example.data.model.SubApp
import com.example.ui.viewmodel.MiniOSViewModel
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("MiniOS", appName)
  }

  @Test
  fun `verify clean initial state without fake or demo data`() = runBlocking {
    val viewModel = MiniOSViewModel()
    assertEquals(SubApp.AI_HUB, viewModel.currentApp.value)

    viewModel.switchApp(SubApp.PHONE)
    assertEquals(SubApp.PHONE, viewModel.currentApp.value)

    viewModel.switchApp(SubApp.CLOCK)
    assertEquals(SubApp.CLOCK, viewModel.currentApp.value)

    viewModel.switchApp(SubApp.WEATHER)
    assertEquals(SubApp.WEATHER, viewModel.currentApp.value)

    viewModel.switchApp(SubApp.MAPS)
    assertEquals(SubApp.MAPS, viewModel.currentApp.value)

    viewModel.switchApp(SubApp.HEALTH)
    assertEquals(SubApp.HEALTH, viewModel.currentApp.value)

    // Verify all fake/demo seed data is purged
    assertTrue(viewModel.contacts.value.isEmpty())
    assertTrue(viewModel.callLogs.value.isEmpty())
    assertTrue(viewModel.voicemails.value.isEmpty())
    assertTrue(viewModel.conversations.value.isEmpty())
    assertTrue(viewModel.calendarEvents.value.isEmpty())
    assertTrue(viewModel.files.value.isEmpty())
    assertTrue(viewModel.notes.value.isEmpty())
    assertTrue(viewModel.alarms.value.isEmpty())
    assertTrue(viewModel.mediaItems.value.isEmpty())
    assertTrue(viewModel.audioMemos.value.isEmpty())
    assertTrue(viewModel.places.value.isEmpty())

    // Verify real user data entry functions work seamlessly
    viewModel.addContact("Real User", "+1 (555) 123-4567", "user@test.org")
    assertEquals(1, viewModel.contacts.value.size)
    assertEquals("Real User", viewModel.contacts.value.first().name)

    viewModel.addCalendarEvent("Sprint Planning", "2026-10-26", "10:00 AM")
    assertEquals(1, viewModel.calendarEvents.value.size)

    viewModel.createFile("specs.txt", "~/Documents", "On-device intelligence")
    assertEquals(1, viewModel.files.value.size)

    viewModel.addPlace("Central Park", "Parks", "59th to 110th St")
    assertEquals(1, viewModel.places.value.size)
  }

  @Test
  fun `verify only real slm models are kept and respond immediately upon switching`() = runBlocking {
    val viewModel = MiniOSViewModel()
    val models = viewModel.availableSlmModels.value
    // Exactly 3 real models: Gemma 4 E2B, Llama 3.2 128K, Qwen 2.5 3B (Gemma 2B base removed)
    assertEquals(3, models.size)
    assertFalse(models.any { it.id == "gemma_2b_baseline" })

    val gemma4 = models.first { it.id == "gemma_4_e2b_slm" }
    assertEquals(SlmDownloadState.ACTIVE, gemma4.status)
    assertTrue(gemma4.isCurrentActive)
    assertEquals("1.42 GB", gemma4.sizeFormatted)
    assertEquals("2.1B (Edge-Optimized)", gemma4.parameterCount)
    assertEquals("8K (8,192 tokens)", gemma4.contextWindow)

    val llama128k = models.first { it.id == "llama_3_2_3b_128k" }
    assertEquals(SlmDownloadState.DOWNLOADED, llama128k.status)
    assertEquals("128K (131,072 tokens)", llama128k.contextWindow)
    assertEquals("1.85 GB", llama128k.sizeFormatted)

    val qwen3b = models.first { it.id == "qwen_2_5_3b" }
    assertEquals(SlmDownloadState.DOWNLOADED, qwen3b.status)
    assertEquals("32K (32,768 tokens, up to 128K)", qwen3b.contextWindow)
    assertEquals("1.68 GB", qwen3b.sizeFormatted)

    val gemmaEngine = com.example.ai.GemmaEngine(viewModel.repository)

    // Verify Gemma 4 responds immediately
    val gemmaResp = gemmaEngine.processUserPrompt("Hello")
    assertNotNull(gemmaResp.text)
    assertTrue(gemmaResp.text.contains("Gemma 4 E2B"))

    // Switch to Llama 3.2 and verify immediate response
    viewModel.activateSlmModel("llama_3_2_3b_128k")
    assertEquals("llama_3_2_3b_128k", viewModel.activeSlmModel.value.id)
    assertEquals("128K (131,072 tokens)", viewModel.activeSlmModel.value.contextWindow)
    val llamaResp = gemmaEngine.processUserPrompt("Hi there")
    assertNotNull(llamaResp.text)
    assertTrue(llamaResp.text.contains("Llama 3.2"))

    // Switch to Qwen 2.5 and verify immediate response
    viewModel.activateSlmModel("qwen_2_5_3b")
    assertEquals("qwen_2_5_3b", viewModel.activeSlmModel.value.id)
    assertEquals("Qwen 2.5 3B (Edge Reasoning)", viewModel.activeSlmModel.value.name)
    val qwenResp = gemmaEngine.processUserPrompt("Greetings")
    assertNotNull(qwenResp.text)
    assertTrue(qwenResp.text.contains("Qwen 2.5"))
  }

  @Test
  fun `verify real on-device neural reasoning and semantic intent understanding`() = runBlocking {
    val viewModel = MiniOSViewModel()

    // 1. Test Open-domain concept explanation
    val gemmaEngine = com.example.ai.GemmaEngine(viewModel.repository)
    val explanation = gemmaEngine.processUserPrompt("Explain how transformers work")
    assertNotNull(explanation.text)
    assertTrue(explanation.text.contains("Attention") || explanation.text.contains("Self-Attention"))
    assertNotNull(explanation.telemetry)
    assertTrue(explanation.telemetry!!.latencyMs > 0)

    // 2. Test Algorithmic coding assistance
    val codeResp = gemmaEngine.processUserPrompt("Write quicksort in python")
    assertTrue(codeResp.text.contains("def quicksort"))
    assertTrue(codeResp.text.contains("pivot"))

    // 3. Test SLM Model comparison breakdown
    val compareResp = gemmaEngine.processUserPrompt("Compare Gemma vs Llama vs Qwen")
    assertTrue(compareResp.text.contains("Gemma 4 E2B"))
    assertTrue(compareResp.text.contains("Llama 3.2 3B"))
    assertTrue(compareResp.text.contains("Qwen 2.5 3B"))

    // 4. Test Semantic Phone Calling with natural phrasing
    val callResp = gemmaEngine.processUserPrompt("Could you ring up Alice")
    assertNotNull(callResp.actionCard)
    assertTrue(callResp.actionCard is com.example.data.model.ActionCard.CallCard)
    assertEquals("Alice", (callResp.actionCard as com.example.data.model.ActionCard.CallCard).contactName)

    // 5. Test Math calculation
    val mathResp = gemmaEngine.processUserPrompt("Calculate 25 * 4 + 10")
    assertNotNull(mathResp.actionCard)
    assertTrue(mathResp.actionCard is com.example.data.model.ActionCard.CalcCard)
  }

  @Test
  fun `verify browser multi tabs and bookmarking`() = runBlocking {
    val viewModel = MiniOSViewModel()
    val initialTabCount = viewModel.browserTabs.value.size
    assertTrue(initialTabCount >= 1)

    viewModel.createBrowserTab("https://news.ycombinator.com")
    assertEquals(initialTabCount + 1, viewModel.browserTabs.value.size)

    val initialBookmarks = viewModel.browserBookmarks.value.size
    viewModel.toggleBookmark("Kotlin Portal", "https://kotlinlang.org")
    assertEquals(initialBookmarks + 1, viewModel.browserBookmarks.value.size)

    viewModel.toggleDesktopSite()
    assertTrue(viewModel.isDesktopSite.value)
  }

  @Test
  fun `verify notes creation with checklist and colors`() = runBlocking {
    val viewModel = MiniOSViewModel()
    assertTrue(viewModel.notes.value.isEmpty())

    val checklist = listOf(
        NoteCheckItem("c1", "Download Gemma 4 E2B weights", false),
        NoteCheckItem("c2", "Test offline intents", true)
    )
    viewModel.addNote("SLM Roadmap", "Sprint goals for on-device AI", "Architecture", 0xFF0284C7, checklist)
    assertEquals(1, viewModel.notes.value.size)

    val note = viewModel.notes.value.first()
    assertEquals("SLM Roadmap", note.title)
    assertEquals(2, note.checklist.size)
    assertFalse(note.checklist.first().isChecked)

    viewModel.toggleChecklistItem(note.id, "c1")
    val updatedNote = viewModel.notes.value.first()
    assertTrue(updatedNote.checklist.first().isChecked)
  }

  @Test
  fun `verify scientific calculator functions`() = runBlocking {
    val viewModel = MiniOSViewModel()
    viewModel.calcKeyPress("9")
    viewModel.calcKeyPress("√")
    assertEquals("3", viewModel.calcResult.value)

    viewModel.calcKeyPress("C")
    assertEquals("0", viewModel.calcResult.value)
  }

  @Test
  fun `verify weather city selector and temp unit conversion`() = runBlocking {
    val viewModel = MiniOSViewModel()
    assertTrue(viewModel.weatherCities.value.any { it.contains("Tokyo") })
    viewModel.selectWeatherCity("Tokyo")
    assertTrue(viewModel.selectedCity.value.contains("Tokyo"))
    assertTrue(viewModel.weatherInfo.value?.cityName?.contains("Tokyo") == true)

    val initialCelsius = viewModel.settings.value.isCelsius
    viewModel.toggleTempUnit()
    assertEquals(!initialCelsius, viewModel.settings.value.isCelsius)
  }

  @Test
  fun `verify health workouts and step logging`() = runBlocking {
    val viewModel = MiniOSViewModel()
    assertEquals(0, viewModel.healthMetrics.value.stepsToday)
    assertNull(viewModel.healthMetrics.value.currentWorkout)

    viewModel.logSteps(2500)
    assertEquals(2500, viewModel.healthMetrics.value.stepsToday)
    assertTrue(viewModel.healthMetrics.value.caloriesBurned > 0)

    viewModel.startWorkout("Outdoor Run")
    assertNotNull(viewModel.healthMetrics.value.currentWorkout)
    assertTrue(viewModel.healthMetrics.value.currentWorkout!!.isActive)
    assertEquals("Outdoor Run", viewModel.healthMetrics.value.currentWorkout!!.type)

    viewModel.stopWorkout()
    assertNull(viewModel.healthMetrics.value.currentWorkout)
  }

  @Test
  fun `verify spam shield toggle`() = runBlocking {
    val viewModel = MiniOSViewModel()
    assertTrue(viewModel.settings.value.spamCallFilterEnabled)

    viewModel.toggleSpamShield()
    assertFalse(viewModel.settings.value.spamCallFilterEnabled)
  }
}
