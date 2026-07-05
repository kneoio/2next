package com.semantyca.officeframe.service;

import com.semantyca.core.model.user.SuperUser;
import com.semantyca.officeframe.dto.LabelDTO;
import com.semantyca.officeframe.dto.LabelFilterDTO;
import com.semantyca.officeframe.model.Label;
import com.semantyca.officeframe.repository.LabelRepository;
import com.semantyca.core.model.cnst.LanguageCode;
import com.semantyca.core.model.user.IUser;
import com.semantyca.core.service.AbstractService;
import com.semantyca.core.service.IRESTService;
import com.semantyca.core.service.UserService;
import com.semantyca.core.util.ColorUtil;
import com.semantyca.core.util.WebHelper;
import io.smallrye.mutiny.Uni;
import io.vertx.core.json.JsonObject;
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

    @Override
    public Uni<List<LabelDTO>> getAll(int pageSize, int offset, LanguageCode languageCode) {
        return getAll(pageSize, offset, SuperUser.build(), languageCode);
    }

    public Uni<List<LabelDTO>> getAll(final int limit, final int offset, IUser user, LanguageCode languageCode) {
        return repository.getAll(limit, offset, user.getId(), user.isSupervisor())
                .chain(labels -> Uni.join().all(
                        labels.stream()
                                .map(this::mapToDTO)
                                .collect(Collectors.toList())
                ).andFailFast());
    }

    public Uni<List<LabelDTO>> getAll(final int limit, final int offset, LabelFilterDTO filter, IUser user, LanguageCode languageCode) {
        return repository.getAll(limit, offset, filter, user.getId(), user.isSupervisor())
                .chain(labels -> Uni.join().all(
                        labels.stream()
                                .map(this::mapToDTO)
                                .collect(Collectors.toList())
                ).andFailFast());
    }

    public Uni<Integer> getAllCount(IUser user) {
        return repository.getAllCount(user.getId(), user.isSupervisor());
    }

    public Uni<Integer> getAllCount(IUser user, LabelFilterDTO filter) {
        return repository.getAllCount(filter, user.getId(), user.isSupervisor());
    }

    public Uni<List<LabelDTO>> getOfCategory(String categoryName, IUser user, LanguageCode languageCode) {
        return repository.getOfCategory(categoryName, user.getId(), user.isSupervisor())
                .chain(labels -> {
                    if (labels.isEmpty()) return Uni.createFrom().item(List.of());
                    return Uni.join().all(
                            labels.stream()
                                    .map(this::mapToDTO)
                                    .collect(Collectors.toList())
                    ).andFailFast();
                });
    }

    public Uni<List<LabelDTO>> getLabels(UUID id, String type) {
        return repository.findForDocument(id, type)
                .chain(labels -> {
                    if (labels.isEmpty()) return Uni.createFrom().item(List.of());
                    return Uni.join().all(
                            labels.stream()
                                    .map(this::mapToDTO)
                                    .collect(Collectors.toList())
                    ).andFailFast();
                });
    }

    public Uni<LabelDTO> getDTO(UUID uuid, IUser user, LanguageCode language) {
        return repository.findById(uuid).chain(this::mapToDTO);
    }

    public Uni<Label> getById(UUID uuid) {
        return repository.findById(uuid);
    }

    public Uni<Label> findByIdentifier(String identifier) {
        return repository.findByIdentifier(identifier);
    }
    
    @Override
    public Uni<LabelDTO> upsert(String id, LabelDTO dto, IUser user, LanguageCode code) {
        String generatedSlug = WebHelper.generateSlug(dto.getLocalizedName());
        if (generatedSlug.isEmpty() && dto.getIdentifier() != null && !dto.getIdentifier().isBlank()) {
            generatedSlug = WebHelper.generateSlug(dto.getIdentifier());
        }
        final String slug = generatedSlug;

        if ("new".equalsIgnoreCase(id) || id == null) {
            JsonObject localizedNameJson = JsonObject.mapFrom(dto.getLocalizedName());
            return repository.findByCategoryAndNameOrSlug(dto.getCategory(), slug, localizedNameJson, user.getId(), user.isSupervisor())
                    .chain(existing -> {
                        if (existing != null) {
                            dto.getLocalizedName().forEach((lang, val) ->
                                    existing.getLocalizedName().putIfAbsent(lang, val));
                            return repository.update(existing.getId(), existing, user).chain(this::mapToDTO);
                        }
                        return repository.insert(buildLabel(dto, slug, user), user).chain(this::mapToDTO);
                    });
        } else {
            UUID uuid = UUID.fromString(id);
            return repository.findById(uuid).chain(existing -> {
                if (existing.getOwner() != null && !existing.getOwner().equals(user.getId()) && !user.isSupervisor()) {
                    return Uni.createFrom().failure(new SecurityException("Not permitted to edit this label"));
                }
                return repository.update(uuid, buildLabel(dto, slug, user), user).chain(this::mapToDTO);
            });
        }
    }

    private Label buildLabel(LabelDTO dto, String slug, IUser user) {
        Label doc = new Label();
        doc.setIdentifier(slug);
        doc.setParent(dto.getParent());
        doc.setCategory(dto.getCategory());
        doc.setLocalizedName(dto.getLocalizedName());
        doc.setHidden(dto.isHidden());
        doc.setOwner(user.isSupervisor() ? null : user.getId());
        if (dto.getColor() == null || dto.getColor().isBlank()) {
            String[] pair = ColorUtil.generateContrastColorPair();
            doc.setColor(pair[0]);
            doc.setFontColor(pair[1]);
        } else {
            doc.setColor(dto.getColor());
            doc.setFontColor(dto.getFontColor() != null ? dto.getFontColor()
                    : ColorUtil.contrastingFontColor(dto.getColor()));
        }
        return doc;
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
                        .owner(label.getOwner())
                        .build()
        );
    }

    @Override
    public Uni<Integer> delete(String id, IUser user) {
        UUID uuid = UUID.fromString(id);
        return repository.findById(uuid).chain(existing -> {
            if (existing.getOwner() != null && !existing.getOwner().equals(user.getId()) && !user.isSupervisor()) {
                return Uni.createFrom().failure(new SecurityException("Not permitted to delete this label"));
            }
            return repository.delete(uuid);
        });
    }
}