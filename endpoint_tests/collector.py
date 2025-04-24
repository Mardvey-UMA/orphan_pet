import os


def find_and_copy_kt_files(root_dir, output_file):
    """
    Находит все .kt файлы в директории и поддиректориях,
    копирует их содержимое в output_file с форматированием.
    """
    with open(output_file, 'w', encoding='utf-8') as out_f:
        for root, dirs, files in os.walk(root_dir):
            for file in files:
                if file.endswith('.kt'):
                    file_path = os.path.join(root, file)
                    try:
                        with open(file_path, 'r', encoding='utf-8') as in_f:
                            content = in_f.read()

                        # Записываем имя файла как заголовок
                        relative_path = os.path.relpath(file_path, root_dir)
                        out_f.write(f"=== Файл: {relative_path} ===\n")

                        # Оборачиваем содержимое в блок кода Kotlin
                        out_f.write("```kotlin\n")
                        out_f.write(content)
                        if not content.endswith('\n'):
                            out_f.write('\n')  # Добавляем перевод строки, если его нет
                        out_f.write("```\n\n")
                    except Exception as e:
                        print(f"Ошибка при обработке файла {file_path}: {str(e)}")

if __name__ == "__main__":
    project_directory = "/home/user/Projects/pets_calendar/backend/user-service/src/main/kotlin/ru/animaltracker/userservice"
    #project_directory = "/home/user/Projects/pets_calendar/backend/user-service/src/main/kotlin/ru/animaltracker/userservice/service/impl"
    output_filename = "all_kt_files_usr.txt"

    if os.path.isdir(project_directory):
        find_and_copy_kt_files(project_directory, output_filename)
        print(f"Все .kt файлы были успешно скопированы в {output_filename}")
    else:
        print("Указанная директория не существует.")