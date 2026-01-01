package com.library.servlet;

import com.library.dao.UserDAO;
import com.library.model.User;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.*;

import static org.mockito.Mockito.*;

public class AuthServletTest {

	// unit test for authentication servlet in LMS
	// Initializing resources
    private AuthServlet servlet;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private HttpSession session;
    private RequestDispatcher dispatcher;
    private UserDAO userDAO;

    //  This subclass is used to expose the post/get method of the servlet in the
    // lms so it can be used by the unit tests
    private static class TestableAuthServlet extends AuthServlet {
        @Override
        public void doPost(HttpServletRequest req, HttpServletResponse resp) {
            try {
                super.doPost(req, resp);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    // unit test and mock component set up
    @Before
    public void setUp() {
        servlet = new TestableAuthServlet();
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        session = mock(HttpSession.class);
        dispatcher = mock(RequestDispatcher.class);
        userDAO = mock(UserDAO.class);

        // Replacing real DAO would a mock DAO
        try {
            java.lang.reflect.Field daoField = AuthServlet.class.getDeclaredField("userDAO");
            daoField.setAccessible(true);
            daoField.set(servlet, userDAO);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // Test: successful login to LMS as an admin
    @Test
    public void testLMSLoginSuccessAsAdmin() throws Exception {
        when(request.getParameter("action")).thenReturn("login");
        when(request.getParameter("email")).thenReturn("Soltehu@gmail.com");
        when(request.getParameter("password")).thenReturn("Richfield01@$%");
        when(request.getSession()).thenReturn(session);

        User mockUser = new User();
        mockUser.setAdmin(true);
        when(userDAO.login("Soltehu@gmail.com", "Richfield01@$%")).thenReturn(mockUser);

        servlet.doPost(request, response);

        verify(response).sendRedirect("jsp/adminDashboard.jsp");
    }

    // Test: unsuccessful LMS login
    @Test
    public void testLoginFailure() throws Exception {
        when(request.getParameter("action")).thenReturn("login");
        when(request.getParameter("email")).thenReturn("sash@gmail.com");
        when(request.getParameter("password")).thenReturn("Richfield01@$%");
        when(request.getRequestDispatcher("/jsp/login.jsp")).thenReturn(dispatcher);

        when(userDAO.login("sash@gmail.com", "Richfield01@$%")).thenReturn(null);

        servlet.doPost(request, response);

        verify(request).setAttribute("error", "Invalid email or password.");
        verify(dispatcher).forward(request, response);
    }

    // Test: Successful LMS Registration
    @Test
    public void testRegisterSuccess() throws Exception {
        when(request.getParameter("action")).thenReturn("register");
        when(request.getParameter("fullname")).thenReturn("Oscar Wilde");
        when(request.getParameter("email")).thenReturn("Oscar@gmail.com");
        when(request.getParameter("password")).thenReturn("Richfield01@$%");

        when(userDAO.register(any(User.class))).thenReturn(true);

        when(request.getRequestDispatcher("/jsp/login.jsp")).thenReturn(dispatcher);

        servlet.doPost(request, response);

        verify(dispatcher).forward(request, response);
    }



    // Test: Registration Failure
    @Test
    public void testRegisterFailure() throws Exception {
        when(request.getParameter("action")).thenReturn("register");
        when(request.getParameter("fullname")).thenReturn("Emily Bronte");
        when(request.getParameter("email")).thenReturn("Emily@gmail.com");
        when(request.getParameter("password")).thenReturn("Richfield01@$%");
        when(userDAO.register(any(User.class))).thenReturn(false);

        when(request.getRequestDispatcher("/jsp/register.jsp")).thenReturn(dispatcher);

        servlet.doPost(request, response);

        verify(request).setAttribute("error", "Registration failed. Email may already exist.");
        verify(dispatcher).forward(request, response);
    }

    // Test: Redirection to forgot password screen of LMS
    @Test
    public void testForgotPasswordRedirect() throws Exception {
        when(request.getParameter("action")).thenReturn("forgot");

        servlet.doPost(request, response);

        verify(response).sendRedirect(contains("/jsp/forgotPassword.jsp"));
    }
}
