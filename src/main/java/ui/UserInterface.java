package ui;

/*
    Интерфейс для непосредственного взаимодействия с пользователем. Вот его надо будет реализовывать для любой платформы
 */
public interface UserInterface {
    public void send_text_message(int recipient, String message);
}
