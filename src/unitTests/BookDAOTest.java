package unitTests;

import com.library.dao.BookDAO;
import com.library.model.Book;
import org.junit.*;

import static org.junit.Assert.*;

// Unit test for book data access object
public class BookDAOTest {
	// Testing resources
    private static BookDAO bookDAO;
    private static final String TEST_ISBN = "000676869";

    // Initializing data access object
    @BeforeClass
    public static void beforeAll() {
        bookDAO = new BookDAO();
    }

    // Test set up deletes resources used in previous test
    @Before
    public void setup() {
        bookDAO.deleteBook(TEST_ISBN);
    }

    // Test: Successful addition and retrieval of book
    @Test
    public void testAddAndGetBook() {
        Book book = new Book(TEST_ISBN, "testBook", "Sashen", "unittesting", 2025);
        assertTrue("Add should succeed", bookDAO.addBook(book));

        Book fetched = bookDAO.getBookByIsbn(TEST_ISBN);
        assertNotNull(fetched);
        assertEquals("testBook", fetched.getTitle());
    }

    // Test: Updating book details
    @Test
    public void testUpdateBook() {
        Book book = new Book(TEST_ISBN, "testBook", "Sashen", "unittesting", 2022);
        bookDAO.addBook(book);

        book.setTitle("testUpdatedbooktitle");
        assertTrue(bookDAO.updateBook(book));

        Book fetched = bookDAO.getBookByIsbn(TEST_ISBN);
        assertEquals("testUpdatedbooktitle", fetched.getTitle());
    }

    // Test clean up
    @After
    public void cleanup() {
        bookDAO.deleteBook(TEST_ISBN);
    }
}
