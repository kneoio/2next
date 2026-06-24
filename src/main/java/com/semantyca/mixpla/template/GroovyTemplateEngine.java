package com.semantyca.mixpla.template;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import org.codehaus.groovy.control.CompilerConfiguration;
import org.codehaus.groovy.control.customizers.SecureASTCustomizer;

import java.util.List;
import java.util.Map;

public class GroovyTemplateEngine {
    private final CompilerConfiguration config;

    public GroovyTemplateEngine() {
        SecureASTCustomizer secure = new SecureASTCustomizer();
        secure.setDisallowedImports(List.of(
                "java.io.File",
                "java.io.FileInputStream",
                "java.io.FileOutputStream",
                "java.io.PrintWriter",
                "java.lang.Runtime",
                "java.lang.ProcessBuilder",
                "java.lang.Process",
                "java.lang.ClassLoader",
                "java.lang.Thread",
                "java.lang.reflect.Method",
                "java.net.Socket",
                "java.net.ServerSocket",
                "java.net.URL",
                "java.net.HttpURLConnection",
                "groovy.lang.GroovyShell",
                "groovy.lang.GroovyClassLoader",
                "groovy.util.Eval"
        ));
        secure.setDisallowedReceivers(List.of(
                "java.lang.Runtime",
                "java.lang.ProcessBuilder",
                "java.lang.Process",
                "java.lang.ClassLoader",
                "java.lang.Thread",
                "java.lang.reflect.Method",
                "groovy.lang.GroovyShell",
                "groovy.lang.GroovyClassLoader",
                "groovy.util.Eval"
        ));

        config = new CompilerConfiguration();
        config.addCompilationCustomizers(secure);
    }

    public String render(String script, Map<String, Object> context, String draftSlug) {
        try {
            Binding binding = new Binding();
            if (context != null) {
                context.forEach(binding::setVariable);
            }
            GroovyShell shell = new GroovyShell(getClass().getClassLoader(), binding, config);
            Object result = shell.evaluate(script);
            return String.valueOf(result);
        } catch (Exception e) {
            String msg = e.getClass().getName() + ": " + (e.getMessage() == null ? "" : e.getMessage());

            StringBuilder contextInfo = new StringBuilder();
            if (context != null) {
                contextInfo.append("Context variables: ");
                context.keySet().forEach(key -> {
                    Object value = context.get(key);
                    String valueStr = value != null ? value.getClass().getSimpleName() : "null";
                    contextInfo.append(key).append("=").append(valueStr).append(", ");
                });
                if (contextInfo.length() > 2) {
                    contextInfo.setLength(contextInfo.length() - 2);
                }
            }

            String errorMsg = String.format("Failed to evaluate Groovy script '%s': %s. %s", draftSlug, msg, contextInfo);
            throw new RuntimeException(errorMsg, e);
        }
    }
}
