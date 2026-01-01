package com.library.servlet;

import org.junit.Before;
import org.junit.Test;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import static org.mockito.Mockito.*;

// Junit test for logout servlet
public class LogoutServletTest {

	// Initializing unit test variables
    private LogoutServlet servlet;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private HttpSession session;

    //  This subclass is used to expose the post/get method of the servlet in the
    // lms so it can be used by the unit tests
    private static class TestableLogoutServlet extends LogoutServlet {
        @Override
        public void doGet(HttpServletRequest req, HttpServletResponse resp) {
            try {
                super.doGet(req, resp);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    // Initializing unit testing resources
    @Before
    public void setUp() {
        servlet = new TestableLogoutServlet();
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        session = mock(HttpSession.class);
    }

    // Test: Logging a user out from an active session
    @Test
    public void testDoGet_WithActiveSessionLogout() throws Exception {
       
        when(request.getSession(false)).thenReturn(session);
        when(request.getContextPath()).thenReturn("/library");

      
        servlet.doGet(request, response);

      
        verify(session).invalidate(); 
        verify(response).sendRedirect("/library/jsp/login.jsp");
    }

    // Test: Invalid No session
    @Test
    public void testDoGet_NoSession_Redirect() throws Exception {
        
        when(request.getSession(false)).thenReturn(null);
        when(request.getContextPath()).thenReturn("/library");

       
        servlet.doGet(request, response);

       
        verify(response).sendRedirect("/library/jsp/login.jsp");
        verify(session, never()).invalidate(); 
    }
}
