CREATE TABLE users (
  user_id int NOT NULL AUTO_INCREMENT,
  name varchar(255) NOT NULL,
  email varchar(255) NOT NULL,
  hashed_password varchar(255) NOT NULL,
  salt varchar(255) NOT NULL,
  UNIQUE (email),
  verified BOOLEAN DEFAULT false,
  answer varchar(255) NOT NULL,
  PRIMARY KEY(user_id)
);

CREATE TABLE friends (
  first_friend int NOT NULL,
  second_friend int NOT NULL,
  answered BOOLEAN DEFAULT false,
  FOREIGN KEY (first_friend) REFERENCES users(user_id) ON DELETE CASCADE,
  FOREIGN KEY (second_friend) REFERENCES users(user_id) ON DELETE CASCADE,
  PRIMARY KEY (first_friend,second_friend)
);

CREATE TABLE projects (
  project_id int NOT NULL AUTO_INCREMENT,
  folder_link varchar(255) NOT NULL,
  name varchar(255)  NOT NULL,
  admin int NOT NULL,
  date_created timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  date_updated timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  description text DEFAULT NULL,
  location varchar(255)  NOT NULL,
  FOREIGN KEY (admin) REFERENCES users(user_id) ON DELETE CASCADE,
  PRIMARY KEY (project_id)
);

CREATE TABLE user_project (
  project_id int NOT NULL,
  user_id int NOT NULL,
  FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
  FOREIGN KEY (project_id) REFERENCES projects(project_id) ON DELETE CASCADE,
  PRIMARY KEY (project_id,user_id)
);

create table reset_password_requests( 
  user_id INTEGER PRIMARY KEY,
  request_id VARCHAR(64) NOT NULL,
  FOREIGN KEY(user_id) REFERENCES users(user_id) ON DELETE CASCADE
  );


create table verify_requests( 
  user_id INTEGER PRIMARY KEY,
  request_id VARCHAR(64) NOT NULL,
  FOREIGN KEY(user_id) REFERENCES users(user_id) ON DELETE CASCADE
  );

