# 0001 - 前后端分离：Spring Boot + Vue 3

本项目为作品集演示项目，选型目标是展示国内企业主流技术栈，而非最小化演示成本。因此采用 Vue 3 + Spring Boot（MyBatis-Plus）+ MySQL 的前后端分离架构，本地通过 Docker Compose 一键运行。

## Considered Options

- Next.js / Nuxt 一体化全栈：演示成本最低，单仓库即可容纳观众端、后台与 API，但不体现 Java 后端能力。
- NestJS（TypeScript）：前后端同语言、可共享类型定义，但与展示方向不符。

## Consequences

- 前后端两个工程分别构建，用 Docker Compose（MySQL + 后端 + Nginx 前端）以一条命令弥合演示成本。
- 观众端与后台不拆两个前端工程：同一个 Vue 应用内以 `/admin` 路由区承载后台（路由懒加载），观众端用 Tailwind CSS 自定义样式，后台用 Element Plus。
- 前后端之间无共享类型，接口以 REST + JSON 约定，字段命名以后端为准。
