package models;

public enum OrderStatus {
    /** Заказ без статуса. */
    NO_STATUS,

    /** Заказ ожидает пока его примут */
    PENDING,

    /** Заказ изменяется */
    UPDATING,

    /** Заказ принят и выполняется */
    RUNNING,

    /** Заказ закрывается */
    CLOSING,

    /** Заказчик подтвердил закрытие заказа */
    CLOSED,

    /** Заказчик не подтвердил закрытие заказа */
    NOT_CLOSED
}
