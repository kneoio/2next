package com.semantyca.core.config;

import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithDefault;
import io.smallrye.config.WithName;

import java.util.List;

@ConfigMapping(prefix = "IINext")
public interface IINextConfig {

    @WithName("controller.upload.files.path")
    @WithDefault("controller-uploads")
    String getPathUploads();


}