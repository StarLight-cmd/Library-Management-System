package unitTests;

import com.library.util.PasswordUtil;
import org.junit.Test;
import static org.junit.Assert.*;

// Unit testing for passsword hashing function of LMS
public class PasswordUtilTest {
	// Test: Successful hashing of password
    @Test
    public void testHashAndCheck() {
        String plain = "123";
        String hashed = PasswordUtil.hashPassword(plain);
        assertNotNull(hashed);
        assertTrue(PasswordUtil.checkPassword(plain, hashed));
        assertFalse(PasswordUtil.checkPassword("wrong", hashed));
    }
}

