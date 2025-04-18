import requests
import json
import uuid
from datetime import datetime, timedelta
import os
import logging
from io import BytesIO

# Настройка логирования
logging.basicConfig(
    level=logging.INFO,
    format='%(asctime)s - %(levelname)s - %(message)s',
    handlers=[
        logging.FileHandler('animal_tracker_test3.log'),
        logging.StreamHandler()
    ]
)
logger = logging.getLogger(__name__)

# Конфигурация
BASE_URL = "http://localhost:80"  # Замените на реальный URL вашего API
AUTH_URL = f"{BASE_URL}/api/auth"
ANIMALS_URL = f"{BASE_URL}/api/animals"
USERS_URL = f"{BASE_URL}/api/users"

# Тестовые данные
TEST_USERNAME = f"testuser_{uuid.uuid4().hex[:8]}"
TEST_EMAIL = f"zinasoska137@gmail.com"
TEST_PASSWORD = "Test1234!"

# Глобальные переменные для хранения состояния между тестами
access_token = None
animal_id = None
status_log_id = None
attribute_id = None
photo_id = None
document_id = None

request_response_logs = []

def log_request_response(method, url, request_data=None, response=None, params=None, files=None):
    """Логирование деталей запроса и ответа с проверкой на пустой ответ"""
    log_entry = {
        "timestamp": datetime.now().isoformat(),
        "method": method,
        "url": url,
        "request": {
            "headers": get_headers() if access_token else None,
            "params": params,
            "data": request_data if request_data else None,
            "files": {
                "count": len(files) if files else 0,
                "filenames": [f[0] for f in files.values()] if files else None
            }
        },
        "response": {
            "status_code": response.status_code if response else None,
            "data": None,
            "headers": dict(response.headers) if response else None
        }
    }

    # Обработка тела ответа
    if response and response.content:
        try:
            content_type = response.headers.get('Content-Type', '')
            if 'application/json' in content_type:
                log_entry["response"]["data"] = response.json()
            elif 'application/pdf' in content_type:
                log_entry["response"]["data"] = "PDF content (binary)"
            else:
                log_entry["response"]["data"] = response.text[:200] + ("..." if len(response.text) > 200 else "")
        except Exception as e:
            log_entry["response"]["error"] = f"Failed to parse response: {str(e)}"
            log_entry["response"]["raw"] = response.text[:500] if response.text else None

    request_response_logs.append(log_entry)
    logger.debug(f"Request/Response logged for {method} {url}")


def save_logs_to_file():
    """Сохранение всех логов в JSON файл с обработкой ошибок"""
    if not request_response_logs:
        logger.warning("No request/response logs to save")
        return

    filename = f"api_test_logs_{datetime.now().strftime('%Y%m%d_%H%M%S')}.json"
    try:
        with open(filename, 'w', encoding='utf-8') as f:
            json.dump(request_response_logs, f, indent=2, ensure_ascii=False)
        logger.info(f"All request/response logs saved to {filename}")
    except Exception as e:
        logger.error(f"Failed to save logs to file: {str(e)}")
        # Попробуем сохранить хотя бы в упрощенном формате
        try:
            simplified_logs = []
            for log in request_response_logs:
                simplified = {
                    "method": log.get("method"),
                    "url": log.get("url"),
                    "status": log.get("response", {}).get("status_code")
                }
                simplified_logs.append(simplified)

            with open(f"simplified_{filename}", 'w', encoding='utf-8') as f:
                json.dump(simplified_logs, f, indent=2)
            logger.info(f"Simplified logs saved to simplified_{filename}")
        except Exception as e2:
            logger.error(f"Failed to save even simplified logs: {str(e2)}")


