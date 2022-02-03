

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.http.javadsl.Http;
import akka.http.javadsl.ServerBinding;
import akka.stream.ActorMaterializer;
import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import com.sun.tools.javac.util.BasicDiagnosticFormatter;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Executor;

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
        ActorRef actorConfig = system.actorOf(Props.create(ActorConfig.class));
        final ActorMaterializer materializer = ActorMaterializer.create(system);
        final Http http = Http.get(system);
        ZooKeeper zk = null;

        try {
            zk= new ZooKeeper(args[INDEX_OF_ZOOKEEPER_ADDRESS_IN_ARGS], ZOOKEEPER_SESSION_TIMEOUT, null);
            new ZooKeeperWatcher(zk, actorConfig);
        } catch (InterruptedException | KeeperException e) {
            e.printStackTrace();
            System.exit(-1);
        }

        List<CompletionStage<ServerBinding>> bindings = new ArrayList<>();

        StringBuilder serverInfo = new StringBuilder("Servers online at\n");
        for (int i=1, i < args.length; i++) {
            try {
                HttpServer server = new HttpServer() {
                    @Override
                    public void bind(InetSocketAddress addr, int backlog) throws IOException {
                        
                    }

                    @Override
                    public void start() {

                    }

                    @Override
                    public void setExecutor(Executor executor) {

                    }

                    @Override
                    public Executor getExecutor() {
                        return null;
                    }

                    @Override
                    public void stop(int delay) {

                    }

                    @Override
                    public HttpContext createContext(String path, HttpHandler handler) {
                        return null;
                    }

                    @Override
                    public HttpContext createContext(String path) {
                        return null;
                    }

                    @Override
                    public void removeContext(String path) throws IllegalArgumentException {

                    }

                    @Override
                    public void removeContext(HttpContext context) {

                    }

                    @Override
                    public InetSocketAddress getAddress() {
                        return null;
                    }
                }
            }
        }
    }
}