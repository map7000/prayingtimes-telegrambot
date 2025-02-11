package ru.mfilatov.prayingtimes.telegrambot.events;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.mfilatov.prayingtimes.telegrambot.entities.Event;
import ru.mfilatov.prayingtimes.telegrambot.repositories.EventRepository;

@Service
public class EventHandler {
    private final EventRepository events;

    @Autowired
    EventHandler(EventRepository events) {
        this.events = events;
    }

    public void newUser(Long chatId) {
        saveEvent(EventType.NEW_USER, chatId);
    }

    public void changeLocation(Long chatId) {
        saveEvent(EventType.CHANGE_LOCATION, chatId);
    }

    public void timesRequest(Long chatId) {
        saveEvent(EventType.TIMES_REQUEST, chatId);
    }

    public void help(Long chatId) {
        saveEvent(EventType.HELP, chatId);
    }

    public void start(Long chatId) {
        saveEvent(EventType.START, chatId);
    }

    public void error(Long chatId, String description ) {
        saveEvent(EventType.ERROR, chatId, description);
    }

    public void unknownCommand(Long chatId, String description) {
        saveEvent(EventType.UNKNOWN_COMMAND, chatId, description);
    }

    private void saveEvent(EventType eventType, Long chatId, String description) {
        var event = new Event();
        event.setEventType(eventType);
        event.setChatId(chatId);
        event.setDescription(description);
        event.setTimestamp(System.currentTimeMillis());
        events.save(event);
    }

    private void saveEvent(EventType eventType, Long chatId) {
        saveEvent(eventType, chatId, "");
    }
}