def register_user():
    """Регистрация тестового пользователя"""
    global TEST_USERNAME, TEST_EMAIL, TEST_PASSWORD

    url = f"{AUTH_URL}/register"
    payload = {
        "username": TEST_USERNAME,
        "email": TEST_EMAIL,
        "password": TEST_PASSWORD
    }

    logger.info(f"Registering user: {TEST_USERNAME}")
    response = requests.post(url, json=payload)
    log_request_response("POST", url, request_data=payload, response=response)
    if response.status_code == 201:
        logger.info(f"User registered successfully. Username: {TEST_USERNAME}")
        return True
    else:
        logger.error(f"Failed to register user. Status code: {response.status_code}, Response: {response.text}")
        return False


def authenticate_user():
    """Аутентификация пользователя и получение токена"""
    global access_token, TEST_USERNAME, TEST_PASSWORD

    url = f"{AUTH_URL}/authenticate"
    payload = {
        "indentifier": TEST_USERNAME,
        "password": TEST_PASSWORD
    }

    logger.info("Authenticating user...")
    response = requests.post(url, json=payload)
    log_request_response("POST", url, request_data=payload, response=response)
    if response.status_code == 200:
        access_token = response.json()["access_token"]
        logger.info("Authentication successful. Token received.")
        return True
    else:
        logger.error(f"Authentication failed. Status code: {response.status_code}, Response: {response.text}")
        return False


def get_headers():
    """Получение заголовков с токеном авторизации"""
    return {
        "Authorization": f"Bearer {access_token}",
        "Content-Type": "application/json"
    }


def test_create_animal():
    """Тестирование создания животного"""
    global animal_id

    url = f"{ANIMALS_URL}"
    payload = {
        "name": "Барсик",
        "description": "Тестовый кот для проверки API",
        "birthDate": "2020-01-15",
        "mass": "4.5",
        "attributes": [
            {"name": "Цвет", "value": "Серый"},
            {"name": "Порода", "value": "Британский"},
            {"name": "Возраст", "value": "3"}
        ]
    }

    logger.info("Creating animal...")
    response = requests.post(url, json=payload, headers=get_headers())
    log_request_response("POST", url, request_data=payload, response=response)
    if response.status_code == 200:
        animal_id = response.json()["id"]
        logger.info(f"Animal created successfully. ID: {animal_id}")
        logger.debug(f"Response: {response.json()}")
        return True
    else:
        logger.error(f"Failed to create animal. Status code: {response.status_code}, Response: {response.text}")
        return False


def test_get_animal():
    """Тестирование получения информации о животном"""
    if not animal_id:
        logger.warning("Animal ID not set, skipping test")
        return False

    url = f"{ANIMALS_URL}/{animal_id}"

    logger.info(f"Getting animal with ID: {animal_id}")
    response = requests.get(url, headers=get_headers())
    log_request_response("GET", url, request_data={"a" : "b"}, response=response)
    if response.status_code == 200:
        logger.info("Animal retrieved successfully")
        logger.debug(f"Response: {response.json()}")
        return True
    else:
        logger.error(f"Failed to get animal. Status code: {response.status_code}, Response: {response.text}")
        return False


def test_update_animal():
    """Тестирование обновления информации о животном"""
    if not animal_id:
        logger.warning("Animal ID not set, skipping test")
        return False

    url = f"{ANIMALS_URL}/{animal_id}"
    payload = {
        "name": "Барсик (обновленный)",
        "description": "Обновленное описание",
        "birthDate": "2020-01-20",
        "mass": "5.0"
    }

    logger.info(f"Updating animal with ID: {animal_id}")
    response = requests.patch(url, json=payload, headers=get_headers())
    log_request_response("PATCH", url, request_data=payload, response=response)
    if response.status_code == 200:
        logger.info("Animal updated successfully")
        logger.debug(f"Response: {response.json()}")
        return True
    else:
        logger.error(f"Failed to update animal. Status code: {response.status_code}, Response: {response.text}")
        return False


