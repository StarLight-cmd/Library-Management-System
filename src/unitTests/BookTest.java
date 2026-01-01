package unitTests;

import com.library.model.*;
import org.junit.Test;
import static org.junit.Assert.*;

// unit test for book entity which maps to book table
public class BookTest {

	// Test: creating new book object
    @Test
    public void testBookConstructorAndGetters() {
        Book book = new Book("12345", "The Odyssey", "Homer", "Epic", 1995);

        assertEquals("12345", book.getIsbn());
        assertEquals("The Odyssey", book.getTitle());
        assertEquals("Homer", book.getAuthor());
        assertEquals("Epic", book.getGenre());
        assertEquals(1995, book.getYear());
    }

    // Test: Setter properties
    @Test
    public void testSetters() {
        Book book = new Book();
        book.setIsbn("98765");
        book.setTitle("Inferno");
        book.setAuthor("Dante Alighieri");
        book.setGenre("Poetry");
        book.setYear(1320);

        assertEquals("98765", book.getIsbn());
        assertEquals("Inferno", book.getTitle());
        assertEquals("Dante Alighieri", book.getAuthor());
        assertEquals("Poetry", book.getGenre());
        assertEquals(1320, book.getYear());
    }
}

