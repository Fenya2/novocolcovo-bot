package ui;

/*
    Интерфейс для непосредственного взаимодействия с пользователем. Вот его надо будет реализовывать для любой платформы
 */
public interface UserInterface {
    public void sendTextMessage(Long recipient, String message);
}
