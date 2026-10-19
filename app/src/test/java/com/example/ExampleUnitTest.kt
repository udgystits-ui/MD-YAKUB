package com.example

import com.example.data.model.ActionType
import com.example.service.CommandInterpreter
import org.junit.Assert.assertEquals
import org.junit.Test

class ExampleUnitTest {

  @Test
  fun testYouTubeOpenCommand() {
    val cmd = CommandInterpreter.interpret("Baby, open YouTube")
    assertEquals(ActionType.OPEN_APP, cmd.type)
    assertEquals("com.google.android.youtube", cmd.target)
  }

  @Test
  fun testYouTubeSearchCommand() {
    val cmd = CommandInterpreter.interpret("Baby, search for viral songs")
    assertEquals(ActionType.SEARCH_YOUTUBE, cmd.type)
    assertEquals("viral songs", cmd.query)
  }

  @Test
  fun testBengaliYouTubeCommand() {
    val cmd = CommandInterpreter.interpret("Baby, Free Fire video dekhao")
    assertEquals(ActionType.SEARCH_YOUTUBE, cmd.type)
    assertEquals("Free Fire", cmd.query)
  }

  @Test
  fun testWhatsAppOpenCommand() {
    val cmd = CommandInterpreter.interpret("Baby, open WhatsApp")
    assertEquals(ActionType.OPEN_APP, cmd.type)
    assertEquals("com.whatsapp", cmd.target)
  }

  @Test
  fun testCallContactCommand() {
    val cmd = CommandInterpreter.interpret("Baby, call Hakim")
    assertEquals(ActionType.CALL_CONTACT, cmd.type)
    assertEquals("Hakim", cmd.target)
  }

  @Test
  fun testSendMessageCommand() {
    val cmd = CommandInterpreter.interpret("Baby, send Hakim a message saying hello")
    assertEquals(ActionType.SEND_MESSAGE, cmd.type)
    assertEquals("Hakim", cmd.target)
    assertEquals("hello", cmd.secondaryData)
  }

  @Test
  fun testScreenAutomationCommand() {
    val cmd = CommandInterpreter.interpret("Baby, go back")
    assertEquals(ActionType.AUTOMATION_SCREEN, cmd.type)
    assertEquals("GLOBAL_BACK", cmd.target)
  }

  @Test
  fun testConversationFallback() {
    val cmd = CommandInterpreter.interpret("What is the speed of light?")
    assertEquals(ActionType.CONVERSATION, cmd.type)
  }
}
