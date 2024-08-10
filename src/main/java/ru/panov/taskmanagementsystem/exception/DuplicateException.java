package ru.panov.taskmanagementsystem.exception;

/**
 * Исключение, указывающее на наличие дублирующей записи.
 * Это исключение используется для сигнализации о том, что
 * операция не может быть выполнена из-за наличия дубликата.
 */
public class DuplicateException extends RuntimeException {
    /**
     * Создает новое исключение DuplicateException с заданным сообщением.
     *
     * @param message подробное сообщение, объясняющее причину исключения.
     */
    public DuplicateException(String message) {
        super(message);
    }
}