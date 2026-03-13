package com.semantyca.core.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.semantyca.core.dto.document.LanguageDTO;
import com.semantyca.core.dto.document.UserModuleDTO;
import com.semantyca.core.dto.view.View;
import com.semantyca.core.model.user.IUser;
import com.semantyca.core.server.EnvConst;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class WorkspacePage extends AbstractPage {

    public WorkspacePage(IUser user, List<LanguageDTO> langs, List<UserModuleDTO> modules) {
        addPayload("application_name", String.format("%s %s", EnvConst.APP_ID, EnvConst.VERSION));
        addPayload("user", user.getUserName());
        addPayload("available_languages", new View<>(langs));
        addPayload("available_modules", new View<>(modules));
    }
}
