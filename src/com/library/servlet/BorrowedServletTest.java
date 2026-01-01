package com.library.servlet;

import com.library.dao.BorrowedDAO;
import com.library.model.Borrowed;
import com.library.model.User;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.*;
import java.util.*;

import static org.mockito.Mockito.*;

// Unit test for borrowed Servlet
public class BorrowedServletTest {

	//Declaring resources
    private BorrowedServlet servlet;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private HttpSession session;
    private RequestDispatcher dispatcher;
    private BorrowedDAO mockBorrowedDAO;

    //  This subclass is used to expose the post/get method of the servlet in the
    // lms so it can be used by the unit tests
    private static class TestableBorrowedServlet extends BorrowedServlet {
        @Override
        public void doGet(HttpServletRequest req, HttpServletResponse resp) {
            try { super.doGet(req, resp); } catch (Exception e) { throw new RuntimeException(e); }
        }

        @Override
        public void doPost(HttpServletRequest req, HttpServletResponse resp) {
            try { super.doPost(req, resp); } catch (Exception e) { throw new RuntimeException(e); }
        }
    }

    // Setting up test resources and mock components
    @Before
    public void setUp() throws Exception {
        servlet = new TestableBorrowedServlet();
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        session = mock(HttpSession.class);
        dispatcher = mock(RequestDispatcher.class);
        mockBorrowedDAO = mock(BorrowedDAO.class);

        // Replacing Real Borrowed DAO with Mock DAO
        java.lang.reflect.Field daoField = BorrowedServlet.class.getDeclaredField("borrowedDAO");
        daoField.setAccessible(true);
        daoField.set(servlet, mockBorrowedDAO);
    }

    // Test: User not login INVALID Session
    @Test
    public void testDoGet_UserNotLoggedIn_RedirectsToLogin() throws Exception {
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("user")).thenReturn(null);

        servlet.doGet(request, response);

        verify(response).sendRedirect("jsp/login.jsp");
    }

    // Test: Invalid user session: Accessing User DashBoard as an Admin  
    @Test
    public void testDoGet_AdminUser_RedirectsToLogin() throws Exception {
        User adminUser = new User();
        adminUser.setAdmin(true);

        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("user")).thenReturn(adminUser);

        servlet.doGet(request, response);

        verify(response).sendRedirect("jsp/login.jsp");
    }

    // Test: Valid user session to borrowed books page
    @Test
    public void testDoGet_NormalUser_ShowsBorrowedBooks() throws Exception {
        User normalUser = new User();
        normalUser.setId(1);
        normalUser.setAdmin(false);

        Borrowed borrowedBook = new Borrowed();
        borrowedBook.setId(1);
        borrowedBook.setIsbn("98374637623897");
        List<Borrowed> borrowedList = Collections.singletonList(borrowedBook);

        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("user")).thenReturn(normalUser);
        when(request.getParameter("action")).thenReturn(null);
        when(mockBorrowedDAO.getBorrowedBooksByUser(1)).thenReturn(borrowedList);
        when(request.getRequestDispatcher("/jsp/books/borrowedBooks.jsp")).thenReturn(dispatcher);

        servlet.doGet(request, response);

        verify(request).setAttribute("borrowedBooks", borrowedList);
        verify(dispatcher).forward(request, response);
    }

    // Test: Successfully returning a borrowed book as a user
    @Test
    public void testDoGet_ReturnAction_CallsReturnBook() throws Exception {
        User patron = new User();
        patron.setId(1);
        patron.setAdmin(false);

        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("user")).thenReturn(patron);
        when(request.getParameter("action")).thenReturn("return");
        when(request.getParameter("id")).thenReturn("5");
        when(mockBorrowedDAO.getBorrowedBooksByUser(1)).thenReturn(Collections.emptyList());
        when(request.getRequestDispatcher("/jsp/books/borrowedBooks.jsp")).thenReturn(dispatcher);

        servlet.doGet(request, response);

        verify(mockBorrowedDAO).returnBook(5);
        verify(dispatcher).forward(request, response);
    }

    // Test: Borrowing a book
    @Test
    public void testDoPost_BorrowBook_Success() throws Exception {
        User patron = new User();
        patron.setId(2);
        patron.setAdmin(false);

        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("user")).thenReturn(patron);
        when(request.getParameter("isbn")).thenReturn("5686479789789");
        when(request.getContextPath()).thenReturn("/library");

        servlet.doPost(request, response);

        verify(mockBorrowedDAO).borrowBook(2, "5686479789789");
        verify(response).sendRedirect("/library/borrowed");
    }
}
