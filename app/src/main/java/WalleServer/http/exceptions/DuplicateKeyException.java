package WalleServer.http.exceptions;

public class DuplicateKeyException extends HttpErrorBaseException {

    public DuplicateKeyException(String message, int status) {
        super(message, status);
    }

}
