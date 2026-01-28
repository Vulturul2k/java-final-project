package app.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RoleTest {

    @Test
    void settersAndGetters_ShouldWorkCorrectly() {
        // Arrange
        Role role = new Role();

        // Act
        role.setId(1);
        role.setName("ADMIN");

        // Assert
        assertEquals(1, role.getId());
        assertEquals("ADMIN", role.getName());
    }

    @Test
    void fluentSetters_ShouldReturnSameInstance() {
        // Arrange
        Role role = new Role();

        // Act
        Role result = role.setId(1).setName("USER");

        // Assert
        assertSame(role, result);
    }
}
