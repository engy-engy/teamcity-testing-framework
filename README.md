# Automation UI and API Tests

Этот проект предназначен для автоматизации UI и API тестирования с использованием TeamCity и Maven. В данном руководстве
описаны шаги для локального запуска тестов и настройки среды TeamCity.

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

   > **Примечание:** В логах контейнера вы найдете значение `<TOKEN>`, которое необходимо добавить в
   `config.properties`.

3. Получите IP-адрес контейнера для подключения:
    ```bash
    ipconfig getifaddr en0  # macOS
    ipconfig  # Windows
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

## Настройка фермы браузеров с использованием Selenoid

Этот раздел описывает настройку фермы браузеров для выполнения UI-тестов в изолированной среде с использованием Docker и Selenoid.

### Шаг 1: Создание конфигурационного файла `browsers.json`

1. В папке проекта `teamcity-workshop` создайте новую директорию для конфигурации Selenoid:
    ```bash
    cd teamcity-workshop
    mkdir -p selenoid/config
    ```

2. В этой директории создайте файл `browsers.json` с настройками для нужных браузеров. Пример содержимого файла:
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

Чтобы Selenoid мог запускать тесты в нужных версиях браузеров, загрузите указанные образы:

   ```bash
   docker pull selenoid/vnc:firefox_89.0
   docker pull selenoid/vnc:chrome_91.0
   docker pull selenoid/vnc:opera_76.0
   ```

### Шаг 3: Запуск контейнера Selenoid

   ```bash
   cd selenoid/config
   
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

### Шаг 5: Запуск Selenoid UI
Определите IP-адрес машины (замените команду на соответствующую вашей ОС):
   ```bash
   # macOS
   ipconfig getifaddr en0
   ```
   ```bash
   # Windows
   ipconfig
   ```

### Шаг 6: Запуск контейнера для Selenoid UI
   ```bash
   docker run -d --name selenoid-ui \
      -p 8080:8080 \
      aerokube/selenoid-ui \
      --selenoid-uri http://<IP-адрес>:4444
   ```


После запуска Selenoid UI будет доступен по адресу http://localhost:8080/#/, где можно наблюдать доступные браузеры и запущенные сессии.

## Запуск тестов

Для запуска тестов с использованием Maven и генерации отчета Allure выполните команду:

   ```bash
      mvn clean test allure:serve
   ```

## Запуск тестов

Для генерации coverage

   ```bash
   .swagger-coverage-commandline/bin/swagger-coverage-commandline -s teamcity.json -i target/swagger-coverage-output
   ```