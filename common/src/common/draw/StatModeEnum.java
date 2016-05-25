package common.draw;

enum StatModeEnum {
    ENERGY(StatMode.ENERGY), ENERGY_BY_LEN(StatMode.ENERGY_BY_LEN),
    NUMBER(StatMode.NUMBER), NUM_BY_LEN(StatMode.NUM_BY_LEN), NUM_BY_LEN_BY_NUM(StatMode.NUM_BY_LEN_BY_NUM),
    NODE(StatMode.NODE);

    final StatMode mode;

    StatModeEnum(StatMode mode) {
        this.mode = mode;
    }

    public String toString() {
        return mode.toString();
    }
}
