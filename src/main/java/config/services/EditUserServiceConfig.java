package config.services;

/**
 * Класс, хранящий строковые константы, отправляемые сервисом UpdateUserService.
 */
public enum EditUserServiceConfig {

    HELP_MESSAGE(
            """
                Напишите:
                /show_profile, чтобы посмотреть, как выглядит ваш профиль.
                /edit_username , чтобы изменить имя пользователя.
                /edit_description, чтобы изменить описание к вашему аккаунту.
                /done для выхода.
                """
    ),
    EDIT_USER_MESSAGE("""
            Введите новое имя пользователя.
            """),

    USERNAME_UPDATED_SUCCESFULLY("Готово! Имя пользователя изменено."),
    DESCRIPTION_UPDATED_SUCCESFULLY("Готово! Описание пользователя изменено."),

    EDIT_DESCRIPTION_MESSAGE("""
            Введите новое описание для вашего профиля.
            """),
    END_MESSAGE("Изменения успешно сохранены.");
    private String str;
    EditUserServiceConfig(String str) {
        this.str = str;
    }

    public String getStr() {
        return str;
    }
}
