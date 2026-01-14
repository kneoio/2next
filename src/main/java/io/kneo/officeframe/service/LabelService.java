package io.kneo.officeframe.service;

import io.kneo.core.localization.LanguageCode;
import io.kneo.core.model.user.IUser;
import io.kneo.core.service.AbstractService;
import io.kneo.core.service.IRESTService;
import io.kneo.core.service.UserService;
import io.kneo.core.util.WebHelper;
import io.kneo.officeframe.dto.LabelDTO;
import io.kneo.officeframe.dto.LabelFilterDTO;
import io.kneo.officeframe.model.Label;
import io.kneo.officeframe.repository.LabelRepository;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@ApplicationScoped
public class LabelService extends AbstractService<Label, LabelDTO> implements IRESTService<LabelDTO> {
    private final LabelRepository repository;

    @Inject
    public LabelService(UserService userService, LabelRepository repository) {
        super(userService);
        this.repository = repository;
    }

    public Uni<List<LabelDTO>> getAll(final int limit, final int offset, LanguageCode languageCode) {
        return repository.getAll(limit, offset)
                .chain(labels -> Uni.join().all(
                        labels.stream()
                                .map(this::mapToDTO)
                                .collect(Collectors.toList())
                ).andFailFast());
    }

    public Uni<List<LabelDTO>> getAll(final int limit, final int offset, LabelFilterDTO filter, LanguageCode languageCode) {
        return repository.getAll(limit, offset, filter)
                .chain(labels -> Uni.join().all(
                        labels.stream()
                                .map(this::mapToDTO)
                                .collect(Collectors.toList())
                ).andFailFast());
    }

    public Uni<Integer> getAllCount(IUser user) {
        return repository.getAllCount();
    }

    public Uni<Integer> getAllCount(IUser user, LabelFilterDTO filter) {
        return repository.getAllCount(filter);
    }

    public Uni<List<LabelDTO>> getOfCategory(String categoryName, LanguageCode languageCode) {
        return repository.getOfCategory(categoryName)
                .chain(labels -> Uni.join().all(
                        labels.stream()
                                .map(this::mapToDTO)
                                .collect(Collectors.toList())
                ).andFailFast());
    }

    public Uni<List<LabelDTO>> getLabels(UUID id, String type) {
        return repository.findForDocument(id, type)
                .chain(labels -> Uni.join().all(
                        labels.stream()
                                .map(this::mapToDTO)
                                .collect(Collectors.toList())
                ).andFailFast());
    }

    public Uni<LabelDTO> getDTO(UUID uuid, IUser user, LanguageCode language) {
        return repository.findById(uuid).chain(this::mapToDTO);
    }

    public Uni<Label> getById(UUID uuid) {
        return repository.findById(uuid);
    }

    
    @Override
    public Uni<LabelDTO> upsert(String id, LabelDTO dto, IUser user, LanguageCode code) {
        Label doc = new Label();
        doc.setIdentifier(dto.getIdentifier());
        doc.setParent(dto.getParent());
        doc.setCategory(dto.getCategory());
        doc.setLocalizedName(dto.getLocalizedName());
        doc.setHidden(dto.isHidden());
        doc.setColor(dto.getColor());
        doc.setFontColor(dto.getFontColor());
        doc.setIdentifier(WebHelper.generateSlug(dto.getLocalizedName()));

        if ("new".equalsIgnoreCase(id) || id == null) {
            return repository.insert(doc, user).chain(this::mapToDTO);
        } else {
            return repository.update(UUID.fromString(id), doc, user).chain(this::mapToDTO);
        }
    }

    private Uni<LabelDTO> mapToDTO(Label label) {
        return Uni.combine().all().unis(
                userService.getName(label.getAuthor()),
                userService.getName(label.getLastModifier())
        ).asTuple().onItem().transform(tuple ->
                LabelDTO.builder()
                        .id(label.getId())
                        .author(tuple.getItem1())
                        .regDate(label.getRegDate())
                        .lastModifier(tuple.getItem2())
                        .lastModifiedDate(label.getLastModifiedDate())
                        .identifier(label.getIdentifier())
                        .localizedName(label.getLocalizedName())
                        .category(label.getCategory())
                        .parent(label.getParent())
                        .color(label.getColor())
                        .fontColor(label.getFontColor())
                        .hidden(label.isHidden())
                        .build()
        );
    }

    @Override
    public Uni<Integer> delete(String id, IUser user) {
        return repository.delete(UUID.fromString(id));
    }
}