def test_upload_animal_photo():
    """Тестирование загрузки фото животного"""
    global photo_id

    if not animal_id:
        logger.warning("Animal ID not set, skipping test")
        return False

    url = f"{ANIMALS_URL}/{animal_id}/photos"

    # Создаем тестовый файл в памяти
    test_file = BytesIO(b"test image content")
    files = {'file': ('test_image.jpg', test_file, 'image/jpeg')}

    headers = get_headers()
    headers.pop("Content-Type")  # Удаляем Content-Type для multipart/form-data

    logger.info(f"Uploading photo for animal with ID: {animal_id}")
    response = requests.post(url, files=files, headers=headers)
    log_request_response("POST", url, request_data={"a" : "b"}, response=response)
    if response.status_code == 200:
        photo_id = response.json()["objectKey"].split("/")[-1]  # Получаем ID фото из objectKey
        logger.info(f"Photo uploaded successfully. Photo ID: {photo_id}")
        logger.debug(f"Response: {response.json()}")
        return True
    else:
        logger.error(f"Failed to upload photo. Status code: {response.status_code}, Response: {response.text}")
        return False


def test_upload_animal_document():
    """Тестирование загрузки документа животного"""
    global document_id

    if not animal_id:
        logger.warning("Animal ID not set, skipping test")
        return False

    url = f"{ANIMALS_URL}/{animal_id}/documents"

    # Создаем тестовый файл в памяти
    test_file = BytesIO(b"test document content")
    files = {'file': ('test_document.pdf', test_file, 'application/pdf')}
    params = {'type': 'medical_record'}

    headers = get_headers()
    headers.pop("Content-Type")  # Удаляем Content-Type для multipart/form-data

    logger.info(f"Uploading document for animal with ID: {animal_id}")
    response = requests.post(url, files=files, headers=headers, params=params)
    log_request_response("POST", url, request_data={"file" : "file"}, response=response)
    if response.status_code == 200:
        document_id = response.json()["objectKey"].split("/")[-1]  # Получаем ID документа из objectKey
        logger.info(f"Document uploaded successfully. Document ID: {document_id}")
        logger.debug(f"Response: {response.json()}")
        return True
    else:
        logger.error(f"Failed to upload document. Status code: {response.status_code}, Response: {response.text}")
        return False


def test_add_status_log():
    """Тестирование добавления записи о состоянии животного"""
    global status_log_id

    if not animal_id:
        logger.warning("Animal ID not set, skipping test")
        return False

    url = f"{ANIMALS_URL}/{animal_id}/status-logs"
    payload = {
        "notes": "Тестовая запись о состоянии",
        "logDate": str(datetime.now().date())
    }

    logger.info(f"Adding status log for animal with ID: {animal_id}")
    response = requests.post(url, json=payload, headers=get_headers())
    log_request_response("POST", url, request_data=payload, response=response)
    if response.status_code == 200:
        status_log_id = response.json()["id"]
        logger.info(f"Status log added successfully. Status log ID: {status_log_id}")
        logger.debug(f"Response: {response.json()}")
        return True
    else:
        logger.error(f"Failed to add status log. Status code: {response.status_code}, Response: {response.text}")
        return False


def test_get_status_logs():
    """Тестирование получения записей о состоянии животного"""
    if not animal_id:
        logger.warning("Animal ID not set, skipping test")
        return False

    url = f"{ANIMALS_URL}/{animal_id}/status-logs"

    logger.info(f"Getting status logs for animal with ID: {animal_id}")
    response = requests.get(url, headers=get_headers())
    log_request_response("GET", url, request_data={"a" : "b"}, response=response)
    if response.status_code == 200:
        logger.info("Status logs retrieved successfully")
        logger.debug(f"Response: {response.json()}")
        return True
    else:
        logger.error(f"Failed to get status logs. Status code: {response.status_code}, Response: {response.text}")
        return False


