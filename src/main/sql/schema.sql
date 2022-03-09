CREATE TABLE workspace (
    id SERIAL PRIMARY KEY,
    name character varying(96) NOT NULL,
    description text
);

CREATE TABLE topic (
    id SERIAL PRIMARY KEY,
    workspace_id integer NOT NULL,
    address character varying(96) NOT NULL,
    name character varying(96) NOT NULL,
    description text,
    web_domain character varying(96) NOT NULL,
    imap_host character varying(96) NOT NULL,
    imap_port integer NOT NULL,
    imap_starttls boolean NOT NULL,
    imap_login character varying(96) NOT NULL,
    imap_passwd character varying(96) NOT NULL,
    smtp_host character varying(96) NOT NULL,
    smtp_port integer NOT NULL,
    smtp_starttls boolean NOT NULL,
    smtp_login character varying(96) NOT NULL,
    smtp_passwd character varying(96) NOT NULL,
    CONSTRAINT fk_workspace
      FOREIGN KEY(workspace_id) REFERENCES workspace(id)
);

CREATE TABLE subscriber (
    id SERIAL PRIMARY KEY,
    address character varying(96) NOT NULL,
    name character varying(96)
);

CREATE TABLE subscription (
    id SERIAL PRIMARY KEY,
    subscriber_id integer NOT NULL,
    topic_id integer NOT NULL,
    recieves_digest boolean NOT NULL,
    recieves_messages boolean NOT NULL,
    recieves_moderation boolean NOT NULL,
    may_send_messages boolean NOT NULL,
    subscribe_date TIMESTAMP,
    unsubscribe_date TIMESTAMP,
    CONSTRAINT fk_subscriber
      FOREIGN KEY(subscriber_id) REFERENCES subscriber(id),
    CONSTRAINT fk_topic
      FOREIGN KEY(topic_id) REFERENCES topic(id)
);

CREATE TABLE session (
    id SERIAL PRIMARY KEY,
    subscriber_id integer NOT NULL,
    token character varying(96) NOT NULL,
    valid_since TIMESTAMP NOT NULL,
    valid_until TIMESTAMP NOT NULL,
    CONSTRAINT fk_subscriber_token
      FOREIGN KEY(subscriber_id) REFERENCES subscriber(id)
);

CREATE TABLE message (
    id SERIAL PRIMARY KEY,
    topic_id integer NOT NULL,
    subject character varying(384) NOT NULL,
    processing TIMESTAMP NOT NULL,
    sender character varying(96) NOT NULL,
    message text NOT NULL,
    token character varying(96) NOT NULL,
    CONSTRAINT fk_topic_msg
      FOREIGN KEY(topic_id) REFERENCES topic(id)
);

CREATE UNIQUE INDEX message_token_idx
	ON message(token);

CREATE TABLE attachment (
    id SERIAL PRIMARY KEY,
    message_id integer NOT NULL,
    filename character varying(96) NOT NULL,
    mime_type character varying(96) NOT NULL,
    path_token character varying(96) NOT NULL,
    CONSTRAINT fk_message
      FOREIGN KEY(message_id) REFERENCES message(id)
);

CREATE UNIQUE INDEX attachment_token_idx
	ON attachment(path_token);
