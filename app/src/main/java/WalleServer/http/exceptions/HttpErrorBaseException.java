package WalleServer.http.exceptions;

class HttpErrorBaseException extends RuntimeException{

    private String message;
    private int status;

    public HttpErrorBaseException(String message, int status) {
        super(message);
        this.message = message;
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "{" +
                "\"message\":" + "\"" + message + "\"" +
                "}";
    }
}
