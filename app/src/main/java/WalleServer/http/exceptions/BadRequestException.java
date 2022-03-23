package WalleServer.http.exceptions;

import WalleServer.http.HttpStatus;

public class BadRequestException extends HttpErrorBaseException {


    public BadRequestException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }

}
