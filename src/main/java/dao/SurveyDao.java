package dao;

import models.Message;
import models.Response;
import models.SurveyResponses;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SurveyDao {

    public static  void insertSurveyResponse(SurveyResponses surveyResponses) {
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

    public  static boolean IsSurveyTaken(final String userId) {
        String url = "jdbc:postgresql://localhost:5432/healthchat";
        String user = "postgres";
        String password = "Iamnumber@423";

        // SQL INSERT statement
        String sql = "SELECT COUNT(*) FROM healthchat.survey_responses where user_id=?";

        try (Connection conn = DriverManager.getConnection(url, user, password);
             PreparedStatement stmt = conn.prepareStatement(sql);
             ) {
            stmt.setString(1, userId);
            ResultSet rs = stmt.executeQuery();

            // Fetch row count
            if (rs.next()) {
                int rowCount = rs.getInt(1); // Get the first column value
                return rowCount > 0;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public  static List<Response> getSurveyResponses(final String userId) {
        List<Response> responses = new ArrayList<>();
        String url = "jdbc:postgresql://localhost:5432/healthchat";
        String user = "postgres";
        String password = "Iamnumber@423";

        // SQL INSERT statement
        String sql = "SELECT question, user_id, answer FROM healthchat.survey_responses where user_id=?";

        try (Connection conn = DriverManager.getConnection(url, user, password);
             PreparedStatement stmt = conn.prepareStatement(sql);
        ) {
            stmt.setString(1, userId);
            ResultSet rs = stmt.executeQuery();
            // Fetch row count
            while (rs.next()) {
                String question = rs.getString(1);
                String answer = rs.getString(3);
                Response response = new Response(question, answer);
                responses.add(response);
            }
            return responses;


        } catch (SQLException e) {
            e.printStackTrace();
        }

        return responses;
    }
}
