package api_rate_limiter.exception.api_error;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ApiError {


    @JsonProperty("status")
    private int httpStatusCode;
    private String message;

    public ApiError(int httpStatusCode, String message) {
        super();
        this.httpStatusCode = httpStatusCode;
        this.message = message;
    }

    public ApiError() {

    }


}
