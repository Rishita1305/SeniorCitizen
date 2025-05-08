package com.sc;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

public class NotificationServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Set the response content type to plain text for easier processing
        response.setContentType("text/plain");
        PrintWriter out = response.getWriter();

        // Retrieve session and check if SC_seq exists (user logged in)
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("SC_seq") == null) {
            out.println("❌ No Notifications Found");
            return;
        }

        // Get the SC_seq value from the session
        int scSeq = (int) session.getAttribute("SC_seq");

        // Database connection setup
        Connection con = null;
        PreparedStatement pst = null;
        ResultSet rs = null;

        try {
            // Establish PostgreSQL database connection
            Class.forName("org.postgresql.Driver");
            con = DriverManager.getConnection("jdbc:postgresql://localhost:5432/mydb", "postgres", "Post1gre2");  // Update DB credentials

            // Prepare SQL query to fetch reminders for the specific Senior Citizen
            String query = "SELECT \"Med_name\", \"Med_bftime\", \"Med_bf_dosage\", \"Med_lunchtime\", " +
                    "\"Med_lunch_dosage\", \"Med_dinnertime\", \"Med_dinnerdosage\" " +
                    "FROM \"SeniorCitizen\".\"Medical\" WHERE \"SC_seq\" = ?";
            pst = con.prepareStatement(query);
            pst.setInt(1, scSeq);
            rs = pst.executeQuery();

            boolean foundReminder = false;

            // Get current time and the time 1 hour later
            LocalTime now = LocalTime.now();
            LocalTime oneHourLater = now.plusHours(1);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");

            // Loop through the database results
            while (rs.next()) {
                // Check breakfast time
                String bfTimeStr = rs.getString("Med_bftime");
                if (bfTimeStr != null) {
                    LocalTime bfTime = LocalTime.parse(bfTimeStr, formatter);
                    if (!bfTime.isBefore(now) && !bfTime.isAfter(oneHourLater)) {
                        String medName = rs.getString("Med_name");
                        String medDosage = rs.getString("Med_bf_dosage");
                        out.println("💊 " + medName + " — " + medDosage + " dose at " + bfTime.format(DateTimeFormatter.ofPattern("hh:mm a")) + " (Breakfast)");
                        foundReminder = true;
                    }
                }

                // Check lunch time
                String lunchTimeStr = rs.getString("Med_lunchtime");
                if (lunchTimeStr != null) {
                    LocalTime lunchTime = LocalTime.parse(lunchTimeStr, formatter);
                    if (!lunchTime.isBefore(now) && !lunchTime.isAfter(oneHourLater)) {
                        String medName = rs.getString("Med_name");
                        String medDosage = rs.getString("Med_lunch_dosage");
                        out.println("💊 " + medName + " — " + medDosage + " dose at " + lunchTime.format(DateTimeFormatter.ofPattern("hh:mm a")) + " (Lunch)");
                        foundReminder = true;
                    }
                }

                // Check dinner time
                String dinnerTimeStr = rs.getString("Med_dinnertime");
                if (dinnerTimeStr != null) {
                    LocalTime dinnerTime = LocalTime.parse(dinnerTimeStr, formatter);
                    if (!dinnerTime.isBefore(now) && !dinnerTime.isAfter(oneHourLater)) {
                        String medName = rs.getString("Med_name");
                        String medDosage = rs.getString("Med_dinnerdosage");
                        out.println("💊 " + medName + " — " + medDosage + " dose at " + dinnerTime.format(DateTimeFormatter.ofPattern("hh:mm a")) + " (Dinner)");
                        foundReminder = true;
                    }
                }
            }

            // If no reminders are found within the next hour, show a message
            if (!foundReminder) {
                out.println("❌ No Notifications Found");
            }

        } catch (Exception e) {
            // Print any exceptions that occur during database interaction
            e.printStackTrace(out);
            out.println("❌ Error processing notifications.");
        } finally {
            // Close database resources to prevent memory leaks
            try { if (rs != null) rs.close(); } catch (Exception e) {}
            try { if (pst != null) pst.close(); } catch (Exception e) {}
            try { if (con != null) con.close(); } catch (Exception e) {}
        }
    }
}
