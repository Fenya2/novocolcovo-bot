package models;


public enum UserState {
    /** Пользователь не находится внутри выполнения какой-либо команды. */
    NO_STATE,
    /** Пользователь регистрируется в системе */
    REGISTRATION,
    /** Пользователь(заказчик) находится внутри выполнения команды /create_order */
    ORDER_CREATING,
    /** Пользователь(заказчик) находится внутри выполнения команды /edit_order  */
    ORDER_EDITING,
    /** Пользователь(заказчик) находится внутри выполнения команды /cancel_order */
    ORDER_CANCELING,
    /** Пользователь(заказчик) находится внутри выполнения команды /edit_user */
    EDIT_USER,
    /** Пользователь(исполнитель) находится внутри выполнения команды /accept_order */
    ORDER_ACCEPTING,
    /** Пользователь(исполнитель) находится внутри выполнения команды /close_order */
    ORDER_CLOSING_COURIER,
    /** Пользователь(заказчик) находится внутри выполнения команды /accept_order */
    ORDER_CLOSING_CLIENT
}
