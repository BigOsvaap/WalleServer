package WalleServer.http.exceptions;

import WalleServer.http.HttpStatus;

public class NotFoundException extends HttpErrorBaseException {

    public NotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND);
    }

}
