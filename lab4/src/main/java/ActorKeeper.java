package main.java;

import akka.actor.AbstractActor;

import java.util.*;

public class ActorKeeper extends AbstractActor {
    private final Map<String, List<main.java.TestResult>> results = new HashMap<>();

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(main.java.ActorTester.MessageStoreTestResult.class,
                        this.storeResult
                )
                .match(
                        main.java.JSTestApp.MessageGetTestPackageResult.class,
                        req -> sender().tell(
                                new MessageReturnResults(
                                        req.getPackageID(),
                                        results.get(req.getPackageID())
                                ),
                                self()
                        )
                )
                .build();
    }

    private void storeResult(main.java.ActorTester.MessageStoreTestResult m) {
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
        private final List<TestResult> results;

        public MessageReturnResults(
                @JsonProperty("packageId") String packageID;
                @JsonProperty("results") List<TestResult> results) {
            this.packageID = packageID;
            this.results = results;
        }

        public String getPackageID() {
            return packageID;
        }

        public List<TestResult> getResults() {
            return results;
        }

    }
}
