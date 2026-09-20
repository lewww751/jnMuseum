# 济南市博物馆官网 — 实施 SPEC

> 本文是前后端实施的唯一契约。术语见 [CONTEXT.md](../CONTEXT.md)，架构决策见 [ADR-0001](adr/0001-spring-boot-vue-split-architecture.md)。
> 演示项目：馆名借用、内容全虚构；仅中文；不做支付/短信/真实核验/活动报名/多角色。

## 1. 仓库结构与运行形态

```
mesume/
├── docker-compose.yml        # 一键起：MySQL + 后端 + Nginx(前端)
├── backend/                  # Spring Boot 3.2, Java 17, Maven
│   ├── Dockerfile
│   └── src/main/resources/db/{schema.sql, data.sql}   # 已提供，勿改内容
└── frontend/                 # Vue 3 + Vite 5 + TypeScript
    ├── Dockerfile            # 多阶段：node 构建 → nginx 运行
    ├── nginx.conf
    └── public/images/        # 已提供种子图片，勿改
```

- 端口：本地开发后端 `8080`、前端 `5173`；Docker Compose 站点入口 `http://localhost:8081`。
- 时区：**所有领域逻辑一律使用 Asia/Shanghai**（预约窗口、当天截止、取消期限、闭馆判断）。
- 编码：全部 UTF-8（pom `project.build.sourceEncoding=UTF-8`）。

## 2. 技术栈（版本钉死）

| 端 | 技术 |
|---|---|
| 后端 | Spring Boot 3.2.5、MyBatis-Plus `mybatis-plus-spring-boot3-starter` 3.5.5、mysql-connector-j（Boot 管理版本）、jjwt 0.12.5(api/impl/jackson)、spring-boot-starter-validation、spring-security-crypto（仅 BCrypt）、Lombok |
| 前端 | Vue ^3.4、vue-router ^4.3、pinia ^2.1、axios ^1.7、TailwindCSS ^3.4、Element Plus ^2.7 + icons、echarts ^5.5、qrcode ^1.5、Vite ^5.2、TypeScript ~5.4、vue-tsc ^2 |

## 3. 数据库（MySQL 8，utf8mb4）

DDL 以仓库中 `backend/src/main/resources/db/schema.sql` 为准（已提供，勿改）。要点：

- `admin_user`：单管理员（Java Runner 启动时若无记录则写入 `admin/admin123`，BCrypt 哈希）。
- `exhibition`：`kind` ∈ PERMANENT|TEMPORARY；`published` 上下架；常设展日期可空。
- `museum_event`：活动（表名避开关键字）。`published` 上下架。
- `collection_item`：藏品精选。`published` + `sort_order`。
- `guide_content`：`page_key` ∈ visit|about，纯文本（段落用空行分隔）。
- `day_setting`：**日期覆盖**。`is_open=1` 表示"当日开放"（可覆盖周一闭馆），`is_open=0` 表示"当日临时闭馆”。无记录则按默认规则（周一闭馆，其余开放）。
- `slot_capacity`：`(capacity_date, slot)` 唯一；无记录的日期+时段用默认容量 **300**。
- `booking`：预约单。`code` 8 位数字唯一；`status` ∈ ACTIVE|CANCELLED|CHECKED_IN。
- `booking_guest`：入馆人。`guest_type` ∈ PRIMARY|COMPANION；`id_card` CHAR(18)。

初始化：`spring.sql.init.mode=always`，schema/data 均幂等（IF NOT EXISTS / INSERT IGNORE + 显式 id）。

## 4. 预约领域规则（TDD 的唯一契约缝）

纯逻辑抽到无 Spring 依赖的类（`IdCardValidator`、`BookingPolicy`、`OpenDayRule`），**先写测试再实现**；`BookingService`（含 DB）用 Mockito 单测。

