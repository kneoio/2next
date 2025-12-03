package io.kneo.core.service;

import io.kneo.core.localization.LanguageCode;
import io.kneo.core.model.UserSubscription;
import io.kneo.core.model.user.IUser;
import io.kneo.core.dto.document.UserSubscriptionDTO;
import io.kneo.core.repository.UserSubscriptionRepository;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@ApplicationScoped
public class UserSubscriptionService extends AbstractService<UserSubscription, UserSubscriptionDTO> implements IRESTService<UserSubscriptionDTO> {
    private final UserSubscriptionRepository repository;

    @Inject
    public UserSubscriptionService(UserService userService, UserSubscriptionRepository repository) {
        super(userService);
        this.repository = repository;
    }

    public Uni<List<UserSubscriptionDTO>> getAll(final int limit, final int offset, LanguageCode languageCode) {
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
    public Uni<UserSubscriptionDTO> getDTOByIdentifier(String identifier) {
        return null;
    }

    @Override
    public Uni<UserSubscriptionDTO> getDTO(UUID uuid, IUser user, LanguageCode language) {
        return repository.findById(uuid).chain(this::mapToDTO);
    }

    @Override
    public Uni<UserSubscriptionDTO> upsert(String id, UserSubscriptionDTO dto, IUser user, LanguageCode code) {
        UserSubscription doc = new UserSubscription();
        doc.setUserId(dto.getUserId());
        doc.setStripeSubscriptionId(dto.getStripeSubscriptionId());
        doc.setSubscriptionType(dto.getSubscriptionType());
        doc.setSubscriptionStatus(dto.getSubscriptionStatus());
        doc.setTrialEnd(dto.getTrialEnd());
        doc.setActive(dto.isActive());
        doc.setMeta(dto.getMeta());

        if ("new".equalsIgnoreCase(id) || id == null) {
            return repository.insert(doc, user).chain(this::mapToDTO);
        } else {
            return repository.update(UUID.fromString(id), doc, user).chain(this::mapToDTO);
        }
    }

    private Uni<UserSubscriptionDTO> mapToDTO(UserSubscription doc) {
        return Uni.combine().all().unis(
                userService.getName(doc.getAuthor()),
                userService.getName(doc.getLastModifier())
        ).asTuple().onItem().transform(tuple ->
                UserSubscriptionDTO.builder()
                        .id(doc.getId())
                        .author(tuple.getItem1())
                        .regDate(doc.getRegDate())
                        .lastModifier(tuple.getItem2())
                        .lastModifiedDate(doc.getLastModifiedDate())
                        .userId(doc.getUserId())
                        .stripeSubscriptionId(doc.getStripeSubscriptionId())
                        .subscriptionType(doc.getSubscriptionType())
                        .subscriptionStatus(doc.getSubscriptionStatus())
                        .trialEnd(doc.getTrialEnd())
                        .active(doc.isActive())
                        .meta(doc.getMeta())
                        .build()
        );
    }

    @Override
    public Uni<Integer> delete(String id, IUser user) {
        return repository.delete(UUID.fromString(id));
    }
}
