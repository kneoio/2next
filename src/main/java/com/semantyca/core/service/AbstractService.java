package com.semantyca.core.service;

import com.semantyca.core.dto.AbstractDTO;
import com.semantyca.core.dto.DocumentAccessDTO;
import com.semantyca.core.model.cnst.LanguageCode;
import com.semantyca.core.model.DataEntity;
import com.semantyca.core.model.embedded.DocumentAccessInfo;
import com.semantyca.core.model.user.IUser;
import com.semantyca.core.repository.UserRepository;
import com.semantyca.core.repository.exception.DocumentModificationAccessException;
import io.smallrye.mutiny.Uni;

import java.time.Duration;
import java.util.UUID;

public abstract class AbstractService<T, V> {
    protected static final Duration TIMEOUT = Duration.ofSeconds(5);
    protected UserRepository userRepository;
    protected UserService userService;

    public AbstractService() {

    }

    public AbstractService(UserService userService) {
        this.userService = userService;
    }

    public Uni<V> getDTO(UUID id, IUser user, LanguageCode language){
        return Uni.createFrom().failure(new RuntimeException("not implemented"));
    };

    public Uni<V> upsert(String id, V dto, IUser user, LanguageCode code) throws DocumentModificationAccessException {
         return Uni.createFrom().failure(new RuntimeException("not implemented"));
    };

    public Uni<Integer> delete(String id, IUser user) throws DocumentModificationAccessException{
        return Uni.createFrom().failure(new RuntimeException("not implemented"));
    }

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