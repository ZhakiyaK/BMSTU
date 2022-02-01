import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;

import java.io.IOException;

public class AverageHttpResponseTimeApp {
    private static final String QUERY_PARAMETER_URL = "testUrl";
    private static final String QUERY_PARAMETER_COUNT = "count";

    private static final int TIMEOUT_MILLISEC = 5000;
    private static final int MAP_PARALLELISM_FOR_EACH_GET_REQUEST = 1;
    private static final int PPORT = 8080;

    public static void main(String[] args) throws IOException {
        System.out.println("start!");
        ActorSystem system = ActorSystem.create("routes");
        ActorRef actor = system.actorOf(Props.create(ActorCache.class));


    }
}