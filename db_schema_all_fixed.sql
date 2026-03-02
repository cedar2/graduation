CREATE DATABASE IF NOT EXISTS `medical_reg` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE `medical_reg`;

DROP TABLE IF EXISTS `stat_daily_doctor`;
DROP TABLE IF EXISTS `notify_log`;
DROP TABLE IF EXISTS `doctor_rating`;
DROP TABLE IF EXISTS `shift_request`;
DROP TABLE IF EXISTS `queue_state`;
DROP TABLE IF EXISTS `checkin_queue`;
DROP TABLE IF EXISTS `refund_record`;
DROP TABLE IF EXISTS `payment_record`;
DROP TABLE IF EXISTS `appointment`;
DROP TABLE IF EXISTS `schedule_slot`;
DROP TABLE IF EXISTS `doctor_profile`;
DROP TABLE IF EXISTS `patient_profile`;
DROP TABLE IF EXISTS `sys_user`;
DROP TABLE IF EXISTS `department`;

CREATE TABLE `department` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
  `name` VARCHAR(64) NOT NULL COMMENT '科室名称',
  `code` VARCHAR(32) DEFAULT NULL COMMENT '科室编码/拼音码',
  `location_desc` VARCHAR(128) DEFAULT NULL COMMENT '位置描述(如门诊楼3层A区)',
  `floor` VARCHAR(32) DEFAULT NULL COMMENT '楼层信息',
  `status` TINYINT NOT NULL DEFAULT 1 COMMENT '1启用 0停用',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  UNIQUE KEY `uk_department_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='科室表';

CREATE TABLE `sys_user` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
  `phone` VARCHAR(20) NOT NULL COMMENT '手机号(登录账号)',
  `password_hash` VARCHAR(255) DEFAULT NULL COMMENT '密码哈希(若仅验证码登录可为空)',
  `role` VARCHAR(16) NOT NULL COMMENT 'PATIENT/DOCTOR/ADMIN',
  `status` TINYINT NOT NULL DEFAULT 1 COMMENT '1启用 0禁用',
  `admin_scope_type` VARCHAR(16) DEFAULT NULL COMMENT '管理员数据范围: DEPT/HOSPITAL',
  `admin_dept_id` BIGINT DEFAULT NULL COMMENT '科室级管理员所属科室(当scope=DEPT时必填)',
  `last_login_at` DATETIME DEFAULT NULL,
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  UNIQUE KEY `uk_sys_user_phone` (`phone`),
  KEY `idx_sys_user_role` (`role`),
  KEY `idx_sys_user_admin_dept` (`admin_dept_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统用户表(统一账号)';

