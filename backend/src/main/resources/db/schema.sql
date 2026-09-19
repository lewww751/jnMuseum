-- 济南市博物馆官网（演示项目）数据库结构
-- 幂等：可重复执行
CREATE TABLE IF NOT EXISTS admin_user (
  id          BIGINT AUTO_INCREMENT PRIMARY KEY,
  username    VARCHAR(50)  NOT NULL UNIQUE,
  password_hash VARCHAR(100) NOT NULL,
  created_at  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS exhibition (
  id          BIGINT AUTO_INCREMENT PRIMARY KEY,
  title       VARCHAR(100) NOT NULL,
  kind        VARCHAR(20)  NOT NULL COMMENT 'PERMANENT | TEMPORARY',
  hall        VARCHAR(50)  NOT NULL,
  summary     VARCHAR(500) NOT NULL,
  content     TEXT         NULL,
  cover_image VARCHAR(255) NOT NULL,
  start_date  DATE         NULL COMMENT '临展展期起（常设为空）',
  end_date    DATE         NULL COMMENT '临展展期止（常设为空）',
  published   TINYINT(1)   NOT NULL DEFAULT 0,
  sort_order  INT          NOT NULL DEFAULT 0,
  created_at  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  KEY idx_kind (kind)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS museum_event (
  id          BIGINT AUTO_INCREMENT PRIMARY KEY,
  title       VARCHAR(100) NOT NULL,
  category    VARCHAR(30)  NOT NULL COMMENT '讲座 | 工作坊 | 亲子活动 | 其他',
  location    VARCHAR(100) NOT NULL,
  start_time  DATETIME     NOT NULL,
  end_time    DATETIME     NULL,
  summary     VARCHAR(500) NOT NULL,
  cover_image VARCHAR(255) NOT NULL,
  published   TINYINT(1)   NOT NULL DEFAULT 0,
  created_at  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  KEY idx_start (start_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS collection_item (
  id          BIGINT AUTO_INCREMENT PRIMARY KEY,
  name        VARCHAR(100)  NOT NULL,
  era         VARCHAR(50)   NOT NULL COMMENT '年代，如：龙山文化',
  category    VARCHAR(50)   NOT NULL COMMENT '类别，如：陶器、玉器、瓷器',
  description VARCHAR(1000) NOT NULL,
  image       VARCHAR(255)  NOT NULL,
  published   TINYINT(1)    NOT NULL DEFAULT 0,
  sort_order  INT           NOT NULL DEFAULT 0,
  created_at  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS guide_content (
  id          BIGINT AUTO_INCREMENT PRIMARY KEY,
  page_key    VARCHAR(30) NOT NULL UNIQUE COMMENT 'visit | about',
  content     MEDIUMTEXT  NOT NULL,
  updated_at  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 日期覆盖：无记录按默认规则（周一闭馆，其余开放）
CREATE TABLE IF NOT EXISTS day_setting (
  id           BIGINT AUTO_INCREMENT PRIMARY KEY,
  setting_date DATE        NOT NULL UNIQUE,
  is_open      TINYINT(1)  NOT NULL COMMENT '1=当日开放（可覆盖周一闭馆） 0=当日临时闭馆',
  reason       VARCHAR(100) NULL,
  created_at   DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS slot_capacity (
  id            BIGINT AUTO_INCREMENT PRIMARY KEY,
  capacity_date DATE       NOT NULL,
  slot          VARCHAR(10) NOT NULL COMMENT 'AM | PM',
  capacity      INT        NOT NULL,
  UNIQUE KEY uk_date_slot (capacity_date, slot)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS booking (
  id            BIGINT AUTO_INCREMENT PRIMARY KEY,
  code          CHAR(8)    NOT NULL UNIQUE COMMENT '8位数字预约码',
  visit_date    DATE       NOT NULL,
  slot          VARCHAR(10) NOT NULL COMMENT 'AM | PM',
  phone         VARCHAR(11) NOT NULL COMMENT '主预约人手机号',
  status        VARCHAR(20) NOT NULL DEFAULT 'ACTIVE' COMMENT 'ACTIVE | CANCELLED | CHECKED_IN',
  created_at    DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  cancelled_at  DATETIME NULL,
  checked_in_at DATETIME NULL,
  KEY idx_visit (visit_date, slot),
  KEY idx_phone (phone)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS booking_guest (
  id         BIGINT AUTO_INCREMENT PRIMARY KEY,
  booking_id BIGINT      NOT NULL,
  guest_type VARCHAR(10) NOT NULL COMMENT 'PRIMARY | COMPANION',
  name       VARCHAR(50) NOT NULL,
  id_card    CHAR(18)    NOT NULL,
  KEY idx_booking (booking_id),
  KEY idx_id_card (id_card),
  CONSTRAINT fk_guest_booking FOREIGN KEY (booking_id) REFERENCES booking(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
