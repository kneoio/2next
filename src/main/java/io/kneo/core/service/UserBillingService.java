package io.kneo.core.service;

import io.kneo.core.localization.LanguageCode;
import io.kneo.core.model.UserBilling;
import io.kneo.core.model.user.IUser;
import io.kneo.core.dto.document.UserBillingDTO;
import io.kneo.core.repository.UserBillingRepository;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@ApplicationScoped
public class UserBillingService extends AbstractService<UserBilling, UserBillingDTO> implements IRESTService<UserBillingDTO> {
    private final UserBillingRepository repository;

    @Inject
    public UserBillingService(UserService userService, UserBillingRepository repository) {
        super(userService);
        this.repository = repository;
    }

    public Uni<List<UserBillingDTO>> getAll(final int limit, final int offset, LanguageCode languageCode) {
        return repository.getAll(limit, offset)
                .chain(list -> Uni.join().all(
                        list.stream()
                                .map(this::mapToDTO)
                                .collect(Collectors.toList())
                ).andFailFast());
    }

    @Override
    public Uni<Integer> getAllCount(IUser user) {
        return repository.getAllCount();
    }

    @Override
    public Uni<UserBillingDTO> getDTO(UUID uuid, IUser user, LanguageCode language) {
        return repository.findById(uuid).chain(this::mapToDTO);
    }

    @Override
    public Uni<UserBillingDTO> upsert(String id, UserBillingDTO dto, IUser user, LanguageCode code) {
        UserBilling doc = new UserBilling();
        doc.setUserId(dto.getUserId());
        doc.setStripeCustomerId(dto.getStripeCustomerId());
        doc.setMeta(dto.getMeta());

        if ("new".equalsIgnoreCase(id) || id == null) {
            return repository.insert(doc, user).chain(this::mapToDTO);
        } else {
            return repository.update(UUID.fromString(id), doc, user).chain(this::mapToDTO);
        }
    }

    private Uni<UserBillingDTO> mapToDTO(UserBilling doc) {
        return Uni.combine().all().unis(
                userService.getName(doc.getAuthor()),
                userService.getName(doc.getLastModifier())
        ).asTuple().onItem().transform(tuple ->
                UserBillingDTO.builder()
                        .id(doc.getId())
                        .author(tuple.getItem1())
                        .regDate(doc.getRegDate())
                        .lastModifier(tuple.getItem2())
                        .lastModifiedDate(doc.getLastModifiedDate())
                        .userId(doc.getUserId())
                        .stripeCustomerId(doc.getStripeCustomerId())
                        .meta(doc.getMeta())
                        .build()
        );
    }

    @Override
    public Uni<Integer> delete(String id, IUser user) {
        return repository.delete(UUID.fromString(id));
    }
}