CREATE TABLE `patient_profile` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
  `user_id` BIGINT NOT NULL COMMENT '关联sys_user.id',
  `real_name` VARCHAR(32) NOT NULL COMMENT '姓名',
  `gender` VARCHAR(8) DEFAULT NULL COMMENT '男/女/未知',
  `birth_date` DATE DEFAULT NULL COMMENT '出生日期',
  `id_card_no` VARCHAR(32) DEFAULT NULL COMMENT '身份证号(可选)',
  `province` VARCHAR(32) DEFAULT NULL COMMENT '省',
  `city` VARCHAR(32) DEFAULT NULL COMMENT '市',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  UNIQUE KEY `uk_patient_user` (`user_id`),
  KEY `idx_patient_province_city` (`province`, `city`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='患者档案';

CREATE TABLE `doctor_profile` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
  `user_id` BIGINT NOT NULL COMMENT '关联sys_user.id',
  `dept_id` BIGINT NOT NULL COMMENT '所属科室department.id',
  `real_name` VARCHAR(32) NOT NULL COMMENT '医生姓名',
  `title` VARCHAR(32) DEFAULT NULL COMMENT '职称(住院医师/主治/副高/正高等)',
  `doctor_type` VARCHAR(16) NOT NULL DEFAULT 'GENERAL' COMMENT '号别: GENERAL普通/EXPERT专家(用于费用规则)',
  `intro` VARCHAR(512) DEFAULT NULL COMMENT '简介',
  `status` VARCHAR(16) NOT NULL DEFAULT 'ACTIVE' COMMENT 'ACTIVE在职/INACTIVE停用',
  `avg_rating` DECIMAL(3,2) NOT NULL DEFAULT 0.00 COMMENT '综合评分均值(1.00~5.00)',
  `rating_count` INT NOT NULL DEFAULT 0 COMMENT '评分次数',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  UNIQUE KEY `uk_doctor_user` (`user_id`),
  KEY `idx_doctor_dept` (`dept_id`),
  KEY `idx_doctor_rating` (`avg_rating`, `rating_count`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='医生档案';

CREATE TABLE `schedule_slot` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
  `doctor_id` BIGINT NOT NULL COMMENT 'doctor_profile.id',
  `dept_id` BIGINT NOT NULL COMMENT '冗余: 科室ID便于统计与权限过滤',
  `visit_date` DATE NOT NULL COMMENT '出诊日期',
  `time_slot` VARCHAR(8) NOT NULL COMMENT '时段: AM/PM',
  `status` VARCHAR(16) NOT NULL DEFAULT 'OPEN' COMMENT 'OPEN/CLOSED/FULL',
  `capacity` INT NOT NULL COMMENT '号源容量',
  `remaining` INT NOT NULL COMMENT '剩余号源',
  `fee` DECIMAL(10,2) NOT NULL COMMENT '挂号费(下单时可复制到订单)',
  `open_time` DATETIME DEFAULT NULL COMMENT '放号时间(可选)',
  `stop_time` DATETIME DEFAULT NULL COMMENT '停号时间(可选)',
  `created_by` BIGINT DEFAULT NULL COMMENT '创建人(管理员user_id)',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  UNIQUE KEY `uk_schedule_doctor_date_slot` (`doctor_id`, `visit_date`, `time_slot`),
  KEY `idx_schedule_dept_date` (`dept_id`, `visit_date`),
  KEY `idx_schedule_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='医生排班与号源(按天+时段)';

CREATE TABLE `appointment` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
  `appointment_no` VARCHAR(32) NOT NULL COMMENT '预约单号(展示用)',
  `patient_id` BIGINT NOT NULL COMMENT 'patient_profile.id',
  `doctor_id` BIGINT NOT NULL COMMENT 'doctor_profile.id(可能因替诊变更)',
  `dept_id` BIGINT NOT NULL COMMENT '科室ID(冗余便于统计)',
  `schedule_id` BIGINT NOT NULL COMMENT 'schedule_slot.id',
  `visit_date` DATE NOT NULL COMMENT '就诊日期(冗余)',
  `time_slot` VARCHAR(8) NOT NULL COMMENT 'AM/PM(冗余)',
  `fee` DECIMAL(10,2) NOT NULL COMMENT '挂号费(下单时锁定)',
  `status` VARCHAR(16) NOT NULL COMMENT 'UNPAID/PAID/CANCELED/REFUNDED/CHECKED_IN/COMPLETED/NO_SHOW...',
  `paid_at` DATETIME DEFAULT NULL,
  `canceled_at` DATETIME DEFAULT NULL,
  `refunded_at` DATETIME DEFAULT NULL,
  `checked_in_at` DATETIME DEFAULT NULL,
  `completed_at` DATETIME DEFAULT NULL,
  `no_show_marked_at` DATETIME DEFAULT NULL COMMENT '爽约判定时间',
  `cancel_reason` VARCHAR(128) DEFAULT NULL,
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  UNIQUE KEY `uk_appointment_no` (`appointment_no`),
  KEY `idx_appt_patient_date_slot` (`patient_id`, `visit_date`, `time_slot`),
  KEY `idx_appt_doctor_date_slot` (`doctor_id`, `visit_date`, `time_slot`),
  KEY `idx_appt_status` (`status`),
  KEY `idx_appt_dept_date` (`dept_id`, `visit_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='预约订单';

CREATE TABLE `payment_record` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
  `appointment_id` BIGINT NOT NULL COMMENT 'appointment.id',
  `pay_no` VARCHAR(32) NOT NULL COMMENT '支付单号',
  `channel` VARCHAR(16) NOT NULL COMMENT 'ALIPAY/WECHAT/MOCK',
  `amount` DECIMAL(10,2) NOT NULL,
  `status` VARCHAR(16) NOT NULL COMMENT 'INIT/SUCCESS/FAILED/CLOSED',
  `third_trade_no` VARCHAR(64) DEFAULT NULL COMMENT '第三方交易号(模拟可空)',
  `paid_at` DATETIME DEFAULT NULL,
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  UNIQUE KEY `uk_pay_no` (`pay_no`),
  KEY `idx_pay_appt` (`appointment_id`),
  KEY `idx_pay_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='支付记录';

CREATE TABLE `refund_record` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
  `appointment_id` BIGINT NOT NULL COMMENT 'appointment.id',
  `refund_no` VARCHAR(32) NOT NULL COMMENT '退款单号',
  `channel` VARCHAR(16) NOT NULL COMMENT 'ALIPAY/WECHAT/MOCK',
  `amount` DECIMAL(10,2) NOT NULL,
  `status` VARCHAR(16) NOT NULL COMMENT 'INIT/SUCCESS/FAILED',
  `reason` VARCHAR(64) DEFAULT NULL COMMENT 'CANCEL/NO_SHOW/AUTO_REFUND/SHIFT_CANCEL等',
  `third_refund_no` VARCHAR(64) DEFAULT NULL,
  `refunded_at` DATETIME DEFAULT NULL,
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  UNIQUE KEY `uk_refund_no` (`refund_no`),
  KEY `idx_refund_appt` (`appointment_id`),
  KEY `idx_refund_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='退款记录';

CREATE TABLE `checkin_queue` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
  `appointment_id` BIGINT NOT NULL COMMENT 'appointment.id',
  `doctor_id` BIGINT NOT NULL COMMENT 'doctor_profile.id',
  `dept_id` BIGINT NOT NULL,
  `visit_date` DATE NOT NULL,
  `time_slot` VARCHAR(8) NOT NULL COMMENT 'AM/PM',
  `queue_no` INT NOT NULL COMMENT '排队号(从1开始)',
  `status` VARCHAR(16) NOT NULL DEFAULT 'WAITING' COMMENT 'WAITING/CALLED/SKIPPED/DONE',
  `checked_in_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `called_at` DATETIME DEFAULT NULL,
  `done_at` DATETIME DEFAULT NULL,
  UNIQUE KEY `uk_queue_appt` (`appointment_id`),
  KEY `idx_queue_doctor_slot` (`doctor_id`, `visit_date`, `time_slot`, `queue_no`),
  KEY `idx_queue_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='签到排队队列';

CREATE TABLE `queue_state` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
  `doctor_id` BIGINT NOT NULL,
  `visit_date` DATE NOT NULL,
  `time_slot` VARCHAR(8) NOT NULL,
  `current_call_no` INT NOT NULL DEFAULT 0 COMMENT '当前叫号号',
  `updated_by` BIGINT DEFAULT NULL COMMENT '操作人user_id(医生/管理员)',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  UNIQUE KEY `uk_queue_state` (`doctor_id`, `visit_date`, `time_slot`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='叫号状态';

CREATE TABLE `shift_request` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
  `request_no` VARCHAR(32) NOT NULL COMMENT '申请单号',
  `request_doctor_id` BIGINT NOT NULL COMMENT '申请调班医生doctor_profile.id',
  `target_schedule_id` BIGINT NOT NULL COMMENT '影响的排班schedule_slot.id',
  `replacement_doctor_id` BIGINT DEFAULT NULL COMMENT '接替医生doctor_profile.id(可由管理员指派)',
  `reason` VARCHAR(256) DEFAULT NULL COMMENT '调班原因',
  `status` VARCHAR(16) NOT NULL COMMENT 'PENDING/APPROVED/REJECTED/CANCELED',
  `reviewed_by` BIGINT DEFAULT NULL COMMENT '审核人user_id(管理员)',
  `reviewed_at` DATETIME DEFAULT NULL,
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  UNIQUE KEY `uk_shift_request_no` (`request_no`),
  KEY `idx_shift_status` (`status`),
  KEY `idx_shift_target_schedule` (`target_schedule_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='临时调班申请';

CREATE TABLE `doctor_rating` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
  `appointment_id` BIGINT NOT NULL COMMENT '预约单id(一单一评)',
  `doctor_id` BIGINT NOT NULL,
  `patient_id` BIGINT NOT NULL,
  `score` TINYINT NOT NULL COMMENT '评分1~5',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  UNIQUE KEY `uk_rating_appointment` (`appointment_id`),
  KEY `idx_rating_doctor` (`doctor_id`, `created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='医生评分(仅评分不评价)';

CREATE TABLE `notify_log` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
  `biz_type` VARCHAR(32) NOT NULL COMMENT 'APPOINTMENT/SHIFT/QUEUE等',
  `biz_id` BIGINT NOT NULL COMMENT '业务ID，如appointment.id或shift_request.id',
  `receiver_user_id` BIGINT NOT NULL COMMENT '接收人sys_user.id',
  `channel` VARCHAR(16) NOT NULL COMMENT 'SMS/PUSH/MOCK',
  `template_code` VARCHAR(32) DEFAULT NULL COMMENT '模板编号(可选)',
  `content` VARCHAR(512) DEFAULT NULL COMMENT '发送内容(模拟可存)',
  `status` VARCHAR(16) NOT NULL COMMENT 'INIT/SENT/FAILED',
  `sent_at` DATETIME DEFAULT NULL,
  `error_msg` VARCHAR(256) DEFAULT NULL,
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  KEY `idx_notify_biz` (`biz_type`, `biz_id`),
  KEY `idx_notify_receiver` (`receiver_user_id`, `created_at`),
  KEY `idx_notify_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='消息提醒发送日志';

CREATE TABLE `stat_daily_doctor` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
  `stat_date` DATE NOT NULL,
  `dept_id` BIGINT NOT NULL,
  `doctor_id` BIGINT NOT NULL,
  `time_slot` VARCHAR(8) DEFAULT NULL COMMENT '可为空代表全天，也可按AM/PM汇总',
  `total_appointments` INT NOT NULL DEFAULT 0,
  `paid_count` INT NOT NULL DEFAULT 0,
  `checkin_count` INT NOT NULL DEFAULT 0,
  `completed_count` INT NOT NULL DEFAULT 0,
  `no_show_count` INT NOT NULL DEFAULT 0,
  `cancel_count` INT NOT NULL DEFAULT 0,
  `avg_rating` DECIMAL(3,2) NOT NULL DEFAULT 0.00,
  `rating_count` INT NOT NULL DEFAULT 0,
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  UNIQUE KEY `uk_stat_daily_doctor` (`stat_date`, `doctor_id`, `time_slot`),
  KEY `idx_stat_dept_date` (`dept_id`, `stat_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='医生维度每日统计';

-- 初始化数据（可选）
INSERT INTO `department` (`name`, `code`, `location_desc`, `floor`, `status`)
VALUES
('内科', 'NK', '门诊楼A区', '3F', 1),
('外科', 'WK', '门诊楼B区', '2F', 1),
('儿科', 'EK', '门诊楼C区', '1F', 1);

INSERT INTO `sys_user` (`phone`, `role`, `status`, `admin_scope_type`, `admin_dept_id`)
VALUES
('18800000001', 'ADMIN', 1, 'HOSPITAL', NULL),
('18800000002', 'ADMIN', 1, 'DEPT', 1);

INSERT INTO `sys_user` (`phone`, `role`, `status`)
VALUES
('18800001001', 'DOCTOR', 1),
('18800001002', 'DOCTOR', 1);

INSERT INTO `doctor_profile` (`user_id`, `dept_id`, `real_name`, `title`, `doctor_type`, `intro`, `status`)
VALUES
(3, 1, '张医生', '主治医师', 'GENERAL', '擅长常见内科疾病诊疗', 'ACTIVE'),
(4, 1, '李医生', '副主任医师', 'EXPERT', '擅长疑难内科病例诊疗', 'ACTIVE');

INSERT INTO `sys_user` (`phone`, `role`, `status`)
VALUES ('18800002001', 'PATIENT', 1);

INSERT INTO `patient_profile` (`user_id`, `real_name`, `gender`, `birth_date`, `province`, `city`)
VALUES (5, '王同学', '男', '2003-01-01', '陕西省', '西安市');