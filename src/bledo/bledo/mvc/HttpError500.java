/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bledo.mvc;

/**
 *
 * @author ricardo
 */
public class HttpError500 extends HttpError {

	private static final long serialVersionUID = -329530235057396792L;

	public HttpError500(Throwable cause) {
		super(cause);
	}

	public HttpError500(String message, Throwable cause) {
		super(message, cause);
	}

	public HttpError500(String message) {
		super(message);
	}

	public HttpError500() {
		super();
	}
	
}
