package com.sc;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

public class ScLogin extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final String URL = "jdbc:postgresql://localhost:5432/mydb";
    private static final String USER = "postgres";
    private static final String PASSWORD = "Post1gre2";

    protected void service(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        try {
            
        	System.out.println("1");
        	// Load database driver
            Class.forName("org.postgresql.Driver");
            // Establish database connection
            Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
            conn.setAutoCommit(false);

            // Set schema to SeniorCitizen
            conn.createStatement().execute("SET search_path TO \"SeniorCitizen\"");

            System.out.println("2");
            // Get phone and password entered in the login form
            String phone = request.getParameter("phone").trim();
            String password = request.getParameter("password").trim();
            System.out.println(phone +" "+password);

            // Prepare SQL query to check if the phone and password match
            String query = "SELECT \"SeniorCitizen\".\"SC_seq\" FROM \"SeniorCitizen\".\"SeniorCitizen\" WHERE \"SC_mob\" = ? AND \"SC_pwd\" = ?";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, phone);
            pstmt.setString(2, password);

            ResultSet rs = pstmt.executeQuery();
            System.out.println("3");
            // Check if the credentials are valid
            if (rs.next()) {
                // Redirect to dashboard if credentials are correct
            	System.out.println("4");
            	int seq = rs.getInt(1);
                System.out.println("Seq: "+seq);
                rs.close();
                pstmt.close();
                conn.close();
                
                HttpSession sess = request.getSession();
                sess.setAttribute("type", "SC");
                sess.setAttribute("seq", seq);
                response.sendRedirect("/SeniorCitizen/pages/Dashboard.html");
            } else {
                // Show an error message if credentials are incorrect
            	System.out.println("5");
                //out.println("<script>alert('❌ Invalid credentials. Please try again.');</script>");
                rs.close();
                pstmt.close();
                conn.close();
                request.getRequestDispatcher("/pages/LogInpageFailed.html").include(request, response);
            }


        } catch (Exception e) {
            // Handle any exceptions that occur during the process
        	System.out.println("6" + e.getMessage());
            //out.println("<script>alert('❌ Error occurred: Login Failed " + e.getMessage() + "');</script>");
            request.getRequestDispatcher("/pages/LogInpageFailed.html").include(request, response);
        }
    }
}
