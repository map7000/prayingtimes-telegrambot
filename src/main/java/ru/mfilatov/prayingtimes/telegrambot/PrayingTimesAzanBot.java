/*
 * Copyright 2023 Mikhail Filatov
 * SPDX-License-Identifier: Apache-2.0
 */
package ru.mfilatov.prayingtimes.telegrambot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.interfaces.LongPollingUpdateConsumer;
import org.telegram.telegrambots.longpolling.starter.SpringLongPollingBot;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import ru.mfilatov.prayingtimes.telegrambot.clients.TimeskeeperClient;
import ru.mfilatov.prayingtimes.telegrambot.entities.Event;
import ru.mfilatov.prayingtimes.telegrambot.entities.User;
import ru.mfilatov.prayingtimes.telegrambot.repositories.EventRepository;
import ru.mfilatov.prayingtimes.telegrambot.repositories.UserRepository;

@Slf4j
@Component
public class PrayingTimesAzanBot
    implements SpringLongPollingBot, LongPollingSingleThreadUpdateConsumer {

  private final TelegramClient telegramClient;
  private final TimeskeeperClient client;
  private final EventRepository events;
  private final UserRepository users;
  private final String botToken;

  private static final Pattern ALPHANUMERIC_PATTERN = Pattern.compile("^[a-zA-Z0-9\\s.,!?-]+$");
  private static final int MAX_REQUESTS_PER_USER = 10; // Max requests per user per minute
  private static final long RATE_LIMIT_TIME_WINDOW = 60_000; // 1 minute in milliseconds

  // Simulated rate-limiting storage (in a real application, use a proper cache or database)
  private final java.util.Map<Long, Integer> userRequestCounts = new java.util.HashMap<>();
  private final java.util.Map<Long, Long> userLastRequestTimes = new java.util.HashMap<>();

  @Autowired
  public PrayingTimesAzanBot(
      TimeskeeperClient client, EventRepository events, UserRepository users, @Value("${BOT_TOKEN}") String token) {
    this.client = client;
    this.events = events;
    this.users = users;
    this.botToken = token;

    this.telegramClient = new OkHttpTelegramClient(getBotToken());
  }

  @Override
  public String getBotToken() {
    return this.botToken;
  }

  @Override
  public LongPollingUpdateConsumer getUpdatesConsumer() {
    return this;
  }

  @Override
  public void consume(Update update) {
    Long chatId = update.getMessage().getChatId();

    // Rate limiting check
    if (isRateLimited(chatId)) {
      sendMessage(chatId, "⚠️ Too many requests. Please try again later.");
      return;
    }

    // Input validation
    if (!isValidInput(update.getMessage().getText())) {
      sendMessage(chatId, "⚠️ Invalid input. Please use only letters, numbers, and common symbols.");
      return;
    }

    Double latitude;
    Double longitude;

    if (update.hasMessage()) {
      var message = update.getMessage();

      switch (message.getText()) {
        case "/start":
          sendStartMenu(chatId);
          break;
        case "/help":
          sendHelpMessage(chatId);
          break;
        case "/prayertimes":
          sendPrayerTimes(chatId);
          break;
        default:
          sendDefaultMessage(chatId);
          break;
      }
    }

    if (update.hasMessage() && update.getMessage().hasLocation()) {
      var eventMessage =
          String.format(
              "Location request. UserId: %s, Latitude: %s, Longitude: %s",
              chatId,
              update.getMessage().getLocation().getLatitude(),
              update.getMessage().getLocation().getLongitude());
      log.info(eventMessage);

      var user = users.findByTelegramId(chatId);
      if (Objects.isNull(user)) {
        user = new User();
        user.setTelegramId(chatId);
      }

      latitude = update.getMessage().getLocation().getLatitude();
      longitude = update.getMessage().getLocation().getLongitude();

      user.setLatitude(latitude);
      user.setLongitude(longitude);

      users.save(user);

      var event = new Event();
      event.setUser(user);
      event.setTimestamp(System.currentTimeMillis());
      event.setDescription(eventMessage);

      events.save(event);

      sendMessage(chatId, client.getTimesByCoordinates(latitude, longitude, 14).toString());
    }
  }

  private boolean isRateLimited(Long userId) {
    long currentTime = System.currentTimeMillis();
    Long lastRequestTime = userLastRequestTimes.get(userId);
    Integer requestCount = userRequestCounts.get(userId);

    if (lastRequestTime == null || (currentTime - lastRequestTime) > RATE_LIMIT_TIME_WINDOW) {
      // Reset the counter if the time window has passed
      userRequestCounts.put(userId, 1);
      userLastRequestTimes.put(userId, currentTime);
      return false;
    } else if (requestCount < MAX_REQUESTS_PER_USER) {
      // Increment the counter
      userRequestCounts.put(userId, requestCount + 1);
      return false;
    } else {
      // Rate limit exceeded
      return true;
    }
  }

  private boolean isValidInput(String input) {
    // Validate input to ensure it contains only allowed characters
    return ALPHANUMERIC_PATTERN.matcher(input).matches();
  }

  private void sendMessage(SendMessage message) {
    try {
      telegramClient.execute(message);
    } catch (TelegramApiException e) {
      // Log the error without exposing sensitive details
      System.err.println("Failed to send message: " + e.getMessage());
    }
  }

  private void sendMessage(Long chatId, String text) {
    SendMessage message = SendMessage.builder().chatId(chatId).text(text).build();
    sendMessage(message);
  }

  private void sendHelpMessage(Long chatId) {
    String text = "**Help Menu**\n\n" +
            "Here are the available commands:\n" +
            "/start - Show the main menu\n" +
            "/help - Get help and instructions\n" +
            "/prayertimes - Get prayer times for your location\n\n" +
            "To get started, share your location and choose a calculation method.\n" +
            "Source code: https://github.com/map7000/timeskeeper";
    sendMessage(chatId, text);
  }

  private void sendPrayerTimes(Long chatId) {
    String text = "🕋Please share your location to get prayer times.";
    sendMessage(chatId, text);
  }

  private void sendDefaultMessage(Long chatId) {
    String text = "Sorry, I didn't understand that command. 😕\n" +
            "Use /help to see available commands.";
    sendMessage(chatId, text);
  }

  private void sendStartMenu(Long chatId) {
    SendMessage message = SendMessage.builder()
            .chatId(chatId)
            .text("Welcome to the Prayer Time Bot! 🕌\n\n" +
                    "Use the commands below to interact with the bot:\n" +
                    "/start - Show this menu\n" +
                    "/help - Get help and instructions\n" +
                    "/prayertimes - Get prayer times for your location").build();

    // Create a custom keyboard
    List<KeyboardRow> keyboard = new ArrayList<>();

    // Row 1
    KeyboardRow row1 = new KeyboardRow();
    row1.add("/start");
    row1.add("/help");

    // Row 2
    KeyboardRow row2 = new KeyboardRow();
    row2.add("/prayertimes");

    keyboard.add(row1);
    keyboard.add(row2);

    ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup(keyboard);
    keyboardMarkup.setResizeKeyboard(true); // Adjust keyboard size to fit buttons

    message.setReplyMarkup(keyboardMarkup);
    sendMessage(message);
  }

}
