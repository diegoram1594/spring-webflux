package com.reactivespring.movieservice;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Sinks;

public class SinkTest {
    @Test
    public void sink(){
        Sinks.Many<Integer> replaySink = Sinks.many().replay().all();

        replaySink.tryEmitNext(1);
        replaySink.tryEmitNext(2);

        replaySink.asFlux().subscribe(data ->{
            System.out.println("Sub1 " + data);
        });
        replaySink.tryEmitNext(3);

        replaySink.asFlux().subscribe(data ->{
            System.out.println("Sub2 " + data);
        });
    }

}
