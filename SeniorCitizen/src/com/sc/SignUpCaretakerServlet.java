package com.sc;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class SignUpCaretakerServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final String URL = "jdbc:postgresql://localhost:5432/mydb";
    private static final String USER = "postgres";
    private static final String PASSWORD = "Post1gre2";

    protected void service(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        String ctName = request.getParameter("CT_name");
        String ctPhone = request.getParameter("CT_phone");
        ctPhone = ctPhone.trim();
        String ctPassword = request.getParameter("CT_password");
        System.out.println("1");
        System.out.println("new statement");
        System.out.println(ctName + " " + ctPhone + " " + ctPassword);

        if (ctName == null || ctPhone == null || ctPassword == null ) {
            out.println("<p style='color:red;'>❌ All fields are required.</p>");
            return;
        }
        System.out.println("2");

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            Class.forName("org.postgresql.Driver");
            conn = DriverManager.getConnection(URL, USER, PASSWORD);

            Statement schemaStmt = conn.createStatement();
            schemaStmt.execute("SET search_path TO \"SeniorCitizen\"");
            System.out.println("3");
            // Check if caretaker already exists
            String checkSQL = "SELECT \"CT_seq\" FROM \"Caretaker\" WHERE \"CT_mob\" = ?";
            pstmt = conn.prepareStatement(checkSQL);
            pstmt.setString(1, ctPhone);
            rs = pstmt.executeQuery();
            System.out.println("4");
            if (rs.next()) {
            	  System.out.println("5");
                //if Caretaker exists, update their name and password
                int ctSeq = rs.getInt("CT_seq");
                System.out.println(ctSeq);
                rs.close();
                pstmt.close();

                String updateSQL = "UPDATE \"Caretaker\" SET \"CT_name\" = ?, \"CT_pwd\" = ? WHERE \"CT_seq\" = ?";
                pstmt = conn.prepareStatement(updateSQL);
                pstmt.setString(1, ctName);
                pstmt.setString(2, ctPassword);
                pstmt.setInt(3, ctSeq);
                pstmt.executeUpdate();
                System.out.println("6");
                out.println("<p style='color:green;'>✅ Existing caretaker updated successfully.</p>");
            } else {
            	  System.out.println("7");
                // New caretaker, insert into table
                pstmt.close();
                String insertSQL = "INSERT INTO \"Caretaker\" (\"CT_name\", \"CT_mob\", \"CT_pwd\") VALUES (?, ?, ?) RETURNING \"CT_seq\"";
                pstmt = conn.prepareStatement(insertSQL);
                pstmt.setString(1, ctName);
                pstmt.setString(2, ctPhone);
                pstmt.setString(3, ctPassword);

                rs = pstmt.executeQuery();
                int ctSeq = -1;
                if (rs.next()) {
                    ctSeq = rs.getInt("CT_seq");
                    out.println("<p style='color:green;'>✅ New caretaker registered successfully. CT_seq: " + ctSeq + "</p>");
                } else {
                    throw new SQLException("Failed to insert new caretaker.");
                }
            }
            System.out.println("8");

        } catch (ClassNotFoundException e) {
            out.println("<p style='color:red;'>❌ JDBC Driver not found: " + e.getMessage() + "</p>");
            e.printStackTrace();
        } catch (SQLException e) {
            out.println("<p style='color:red;'>❌ Database Error: " + e.getMessage() + "</p>");
            e.printStackTrace();
        } finally {
            try { if (rs != null) rs.close(); } catch (Exception e) {}
            try { if (pstmt != null) pstmt.close(); } catch (Exception e) {}
            try { if (conn != null) conn.close(); } catch (Exception e) {}
        }
    }
}
