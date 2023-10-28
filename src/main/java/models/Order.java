package models;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Formatter;

/**
 * Класс заказа
 */
public class Order {
    /** Уникальный идентификатор заказа. */
    private long id;

    /** Уникальный Идентификатор создателя заказа */
    private long creatorId;

    /** Идентификатор доставщика заказа. Может быть <b>0</b>, что значит, что заказ никем не принят. */
    private long courierId;
    private Date dateCreated;
    private String description;

    /** статус заказа */
    private String status;

    /**
     * {@link #dateCreated} ставится в null.
     * {@link #id} заказа выставляется в 0.
     * @param creatorId
     * @param courierId
     * @param description
     */
    public Order(long creatorId, long courierId, String description,
                 String status, Date dateCreated) {
        this.id = 0;
        setCreatorId(creatorId);
        setCourierId(courierId);
        setDescription(description);
        setStatus(status);
        this.dateCreated = dateCreated;
    }
    /**
     * Облегченный конструктор. Ведь только эта id создателя известен при создании заказа.
     * <ul>
     * <li>{@link #dateCreated} указывается при создании заказа.</li>
     * <li>{@link #id id} выставляется в -42</li>
     * <li>{@link #courierId} выставляется в 0.</li>
     * <li>{@link #description} выставляется в "default description".</li>
     * <li>{@link #status} выставляется в "updating".</li>
     * </ul>
     * @param creatorId идентификатор создателя заказа.
     */
    public Order(long creatorId) {
        this.id = -42;
        this.courierId = 0;
        description = "default description";
        setCreatorId(creatorId);
        setStatus("pending");
        this.dateCreated = new Date();
    }

    /**
     * Конструктор копирования
     */
    public Order(Order order) {
        this.id = order.id;
        this.courierId = order.courierId;
        setCreatorId(order.creatorId);
        setDescription(order.description);
        setStatus(order.status);
        this.dateCreated = order.dateCreated;
    }

    public Order() {
        id = -42;
        creatorId = -42;
        courierId = -42;
        status = "pending";
        description = "default description";
        dateCreated = new Date();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(long creatorId) throws IllegalArgumentException {
        this.creatorId = creatorId;
    }

    public long getCourierId() {
        return courierId;
    }

    public void setCourierId(long courierId) throws IllegalArgumentException {
        this.courierId = courierId;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public String getDateCreatedToString() {
        return new SimpleDateFormat("yyyy-MM-dd H:m:s.S").format(dateCreated);
    }

    public String getDescription() {
        return description;
    }

    /**
        в сетере есть проверка на null
     */
    public void setDescription(String description) throws IllegalArgumentException {
        if (description == null) {
            throw new IllegalArgumentException("description can't null");
        }
        //todo здесь можно сделать валидацию на запрещенные слова. или выше?
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    /**
        в сетере есть проверка на допустимое значение status
     */
    public void setStatus(String status) throws IllegalArgumentException {
        switch (status) {
            case "pending", "updating", "in_process":
                this.status = status;
                return;
        }
        throw new IllegalArgumentException("Попытка приписать к заказу несуществующий статус.");
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
