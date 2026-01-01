package com.library.servlet;

import com.library.dao.BookDAO;
import com.library.model.Book;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

import static org.mockito.Mockito.*;

// Unit tests for book servlet
public class BookServletTest {

    private BookServlet servlet;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private RequestDispatcher dispatcher;
    private BookDAO mockBookDAO;

    //  This subclass is used to expose the post/get method of the servlet in the
    // lms so it can be used by the unit tests
    private static class TestableBookServlet extends BookServlet {
        @Override
        public void doGet(HttpServletRequest req, HttpServletResponse resp) {
            try { 
            	super.doGet(req, resp); 
            	} catch (Exception e) {
            		throw new RuntimeException(e); 
            	}
        }
        
        @Override
        public void doPost(HttpServletRequest req, HttpServletResponse resp) {
            try { 
            	super.doPost(req, resp); 
            	} catch (Exception e) { 
            		throw new RuntimeException(e); 
            	}
        }
    }

    // Setting up unit tests and mock resources
    @Before
    public void setUp() throws Exception {
        servlet = new TestableBookServlet();
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        dispatcher = mock(RequestDispatcher.class);
        mockBookDAO = mock(BookDAO.class);

        // implementing the created mockBookDAO in place of the LMS's real LMS.
        java.lang.reflect.Field daoField = BookServlet.class.getDeclaredField("bookDAO");
        daoField.setAccessible(true);
        daoField.set(servlet, mockBookDAO);
    }

    // Test: Get method for add jsp
    @Test
    public void testDoGetAdd() {
        when(request.getParameter("action")).thenReturn("add");
        when(request.getRequestDispatcher("/jsp/books/addBook.jsp")).thenReturn(dispatcher);

        try {
			servlet.doGet(request, response);
		} catch (ServletException e) {
		
			e.printStackTrace();
		} catch (IOException e) {
		
			e.printStackTrace();
		}

        try {
			verify(dispatcher).forward(request, response);
		} catch (ServletException e) {
			
			e.printStackTrace();
		} catch (IOException e) {
		
			e.printStackTrace();
		}
    }

    // Test Get method edit jsp
    @Test
    public void testDoGetEdit() {
        when(request.getParameter("action")).thenReturn("edit");
        when(request.getParameter("isbn")).thenReturn("6758468758757");
        when(mockBookDAO.getBookByIsbn("6758468758757")).thenReturn(new Book());
        when(request.getRequestDispatcher("/jsp/books/editBook.jsp")).thenReturn(dispatcher);

        try {
			servlet.doGet(request, response);
		} catch (ServletException e) {
		
			e.printStackTrace();
		} catch (IOException e) {
		
			e.printStackTrace();
		}

        verify(request).setAttribute(eq("book"), any(Book.class));
        try {
			verify(dispatcher).forward(request, response);
		} catch (ServletException e) {
			
			e.printStackTrace();
		} catch (IOException e) {
		
			e.printStackTrace();
		}
    }

    // Test: Delete method
    @Test
    public void testDoGetDelete() throws Exception {
        when(request.getParameter("action")).thenReturn("delete");
        when(request.getParameter("isbn")).thenReturn("9795796896325");
        when(request.getContextPath()).thenReturn("/library");

        servlet.doGet(request, response);

        verify(mockBookDAO).deleteBook("9795796896325");
        verify(response).sendRedirect("/library/books");
    }

    // Test: Successful LMS Search Function
    @Test
    public void testDoGetSearchWithKeyword() {
        when(request.getParameter("action")).thenReturn("search");
        when(request.getParameter("keyword")).thenReturn("Wuthering Heights");
        when(mockBookDAO.searchBooks("Wuthering Heights")).thenReturn(Collections.singletonList(new Book()));
        when(request.getRequestDispatcher("/jsp/books/listBooks.jsp")).thenReturn(dispatcher);

        try {
			servlet.doGet(request, response);
		} catch (ServletException e) {
		
			e.printStackTrace();
		} catch (IOException e) {
		
			e.printStackTrace();
		}

        verify(request).setAttribute(eq("books"), anyList());
        try {
			verify(dispatcher).forward(request, response);
		} catch (ServletException e) {
		
			e.printStackTrace();
		} catch (IOException e) {
		
			e.printStackTrace();
		}
    }

    // Testing LMS Search Functionality with an empty search box
    @Test
    public void testDoGetSearchWithoutKeyword() throws Exception {
        when(request.getParameter("action")).thenReturn("search");
        when(request.getParameter("keyword")).thenReturn("   ");
        when(request.getContextPath()).thenReturn("/library");

        servlet.doGet(request, response);

        verify(response).sendRedirect("/library/books");
    }

    // Testing default list results of book catalogue
    @Test
    public void testDoGetDefaultList() {
        when(request.getParameter("action")).thenReturn(null);
        when(mockBookDAO.getAllBooks()).thenReturn(Collections.singletonList(new Book()));
        when(request.getRequestDispatcher("/jsp/books/listBooks.jsp")).thenReturn(dispatcher);

        try {
			servlet.doGet(request, response);
		} catch (ServletException e) {
	
			e.printStackTrace();
		} catch (IOException e) {
		
			e.printStackTrace();
		}

        verify(request).setAttribute(eq("books"), anyList());
        try {
			verify(dispatcher).forward(request, response);
		} catch (ServletException e) {
	
			e.printStackTrace();
		} catch (IOException e) {
	
			e.printStackTrace();
		}
    }

    // Test: Adding book to book catalogue sucessfully
    @Test
    public void testDoPostAddBookSuccess() throws Exception {
        when(request.getParameter("action")).thenReturn("add");
        when(request.getParameter("isbn")).thenReturn("7362837492837");
        when(request.getParameter("title")).thenReturn("The Prince");
        when(request.getParameter("author")).thenReturn("Machevali");
        when(request.getParameter("genre")).thenReturn("Classic");
        when(request.getParameter("year")).thenReturn("1300");
        when(request.getContextPath()).thenReturn("/library");
        when(mockBookDAO.addBook(any(Book.class))).thenReturn(true);

        servlet.doPost(request, response);

        verify(response).sendRedirect("/library/books");
    }

    // Test: Failure when adding book to LMS
    @Test
    public void testDoPostAddBookFail() throws Exception {
        when(request.getParameter("action")).thenReturn("add");
        when(request.getParameter("isbn")).thenReturn("983746372637");
        when(request.getParameter("title")).thenReturn("Pat the Pig");
        when(request.getParameter("author")).thenReturn("Sashen Sanker");
        when(request.getParameter("genre")).thenReturn("Kids Classic");
        when(request.getParameter("year")).thenReturn("2000");
        when(mockBookDAO.addBook(any(Book.class))).thenReturn(false);
        PrintWriter writer = mock(PrintWriter.class);
        when(response.getWriter()).thenReturn(writer);

        servlet.doPost(request, response);

        verify(writer).println("Error adding book!");
    }

    // Test: Successful book update 
    @Test
    public void testDoPostUpdateSuccess() throws Exception {
        when(request.getParameter("action")).thenReturn("update");
        when(request.getParameter("isbn")).thenReturn("4635273627362");
        when(request.getParameter("title")).thenReturn("The Self fish Giant");
        when(request.getParameter("author")).thenReturn("Oscar Wilde");
        when(request.getParameter("genre")).thenReturn("Classic");
        when(request.getParameter("year")).thenReturn("1760");
        when(request.getContextPath()).thenReturn("/library");
        when(mockBookDAO.updateBook(any(Book.class))).thenReturn(true);

        servlet.doPost(request, response);

        verify(response).sendRedirect("/library/books");
    }
}
