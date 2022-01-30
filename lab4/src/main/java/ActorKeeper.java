import akka.actor.AbstractActor;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.*;

public class ActorKeeper extends AbstractActor {
    private final Map<String, List<main.java.TestResult>> results = new HashMap<>();

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(ActorTester.MessageStoreTestResult.class,
                        this::storeResult
                )
                .match(JSTestApp.MessageGetTestPackageResult.class, req -> sender().tell(new MessageReturnResults(req.getPackageID(), results.get(req.getPackageID()));           --(new MessageReturnResults(req.getPackageID(), results.get(req.getPackageID())), self()))
                .build();
    }

    private void storeResult(ActorTester.MessageStoreTestResult m) {
        String packageID = m.getPackageID();
        if (results.containsKey(packageID)) {
            results.get(packageID).add(m.getTestResult());
        }
        else {
            results.put(
                    m.getPackageId(),
                    new ArrayList<>(
                            Collections.singleton(m.getTestResult())
                    )
            );
        }
        System.out.println("Received message: " + m);
    }

    static class MessageReturnResults {
        private final String packageID;
        private final List<main.java.TestResult> results;

        @JsonCreator
        public MessageReturnResults(
                @JsonProperty("packageId") String packageID;
                @JsonProperty("results") List<main.java.TestResult> results) {
            this.packageID = packageID;
            this.results = results;
        }

        public String getPackageID() {
            return packageID;
        }

        public List<main.java.TestResult> getResults() {
            return results;
        }

    }
}
