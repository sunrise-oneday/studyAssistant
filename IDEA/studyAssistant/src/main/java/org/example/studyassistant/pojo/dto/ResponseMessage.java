package org.example.studyassistant.pojo.dto;


import lombok.Data;
import org.example.studyassistant.pojo.entity.User;
import org.springframework.http.HttpStatus;
@Data
public class ResponseMessage<T> {
    private  Integer code;
    private String message;
    private T data;

    public ResponseMessage(Integer code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public ResponseMessage(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public static <T>ResponseMessage<T> success(T data){
        return new ResponseMessage(HttpStatus.OK.value(),"success!!",data);
    }

    public static <T> ResponseMessage<T> success() {
        return new ResponseMessage<>(HttpStatus.OK.value(),"success!!",null);
    }


}