# def test_upload_status_log_photo():
#     """Тестирование загрузки фото для записи о состоянии"""
#     if not animal_id or not status_log_id:
#         logger.warning("Animal ID or Status log ID not set, skipping test")
#         return False
#
#     url = f"{ANIMALS_URL}/{animal_id}/status-logs/{status_log_id}/photos"
#
#     # Создаем тестовый файл в памяти
#     test_file = BytesIO(b"test status log image content")
#     files = {'file': ('test_status_log_image.jpg', test_file, 'image/jpeg')}
#
#     headers = get_headers()
#     headers.pop("Content-Type")  # Удаляем Content-Type для multipart/form-data
#
#     logger.info(f"Uploading photo for status log with ID: {status_log_id}")
#     response = requests.post(url, files=files, headers=headers)
#     log_request_response("POST", url, request_data={"a" : "b"}, response=response)
#     if response.status_code == 200:
#         logger.info("Status log photo uploaded successfully")
#         logger.debug(f"Response: {response.json()}")
#         return True
#     else:
#         logger.error(
#             f"Failed to upload status log photo. Status code: {response.status_code}, Response: {response.text}")
#         return False


def test_upload_status_log_document():
    """Тестирование загрузки документа для записи о состоянии"""
    if not animal_id or not status_log_id:
        logger.warning("Animal ID or Status log ID not set, skipping test")
        return False

    url = f"{ANIMALS_URL}/{animal_id}/status-logs/{status_log_id}/documents"

    # Создаем тестовый файл в памяти
    test_file = BytesIO(b"test status log document content")
    files = {'file': ('test_status_log_document.pdf', test_file, 'application/pdf')}
    params = {'type': 'vet_report'}

    headers = get_headers()
    headers.pop("Content-Type")  # Удаляем Content-Type для multipart/form-data

    logger.info(f"Uploading document for status log with ID: {status_log_id}")
    response = requests.post(url, files=files, headers=headers, params=params)
    log_request_response("POST", url, request_data={"a" : "b"}, response=response)
    if response.status_code == 200:
        logger.info("Status log document uploaded successfully")
        logger.debug(f"Response: {response.json()}")
        return True
    else:
        logger.error(
            f"Failed to upload status log document. Status code: {response.status_code}, Response: {response.text}")
        return False


def test_update_status_log():
    """Тестирование обновления записи о состоянии"""
    if not animal_id or not status_log_id:
        logger.warning("Animal ID or Status log ID not set, skipping test")
        return False

    url = f"{ANIMALS_URL}/{animal_id}/status-logs/{status_log_id}"
    payload = {
        "notes": "Обновленная запись о состоянии",
        "logDate": str(datetime.now().date() - timedelta(days=1))
    }

    logger.info(f"Updating status log with ID: {status_log_id}")
    response = requests.put(url, json=payload, headers=get_headers())
    log_request_response("PUT", url, request_data=payload, response=response)
    if response.status_code == 200:
        logger.info("Status log updated successfully")
        logger.debug(f"Response: {response.json()}")
        return True
    else:
        logger.error(f"Failed to update status log. Status code: {response.status_code}, Response: {response.text}")
        return False


def test_add_attribute():
    """Тестирование добавления атрибута животного"""
    global attribute_id

    if not animal_id:
        logger.warning("Animal ID not set, skipping test")
        return False

    url = f"{ANIMALS_URL}/{animal_id}/attributes"
    payload = {
        "name": "Вес",
        "value": "4.2"
    }

    logger.info(f"Adding attribute for animal with ID: {animal_id}")
    response = requests.post(url, json=payload, headers=get_headers())
    log_request_response("POST", url, request_data=payload, response=response)
    if response.status_code == 201:
        attribute_id = response.json()["id"]
        logger.info(f"Attribute added successfully. Attribute ID: {attribute_id}")
        logger.debug(f"Response: {response.json()}")
        return True
    else:
        logger.error(f"Failed to add attribute. Status code: {response.status_code}, Response: {response.text}")
        return False


