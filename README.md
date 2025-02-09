# Automation UI and API Tests

Этот проект предназначен для автоматизации UI и API тестирования с использованием TeamCity и Maven. В данном руководстве
описаны шаги для локального запуска тестов и настройки среды TeamCity.

# Предварительная настройка

## Запуск TeamCity-сервера в Docker

1. Создайте директорию для TeamCity-сервера:
    ```bash
    mkdir teamcity-workshop
    cd teamcity-workshop
    mkdir teamcity-server
    ```

2. Подтяните и запустите контейнер TeamCity-сервера с помощью следующей команды:
    ```bash
    cd teamcity-server
   ```
   
   ```bash
    docker run --name teamcity-server-instance \
      -v $(pwd)/teamcity-server/datadir:/data/teamcity_server/datadir \
      -v $(pwd)/teamcity-server/logs:/opt/teamcity/logs \
      -p 8111:8111 \
      jetbrains/teamcity-server:2023.11.1
    ```

   > **Примечание:** В логах контейнера вы найдете значение `<TOKEN>`, которое необходимо добавить позже в
   `config.properties`.

3. Получите IP-адрес контейнера для подключения:
    ```bash
    ipconfig getifaddr en0  # macOS
    ```
    
   ```bash
    ipconfig  # Windows
    ```

## Запуск TeamCity-агента

1. Создайте директорию для TeamCity-агента:
    ```bash
    cd teamcity-workshop
    mkdir teamcity-agent
    cd teamcity-agent
    ```

2. Запустите TeamCity-агента, указав IP-адрес контейнера TeamCity-сервера в <host>:
    ```bash
    docker run -e SERVER_URL="http://<host>:8111" \
      -v $(pwd)/teamcity-agent/conf:/data/teamcity_agent/conf \
      jetbrains/teamcity-agent
    ```

3. После запуска агента авторизуйте его через браузер в TeamCity.
   URL в логах teamcity-server



# Настройка фермы браузеров с использованием Selenoid

Этот раздел описывает настройку фермы браузеров для выполнения UI-тестов в изолированной среде с использованием Docker и Selenoid.

### Шаг 1: Создание конфигурационного файла `browsers.json`

1. В папке проекта `teamcity-workshop` создайте новую директорию для конфигурации Selenoid:
    ```bash
    cd teamcity-workshop
    mkdir -p selenoid/config
    ```

2. В директории `config` создайте файл `browsers.json` с настройками для нужных браузеров. Пример содержимого файла:
    ```json
    {
      "firefox": {
        "default": "89.0",
        "versions": {
          "89.0": {
            "image": "selenoid/vnc:firefox_89.0",
            "port": "4444",
            "path": "/wd/hub"
          }
        }
      },
      "chrome": {
        "default": "91.0",
        "versions": {
          "91.0": {
            "image": "selenoid/vnc:chrome_91.0",
            "port": "4444",
            "path": "/"
          }
        }
      },
      "opera": {
        "default": "76.0",
        "versions": {
          "76.0": {
            "image": "selenoid/vnc:opera_76.0",
            "port": "4444",
            "path": "/wd/hub"
          }
        }
      }
    }
    ```

### Шаг 2: Загрузка Docker-образов для браузеров

Чтобы Selenoid мог запускать тесты в нужных версиях браузеров, загрузите указанные образы в текущей директории `config`:

   ```bash
   docker pull selenoid/vnc:firefox_89.0
   docker pull selenoid/vnc:chrome_91.0
   docker pull selenoid/vnc:opera_76.0
   ```

### Шаг 3: Запуск контейнера Selenoid
Selenoid для выполнения UI тестов.
Запустить из директории `selenoid`

   ```bash
   cd selenoid
   ```

   ```bash
   docker run -d \
     --name selenoid \
     -p 4444:4444 \
     -v /var/run/docker.sock:/var/run/docker.sock \
     -v $(pwd)/config/:/etc/selenoid/:ro \
     aerokube/selenoid:latest-release
   ```

### Шаг 4:  Проверка успешного запуска

   ```bash
  Перейдите по адресу http://localhost:4444/, где должно отобразиться сообщение "You are using Selenoid!"
   ```

### Шаг 4: Запуск контейнера для Selenoid UI
Selenoid UI для мониторинга выполнения UI тестов.
Заменить <IP-адрес> на адрес IP-адрес контейнера.

   ```bash
   cd selenoid
  ```

  ```bash
   docker run -d --name selenoid-ui \
      -p 8080:8080 \
      aerokube/selenoid-ui \
      --selenoid-uri http://<IP-адрес>:4444
   ```


После запуска Selenoid UI будет доступен по адресу http://localhost:8080/#/, где можно наблюдать доступные браузеры и запущенные сессии.

## Перед запуском тестов выполните следующие шаги:

1. Добавьте значения параметров в файл `src/main/resources/config.properties`:
    ```properties
    host=<IP>:8111
    superUserToken=<TOKEN>
    ```
   - `host` — IP-адрес и порт сервера TeamCity.
   - `superUserToken` — токен авторизации для пользователя с полными правами.


## Запуск тестов

Для запуска тестов с использованием Maven и генерации отчета Allure выполните команду:

   ```bash
      mvn clean test allure:serve
   ```
Run api/ui tests
   ```bash
      mvn clean test -Dtest="com.example.teamcity.api.**" allure:serve
   ```
   ```bash
      mvn clean test -Dtest="com.example.teamcity.ui.**" allure:serve
   ```

## Генерация отчета покрытия API

Для генерации coverage

   ```bash
   .swagger-coverage-commandline/bin/swagger-coverage-commandline -s teamcity.json -i target/swagger-coverage-output
   ```