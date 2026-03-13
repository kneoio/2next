package com.semantyca.officeframe.service;

import com.semantyca.officeframe.dto.OrgCategoryDTO;
import com.semantyca.officeframe.dto.OrganizationDTO;
import com.semantyca.officeframe.model.OrgCategory;
import com.semantyca.officeframe.model.Organization;
import com.semantyca.officeframe.repository.OrgCategoryRepository;
import com.semantyca.officeframe.repository.OrganizationRepository;
import com.semantyca.core.model.cnst.LanguageCode;
import io.kneo.core.model.user.IUser;
import io.kneo.core.service.AbstractService;
import io.kneo.core.service.IRESTService;
import io.kneo.core.service.UserService;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
@ApplicationScoped
public class OrganizationService extends AbstractService<Organization, OrganizationDTO> implements IRESTService<OrganizationDTO> {
    private final OrganizationRepository repository;
    private final OrgCategoryRepository orgCategoryRepository;

    @Inject
    public OrganizationService(UserService userService,
                               OrganizationRepository repository,
                               OrgCategoryRepository orgCategoryRepository) {
        super(userService);
        this.repository = repository;
        this.orgCategoryRepository = orgCategoryRepository;
    }

    public Uni<List<OrganizationDTO>> getAll(final int limit, final int offset, LanguageCode languageCode) {
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

    public Uni<List<OrganizationDTO>> getPrimary(LanguageCode languageCode) {
        return repository.getAllPrimary()
                .chain(list -> Uni.join().all(
                        list.stream()
                                .map(this::mapToDTO)
                                .collect(Collectors.toList())
                ).andFailFast());
    }

    public Uni<Organization> get(String id) {
        return repository.findById(UUID.fromString(id));
    }

    public Uni<Organization> get(UUID uuid) {
        return repository.findById(uuid);
    }

    @Override
    public Uni<OrganizationDTO> getDTO(UUID id, IUser user, LanguageCode language) {
        return repository.findById(id).chain(this::mapToDTO);
    }

    @Override
    public Uni<OrganizationDTO> upsert(String id, OrganizationDTO dto, IUser user, LanguageCode code) {
        Organization doc = new Organization();
        doc.setIdentifier(dto.getIdentifier());
        doc.setOrgCategory(dto.getOrgCategory().getId());
        doc.setBizID(dto.getBizID());
        doc.setRank(dto.getRank());
        doc.setPrimary(dto.isPrimary());
        doc.setLocalizedName(dto.getLocalizedName());

        if ("new".equalsIgnoreCase(id) || id == null) {
            return repository.insert(doc, user).chain(this::mapToDTO);
        } else {
            return repository.update(UUID.fromString(id), doc, user).chain(this::mapToDTO);
        }
    }

    @Override
    public Uni<Integer> delete(String id, IUser user) {
        return repository.delete(UUID.fromString(id));
    }

    private Uni<OrganizationDTO> mapToDTO(Organization org) {
        return Uni.combine().all().unis(
                userRepository.getUserName(org.getAuthor()),
                userRepository.getUserName(org.getLastModifier()),
                orgCategoryRepository.findById(org.getOrgCategory())
        ).asTuple().onItem().transform(tuple -> {
            OrganizationDTO dto = OrganizationDTO.builder()
                    .id(org.getId())
                    .author(tuple.getItem1())
                    .regDate(org.getRegDate())
                    .lastModifier(tuple.getItem2())
                    .lastModifiedDate(org.getLastModifiedDate())
                    .isPrimary(org.isPrimary())
                    .identifier(org.getIdentifier())
                    .localizedName(org.getLocalizedName())
                    .bizID(org.getBizID())
                    .build();

            OrgCategory category = tuple.getItem3();
            dto.setOrgCategory(OrgCategoryDTO.builder()
                    .identifier(category.getIdentifier())
                    .localizedName(category.getLocalizedName())
                    .id(category.getId())
                    .build());

            return dto;
        });
    }
}