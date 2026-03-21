/*
 Navicat Premium Data Transfer

 Source Server         : local
 Source Server Type    : MySQL
 Source Server Version : 80030
 Source Host           : localhost:3306
 Source Schema         : medical_reg

 Target Server Type    : MySQL
 Target Server Version : 80030
 File Encoding         : 65001

 Date: 21/03/2026 16:40:10
*/

CREATE DATABASE IF NOT EXISTS medical_reg DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE medical_reg;

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for appointment
-- ----------------------------
DROP TABLE IF EXISTS `appointment`;
CREATE TABLE `appointment`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT,
  `appointment_no` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '预约单号(展示用)',
  `patient_id` bigint(0) NOT NULL COMMENT 'patient_profile.id',
  `doctor_id` bigint(0) NOT NULL COMMENT 'doctor_profile.id(可能因替诊变更)',
  `dept_id` bigint(0) NOT NULL COMMENT '科室ID(冗余便于统计)',
  `schedule_id` bigint(0) NOT NULL COMMENT 'schedule_slot.id',
  `visit_date` date NOT NULL COMMENT '就诊日期(冗余)',
  `checkin_code` char(6) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '6位预约签到码',
  `checkin_code_generated_at` datetime(0) NULL DEFAULT NULL COMMENT '签到码生成时间',
  `fee` decimal(10, 2) NOT NULL COMMENT '挂号费(下单时锁定)',
  `paid_at` datetime(0) NULL DEFAULT NULL,
  `canceled_at` datetime(0) NULL DEFAULT NULL,
  `refunded_at` datetime(0) NULL DEFAULT NULL,
  `checked_in_at` datetime(0) NULL DEFAULT NULL,
  `completed_at` datetime(0) NULL DEFAULT NULL,
  `no_show_marked_at` datetime(0) NULL DEFAULT NULL COMMENT '爽约判定时间',
  `cancel_reason` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `created_at` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0),
  `updated_at` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0),
  `status` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '字典编码:APPOINTMENT_STATUS',
  `time_slot` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '字典编码:TIME_SLOT',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_appointment_no`(`appointment_no`) USING BTREE,
  UNIQUE INDEX `uk_appt_date_checkin_code`(`visit_date`, `checkin_code`) USING BTREE,
  INDEX `idx_appt_dept_date`(`dept_id`, `visit_date`) USING BTREE,
  INDEX `idx_appt_visit_date_code`(`visit_date`, `checkin_code`) USING BTREE,
  INDEX `idx_appt_patient_date_slot`(`patient_id`, `visit_date`, `time_slot`) USING BTREE,
  INDEX `idx_appt_doctor_date_slot`(`doctor_id`, `visit_date`, `time_slot`) USING BTREE,
  INDEX `idx_appt_status`(`status`) USING BTREE,
  INDEX `fk_appointment_time_slot`(`time_slot`) USING BTREE,
  CONSTRAINT `fk_appointment_status` FOREIGN KEY (`status`) REFERENCES `dict_item` (`item_code`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fk_appointment_time_slot` FOREIGN KEY (`time_slot`) REFERENCES `dict_item` (`item_code`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '预约订单' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of appointment
-- ----------------------------
INSERT INTO `appointment` VALUES (1, 'T20260215A0001', 401, 201, 1, 7, '2026-02-15', '120015', '2026-02-10 10:00:00', 10.00, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2026-03-07 14:53:29', '2026-03-20 17:42:01', 'UNPAID', 'AM');
INSERT INTO `appointment` VALUES (2, 'T20260215A0002', 401, 201, 1, 8, '2026-02-15', '120016', '2026-02-10 10:05:00', 10.00, '2026-02-10 10:06:00', NULL, NULL, NULL, NULL, NULL, NULL, '2026-03-07 14:53:29', '2026-03-20 17:42:01', 'PAID', 'PM');
INSERT INTO `appointment` VALUES (3, 'T20260216B0001', 402, 202, 1, 13, '2026-02-16', '220001', '2026-02-10 11:00:00', 30.00, '2026-02-10 11:01:00', NULL, NULL, '2026-02-16 08:30:00', NULL, NULL, NULL, '2026-03-07 14:53:29', '2026-03-20 17:42:01', 'CHECKED_IN', 'AM');
INSERT INTO `appointment` VALUES (4, 'T20260217B0002', 402, 203, 2, 20, '2026-02-17', '330001', '2026-02-10 12:00:00', 10.00, '2026-02-10 12:01:00', NULL, NULL, '2026-02-17 13:10:00', '2026-02-17 15:00:00', NULL, NULL, '2026-03-07 14:53:29', '2026-03-20 17:42:01', 'COMPLETED', 'PM');
INSERT INTO `appointment` VALUES (5, 'AP202603071635528410', 402, 202, 1, 5, '2026-02-15', '016140', '2026-03-07 16:35:52', 30.00, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2026-03-07 16:35:52', '2026-03-20 17:42:01', 'UNPAID', 'AM');

-- ----------------------------
-- Table structure for checkin_queue
-- ----------------------------
DROP TABLE IF EXISTS `checkin_queue`;
CREATE TABLE `checkin_queue`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT,
  `appointment_id` bigint(0) NOT NULL COMMENT 'appointment.id',
  `doctor_id` bigint(0) NOT NULL COMMENT 'doctor_profile.id',
  `dept_id` bigint(0) NOT NULL,
  `visit_date` date NOT NULL,
  `queue_no` int(0) NOT NULL COMMENT '排队号(从1开始)',
  `checked_in_at` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0),
  `called_at` datetime(0) NULL DEFAULT NULL,
  `done_at` datetime(0) NULL DEFAULT NULL,
  `status` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'WAITING' COMMENT '字典编码:QUEUE_STATUS',
  `time_slot` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '字典编码:TIME_SLOT',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_queue_appt`(`appointment_id`) USING BTREE,
  INDEX `idx_queue_doctor_slot`(`doctor_id`, `visit_date`, `time_slot`, `queue_no`) USING BTREE,
  INDEX `idx_queue_status`(`status`) USING BTREE,
  INDEX `fk_checkin_queue_time_slot`(`time_slot`) USING BTREE,
  CONSTRAINT `fk_checkin_queue_status` FOREIGN KEY (`status`) REFERENCES `dict_item` (`item_code`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fk_checkin_queue_time_slot` FOREIGN KEY (`time_slot`) REFERENCES `dict_item` (`item_code`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '签到排队队列' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of checkin_queue
-- ----------------------------
INSERT INTO `checkin_queue` VALUES (1, 3, 202, 1, '2026-02-16', 1, '2026-02-16 08:30:00', NULL, NULL, 'WAITING', 'AM');

-- ----------------------------
-- Table structure for department
-- ----------------------------
DROP TABLE IF EXISTS `department`;
CREATE TABLE `department`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT,
  `name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '科室名称',
  `code` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '科室编码/拼音码',
  `location_desc` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '位置描述(如门诊楼3层A区)',
  `floor` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '楼层信息',
  `status` tinyint(0) NOT NULL DEFAULT 1 COMMENT '1启用 0停用',
  `created_at` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0),
  `updated_at` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0),
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_department_name`(`name`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '科室表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of department
-- ----------------------------
INSERT INTO `department` VALUES (1, '内科', 'NK', '门诊楼A区', '3F', 1, '2026-02-26 21:38:52', '2026-02-26 21:38:52');
INSERT INTO `department` VALUES (2, '外科', 'WK', '门诊楼B区', '2F', 1, '2026-02-26 21:38:52', '2026-02-26 21:38:52');
INSERT INTO `department` VALUES (3, '儿科', 'EK', '门诊楼C区', '1F', 1, '2026-02-26 21:38:52', '2026-02-26 21:38:52');

-- ----------------------------
-- Table structure for dict_item
-- ----------------------------
DROP TABLE IF EXISTS `dict_item`;
CREATE TABLE `dict_item`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT,
  `type_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '关联dict_type.type_code',
  `item_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '字典值编码（业务字段存这个）',
  `item_name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '字典显示名',
  `sort_no` int(0) NOT NULL DEFAULT 0,
  `status` tinyint(0) NOT NULL DEFAULT 1,
  `is_default` tinyint(0) NOT NULL DEFAULT 0,
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `created_at` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0),
  `updated_at` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0),
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_dict_item_code`(`item_code`) USING BTREE,
  INDEX `idx_dict_item_type_status`(`type_code`, `status`) USING BTREE,
  CONSTRAINT `fk_dict_item_type_code` FOREIGN KEY (`type_code`) REFERENCES `dict_type` (`type_code`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 30 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '字典项表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of dict_item
-- ----------------------------
INSERT INTO `dict_item` VALUES (1, 'DOCTOR_TITLE', 'RESIDENT', '住院医师', 10, 1, 0, NULL, '2026-03-20 17:42:01', '2026-03-20 17:42:01');
INSERT INTO `dict_item` VALUES (2, 'DOCTOR_TITLE', 'ATTENDING', '主治医师', 20, 1, 0, NULL, '2026-03-20 17:42:01', '2026-03-20 17:42:01');
INSERT INTO `dict_item` VALUES (3, 'DOCTOR_TITLE', 'ASSOC_CHIEF', '副主任医师', 30, 1, 0, NULL, '2026-03-20 17:42:01', '2026-03-20 17:42:01');
INSERT INTO `dict_item` VALUES (4, 'DOCTOR_TITLE', 'CHIEF', '主任医师', 40, 1, 0, NULL, '2026-03-20 17:42:01', '2026-03-20 17:42:01');
INSERT INTO `dict_item` VALUES (5, 'DOCTOR_TYPE', 'GENERAL', '普通', 10, 1, 0, NULL, '2026-03-20 17:42:01', '2026-03-20 17:42:01');
INSERT INTO `dict_item` VALUES (6, 'DOCTOR_TYPE', 'EXPERT', '专家', 20, 1, 0, NULL, '2026-03-20 17:42:01', '2026-03-20 17:42:01');
INSERT INTO `dict_item` VALUES (7, 'DOCTOR_STATUS', 'ACTIVE', '在职', 10, 1, 0, NULL, '2026-03-20 17:42:01', '2026-03-20 17:42:01');
INSERT INTO `dict_item` VALUES (8, 'DOCTOR_STATUS', 'INACTIVE', '停用', 20, 1, 0, NULL, '2026-03-20 17:42:01', '2026-03-20 17:42:01');
INSERT INTO `dict_item` VALUES (9, 'APPOINTMENT_STATUS', 'UNPAID', '待支付', 10, 1, 0, NULL, '2026-03-20 17:42:01', '2026-03-20 17:42:01');
INSERT INTO `dict_item` VALUES (10, 'APPOINTMENT_STATUS', 'PAID', '已支付', 20, 1, 0, NULL, '2026-03-20 17:42:01', '2026-03-20 17:42:01');
INSERT INTO `dict_item` VALUES (11, 'APPOINTMENT_STATUS', 'CANCELED', '已取消', 30, 1, 0, NULL, '2026-03-20 17:42:01', '2026-03-20 17:42:01');
INSERT INTO `dict_item` VALUES (12, 'APPOINTMENT_STATUS', 'REFUNDED', '已退款', 40, 1, 0, NULL, '2026-03-20 17:42:01', '2026-03-20 17:42:01');
INSERT INTO `dict_item` VALUES (13, 'APPOINTMENT_STATUS', 'CHECKED_IN', '已签到', 50, 1, 0, NULL, '2026-03-20 17:42:01', '2026-03-20 17:42:01');
INSERT INTO `dict_item` VALUES (14, 'APPOINTMENT_STATUS', 'COMPLETED', '已完成', 60, 1, 0, NULL, '2026-03-20 17:42:01', '2026-03-20 17:42:01');
INSERT INTO `dict_item` VALUES (15, 'APPOINTMENT_STATUS', 'NO_SHOW', '爽约', 70, 1, 0, NULL, '2026-03-20 17:42:01', '2026-03-20 17:42:01');
INSERT INTO `dict_item` VALUES (16, 'TIME_SLOT', 'AM', '上午', 10, 1, 0, NULL, '2026-03-20 17:42:01', '2026-03-20 17:42:01');
INSERT INTO `dict_item` VALUES (17, 'TIME_SLOT', 'PM', '下午', 20, 1, 0, NULL, '2026-03-20 17:42:01', '2026-03-20 17:42:01');
INSERT INTO `dict_item` VALUES (18, 'SCHEDULE_STATUS', 'OPEN', '开放', 10, 1, 0, NULL, '2026-03-20 17:42:01', '2026-03-20 17:42:01');
INSERT INTO `dict_item` VALUES (19, 'SCHEDULE_STATUS', 'CLOSED', '关闭', 20, 1, 0, NULL, '2026-03-20 17:42:01', '2026-03-20 17:42:01');
INSERT INTO `dict_item` VALUES (20, 'SCHEDULE_STATUS', 'FULL', '已满', 30, 1, 0, NULL, '2026-03-20 17:42:01', '2026-03-20 17:42:01');
INSERT INTO `dict_item` VALUES (21, 'QUEUE_STATUS', 'WAITING', '等待中', 10, 1, 0, NULL, '2026-03-20 17:42:01', '2026-03-20 17:42:01');
INSERT INTO `dict_item` VALUES (22, 'QUEUE_STATUS', 'CALLED', '已叫号', 20, 1, 0, NULL, '2026-03-20 17:42:01', '2026-03-20 17:42:01');
INSERT INTO `dict_item` VALUES (23, 'QUEUE_STATUS', 'SKIPPED', '过号', 30, 1, 0, NULL, '2026-03-20 17:42:01', '2026-03-20 17:42:01');
INSERT INTO `dict_item` VALUES (24, 'QUEUE_STATUS', 'DONE', '完成', 40, 1, 0, NULL, '2026-03-20 17:42:01', '2026-03-20 17:42:01');
INSERT INTO `dict_item` VALUES (25, 'USER_ROLE', 'PATIENT', '患者', 10, 1, 0, NULL, '2026-03-20 17:42:01', '2026-03-20 17:42:01');
INSERT INTO `dict_item` VALUES (26, 'USER_ROLE', 'DOCTOR', '医生', 20, 1, 0, NULL, '2026-03-20 17:42:01', '2026-03-20 17:42:01');
INSERT INTO `dict_item` VALUES (27, 'USER_ROLE', 'ADMIN', '管理员', 30, 1, 0, NULL, '2026-03-20 17:42:01', '2026-03-20 17:42:01');
INSERT INTO `dict_item` VALUES (28, 'ADMIN_SCOPE_TYPE', 'DEPT', '科室级', 10, 1, 0, NULL, '2026-03-20 17:42:01', '2026-03-20 17:42:01');
INSERT INTO `dict_item` VALUES (29, 'ADMIN_SCOPE_TYPE', 'HOSPITAL', '全院级', 20, 1, 0, NULL, '2026-03-20 17:42:01', '2026-03-20 17:42:01');

-- ----------------------------
-- Table structure for dict_type
-- ----------------------------
DROP TABLE IF EXISTS `dict_type`;
CREATE TABLE `dict_type`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT,
  `type_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '字典类型编码',
  `type_name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '字典类型名称',
  `status` tinyint(0) NOT NULL DEFAULT 1 COMMENT '1启用 0停用',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `created_at` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0),
  `updated_at` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0),
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_dict_type_code`(`type_code`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 10 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '字典类型表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of dict_type
-- ----------------------------
INSERT INTO `dict_type` VALUES (1, 'DOCTOR_TITLE', '医生职称', 1, 'doctor_profile.title', '2026-03-20 17:42:01', '2026-03-20 17:42:01');
INSERT INTO `dict_type` VALUES (2, 'DOCTOR_TYPE', '医生号别', 1, 'doctor_profile.doctor_type', '2026-03-20 17:42:01', '2026-03-20 17:42:01');
INSERT INTO `dict_type` VALUES (3, 'DOCTOR_STATUS', '医生状态', 1, 'doctor_profile.status', '2026-03-20 17:42:01', '2026-03-20 17:42:01');
INSERT INTO `dict_type` VALUES (4, 'APPOINTMENT_STATUS', '预约状态', 1, 'appointment.status', '2026-03-20 17:42:01', '2026-03-20 17:42:01');
INSERT INTO `dict_type` VALUES (5, 'TIME_SLOT', '时段', 1, 'AM/PM', '2026-03-20 17:42:01', '2026-03-20 17:42:01');
INSERT INTO `dict_type` VALUES (6, 'SCHEDULE_STATUS', '排班状态', 1, 'schedule_slot.status', '2026-03-20 17:42:01', '2026-03-20 17:42:01');
INSERT INTO `dict_type` VALUES (7, 'QUEUE_STATUS', '排队状态', 1, 'checkin_queue.status', '2026-03-20 17:42:01', '2026-03-20 17:42:01');
INSERT INTO `dict_type` VALUES (8, 'USER_ROLE', '用户角色', 1, 'sys_user.role', '2026-03-20 17:42:01', '2026-03-20 17:42:01');
INSERT INTO `dict_type` VALUES (9, 'ADMIN_SCOPE_TYPE', '管理员数据范围', 1, 'sys_user.admin_scope_type', '2026-03-20 17:42:01', '2026-03-20 17:42:01');

-- ----------------------------
-- Table structure for doctor_profile
-- ----------------------------
DROP TABLE IF EXISTS `doctor_profile`;
CREATE TABLE `doctor_profile`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(0) NOT NULL COMMENT '关联sys_user.id',
  `dept_id` bigint(0) NOT NULL COMMENT '所属科室department.id',
  `real_name` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '医生姓名',
  `intro` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '简介',
  `avg_rating` decimal(3, 2) NOT NULL DEFAULT 0.00 COMMENT '综合评分均值(1.00~5.00)',
  `rating_count` int(0) NOT NULL DEFAULT 0 COMMENT '评分次数',
  `created_at` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0),
  `updated_at` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0),
  `title` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '字典编码:DOCTOR_TITLE',
  `doctor_type` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'GENERAL' COMMENT '字典编码:DOCTOR_TYPE',
  `status` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'ACTIVE' COMMENT '字典编码:DOCTOR_STATUS',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_doctor_user`(`user_id`) USING BTREE,
  INDEX `idx_doctor_dept`(`dept_id`) USING BTREE,
  INDEX `idx_doctor_rating`(`avg_rating`, `rating_count`) USING BTREE,
  INDEX `fk_doctor_profile_title`(`title`) USING BTREE,
  INDEX `fk_doctor_profile_doctor_type`(`doctor_type`) USING BTREE,
  INDEX `fk_doctor_profile_status`(`status`) USING BTREE,
  CONSTRAINT `fk_doctor_profile_doctor_type` FOREIGN KEY (`doctor_type`) REFERENCES `dict_item` (`item_code`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fk_doctor_profile_status` FOREIGN KEY (`status`) REFERENCES `dict_item` (`item_code`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fk_doctor_profile_title` FOREIGN KEY (`title`) REFERENCES `dict_item` (`item_code`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 205 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '医生档案' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of doctor_profile
-- ----------------------------
INSERT INTO `doctor_profile` VALUES (1, 3, 1, '张医生', '擅长常见内科疾病诊疗', 0.00, 0, '2026-02-26 21:38:52', '2026-03-20 17:42:01', 'ATTENDING', 'GENERAL', 'ACTIVE');
INSERT INTO `doctor_profile` VALUES (2, 4, 1, '李医生', '擅长疑难内科病例诊疗', 0.00, 0, '2026-02-26 21:38:52', '2026-03-20 17:42:01', 'ASSOC_CHIEF', 'EXPERT', 'ACTIVE');
INSERT INTO `doctor_profile` VALUES (201, 101, 1, '赵内科', '内科常见病诊疗', 0.00, 0, '2026-03-07 14:53:29', '2026-03-20 17:42:01', 'ATTENDING', 'GENERAL', 'ACTIVE');
INSERT INTO `doctor_profile` VALUES (202, 102, 1, '钱专家', '疑难内科诊疗', 0.00, 0, '2026-03-07 14:53:29', '2026-03-20 17:42:01', 'ASSOC_CHIEF', 'EXPERT', 'ACTIVE');
INSERT INTO `doctor_profile` VALUES (203, 103, 2, '孙外科', '外科常见病诊疗', 5.00, 1, '2026-03-07 14:53:29', '2026-03-20 17:42:01', 'ATTENDING', 'GENERAL', 'ACTIVE');
INSERT INTO `doctor_profile` VALUES (204, 104, 3, '李儿科', '儿科常见病诊疗', 0.00, 0, '2026-03-07 14:53:29', '2026-03-20 17:42:01', 'ATTENDING', 'GENERAL', 'ACTIVE');

-- ----------------------------
-- Table structure for doctor_rating
-- ----------------------------
DROP TABLE IF EXISTS `doctor_rating`;
CREATE TABLE `doctor_rating`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT,
  `appointment_id` bigint(0) NOT NULL COMMENT '预约单id(一单一评)',
  `doctor_id` bigint(0) NOT NULL,
  `patient_id` bigint(0) NOT NULL,
  `score` tinyint(0) NOT NULL COMMENT '评分1~5',
  `created_at` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0),
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_rating_appointment`(`appointment_id`) USING BTREE,
  INDEX `idx_rating_doctor`(`doctor_id`, `created_at`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '医生评分(仅评分不评价)' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of doctor_rating
-- ----------------------------
INSERT INTO `doctor_rating` VALUES (1, 4, 203, 402, 5, '2026-02-17 16:00:00');

-- ----------------------------
-- Table structure for notify_log
-- ----------------------------
DROP TABLE IF EXISTS `notify_log`;
CREATE TABLE `notify_log`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT,
  `biz_type` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'APPOINTMENT/SHIFT/QUEUE等',
  `biz_id` bigint(0) NOT NULL COMMENT '业务ID，如appointment.id或shift_request.id',
  `receiver_user_id` bigint(0) NOT NULL COMMENT '接收人sys_user.id',
  `channel` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'SMS/PUSH/MOCK',
  `template_code` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '模板编号(可选)',
  `content` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '发送内容(模拟可存)',
  `status` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'INIT/SENT/FAILED',
  `sent_at` datetime(0) NULL DEFAULT NULL,
  `error_msg` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `created_at` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0),
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_notify_biz`(`biz_type`, `biz_id`) USING BTREE,
  INDEX `idx_notify_receiver`(`receiver_user_id`, `created_at`) USING BTREE,
  INDEX `idx_notify_status`(`status`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '消息提醒发送日志' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for patient_profile
-- ----------------------------
DROP TABLE IF EXISTS `patient_profile`;
CREATE TABLE `patient_profile`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(0) NOT NULL COMMENT '关联sys_user.id',
  `real_name` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '姓名',
  `gender` varchar(8) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '男/女/未知',
  `birth_date` date NULL DEFAULT NULL COMMENT '出生日期',
  `id_card_no` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '身份证号',
  `province` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '省',
  `city` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '市',
  `created_at` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0),
  `updated_at` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0),
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_patient_user`(`user_id`) USING BTREE,
  INDEX `idx_patient_province_city`(`province`, `city`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 404 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '患者档案' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of patient_profile
-- ----------------------------
INSERT INTO `patient_profile` VALUES (1, 5, '王同学', '男', '2003-01-01', '360731200403017637', '陕西省', '西安市', '2026-02-26 21:38:52', '2026-03-13 21:56:56');
INSERT INTO `patient_profile` VALUES (401, 301, '测试患者A', '男', '2003-01-01', '360731200403017636', '陕西省', '西安市', '2026-03-07 14:53:29', '2026-03-13 21:56:47');
INSERT INTO `patient_profile` VALUES (402, 302, '测试患者B', '女', '2002-02-02', '360731200403017638', '陕西省', '咸阳市', '2026-03-07 14:53:29', '2026-03-13 21:57:06');
INSERT INTO `patient_profile` VALUES (403, 303, '遇柏', '女', '2005-03-16', '370741200503167823', '江西省', '南昌市', '2026-03-16 17:50:05', '2026-03-16 17:50:05');

-- ----------------------------
-- Table structure for payment_record
-- ----------------------------
DROP TABLE IF EXISTS `payment_record`;
CREATE TABLE `payment_record`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT,
  `appointment_id` bigint(0) NOT NULL COMMENT 'appointment.id',
  `pay_no` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '支付单号',
  `channel` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'ALIPAY/WECHAT/MOCK',
  `amount` decimal(10, 2) NOT NULL,
  `status` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'INIT/SUCCESS/FAILED/CLOSED',
  `third_trade_no` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '第三方交易号(模拟可空)',
  `paid_at` datetime(0) NULL DEFAULT NULL,
  `created_at` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0),
  `updated_at` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0),
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_pay_no`(`pay_no`) USING BTREE,
  INDEX `idx_pay_appt`(`appointment_id`) USING BTREE,
  INDEX `idx_pay_status`(`status`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '支付记录' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of payment_record
-- ----------------------------
INSERT INTO `payment_record` VALUES (1, 2, 'PAYT20260215A0002', 'MOCK', 10.00, 'SUCCESS', NULL, '2026-02-10 10:06:00', '2026-03-07 14:53:29', '2026-03-07 14:53:29');
INSERT INTO `payment_record` VALUES (2, 3, 'PAYT20260216B0001', 'MOCK', 30.00, 'SUCCESS', NULL, '2026-02-10 11:01:00', '2026-03-07 14:53:29', '2026-03-07 14:53:29');
INSERT INTO `payment_record` VALUES (3, 4, 'PAYT20260217B0002', 'MOCK', 10.00, 'SUCCESS', NULL, '2026-02-10 12:01:00', '2026-03-07 14:53:29', '2026-03-07 14:53:29');

-- ----------------------------
-- Table structure for queue_state
-- ----------------------------
DROP TABLE IF EXISTS `queue_state`;
CREATE TABLE `queue_state`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT,
  `doctor_id` bigint(0) NOT NULL,
  `visit_date` date NOT NULL,
  `time_slot` varchar(8) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `current_call_no` int(0) NOT NULL DEFAULT 0 COMMENT '当前叫号号',
  `updated_by` bigint(0) NULL DEFAULT NULL COMMENT '操作人user_id(医生/管理员)',
  `updated_at` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0),
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_queue_state`(`doctor_id`, `visit_date`, `time_slot`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '叫号状态' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for refund_record
-- ----------------------------
DROP TABLE IF EXISTS `refund_record`;
CREATE TABLE `refund_record`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT,
  `appointment_id` bigint(0) NOT NULL COMMENT 'appointment.id',
  `refund_no` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '退款单号',
  `channel` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'ALIPAY/WECHAT/MOCK',
  `amount` decimal(10, 2) NOT NULL,
  `status` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'INIT/SUCCESS/FAILED',
  `reason` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'CANCEL/NO_SHOW/AUTO_REFUND/SHIFT_CANCEL等',
  `third_refund_no` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `refunded_at` datetime(0) NULL DEFAULT NULL,
  `created_at` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0),
  `updated_at` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0),
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_refund_no`(`refund_no`) USING BTREE,
  INDEX `idx_refund_appt`(`appointment_id`) USING BTREE,
  INDEX `idx_refund_status`(`status`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '退款记录' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for schedule_slot
-- ----------------------------
DROP TABLE IF EXISTS `schedule_slot`;
CREATE TABLE `schedule_slot`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT,
  `doctor_id` bigint(0) NOT NULL COMMENT 'doctor_profile.id',
  `dept_id` bigint(0) NOT NULL COMMENT '冗余: 科室ID便于统计与权限过滤',
  `visit_date` date NOT NULL COMMENT '出诊日期',
  `capacity` int(0) NOT NULL COMMENT '号源容量',
  `remaining` int(0) NOT NULL COMMENT '剩余号源',
  `fee` decimal(10, 2) NOT NULL COMMENT '挂号费(下单时可复制到订单)',
  `open_time` datetime(0) NULL DEFAULT NULL COMMENT '放号时间(可选)',
  `stop_time` datetime(0) NULL DEFAULT NULL COMMENT '停号时间(可选)',
  `created_by` bigint(0) NULL DEFAULT NULL COMMENT '创建人(管理员user_id)',
  `created_at` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0),
  `updated_at` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0),
  `status` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'OPEN' COMMENT '字典编码:SCHEDULE_STATUS',
  `time_slot` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '字典编码:TIME_SLOT',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_schedule_doctor_date_slot`(`doctor_id`, `visit_date`, `time_slot`) USING BTREE,
  INDEX `idx_schedule_dept_date`(`dept_id`, `visit_date`) USING BTREE,
  INDEX `idx_schedule_status`(`status`) USING BTREE,
  INDEX `fk_schedule_slot_time_slot`(`time_slot`) USING BTREE,
  CONSTRAINT `fk_schedule_slot_status` FOREIGN KEY (`status`) REFERENCES `dict_item` (`item_code`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fk_schedule_slot_time_slot` FOREIGN KEY (`time_slot`) REFERENCES `dict_item` (`item_code`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 64 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '医生排班与号源(按天+时段)' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of schedule_slot
-- ----------------------------
INSERT INTO `schedule_slot` VALUES (1, 204, 3, '2026-02-15', 20, 20, 10.00, '2026-02-15 08:00:00', '2026-02-15 11:00:00', NULL, '2026-03-07 14:53:29', '2026-03-20 17:42:02', 'OPEN', 'AM');
INSERT INTO `schedule_slot` VALUES (2, 204, 3, '2026-02-15', 20, 20, 10.00, '2026-02-15 08:00:00', '2026-02-15 11:00:00', NULL, '2026-03-07 14:53:29', '2026-03-20 17:42:02', 'OPEN', 'PM');
INSERT INTO `schedule_slot` VALUES (3, 203, 2, '2026-02-15', 20, 20, 10.00, '2026-02-15 08:00:00', '2026-02-15 11:00:00', NULL, '2026-03-07 14:53:29', '2026-03-20 17:42:02', 'OPEN', 'AM');
INSERT INTO `schedule_slot` VALUES (4, 203, 2, '2026-02-15', 20, 20, 10.00, '2026-02-15 08:00:00', '2026-02-15 11:00:00', NULL, '2026-03-07 14:53:29', '2026-03-20 17:42:02', 'OPEN', 'PM');
INSERT INTO `schedule_slot` VALUES (5, 202, 1, '2026-02-15', 10, 9, 30.00, '2026-02-15 08:00:00', '2026-02-15 11:00:00', NULL, '2026-03-07 14:53:29', '2026-03-20 17:42:02', 'OPEN', 'AM');
INSERT INTO `schedule_slot` VALUES (6, 202, 1, '2026-02-15', 10, 10, 30.00, '2026-02-15 08:00:00', '2026-02-15 11:00:00', NULL, '2026-03-07 14:53:29', '2026-03-20 17:42:02', 'OPEN', 'PM');
INSERT INTO `schedule_slot` VALUES (7, 201, 1, '2026-02-15', 20, 19, 10.00, '2026-02-15 08:00:00', '2026-02-15 11:00:00', NULL, '2026-03-07 14:53:29', '2026-03-20 17:42:02', 'OPEN', 'AM');
INSERT INTO `schedule_slot` VALUES (8, 201, 1, '2026-02-15', 20, 19, 10.00, '2026-02-15 08:00:00', '2026-02-15 11:00:00', NULL, '2026-03-07 14:53:29', '2026-03-20 17:42:02', 'OPEN', 'PM');
INSERT INTO `schedule_slot` VALUES (9, 204, 3, '2026-02-16', 20, 20, 10.00, '2026-02-16 08:00:00', '2026-02-16 11:00:00', NULL, '2026-03-07 14:53:29', '2026-03-20 17:42:02', 'OPEN', 'AM');
INSERT INTO `schedule_slot` VALUES (10, 204, 3, '2026-02-16', 20, 20, 10.00, '2026-02-16 08:00:00', '2026-02-16 11:00:00', NULL, '2026-03-07 14:53:29', '2026-03-20 17:42:02', 'OPEN', 'PM');
INSERT INTO `schedule_slot` VALUES (11, 203, 2, '2026-02-16', 20, 20, 10.00, '2026-02-16 08:00:00', '2026-02-16 11:00:00', NULL, '2026-03-07 14:53:29', '2026-03-20 17:42:02', 'OPEN', 'AM');
INSERT INTO `schedule_slot` VALUES (12, 203, 2, '2026-02-16', 20, 20, 10.00, '2026-02-16 08:00:00', '2026-02-16 11:00:00', NULL, '2026-03-07 14:53:29', '2026-03-20 17:42:02', 'OPEN', 'PM');
INSERT INTO `schedule_slot` VALUES (13, 202, 1, '2026-02-16', 10, 9, 30.00, '2026-02-16 08:00:00', '2026-02-16 11:00:00', NULL, '2026-03-07 14:53:29', '2026-03-20 17:42:02', 'OPEN', 'AM');
INSERT INTO `schedule_slot` VALUES (14, 202, 1, '2026-02-16', 10, 10, 30.00, '2026-02-16 08:00:00', '2026-02-16 11:00:00', NULL, '2026-03-07 14:53:29', '2026-03-20 17:42:02', 'OPEN', 'PM');
INSERT INTO `schedule_slot` VALUES (15, 201, 1, '2026-02-16', 20, 20, 10.00, '2026-02-16 08:00:00', '2026-02-16 11:00:00', NULL, '2026-03-07 14:53:29', '2026-03-20 17:42:02', 'OPEN', 'AM');
INSERT INTO `schedule_slot` VALUES (16, 201, 1, '2026-02-16', 20, 20, 10.00, '2026-02-16 08:00:00', '2026-02-16 11:00:00', NULL, '2026-03-07 14:53:29', '2026-03-20 17:42:02', 'OPEN', 'PM');
INSERT INTO `schedule_slot` VALUES (17, 204, 3, '2026-02-17', 20, 20, 10.00, '2026-02-17 08:00:00', '2026-02-17 11:00:00', NULL, '2026-03-07 14:53:29', '2026-03-20 17:42:02', 'OPEN', 'AM');
INSERT INTO `schedule_slot` VALUES (18, 204, 3, '2026-02-17', 20, 20, 10.00, '2026-02-17 08:00:00', '2026-02-17 11:00:00', NULL, '2026-03-07 14:53:29', '2026-03-20 17:42:02', 'OPEN', 'PM');
INSERT INTO `schedule_slot` VALUES (19, 203, 2, '2026-02-17', 20, 20, 10.00, '2026-02-17 08:00:00', '2026-02-17 11:00:00', NULL, '2026-03-07 14:53:29', '2026-03-20 17:42:02', 'OPEN', 'AM');
INSERT INTO `schedule_slot` VALUES (20, 203, 2, '2026-02-17', 20, 19, 10.00, '2026-02-17 08:00:00', '2026-02-17 11:00:00', NULL, '2026-03-07 14:53:29', '2026-03-20 17:42:02', 'OPEN', 'PM');
INSERT INTO `schedule_slot` VALUES (21, 202, 1, '2026-02-17', 10, 10, 30.00, '2026-02-17 08:00:00', '2026-02-17 11:00:00', NULL, '2026-03-07 14:53:29', '2026-03-20 17:42:02', 'OPEN', 'AM');
INSERT INTO `schedule_slot` VALUES (22, 202, 1, '2026-02-17', 10, 10, 30.00, '2026-02-17 08:00:00', '2026-02-17 11:00:00', NULL, '2026-03-07 14:53:29', '2026-03-20 17:42:02', 'OPEN', 'PM');
INSERT INTO `schedule_slot` VALUES (23, 201, 1, '2026-02-17', 20, 20, 10.00, '2026-02-17 08:00:00', '2026-02-17 11:00:00', NULL, '2026-03-07 14:53:29', '2026-03-20 17:42:02', 'OPEN', 'AM');
INSERT INTO `schedule_slot` VALUES (24, 201, 1, '2026-02-17', 20, 20, 10.00, '2026-02-17 08:00:00', '2026-02-17 11:00:00', NULL, '2026-03-07 14:53:29', '2026-03-20 17:42:02', 'OPEN', 'PM');
INSERT INTO `schedule_slot` VALUES (25, 204, 3, '2026-02-18', 20, 20, 10.00, '2026-02-18 08:00:00', '2026-02-18 11:00:00', NULL, '2026-03-07 14:53:29', '2026-03-20 17:42:02', 'OPEN', 'AM');
INSERT INTO `schedule_slot` VALUES (26, 204, 3, '2026-02-18', 20, 20, 10.00, '2026-02-18 08:00:00', '2026-02-18 11:00:00', NULL, '2026-03-07 14:53:29', '2026-03-20 17:42:02', 'OPEN', 'PM');
INSERT INTO `schedule_slot` VALUES (27, 203, 2, '2026-02-18', 20, 20, 10.00, '2026-02-18 08:00:00', '2026-02-18 11:00:00', NULL, '2026-03-07 14:53:29', '2026-03-20 17:42:02', 'OPEN', 'AM');
INSERT INTO `schedule_slot` VALUES (28, 203, 2, '2026-02-18', 20, 20, 10.00, '2026-02-18 08:00:00', '2026-02-18 11:00:00', NULL, '2026-03-07 14:53:29', '2026-03-20 17:42:02', 'OPEN', 'PM');
INSERT INTO `schedule_slot` VALUES (29, 202, 1, '2026-02-18', 10, 10, 30.00, '2026-02-18 08:00:00', '2026-02-18 11:00:00', NULL, '2026-03-07 14:53:29', '2026-03-20 17:42:02', 'OPEN', 'AM');
INSERT INTO `schedule_slot` VALUES (30, 202, 1, '2026-02-18', 10, 10, 30.00, '2026-02-18 08:00:00', '2026-02-18 11:00:00', NULL, '2026-03-07 14:53:29', '2026-03-20 17:42:02', 'OPEN', 'PM');
INSERT INTO `schedule_slot` VALUES (31, 201, 1, '2026-02-18', 20, 20, 10.00, '2026-02-18 08:00:00', '2026-02-18 11:00:00', NULL, '2026-03-07 14:53:29', '2026-03-20 17:42:02', 'OPEN', 'AM');
INSERT INTO `schedule_slot` VALUES (32, 201, 1, '2026-02-18', 20, 20, 10.00, '2026-02-18 08:00:00', '2026-02-18 11:00:00', NULL, '2026-03-07 14:53:29', '2026-03-20 17:42:02', 'OPEN', 'PM');
INSERT INTO `schedule_slot` VALUES (33, 204, 3, '2026-02-19', 20, 20, 10.00, '2026-02-19 08:00:00', '2026-02-19 11:00:00', NULL, '2026-03-07 14:53:29', '2026-03-20 17:42:02', 'OPEN', 'AM');
INSERT INTO `schedule_slot` VALUES (34, 204, 3, '2026-02-19', 20, 20, 10.00, '2026-02-19 08:00:00', '2026-02-19 11:00:00', NULL, '2026-03-07 14:53:29', '2026-03-20 17:42:02', 'OPEN', 'PM');
INSERT INTO `schedule_slot` VALUES (35, 203, 2, '2026-02-19', 20, 20, 10.00, '2026-02-19 08:00:00', '2026-02-19 11:00:00', NULL, '2026-03-07 14:53:29', '2026-03-20 17:42:02', 'OPEN', 'AM');
INSERT INTO `schedule_slot` VALUES (36, 203, 2, '2026-02-19', 20, 20, 10.00, '2026-02-19 08:00:00', '2026-02-19 11:00:00', NULL, '2026-03-07 14:53:29', '2026-03-20 17:42:02', 'OPEN', 'PM');
INSERT INTO `schedule_slot` VALUES (37, 202, 1, '2026-02-19', 10, 10, 30.00, '2026-02-19 08:00:00', '2026-02-19 11:00:00', NULL, '2026-03-07 14:53:29', '2026-03-20 17:42:02', 'OPEN', 'AM');
INSERT INTO `schedule_slot` VALUES (38, 202, 1, '2026-02-19', 10, 10, 30.00, '2026-02-19 08:00:00', '2026-02-19 11:00:00', NULL, '2026-03-07 14:53:29', '2026-03-20 17:42:02', 'OPEN', 'PM');
INSERT INTO `schedule_slot` VALUES (39, 201, 1, '2026-02-19', 20, 20, 10.00, '2026-02-19 08:00:00', '2026-02-19 11:00:00', NULL, '2026-03-07 14:53:29', '2026-03-20 17:42:02', 'OPEN', 'AM');
INSERT INTO `schedule_slot` VALUES (40, 201, 1, '2026-02-19', 20, 20, 10.00, '2026-02-19 08:00:00', '2026-02-19 11:00:00', NULL, '2026-03-07 14:53:29', '2026-03-20 17:42:02', 'OPEN', 'PM');
INSERT INTO `schedule_slot` VALUES (41, 204, 3, '2026-02-20', 20, 20, 10.00, '2026-02-20 08:00:00', '2026-02-20 11:00:00', NULL, '2026-03-07 14:53:29', '2026-03-20 17:42:02', 'OPEN', 'AM');
INSERT INTO `schedule_slot` VALUES (42, 204, 3, '2026-02-20', 20, 20, 10.00, '2026-02-20 08:00:00', '2026-02-20 11:00:00', NULL, '2026-03-07 14:53:29', '2026-03-20 17:42:02', 'OPEN', 'PM');
INSERT INTO `schedule_slot` VALUES (43, 203, 2, '2026-02-20', 20, 20, 10.00, '2026-02-20 08:00:00', '2026-02-20 11:00:00', NULL, '2026-03-07 14:53:29', '2026-03-20 17:42:02', 'OPEN', 'AM');
INSERT INTO `schedule_slot` VALUES (44, 203, 2, '2026-02-20', 20, 20, 10.00, '2026-02-20 08:00:00', '2026-02-20 11:00:00', NULL, '2026-03-07 14:53:29', '2026-03-20 17:42:02', 'OPEN', 'PM');
INSERT INTO `schedule_slot` VALUES (45, 202, 1, '2026-02-20', 10, 10, 30.00, '2026-02-20 08:00:00', '2026-02-20 11:00:00', NULL, '2026-03-07 14:53:29', '2026-03-20 17:42:02', 'OPEN', 'AM');
INSERT INTO `schedule_slot` VALUES (46, 202, 1, '2026-02-20', 10, 10, 30.00, '2026-02-20 08:00:00', '2026-02-20 11:00:00', NULL, '2026-03-07 14:53:29', '2026-03-20 17:42:02', 'OPEN', 'PM');
INSERT INTO `schedule_slot` VALUES (47, 201, 1, '2026-02-20', 20, 20, 10.00, '2026-02-20 08:00:00', '2026-02-20 11:00:00', NULL, '2026-03-07 14:53:29', '2026-03-20 17:42:02', 'OPEN', 'AM');
INSERT INTO `schedule_slot` VALUES (48, 201, 1, '2026-02-20', 20, 20, 10.00, '2026-02-20 08:00:00', '2026-02-20 11:00:00', NULL, '2026-03-07 14:53:29', '2026-03-20 17:42:02', 'OPEN', 'PM');
INSERT INTO `schedule_slot` VALUES (49, 204, 3, '2026-02-21', 20, 20, 10.00, '2026-02-21 08:00:00', '2026-02-21 11:00:00', NULL, '2026-03-07 14:53:29', '2026-03-20 17:42:02', 'OPEN', 'AM');
INSERT INTO `schedule_slot` VALUES (50, 204, 3, '2026-02-21', 20, 20, 10.00, '2026-02-21 08:00:00', '2026-02-21 11:00:00', NULL, '2026-03-07 14:53:29', '2026-03-20 17:42:02', 'OPEN', 'PM');
INSERT INTO `schedule_slot` VALUES (51, 203, 2, '2026-02-21', 20, 20, 10.00, '2026-02-21 08:00:00', '2026-02-21 11:00:00', NULL, '2026-03-07 14:53:29', '2026-03-20 17:42:02', 'OPEN', 'AM');
INSERT INTO `schedule_slot` VALUES (52, 203, 2, '2026-02-21', 20, 20, 10.00, '2026-02-21 08:00:00', '2026-02-21 11:00:00', NULL, '2026-03-07 14:53:29', '2026-03-20 17:42:02', 'OPEN', 'PM');
INSERT INTO `schedule_slot` VALUES (53, 202, 1, '2026-02-21', 10, 10, 30.00, '2026-02-21 08:00:00', '2026-02-21 11:00:00', NULL, '2026-03-07 14:53:29', '2026-03-20 17:42:02', 'OPEN', 'AM');
INSERT INTO `schedule_slot` VALUES (54, 202, 1, '2026-02-21', 10, 10, 30.00, '2026-02-21 08:00:00', '2026-02-21 11:00:00', NULL, '2026-03-07 14:53:29', '2026-03-20 17:42:02', 'OPEN', 'PM');
INSERT INTO `schedule_slot` VALUES (55, 201, 1, '2026-02-21', 20, 20, 10.00, '2026-02-21 08:00:00', '2026-02-21 11:00:00', NULL, '2026-03-07 14:53:29', '2026-03-20 17:42:02', 'OPEN', 'AM');
INSERT INTO `schedule_slot` VALUES (56, 201, 1, '2026-02-21', 20, 20, 10.00, '2026-02-21 08:00:00', '2026-02-21 11:00:00', NULL, '2026-03-07 14:53:29', '2026-03-20 17:42:02', 'OPEN', 'PM');

-- ----------------------------
-- Table structure for shift_request
-- ----------------------------
DROP TABLE IF EXISTS `shift_request`;
CREATE TABLE `shift_request`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT,
  `request_no` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '申请单号',
  `request_doctor_id` bigint(0) NOT NULL COMMENT '申请调班医生doctor_profile.id',
  `target_schedule_id` bigint(0) NOT NULL COMMENT '影响的排班schedule_slot.id',
  `replacement_doctor_id` bigint(0) NULL DEFAULT NULL COMMENT '接替医生doctor_profile.id(可由管理员指派)',
  `reason` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '调班原因',
  `status` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'PENDING/APPROVED/REJECTED/CANCELED',
  `reviewed_by` bigint(0) NULL DEFAULT NULL COMMENT '审核人user_id(管理员)',
  `reviewed_at` datetime(0) NULL DEFAULT NULL,
  `created_at` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0),
  `updated_at` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0),
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_shift_request_no`(`request_no`) USING BTREE,
  INDEX `idx_shift_status`(`status`) USING BTREE,
  INDEX `idx_shift_target_schedule`(`target_schedule_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '临时调班申请' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for stat_daily_doctor
-- ----------------------------
DROP TABLE IF EXISTS `stat_daily_doctor`;
CREATE TABLE `stat_daily_doctor`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT,
  `stat_date` date NOT NULL,
  `dept_id` bigint(0) NOT NULL,
  `doctor_id` bigint(0) NOT NULL,
  `time_slot` varchar(8) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '可为空代表全天，也可按AM/PM汇总',
  `total_appointments` int(0) NOT NULL DEFAULT 0,
  `paid_count` int(0) NOT NULL DEFAULT 0,
  `checkin_count` int(0) NOT NULL DEFAULT 0,
  `completed_count` int(0) NOT NULL DEFAULT 0,
  `no_show_count` int(0) NOT NULL DEFAULT 0,
  `cancel_count` int(0) NOT NULL DEFAULT 0,
  `avg_rating` decimal(3, 2) NOT NULL DEFAULT 0.00,
  `rating_count` int(0) NOT NULL DEFAULT 0,
  `created_at` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0),
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_stat_daily_doctor`(`stat_date`, `doctor_id`, `time_slot`) USING BTREE,
  INDEX `idx_stat_dept_date`(`dept_id`, `stat_date`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '医生维度每日统计' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for sys_user
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT,
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '手机号(登录账号)',
  `password_hash` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '密码哈希(若仅验证码登录可为空)',
  `status` tinyint(0) NOT NULL DEFAULT 1 COMMENT '1启用 0禁用',
  `admin_dept_id` bigint(0) NULL DEFAULT NULL COMMENT '科室级管理员所属科室(当scope=DEPT时必填)',
  `last_login_at` datetime(0) NULL DEFAULT NULL,
  `created_at` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0),
  `updated_at` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0),
  `role` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '字典编码:USER_ROLE',
  `admin_scope_type` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '字典编码:ADMIN_SCOPE_TYPE',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_sys_user_phone`(`phone`) USING BTREE,
  INDEX `idx_sys_user_admin_dept`(`admin_dept_id`) USING BTREE,
  INDEX `idx_sys_user_role`(`role`) USING BTREE,
  INDEX `fk_sys_user_admin_scope_type`(`admin_scope_type`) USING BTREE,
  CONSTRAINT `fk_sys_user_admin_scope_type` FOREIGN KEY (`admin_scope_type`) REFERENCES `dict_item` (`item_code`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fk_sys_user_role` FOREIGN KEY (`role`) REFERENCES `dict_item` (`item_code`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 306 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '系统用户表(统一账号)' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_user
-- ----------------------------
INSERT INTO `sys_user` VALUES (1, '18800000001', NULL, 1, NULL, NULL, '2026-02-26 21:38:52', '2026-03-20 17:42:02', 'ADMIN', 'HOSPITAL');
INSERT INTO `sys_user` VALUES (2, '18800000002', NULL, 1, 1, NULL, '2026-02-26 21:38:52', '2026-03-20 17:42:02', 'ADMIN', 'DEPT');
INSERT INTO `sys_user` VALUES (3, '18800001001', NULL, 1, NULL, NULL, '2026-02-26 21:38:52', '2026-03-20 17:42:02', 'DOCTOR', NULL);
INSERT INTO `sys_user` VALUES (4, '18800001002', NULL, 1, NULL, NULL, '2026-02-26 21:38:52', '2026-03-20 17:42:02', 'DOCTOR', NULL);
INSERT INTO `sys_user` VALUES (5, '18800002001', NULL, 1, NULL, NULL, '2026-02-26 21:38:52', '2026-03-20 17:42:02', 'PATIENT', NULL);
INSERT INTO `sys_user` VALUES (101, '18800003001', NULL, 1, NULL, NULL, '2026-03-07 14:53:29', '2026-03-20 17:42:02', 'DOCTOR', NULL);
INSERT INTO `sys_user` VALUES (102, '18800003002', NULL, 1, NULL, NULL, '2026-03-07 14:53:29', '2026-03-20 17:42:02', 'DOCTOR', NULL);
INSERT INTO `sys_user` VALUES (103, '18800003003', NULL, 1, NULL, NULL, '2026-03-07 14:53:29', '2026-03-20 17:42:02', 'DOCTOR', NULL);
INSERT INTO `sys_user` VALUES (104, '18800003004', NULL, 1, NULL, NULL, '2026-03-07 14:53:29', '2026-03-20 17:42:02', 'DOCTOR', NULL);
INSERT INTO `sys_user` VALUES (301, '15770880133', NULL, 1, NULL, NULL, '2026-03-07 14:53:29', '2026-03-20 17:42:02', 'PATIENT', NULL);
INSERT INTO `sys_user` VALUES (302, '17772317748', NULL, 1, NULL, '2026-03-14 17:26:06', '2026-03-07 14:53:29', '2026-03-20 17:42:02', 'PATIENT', NULL);
INSERT INTO `sys_user` VALUES (303, '14417938163', NULL, 1, NULL, '2026-03-16 17:47:21', '2026-03-14 17:28:42', '2026-03-20 17:42:02', 'PATIENT', NULL);
INSERT INTO `sys_user` VALUES (304, '17245735507', NULL, 1, NULL, '2026-03-15 17:23:03', '2026-03-15 17:23:03', '2026-03-20 17:42:02', 'PATIENT', NULL);
INSERT INTO `sys_user` VALUES (305, '11838387831', NULL, 1, NULL, '2026-03-16 17:28:26', '2026-03-16 17:28:26', '2026-03-20 17:42:02', 'PATIENT', NULL);

SET FOREIGN_KEY_CHECKS = 1;
