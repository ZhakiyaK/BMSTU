import akka.NotUsed;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.http.javadsl.ConnectHttp;
import akka.http.javadsl.Http;
import akka.http.javadsl.ServerBinding;
import akka.http.javadsl.model.HttpRequest;
import akka.http.javadsl.model.HttpResponse;
import akka.http.javadsl.model.Query;
import akka.japi.Pair;
import akka.pattern.Patterns;
import akka.stream.ActorMaterializer;
import akka.stream.javadsl.Flow;

import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

public class AverageHttpResponseTimeApp {
    private static final String QUERY_PARAMETER_URL = "testUrl";
    private static final String QUERY_PARAMETER_COUNT = "count";

    private static final int TIMEOUT_MILLISEC = 5000;
    private static final int MAP_PARALLELISM_FOR_EACH_GET_REQUEST = 1;
    private static final int PORT = 8080;

    public static void main(String[] args) throws IOException {
        System.out.println("start!");
        ActorSystem system = ActorSystem.create("routes");
        ActorRef actor = system.actorOf(Props.create(ActorCache.class));

        final Http http = Http.get(system);
        final ActorMaterializer materializer = ActorMaterializer.create(system);
        final Flow<HttpRequest, HttpResponse, NotUsed> routeFlow = flowHttpRequest(materializer, actor);
        final CompletionStage<ServerBinding> binding = http.bindAndHandle(
                routeFlow,
                ConnectHttp.toHost("localhost", PORT),
                materializer);
        System.out.println("Server online in http://localhost:" + PORT + "/\nPress RETURN to stop...");
        System.in.read();
        binding.thenCompose(ServerBinding::unbind);
        binding.thenAccept(unbound -> system.terminate());
    }

    private static Flow<HttpRequest,HttpResponse,NotUsed> flowHttpRequest(ActorMaterializer materializer, ActorRef actor) {
        return Flow.of(HttpRequest.class).map(req -> {
            Query query = req.getUri().query();
            String url = query.get(QUERY_PARAMETER_URL).get();
            int count = Integer.parseInt(query.get(QUERY_PARAMETER_COUNT).get());
            return new Pair<>(url,count);
        })
                                          .mapAsync(MAP_PARALLELISM_FOR_EACH_GET_REQUEST, req -> Patterns.ask(
                                                                                                                actor,
                                                                                                                new MessageGetResult(req.first()),
                                                                                                                java.time.Duration.ofMillis(TIMEOUT_MILLISEC))
                                                                                                          .thenCompose( res -> {
                                                                                                              if (((Optional<Long>) res).isPresent()) {
                                                                                                                  return CompletableFuture.completedFuture(new Pair<>(req.first(), ((Optional))))
                                                                                                              }
                                                                                                          }
                                          ))
    }
}