<h2>java-shareit</h2>
<p>Учебный проект, знакомство с микросервисной архитектурой. <br/>Использовал java 11, Maven, Spring BOOT, RestTemplate, JpaRepository, PostgreSQL, Docker.<p>
<h3>Описание</h3>
<p>Приложение позволяет делиться вещами с другими пользователями.</p>
<h3>Структура</h3>
<p>Приложение разделено на микросервисы:</p>  
<p><ul><li>Gateway - валидация запросов к приложению, перенаправление на сервис Server.</li>
<li>Server - обработка запросов согласно бизнес логики. Связь с базой данных. Передача ответов на Gateway.</li></ul></p>
<h3>Эндпоинты</h3>
<p><ul>
<li>[POST] /users - создать нового пользователя;</li>
<li>[PATCH] /users - обновление существующего пользователя;</li>
<li>[GET] /users - получить список всех пользователей;</li>
<li>[GET] /users/{id} - получить пользователя по некоторому id;</li>
<li>[DELETE] /users/{id} - удалить пользователя по некоторому id;</li>
</ul></p>
<p><ul>
<li>[POST] /items - создать новую вещь;</li>
<li>[POST] /items/{itemId}/comment - создание комментария для вещи с некоторым id пользователем;</li>
<li>[PATCH] /items/{id} - обновление существующей вещи с некоторым id;</li>
<li>[GET] /items/{id} - получить вещь с определённым id;</li>
<li>[GET] /items - получить список вещей определённого пользователя;</li>
<li>[GET] /items/search?text={text}&from={from}&size={size} - получить список вещей по поисковому запросу;</li>
<li>[DELETE] /items/{id} - удалить вещь по некоторому id;</li>
</ul></p>
<p><ul>
<li>[POST] /requests - создать новый запрос;</li>
<li>[GET] /requests - получить список своих запросов вместе с данными об ответах на них;</li>
<li>[GET] /requests/all?from={from}&size={size} - получить список запросов на вещь, созданных другими пользователями;</li>
<li>[GET] /items/search?text={text}&from={from}&size={size} - получить список вещей по поисковому запросу;</li>
<li>[GET] /requests/{requestId} - получить данные об одном конкретном запросе с некоторым requestId вместе с данными об ответах на него;</li>
</ul></p>
<p><ul>
<li>[POST] /bookings - создание бронирования;</li>
<li>[PATCH] /bookings/{bookingId}?approved={approved} - подтверждение или отклонение запроса на бронирование. Параметр approved может принимать значения true или false;</li>
<li>[GET] /bookings/{bookingId} - получение данных о конкретном бронировании по bookingId (включая его статус);</li>
<li>[GET] /bookings?state={state} - получение списка всех бронирований текущего пользователя. Параметр state необязательный и по умолчанию равен ALL. Также он может принимать значения CURRENT, PAST, FUTURE, WAITING, REJECTED;</li>
<li>[GET] /bookings/owner?state={state} - получение списка бронирований для всех вещей текущего пользователя;</li>
</ul></p>
<h3>Схема базы данных</h3>
<p><img src="https://github.com/Konstakox/java-shareit/blob/main/postgres_shareit.png"/></p>
