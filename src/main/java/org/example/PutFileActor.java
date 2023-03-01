package org.example;

import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;

import java.nio.file.Files;
import java.nio.file.Paths;

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
        Files.writeString(Paths.get(filePath), data + "\n", java.nio.charset.StandardCharsets.UTF_8, java.nio.file.StandardOpenOption.CREATE, java.nio.file.StandardOpenOption.APPEND);
        return this;
    }
}
