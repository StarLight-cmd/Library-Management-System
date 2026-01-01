package unitTests;

import com.library.util.DBUtil;
import org.junit.Test;
import java.sql.Connection;
import static org.junit.Assert.*;

// Unit test for database connection class
public class DBUtilTest {
	// Test: Successful Database Connection
    @Test
    public void testConnectionNotNull() throws Exception {
        Connection conn = DBUtil.getConnection();
        assertNotNull("Database connection should not be null", conn);
        if (conn != null) {
        	conn.close();
        	}
    }
}

