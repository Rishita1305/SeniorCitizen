package com.sc;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class SignUpServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final String URL = "jdbc:postgresql://localhost:5432/mydb";
    private static final String USER = "postgres";
    private static final String PASSWORD = "Post1gre2";

    protected void service(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        Connection conn = null;
        PreparedStatement ctStmt = null;
        PreparedStatement scStmt = null;
        Statement st =null;
        ResultSet ctRs = null;
        ResultSet scRs = null;

        try {
            Class.forName("org.postgresql.Driver");
            conn = DriverManager.getConnection(URL, USER, PASSWORD);
            conn.setAutoCommit(false); // Start transaction

            Statement schemaStmt = conn.createStatement();
            schemaStmt.execute("SET search_path TO \"SeniorCitizen\"");

            // Get form data
            String scName = request.getParameter("sc-name");
            String scPhone = request.getParameter("sc-phone");
            String scPassword = request.getParameter("sc-password");

            String ctName = request.getParameter("ct-name");
            String ctPhone = request.getParameter("ct-phone");
            int ctseq = 0;
            System.out.println("1");

            // 1. Insert into Caretaker and get generated CT_seq
            String verifySQL = "SELECT \"CT_seq\" FROM \"SeniorCitizen\".\"Caretaker\" WHERE \"CT_mob\" = " + "'"+ctPhone+"'" ;
            System.out.println(verifySQL);
            st = conn.createStatement();
            
            ctRs = st.executeQuery(verifySQL);
            System.out.println("2");
            
            if (ctRs.next()) {
            	ctseq = ctRs.getInt(1);	
            }
            System.out.println("3");
            if ( ctseq == 0)
            {
            	System.out.println("4");
            	String insertCaretakerSQL = "INSERT INTO \"Caretaker\" (\"CT_name\", \"CT_mob\") VALUES (?, ?) RETURNING \"CT_seq\"";
                ctStmt = conn.prepareStatement(insertCaretakerSQL);
                ctStmt.setString(1, ctName);
                ctStmt.setString(2, ctPhone);
                System.out.println("5");
                ctRs = ctStmt.executeQuery();
             
                if (ctRs.next()) {
                    ctseq = ctRs.getInt("CT_seq");
                } else {
                    throw new SQLException("Failed to insert Caretaker.");
                }
            }
          
            // 2. Insert into SeniorCitizen with fetched CT_seq
            String insertSCSQL = "INSERT INTO \"SeniorCitizen\" (\"SC_name\", \"SC_mob\", \"SC_pwd\", \"CT_seq\") VALUES (?, ?, ?, ?) RETURNING \"SC_seq\"";
            scStmt = conn.prepareStatement(insertSCSQL);
            scStmt.setString(1, scName);
            scStmt.setString(2, scPhone);
            scStmt.setString(3, scPassword);
            scStmt.setInt(4, ctseq);

            scRs = scStmt.executeQuery();
            int scSeq = -1;
            if (scRs.next()) {
                scSeq = scRs.getInt("SC_seq");
            } else {
                throw new SQLException("Failed to insert Senior Citizen.");
            }

            conn.commit(); // Commit transaction
            response.sendRedirect("/SeniorCitizen/pages/AskLoginpage.html");

        } catch (SQLException e) {
            if (conn != null) try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            out.println("<p style='color:red;'>❌ SQL Error: " + e.getMessage() + "</p>");
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            out.println("<p style='color:red;'>❌ Driver Error: " + e.getMessage() + "</p>");
            e.printStackTrace();
        } catch (Exception e) {
            out.println("<p style='color:red;'>❌ General Error: " + e.getMessage() + "</p>");
            e.printStackTrace();
        } finally {
            try { if (ctRs != null) ctRs.close(); } catch (Exception e) {}
            try { if (scRs != null) scRs.close(); } catch (Exception e) {}
            try { if (ctStmt != null) ctStmt.close(); } catch (Exception e) {}
            try { if (scStmt != null) scStmt.close(); } catch (Exception e) {}
            try { if (conn != null) conn.close(); } catch (Exception e) {}
        }
    }
}
