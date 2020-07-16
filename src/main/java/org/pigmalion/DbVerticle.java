package org.pigmalion;

import io.quarkus.vertx.ConsumeEvent;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.pgclient.PgConnectOptions;
import io.vertx.pgclient.PgPool;
import io.vertx.sqlclient.PoolOptions;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class DbVerticle extends AbstractVerticle {

    private PgPool client;

    private final Logger logger = LoggerFactory.getLogger(DbVerticle.class);

    @Override
    public void start (Promise<Void> promise) throws Exception {

        PgConnectOptions connectOptions = new PgConnectOptions()
                .setPort(5432)
                .setHost("localhost")
                .setDatabase("usersdb")
                .setUser("postgres")
                .setPassword("1111");

        PoolOptions poolOptions = new PoolOptions().setMaxSize(5);

        client = PgPool.pool(vertx, connectOptions, poolOptions);

        client.getConnection(ar -> {
            if (ar.succeeded()) {
                vertx.eventBus().consumer("GET_USERS", this::getUsers);
                logger.info("------ Postgres getConnection success ----------");
                promise.complete();
            } else {
                logger.error("------ Postgres getConnection error ---------" + ar.cause());
                promise.fail(ar.cause());
            }
        });

    }

    @ConsumeEvent("getUsers")
    public void getUsers(Message<JsonObject> message) {
        String rawQuery = "SELECT * FROM USERS";
        logger.info("Executed query: " + rawQuery);
        client
                .query(rawQuery)
                .execute( res -> {
                    if (res.succeeded()) {
                        RowSet<Row> rows = res.result();
                        JsonArray userArrayList = new JsonArray();
                        for (Row row : rows) {
                            Long id = row.getLong("id");
                            String name = row.getString("name");
                            userArrayList.add(new JsonObject().put("id", id).put("name", name));
                        }
                        message.reply(userArrayList);
                    } else {
                        logger.error("------ getUsers message error ------ " + res.cause().toString());
                    }
                });
    }


}
