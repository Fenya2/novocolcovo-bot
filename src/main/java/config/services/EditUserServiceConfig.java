package config.services;

/**
 * Класс, хранящий строковые константы, отправляемые сервисом UpdateUserService.
 */
public enum EditUserServiceConfig {
    START_MESSAGE("""
            Обновление вашего аккаунта.
            Напишите:
            /edit_username , чтобы изменить имя пользователя.
            /edit_description, чтобы изменить описание к вашему аккаунту.
            Напишите:
            /cancel для отмены действия.
            """),

    EDIT_USER_MESSAGE("""
            Введите новое имя пользователя.
            """),

    USERNAME_UPDATED_SUCCESFULLY("Готово! Имя пользователя изменено."),
    DESCRIPTION_UPDATED_SUCCESFULLY("Готово! Описание пользователя изменено."),

    EDIT_DESCRIPTION_MESSAGE("""
            Введите новое для вашего аккаунта.
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
