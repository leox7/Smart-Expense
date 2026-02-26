package web;

import dao.ExpenseDAO;
import dao.ExpenseStoredProcedures;
import model.Expense;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet(name = "ViewExpensesServlet", urlPatterns = "/viewExpenses")
public class ViewExpensesServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("username") == null) {
            resp.sendRedirect("login.html");
            return;
        }

        // Get user ID from session
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) userId = 1; // Default fallback

        // Try to load from database first
        ExpenseDAO expenseDAO = new ExpenseDAO();
        ExpenseStoredProcedures storedProcedures = new ExpenseStoredProcedures();
        List<Expense> expenses = expenseDAO.getExpensesByUser(userId);
        
        // If database fails, fallback to session storage
        if (expenses.isEmpty()) {
            @SuppressWarnings("unchecked")
            List<Expense> sessionExpenses = (List<Expense>) session.getAttribute("expenses");
            if (sessionExpenses != null) {
                expenses = sessionExpenses;
            }
        }

        resp.setContentType("text/html");
        PrintWriter out = resp.getWriter();
        out.println("<html><head><title>Your Expenses</title></head><body>");
        out.println("<h2>Your Expenses</h2>");
        out.println("<a href='dashboard'>Back to Dashboard</a><br/><br/>");
        
        // Check for notices
        String notice = req.getParameter("notice");
        if (notice != null) {
            out.println("<p style='color:orange; background:#fff3cd; padding:10px; border-radius:5px;'>");
            out.println("Notice: " + notice.replace("+", " "));
            out.println("</p>");
        }
        
        out.println("<table border='1' cellpadding='6' cellspacing='0' style='border-collapse:collapse; width:100%;'>");
        out.println("<tr style='background:#f8f9fa;'><th>Name</th><th>Amount</th><th>Category</th><th>Date</th></tr>");
        double total = 0.0;
        if (expenses != null && !expenses.isEmpty()) {
            for (Expense exp : expenses) {
                out.println("<tr>" +
                        "<td>" + exp.getName() + "</td>" +
                        "<td>$" + String.format("%.2f", exp.getAmount()) + "</td>" +
                        "<td>" + exp.getCategory() + "</td>" +
                        "<td>" + exp.getDate() + "</td>" +
                        "</tr>");
                total += exp.getAmount();
            }
        } else {
            out.println("<tr><td colspan='4' style='text-align:center; color:#666;'>No expenses found</td></tr>");
        }
        out.println("</table>");
        out.println("<p><strong>Total: $" + String.format("%.2f", total) + "</strong></p>");
        
        // Add stored procedure demo section
        out.println("<hr>");
        out.println("<h3>Database Statistics (Check Server Console)</h3>");
        out.println("<p>The following statistics are printed to the server console:</p>");
        out.println("<ul>");
        out.println("<li>Table structure analysis (ResultSetMetaData)</li>");
        out.println("<li>Expense statistics by category</li>");
        out.println("<li>Stored procedure results</li>");
        out.println("</ul>");
        
        out.println("</body></html>");
        out.flush();
        
        // Print database statistics to console (demonstrates JDBC concepts)
        System.out.println("=== ViewExpensesServlet Database Demo ===");
        expenseDAO.printExpenseStatistics(userId);
        storedProcedures.printExpenseSummary(userId);
        System.out.println("==========================================");
    }
}


