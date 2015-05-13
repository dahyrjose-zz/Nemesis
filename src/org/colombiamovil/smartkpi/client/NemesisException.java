package org.colombiamovil.smartkpi.client;

import java.io.Serializable;

public class NemesisException extends Exception implements Serializable {

	private static final long serialVersionUID = 3673153839601617246L;

	public NemesisException() {
	}

	public NemesisException(String message) {
		super(message);
	}
}
