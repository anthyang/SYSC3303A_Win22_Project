import java.util.Locale;

/**
 * Describes the direction an elevator should move in the system
 */
public enum Direction {
    UP("up"), DOWN("down"), NOT_MOVING("not moving");

    /** A readable string for the enum value */
    private final String readable;

    Direction(String readable) {
        this.readable = readable;
    }

    /**
     * Get an enum value with the given search value, case-insensitive
     * @param search The desired enum value
     * @return The enum matching the search term
     * @throws IllegalArgumentException when no valid enum was found
     */
    public static Direction get(String search) {
        for (Direction direction : Direction.values()) {
            if (direction.readable.equals(search.toLowerCase(Locale.ROOT))) {
                return direction;
            }
        }

        throw new IllegalArgumentException("Specified enum was not found");
    }

    /**
     * Get a readable string for the direction
     * @return a readable string
     */
    public String getReadable() {
        return this.readable;
    }
}
