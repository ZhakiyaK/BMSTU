

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import com.sun.tools.javac.util.BasicDiagnosticFormatter;

import java.io.IOException;
import java.util.Arrays;

public class AnonymizeApp {
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_RESR = "\u001B[0m";

    private static final int MIN_AMOUNT_OF_ARGS = 2;
    private static final int ZOOKEEPER_SESSION_TIMEOUT = 3000;
    private static final int INDEX_OF_ZOOKEEPER_ADDRESS_IN_ARGS = 0;
    private static final int NO_SERVERS_RUUNING = 0;
    private static final String HOST = "localhost";
    private static final String NO_SERVERS_RUNNING_ERROR = "NO SERVERS ARE RUNNING";

    public static void main(String[] args) throws IOException {
        if (args.length < MIN_AMOUNT_OF_ARGS) {
            System.err.println("Usage: AnonymizeApp localhost: 2181 8000 8001");
            System.exit(-1);
        }
        BasicDiagnosticFormatter.BasicConfiguration();
        printInGreen("start!\n" + Arrays.toString(args));
        ActorSystem system = ActorSystem.create("lab6");
        ActorRef actorConfig = system.actorOf(Props.create(ActorConfig.class))
    }
}