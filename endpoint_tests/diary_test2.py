import requests
import json
import logging
from datetime import datetime, timedelta
import random

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
        self.animal_ids = []
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

    def create_animal(self, name, description, birth_date, mass, height, temperature, activity_level, appetite_level):
        """Создание тестового животного с начальными параметрами"""
        animal_data = {
            "name": name,
            "description": description,
            "birthDate": birth_date,
            "mass": mass,
            "height": height,
            "temperature": temperature,
            "activityLevel": activity_level,
            "appetiteLevel": appetite_level,
            "attributes": [
                {"name": "Порода", "value": "Дворняжка"},
                {"name": "Окрас", "value": "Рыжий"}
            ]
        }

        response = self.make_request("POST", "/api/animals", animal_data)
        if response:
            animal_id = response["id"]
            self.animal_ids.append(animal_id)
            logging.info(f"Создано животное {name} с ID: {animal_id}")
            logging.info(f"Начальные параметры: масса={response['mass']}, рост={response['height']}, "
                         f"температура={response['temperature']}, активность={response['activityLevel']}, "
                         f"аппетит={response['appetiteLevel']}")
            return animal_id
        return None

    def update_animal_parameters(self, animal_id, mass=None, height=None, temperature=None,
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

        return self.make_request("PATCH", f"/api/animals/{animal_id}", update_data)

    def add_status_log(self, animal_id, date, notes, mass_change=None, height_change=None,
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
            f"/api/animals/{animal_id}/status-logs",
            log_data
        )

        if response:
            self.status_logs.append(response["id"])
            return True
        return False

    def get_current_parameters(self, animal_id):
        """Получение текущих параметров животного"""
        return self.make_request("GET", f"/api/animals/{animal_id}")

    def get_status_logs(self, animal_id):
        """Получение всех записей дневника"""
        return self.make_request("GET", f"/api/animals/{animal_id}/status-logs")

    def get_parameter_history(self, animal_id, parameter_name):
        """Получение истории изменения параметра"""
        return self.make_request(
            "GET",
            f"/api/animals/{animal_id}/parameters/history/{parameter_name}"
        )

    def get_parameter_stats(self, animal_id, parameter_name):
        """Получение статистики по параметру"""
        return self.make_request(
            "GET",
            f"/api/animals/{animal_id}/parameters/stats/{parameter_name}"
        )


def generate_random_change(base_value, max_change, decimal_places=2):
    """Генерация случайного изменения параметра"""
    change = round(random.uniform(-max_change, max_change), decimal_places)
    return round(base_value + change, decimal_places)


def generate_random_level_change(current_level, max_change=1):
    """Генерация случайного изменения уровня (активность, аппетит)"""
    change = random.randint(1, 4)
    new_level = current_level + change
    return max(1, min(5, new_level))


def create_test_animals(tester):
    """Создание 5 тестовых животных"""
    animals = [
        {
            "name": "Барсик",
            "description": "Ленивый кот",
            "birth_date": "2019-05-10",
            "mass": 4.5,
            "height": 0.4,
            "temperature": 38.2,
            "activity_level": 2,
            "appetite_level": 4
        },
        {
            "name": "Шарик",
            "description": "Энергичный пёс",
            "birth_date": "2020-03-15",
            "mass": 12.0,
            "height": 0.6,
            "temperature": 38.5,
            "activity_level": 4,
            "appetite_level": 5
        },
        {
            "name": "Мурка",
            "description": "Игривая кошка",
            "birth_date": "2021-01-20",
            "mass": 3.8,
            "height": 0.35,
            "temperature": 38.3,
            "activity_level": 3,
            "appetite_level": 3
        },
        {
            "name": "Рекс",
            "description": "Большой пёс",
            "birth_date": "2018-07-05",
            "mass": 25.0,
            "height": 0.8,
            "temperature": 38.7,
            "activity_level": 3,
            "appetite_level": 4
        },
        {
            "name": "Пушинка",
            "description": "Маленький котёнок",
            "birth_date": "2022-11-01",
            "mass": 2.2,
            "height": 0.25,
            "temperature": 38.1,
            "activity_level": 5,
            "appetite_level": 5
        }
    ]

    for animal in animals:
        tester.create_animal(
            name=animal["name"],
            description=animal["description"],
            birth_date=animal["birth_date"],
            mass=animal["mass"],
            height=animal["height"],
            temperature=animal["temperature"],
            activity_level=animal["activity_level"],
            appetite_level=animal["appetite_level"]
        )


def simulate_parameter_changes(tester, animal_id, start_date, days, name):
    """Симуляция изменения параметров животного за период"""
    current_date = datetime.strptime(start_date, "%Y-%m-%d").date()
    end_date = current_date + timedelta(days=days)

    # Начальные параметры
    params = tester.get_current_parameters(animal_id)
    if not params:
        logging.error(f"Не удалось получить параметры для животного {animal_id}")
        return

    current_mass = params["mass"]
    current_height = params["height"]
    current_temp = params["temperature"]
    current_activity = params["activityLevel"]
    current_appetite = params["appetiteLevel"]

    logging.info(f"=== Начало симуляции для {name} ({animal_id}) ===")
    logging.info(f"Период: {start_date} - {end_date.isoformat()}")
    logging.info(f"Начальные параметры: масса={current_mass}, рост={current_height}, "
                 f"температура={current_temp}, активность={current_activity}, аппетит={current_appetite}")

    while current_date <= end_date:
        # Генерация случайных изменений
        new_mass = generate_random_change(current_mass, 0.5)
        new_height = generate_random_change(current_height, 0.02) if random.random() < 0.3 else current_height
        new_temp = generate_random_change(current_temp, 0.3)
        new_activity = generate_random_level_change(current_activity)
        new_appetite = generate_random_level_change(current_appetite)

        # Определение типа записи
        if random.random() < 0.7:  # 70% - обычный осмотр
            notes = "Ежедневный осмотр"
        else:  # 30% - особый случай
            special_cases = [
                "Визит к ветеринару",
                "Плановый осмотр",
                "После вакцинации",
                "После болезни",
                "После активной прогулки"
            ]
            notes = random.choice(special_cases)

        # Добавление записи
        tester.add_status_log(
            animal_id=animal_id,
            date=current_date.isoformat(),
            notes=notes,
            mass_change=new_mass - current_mass,
            height_change=new_height - current_height if new_height != current_height else None,
            temp_change=new_temp - current_temp,
            activity_change=new_activity - current_activity,
            appetite_change=new_appetite - current_appetite
        )

        # Обновление текущих параметров
        current_mass = new_mass
        current_height = new_height
        current_temp = new_temp
        current_activity = new_activity
        current_appetite = new_appetite

        # Переход к следующему дню
        current_date += timedelta(days=1)

    # Получение финальных параметров
    final_params = tester.get_current_parameters(animal_id)
    if final_params:
        logging.info(f"Финальные параметры: масса={final_params['mass']}, рост={final_params['height']}, "
                     f"температура={final_params['temperature']}, активность={final_params['activityLevel']}, "
                     f"аппетит={final_params['appetiteLevel']}")

    logging.info(f"=== Завершение симуляции для {name} ({animal_id}) ===")


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
        # 1. Создание 5 тестовых животных
        logging.info("=== Создание 5 тестовых животных ===")
        create_test_animals(tester)

        if not tester.animal_ids:
            logging.error("Не удалось создать животных")
            return

        # 2. Симуляция изменений параметров для каждого животного
        today = datetime.now().date()

        # Животное 1: изменения за неделю
        simulate_parameter_changes(
            tester,
            tester.animal_ids[0],
            (today - timedelta(days=7)).isoformat(),
            7,
            "Барсик (неделя)"
        )

        # Животное 2: изменения за месяц
        simulate_parameter_changes(
            tester,
            tester.animal_ids[1],
            (today - timedelta(days=30)).isoformat(),
            30,
            "Шарик (месяц)"
        )

        # Животное 3: изменения за 3 месяца
        simulate_parameter_changes(
            tester,
            tester.animal_ids[2],
            (today - timedelta(days=90)).isoformat(),
            90,
            "Мурка (3 месяца)"
        )

        # Животное 4: изменения за полгода
        simulate_parameter_changes(
            tester,
            tester.animal_ids[3],
            (today - timedelta(days=180)).isoformat(),
            180,
            "Рекс (полгода)"
        )

        # Животное 5: изменения за год
        simulate_parameter_changes(
            tester,
            tester.animal_ids[4],
            (today - timedelta(days=365)).isoformat(),
            365,
            "Пушинка (год)"
        )

        # 3. Вывод статистики для каждого животного
        for animal_id in tester.animal_ids:
            params = tester.get_current_parameters(animal_id)
            if params:
                logging.info(f"=== Итоговые параметры животного {params['name']} ({animal_id}) ===")
                for param in ["mass", "height", "temperature", "activityLevel", "appetiteLevel"]:
                    history = tester.get_parameter_history(animal_id, param)
                    stats = tester.get_parameter_stats(animal_id, param)
                    logging.info(f"История {param}: {len(history)} записей")
                    logging.info(f"Статистика {param}: {stats}")

    except Exception as e:
        logging.error(f"Произошла ошибка: {str(e)}")
    finally:
        logging.info("Тестирование завершено. Животные не удаляются для дальнейших проверок.")


if __name__ == "__main__":
    main()