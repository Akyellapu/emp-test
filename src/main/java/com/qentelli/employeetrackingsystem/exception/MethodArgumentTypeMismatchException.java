package com.qentelli.employeetrackingsystem.exception;

public class MethodArgumentTypeMismatchException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	private final String name;
	private final Object value;
	public MethodArgumentTypeMismatchException(String name, Object value, String message) {
        super(message);
        this.name = name;
        this.value = value;
    }

	public String getName() {
		return name;
	}

	public Object getValue() {
		return value;
	}
}
