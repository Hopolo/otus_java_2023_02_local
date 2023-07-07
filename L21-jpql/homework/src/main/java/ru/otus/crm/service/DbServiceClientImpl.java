package ru.otus.crm.service;

import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otus.core.cache.HwCache;
import ru.otus.core.cache.HwListener;
import ru.otus.core.cache.MyCache;
import ru.otus.core.repository.DataTemplate;
import ru.otus.core.sessionmanager.TransactionManager;
import ru.otus.crm.model.Client;

public class DbServiceClientImpl implements DBServiceClient {
    private static final Logger log = LoggerFactory.getLogger(DbServiceClientImpl.class);

    private final HwCache<String, Client> cache = new MyCache<>();

    private final DataTemplate<Client> clientDataTemplate;
    private final TransactionManager transactionManager;

    public DbServiceClientImpl(
        TransactionManager transactionManager,
        DataTemplate<Client> clientDataTemplate,
        HwListener<String, Client> cacheListener
    ) {
        this.transactionManager = transactionManager;
        this.clientDataTemplate = clientDataTemplate;
        cache.addListener(cacheListener);
    }

    @Override
    public Client saveClient(Client client) {
        return transactionManager.doInTransaction(session -> {
            var clientCloned = client.clone();
            if (client.getId() == null) {
                var savedClient = clientDataTemplate.insert(session, clientCloned);
                log.info("created client: {}", clientCloned);
                cache.put(savedClient.getId().toString(), savedClient);
                return savedClient;
            }
            var savedClient = clientDataTemplate.update(session, clientCloned);
            cache.put(savedClient.getId().toString(), savedClient);
            log.info("updated client: {}", savedClient);
            return savedClient;
        });
    }

    @Override
    public Optional<Client> getClient(long id) {
        return Optional.of(cache.get(String.valueOf(id))).or(() -> transactionManager.doInReadOnlyTransaction(session -> {
            var clientOptional = clientDataTemplate.findById(session, id);
            clientOptional.ifPresent(client -> cache.put(String.valueOf(id), client));
            log.info("client: {}", clientOptional);
            return clientOptional;
        }));
    }

    @Override
    public List<Client> findAll() {
        return transactionManager.doInReadOnlyTransaction(session -> {
            var clientList = clientDataTemplate.findAll(session);
            log.info("clientList:{}", clientList);
            return clientList;
        });
    }
}
