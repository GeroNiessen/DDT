package de.codecentric.ddt.web;

/**
 * DDTException enables to throw RuntimeExceptions, which are able to be distinguished from unintended RuntimeExceptions 
 * @author Gero Niessen
 */
public class DDTException extends RuntimeException {
	private static final long serialVersionUID = -3528137214731921257L;
	public DDTException(String message){
		super(message);
	}
}
