# Бот для Новокольцово

## Задача 2 (Регистрация пользователей)
Бот должен научиться отвечать на команду "registration". После команды с пользователем начинается диалог, где пользователь вводит основную информацию о себе:
1. Телефон (по умолчанию не указывается)
3. Имя в системе (по умолчанию, такое же, как на платформе)
4. Описание (bio)

При работе пользователя с сервисом (в данном случае сервисом регистрации) остальные команды будут игнорироваться, пока пользователь не закончит работу с этим сервисом.

После регистрации пользователь сможет обновить данные о себе через соответствующие команды:
```
updateName, updatePhone, updateDescription, ...
```
### Пример взаимодействия с ботом:
![task2_pic]

После регистрации информация о пользователе будет добавляться в базу данных.
<br/>
<br/>
## Задача 1 (echo bot)
Написать телеграм бота, который умеет с тобой здороваться и рассказывать, что он умеет.

бот будет отвечать на команды:

```
/help
```
```
/start
```

Остальные текстовые сообщения бот будет дублировать обратно пользователю.

### Пример взаимодействия с ботом:

![][task1_pic]

[task1_pic]: task1_example.jpg
[task2_pic]: task2_example.jpg
