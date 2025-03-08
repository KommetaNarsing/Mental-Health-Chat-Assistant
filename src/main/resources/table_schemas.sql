CREATE TABLE IF NOT EXISTS healthchat.chat_user
(
    user_id   VARCHAR(255) PRIMARY KEY,
    user_name VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS healthchat.survey_responses
(
    question VARCHAR(255) NOT NULL,
    answer   VARCHAR(255) NOT NULL,
    user_id  VARCHAR(255) NOT NULL,
    FOREIGN KEY (user_id) REFERENCES chat_user (user_id)
);

CREATE TABLE IF NOT EXISTS healthchat.chat_content
(
    created_at       VARCHAR(255) NOT NULL,
    conversation_id VARCHAR(255) NOT NULL,
    user_id         VARCHAR(255) NOT NULL,
    role            VARCHAR(255) NOT NULL,
    content         TEXT         NOT NULL,
    FOREIGN KEY (user_id) REFERENCES chat_user (user_id)
);