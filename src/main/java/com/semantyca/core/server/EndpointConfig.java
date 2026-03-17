package com.semantyca.core.server;

import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithDefault;

@ConfigMapping(prefix = "2next.endpoints")
public interface EndpointConfig {
    
    @WithDefault("")
    String enabled();
}
