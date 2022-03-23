package WalleServer.http;

public class HttpResponse<T> {

    private T payload;
    private int status;

    public HttpResponse(T payload, int status) {
        this.payload = payload;
        this.status = status;
    }

    public T getPayload() {
        return payload;
    }

    public void setPayload(T payload) {
        this.payload = payload;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return payload.toString();
    }
}
