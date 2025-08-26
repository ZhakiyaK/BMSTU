import akka.actor.ActorRef;
import org.apache.zookeeper.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ZooKeeperWatcher implements Watcher {
    private static final String SERVERS_PATH = "/servers";

    private final ZooKeeper zooKeeper;
    private final ActorRef actorConfig;

    public ZooKeeperWatcher(ZooKeeper zooKeeper, ActorRef actorConfig) throws IOException, InterruptedException, KeeperException {
        this.actorConfig = actorConfig;
        this.zooKeeper = zooKeeper;

        byte[] data = this.zooKeeper.getData(SERVERS_PATH, true, null);
        AnonymizeApp.printInGreen("servers" + " data=" + new String(data));
        sendServers();
    }

    private void sendServers() throws InterruptedException, KeeperException {
        List<String> servers = new ArrayList<>();
        for (String s : zooKeeper.getChildren(SERVERS_PATH, this)) {
            servers.add(new String(zooKeeper.getData(SERVERS_PATH + "/" + s, false, null)));
        }
        actorConfig.tell(new MessageSendServersList(servers), ActorRef.noSender());
    }

    @Override
    public void process(WatchedEvent watchedEvent) {
        try {
            zooKeeper.getChildren(SERVERS_PATH, this);
            sendServers();
        } catch (InterruptedException | KeeperException e) {
            e.printStackTrace();
        }
    }

    static class MessageSendServersList {
        private final List<String> servers;

        MessageSendServersList(List<String> servers) {
            this.servers = servers;
        }

        public List<String> getServers() {
            return servers;
        }
    }
}
