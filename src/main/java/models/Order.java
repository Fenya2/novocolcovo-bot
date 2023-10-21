package models;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Formatter;

/**
 * Класс заказа
 */
public class Order {
    /**
     * Уникальный идентификатор заказа.
     */
    private long id;

    /**
     * Идентификатор создателя заказа.
     */
    private long creatorId;

    /**
     * Идентификатор доставщика заказа. Может быть 0, что значит, что заказ никем не принят.
     */
    private long courierId;
    private Date dateCreated;

    /**
     * Описание заказа
     */
    private String description;

    /**
     * {@link #dateCreated} указывается при создании заказа.
     * {@link #id} выставляется в 0.
     * @param creatorId
     * @param courierId
     * @param description
     */
    public Order(long creatorId, long courierId, String description) {
        this.id = 0;
        setCreatorId(creatorId);
        setCourierId(courierId);
        setDescription(description);
        this.dateCreated = new Date();
    }

    /**
     * Облегченный конструктор. Ведь только эта информация известна при создании заказа.
     * {@link #dateCreated} указывается при создании заказа.
     * {@link #id id} выставляется в 0
     * {@link #courierId} выставляется в 0.
     * @param creatorId
     * @param description
     */
    public Order(long creatorId, String description) {
        this.id = 0;
        this.courierId = 0;
        setCreatorId(creatorId);
        setDescription(description);
        this.dateCreated = new Date();
    }

    public Order() {
        id = -42;
        creatorId = -42;
        courierId = -42;
        description = null;
        dateCreated = null;
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
        if(creatorId <= 0) {
            throw new IllegalArgumentException("Creator id must be positive.");
        }
        this.creatorId = creatorId;
    }

    public long getCourierId() {
        return courierId;
    }

    public void setCourierId(long courierId) throws IllegalArgumentException {
        if(id < 0) {
            throw new IllegalArgumentException("Creator id can't be negative.");
        }
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

    public void setDescription(String description) throws  IllegalArgumentException{
        if(description == null) {
            throw new IllegalArgumentException("description can't null");
        }
        /*
            todo здесь можно сделать валидацию на запрещенные слова.
         */
        this.description = description;
    }

    @Override
    public String toString() {
        return "Order(id:%d, creator_id:%d, courier_id:%d, date_created:<%s>, description: \"%s\" )"
                .formatted(
                        id,
                        creatorId,
                        courierId,
                        getDateCreatedToString(),
                        description
                );
    }

}
