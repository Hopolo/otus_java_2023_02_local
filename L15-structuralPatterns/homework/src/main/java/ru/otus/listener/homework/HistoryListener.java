package ru.otus.listener.homework;

import java.util.HashMap;
import java.util.Optional;
import ru.otus.listener.Listener;
import ru.otus.model.Message;

public class HistoryListener implements Listener, HistoryReader {

    HashMap<Long, Message> history;

    public HistoryListener() {
        this.history = new HashMap<>();
    }

    @Override
    public void onUpdated(Message msg) {
        history.put(msg.getId(), new Message(msg));
    }

    @Override
    public Optional<Message> findMessageById(long id) {
        return Optional.of(history.get(id));
    }
}
