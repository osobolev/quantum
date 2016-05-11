package neuron.events;

final class OutEvent extends Event {

    final int vertex;

    OutEvent(int vertex) {
        this.vertex = vertex;
    }
}
