package unitTests;

import com.library.model.User;
import org.junit.Test;
import static org.junit.Assert.*;

// Unit tests for user entity
public class UserTest {

	// Test: User entity properties
    @Test
    public void testSettersAndGetters() {
        User user = new User();

        user.setId(1);
        user.setFullname("Sashen Sanker");
        user.setEmail("sash@gmail.com");
        user.setPassword("123334");
        user.setAdmin(true);

        assertEquals(1, user.getId());
        assertEquals("Sashen Sanker", user.getFullname());
        assertEquals("sash@gmail.com", user.getEmail());
        assertEquals("123334", user.getPassword());
        assertTrue(user.isAdmin());
    }

    // Test: Default values 
    @Test
    public void testDefaultValues() {
        User user = new User();

        assertEquals(0, user.getId());
        assertNull(user.getFullname());
        assertNull(user.getEmail());
        assertNull(user.getPassword());
        assertFalse(user.isAdmin());
    }

    // Testing is_admin attribute
    @Test
    public void testAdminFlag() {
        User user = new User();

        user.setAdmin(false);
        assertFalse(user.isAdmin());

        user.setAdmin(true);
        assertTrue(user.isAdmin());
    }
}
