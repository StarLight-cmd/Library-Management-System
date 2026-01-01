package com.library.servlet;

import com.library.dao.UserDAO;
import com.library.model.User;
import org.junit.Before;
import org.junit.Test;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;

// unit test for user servlet
public class UserServletTest {

	// Declaring resource variables
    private UserServlet servlet;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private RequestDispatcher dispatcher;
    private UserDAO userDAO;

    //  This subclass is used to expose the post/get method of the servlet in the
    // lms so it can be used by the unit tests
    private static class TestableUserServlet extends UserServlet {
        @Override
        public void doGet(HttpServletRequest req, HttpServletResponse resp) {
            try {
                super.doGet(req, resp);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    // unit test resource setup and mocking
    @Before
    public void setUp() throws Exception {
        servlet = new TestableUserServlet();
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        dispatcher = mock(RequestDispatcher.class);
        userDAO = mock(UserDAO.class);

        // Replacing userDAO with mock DAO
        java.lang.reflect.Field daoField = UserServlet.class.getDeclaredField("userDAO");
        daoField.setAccessible(true);
        daoField.set(servlet, userDAO);
    }

    // Test: Admin successfully viewing the details of a specific record
    @Test
    public void testDoGet_UserDetails() throws Exception {
        when(request.getParameter("action")).thenReturn("details");
        when(request.getParameter("id")).thenReturn("5");

        User user = new User();
        user.setId(5);
        user.setFullname("Mach");
        user.setEmail("Mach@gmail.com");

        when(userDAO.getUserById(5)).thenReturn(user);
        when(request.getRequestDispatcher("/jsp/users/userDetails.jsp")).thenReturn(dispatcher);

        servlet.doGet(request, response);

        verify(userDAO).getUserById(5);
        verify(request).setAttribute("user", user);
        verify(dispatcher).forward(request, response);
    }

    // Test: Display all users in system to admin : Successful
    @Test
    public void testDoGet_ListUsers() throws Exception {
        when(request.getParameter("action")).thenReturn(null); 

        List<User> mockUsers = Arrays.asList(
                new User(), new User()
        );
        
        when(userDAO.getAllUsers()).thenReturn(mockUsers);
        when(request.getRequestDispatcher("/jsp/users/listUsers.jsp")).thenReturn(dispatcher);

        servlet.doGet(request, response);

        verify(userDAO).getAllUsers();
        verify(request).setAttribute("users", mockUsers);
        verify(dispatcher).forward(request, response);
    }
}
