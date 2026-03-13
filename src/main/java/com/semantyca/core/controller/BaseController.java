
package com.semantyca.core.controller;

import com.semantyca.core.model.cnst.LanguageCode;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class BaseController {
    protected final Logger LOGGER = LoggerFactory.getLogger(BaseController.class);



    protected static LanguageCode resolveLanguage(RoutingContext rc) {
        try {
            return LanguageCode.valueOf(rc.acceptableLanguages().get(0).value().toUpperCase());
        } catch (Exception e) {
            return LanguageCode.en;
        }
    }
}
