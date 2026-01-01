package unitTests;

import org.junit.Test;
import static org.junit.Assert.*;
import java.sql.Date;
import com.library.model.*;

// Borrowed entity unit test
public class BorrowedTest {

	// Testing class properties
    @Test
    public void testSettersAndGetters() {
        Borrowed borrowed = new Borrowed();

        int expectedId = 1;
        int expectedUserId = 10;
        String expectedIsbn = "9780451524935";
        Date expectedBorrowedDate = Date.valueOf("2025-10-20");
        Date expectedReturnDate = Date.valueOf("2025-10-25");
        String expectedStatus = "Borrowed";

        borrowed.setId(expectedId);
        borrowed.setUserId(expectedUserId);
        borrowed.setIsbn(expectedIsbn);
        borrowed.setBorrowedDate(expectedBorrowedDate);
        borrowed.setReturnDate(expectedReturnDate);
        borrowed.setStatus(expectedStatus);

        assertEquals(expectedId, borrowed.getId());
        assertEquals(expectedUserId, borrowed.getUserId());
        assertEquals(expectedIsbn, borrowed.getIsbn());
        assertEquals(expectedBorrowedDate, borrowed.getBorrowedDate());
        assertEquals(expectedReturnDate, borrowed.getReturnDate());
        assertEquals(expectedStatus, borrowed.getStatus());
    }
}

