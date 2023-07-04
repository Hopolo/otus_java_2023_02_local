package ru.otus.servers;

public interface ClientsWebServer {
    void start() throws Exception;

    void join() throws Exception;

    void stop() throws Exception;
}