| # | 规则 |
|---|---|
| R1 预约窗口 | `visitDate ∈ [今天, 今天+6]`（Asia/Shanghai，共 7 天，含当天） |
| R2 当天截止 | 若 `visitDate == 今天`：AM 须在 09:00 前、PM 须在 13:00 前提交 |
| R3 开放日 | 默认周一闭馆、周二至周日开放；`day_setting` 覆盖：`is_open=1` 强制开放、`is_open=0` 强制闭馆 |
| R4 单据结构 | 1 个主预约人(PRIMARY) + 0~2 个同行人(COMPANION)，共 1~3 人；单内证件号不得重复；姓名非空 ≤20 字 |
| R5 身份证 | 18 位：前 17 位数字 + 末位数字或 X/x；**校验 GB11643 加权 checksum**；小写 x 归一化为 X |
| R6 手机号 | `^1[3-9]\d{9}$`（主预约人，作为联系与取消凭证） |
| R7 查重 | 任一入馆人证件号在 `visitDate` 当天已存在于状态 ≠ CANCELLED 的预约单中（跨单、含同行人）→ 拒绝 |
| R8 容量 | 该 (date, slot) 状态 ACTIVE 的入馆人总数 + 本次人数 ≤ 容量（默认 300）。实现须防超卖：事务内先 upsert `slot_capacity` 行（INSERT IGNORE）再 `SELECT ... FOR UPDATE` 后计数 |
| R9 取消 | 仅 status=ACTIVE 且 `now < visitDate 当天 00:00`（即前一天 24:00 前）；整单取消（CANCELLED），名额即时释放（容量按 ACTIVE 实时统计） |
| R10 核销 | 仅 status=ACTIVE 且 `visitDate == 今天`（Asia/Shanghai）；置 CHECKED_IN + 时间 |
| R11 预约码 | 8 位数字（首位非 0），随机生成，冲突重试 ≤5 次 |
| R12 查询凭证 | ① 预约码；② 主预约人手机号 + 任一入馆人证件号。返回列表（同一手机可多单） |

取消需要 `code + phone` 双凭证（phone 须匹配预约单）。已核销不可取消；取消过的单可重新预约同日（R7 只看非 CANCELLED）。

## 7. 前端实现要求

### 7.1 工程与结构

- Vite + Vue3 + TS；`src/api/`（axios 封装：baseURL `/api`、响应拦截统一弹错、admin 请求附 Bearer、401 跳登录）；`src/types/`；`src/views/public/`、`src/views/admin/`；`src/components/`。
- dev 代理：`/api`、`/uploads` → `http://localhost:8080`。
- 路由（观众端全懒加载可选，admin 整组懒加载）：
  `/` 首页；`/exhibitions`(+`/:id`)；`/events`；`/collections`；`/visit`；`/about`；`/reserve`；`/my-booking`；`/admin/login`；`/admin`(layout) 子路由：dashboard(``)、exhibitions、events、collections、guide、calendar、bookings。`meta.requiresAuth` + 守卫查 token。
- 构建：`npm run build` = `vue-tsc -b && vite build`，必须零错误通过。

### 7.2 视觉规范（观众端，Tailwind 自定义，暗色庄重）

- 色板（tailwind.config 自定义）：`ink:#161310`(页面底)、`panel:#211C17`、`panel2:#2B241D`、`line:#3A322A`、`textMain:#EDE6DA`、`textSub:#A79A87`、`spring:#6FA8A0`(泉水青，主按钮/链接，hover `#8FC2BA`)、`clay:#C0784F`(陶土，强调/标签)、`gold:#C9A96A`(镇馆之宝等高亮)。
- 字体：标题衬线 `"Noto Serif SC","Songti SC","STSong","SimSun",serif`；正文 `"PingFang SC","Microsoft YaHei",system-ui`。**不引入 webfont**。
- 布局：固定顶栏（馆名衬线字 + 导航 + 「门票预约」主按钮，移动端汉堡抽屉）；页脚（地址/开放时间/咨询电话/「本站为演示项目，内容虚构」）。首页 hero 用 `public/images/hero.jpg` 暗色渐变遮罩。
- 手机优先响应式；卡片圆角 `rounded-lg`、边框 `border-line`、悬浮微上移。

### 7.3 观众端页面

