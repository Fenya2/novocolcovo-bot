package models;

/** Перечисление платформ, на которых работают поддерживаемые боты */
public enum Platform {
    NO_PLATFORM,
    TELEGRAM,
    VK;

    public static Platform fromString(String str) throws IllegalArgumentException {
        for (Platform platform : Platform.values()) {
            if (platform.toString().equals(str))
                return platform;
        }
        throw new IllegalArgumentException("No such platform for str parameter.");
    }
}
