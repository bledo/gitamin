/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bledo.mvc;

/**
 *
 * @author ricardo
 */
public class HttpError404 extends HttpError {

	private static final long serialVersionUID = 2057119573981568193L;

	public HttpError404(Throwable cause) {
		super(cause);
	}

	public HttpError404(String message, Throwable cause) {
		super(message, cause);
	}

	public HttpError404(String message) {
		super(message);
	}

	public HttpError404() {
		super();
	}

}
