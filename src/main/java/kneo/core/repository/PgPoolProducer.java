package kneo.core.repository;

import io.quarkus.arc.DefaultBean;
import io.quarkus.arc.Unremovable;
import io.vertx.mutiny.pgclient.PgPool;
import io.vertx.pgclient.PgConnectOptions;
import io.vertx.sqlclient.PoolOptions;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@ApplicationScoped
public class PgPoolProducer {

    @Inject
    @ConfigProperty(name = "quarkus.datasource.reactive.url")
    String jdbcUrl;

    @Produces
    @ApplicationScoped
    @DefaultBean
    @Unremovable
    public PgPool createPgPool() {
        PgConnectOptions connectOptions = PgConnectOptions.fromUri(jdbcUrl);
        PoolOptions poolOptions = new PoolOptions().setMaxSize(20);
        return PgPool.pool(connectOptions, poolOptions);
    }
}
