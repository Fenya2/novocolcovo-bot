package models;

/**
 * Класс, описывающий пользователя в программе
 */
public class User {
    private long id;
    private String name;
    private String description;

    /**
     * Устанавливает id, name, description в
     * -42, "default name", "default description" соответственно.
     */
    public User() {
        id = -42;
        name = "default name";
        description = "default description";
    }

    /**
     * @param id
     * @param name
     * @param description
     */
    public User(long id, String name, String description) {
        this.id = id;
        setName(name);
        setDescription(description);
    }
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) throws IllegalArgumentException {
        if(name == null)
            throw new IllegalArgumentException();
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) throws IllegalArgumentException {
        if(description == null)
            throw new IllegalArgumentException();
        //todo тут можно прикрутить проверку на недопустимые слова. Или повыше?
        this.description = description;
    }

    @Override
    public String toString() {
        return "User(id:%d, name:\"%s\", description:\"%s\")".formatted(id, name, description);
    }
}
