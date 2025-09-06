package reportEngine.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApiResponse<T>  {

    @JsonProperty("status")
    private String status; // "success" or "error"
    @JsonProperty("message")
    private String message;
    @JsonProperty("error")
    private String error;
    @JsonProperty("data")
    private T data;
    @JsonProperty("token")
    private String token;
    @JsonProperty("refreshToken")
    private String refreshToken;

    public static <T> ApiResponse<T> success(String message) {
        return ApiResponse.<T>builder()
                .status("success")
                .message(message)
                .build();
    }
    
    public static <T> ApiResponse<T> success(String message, T data) {
        return ApiResponse.<T>builder()
                .status("success")
                .message(message)
                .data(data)
                .build();
    }

	public static <T> ApiResponse<T> success(T data) {
        return success("Operation successful", data);
    }

    public static <T> ApiResponse<T> error(String message) {
        return ApiResponse.<T>builder()
                .status("error")
                .message(message)
                .build();
    }

    public static <T> ApiResponse<T> error(String message, T data) {
        return ApiResponse.<T>builder()
                .status("error")
                .message(message)
                .data(data)
                .build();
    }

    public static <T> ApiResponse<T> error(String status, String message, String errorDetails) {
        return ApiResponse.<T>builder()
                .status(status)
                .message(message)
                .error(errorDetails)
                .build();
    }

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

}
