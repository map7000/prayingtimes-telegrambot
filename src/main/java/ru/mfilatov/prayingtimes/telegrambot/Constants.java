package ru.mfilatov.prayingtimes.telegrambot;

import ru.mfilatov.prayingtimes.calculator.enums.CalculationMethods;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Constants {
  public static final String HELP_MESSAGE =
      """
            **Help Menu**

            Here are the available commands:
            /start - Show the main menu
            /help - Get help and instructions
            /prayertimes - Get prayer times for your location
            /set_method - Set calculation method
            /get_methods - Get possible calculation methods

            To get started, share your location and choose a calculation method.
            Source code: https://github.com/map7000/timeskeeper""";

  public static final String DEFAULT_MESSAGE =
      """
            Sorry, I didn't understand that command.
            Use /help to see available commands.""";

  public static final String MENU_MESSAGE =
      """
            Welcome to the Prayer Time Bot!
            Use the commands below to interact with the bot:
            /start - Show this menu
            /help - Get help and instructions
            /prayertimes - Get prayer times for your location
            /set_method - Set calculation method
            /get_methods - Get possible calculation methods""";

  public static final String POSSIBLE_CALCULATION_METHODS =
          Arrays.stream(CalculationMethods.values()).map(a->
                  "/set_method_" + a + " " + a.getName()).collect(Collectors.joining("\n"));
}
