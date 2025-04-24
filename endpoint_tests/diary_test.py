import requests
import json
import logging
from datetime import datetime, timedelta

# Настройка логирования
logging.basicConfig(
    level=logging.INFO,
    format='%(asctime)s - %(levelname)s - %(message)s',
    handlers=[
        logging.FileHandler('animal_diary_test.log'),
        logging.StreamHandler()
    ]
)


class AnimalDiaryTester:
    def __init__(self, base_url, auth_url, username, password):
        self.base_url = base_url
        self.auth_url = auth_url
        self.username = username
        self.password = password
        self.access_token = None
        self.animal_id = None
        self.status_logs = []

    def authenticate(self):
        """Аутентификация и получение токена"""
        auth_data = {
            "indentifier": self.username,
            "password": self.password
        }

        try:
            response = requests.post(
                f"{self.auth_url}/api/auth/authenticate",
                json=auth_data,
                headers={"Content-Type": "application/json"}
            )
            response.raise_for_status()

            auth_response = response.json()
            self.access_token = auth_response["access_token"]
            logging.info(f"Успешная аутентификация. Токен получен: {self.access_token[:15]}...")
            return True

        except Exception as e:
            logging.error(f"Ошибка аутентификации: {str(e)}")
            return False

    def make_request(self, method, endpoint, data=None, params=None):
        """Универсальный метод для выполнения запросов"""
        headers = {
            "Authorization": f"Bearer {self.access_token}",
            "Content-Type": "application/json"
        }

        url = f"{self.base_url}{endpoint}"

        try:
            logging.info(f"Выполнение запроса: {method} {url}")
            if data:
                logging.info(f"Тело запроса: {json.dumps(data, indent=2, ensure_ascii=False)}")

            response = requests.request(
                method,
                url,
                json=data,
                params=params,
                headers=headers
            )

            logging.info(f"Статус ответа: {response.status_code}")
            if response.text:
                try:
                    logging.info(f"Тело ответа: {json.dumps(response.json(), indent=2, ensure_ascii=False)}")
                except:
                    logging.info(f"Тело ответа: {response.text}")

            response.raise_for_status()
            return response.json() if response.text else None

        except Exception as e:
            logging.error(f"Ошибка запроса: {str(e)}")
            return None

    def create_animal(self):
        """Создание тестового животного с начальными параметрами"""
        animal_data = {
            "name": "Тестовый питомец",
            "description": "Тестовое описание",
            "birthDate": "2020-01-15",
            "mass": 5.2,
            "height": 0.5,
            "temperature": 38.5,
            "activityLevel": 3,
            "appetiteLevel": 2,
            "attributes": [
                {"name": "Порода", "value": "Дворняжка"},
                {"name": "Окрас", "value": "Рыжий"}
            ]
        }

        response = self.make_request("POST", "/api/animals", animal_data)
        if response:
            self.animal_id = response["id"]
            logging.info(f"Создано животное с ID: {self.animal_id}")
            logging.info(f"Начальные параметры: масса={response['mass']}, рост={response['height']}, "
                         f"температура={response['temperature']}, активность={response['activityLevel']}, "
                         f"аппетит={response['appetiteLevel']}")
            return True
        return False

    def update_animal_parameters(self, mass=None, height=None, temperature=None,
                                 activity_level=None, appetite_level=None):
        """Обновление параметров животного"""
        update_data = {
            "mass": mass,
            "height": height,
            "temperature": temperature,
            "activityLevel": activity_level,
            "appetiteLevel": appetite_level
        }

        # Удаляем None значения
        update_data = {k: v for k, v in update_data.items() if v is not None}

        return self.make_request("PATCH", f"/api/animals/{self.animal_id}", update_data)

    def add_status_log(self, date, notes, mass_change=None, height_change=None,
                       temp_change=None, activity_change=None, appetite_change=None):
        """Добавление записи в дневник"""
        log_data = {
            "logDate": date,
            "notes": notes,
            "massChange": mass_change,
            "heightChange": height_change,
            "temperatureChange": temp_change,
            "activityLevelChange": activity_change,
            "appetiteLevelChange": appetite_change
        }

        response = self.make_request(
            "POST",
            f"/api/animals/{self.animal_id}/status-logs",
            log_data
        )

        if response:
            self.status_logs.append(response["id"])
            return True
        return False

    def get_current_parameters(self):
        """Получение текущих параметров животного"""
        return self.make_request("GET", f"/api/animals/{self.animal_id}")

    def get_status_logs(self):
        """Получение всех записей дневника"""
        return self.make_request("GET", f"/api/animals/{self.animal_id}/status-logs")

    def get_parameter_history(self, parameter_name):
        """Получение истории изменения параметра"""
        return self.make_request(
            "GET",
            f"/api/animals/{self.animal_id}/parameters/history/{parameter_name}"
        )

    def get_parameter_stats(self, parameter_name):
        """Получение статистики по параметру"""
        return self.make_request(
            "GET",
            f"/api/animals/{self.animal_id}/parameters/stats/{parameter_name}"
        )


