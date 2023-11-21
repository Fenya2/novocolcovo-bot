package config;

public enum BotMessages {
    START_MESSAGE(
            """
            Привет! Введи /login, чтобы войти в аккаунт, /register, чтобы создать аккаунт.
            """
    ),
    HELP_MESSAGE(
            """
            /profile - информация о профиле, настройка профиля.
            /create_order - создать заказ.
            /edit_order - изменить заказ.
            /cancel_order - удалить заказ.
            /show_order - посмотреть список созданных заказов.
            /show_pending_orders - вывести список всех заказов, доступных для принятия.
            /accept_order - принять заказ. Прежде чем принимать заказ, посмотрите список заказов(доступных для принятия), вызвав команду /show_pending_orders).
            /show_accept_order - вывести список принятых заказов.
            /close_order - завершить заказ.
            """
    ),

    REGISTER_MESSAGE(
            """
            Готово! Аккаунт создан.
            Вы находитесь в меню редактирования пользователя.
            Введитете /help для справки.
            """
    ),

    REGISTER_MESSAGE_WHEN_USER_LOGIN("Вы уже зарегистрированы."),

    LOGIN_MESSAGE_WHEN_USER_LOGIN("Вы уже вошли в систему.");


    private String message;
    BotMessages(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
