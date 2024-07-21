// Напишите приложение, которое будет запрашивать у пользователя следующие данные в произвольном порядке, разделенные пробелом:
// Фамилия Имя Отчество датарождения номертелефона пол
// Форматы данных:
// фамилия, имя, отчество - строки
// дата_рождения - строка формата dd.mm.yyyy
// номер_телефона - целое беззнаковое число без форматирования
// пол - символ латиницей f или m.
// Приложение должно проверить введенные данные по количеству. Если количество не совпадает с требуемым, вернуть код ошибки, 
// обработать его и показать пользователю сообщение, что он ввел меньше и больше данных, чем требуется.
// Приложение должно попытаться распарсить полученные значения и выделить из них требуемые параметры. Если форматы данных не 
// совпадают, нужно бросить исключение, соответствующее типу проблемы. Можно использовать встроенные типы java и создать свои. 
// Исключение должно быть корректно обработано, пользователю выведено сообщение с информацией, что именно неверно.
// Если всё введено и обработано верно, должен создаться файл с названием, равным фамилии, в него в одну строку должны 
// записаться полученные данные, вида
// <Фамилия><Имя><Отчество><датарождения> <номертелефона><пол>
// Однофамильцы должны записаться в один и тот же файл, в отдельные строки.
// Не забудьте закрыть соединение с файлом.
// При возникновении проблемы с чтением-записью в файл, исключение должно быть корректно обработано, пользователь должен 
// увидеть стектрейс ошибки.


import java.io.*;
import java.util.*;
import java.text.*;

// Основной класс приложения
public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in); // Создаем объект Scanner для чтения ввода пользователя
        System.out.println("Введите ваши данные через пробел (на английском языке).");
        System.out.println("Фамилия Имя Отчество датарождения номертелефона пол(m/f):");
        String input = scanner.nextLine(); // Читаем строку, введенную пользователем

        try {
            UserInput userInput = DataParser.parseInput(input); // Парсим и проверяем введенные данные
            writeToFile(userInput); // Сохраняем данные в файл
            System.out.println("Данные успешно записаны в файл."); // Сообщаем пользователю об успешной записи данных
        } catch (InvalidDataFormatException e) {
            System.err.println("Error: " + e.getMessage()); // Сообщаем пользователю об ошибке формата данных
        } catch (IOException e) {
            System.err.println("File writing error: "); // Сообщаем пользователю об ошибке записи в файл
            e.printStackTrace(); // Выводим стек ошибки
        } finally{
            if (input != null){
                scanner.close();
            }
        }
    }

    // Метод для записи данных пользователя в файл
    public static void writeToFile(UserInput userInput) throws IOException {
        String filename = userInput.getSurname() + ".txt"; // Определяем имя файла по фамилии пользователя
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename, true))) { // Создаем объект BufferedWriter для записи в файл
            writer.write(userInput.toString()); // Записываем данные пользователя в файл
            writer.newLine(); // Добавляем новую строку
        }
    }
}

// Исключение, которое будет выбрасываться при неверном формате данных
class InvalidDataFormatException extends Exception {
    public InvalidDataFormatException(String message) {
        super(message); // Передаем сообщение об ошибке в конструктор суперкласса
    }
}

// Класс для хранения данных пользователя
class UserInput {
    private String surname; // Фамилия
    private String name; // Имя
    private String patronymic; // Отчество
    private String birthDate; // Дата рождения
    private long phoneNumber; // Номер телефона
    private char gender; // Пол

    // Конструктор для инициализации всех полей
    public UserInput(String surname, String name, String patronymic, String birthDate, long phoneNumber, char gender) {
        this.surname = surname;
        this.name = name;
        this.patronymic = patronymic;
        this.birthDate = birthDate;
        this.phoneNumber = phoneNumber;
        this.gender = gender;
    }

    // Метод для форматированного вывода данных пользователя
    @Override
    public String toString() {
        return surname + " " + name + " " + patronymic + " " + birthDate + " " + phoneNumber + " " + gender;
    }

    // Метод для получения фамилии
    public String getSurname() {
        return surname;
    }
}

// Класс для разбора и проверки входных данных
class DataParser {
    public static UserInput parseInput(String input) throws InvalidDataFormatException {
        String[] parts = input.split(" "); // Разделяем входную строку на части по пробелам
        if (parts.length != 6) { // Проверяем, что количество частей равно 6
            throw new InvalidDataFormatException("Invalid number of input data. Expected 6 fields."); // Выбрасываем исключение, если количество частей неверное
        }

        String surname = parts[0]; // Фамилия
        String name = parts[1]; // Имя
        String patronymic = parts[2]; // Отчество
        String birthDate = parts[3]; // Дата рождения
        long phoneNumber;
        char gender;

        // Проверка формата даты рождения
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
            dateFormat.setLenient(false); // Устанавливаем строгую проверку формата даты
            dateFormat.parse(birthDate); // Парсим дату
        } catch (ParseException e) {
            throw new InvalidDataFormatException("Не верный формат даты. Необходимый формат: dd.MM.yyyy"); // Выбрасываем исключение, если формат даты неверный
        }

        // Проверка формата номера телефона
        try {
            phoneNumber = Long.parseLong(parts[4]);
            if (phoneNumber < 0) {
                throw new InvalidDataFormatException("Phone number must be a positive integer."); // Выбрасываем исключение, если номер телефона отрицательный
            }
        } catch (NumberFormatException e) {
            throw new InvalidDataFormatException("Invalid phone number format. Expected a positive integer."); // Выбрасываем исключение, если формат номера телефона неверный
        }

        // Проверка формата пола
        gender = parts[5].charAt(0);
        if (gender != 'm' && gender != 'f') {
            throw new InvalidDataFormatException("Не верно указан пол. Введите 'm' или 'f'."); // Выбрасываем исключение, если формат пола неверный
        }

        // Возвращаем объект UserInput с проверенными данными
        return new UserInput(surname, name, patronymic, birthDate, phoneNumber, gender);
    }
}


