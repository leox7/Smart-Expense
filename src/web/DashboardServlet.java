package web;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet(name = "DashboardServlet", urlPatterns = "/dashboard")
public class DashboardServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        String username = session != null ? (String) session.getAttribute("username") : null;
        if (username == null) {
            resp.sendRedirect("login.html");
            return;
        }

        resp.setContentType("text/html");
        PrintWriter out = resp.getWriter();
        out.println("<html><body>");
        out.println("<h2>Welcome, " + username + "</h2>");
        out.println("<p>This is your SmartExpense dashboard.</p>");
        out.println("<ul>");
        out.println("<li><a href='addExpense.html'>Add Expense</a></li>") ;
        out.println("<li><a href='viewExpenses'>View Expenses</a></li>");
        out.println("<li><a href='logout'>Logout</a></li>");
        out.println("</ul>");
        out.println("</body></html>");
        out.flush();
    }
}