def main():
    # Конфигурация
    BASE_URL = "http://localhost:80"  # URL вашего сервиса
    AUTH_URL = "http://localhost:80"  # URL сервиса аутентификации
    USERNAME = "chikernut213@gmail.com"  # Замените на реальные данные
    PASSWORD = "chikernut213@gmail.com"  # Замените на реальные данные

    tester = AnimalDiaryTester(BASE_URL, AUTH_URL, USERNAME, PASSWORD)

    # Аутентификация
    if not tester.authenticate():
        return

    try:
        # 1. Создание животного с начальными параметрами
        logging.info("=== Создание животного с начальными параметрами ===")
        if not tester.create_animal():
            return

        # 2. Проверка текущих параметров
        logging.info("=== Текущие параметры животного ===")
        current_params = tester.get_current_parameters()
        logging.info(f"Текущее состояние: {json.dumps(current_params, indent=2, ensure_ascii=False)}")

        # 3. Добавление записей в дневник за разные периоды
        today = datetime.now().date().isoformat()
        yesterday = (datetime.now() - timedelta(days=1)).date().isoformat()
        week_ago = (datetime.now() - timedelta(days=7)).date().isoformat()
        two_weeks_ago = (datetime.now() - timedelta(days=14)).date().isoformat()
        month_ago = (datetime.now() - timedelta(days=30)).date().isoformat()

        logging.info("=== Добавление записи месяц назад (первый осмотр) ===")
        tester.add_status_log(
            date=month_ago,
            notes="Первый осмотр у ветеринара",
            mass_change=0.8,
            height_change=0.15,
            temp_change=0.3,
            activity_change=1,
            appetite_change=1
        )

        logging.info("=== Добавление записи 2 недели назад ===")
        tester.add_status_log(
            date=two_weeks_ago,
            notes="Плановый осмотр после лечения",
            mass_change=-0.2,
            temp_change=-0.1,
            activity_change=-1,
            appetite_change=-1
        )

        logging.info("=== Добавление записи 1 неделю назад ===")
        tester.add_status_log(
            date=week_ago,
            notes="Улучшение состояния",
            mass_change=0.4,
            height_change=0.05,
            temp_change=0.1,
            activity_change=2,
            appetite_change=2
        )

        logging.info("=== Добавление записи вчера ===")
        tester.add_status_log(
            date=yesterday,
            notes="Ежедневный осмотр",
            mass_change=0.1,
            temp_change=-0.05,
            appetite_change=-1
        )

        logging.info("=== Добавление записи сегодня ===")
        tester.add_status_log(
            date=today,
            notes="Текущий осмотр перед прививкой",
            mass_change=0.05,
            activity_change=1
        )

        # 4. Проверка всех записей дневника
        logging.info("=== Все записи дневника ===")
        tester.get_status_logs()

        # 5. Проверка истории изменений по всем параметрам
        for param in ["mass", "height", "temperature", "activityLevel", "appetiteLevel"]:
            logging.info(f"=== История изменения {param} ===")
            tester.get_parameter_history(param)

        # 6. Проверка статистики по всем параметрам
        for param in ["mass", "height", "temperature", "activityLevel", "appetiteLevel"]:
            logging.info(f"=== Статистика по {param} ===")
            tester.get_parameter_stats(param)

        # 7. Проверка текущих параметров после всех изменений
        logging.info("=== Текущие параметры после всех изменений ===")
        final_params = tester.get_current_parameters()
        logging.info(f"Финальное состояние: {json.dumps(final_params, indent=2, ensure_ascii=False)}")

        # 8. Сравнение начальных и конечных параметров
        logging.info("=== Сравнение начальных и конечных параметров ===")
        logging.info(f"Масса: {current_params['mass']} -> {final_params['mass']}")
        logging.info(f"Рост: {current_params['height']} -> {final_params['height']}")
        logging.info(f"Температура: {current_params['temperature']} -> {final_params['temperature']}")
        logging.info(f"Активность: {current_params['activityLevel']} -> {final_params['activityLevel']}")
        logging.info(f"Аппетит: {current_params['appetiteLevel']} -> {final_params['appetiteLevel']}")

    except Exception as e:
        logging.error(f"Произошла ошибка: {str(e)}")
    finally:
        logging.info("Тестирование завершено. Животное не удаляется для дальнейших проверок.")


if __name__ == "__main__":
    main()