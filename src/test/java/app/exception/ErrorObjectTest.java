package app.exception;

import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class ErrorObjectTest {

    @Test
    void settersAndGetters_ShouldWorkCorrectly() {
        // Arrange
        ErrorObject errorObject = new ErrorObject();
        LocalDateTime now = LocalDateTime.now();

        // Act
        errorObject.setStatusCode(404)
                .setMessage("Not Found")
                .setTimestamp(now);

        // Assert
        assertEquals(404, errorObject.getStatusCode());
        assertEquals("Not Found", errorObject.getMessage());
        assertEquals(now, errorObject.getTimestamp());
    }

    @Test
    void fluentSetters_ShouldReturnSameInstance() {
        // Arrange
        ErrorObject errorObject = new ErrorObject();

        // Act
        ErrorObject result = errorObject.setStatusCode(500);

        // Assert
        assertSame(errorObject, result);
    }
}
