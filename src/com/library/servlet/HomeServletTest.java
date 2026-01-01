package com.library.servlet;

import org.junit.Before;
import org.junit.Test;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.mockito.Mockito.*;

public class HomeServletTest {
	// JUNIT test for home servlet
	// Declaring resource variables
    private HomeServlet servlet;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private RequestDispatcher dispatcher;

    //  This subclass is used to expose the post/get method of the servlet in the
    // lms so it can be used by the unit tests
    private static class TestableHomeServlet extends HomeServlet {
        @Override
        public void doGet(HttpServletRequest req, HttpServletResponse resp) {
            try {
                super.doGet(req, resp);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    // Initializing unit test components 
    @Before
    public void setUp() {
        servlet = new TestableHomeServlet();
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        dispatcher = mock(RequestDispatcher.class);

        when(request.getRequestDispatcher("/jsp/login.jsp")).thenReturn(dispatcher);
    }

    // Test: Successful Navigation to Login Page
    @Test
    public void testDoGet_ForwardsToLoginPage() throws Exception {
        servlet.doGet(request, response);

        verify(request).getRequestDispatcher("/jsp/login.jsp");
        verify(dispatcher).forward(request, response); 
    }
}
