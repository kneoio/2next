package com.semantyca.core.service;

import com.semantyca.core.dto.document.LanguageDTO;
import com.semantyca.core.model.Language;
import com.semantyca.core.repository.LanguageRepository;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class WorkspaceService {
    private final LanguageRepository languageRepository;

    @Inject
    public WorkspaceService(LanguageRepository languageRepository) {
        this.languageRepository = languageRepository;
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
