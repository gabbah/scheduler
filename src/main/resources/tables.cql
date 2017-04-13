CREATE TABLE sessions (
  id uuid,
  owner uuid,
  title varchar,
  resources map<uuid, text>,
  timeSlots int,
  status varchar,
  PRIMARY KEY(id)
);

CREATE TABLE topics (
  session_id uuid,
  id uuid,
  title varchar,
  description text,
  PRIMARY KEY(session_id, id)
);


CREATE TABLE votes (
  session_id uuid,
  user_id uuid,
  topic_ids list<uuid>,
  PRIMARY KEY(session_id, user_id)
);
