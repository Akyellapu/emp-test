package com.qentelli.employeetrackingsystem.exception;

public class MissingRequestDateException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	private final String parameterName;

	 public MissingRequestDateException(String parameterName, String message) {
	        super(message);
	        this.parameterName = parameterName;
	    }

	    public String getParameterName() {
	        return this.parameterName;
	    }
}
