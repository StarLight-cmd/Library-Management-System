package unitTests;

import com.library.model.User;
import com.library.dao.*;
import com.library.util.DBUtil;
import org.junit.*;
import static org.junit.Assert.*;
import static org.hamcrest.Matchers.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

// Unit test for user data access object
public class UserDAOTest {

	// User data access object
    private UserDAO userDAO;

    // Test preparations cleans test environment before test execution
    @Before
    public void setUp() throws Exception {
        userDAO = new UserDAO();
        clearUsersTable(); 
    }

    // Test clean up cleans test environment after test execution
    @After
    public void tearDown() throws Exception {
        clearUsersTable(); 
    }

    // Method to delete data used in testing the user data access object
    private void clearUsersTable() {
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement("DELETE FROM Users WHERE email LIKE 'testuser%'")) {
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Test: Successful LMS user registration
    @Test
    public void testRegisterUser_Success() {
        User user = new User();
        user.setFullname("Mario");
        user.setEmail("testuser@gmail.com");
        user.setPassword("ekidnvwkdnfj");
        user.setAdmin(false);

        boolean result = userDAO.register(user);

        assertThat("User registration should succeed", result, is(true));

        User retrieved = userDAO.login("testuser@gmail.com", "ekidnvwkdnfj");
        assertThat(retrieved, is(notNullValue()));
        assertThat(retrieved.getEmail(), is("testuser@gmail.com"));
        assertThat(retrieved.getFullname(), is("Mario"));
    }

    // Test: Successful LMS Login
    @Test
    public void testLogin_ValidCredentials_ReturnsUser() {
     
        User user = new User();
        user.setFullname("Luigi");
        user.setEmail("testuser_Luigi@gmail.com");
        user.setPassword("123");
        user.setAdmin(false);
        userDAO.register(user);

        User loggedIn = userDAO.login("testuser_Luigi@gmail.com", "123");

        assertThat(loggedIn, is(notNullValue()));
        assertThat(loggedIn.getFullname(), is("Luigi"));
    }

    // Test: Invalid login incorrect password
    @Test
    public void testLogin_InvalidPassword_ReturnsNull() {
        User user = new User();
        user.setFullname("Bowser");
        user.setEmail("testuser_Bowser@gmail.com");
        user.setPassword("456");
        user.setAdmin(false);
        userDAO.register(user);

        User result = userDAO.login("testuser_Bowser@gmail.com", "4569");
        assertThat(result, is(nullValue()));
    }

    // Test: Successful password reset
    @Test
    public void testResetPassword_Success() {
        User user = new User();
        user.setFullname("Todd");
        user.setEmail("testuser_Todd@gmail.com");
        user.setPassword("123");
        user.setAdmin(false);
        userDAO.register(user);

        boolean updated = userDAO.resetPassword("testuser_Todd@gmail.com", "8910");
        assertThat(updated, is(true));

        User loggedIn = userDAO.login("testuser_Todd@gmail.com", "8910");
        assertThat(loggedIn, is(notNullValue()));
    }

    // Test: Getting all users in LMS
    @Test
    public void testGetAllUsers_ReturnsList() {
        User user1 = new User();
        user1.setFullname("Donkey Kong");
        user1.setEmail("testuser_Donkey_Kong@gmail.com");
        user1.setPassword("banana");
        userDAO.register(user1);

        User user2 = new User();
        user2.setFullname("Penguin");
        user2.setEmail("testuser_Penguin@gmail.com");
        user2.setPassword("Snowflake");
        userDAO.register(user2);

        List<User> users = userDAO.getAllUsers();

        assertThat(users, is(not(empty())));
        assertThat(users.size(), greaterThanOrEqualTo(2));
    }

    // Test: Getting specific user by ID
    @Test
    public void testGetUserById_ReturnsCorrectUser() {
        User user = new User();
        user.setFullname("Waldo");
        user.setEmail("testuser_Waldo@gmail.com");
        user.setPassword("people");
        userDAO.register(user);

        User loggedIn = userDAO.login("testuser_Waldo@gmail.com", "people");
        assertThat(loggedIn, is(notNullValue()));

        User retrieved = userDAO.getUserById(loggedIn.getId());
        assertThat(retrieved, is(notNullValue()));
        assertThat(retrieved.getEmail(), is("testuser_Waldo@gmail.com"));
    }
}

