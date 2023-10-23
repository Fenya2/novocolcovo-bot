package db;

import models.Order;
import org.apache.log4j.Logger;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Класс, отвечающий за работу с таблицей orders базы данных.
 */
public class OrderRepository extends Repository{
    private static final Logger log = Logger.getLogger(OrderRepository.class.getName());
    private final UserRepository userRepository;

    public OrderRepository(DB db, UserRepository userRepository) {
        super(db);
        this.userRepository = userRepository;
    }

    /**
     * Сохраняет переданный заказ в таблицу orders базы данных.
     * @param order заказ, который нужно сохранить.
     *              {@link Order#getCourierId()}  Идентификатор доставщика заказа}
     *              должен быть 0 (системный пользователь) или
     *              должен существовать пользователь с таким идентификатором.
     *              Пользователь с {@link Order#getCreatorId()}  идентификатором создателя} должен
     *              существовать.
     * @return Если успешно,
     * ссылка на переданный заказ с установленным {@link Order#getId() идентификатором заказа},
     * данным базой данных. Если {@link Order#getCourierId()}  Идентификатор доставщика заказа} некорректен
     * или {@link Order#getCreatorId()}  идентификатором создателя} некорректен, id заказа выставляется
     * в -1.
     */
    public Order save(Order order) throws SQLException {
        if(userRepository.getById(order.getCreatorId()) == null) {
            order.setId(-1);
            return order;
        }
        if(order.getCourierId() != 0 && userRepository.getById(order.getCourierId()) == null) {
            order.setId(-1);
            return order;
        }

        //todo или дату заказа назначать перед добавлением? В однопотоке как будто не важно.
        String request = """
                INSERT INTO orders(
                creator_id,
                courier_id,
                description,
                date_created,
                status
                ) VALUES (%d, %d, "%s", "%s", "%s");"""
                .formatted(
                        order.getCreatorId(),
                        order.getCourierId(),
                        order.getDescription(),
                        order.getDateCreatedToString(),
                        order.getStatus()
                );
        Statement statement = db.getStatement();
        if(statement.executeUpdate(request) == 0) {
            statement.close();
            throw new SQLException("Something went wrong, хотя не должно");
        }

        request = "SELECT max(id) FROM orders;";
        ResultSet resultSet = statement.executeQuery(request);
        if(!resultSet.next()) {
            throw new SQLException("Something went wrong, хотя не должно");
        }
        order.setId(resultSet.getLong(1));
        resultSet.close();
        statement.close();
        OrderRepository.log.info("создан новый заказ %s".formatted(order));
        return order;
    }

    /**
     * Возвращает заказ по его переданному идентификатору.
     * @param orderId
     * @return заказ с переданным идентификатором заказа.
     * Если заказа с таким идентификатором не существует - <b>null</b>.
     * @throws SQLException
     * @throws ParseException
     */
    public Order getById(long orderId) throws SQLException, ParseException {
        if(orderId <= 0) {
            return null;
        }
        String request = """
                SELECT
                id,
                creator_id,
                courier_id,
                date_created,
                description,
                status
                FROM orders WHERE id = %d;
                """.formatted(orderId);
        Statement statement = db.getStatement();
        ResultSet resultSet = statement.executeQuery(request);
        if(!resultSet.next()) {
            resultSet.close();
            statement.close();
            return null;
        }
        Order order = new Order();
        order.setId(resultSet.getLong("id"));
        order.setCreatorId(resultSet.getLong("creator_id"));
        order.setCourierId(resultSet.getLong("courier_id"));
        order.setDescription(resultSet.getString("description"));
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd H:m:s.S");
        order.setDateCreated(formatter.parse(resultSet.getString("date_created")));
        order.setStatus(resultSet.getString("status"));
        resultSet.close();
        statement.close();
        return order;
    }

