package oripa.domain.cptool;

import java.util.Optional;
import java.util.stream.Stream;

import oripa.value.OriLine;

public enum TypeForChange {
    EMPTY("Any", null),
    MOUNTAIN("M", OriLine.Type.MOUNTAIN),
    VALLEY("V", OriLine.Type.VALLEY),
    UNASSIGNED("U", OriLine.Type.UNASSIGNED),
    AUX("Aux", OriLine.Type.AUX),
    CUT("Cut", OriLine.Type.CUT),
    DELETE("Del", null),
    FLIP("Flip", null);

    private String shortName;
    private OriLine.Type oriType;

    private TypeForChange(final String shortName, final OriLine.Type oriType) {
        this.shortName = shortName;
        this.oriType = oriType;
    }

    public OriLine.Type getOriLineType() {
        return oriType;
    }

    public static Optional<TypeForChange> fromString(final String s) {
        return Stream.of(values())
                .filter(type -> type.toString().equals(s))
                .findFirst();
    }

    @Override
    public String toString() {
        return shortName;
    }
}