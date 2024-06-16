package ru.mfilatov.prayingtimes.telegrambot.commands;

import lombok.extern.slf4j.Slf4j;

import java.util.function.Predicate;
import java.util.regex.Pattern;

@Slf4j
public class UserCommandHandler implements CommandHandler{
    private final Predicate<String> commandPattern = Pattern.compile("get|set\s[aA-zZ]{4,10}\s[aA-zZ]{4,10}").asPredicate();
    public String handle(Long userId, String command) {
        if(!commandPattern.test(command)) {
            return "Unknown command";
        }
        var commandContext = command.split(" ");
        var commandType = commandContext[0];
        var commandName = commandContext[1];
        var commandValue = commandContext[2];

    return switch (commandType) {
      case "set", "get" -> {
        log.info("Command {}", command);
        yield "Not implemented";
      }
      default -> "Unknown command";
    };
}}
