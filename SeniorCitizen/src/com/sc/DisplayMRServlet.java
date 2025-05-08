package com.sc;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.RequestDispatcher;

public class DisplayMRServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private static final String URL = "jdbc:postgresql://localhost:5432/mydb";
    private static final String USER = "postgres";
    private static final String PASSWORD = "Post1gre2";

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        
        
        String html = "<!DOCTYPE html>\n" +
        		"<html lang=\"en\">\n" +
        		"<head>\n" +
        		"  <meta charset=\"UTF-8\" />\n" +
        		"  <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\"/>\n" +
        		"  <title>Medical Reminders</title>\n" +
        		"  <link href=\"https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css\" rel=\"stylesheet\"/>\n" +
        		"  <style>\n" +
        		"    .header {\n" +
        		"      background-color: #003366;\n" +
        		"      color: white;\n" +
        		"      display: flex;\n" +
        		"      justify-content: space-between;\n" +
        		"      align-items: center;\n" +
        		"      padding: 10px 20px;\n" +
        		"    }\n" +
        		"    .controls {\n" +
        		"      display: flex;\n" +
        		"      flex-direction: column;\n" +
        		"      align-items: flex-end;\n" +
        		"    }\n" +
        		"    .lang-btn, .font-btn {\n" +
        		"      background-color: white;\n" +
        		"      color: #003366;\n" +
        		"      border: none;\n" +
        		"      padding: 5px 10px;\n" +
        		"      margin: 2px;\n" +
        		"      border-radius: 4px;\n" +
        		"      cursor: pointer;\n" +
        		"    }\n" +
        		"    .home-icon {\n" +
        		"      font-size: 20px;\n" +
        		"      cursor: pointer;\n" +
        		"    }\n" +
        		"    .logo {\n" +
        		"      font-size: 24px;\n" +
        		"      font-weight: bold;\n" +
        		"    }\n" +
        		"    .controls span, .controls button {\n" +
        		"      margin-right: 8px;\n" +
        		"    }\n" +
        		"    .add-btn, .submit-btn {\n" +
        		"      margin: 15px auto;\n" +
        		"      padding: 10px 20px;\n" +
        		"      display: block;\n" +
        		"      background: green;\n" +
        		"      color: white;\n" +
        		"      border: none;\n" +
        		"      border-radius: 5px;\n" +
        		"      cursor: pointer;\n" +
        		"    }\n" +
        		"    .submit-btn { background: blue; }\n" +
        		"    .remove-btn {\n" +
        		"      padding: 5px 10px;\n" +
        		"      background: red;\n" +
        		"      color: white;\n" +
        		"      border: none;\n" +
        		"      border-radius: 5px;\n" +
        		"      cursor: pointer;\n" +
        		"    }\n" +
        		"    .table-container {\n" +
        		"      margin: 20px auto;\n" +
        		"      width: 95%;\n" +
        		"    }\n" +
        		"    table {\n" +
        		"      width: 100%;\n" +
        		"      border-collapse: collapse;\n" +
        		"    }\n" +
        		"    th, td {\n" +
        		"      border: 1px solid #ccc;\n" +
        		"      padding: 10px;\n" +
        		"      text-align: center;\n" +
        		"    }\n" +
        		"    input, select {\n" +
        		"      width: 100%;\n" +
        		"      padding: 5px;\n" +
        		"    }\n" +
        		"  </style>\n" +
        		"</head>\n" +
        		"<body>\n" +
        		"  <header class=\"header\">\n" +
        		"    <div class=\"home-icon\" onclick=\"goHome()\">🏠</div>\n" +
        		"    <div class=\"logo\" id=\"logo-text\"><i>❀ Oldie2Genz</i></div>\n" +
        		"    <div class=\"controls\">\n" +
        		"      <div>\n" +
        		"        <span id=\"lang-label\">Language:</span>\n" +
        		"        <button class=\"lang-btn\" onclick=\"changeLanguage('en')\">English</button>\n" +
        		"        <button class=\"lang-btn\" onclick=\"changeLanguage('hi')\">हिन्दी</button>\n" +
        		"        <button class=\"lang-btn\" onclick=\"changeLanguage('te')\">తెలుగు</button>\n" +
        		"      </div>\n" +
        		"      <div>\n" +
        		"        <span id=\"font-label\">Font:</span>\n" +
        		"        <button class=\"font-btn\" onclick=\"adjustFontSize(1)\">+</button>\n" +
        		"        <button class=\"font-btn\" onclick=\"adjustFontSize(-1)\">-</button>\n" +
        		"      </div>\n" +
        		"    </div>\n" +
        		"  </header>\n" +
        		"  <button class=\"add-btn\" id=\"add-btn\" onclick=\"addRow()\">➕ Add Medicine</button>\n" +
        		"  <div class=\"table-container\">\n" +
        		"    <table id=\"medicine-table\">\n" +
        		"      <thead>\n" +
        		"        <tr>\n" +
        		"          <th>Medicine Name</th>\n" +
        		"          <th>Med Date</th>\n" +
        		"          <th>No. of Days</th>\n" +
        		"          <th>Breakfast Time</th>\n" +
        		"          <th>Dosage</th>\n" +
        		"          <th>Lunch Time</th>\n" +
        		"          <th>Dosage</th>\n" +
        		"          <th>Dinner Time</th>\n" +
        		"          <th>Dosage</th>\n" +
        		"          <th>Remove</th>\n" +
        		"        </tr>\n" +
        		"      </thead>\n" +
        		"      <tbody></tbody>\n" +
        		"    </table>\n" +
        		"  </div>\n" +
        		"  <button class=\"submit-btn\" onclick=\"submitTable()\">Submit</button>\n" +
        		"  <script>\n" +
        		"    let fontSize = 16;\n" +
        		"    function adjustFontSize(change) {\n" +
        		"      fontSize += change;\n" +
        		"      document.documentElement.style.fontSize = fontSize + 'px';\n" +
        		"    }\n" +
        		"    function changeLanguage(lang) {\n" +
        		"      alert('Language switched to: ' + lang);\n" +
        		"    }\n" +
				" function goHome() {\n" +
        		"	  window.location.href = '/SeniorCitizen/pages/Remainders.html'; \n" +
        		
        		"    }\n" +
        		"    function addRow() {\n" +
        		"      const table = document.getElementById('medicine-table').getElementsByTagName('tbody')[0];\n" +
        		"      const newRow = table.insertRow();\n" +
        		"      const fields = ['text', 'date', 'number', 'time', 'text', 'time', 'text', 'time', 'text'];\n" +
        		"      fields.forEach(type => {\n" +
        		"        const cell = newRow.insertCell();\n" +
        		"        const input = document.createElement('input');\n" +
        		"        input.type = type;\n" +
        		"        cell.appendChild(input);\n" +
        		"      });\n" +
        		"      const removeCell = newRow.insertCell();\n" +
        		"      const removeBtn = document.createElement('button');\n" +
        		"      removeBtn.className = 'remove-btn';\n" +
        		"      removeBtn.textContent = 'Remove';\n" +
        		"      removeBtn.onclick = () => newRow.remove();\n" +
        		"      removeCell.appendChild(removeBtn);\n" +
        		"    }\n" +
        		"    function submitTable() {\n" +
        		"      alert('Data submitted.');\n" +
        		"    }\n" +
        		"  </script>\n" +
        		"</body>\n" +
        		"</html>";

        
        out.println(html); 
        

        // Retrieve scSeq parameter (to filter by logged-in user)
      
          int scSeq = Integer.parseInt(request.getParameter("scSeq"));
/*
        try {
            Class.forName("org.postgresql.Driver");
            Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
            conn.setAutoCommit(false);

            // Set schema
            Statement schemaStmt = conn.createStatement();
            schemaStmt.execute("SET search_path TO \"SeniorCitizen\"");

            // Query to get the medical records for the logged-in SC
            String selectSQL = "SELECT * FROM \"Medical\" WHERE \"SC_seq\" = ?";
            PreparedStatement pstmt = conn.prepareStatement(selectSQL);
            pstmt.setInt(1, scSeq);

            ResultSet rs = pstmt.executeQuery();

            // Store the data to request attributes to pass it to the next page
            request.setAttribute("resultSet", rs);
            request.setAttribute("scSeq", scSeq); // passing scSeq to be used in the HTML page

            // Close connection
            pstmt.close();
            conn.close();

            // Forward to the MedicalRemainder.html page with the data
            RequestDispatcher dispatcher = request.getRequestDispatcher("/SeniorCitizen/pages/MedicalRemainder.html");
            dispatcher.forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            out.println("<h3 style='color:red;'>❌ Error: " + e.getMessage() + "</h3>");
        }
*/
    }
}
