package com.semantyca.core.service;

import com.semantyca.core.model.SubscriptionProduct;
import com.semantyca.core.model.user.IUser;
import com.semantyca.core.repository.SubscriptionProductRepository;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class SubscriptionProductService {
    private final SubscriptionProductRepository repository;

    @Inject
    public SubscriptionProductService(SubscriptionProductRepository repository) {
        this.repository = repository;
    }

    public Uni<List<SubscriptionProduct>> getAll(int limit, int offset) {
        return repository.getAll(limit, offset);
    }

    public Uni<Integer> getAllCount() {
        return repository.getAllCount();
    }

    public Uni<SubscriptionProduct> findById(UUID id) {
        return repository.findById(id);
    }

    public Uni<SubscriptionProduct> upsert(String id, SubscriptionProduct doc, IUser user) {
        if ("new".equalsIgnoreCase(id) || id == null) {
            return repository.insert(doc, user);
        }
        return repository.update(UUID.fromString(id), doc, user);
    }

    public Uni<Integer> delete(UUID id) {
        return repository.delete(id);
    }
}
