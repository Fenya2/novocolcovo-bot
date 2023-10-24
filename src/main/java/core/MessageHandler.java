package core;

import db.LoggedUsersRepository;
import db.UserContextRepository;
import models.Message;
import models.User;
import models.UserContext;

import java.sql.SQLException;

/**
 * Обработчик сообщений
 */
public class MessageHandler {

    /**
     * обработчик команд
     */
    private final CommandHandler commandHandler;

    /**
     * обработчик текста
     */
    private final TextHandler textHandler;
    public MessageHandler(CommandHandler commandHandler, UserContextRepository userContextRepository, LoggedUsersRepository loggedUsersRepository, TextHandler textHandler){
        this.commandHandler = commandHandler;
        this.textHandler = textHandler;
    }

    /**
     * метод определяет сообщение как команду или как текст
     * и отправляет в соответствующий обработчик
     * @param msg
     * @return String, текст который сформирует соответствующий обработчик
     */
    public String handle(Message msg){
        String text = msg.getText();
        if (text.charAt(0)=='/')
            return commandHandler.handle(msg);
        return textHandler.handle(msg);
    }
}
