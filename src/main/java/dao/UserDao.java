package dao;

import models.Response;
import models.SurveyResponses;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class UserDao {

    public static  void insertUser(SurveyResponses surveyResponses) {
        String url = "jdbc:postgresql://localhost:5432/healthchat";
        String user = "postgres";
        String password = "Iamnumber@423";

        // SQL INSERT statement
        String sql = "INSERT INTO \"healthchat\".survey_responses(question, user_id, answer) VALUES (?, ?, ?);";

        try (Connection conn = DriverManager.getConnection(url, user, password);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            for(Response r : surveyResponses.getResponses()) {
                stmt.setString(1, r.getQuestion());         // id
                stmt.setString(2, surveyResponses.getUserId()); // name
                stmt.setString(3, r.getResponse()); // email
                stmt.addBatch();
            }

            // Execute the insert
            int[] rowsAffected = stmt.executeBatch();
            System.out.println("Inserted " + rowsAffected.length + " row(s) into the database.");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
