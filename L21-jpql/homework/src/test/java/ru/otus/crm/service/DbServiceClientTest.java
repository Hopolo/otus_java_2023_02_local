package ru.otus.crm.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ru.otus.base.AbstractHibernateTest;
import ru.otus.core.cache.HwListener;
import ru.otus.crm.model.Address;
import ru.otus.crm.model.Client;
import ru.otus.crm.model.Phone;

@DisplayName("Демо работы с hibernate (с абстракциями) должно ")
class DbServiceClientTest extends AbstractHibernateTest {

    @Test
    @DisplayName(" корректно сохранять, изменять и загружать клиента")
    void shouldCorrectSaveClient() {
        //given
//        var client = new Client("Ivan");

        // Это надо раскомментировать, у выполненного ДЗ, все тесты должны проходить
        // Кроме удаления комментирования, тестовый класс менять нельзя

        var client = new Client(null, "Vasya", new Address(null, "AnyStreet"), List.of(
            new Phone(null, "13-555-22"),
            new Phone(null, "14-666-333")
        ));

        //when
        var savedClient = dbServiceClient.saveClient(client);
        System.out.println(savedClient);

        //then
        var loadedSavedClient = dbServiceClient.getClient(savedClient.getId());
        assertThat(loadedSavedClient).isPresent();
        assertThat(loadedSavedClient).get()
            .usingRecursiveComparison().isEqualTo(savedClient);

        //when
        var savedClientUpdated = loadedSavedClient.get();
        savedClientUpdated.setName("updatedName");
        dbServiceClient.saveClient(savedClientUpdated);

        //then
        var loadedClient = dbServiceClient.getClient(savedClientUpdated.getId());
        assertThat(loadedClient).isPresent();
        assertThat(loadedClient).get()
            .usingRecursiveComparison().isEqualTo(savedClientUpdated);
        System.out.println(loadedClient);

        //when
        var clientList = dbServiceClient.findAll();

        //then
        assertThat(clientList.size()).isEqualTo(1);
        assertThat(clientList.get(0)).usingRecursiveComparison().isEqualTo(loadedClient.get());
    }

    @Test
    @DisplayName("кэш удаляется при нехватке памяти, нет OOM")
//    -Xms128m -Xmx128m
    void clearCache() {
        assertDoesNotThrow(() -> {
            Client client;
            for (int i = 0; i < 1000000; i++) {
                client = new Client(null, "Vasya", new Address(null, String.valueOf(i)), List.of(
                    new Phone(null, String.valueOf(i)),
                    new Phone(null, "14-666-333")
                ));
                dbServiceClient.saveClient(client);
            }
        });
    }

    @Test
    @DisplayName("использование кэша")
    void cacheUsage() {
        var client = new Client(null, "Vasya", new Address(null, "AnyStreet"), List.of(
            new Phone(null, "13-555-22"),
            new Phone(null, "14-666-333")
        ));

        HwListener<String, Client> listener = Mockito.spy(new HwListener<String, Client>() {
            @Override
            public void notify(
                String key,
                Client value,
                String action
            ) {
                System.out.println("cache used");
            }
        });
        dbServiceClient = new DbServiceClientImpl(transactionManager, clientTemplate, listener);

        //when
        var savedClient = dbServiceClient.saveClient(client);
        Long id = savedClient.getId();
        //then
        dbServiceClient.getClient(id);
        dbServiceClient.getClient(id);

        verify(listener, Mockito.times(3)).notify(any(), any(), any());
    }
}