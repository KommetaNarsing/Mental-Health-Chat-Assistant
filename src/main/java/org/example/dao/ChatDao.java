package org.example.dao;

import org.example.DBManager;
import org.example.models.ChatContent;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ChatDao {

    public static ChatContent getConversation(final String userId) {
        ChatContent chatContent = null;

        String sql = "SELECT  user_id, conversation_id,timestamp, role, content  FROM healthchat.chat_content where user_id=? order by timestamp desc limit 1;";

        try (Connection conn = DBManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
        ) {
            stmt.setString(1, userId);
            ResultSet rs = stmt.executeQuery();
            // Fetch row count
            if (rs.next()) {
                chatContent = new ChatContent();
                chatContent.setUserId(rs.getString(1));
                chatContent.setConversationId(rs.getString(2));
                chatContent.setTimeStamp(rs.getString(3));
                chatContent.setRole(rs.getString(4));
                chatContent.setContent(rs.getString(5));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return chatContent;
    }


    public static void insertChatContent(ChatContent chatContent) {
        // SQL INSERT statement
        String sql = "INSERT INTO healthchat.chat_content(user_id, conversation_id, \"timestamp\", role, content) VALUES (?, ?, ?, ?, ?);";

        try (Connection conn = DBManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, chatContent.getUserId());
            stmt.setString(2, chatContent.getConversationId());
            stmt.setString(3, chatContent.getTimeStamp());
            stmt.setString(4, chatContent.getRole());
            stmt.setString(5, chatContent.getContent());
            stmt.addBatch();


            // Execute the insert
            int[] rowsAffected = stmt.executeBatch();
            System.out.println("Inserted " + rowsAffected.length + " row(s) into the database.");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public static List<ChatContent> getChatHistory(final String conversationId) {
        List<ChatContent> chatHistory = new ArrayList<>();

        String sql = "SELECT user_id, conversation_id, \"timestamp\", role, content FROM healthchat.chat_content where conversation_id=? order by timestamp desc limit 10";

        try (Connection conn = DBManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
        ) {
            stmt.setString(1, conversationId);
            ResultSet rs = stmt.executeQuery();
            // Fetch row count
            while (rs.next()) {
                String userId = rs.getString(1);
                String timeStamp = rs.getString(3);
                String role = rs.getString(4);
                String content = rs.getString(5);
                ChatContent chatContent = new ChatContent();
                chatContent.setContent(content);
                chatContent.setRole(role);
                chatContent.setUserId(userId);
                chatContent.setConversationId(conversationId);
                chatContent.setTimeStamp(timeStamp);
                chatHistory.add(0, chatContent);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return chatHistory;
    }
}
