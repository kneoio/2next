package io.kneo.core.service;

import io.kneo.core.localization.LanguageCode;
import io.kneo.core.model.Agreement;
import io.kneo.core.model.user.IUser;
import io.kneo.core.dto.document.AgreementDTO;
import io.kneo.core.repository.AgreementRepository;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@ApplicationScoped
public class AgreementService extends AbstractService<Agreement, AgreementDTO> implements IRESTService<AgreementDTO> {
    private final AgreementRepository repository;

    @Inject
    public AgreementService(UserService userService, AgreementRepository repository) {
        super(userService);
        this.repository = repository;
    }

    public Uni<List<AgreementDTO>> getAll(final int limit, final int offset, LanguageCode languageCode) {
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
    public Uni<AgreementDTO> getDTO(UUID uuid, IUser user, LanguageCode language) {
        return repository.findById(uuid).chain(this::mapToDTO);
    }

    @Override
    public Uni<AgreementDTO> upsert(String id, AgreementDTO dto, IUser user, LanguageCode code) {
        Agreement doc = new Agreement();
        doc.setCountry(dto.getCountry());
        doc.setUserAgent(dto.getUserAgent());
        doc.setAgreementVersion(dto.getAgreementVersion());
        doc.setTermsText(dto.getTermsText());

        if ("new".equalsIgnoreCase(id) || id == null) {
            return repository.insert(doc, user).chain(this::mapToDTO);
        } else {
            return repository.update(UUID.fromString(id), doc, user).chain(this::mapToDTO);
        }
    }

    private Uni<AgreementDTO> mapToDTO(Agreement doc) {
        return Uni.combine().all().unis(
                userService.getName(doc.getAuthor()),
                userService.getName(doc.getLastModifier())
        ).asTuple().onItem().transform(tuple ->
                AgreementDTO.builder()
                        .id(doc.getId())
                        .author(tuple.getItem1())
                        .regDate(doc.getRegDate())
                        .lastModifier(tuple.getItem2())
                        .lastModifiedDate(doc.getLastModifiedDate())
                        .country(doc.getCountry())
                        .userAgent(doc.getUserAgent())
                        .agreementVersion(doc.getAgreementVersion())
                        .termsText(doc.getTermsText())
                        .build()
        );
    }

    @Override
    public Uni<Integer> delete(String id, IUser user) {
        return repository.delete(UUID.fromString(id));
    }
}
