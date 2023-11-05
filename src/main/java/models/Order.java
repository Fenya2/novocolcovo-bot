package models;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Класс заказа
 */
public class Order {
    /** Уникальный идентификатор заказа. */
    private long id;

    /** Уникальный Идентификатор создателя заказа. */
    private long creatorId;

    /** Идентификатор доставщика заказа. Может быть <b>0</b>, что значит, что заказ никем не принят. */
    private long courierId;

    private Date dateCreated;

    private String description;

    /** статус заказа */
    private OrderStatus status;

    /**
     * Поле id выставляется в -42.
     * @param creatorId идентификатор заказчика.
     * @param courierId идентификатор курьера. Если еще никем не принят, желательно ставить 0.
     * @param dateCreated дата и время создания заказа.
     * @param description описание заказа. Не <b>null</b>.
     * @param status статус заказа.
     */
    public Order(long creatorId, long courierId, String description,
                 OrderStatus status, Date dateCreated) {
        this.id = -42;
        setCreatorId(creatorId);
        setCourierId(courierId);
        this.dateCreated = dateCreated;
        setDescription(description);
        setStatus(status);
    }

    public Order(long creatorId, long courierId, OrderStatus status) {
        this.id = -42;
        setCreatorId(creatorId);
        setCourierId(courierId);
        this.dateCreated = new Date();
        description = "default description";
        setStatus(status);
    }

    /**
     * Облегченный конструктор. Ведь только эта id создателя известен при создании заказа.
     * <ul>
     * <li>{@link #id id} выставляется в -42</li>
     * <li>{@link #courierId} выставляется в 0.</li>
     * <li>{@link #description} выставляется в "default description".</li>
     * <li>{@link #dateCreated} указывается при создании заказа.</li>
     * <li>{@link #status} выставляется в {@link OrderStatus NO_STATUS}.</li>
     * </ul>
     * @param creatorId идентификатор создателя заказа.
     */
    public Order(long creatorId) {
        setCreatorId(creatorId);
        this.id = -42;
        this.courierId = 0;
        description = "default description";
        setStatus(OrderStatus.NO_STATUS);
        this.dateCreated = new Date();
    }


    /**
     * <ul>
     * <li>{@link #id id} выставляется в -42.</li>
     * <li>{@link #creatorId} выставляется в -42.</li>
     * <li>{@link #courierId} выставляется в -42.</li>
     * <li>{@link #dateCreated} выставляется текущей датой.</li>
     * <li>{@link #description} выставляется в "default description".</li>
     * <li>{@link #status} выставляется в {@link OrderStatus NO_STATUS}".</li>
     * </ul>
     */
    public Order() {
        id = -42;
        creatorId = -42;
        courierId = -42;
        dateCreated = new Date();
        description = "default description";
        status = OrderStatus.NO_STATUS;
    }

    /** Возвращает идентификатор заказа. */
    public long getId() {
        return id;
    }

    /**
     * Устанавливает идентификатор заказа.
     * @param id идентификатор заказа.
     */
    public void setId(long id) {
        this.id = id;
    }

    /** Возвращает идентификатор заказчика. */
    public long getCreatorId() {
        return creatorId;
    }

    /**
     * Устанавливаeт идентификатор заказчика.
     * @param creatorId идентификатор заказчика.
     */
    public void setCreatorId(long creatorId) throws IllegalArgumentException {
        this.creatorId = creatorId;
    }

    /** Возвращает идентификатор доставщика заказа. */
    public long getCourierId() {
        return courierId;
    }

    /**
     * Устанавливает идентификатор доставщика заказа.
     * @param courierId идентификатор заказчика
     */
    public void setCourierId(long courierId) throws IllegalArgumentException {
        this.courierId = courierId;
    }

    /** Возвращает дату создания заказа */
    public Date getDateCreated() {
        return dateCreated;
    }


    /**
     * Устанавливает дату создания заказа.
     * @param dateCreated не <b>null</b>
     */
    public void setDateCreated(Date dateCreated) throws IllegalArgumentException{
        if(dateCreated == null) {
            throw new IllegalArgumentException("dateCreated must be not null.");
        }
        this.dateCreated = dateCreated;
    }

    /** Возвращает дату создания заказа */
    public String getDateCreatedToString() {
        return new SimpleDateFormat("yyyy-MM-dd H:m:s.S").format(dateCreated);
    }

    /** Возвращает описание заказа */
    public String getDescription() {
        return description;
    }

    /**
     * Устанавливает описание к заказу.
     * @param description описание заказа. Не <b>null</b>.
     */
    public void setDescription(String description) throws IllegalArgumentException {
        if (description == null) {
            throw new IllegalArgumentException("description must be not null.");
        }
        //todo здесь можно сделать валидацию на запрещенные слова. или выше?
        this.description = description;
    }

    /** Возвращает статус заказа. */
    public OrderStatus getStatus() {
        return status;
    }

    /**
     * Устанавливает статус заказа.
     * @param status статус заказа. Не <b>null</b>.
     */
    public void setStatus(OrderStatus status) throws IllegalArgumentException {
        if(status == null)
            throw new IllegalArgumentException("Status must be not null.");
        this.status = status;
    }

    @Override
    public String toString() {
        return "Order(id:%d, creator_id:%d, courier_id:%d, date_created:<%s>, description: \"%s\", status: \"%s\" )"
                .formatted(
                        id,
                        creatorId,
                        courierId,
                        getDateCreatedToString(),
                        description,
                        status
                );
    }
}
