package dao;

import models.Response;
import models.SurveyResponses;
import models.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDao {

    public static  void insertUser(User signedInUser) {
        String url = "jdbc:postgresql://localhost:5432/healthchat";
        String user = "postgres";
        String password = "Iamnumber@423";

        // SQL INSERT statement
        String sql = "INSERT INTO healthchat.\"user\"(user_id, user_name) VALUES (?, ?);";

        try (Connection conn = DriverManager.getConnection(url, user, password);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, signedInUser.getUserId());         // id
                stmt.setString(2, signedInUser.getUserName()); // name

            // Execute the insert
            int rowsAffected = stmt.executeUpdate();
            System.out.println("Inserted " + rowsAffected + " row(s) into the database.");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public static User getUser(final String userId) {
        User signedInUser = null;
        String url = "jdbc:postgresql://localhost:5432/healthchat";
        String user = "postgres";
        String password = "Iamnumber@423";

        // SQL INSERT statement
        String sql = "SELECT user_id, user_name FROM healthchat.\"user\" where user_id=?";

        try (Connection conn = DriverManager.getConnection(url, user, password);
             PreparedStatement stmt = conn.prepareStatement(sql);
        ) {
            stmt.setString(1, userId);
            ResultSet rs = stmt.executeQuery();
            // Fetch row count
            if (rs.next()) {
                String userName = rs.getString(2);
                signedInUser = new User(userId, userName);

            }
            return signedInUser;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return  signedInUser ;

    }
}
