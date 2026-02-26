package web;

import dao.UserDAO;
import model.User;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet(name = "LoginServlet", urlPatterns = "/login")
public class LoginServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.sendRedirect("login.html");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String username = req.getParameter("username");
        String password = req.getParameter("password");

        // Try database authentication first
        UserDAO userDAO = new UserDAO();
        User user = userDAO.authenticateUser(username, password);
        
        if (user != null) {
            HttpSession session = req.getSession(true);
            session.setAttribute("username", username);
            session.setAttribute("userId", user.getUserId());
            session.setAttribute("user", user);
            resp.sendRedirect("dashboard");
        } else {
            // Fallback to hardcoded credentials for demo
            if ("admin".equals(username) && "password".equals(password)) {
                HttpSession session = req.getSession(true);
                session.setAttribute("username", username);
                session.setAttribute("userId", 1); // Default user ID
                resp.sendRedirect("dashboard");
            } else {
                resp.setContentType("text/html");
                PrintWriter out = resp.getWriter();
                out.println("<html><body>");
                out.println("<p style='color:red'>Invalid Login</p>");
                out.println("<a href='login.html'>Back to Login</a>");
                out.println("</body></html>");
                out.flush();
            }
        }
    }
}


