package entities;

public class User {
    public long id;
    public String name;
    public String description;
    public User() {
        id = 0;
        name = null;
        description = null;
    }

    public User(long id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    public User id(long id) {
        this.id = id;
        return this;
    }

    public User name(String name) {
        this.name = name;
        return this;
    }

    public User description(String description) {
        this.description = description;
        return this;
    }

    @Override
    public String toString() {
        return "User(id:%d, name:%s, description:%s)".formatted(id, name, description);
    }
}
