package ru.panov.taskmanagementsystem.exception;

/**
 * Исключение, указывающее на то, что запрашиваемый ресурс не найден.
 * Это исключение используется для сигнализации о том, что
 * операция не может быть выполнена, поскольку ресурс, на который
 * осуществляется ссылка, не существует.
 */
public class NotFoundException extends RuntimeException {
    /**
     * Создает новое исключение NotFoundException с заданным сообщением.
     *
     * @param message подробное сообщение, объясняющее причину исключения.
     */
    public NotFoundException(String message) {
        super(message);
    }
}