def test_update_attribute():
    """Тестирование обновления атрибута животного"""
    if not animal_id or not attribute_id:
        logger.warning("Animal ID or Attribute ID not set, skipping test")
        return False

    url = f"{ANIMALS_URL}/{animal_id}/attributes/{attribute_id}"
    payload = {
        "name": "Вес (обновленный)",
        "value": "4.5"
    }

    logger.info(f"Updating attribute with ID: {attribute_id}")
    response = requests.patch(url, json=payload, headers=get_headers())
    log_request_response("PATCH", url, request_data=payload, response=response)
    if response.status_code == 200:
        logger.info("Attribute updated successfully")
        logger.debug(f"Response: {response.json()}")
        return True
    else:
        logger.error(f"Failed to update attribute. Status code: {response.status_code}, Response: {response.text}")
        return False


def test_get_attributes_history():
    """Тестирование получения истории изменений атрибутов"""
    if not animal_id:
        logger.warning("Animal ID not set, skipping test")
        return False

    url = f"{ANIMALS_URL}/{animal_id}/attributes/history"

    logger.info(f"Getting attributes history for animal with ID: {animal_id}")
    response = requests.get(url, headers=get_headers())
    log_request_response("GET", url, request_data={"a" : "b"}, response=response)
    if response.status_code == 200:
        logger.info("Attributes history retrieved successfully")
        logger.debug(f"Response: {response.json()}")
        return True
    else:
        logger.error(
            f"Failed to get attributes history. Status code: {response.status_code}, Response: {response.text}")
        return False


def test_get_animal_analytics():
    """Тестирование получения аналитики по животному"""
    if not animal_id:
        logger.warning("Animal ID not set, skipping test")
        return False

    url = f"{ANIMALS_URL}/{animal_id}/analytics"

    logger.info(f"Getting analytics for animal with ID: {animal_id}")
    response = requests.get(url, headers=get_headers())
    log_request_response("GET", url, request_data={"a" : "b"}, response=response)
    if response.status_code == 200:
        logger.info("Animal analytics retrieved successfully")
        logger.debug(f"Response: {response.json()}")
        return True
    else:
        logger.error(f"Failed to get animal analytics. Status code: {response.status_code}, Response: {response.text}")
        return False


def test_export_animal_to_pdf():
    """Тестирование экспорта информации о животном в PDF"""
    if not animal_id:
        logger.warning("Animal ID not set, skipping test")
        return False

    url = f"{ANIMALS_URL}/{animal_id}/export/pdf"

    logger.info(f"Exporting animal with ID: {animal_id} to PDF")
    response = requests.get(url, headers=get_headers())
    log_request_response("GET", url, request_data={"a" : "b"}, response=response)
    if response.status_code == 200:
        # Сохраняем PDF для проверки
        with open(f"animal_{animal_id}.pdf", "wb") as f:
            f.write(response.content)
        logger.info(f"Animal exported to PDF successfully. File saved as animal_{animal_id}.pdf")
        return True
    else:
        logger.error(f"Failed to export animal to PDF. Status code: {response.status_code}, Response: {response.text}")
        return False


def test_get_user_animals():
    """Тестирование получения списка животных пользователя"""
    url = f"{USERS_URL}/me/animals"

    logger.info("Getting user's animals")
    response = requests.get(url, headers=get_headers())
    log_request_response("GET", url, request_data={"a" : "b"}, response=response)
    if response.status_code == 200:
        logger.info("User's animals retrieved successfully")
        logger.debug(f"Response: {response.json()}")
        return True
    else:
        logger.error(f"Failed to get user's animals. Status code: {response.status_code}, Response: {response.text}")
        return False


