package ru.panov.taskmanagementsystem.exception;

/**
 * Исключение, указывающее на конфликт входных данных.
 * Это исключение используется для сигнализации о том, что
 * предоставленные данные конфликтуют с существующими данными или
 * нарушают бизнес-логику.
 */
public class InputDataConflictException extends RuntimeException {
    /**
     * Создает новое исключение InputDataConflictException с заданным сообщением.
     *
     * @param message подробное сообщение, объясняющее причину исключения.
     */
    public InputDataConflictException(String message) {
        super(message);
    }
}