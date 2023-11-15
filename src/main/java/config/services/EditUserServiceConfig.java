package config.services;

/**
 * Класс, хранящий строковые константы, отправляемые сервисом UpdateUserService.
 */
public enum EditUserServiceConfig {
    HELP_MESSAGE(
            """
                Напишите:
                /show_profile, чтобы посмотреть, как выглядит ваш профиль.
                /edit_login, чтобы изменить логин
                /edit_username , чтобы изменить имя пользователя.
                /edit_description, чтобы изменить описание к вашему аккаунту.
                /done для выхода.
                """
    ),

    USERNAME_UPDATED_SUCCESFULLY("Готово! Имя пользователя изменено."),
    DESCRIPTION_UPDATED_SUCCESFULLY("Готово! Описание пользователя изменено."),
    LOGIN_UPDATED_SUCCESFULLY("Готово! Логин пользователя изменен."),
    LOGIN_EXIST_ERROR("Похоже, что такой логин уже занят. Придумайте другой."),


    EDIT_LOGIN_MESSAGE("""
                Придумайте новый логин.
                """),
    EDIT_USER_MESSAGE("""
            Введите новое имя пользователя.
            """),
    EDIT_DESCRIPTION_MESSAGE("""
            Введите новое описание для вашего профиля.
            """),
    END_MESSAGE("Изменения успешно сохранены.");
    private String str;

    private int contextNum;
    EditUserServiceConfig(String str) {
        this.str = str;
    }

    EditUserServiceConfig(int contextNum) {
        this.contextNum = contextNum;
    }

    public String getStr() {
        return str;
    }

    public int getContextNum() {
        return contextNum;
    }
}
