# Automation UI and API Tests

Этот проект предназначен для автоматизации UI и API тестирования с использованием TeamCity и Maven. В данном руководстве описаны шаги для локального запуска тестов и настройки среды TeamCity.

## Предварительная настройка

Перед запуском тестов выполните следующие шаги:

1. Добавьте значения параметров в файл `src/main/resources/config.properties`:
    ```properties
    host=<IP>:8111
    superUserToken=<TOKEN>
    ```
    - `host` — IP-адрес и порт сервера TeamCity.
    - `superUserToken` — токен авторизации для пользователя с полными правами.

## Запуск TeamCity-сервера в Docker

1. Создайте директорию для TeamCity-сервера:
    ```bash
    mkdir teamcity-workshop
    cd teamcity-workshop
    mkdir teamcity-server
    ```

2. Подтяните и запустите контейнер TeamCity-сервера с помощью следующей команды:
    ```bash
    docker run --name teamcity-server-instance \
      -v $(pwd)/teamcity-server/datadir:/data/teamcity_server/datadir \
      -v $(pwd)/teamcity-server/logs:/opt/teamcity/logs \
      -p 8111:8111 \
      jetbrains/teamcity-server:2023.11.1
    ```

   > **Примечание:** В логах контейнера вы найдете значение `<TOKEN>`, которое необходимо добавить в `config.properties`.

3. Получите IP-адрес контейнера для подключения:
    ```bash
    ipconfig getifaddr en0
    ```

## Запуск TeamCity-агента

1. Создайте директорию для TeamCity-агента:
    ```bash
    cd teamcity-workshop
    mkdir teamcity-agent
    cd teamcity-agent
    ```

2. Запустите TeamCity-агента, указав URL TeamCity-сервера в <host>:
    ```bash
    docker run -e SERVER_URL="http://<host>:8111" \
      -v $(pwd)/teamcity-agent/conf:/data/teamcity_agent/conf \
      jetbrains/teamcity-agent
    ```

3. После запуска агента авторизуйте его через браузер в TeamCity.


## Запуск тестов

Для запуска тестов с использованием Maven и генерации отчета Allure выполните команду:

```bash
mvn clean test allure:serve