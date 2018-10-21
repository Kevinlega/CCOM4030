CREATE TABLE users (
  user_id int NOT NULL AUTO_INCREMENT,
  initialValue int unsigned NOT NULL,
  name varchar(255) NOT NULL,
  email varchar(255) NOT NULL,
  hashed_password varchar(255) NOT NULL,
  salt varchar(255) NOT NULL,
  UNIQUE (email),
  PRIMARY KEY(user_id)
);

CREATE TABLE projects (
  project_id int NOT NULL AUTO_INCREMENT,
  folder_link varchar(255) NOT NULL,
  name varchar(255)  NOT NULL,
  user_id int NOT NULL,
  date_created timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  date_updated timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  description text DEFAULT NULL,
  location varchar(255)  NOT NULL,
  FOREIGN KEY (user_id) REFERENCES users(user_id),
  PRIMARY KEY (project_id)
);

CREATE TABLE user_project (
  project_id int NOT NULL,
  user_id int NOT NULL,
  FOREIGN KEY (user_id) REFERENCES users(user_id),
  FOREIGN KEY (project_id) REFERENCES projects(project_id),
  PRIMARY KEY (project_id,user_id)
);