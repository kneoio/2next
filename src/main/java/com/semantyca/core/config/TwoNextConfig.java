package com.semantyca.core.config;

import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithDefault;
import io.smallrye.config.WithName;

@ConfigMapping(prefix = "2next")
public interface TwoNextConfig {

    @WithName("controller.upload.files.path")
    @WithDefault("controller-uploads")
    String getPathUploads();

    @WithName("request.logger.enable")
    @WithDefault("false")
    boolean getRequestLoggerEnabled();

}