/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bledo.mvc;

/**
 *
 * @author ricardo
 */
public class HttpError extends Exception
{
	private static final long serialVersionUID = -6810179647504354638L;

	public HttpError() {
		super();
	}

	public HttpError(String message, Throwable cause) {
		super(message, cause);
	}

	public HttpError(String message) {
		super(message);
	}

	public HttpError(Throwable cause) {
		super(cause);
	}
	
}
