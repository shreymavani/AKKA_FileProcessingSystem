package org.example.getFileActor;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class GetFileActor extends AbstractBehavior<String> {

    private static final int MAX_BATCH_SIZE = 1024;
    private final ActorRef<String> filterFileActorRef;
    public GetFileActor(ActorContext<String> context, ActorRef<String> filterFileActorRef) {
        super(context);
        this.filterFileActorRef = filterFileActorRef;
    }

    public static Behavior<String> create(ActorRef<String> filterFileActorRef) {
        return Behaviors.setup(context -> new GetFileActor(context, filterFileActorRef));
    }

    @Override
    public Receive<String> createReceive() {
        return newReceiveBuilder()
                .onMessage(String.class, this::onMessage)
                .build();
    }

    private Behavior<String> onMessage(String directoryPath) throws Exception {
        File directory = new File(directoryPath);                                                //path to directory

        File[] files = directory.listFiles();

        assert files != null;
        for (File file : files) {

            try {

                long fileSize = file.length();
                StringBuilder data= new StringBuilder();
                if (fileSize > MAX_BATCH_SIZE) {

                    try (BufferedReader reader = new BufferedReader(new FileReader(file))) {

                        int numBatches = (int) Math.ceil((double) fileSize / MAX_BATCH_SIZE);   // Calculate the number of batches

                        for (int i = 0; i < numBatches; i++) {
                            // Read the next batch
                            char[] batch = new char[MAX_BATCH_SIZE];
                            int read = reader.read(batch, 0, MAX_BATCH_SIZE);


                            String batchString = new String(batch, 0, read);              // Process the batch
                            data.append(batchString);

                        }
                    }
                } else {

                    try (BufferedReader reader = new BufferedReader(new FileReader(file))) {      // File size is less than or equal to the maximum batch size
                        // Read the entire file in one go
                        StringBuilder entireFile = new StringBuilder();
                        String line;
                        while ((line = reader.readLine()) != null) {
                            entireFile.append(line).append("\n");
                        }
                        data = new StringBuilder(entireFile.toString());
                    }
                }
                filterFileActorRef.tell(data.toString());
            }catch (IOException e) {
                e.printStackTrace();
            }
            file.delete();
        }
        return this;
    }
}
