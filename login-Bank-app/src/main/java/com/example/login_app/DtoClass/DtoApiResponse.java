package com.example.login_app.DtoClass;

public class DtoApiResponse {
       private String status;
       private String message;
       private Object data;
       public DtoApiResponse(String status, String message, Object data){
           this.status=status;
           this.message=message;
           this.data=data;
       }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMsg() {
        return message;
    }

    public void setMsg(String msg) {
        this.message = msg;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
