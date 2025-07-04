package io.kneo.core.service;

import io.kneo.core.dto.AbstractDTO;
import io.kneo.core.dto.DocumentAccessDTO;
import io.kneo.core.dto.rls.RLSDTO;
import io.kneo.core.localization.LanguageCode;
import io.kneo.core.model.DataEntity;
import io.kneo.core.model.embedded.DocumentAccessInfo;
import io.kneo.core.model.user.IUser;
import io.kneo.core.repository.UserRepository;
import io.kneo.core.repository.exception.DocumentModificationAccessException;
import io.smallrye.mutiny.Uni;

import java.time.Duration;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public abstract class AbstractService<T, V> {
    protected static final Duration TIMEOUT = Duration.ofSeconds(5);
    protected UserRepository userRepository;
    protected UserService userService;

    public AbstractService() {

    }

    public AbstractService(UserService userService) {
        this.userService = userService;
    }

    @Deprecated
    public AbstractService(UserRepository userRepository, UserService userService) {
        this.userRepository = userRepository;
        this.userService = userService;
    }

    //TODO keep temporarily until all migrate to Uni<Integer> getAllCount(IUser user)
    Uni<Integer> getAllCount(){
        return Uni.createFrom().item(0);
    }

    public abstract Uni<V> getDTO(UUID id, IUser user, LanguageCode language);

    public Uni<V> upsert(String id, V dto, IUser user, LanguageCode code) throws DocumentModificationAccessException {
         return Uni.createFrom().failure(new RuntimeException("The upsert is not implemented"));
    };

    public abstract Uni<Integer> delete(String id, IUser user) throws DocumentModificationAccessException;

    protected void setDefaultFields(AbstractDTO dto, DataEntity<UUID> doc) {
        dto.setId(doc.getId());
        dto.setAuthor(userService.getName(doc.getAuthor()).await().atMost(TIMEOUT));
        dto.setRegDate(doc.getRegDate());
        dto.setLastModifier(userService.getName(doc.getLastModifier()).await().atMost(TIMEOUT));
        dto.setLastModifiedDate(doc.getLastModifiedDate());
    }

    protected DocumentAccessDTO mapToDocumentAccessDTO(DocumentAccessInfo doc) {
        return DocumentAccessDTO.builder()
                .userId(doc.getUserId())
                .canEdit(doc.getCanEdit())
                .canDelete(doc.getCanDelete())
                .userLogin(doc.getUserLogin())
                .IsSu(doc.isIsSu())
                .build();
    }
}