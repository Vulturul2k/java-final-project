package app.exception;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import static org.junit.jupiter.api.Assertions.*;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void handleBadRequest_ShouldReturnBadRequestStatus() {
        // Arrange
        BadRequestException exception = new BadRequestException("Test bad request");
        WebRequest request = new ServletWebRequest(new MockHttpServletRequest());

        // Act
        ResponseEntity<ErrorObject> response = handler.handleBadRequest(exception, request);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(400, response.getBody().getStatusCode());
        assertEquals("Test bad request", response.getBody().getMessage());
        assertNotNull(response.getBody().getTimestamp());
    }
}
