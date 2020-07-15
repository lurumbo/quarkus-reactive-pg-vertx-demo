package org.pigmalion;

import io.quarkus.vertx.web.Route;
import io.smallrye.mutiny.Multi;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.Message;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

public class UsersResource {

    @Inject
    EventBus bus;

    @Route(path = "/users", methods = HttpMethod.GET)
    public void query (RoutingContext rc) {
        bus.request("getUsers", null, new DeliveryOptions(), getAsyncResultHandler(rc));
    }

    protected Handler<AsyncResult<Message<Object>>> getAsyncResultHandler (RoutingContext rc) {
        return reply -> {
            rc.response().putHeader("Content-Type", "application/json");

            if (reply.succeeded()) {
                JsonArray response = (JsonArray) reply.result().body();
                rc.response().end(response.encode());
            } else { // por aca entra, por ej, si la address a la que se envio el mje por el bus no existe
                rc.response().end(new JsonObject().put("success", false).encode());
            }
        };
    }

}
