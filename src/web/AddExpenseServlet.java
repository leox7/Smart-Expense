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
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@WebServlet(name = "AddExpenseServlet", urlPatterns = "/addExpense")
public class AddExpenseServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("username") == null) {
            resp.sendRedirect("login.html");
            return;
        }

        String name = req.getParameter("name");
        String amountText = req.getParameter("amount");
        String category = req.getParameter("category");
        String dateText = req.getParameter("date");

        if (name == null || name.isBlank() || amountText == null || amountText.isBlank() || dateText == null || dateText.isBlank()) {
            resp.sendRedirect("addExpense.html?error=Please+fill+all+fields");
            return;
        }

        double amount;
        try {
            amount = Double.parseDouble(amountText);
            if (amount < 0) throw new NumberFormatException("negative");
        } catch (NumberFormatException ex) {
            resp.sendRedirect("addExpense.html?error=Invalid+amount");
            return;
        }

        LocalDate date = LocalDate.parse(dateText);
        Expense expense = new Expense(name, amount, category, date);
        
        // Get user ID from session
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) userId = 1; // Default fallback

        // Try to save to database first
        ExpenseDAO expenseDAO = new ExpenseDAO();
        boolean savedToDatabase = expenseDAO.addExpense(expense, userId);
        
        if (savedToDatabase) {
            System.out.println("Expense saved to database via servlet");
            resp.sendRedirect("viewExpenses");
        } else {
            // Fallback to session storage
            @SuppressWarnings("unchecked")
            List<Expense> expenses = (List<Expense>) session.getAttribute("expenses");
            if (expenses == null) {
                expenses = new ArrayList<>();
                session.setAttribute("expenses", expenses);
            }
            expenses.add(expense);
            resp.sendRedirect("viewExpenses?notice=Saved+to+session+only");
        }
    }
}


