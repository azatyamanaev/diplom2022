# pipeline-errors-classifier


## Использование

1. Скачать исходный код
2. Открыть в новом проекте Intellij IDEA
3. Указать в application.properties pipeline.monitor.token(токен доступа Gitlab), pipeline.monitor.logs-dir(каталог, в который будут сохраняться логи пайплайнов)
4. Эндпоинты CommonController используются для заполнения БД
5. Эндпоинты ErrorControler используются для получения ошибки конкретного пайплайна
