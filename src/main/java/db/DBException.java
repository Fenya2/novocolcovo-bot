package db;

/**
 * Класc исключения, генерирующегося, когда происходит попытка изменить целостность данных.
 * todo переписать все методы, возвращающие int на это исключение.
 */
public class DBException extends Exception{
    DBException(String message) {
        super(message);
    }
}
