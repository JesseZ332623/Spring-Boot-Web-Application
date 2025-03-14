package response;

import org.springframework.http.HttpStatus;

public class Response <DataType>
{
    private DataType data;
    private boolean  ifSuccess;
    private String   infoMessage;
    private String   errorMessage;

    HttpStatus       status;

    public static <DT> Response<DT> newSuccess(DT data)
    {
        Response<DT> response = new Response<>();

        response.setData(data);
        response.setIfSuccess(true);
        response.setStatus(HttpStatus.OK);

        return response;
    }

    public static <DT> Response<DT> newSuccess(DT data, String infoMessage)
    {
        Response<DT> response = new Response<>();

        response.setData(data);
        response.setInfoMessage(infoMessage);
        response.setIfSuccess(true);
        response.setStatus(HttpStatus.OK);

        return response;
    }

    public static <DT> Response<DT> newFailed(String errorMessage, HttpStatus status)
    {
        Response<DT> response = new Response<>();

        response.setIfSuccess(false);
        response.setErrorMessage(errorMessage);
        response.setStatus(status);

        return response;
    }

    public DataType getData() {
        return data;
    }

    public void setData(DataType data) {
        this.data = data;
    }

    public HttpStatus getStatus() { return this.status; }

    public void setStatus(HttpStatus status) { this.status = status; }

    public boolean isIfSuccess() {
        return ifSuccess;
    }

    public void setIfSuccess(boolean ifSuccess) {
        this.ifSuccess = ifSuccess;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getInfoMessage() {
        return infoMessage;
    }

    public void setInfoMessage(String infoMessage) {
        this.infoMessage = infoMessage;
    }
}
