package ameba.event;

/**
 * @author icode
 */
public class SystemEventBus {

    private static final EventBus EVENT_BUS = EventBus.createMix("ameba-sys");

    public static <E extends Event> void subscribe(Class<E> event, final Listener<E> listener) {
        EVENT_BUS.subscribe(event, listener);
    }

    public static <E extends Event> void unsubscribe(Class<E> event, final Listener<E> listener) {
        EVENT_BUS.unsubscribe(event, listener);
    }

    public static void publish(Event event) {
        EVENT_BUS.publish(event);
    }
}
