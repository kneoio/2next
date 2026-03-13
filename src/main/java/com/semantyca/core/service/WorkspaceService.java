package com.semantyca.core.service;

import com.semantyca.core.dto.document.LanguageDTO;
import com.semantyca.core.dto.document.UserModuleDTO;
import com.semantyca.core.model.Language;
import com.semantyca.core.model.UserModule;
import com.semantyca.core.model.user.IUser;
import com.semantyca.core.repository.LanguageRepository;
import com.semantyca.core.repository.ModuleRepository;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class WorkspaceService {
    private final LanguageRepository languageRepository;
    private final ModuleRepository repository;

    @Inject
    public WorkspaceService(LanguageRepository languageRepository, ModuleRepository repository) {
        this.languageRepository = languageRepository;
        this.repository = repository;
    }

    public Uni<List<UserModuleDTO>> getAvailableModules(IUser user) {
        Uni<List<UserModule>> listUni = repository.getAvailable(user);
        return listUni.onItem().transform(list -> list.stream()
                .map(doc ->
                        UserModuleDTO.builder()
                                .identifier(doc.getIdentifier())
                                .localizedName(doc.getLocalizedName())
                                .localizedDescription(doc.getLocalizedDescription())
                                .position(doc.getPosition())
                                .theme(doc.getTheme())
                                .localizedDescription(doc.getLocalizedDescription())
                                .build())
                .collect(Collectors.toList()));
    }

    public Uni<List<LanguageDTO>> getAvailableLanguages() {
        Uni<List<Language>> listUni = languageRepository.getAvailable();
        return listUni.onItem().transform(list -> list.stream()
                .map(doc ->
                        LanguageDTO.builder()
                                .code(doc.getCode())
                                .localizedName(doc.getLocalizedName())
                                .position(doc.getPosition())
                                .build())
                .collect(Collectors.toList()));
    }

}
