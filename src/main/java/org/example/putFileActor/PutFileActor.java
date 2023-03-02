package org.example.putFileActor;

import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;

import java.io.FileWriter;

public class PutFileActor extends AbstractBehavior<String> {

    private final String filePath;

    public PutFileActor(ActorContext<String> context, String filePath) {
        super(context);
        this.filePath = filePath;
    }

    public static Behavior<String> create(String filePath) {
        return Behaviors.setup(context -> new PutFileActor(context, filePath));
    }

    @Override
    public Receive<String> createReceive() {
        return newReceiveBuilder()
                .onMessage(String.class, this::onMessage)
                .build();
    }

    private Behavior<String> onMessage(String data) throws Exception {
        // Write the filtered data to the file at the specified location
        FileWriter fw = new FileWriter(filePath, true);
        fw.write(data);
        fw.close();
        return this;
    }
}
