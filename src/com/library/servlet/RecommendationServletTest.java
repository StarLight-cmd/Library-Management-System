package com.library.servlet;

import com.library.dao.BookDAO;
import com.library.model.Book;
import com.library.model.User;
import org.junit.Before;
import org.junit.Test;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;


// Unit test for recommendation servlet
public class RecommendationServletTest {

	// Declaring resource variables
    private RecommendationServlet servlet;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private HttpSession session;
    private RequestDispatcher dispatcher;
    private BookDAO bookDAO;

    //  This subclass is used to expose the post/get method of the servlet in the
    // lms so it can be used by the unit tests
    private static class TestableRecommendationServlet extends RecommendationServlet {
        @Override
        public void doGet(HttpServletRequest req, HttpServletResponse resp) {
            try {
                super.doGet(req, resp);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    // Unit test resource setup and mocking 
    @Before
    public void setUp() throws Exception {
        servlet = new TestableRecommendationServlet();
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        session = mock(HttpSession.class);
        dispatcher = mock(RequestDispatcher.class);
        bookDAO = mock(BookDAO.class);

        // Replacing book DAO with mock book DAO
        java.lang.reflect.Field daoField = RecommendationServlet.class.getDeclaredField("bookDAO");
        daoField.setAccessible(true);
        daoField.set(servlet, bookDAO);
    }

    // Test: Invalid user session redirect
    @Test
    public void testDoGet_UserNotLoggedIn_RedirectsToLogin() throws Exception {
    	when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("user")).thenReturn(null);

        servlet.doGet(request, response);

        verify(response).sendRedirect("jsp/login.jsp");
        verifyNoInteractions(bookDAO);
    }

    // Test: Admin user trying to access patron function redirect
    @Test
    public void testDoGet_AdminUser_RedirectsToLogin() throws Exception {
        when(request.getSession(false)).thenReturn(session);
        User adminUser = new User();
        adminUser.setAdmin(true);
        when(session.getAttribute("user")).thenReturn(adminUser);

        servlet.doGet(request, response);

        verify(response).sendRedirect("jsp/login.jsp");
        verifyNoInteractions(bookDAO);
    }

    // Test: Library patron successful display of LMS recommendation list
    @Test
    public void testDoGet_patron_SeesRecommendations() throws Exception {
        when(request.getSession(false)).thenReturn(session);

        User patron = new User();
        patron.setAdmin(false);
        patron.setId(10);
        when(session.getAttribute("user")).thenReturn(patron);

        List<Book> mockBooks = Arrays.asList(
                new Book("8563748927584", "Inferno", "Dante", "Poetry", 1320),
                new Book("9888273847384", "Odyssey", "Homer", "Epic", 800)
        );
        when(bookDAO.getBookSuggestions(10)).thenReturn(mockBooks);
        when(request.getRequestDispatcher("/jsp/books/recommendations.jsp")).thenReturn(dispatcher);

        servlet.doGet(request, response);

        verify(request).setAttribute("suggestedBooks", mockBooks);
        verify(dispatcher).forward(request, response);
    }
}
