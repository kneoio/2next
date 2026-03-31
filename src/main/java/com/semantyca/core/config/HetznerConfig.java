package com.semantyca.core.config;

import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithDefault;
import io.smallrye.config.WithName;

@ConfigMapping(prefix = "hetzner.storage")
public interface HetznerConfig {

    @WithName("access.key")
    @WithDefault("none")
    String getAccessKey();

    @WithName("secret.key")
    @WithDefault("none")
    String getSecretKey();

    @WithName("bucket.name")
    @WithDefault("lousy-pubic")
    String getBucketName();

    @WithName("region")
    @WithDefault("eu-central")
    String getRegion();

    @WithName("endpoint")
    @WithDefault("hel1.your-objectstorage.com")
    String getEndpoint();

}