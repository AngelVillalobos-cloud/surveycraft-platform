-- ============================================================
--  SurveyCraft - Schema MySQL
--  Compatible con WAMP / XAMPP / Laragon
--  Ejecutar en phpMyAdmin o consola MySQL
-- ============================================================

CREATE DATABASE IF NOT EXISTS surveycraft
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE surveycraft;

-- -------------------------------------------------------
-- Tabla: roless
-- -------------------------------------------------------
CREATE TABLE IF NOT EXISTS roless (
  role_id   INT          NOT NULL AUTO_INCREMENT,
  nombre    VARCHAR(30)  NOT NULL UNIQUE,
  PRIMARY KEY (role_id)
) ENGINE=InnoDB;

-- Seed de roles iniciales
INSERT IGNORE INTO roless (nombre) VALUES ('ROLE_CRAFTER'), ('ROLE_PLAYER');

-- -------------------------------------------------------
-- Tabla: usuarios
-- -------------------------------------------------------
CREATE TABLE IF NOT EXISTS usuarios (
  user_id       INT          NOT NULL AUTO_INCREMENT,
  username      VARCHAR(20),
  email         VARCHAR(50)  NOT NULL UNIQUE,
  password_hash VARCHAR(60)  NOT NULL,
  role_id       INT,
  PRIMARY KEY (user_id),
  CONSTRAINT fk_usuarios_role FOREIGN KEY (role_id) REFERENCES roless(role_id)
) ENGINE=InnoDB;

-- -------------------------------------------------------
-- Tabla: categories
-- -------------------------------------------------------
CREATE TABLE IF NOT EXISTS categories (
  id               INT          NOT NULL AUTO_INCREMENT,
  nombre_categoria VARCHAR(50)  NOT NULL UNIQUE,
  PRIMARY KEY (id)
) ENGINE=InnoDB;

-- -------------------------------------------------------
-- Tabla: videojuegos
-- -------------------------------------------------------
CREATE TABLE IF NOT EXISTS videojuegos (
  id          INT           NOT NULL AUTO_INCREMENT,
  titulo      VARCHAR(100)  NOT NULL,
  descripcion VARCHAR(255),
  categoria_id INT,
  PRIMARY KEY (id),
  CONSTRAINT fk_vj_categoria FOREIGN KEY (categoria_id) REFERENCES categories(id)
) ENGINE=InnoDB;

-- -------------------------------------------------------
-- Tabla: surveys
-- -------------------------------------------------------
CREATE TABLE IF NOT EXISTS surveys (
  survey_id    INT           NOT NULL AUTO_INCREMENT,
  titulo       VARCHAR(100)  NOT NULL,
  descripcion  VARCHAR(255),
  estado       VARCHAR(20)   DEFAULT 'activa',
  juego_id     INT,
  tipo_encuesta VARCHAR(20),
  crafter_id   INT,
  PRIMARY KEY (survey_id),
  CONSTRAINT fk_surveys_crafter FOREIGN KEY (crafter_id) REFERENCES usuarios(user_id)
) ENGINE=InnoDB;

-- -------------------------------------------------------
-- Tabla: questions
-- -------------------------------------------------------
CREATE TABLE IF NOT EXISTS questions (
  question_id    INT          NOT NULL AUTO_INCREMENT,
  pregunta_texto VARCHAR(255),
  tipo_pregunta  VARCHAR(20),
  survey_id      INT,
  PRIMARY KEY (question_id),
  CONSTRAINT fk_questions_survey FOREIGN KEY (survey_id) REFERENCES surveys(survey_id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- -------------------------------------------------------
-- Tabla: options
-- -------------------------------------------------------
CREATE TABLE IF NOT EXISTS options (
  option_id    INT          NOT NULL AUTO_INCREMENT,
  opcion_texto VARCHAR(255),
  question_id  INT,
  PRIMARY KEY (option_id),
  CONSTRAINT fk_options_question FOREIGN KEY (question_id) REFERENCES questions(question_id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- -------------------------------------------------------
-- Tabla: responses
-- -------------------------------------------------------
CREATE TABLE IF NOT EXISTS responses (
  response_id    INT          NOT NULL AUTO_INCREMENT,
  player_id      INT,
  question_id    INT,
  option_id      INT,
  tiempo_jugado  VARCHAR(20),
  plataforma_usada VARCHAR(20),
  submitted_at   DATETIME     DEFAULT CURRENT_TIMESTAMP,
  session_id     VARCHAR(50),
  PRIMARY KEY (response_id),
  CONSTRAINT fk_responses_player   FOREIGN KEY (player_id)   REFERENCES usuarios(user_id),
  CONSTRAINT fk_responses_question FOREIGN KEY (question_id) REFERENCES questions(question_id),
  CONSTRAINT fk_responses_option   FOREIGN KEY (option_id)   REFERENCES options(option_id)
) ENGINE=InnoDB;
