package com.semantyca.core.service.template;

import com.semantyca.core.util.ResourceUtil;
import io.quarkus.qute.Engine;
import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;
import io.quarkus.qute.Variant;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@ApplicationScoped
public class TemplateService {

    @Inject
    Engine engine;

    private final ConcurrentHashMap<String, Template> cache = new ConcurrentHashMap<>();

    public String render(String resourcePath, Map<String, Object> data) {
        Template template = cache.computeIfAbsent(resourcePath, this::parse);
        TemplateInstance instance = template.instance();
        if (data != null) {
            data.forEach(instance::data);
        }
        return instance.render();
    }

    private Template parse(String resourcePath) {
        String source = ResourceUtil.loadResourceAsString(resourcePath);
        String contentType = resourcePath.endsWith(".txt") ? "text/plain" : "text/html";
        return engine.parse(source, new Variant(Locale.ENGLISH, contentType, "utf-8"));
    }
}
