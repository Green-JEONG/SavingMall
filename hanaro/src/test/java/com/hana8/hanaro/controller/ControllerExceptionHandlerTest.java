package com.hana8.hanaro.controller;

import static org.assertj.core.api.Assertions.assertThat;

import com.hana8.hanaro.security.exception.CustomJwtException;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.ConstraintViolationException;
import java.lang.reflect.Method;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.NoHandlerFoundException;

class ControllerExceptionHandlerTest {

    private final ControllerExceptionHandler handler = new ControllerExceptionHandler();
    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void illegalArgumentReturnsBadRequestMessage() {
        String body = handler.handleIllegalExceptionHandler(new IllegalArgumentException("잘못된 요청입니다.")).getBody();

        assertThat(body).isEqualTo("Warn: 잘못된 요청입니다.");
    }

    @Test
    void methodArgumentNotValidReturnsFieldErrorMap() throws Exception {
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(new SampleForm(""), "sampleForm");
        bindingResult.addError(new FieldError("sampleForm", "name", "이름은 필수입니다."));

        Method method = SampleController.class.getDeclaredMethod("sample", String.class);
        MethodParameter methodParameter = new MethodParameter(method, 0);
        MethodArgumentNotValidException exception = new MethodArgumentNotValidException(methodParameter, bindingResult);

        assertThat(handler.handleNotValidExceptionHandler(exception).getBody())
                .containsEntry("name", "이름은 필수입니다.");
    }

    @Test
    void constraintViolationReturnsViolationMap() {
        ConstraintViolationException exception = new ConstraintViolationException(
                validator.validate(new SampleForm(""))
        );

        assertThat(handler.handleViolationExceptionHandler(exception).getBody())
                .containsKey("name");
    }

    @Test
    void authorizationDeniedReturnsForbiddenMap() {
        assertThat(handler.handleAccessDeniedException(new org.springframework.security.authorization.AuthorizationDeniedException("Access Denied")).getBody())
                .containsEntry("error", "Access Denied");
    }

    @Test
    void customJwtExceptionReturnsUnauthorizedMap() {
        assertThat(handler.handleJwtException(new CustomJwtException("만료된 토큰입니다.")).getStatusCode())
                .isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void responseStatusExceptionPreservesNotFoundStatus() {
        assertThat(handler.handleResponseStatusException(
                new ResponseStatusException(HttpStatus.NOT_FOUND, "상품을 찾을 수 없습니다.")
        ).getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void noHandlerFoundReturnsNotFound() throws Exception {
        NoHandlerFoundException exception = new NoHandlerFoundException("GET", "/missing", new HttpHeaders());

        assertThat(handler.handleNotFoundException(exception).getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void maxUploadSizeExceededReturnsPayloadTooLarge() {
        assertThat(handler.handleMaxUploadSizeExceededException(new MaxUploadSizeExceededException(10)).getStatusCode())
                .isEqualTo(HttpStatus.PAYLOAD_TOO_LARGE);
    }

    @Test
    void unhandledExceptionReturnsInternalServerError() {
        assertThat(handler.handleOthersExceptionHandler(new RuntimeException("boom")).getStatusCode())
                .isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private record SampleForm(@NotBlank String name) {
    }

    private static class SampleController {
        @SuppressWarnings("unused")
        void sample(String name) {
        }
    }
}
