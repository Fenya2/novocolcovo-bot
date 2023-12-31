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

    /** логин пользователя. Уникальный для каждого пользователя */
    private String login;

    /**
     * Устанавливает id, name, description в
     * -42, "default name", "default description" соответственно.
     */
    public User() {
        id = -42;
        name = "default name";
        description = "default description";
        login = "default login";
    }

    /**
     * @param id идентификатор пользователя.
     * @param name имя пользователя.
     * @param description описание пользователя.
     * @param login логин пользователя
     */
    public User(long id, String name, String description, String login) {
        this.id = id;
        setName(name);
        setDescription(description);
        setLogin(login);
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

    /** @return логин пользователя */
    public String getLogin() {
        return login;
    }

    /** @param login логин пользователя не null */
    public void setLogin(String login) {
        if(login == null) {
            throw new IllegalArgumentException("Invalid login. Must be not null.");
        }
        this.login = login;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", login='" + login + '\'' +
                '}';
    }
}
