# 济南市博物馆官网（演示项目）

> 馆名借用、内容全虚构；仅中文；Spring Boot + Vue 3 + MySQL + Docker Compose。

## 快速开始

```bash
docker compose up -d --build
open http://localhost:8081
```

## 技术栈

| 端 | 技术 |
|---|---|
| 后端 | Spring Boot 3.2.5、MyBatis-Plus 3.5.5、JWT、MySQL 8 |
| 前端 | Vue 3.4、Vite 5、TailwindCSS（观众端）、Element Plus（后台） |
| 运行 | Docker Compose（MySQL + 后端 + Nginx 前端） |

## 主要功能

- 观众端：展览/活动/藏品展示、免费实名预约（1+2 人，查重、容量、当天截止、前一日可取消）
- 后台：数据看板、四类内容 CRUD、容量与闭馆日管理、预约核销

## 目录结构

```
mesume/
├── docker-compose.yml        # 一键起
├── backend/                  # Spring Boot
│   └── src/main/resources/db/{schema.sql, data.sql}
└── frontend/                 # Vue 3
    └── public/images/        # 25 张演示图片
```

## 领约领域规则

见 [docs/SPEC.md](docs/SPEC.md) 第 4 节（12 条规则，TDD 覆盖）。