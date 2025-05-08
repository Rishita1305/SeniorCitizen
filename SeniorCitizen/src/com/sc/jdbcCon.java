package com.sc;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class jdbcCon extends HttpServlet {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final String URL = "jdbc:postgresql://localhost:5432/mydb";
    private static final String USER = "postgres";
    private static final String PASSWORD = "Post1gre2";

    protected void service(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        // Print request info for debugging
        out.println("<h3>Request Info: " + request.getRequestURL() + "</h3>");

        try {
            // Load PostgreSQL JDBC driver
            Class.forName("org.postgresql.Driver");
            out.println("<p>Driver loaded successfully!</p>");

            // Establish connection
            Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
            
            conn.setAutoCommit(false);
            
            out.println("<p>Connected to database!</p>");

            // Set schema
            Statement schemaStmt = conn.createStatement();
            schemaStmt.execute("SET search_path TO \"SeniorCitizen\"");

            // Tables to fetch
            String[][] tables = {
                {"SeniorCitizen", "SC_seq", "SC_name", "SC_mob", "CT_seq"},
                {"Caretaker", "CT_seq", "CT_name", "CT_mob"},
                {"Medical", "Med_seq", "Med_name", "Med_date", "Med_days", "Med_bftime", "Med_bf_ampm", "Med_bf_dosage",
                    "Med_lunchtime", "Med_lunch_ampm", "Med_lunch_dosage", "Med_dinnertime", "Med_dinner_ampm", "Med_dinner_dosage", "SC_seq"},
                {"Appointments", "App_seq", "App_des", "App_date", "App_hrs", "App_min", "App_ampm", "SC_seq"},
                {"DailyTasks", "Task_seq", "Task_description", "Task_date", "Task_hrs", "Task_min", "Task_ampm", "SC_seq"}
            };

            for (String[] tableInfo : tables) {
                String tableName = tableInfo[0];
                String[] columns = new String[tableInfo.length - 1];
                System.arraycopy(tableInfo, 1, columns, 0, columns.length);

                printTable(out, conn, tableName, columns);
            }

            conn.close();
        } catch (SQLException e) {
            out.println("<p style='color:red;'>❌ SQL Error: " + e.getMessage() + "</p>");
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            out.println("<p style='color:red;'>❌ Driver Error: " + e.getMessage() + "</p>");
            e.printStackTrace();
        } catch (Exception e) {
            out.println("<p style='color:red;'>❌ General Error: " + e.getMessage() + "</p>");
            e.printStackTrace();
        }
    }

    private void printTable(PrintWriter out, Connection conn, String tableName, String[] columns) throws SQLException {
        out.println("<h2>📄 Table: " + tableName + "</h2>");
        out.println("<table border='1'><tr>");
        for (String col : columns) {
            out.print("<th>" + col + "</th>");
        }
        out.println("</tr>");

        StringBuilder query = new StringBuilder("SELECT ");
        for (int i = 0; i < columns.length; i++) {
            query.append("\"").append(columns[i]).append("\"");
            if (i < columns.length - 1) query.append(", ");
        }
        query.append(" FROM \"").append(tableName).append("\"");
        System.out.println("Query:"+query);
        
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(query.toString());

        while (rs.next()) {
            out.println("<tr>");
            for (String col : columns) {
                out.print("<td>" + rs.getString(col) + "</td>");
            }
            out.println("</tr>");
        }

        out.println("</table><br>");
    }
}