    /**
     * Обновляет существующий заказ.
     * @param order заказ, который нужно сохранить. Должен существовать.
     *              {@link Order#getCourierId()}  Идентификатор доставщика заказа}
     *              должен быть 0 или должен существовать пользователь с таким идентификатором.
     *              Пользователь с {@link Order#getCreatorId()}  идентификатором создателя} должен
     *              существовать.
     * @return <b>1</b>, если успешно обновлен заказ.
     * -1, если заказ с заданным идентификатором не существует.
     * 0, если идентификаторы создателя или доставщика некорректны.
     */
    public int updateWithId(Order order) throws SQLException, ParseException {
        if(getById(order.getId()) == null) {
            return -1;
        }
        if(userRepository.getById(order.getCreatorId()) == null) {
            return 0;
        }
        if(order.getCourierId() != 0 && userRepository.getById(order.getCourierId()) == null) {
            return 0;
        }
        String request = """
                        UPDATE orders SET
                        creator_id = %d,
                        courier_id = %d,
                        date_created = "%s",
                        description = "%s",
                        status = "%s"
                        WHERE id = %d;"""
                .formatted(
                        order.getCreatorId(),
                        order.getCourierId(),
                        order.getDateCreatedToString(),
                        order.getDescription(),
                        order.getStatus(),
                        order.getId()
                );

        Statement statement = db.getStatement();
        if(statement.executeUpdate(request) == 0) {
            statement.close();
            throw new SQLException("Something went wrong, хотя не должно");
        }
        statement.close();
        OrderRepository.log.info("Заказ с id = %d изменен на %s".formatted(order.getId(), order));
        return 1;
    }

    /**
     * удаляет заказ с переданным идентификатором из таблицы orders.
     * @param orderId
     * @return 1, если заказ был удален. Иначе 0.
     * @throws SQLException
     */
    public int delete(long orderId) throws SQLException {
        if(orderId <= 0) {
            return 0;
        }
        String request = "DELETE FROM orders WHERE id = %d;".formatted(orderId);
        Statement statement = db.getStatement();
        if(statement.executeUpdate(request) == 0) {
            statement.close();
            throw new SQLException("Something went wrong, хотя не должно");
        }
        statement.close();
        OrderRepository.log.info("Заказ с id = %d удален".formatted(orderId));
        return 1;
    }

    /**
     * Возвращает список всех заказов.
     * @return
     * @throws SQLException
     * @throws ParseException
     */
    public ArrayList<Order> getAll() throws SQLException, ParseException {
        String request = "SELECT * FROM orders;";
        ArrayList<Order> ret= new ArrayList<>();
        Statement statement = db.getStatement();
        ResultSet resultSet = statement.executeQuery(request);
        while (resultSet.next()) {
            Order order = new Order();
            order.setId(resultSet.getLong("id"));
            order.setCreatorId(resultSet.getLong("creator_id"));
            order.setCourierId(resultSet.getLong("courier_id"));
            order.setDescription(resultSet.getString("description"));
            order.setDateCreated(new SimpleDateFormat("yyyy-MM-dd H:m:s.S")
                    .parse(resultSet.getString("date_created")));
            order.setStatus(resultSet.getString("status"));
            ret.add(order);
        }
        resultSet.close();
        statement.close();
        return ret;
    }

    /**
     * У одного пользователя может изменятся только один заказ, а значит можно
     * @param idUser по пользователю
     * @param status и статусу заказа
     * @return вернуть заказ
     */
    public Order getOrderByIdUserAndStatus(long idUser, String status) throws SQLException, ParseException {
        ArrayList<Order> listAllOrders = getAll();
        for(Order s: listAllOrders){
            if (s.getCreatorId() == idUser && s.getStatus().equals(status))
                return s;
        }
        return null;
    }

    /**
     * Обновляет статус у заказа на новый
     * @return вовзращает 1 при удачно обновлении и 0 иначе
     */
    public int updateOrderStatus(long idOrder,String status) throws SQLException, ParseException {
        ArrayList<Order> listAllOrders = getAll();
        for(Order s: listAllOrders){
            if (s.getId() == idOrder){
                Order orderNew = new Order(s);
                orderNew.setStatus(status);
                updateWithId(orderNew);
                return 1;
            }
        }
        return 0;
    }
}