- **首页**：hero（当期临展《汉画像石上的齐鲁生活》+ 预约 CTA）→ 「正在展出」展览卡 3 张 → 「近期活动」4 条（状态标签）→ 「馆藏精选」横向滚动 6 件 → 开放时间条 + 预约入口。
- **展览列表**：筛选 chips 全部/常设/临展；卡片显示 displayStatus 中文标签（正在展出=泉水青、即将开幕=gold、已闭展=灰）。
- **展览详情**：大图、日期/展厅、正文段落渲染。
- **活动**：列表 + 状态标签（即将开始/进行中/已结束），类别筛选。
- **藏品**：网格卡（图 + 名称 + 年代 + 类别）；镇馆之宝（蛋壳黑陶高柄杯）加 gold 「镇馆之宝」角标。
- **参观指南 / 关于**：渲染 guide_content 纯文本段落；指南页固定展示「免费不免票·实名预约」说明块。
- **门票预约 `/reserve`**（核心，分步表单）：
  1. 选日期：7 天横滑卡（周几、余量合计；闭馆日禁用并显示原因）
  2. 选时段：AM/PM 卡（剩余名额；不可约显示原因）
  3. 填信息：手机号；主预约人姓名+身份证；同行人动态增删（≤2）；**前端同样实现身份证 checksum 校验**（即时反馈，服务端为准）
  4. 成功页：大号预约码 + `qrcode` 生成的二维码 + 入馆提示 + 「查询我的预约」入口。
  失败统一 toast 中文 message；可回退上一步。
- **我的预约 `/my-booking`**：两种查询 tab（预约码 / 手机号+证件号）→ 预约单卡列表（状态、时段、人数、证件掩码）→ 取消（确认弹窗；不可取消显示截止时间）。

### 7.4 后台（Element Plus，默认亮色主题，`/admin` 路由组）

- **登录**：居中卡片；错误 toast。
- **布局**：左侧菜单（数据看板/展览管理/活动管理/藏品管理/指南编辑/容量与闭馆日/预约管理）+ 顶栏（馆名 + 退出）。
- **看板**：4 个统计卡 + 今日/明日时段容量表 + ECharts 近 7 天预约柱状图。
- **展览/活动/藏品管理**：ElTable + 新增/编辑 Dialog 表单 + 删除确认；图片用 el-upload 直传 `/api/admin/upload` 回显；published 开关。展览表单：临展时展期必填。
- **指南编辑**：visit/about 两个 tab，textarea 保存。
- **容量与闭馆日**：月历网格（每格：开放状态 + AM/PM 容量与已约）；点格弹 Drawer：设开放/闭馆（覆盖周一）+ 原因 + AM/PM 容量修改；上下月切换。
- **预约管理**：筛选（日期/时段/状态/关键字）+ 分页表格 + 行操作「核销」（确认弹窗；非当日/非 ACTIVE 禁用）+ 详情 Drawer（入馆人列表）。

## 8. 基础设施（根目录已提供，实施方勿改）

- `docker-compose.yml`：mysql(8.0，卷持久化) + backend(依赖 mysql 健康) + frontend(nginx, `8081:80`，反代 `/api`、`/uploads` → backend:8080)。uploads 卷共享 backend。
- `backend/Dockerfile`：maven:3.9-eclipse-temurin-17 构建 → eclipse-temurin:17-jre。
- `frontend/Dockerfile`：node:20-alpine 构建 → nginx:alpine + `frontend/nginx.conf`（SPA try_files + 反代）。

## 9. 验收清单

1. `cd backend && mvn test` 全绿；`mvn package` 成功。
2. `cd frontend && npm run build`（含 vue-tsc）零错误。
3. `docker compose up -d --build` 后 `http://localhost:8081`：六个观众端页面有真实种子内容；预约全流程（含查重、容量、当天截止、取消期限）行为符合第 4 节；`/admin`（admin/admin123）看板有数据、四类内容 CRUD、月历设置生效、预约可核销。
4. 种子数据完整：5 展览、8 活动、10 藏品、visit/about 文案、演示预约单若干（见 data.sql）。
