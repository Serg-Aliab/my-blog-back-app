package ru.yandex.practicum.controllerExceptionHandler.Exceptions;

public class NoIntegrityInputData extends RuntimeException {
    private String message;

    public NoIntegrityInputData() {}

    public NoIntegrityInputData(String msg) {
        super(msg);
        this.message = msg;
    }
}
