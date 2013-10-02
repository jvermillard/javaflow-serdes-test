import java.io.Serializable;

import org.apache.commons.javaflow.Continuation;

/**
 * Some demo task, incrementing a counter in a loop.
 */
public class MyLongTask implements Runnable, Serializable {

    private static final long serialVersionUID = 1L;

    private int counter = 0;

    @Override
    public void run() {
        System.err.println("started task !");

        while (counter < 50) {
            if (counter < 5) {
                counter++;
            } else {
                counter += 10;
            }
            Event next = nextEvent();

            // bla bla process event
            System.err.println("process event: " + next + " counter: " + counter);
        }
        System.err.println("task exit");
    }

    private Event nextEvent() {
        Continuation.suspend();

        // message passing
        return (Event) Continuation.getContext();
    }
}
