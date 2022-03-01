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
    digest boolean NOT NULL,
    moderator boolean NOT NULL,
    subscribe_date TIMESTAMP,
    unsubscribe_date TIMESTAMP,
    active boolean NOT NULL,
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

CREATE TABLE attachment (
    id SERIAL PRIMARY KEY,
    message_id integer NOT NULL,
    filename character varying(96) NOT NULL,
    path_token character varying(96) NOT NULL,
    CONSTRAINT fk_message
      FOREIGN KEY(message_id) REFERENCES message(id)
);
