package se.kth.databas.booksdb;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

public class JDBCTest {

    public static void main(String[] args) throws Exception {

        if (args.length != 2) {
            System.out.println("Usage: java JDBCTest <user> <password>");
            System.exit(0);
        }

        String user = args[0]; // user name
        String pwd = args[1]; // password
        System.out.println("user "+user);
        System.out.println("pwd "+pwd);
        System.out.println(user + ", *********");
        String database = "Company"; // the name of the specific database
        String server
                = "jdbc:mysql://localhost:3306/" + database
                + "?UseClientEnc=UTF8";

        Connection con = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

//            Class.forName(" Driver: MySQL Connector/J (ver. mysql-connector-java-8.0.25 (Revision: 08be9e9b4cba6aa115f9b27b215887af40b159e0), JDBC4.2)");
//            Class.forName("com.mysql.jdbc.Driver").newInstance();
            con = DriverManager.getConnection(server, user, pwd);
            System.out.println("Connected!");

            sqlInjection(con,"INSERT into T_Employee values(1,'Marcus','Okodugha','Stockholm')");

            executeQuery(con, "SELECT * FROM T_Employee");
        } finally {
            try {
                if (con != null) {
                    con.close();
                    System.out.println("Connection closed.");
                }
            } catch (SQLException e) {
            }
        }
    }

    public static void executeQuery(Connection con, String query) throws SQLException {

        try (Statement stmt = con.createStatement()) {
            // Execute the SQL statement
            ResultSet rs = stmt.executeQuery(query);

            // Get the attribute names
            ResultSetMetaData metaData = rs.getMetaData();
            int ccount = metaData.getColumnCount();
            for (int c = 1; c <= ccount; c++) {
                System.out.print(metaData.getColumnName(c) + "\t");
            }
            System.out.println();

            // Get the attribute values
            while (rs.next()) {
                // NB! This is an example, -not- the preferred way to retrieve data.
                // You should use methods that return a specific data type, like
                // rs.getInt(), rs.getString() or such.
                // It's also advisable to store each tuple (row) in an object of
                // custom type (e.g. Employee).
                for (int c = 1; c <= ccount; c++) {
                    System.out.print(rs.getObject(c) + "\t");
                }
                System.out.println();
            }

        }
    }

    public static void sqlInjection(Connection con, String sql) {

        try (Statement stmt = con.createStatement()) {

            stmt.executeUpdate(sql);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    //"DELETE FROM T_Employee WHERE FirstName='Marcus'
    //"INSERT into T_Employee values(1,'Marcus','Okodugha','Stockholm')"
}


