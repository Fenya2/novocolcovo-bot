package db;

/**
 * Класс, выносящий общие свойства репозиториев в класс-родитель, чтобы избавиться от дублирования кода.
 */
public abstract class Repository {

    /**
     * Так как приложение работает с одной бд, все репозитории работают с ней.
     * Выносим ее в поле класса-родителя.
     */
    protected DB db;
    Repository(DB db) {
        this.db = db;
    }
}
