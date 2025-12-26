package io.kneo.core.service;

import io.kneo.core.localization.LanguageCode;
import io.kneo.core.model.SubscriptionProduct;
import io.kneo.core.model.user.IUser;
import io.kneo.core.dto.document.SubscriptionProductDTO;
import io.kneo.core.repository.SubscriptionProductRepository;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@ApplicationScoped
public class SubscriptionProductService extends AbstractService<SubscriptionProduct, SubscriptionProductDTO> implements IRESTService<SubscriptionProductDTO> {
    private final SubscriptionProductRepository repository;

    @Inject
    public SubscriptionProductService(UserService userService, SubscriptionProductRepository repository) {
        super(userService);
        this.repository = repository;
    }

    public Uni<List<SubscriptionProductDTO>> getAll(final int limit, final int offset, LanguageCode languageCode) {
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


    public Uni<SubscriptionProductDTO> getDTOByIdentifier(String identifier) {
        return null;
    }

    @Override
    public Uni<SubscriptionProductDTO> getDTO(UUID uuid, IUser user, LanguageCode language) {
        return repository.findById(uuid).chain(this::mapToDTO);
    }

    @Override
    public Uni<SubscriptionProductDTO> upsert(String id, SubscriptionProductDTO dto, IUser user, LanguageCode code) {
        SubscriptionProduct doc = new SubscriptionProduct();
        doc.setIdentifier(dto.getIdentifier());
        doc.setLocalizedName(dto.getLocalizedName());
        doc.setLocalizedDescription(dto.getLocalizedDescription());
        doc.setStripePriceId(dto.getStripePriceId());
        doc.setStripeProductId(dto.getStripeProductId());
        doc.setActive(dto.isActive());

        if ("new".equalsIgnoreCase(id) || id == null) {
            return repository.insert(doc, user).chain(this::mapToDTO);
        } else {
            return repository.update(UUID.fromString(id), doc, user).chain(this::mapToDTO);
        }
    }

    private Uni<SubscriptionProductDTO> mapToDTO(SubscriptionProduct doc) {
        return Uni.combine().all().unis(
                userService.getName(doc.getAuthor()),
                userService.getName(doc.getLastModifier())
        ).asTuple().onItem().transform(tuple ->
                SubscriptionProductDTO.builder()
                        .id(doc.getId())
                        .author(tuple.getItem1())
                        .regDate(doc.getRegDate())
                        .lastModifier(tuple.getItem2())
                        .lastModifiedDate(doc.getLastModifiedDate())
                        .identifier(doc.getIdentifier())
                        .localizedName(doc.getLocalizedName())
                        .localizedDescription(doc.getLocalizedDescription())
                        .stripePriceId(doc.getStripePriceId())
                        .stripeProductId(doc.getStripeProductId())
                        .active(doc.isActive())
                        .build()
        );
    }

    @Override
    public Uni<Integer> delete(String id, IUser user) {
        return repository.delete(UUID.fromString(id));
    }
}
