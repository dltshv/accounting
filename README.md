# Тестовое задание
## Исходная постановка
Надо написать веб-приложение, цель которого - операции со счетами пользователей.
Должно быть 3 API (RESTful) - перевод денег с одного счёта на другой, положить деньги на
счёт, снять деньги со счёта.
В качестве хранилища можно использовать любую in-memory БД.
Исходный код должен собираться с помощью maven или gradle в исполняемый jar. Решение
должно быть на любом JVM-языке (Java, Kotlin, Scala, Frege, Groovy).


## Допущения
В приложении не реализована полноценная функциональность
аутентификации пользователей. Авторизация производится через
токен, генерируемый в момент создания пользователя (см. инструкцию).
Пользователь в ходе работы с приложением может оперировать только своими счетами.

## Инструкция
`POST /user/new` создает нового пользователя, возвращает токен. Принимает обязательным
параметром строку с именем пользователя в теле запроса

*Далее и везде методы принимают в качестве обязательного параметра 
HTTP заголовок X-token с токеном, полученным в момент создания пользователя* 

`POST /account/new` - создает новый счет для пользователя. 

`GET /account/list`  - список счетов пользователя.

`POST /account/deposit` - пополнение счета. Принимает JSON вида
```
{
    "account": 1,
    "amount": 1000,
    "type": "DEPOSIT"
}
```
в случае успеха возращает в ответе JSON с результатом операции

`POST /account/withdraw` - снятие со счета. Принимает JSON вида
```
{
    "account": 1,
    "amount": 1000,
    "type": "WITHDRAW"
}
```
в случае успеха возращает в ответе JSON с результатом операции

`POST /account/internal` - перевод между своими счетами. Принимает JSON вида
```
{
    "fromAccount": 1,
    "toAccount": 2
    "amount": 1000,
    "type": "INTERNAL"
}
```
в случае успеха возращает в ответе JSON с результатом операции

## Сборка

Собирать командай `gradle build`. Запускать `java -jar build/libs/accounting-0.0.1-SNAPSHOT.jar`
