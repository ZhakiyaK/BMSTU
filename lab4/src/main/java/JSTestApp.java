

import akka.NotUsed;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.http.javadsl.ConnectHttp;
import akka.http.javadsl.Http;
import akka.http.javadsl.ServerBinding;
import akka.http.javadsl.marshallers.jackson.Jackson;
import akka.http.javadsl.model.HttpRequest;
import akka.http.javadsl.model.HttpResponse;
import akka.http.javadsl.server.AllDirectives;
import akka.http.javadsl.server.Route;
import akka.pattern.Patterns;
import akka.stream.ActorMaterializer;
import akka.stream.javadsl.Flow;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import scala.concurrent.Future;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletionStage;


public class JSTestApp extends AllDirectives {
    public static final String ACTOR_SYSTEM_NAME = "js_test_app";

    public static void main(String[] args) throws IOException {
        ActorSystem actorSystem = ActorSystem.create(ACTOR_SYSTEM_NAME);
        ActorRef actorRouter = actorSystem.actorOf(Props.create(main.java.ActorRouter.class));

        final Http http = Http.get(actorSystem);
        final ActorMaterializer materializer = ActorMaterializer.create(actorSystem);
        JSTestApp instance = new JSTestApp();
        final Flow<HttpRequest, HttpResponse, NotUsed> routeFlow = instance.createRoute(actorRouter).flow(actorSystem, materializer);
        final CompletionStage<ServerBinding> binding;
        binding = http.bindAndHandle(
                routeFlow,
                ConnectHttp.toHost("localhost", 8080),
                materializer
        );
        System.out.println("Server online at http://localhost:8080/\nPress RETURN to stop...");
        System.in.read();
        binding.thenCompose(ServerBinding::unbind);
        binding.thenAccept(unbound -> actorSystem.terminate());

    }

    private Route createRoute(ActorRef actorRouter) {
        return route(
                path("test", () -> route(post(() -> entity(Jackson.unmarshaller(MessageTestPackage.class), message -> { actorRouter.tell(message, ActorRef.noSender());
                                    return complete("Test started!"); })))),
                path("result", () -> route(get( () -> parameter("packageId", (id) -> { Future<Object> result = Patterns.ask( actorRouter, new MessageGetTestPackageResult(id), 5000);
                                    return completeOKWithFuture(result, Jackson.marshaller()); })))));
    }

    static class MessageGetTestPackageResult {

        private final String packageID;

        public MessageGetTestPackageResult(String packageID) {
            this.packageID = packageID;
        }

        protected String getPackageID() {
            return packageID;}
    }

    static class MessageTestPackage {
        private final String packageID;
        private final String jsScript;
        private final String funcName;
        private final List<main.java.TestBody> tests;

        @JsonCreator
        public MessageTestPackage(
                @JsonProperty("packageID") String packageID,
                @JsonProperty("jsScript") String jsScript,
                @JsonProperty("functionName") String funcName,
                @JsonProperty("tests") List<main.java.TestBody> tests) {
            this.packageID = packageID;
            this.funcName = funcName;
            this.jsScript = jsScript;
            this.tests = tests;
        }

        protected List<main.java.TestBody> getTests() {
            return tests;
        }

        protected String getPackageID() {
            return packageID;
        }

        protected String getJsScript() {
            return jsScript;
        }

        protected String getFuncName() {
            return funcName;
        }


    }
}
