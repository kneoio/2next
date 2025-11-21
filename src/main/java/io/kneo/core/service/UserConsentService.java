package io.kneo.core.service;

import io.kneo.core.localization.LanguageCode;
import io.kneo.core.model.UserConsent;
import io.kneo.core.model.user.IUser;
import io.kneo.core.dto.document.UserConsentDTO;
import io.kneo.core.repository.UserConsentRepository;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@ApplicationScoped
public class UserConsentService extends AbstractService<UserConsent, UserConsentDTO> implements IRESTService<UserConsentDTO> {
    private final UserConsentRepository repository;

    @Inject
    public UserConsentService(UserService userService, UserConsentRepository repository) {
        super(userService);
        this.repository = repository;
    }

    public Uni<List<UserConsentDTO>> getAll(final int limit, final int offset, LanguageCode languageCode) {
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
    public Uni<UserConsentDTO> getDTOByIdentifier(String identifier) {
        return null;
    }

    @Override
    public Uni<UserConsentDTO> getDTO(UUID uuid, IUser user, LanguageCode language) {
        return repository.findById(uuid).chain(this::mapToDTO);
    }

    @Override
    public Uni<UserConsentDTO> upsert(String id, UserConsentDTO dto, IUser user, LanguageCode code) {
        UserConsent doc = new UserConsent();
        doc.setUserId(dto.getUserId());
        doc.setEssential(dto.isEssential());
        doc.setAnalytics(dto.isAnalytics());
        doc.setMarketing(dto.isMarketing());
        doc.setTimestamp(dto.getTimestamp());
        doc.setIpAddress(dto.getIpAddress());
        doc.setUserAgent(dto.getUserAgent());

        if ("new".equalsIgnoreCase(id) || id == null) {
            return repository.insert(doc, user).chain(this::mapToDTO);
        } else {
            return repository.update(UUID.fromString(id), doc, user).chain(this::mapToDTO);
        }
    }

    private Uni<UserConsentDTO> mapToDTO(UserConsent doc) {
        return Uni.combine().all().unis(
                userService.getName(doc.getAuthor()),
                userService.getName(doc.getLastModifier())
        ).asTuple().onItem().transform(tuple ->
                UserConsentDTO.builder()
                        .id(doc.getId())
                        .author(tuple.getItem1())
                        .regDate(doc.getRegDate())
                        .lastModifier(tuple.getItem2())
                        .lastModifiedDate(doc.getLastModifiedDate())
                        .userId(doc.getUserId())
                        .essential(doc.isEssential())
                        .analytics(doc.isAnalytics())
                        .marketing(doc.isMarketing())
                        .timestamp(doc.getTimestamp())
                        .ipAddress(doc.getIpAddress())
                        .userAgent(doc.getUserAgent())
                        .build()
        );
    }

    @Override
    public Uni<Integer> delete(String id, IUser user) {
        return repository.delete(UUID.fromString(id));
    }
}
