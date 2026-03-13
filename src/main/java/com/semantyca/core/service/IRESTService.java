package com.semantyca.core.service;

import com.semantyca.core.model.cnst.LanguageCode;
import com.semantyca.core.model.user.IUser;
import io.smallrye.mutiny.Uni;

import java.util.List;

public interface IRESTService<V> {

    Uni<Integer> getAllCount(IUser user);

    Uni<List<V>> getAll(int pageSize, int offset, LanguageCode languageCode);

}
