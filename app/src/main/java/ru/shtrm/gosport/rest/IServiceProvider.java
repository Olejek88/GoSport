package ru.shtrm.gosport.rest;

import android.os.Bundle;

public interface IServiceProvider {

	String RESULT = "result";
	String MESSAGE = "message";

	/**
	 * 
	 * @param method
	 * @param extras
	 * @return
	 */
	Bundle RunTask(int method, Bundle extras);
}
