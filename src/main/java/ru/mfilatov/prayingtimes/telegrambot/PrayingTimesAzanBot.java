/*
 * Copyright 2023 Mikhail Filatov
 * SPDX-License-Identifier: Apache-2.0
 */
package ru.mfilatov.prayingtimes.telegrambot;

import static ru.mfilatov.prayingtimes.telegrambot.Constants.*;

import java.util.Arrays;
import java.util.Objects;
import java.util.regex.Pattern;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.interfaces.LongPollingUpdateConsumer;
import org.telegram.telegrambots.longpolling.starter.SpringLongPollingBot;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import ru.mfilatov.prayingtimes.calculator.enums.CalculationMethods;
import ru.mfilatov.prayingtimes.telegrambot.clients.TimeskeeperClient;
import ru.mfilatov.prayingtimes.telegrambot.counter.RateLimiterService;
import ru.mfilatov.prayingtimes.telegrambot.entities.User;
import ru.mfilatov.prayingtimes.telegrambot.events.EventHandler;
import ru.mfilatov.prayingtimes.telegrambot.repositories.UserRepository;

@Slf4j
@Component
public class PrayingTimesAzanBot
    implements SpringLongPollingBot, LongPollingSingleThreadUpdateConsumer {

  private final TelegramClient telegramClient;
  private final TimeskeeperClient client;
  private final EventHandler events;
  private final UserRepository users;
  private final RateLimiterService rateLimiter;
  private final String botToken;

  private static final Pattern ALPHANUMERIC_PATTERN = Pattern.compile("^[a-zA-Z0-9\\s.,!/?-_]+$");

  public PrayingTimesAzanBot(
      TimeskeeperClient client,
      EventHandler events,
      UserRepository users,
      RateLimiterService rateLimiter,
      @Value("${BOT_TOKEN}") String token) {
    this.client = client;
    this.events = events;
    this.users = users;
    this.rateLimiter = rateLimiter;
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

    if (rateLimiter.isRateLimited(chatId)) {
      sendMessage(chatId, "⚠️ Too many requests. Please try again later.");
      return;
    }

    if (update.hasMessage() && update.getMessage().hasText()) {
      if (!isValidInput(update.getMessage().getText())) {
        sendMessage(
            chatId, "⚠️ Invalid input. Please use only letters, numbers, and common symbols.");
        return;
      }
      var message = update.getMessage();

      switch (message.getText()) {
        case "/start" -> sendStartMenu(chatId);
        case "/help" -> sendHelpMessage(chatId);
        case "/prayertimes" -> sendPrayerTimes(chatId);
        case String s when s.startsWith("/set_method") -> setMethod(message.getText(), chatId);
        case "/get_methods" -> getMethods(chatId);
        default -> sendDefaultMessage(chatId, message.getText());
      }
    }

    if (update.hasMessage() && update.getMessage().hasLocation()) {
      var user = users.findByTelegramId(chatId);
      if (Objects.isNull(user)) {
        events.newUser(chatId);
        user = new User();
        user.setTelegramId(chatId);
        user.setMethod("MWL");
      }

      user.setLatitude(update.getMessage().getLocation().getLatitude());
      user.setLongitude(update.getMessage().getLocation().getLongitude());

      users.save(user);
      events.changeLocation(chatId);

      sendMessage(chatId, getPrayingTimes(user));
    }
  }

  private boolean isValidInput(String input) {
    return ALPHANUMERIC_PATTERN.matcher(input).matches();
  }

  private void sendMessage(Long chatId, String text) {
    SendMessage message = SendMessage.builder().chatId(chatId).text(text).build();
    try {
      telegramClient.execute(message);
    } catch (TelegramApiException e) {
      events.error(chatId, "Failed to send message: " + e.getMessage());
    }
  }

  private void sendHelpMessage(Long chatId) {
    events.help(chatId);
    sendMessage(chatId, HELP_MESSAGE);
  }

  private void sendPrayerTimes(Long chatId) {
    events.timesRequest(chatId);
    var user = users.findByTelegramId(chatId);
    if (Objects.isNull(user)) {
      sendMessage(chatId, "🕋Please share your location to get prayer times.");
      return;
    }

    sendMessage(chatId, getPrayingTimes(user));
  }

  private void sendDefaultMessage(Long chatId, String description) {
    events.unknownCommand(chatId, description);
    sendMessage(chatId, DEFAULT_MESSAGE);
  }

  private void sendStartMenu(Long chatId) {
    events.start(chatId);
    sendMessage(chatId, MENU_MESSAGE);
  }

  private void getMethods(Long chatId) {
    sendMessage(chatId, POSSIBLE_CALCULATION_METHODS);
  }

  private void setMethod(String text, Long chatId) {
    var method = text.replace("/set_method_", "");
    if (Arrays.stream(CalculationMethods.values()).anyMatch(a -> a.name().equals(method))) {
      var user = users.findByTelegramId(chatId);
      user.setMethod(method);
      users.save(user);
      sendMessage(chatId, "Calculation method was successfully set");
    } else {
      sendMessage(chatId, "Unknown calculation method");
    }
  }

  private String getPrayingTimes(User user) {
    var times =
        client.getTimesByCoordinates(user.getLatitude(), user.getLongitude(), user.getMethod());
    return String.format(
        "Date: %s Timezone: %s\nMethod: %s\nImsak:\t%s\nFajr:\t%s\nSunrise:\t%s\n"
            + "Dhuhr:\t%s\nAsr:\t%s\nSunset:\t%s\nMaghrib:\t%s\nIsha:\t%s,\nMidnight:\t%s",
        times.date(),
        times.timezone(),
        times.method(),
        times.imsak(),
        times.fajr(),
        times.sunrise(),
        times.dhuhr(),
        times.asr(),
        times.sunset(),
        times.maghrib(),
        times.isha(),
        times.midnight());
  }
}
