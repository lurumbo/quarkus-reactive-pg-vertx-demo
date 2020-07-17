package org.pigmalion;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.quarkus.runtime.Quarkus;
import io.quarkus.runtime.StartupEvent;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.json.Json;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Instance;

@ApplicationScoped
public class VerticlesDeployer {

    private static Logger logger = LoggerFactory.getLogger(VerticlesDeployer.class);

    public void init(@Observes StartupEvent e, Vertx vertx, Instance<AbstractVerticle> verticles) {
        try {
            logger.info("Inicializando sistemas...");

            for (AbstractVerticle verticle : verticles) {
                vertx.deployVerticle(verticle, ar -> {
                    if (!ar.succeeded()) {
                        logger.fatal("El sistema no pudo iniciar porque al menos un verticle no pudo ser deployado...");
                        Quarkus.asyncExit();
                    }
                });
            }

        } catch (Exception exc) {
            logger.error(exc);
        }
    }
}
