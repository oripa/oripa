package oripa.gui.presenter.creasepattern;

import java.util.Optional;
import java.util.stream.Stream;

public enum EditMode {
    NONE,
    INPUT,
    SELECT,
    CHANGE_TYPE,
    DELETE_LINE,
    VERTEX,
    OTHER,
    COPY,
    CUT;

    public static Optional<EditMode> fromString(final String s) {
        return Stream.of(values())
                .filter(type -> type.toString().equals(s))
                .findFirst();
    }

}