package com.library.servlet;

import com.library.dao.UserDAO;
import org.junit.Before;
import org.junit.Test;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import static org.mockito.Mockito.*;

// JUnit for ForgotPasswordServlet 
public class ForgotPasswordServletTest {

    private ForgotPasswordServlet servlet;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private RequestDispatcher dispatcher;
    private UserDAO userDAO;
    private HttpSession session;

    private static class TestableForgotPasswordServlet extends ForgotPasswordServlet {
        @Override
        public void doPost(HttpServletRequest req, HttpServletResponse resp) {
            try {
                super.doPost(req, resp);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Before
    public void setUp() throws Exception {
        servlet = new TestableForgotPasswordServlet();
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        dispatcher = mock(RequestDispatcher.class);
        userDAO = mock(UserDAO.class);
        session = mock(HttpSession.class);

        java.lang.reflect.Field daoField = ForgotPasswordServlet.class.getDeclaredField("userDAO");
        daoField.setAccessible(true);
        daoField.set(servlet, userDAO);

        when(request.getRequestDispatcher(anyString())).thenReturn(dispatcher);
        when(request.getSession()).thenReturn(session);
        when(request.getSession(false)).thenReturn(session);
    }

    // Test: Sending OTP
    @Test
    public void testSendOtp_Success() throws Exception {
        when(request.getParameter("action")).thenReturn("sendOtp");
        when(request.getParameter("email")).thenReturn("sash@gmail.com");
        when(request.getRequestDispatcher("/jsp/forgotPassword.jsp")).thenReturn(dispatcher);

        servlet.doPost(request, response);

        verify(request).setAttribute("message", "OTP sent to your email. Please check your inbox.");
        verify(dispatcher).forward(request, response);
    }

    // Test: Reset password success
    @Test
    public void testResetPassword_Success() throws Exception {
        when(request.getParameter("action")).thenReturn("resetPassword");
        when(request.getParameter("email")).thenReturn("sash@gmail.com");
        when(request.getParameter("newPassword")).thenReturn("Richfield01@$%");
        when(request.getParameter("otp")).thenReturn("6768");

        when(session.getAttribute("otp")).thenReturn("6768");
        when(session.getAttribute("email")).thenReturn("sash@gmail.com");

        when(userDAO.resetPassword("sash@gmail.com", "Richfield01@$%")).thenReturn(true);
        when(request.getRequestDispatcher("/jsp/login.jsp")).thenReturn(dispatcher);

        servlet.doPost(request, response);

        verify(request).setAttribute("message", "Password updated successfully! You may now log in.");
        verify(dispatcher).forward(request, response);
    }

    // Test: Reset password failure
    @Test
    public void testResetPassword_Failure() throws Exception {
        when(request.getParameter("action")).thenReturn("resetPassword");
        when(request.getParameter("email")).thenReturn("steve@gmail.com");
        when(request.getParameter("newPassword")).thenReturn("Richfield01@$%");
        when(request.getParameter("otp")).thenReturn("6768");

        when(session.getAttribute("otp")).thenReturn("6768");
        when(session.getAttribute("email")).thenReturn("steve@gmail.com");

        when(userDAO.resetPassword("steve@gmail.com", "Richfield01@$%")).thenReturn(false);
        when(request.getRequestDispatcher("/jsp/forgotPassword.jsp")).thenReturn(dispatcher);

        servlet.doPost(request, response);

        verify(request).setAttribute("error", "Failed to reset password. Please verify your email.");
        verify(dispatcher).forward(request, response);
    }
}