def test_delete_status_log():
    """Тестирование удаления записи о состоянии"""
    url = f"{ANIMALS_URL}/{animal_id}/status-logs/{status_log_id}"
    logger.info(f"Deleting status log with ID: {status_log_id}")
    if not animal_id or not status_log_id:
        logger.warning("Animal ID or Status log ID not set, skipping test")
        return False
    response = requests.delete(url, headers=get_headers())
    log_request_response("DELETE", url, request_data={"a" : "b"}, response=response)
    if response.status_code == 204:
        logger.info("Status log deleted successfully")
        return True
    else:
        logger.error(f"Failed to delete status log. Status code: {response.status_code}, Response: {response.text}")
        return False

def test_delete_attribute():
    """Тестирование удаления атрибута"""
    if not animal_id or not attribute_id:
        logger.warning("Animal ID or Attribute ID not set, skipping test")
        return False
    url = f"{ANIMALS_URL}/{animal_id}/attributes/{attribute_id}"

    logger.info(f"Deleting attribute with ID: {attribute_id}")
    response = requests.delete(url, headers=get_headers())
    log_request_response("DELETE", url, request_data={"a": "b"}, response=response)
    if response.status_code == 204:
        logger.info("Attribute deleted successfully")
        return True
    else:
        logger.error(f"Failed to delete attribute. Status code: {response.status_code}, Response: {response.text}")
        return False

def test_delete_animal_photo():
    """Тестирование удаления фото животного"""
    if not photo_id:
        logger.warning("Photo ID not set, skipping test")
        return False
    url = f"{ANIMALS_URL}/photos/{photo_id}"

    logger.info(f"Deleting animal photo with ID: {photo_id}")
    response = requests.delete(url, headers=get_headers())
    log_request_response("DELETE", url, request_data={"a": "b"}, response=response)
    if response.status_code == 204:
        logger.info("Animal photo deleted successfully")
        return True
    else:
        logger.error(f"Failed to delete animal photo. Status code: {response.status_code}, Response: {response.text}")
        return False

def test_delete_animal_document():
    """Тестирование удаления документа животного"""
    if not document_id:
        logger.warning("Document ID not set, skipping test")
        return False
    url = f"{ANIMALS_URL}/documents/{document_id}"

    logger.info(f"Deleting animal document with ID: {document_id}")
    response = requests.delete(url, headers=get_headers())
    log_request_response("DELETE", url, request_data={"a": "b"}, response=response)
    if response.status_code == 204:
        logger.info("Animal document deleted successfully")
        return True
    else:
        logger.error(
            f"Failed to delete animal document. Status code: {response.status_code}, Response: {response.text}")
        return False

def test_delete_animal():
    """Тестирование удаления животного"""
    if not animal_id:
        logger.warning("Animal ID not set, skipping test")
        return False
    url = f"{ANIMALS_URL}/{animal_id}"

    logger.info(f"Deleting animal with ID: {animal_id}")
    response = requests.delete(url, headers=get_headers())
    log_request_response("DELETE", url, request_data={"a": "b"}, response=response)
    if response.status_code == 204:
        logger.info("Animal deleted successfully")
        return True
    else:
        logger.error(f"Failed to delete animal. Status code: {response.status_code}, Response: {response.text}")
        return False


def create_multiple_status_logs(count=10):
    """Создание нескольких записей о состоянии с разными датами"""
    global status_log_id

    if not animal_id:
        logger.warning("Animal ID not set, skipping test")
        return False

    base_date = datetime.now().date()
    results = []

    for i in range(count):
        log_date = base_date - timedelta(days=i)
        url = f"{ANIMALS_URL}/{animal_id}/status-logs"
        payload = {
            "notes": f"Тестовая запись #{i + 1} за {log_date}",
            "logDate": str(log_date)
        }

        logger.info(f"Adding status log #{i + 1} for {log_date}")
        response = requests.post(url, json=payload, headers=get_headers())
        log_request_response("POST", url, request_data=payload, response=response)

        if response.status_code == 200:
            log_id = response.json()["id"]
            results.append(log_id)
            logger.info(f"Status log added successfully. ID: {log_id}")

            # Добавляем фото и документы для некоторых записей
            if i % 3 == 0:
                test_upload_status_log_photo(log_id)
            if i % 4 == 0:
                test_upload_status_log_document(log_id)
        else:
            logger.error(f"Failed to add status log #{i + 1}. Status: {response.status_code}")

    if results:
        status_log_id = results[0]  # Сохраняем первый ID для других тестов
        return True
    return False


