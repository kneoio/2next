package io.kneo.core.service;

import io.kneo.core.localization.LanguageCode;
import io.kneo.core.model.user.IUser;
import io.smallrye.mutiny.Uni;

import java.util.List;

public interface IRESTService<V> {

    Uni<Integer> getAllCount(IUser user);

    Uni<List<V>> getAll(int pageSize, int offset, LanguageCode languageCode);

}
