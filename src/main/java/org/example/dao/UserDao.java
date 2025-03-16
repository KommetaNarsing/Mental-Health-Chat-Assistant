package org.example.dao;

import org.example.DBManager;
import org.example.models.User;

import java.sql.*;

public class UserDao {

    public static void insertUser(User signedInUser) {
        // SQL INSERT statement
        String sql = "INSERT INTO healthchat.\"chat_user\"(user_id, user_name, password, salt) VALUES (?, ?, ?, ?);";

        try (Connection conn = DBManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, signedInUser.getUserId());         // id
            stmt.setString(2, signedInUser.getUserName());
            stmt.setString(3, signedInUser.getPassword());
            stmt.setString(4, signedInUser.getSalt());

            // Execute the insert
            int rowsAffected = stmt.executeUpdate();
            System.out.println("Inserted " + rowsAffected + " row(s) into the database.");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public static User getUser(final String userId) {
        User signedInUser = null;

        // SQL INSERT statement
        String sql = "SELECT user_id, user_name, password, salt FROM healthchat.\"chat_user\" where user_id=?";

        try (Connection conn = DBManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
        ) {
            stmt.setString(1, userId);
            ResultSet rs = stmt.executeQuery();
            // Fetch row count
            if (rs.next()) {
                String userName = rs.getString(2);
                String password = rs.getString(3);
                String salt = rs.getString(4);
                signedInUser = new User(userId, userName, password, salt);

            }
            return signedInUser;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return signedInUser;

    }
}
