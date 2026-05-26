/*
 Navicat Premium Dump SQL

 Source Server         : GCLOCALSERVER
 Source Server Type    : MySQL
 Source Server Version : 100432 (10.4.32-MariaDB)
 Source Host           : localhost:3306
 Source Schema         : digitalshelf

 Target Server Type    : MySQL
 Target Server Version : 100432 (10.4.32-MariaDB)
 File Encoding         : 65001

 Date: 14/05/2026 23:38:56
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for admin
-- ----------------------------
DROP TABLE IF EXISTS `admin`;
CREATE TABLE `admin`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `password` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `username`(`username` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of admin
-- ----------------------------
INSERT INTO `admin` VALUES (1, 'admin', 'admin123');

-- ----------------------------
-- Table structure for books
-- ----------------------------
DROP TABLE IF EXISTS `books`;
CREATE TABLE `books`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `isbn` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `title` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `author` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `cover_image_path` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `shelf_location` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `genre` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `category` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `publication_date` date NULL DEFAULT NULL,
  `total_copies` int NOT NULL DEFAULT 1,
  `available_copies` int NOT NULL DEFAULT 1,
  `is_archived` tinyint(1) NOT NULL DEFAULT 0,
  `archived_at` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `isbn`(`isbn` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 26 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of books
-- ----------------------------
INSERT INTO `books` VALUES (1, '9780135166307', 'Introduction to Java Programming', 'Y. Daniel Liang', NULL, 'A-01', 'Programming', 'Programming', NULL, 5, 5, 1, '2026-05-14 23:36:54');
INSERT INTO `books` VALUES (2, '9780262033848', 'Data Structures and Algorithms', 'Robert Lafore', NULL, 'B-02', 'Programming', 'Computer Science', NULL, 2, 2, 1, '2026-05-14 23:38:11');
INSERT INTO `books` VALUES (3, '9780132350884', 'Clean Code', 'Robert C. Martin', NULL, 'A-03', 'Programming', 'Programming', NULL, 2, 2, 1, '2026-05-14 23:37:08');
INSERT INTO `books` VALUES (4, '9780201616224', 'The Pragmatic Programmer', 'Andrew Hunt', NULL, 'A-03', 'Programming', 'Software Engineering', NULL, 3, 3, 1, '2026-05-14 23:37:22');
INSERT INTO `books` VALUES (11, '9780063204157', 'Remarkably Bright Creatures', 'Shelby Van Pelt', 'covers/remarkably_bright_creatures.jfif', 'FIC-A-01', 'Contemporary Fiction', NULL, '2022-01-01', 3, 2, 0, NULL);
INSERT INTO `books` VALUES (12, '9780385495325', 'The Code Book: The Science of Secrecy...', 'Simon Singh', 'covers/the_code_book.jpg', 'NONFIC-B-02', 'Mathematics/History', NULL, '2002-01-01', 2, 2, 0, NULL);
INSERT INTO `books` VALUES (13, '9780393248023', 'Smoke Gets in Your Eyes: And Other...', 'Caitlin Doughty', 'covers/smoke_gets_in_your_eyes.jpg', 'NONFIC-C-03', 'Social Science/Memoir', NULL, '2014-01-01', 2, 2, 0, NULL);
INSERT INTO `books` VALUES (14, '9780393304510', 'Civilization and Its Discontents', 'Sigmund Freud', 'covers/civilization_discontents.jpg', 'NONFIC-D-04', 'Psychology/Philosophy', NULL, '1930-01-01', 4, 4, 0, NULL);
INSERT INTO `books` VALUES (15, '9781610352202', 'The Shirtmaking Workbook', 'David Page Coffin', 'covers/shirtmaking_workbook.jpg', 'NONFIC-E-05', 'Crafts/Hobbies', NULL, '1993-01-01', 1, 1, 0, NULL);
INSERT INTO `books` VALUES (16, '9780140085208', 'The World of the Shining Prince', 'Ivan Morris', 'covers/shining_prince.jpg', 'NONFIC-F-06', 'History/Japan', NULL, '1964-01-01', 1, 1, 0, NULL);
INSERT INTO `books` VALUES (17, '9781844836422', 'The Big Book of Juices', 'Natalie Savona', 'covers/big_book_juices.png', 'NONFIC-G-07', 'Health/Nutrition', NULL, '2008-01-01', 2, 2, 0, NULL);
INSERT INTO `books` VALUES (18, '9780471568866', 'Hard Drive: Bill Gates...', 'James Wallace', 'covers/hard_drive.jpg', 'BIOG-H-08', 'Biography/Technology', NULL, '1992-01-01', 1, 1, 0, NULL);
INSERT INTO `books` VALUES (19, '9780465019779', 'The Interpretation of Dreams', 'Sigmund Freud', 'covers/interpretation_dreams.jpg', 'NONFIC-D-04', 'Psychology', NULL, '1899-01-01', 3, 3, 0, NULL);
INSERT INTO `books` VALUES (20, '9780385493628', 'Fermat\'s Enigma', 'Simon Singh', 'covers/fermats_enigma.jpg', 'NONFIC-B-02', 'Mathematics', NULL, '1997-01-01', 2, 2, 0, NULL);
INSERT INTO `books` VALUES (21, '9780393249785', 'From Here to Eternity', 'Caitlin Doughty', 'covers/from_here_to_eternity.jpg', 'NONFIC-C-03', 'Social Science', NULL, '2017-01-01', 2, 2, 0, NULL);
INSERT INTO `books` VALUES (22, '9784770030166', 'The Art of Setting Stones', 'Marc Peter Keane', 'covers/art_setting_stones.jpg', 'NONFIC-I-09', 'Gardening/Design', NULL, '2002-01-01', 1, 1, 0, NULL);
INSERT INTO `books` VALUES (23, '9784770010649', 'Japanese Woodworking', 'Koichi Hara', 'covers/japanese_woodworking.jpg', 'NONFIC-E-05', 'Crafts/Woodworking', NULL, '1978-01-01', 1, 1, 0, NULL);
INSERT INTO `books` VALUES (24, '9789776234008', 'The World of the Jinn', 'Ashraf. F. N. Abouelazayem', 'covers/world_of_jinn.jpg', 'NONFIC-J-10', 'Religion/Mythology', NULL, '2012-01-01', 1, 1, 0, NULL);
INSERT INTO `books` VALUES (25, '9780130886828', 'Management Information Systems', 'Raymond McLeod', 'covers/management_info_systems.jpg', 'NONFIC-K-11', 'Business/IT', NULL, '1979-01-01', 3, 3, 0, NULL);

-- ----------------------------
-- Table structure for borrow_records
-- ----------------------------
DROP TABLE IF EXISTS `borrow_records`;
CREATE TABLE `borrow_records`  (
  `record_id` int NOT NULL AUTO_INCREMENT,
  `book_id` int NOT NULL,
  `student_id` int NOT NULL,
  `borrow_date` datetime NOT NULL,
  `due_date` datetime NOT NULL,
  `return_date` datetime NULL DEFAULT NULL,
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'BORROWED',
  `book_title` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `student_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`record_id`) USING BTREE,
  INDEX `book_id`(`book_id` ASC) USING BTREE,
  INDEX `student_id`(`student_id` ASC) USING BTREE,
  CONSTRAINT `borrow_records_ibfk_1` FOREIGN KEY (`book_id`) REFERENCES `books` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `borrow_records_ibfk_2` FOREIGN KEY (`student_id`) REFERENCES `students` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 10 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of borrow_records
-- ----------------------------
INSERT INTO `borrow_records` VALUES (1, 1, 4, '2026-05-08 08:58:40', '2026-05-22 08:58:40', '2026-05-08 08:58:46', 'RETURNED', 'Introduction to Java Programming', 'Reyes, Marcus Gabriel');
INSERT INTO `borrow_records` VALUES (2, 2, 1, '2026-05-10 12:24:54', '2026-05-24 12:24:54', '2026-05-14 22:37:59', 'RETURNED', 'Data Structures and Algorithms', 'Razon, Justin Rain');
INSERT INTO `borrow_records` VALUES (9, 11, 1, '2026-05-14 21:21:36', '2026-05-28 21:21:36', NULL, 'BORROWED', 'Remarkably Bright Creatures', 'Razon, Justin Rain');

-- ----------------------------
-- Table structure for categories
-- ----------------------------
DROP TABLE IF EXISTS `categories`;
CREATE TABLE `categories`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `name`(`name` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 8 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of categories
-- ----------------------------
INSERT INTO `categories` VALUES (1, 'Programming', 'Computer programming and software development books');
INSERT INTO `categories` VALUES (2, 'Computer Science', 'Computer science theory and practice');
INSERT INTO `categories` VALUES (3, 'Software Engineering', 'Software development methodologies');
INSERT INTO `categories` VALUES (4, 'Science', 'General science books');
INSERT INTO `categories` VALUES (5, 'General Reference', 'Encyclopedias and general reference');
INSERT INTO `categories` VALUES (6, 'Mathematics', 'Mathematics textbooks');
INSERT INTO `categories` VALUES (7, 'Accounting textbook', 'Accounting and finance books');

-- ----------------------------
-- Table structure for students
-- ----------------------------
DROP TABLE IF EXISTS `students`;
CREATE TABLE `students`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `student_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `year_level` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `password` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `student_id`(`student_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 9 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of students
-- ----------------------------
INSERT INTO `students` VALUES (1, '202513969', 'Razon, Justin Rain', '202513969@gordoncollege.edu.ph', '1st Year', '202513969');
INSERT INTO `students` VALUES (2, '202513209', 'Alinan, Ethan Axl', '202513209@gordoncollege.edu.ph', '1st Year', '202513209');
INSERT INTO `students` VALUES (3, '202510869', 'Dela Torre, Caine Jarret', '202510869@gordoncollege.edu.ph', '1st Year', '202510869');
INSERT INTO `students` VALUES (4, '202510237', 'Reyes, Marcus Gabriel', '202510237@gordoncollege.edu.ph', '1st Year', '202510237');
INSERT INTO `students` VALUES (5, '202510603', 'Romero, Kristian Paul', '202510603@gordoncollege.edu.ph', '1st Year', '202510603');
INSERT INTO `students` VALUES (6, '202513907', 'Pambid, Clark Justin', '202513907@gordoncollege.edu.ph', '1st Year', '202513907');

SET FOREIGN_KEY_CHECKS = 1;
