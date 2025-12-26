package io.kneo.officeframe.service;

import io.kneo.core.localization.LanguageCode;
import io.kneo.core.model.user.IUser;
import io.kneo.core.service.AbstractService;
import io.kneo.core.service.IRESTService;
import io.kneo.core.service.UserService;
import io.kneo.core.util.WebHelper;
import io.kneo.officeframe.dto.GenreDTO;
import io.kneo.officeframe.dto.GenreFilterDTO;
import io.kneo.officeframe.model.Genre;
import io.kneo.officeframe.repository.GenreRepository;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@ApplicationScoped
public class GenreService extends AbstractService<Genre, GenreDTO> implements IRESTService<GenreDTO> {
    private final GenreRepository repository;

    @Inject
    public GenreService(UserService userService, GenreRepository repository) {
        super(userService);
        this.repository = repository;
    }

    public Uni<List<GenreDTO>> getAll(final int limit, final int offset, LanguageCode languageCode) {
        return repository.getAll(limit, offset)
                .chain(list -> Uni.join().all(
                        list.stream()
                                .map(this::mapToDTO)
                                .collect(Collectors.toList())
                ).andFailFast());
    }

    public Uni<List<GenreDTO>> getAll(final int limit, final int offset, GenreFilterDTO filter, LanguageCode languageCode) {
        return repository.getAll(limit, offset, filter)
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

    public Uni<Integer> getAllCount(IUser user, GenreFilterDTO filter) {
        return repository.getAllCount(filter);
    }

    @Override
    public Uni<GenreDTO> getDTO(UUID uuid, IUser user, LanguageCode language) {
        return repository.findById(uuid).chain(this::mapToDTO);
    }

    @Override
    public Uni<GenreDTO> upsert(String id, GenreDTO dto, IUser user, LanguageCode code) {
        Genre doc = new Genre();
        doc.setIdentifier(WebHelper.generateSlug(dto.getLocalizedName()));
        doc.setRank(dto.getRank() != null ? dto.getRank() : 999);
        doc.setLocalizedName(dto.getLocalizedName());
        doc.setColor(dto.getColor());
        doc.setFontColor(dto.getFontColor());
        doc.setParent(dto.getParent());

        if ("new".equalsIgnoreCase(id) || id == null) {
            return repository.insert(doc, user).chain(this::mapToDTO);
        } else {
            return repository.update(UUID.fromString(id), doc, user).chain(this::mapToDTO);
        }
    }

    private Uni<GenreDTO> mapToDTO(Genre doc) {
        return Uni.combine().all().unis(
                userService.getName(doc.getAuthor()),
                userService.getName(doc.getLastModifier())
        ).asTuple().onItem().transform(tuple ->
                GenreDTO.builder()
                        .id(doc.getId())
                        .author(tuple.getItem1())
                        .regDate(doc.getRegDate())
                        .lastModifier(tuple.getItem2())
                        .lastModifiedDate(doc.getLastModifiedDate())
                        .identifier(doc.getIdentifier())
                        .localizedName(doc.getLocalizedName())
                        .rank(doc.getRank())
                        .color(doc.getColor())
                        .fontColor(doc.getFontColor())
                        .parent(doc.getParent())
                        .build()
        );
    }

    @Override
    public Uni<Integer> delete(String id, IUser user) {
        return repository.delete(UUID.fromString(id));
    }
}