def test_upload_status_log_photo(log_id=None):
    """Тестирование загрузки фото для записи о состоянии"""
    if not animal_id:
        logger.warning("Animal ID not set, skipping test")
        return False

    target_log_id = log_id if log_id else status_log_id
    if not target_log_id:
        logger.warning("Status log ID not set, skipping test")
        return False

    url = f"{ANIMALS_URL}/{animal_id}/status-logs/{target_log_id}/photos"
    test_file = BytesIO(b"test status log image content " + str(datetime.now()).encode())
    files = {'file': (f'test_image_{target_log_id}.jpg', test_file, 'image/jpeg')}

    headers = get_headers()
    headers.pop("Content-Type")

    logger.info(f"Uploading photo for status log {target_log_id}")
    response = requests.post(url, files=files, headers=headers)
    log_request_response("POST", url, files=files, response=response)

    if response.status_code == 200:
        logger.info("Status log photo uploaded successfully")
        return True
    else:
        logger.error(f"Failed to upload photo. Status: {response.status_code}")
        return False


def run_tests():
    """Запуск всех тестов"""
    # Регистрация и аутентификация
    if not register_user():
        logger.error("User registration failed, cannot proceed with tests")
        return False
    # Имитация активации аккаунта (в реальной системе нужно подтвердить email)
    logger.info("Simulating account activation (in real system would require email confirmation)")

    if not authenticate_user():
        logger.error("Authentication failed, cannot proceed with tests")
        return False

    # Основные тесты
    test_results = {
        "create_animal": test_create_animal(),
        "get_animal": test_get_animal(),
        "update_animal": test_update_animal(),
        "upload_animal_photo": test_upload_animal_photo(),
        "upload_animal_document": test_upload_animal_document(),
        "create_10_status_logs": create_multiple_status_logs(10),
        "get_status_logs": test_get_status_logs(),
        "update_first_status_log": test_update_status_log(),
        "upload_status_log_photo": test_upload_status_log_photo(),
        "upload_status_log_document": test_upload_status_log_document(),
        "update_status_log": test_update_status_log(),
        "add_attribute": test_add_attribute(),
        "update_attribute": test_update_attribute(),
        "get_attributes_history": test_get_attributes_history(),
        "get_animal_analytics": test_get_animal_analytics(),
        "export_animal_to_pdf": test_export_animal_to_pdf(),
        "get_user_animals": test_get_user_animals(),
        # "delete_status_log": test_delete_status_log(),
        # "delete_attribute": test_delete_attribute(),
        # "delete_animal_photo": test_delete_animal_photo(),
        # "delete_animal_document": test_delete_animal_document(),
        # "delete_animal": test_delete_animal()
    }

    # Вывод результатов
    logger.info("\nTest Results Summary:")
    for test_name, result in test_results.items():
        status = "PASSED" if result else "FAILED"
        logger.info(f"{test_name.ljust(30)}: {status}")

    save_logs_to_file()

    # Общая статистика
    passed = sum(test_results.values())
    total = len(test_results)
    logger.info(f"\nTotal: {passed}/{total} tests passed ({passed / total * 100:.1f}%)")

    return all(test_results.values())

if __name__ == "__main__":
    logger.info("Starting Animal Tracker API tests")
    success = run_tests()
    if success:
        logger.info("All tests completed successfully")
    else:
        logger.error("Some tests failed")

    logger.info("Testing completed")