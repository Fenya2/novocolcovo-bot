package models;

/**
 * Класс, описывающий пользователя в программе
 */
public class User {
    /** Идентификатор пользователя. */
    private long id;
    /** Имя пользователя. */
    private String name;
    /** Описание пользователя. */
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
     * @param id идентификатор пользователя.
     * @param name имя пользователя.
     * @param description описание пользователя.
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

    /**
     * @param name имя пользователя. Не <b>null</b>.
     */
    public void setName(String name) throws IllegalArgumentException {
        if(name == null)
            throw new IllegalArgumentException("name must be not null.");
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    /** @param description описание пользователя. не <b>null</b> */
    public void setDescription(String description) throws IllegalArgumentException {
        if (description == null)
            throw new IllegalArgumentException("description must be not null.");
        //todo тут можно прикрутить проверку на недопустимые слова. Или повыше?
        this.description = description;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
