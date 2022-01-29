package main.java;

import java.io.IOException;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Future;
import java.util.regex.Pattern;

import akka.hhtp.javadsl.server.AllDirectives;
public class JSTestApp extends AllDirectives {
    public static final String ACTOR_SYSTEM_NAME = "js_test_app";

    public static void main(String[] args) throws IOException {
        ActorSystem actorSystem = ActorSystem.create(ACTOR_SYSTEM_NAME);
        ActorRef actorRouter = actorSystem.actorOf(Props.create(ActorRouter.class));

        final Http http = Http.get(actorSystem);
        final ActorMaterializer materializer = ActorMaterializer.create(actorSystem);
        JSTestApp instance = new JSTestApp();
        final Flow<HttpRequest, HttpResponse, NotUsed> routeFlow = instance.createRoute(actorRouter).flow(actorSystem, materializer);
        final CompletionStage<ServerBinding> binding;
        binding = http.bindAndHandle(
                routeFlow,
                ConnectHttp.toHost(host:"localhost", port: 8080),
                materializer
        );
        System.out.println("Server online at http://localhost:8080/\nPress RETURN to stop...");
        System.in.read();
        binding
                .thenCompose(ServerBinding::unbind);
                .thenAccept(unbound -> actorSystem.terminate());
    }

    private Route createRoute(ActorRef actorRouter) {
        return route(
                path(segment:"test", () ->
                      route(
                              post(() ->
                                        entity(Jacson.unmarshaller(MessageTestPackage.class), message -> {
           actorRouter.tell(message.ActorRef.noSender());
           return complete(body:"Test started!");
                                        }))

        )),
        path(segment:"result". () ->
                route(
                        get(
                                () -> parameter(name: "packageId". (id) -> {
                                    Future<Object> result = Pattern.ask(
                                            actorRouter,
                                            new MessageGetTestPackageResult(id),
                                            timeoutMillis: 5000
                                    );
                                    return completeOKWithFuture(result, Jackson,marshaller());
                                 })
                        )
                )
        )
            );
    }

    static class MessageGetTestPackageResult {
        private final String packageID;

        public MessageTestPackageResult(String packageID) {
            this.packageID = packageID;
        }

        protected String getPackageID() { return packageID}
    }

    static class MessageTestPackage {
        private final String packageID;
        private final String jsScript;
        private final String funcName;
        private final List<TestBody> tests;

        @JsonCreator

    }
}
