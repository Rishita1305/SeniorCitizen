package com.sc;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class LogInServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final String URL = "jdbc:postgresql://localhost:5432/mydb";
    private static final String USER = "postgres";
    private static final String PASSWORD = "Post1gre2";

    protected void service(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        try {
            // Load database driver
            Class.forName("org.postgresql.Driver");
            // Establish database connection
            Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
            conn.setAutoCommit(false);

            // Set schema to SeniorCitizen
            conn.createStatement().execute("SET search_path TO \"SeniorCitizen\"");

            // Get phone and password entered in the login form
            String phone = request.getParameter("phone").trim();
            String password = request.getParameter("password").trim();

            // Prepare SQL query to check if the phone and password match
            String query = "SELECT * FROM \"SeniorCitizen\" WHERE \"SC_mob\" = ? AND \"SC_pwd\" = ?";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, phone);
            pstmt.setString(2, password);

            ResultSet rs = pstmt.executeQuery();

            // Check if the credentials are valid
            if (rs.next()) {
                // Redirect to dashboard if credentials are correct
                response.sendRedirect("/SeniorCitizen/pages/Dashboard.html");
            } else {
                // Show an error message if credentials are incorrect
                out.println("<script>alert('❌ Invalid credentials. Please try again.');</script>");
                request.getRequestDispatcher("/SeniorCitizen/pages/LogInPage.html").include(request, response);
            }

            rs.close();
            pstmt.close();
            conn.close();

        } catch (Exception e) {
            // Handle any exceptions that occur during the process
            out.println("<script>alert('❌ Error occurred: " + e.getMessage() + "');</script>");
            request.getRequestDispatcher("/SeniorCitizen/pages/LogInPage.html").include(request, response);
        }
    }
}
