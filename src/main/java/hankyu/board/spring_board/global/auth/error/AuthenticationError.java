package hankyu.board.spring_board.global.auth.error;

import com.fasterxml.jackson.databind.ObjectMapper;
import hankyu.board.spring_board.global.dto.response.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


public class AuthenticationError {

    public static void setErrorResponse(HttpStatus status, int code, String msg, HttpServletResponse response) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        response.setCharacterEncoding("UTF-8");
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(status.value());
        response.getWriter().write(objectMapper.writeValueAsString(
                Response.failure(code, msg)
        ));
    }

}
