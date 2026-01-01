package unitTests;

import com.library.dao.BorrowedDAO;
import com.library.model.Borrowed;
import org.junit.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import java.util.List;

// unit test for borrowed data access object
public class BorrowedDAOTest {

	// Declaration of data access object
    private static BorrowedDAO borrowedDAO;

    // Intializing data access object
    @BeforeClass
    public static void setup() {
        borrowedDAO = new BorrowedDAO();
    }

    // Test: Successfully borrowing a book
    @Test
    public void testBorrowBook_Success() {
        boolean result = borrowedDAO.borrowBook(5, "9780140449181");
        assertThat("Book should be borrowed successfully", result, is(true));
    }

    // Test: Getting all the books borrowed by a user
    @Test
    public void testGetBorrowedBooksByUser() {
        List<Borrowed> books = borrowedDAO.getBorrowedBooksByUser(5);
        assertThat("List should not be null", books, is(notNullValue()));
        assertThat("List should contain at least 1 book", books.size(), greaterThan(0));
    }

    // Test: Successfully returning a book
    @Test
    public void testReturnBook_Success() {
        boolean result = borrowedDAO.returnBook(14);
        assertThat("Book should be successfully returned", result, is(true));
    }
}

