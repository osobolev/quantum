package neuron.events;

/**
 * Event of signal arriving to vertex from the edge (always forward - i.e. from vertex 0 to vertex 1)
 */
final class InEvent extends Event {

    final int edge;

    InEvent(int edge) {
        this.edge = edge;
    }
}
