# 演示视频录制指南（交付物要求 1）

> 本文件将 `docs/IMPLEMENTATION_PLAN.md` §8.3 的视频脚本抽离为**独立、可照拍**的清单，
> 用于录制 README 要求的「完整展示所完成需求」的演示视频。
> 录制顺序 S1~S13，每条录完停顿 1~2 秒并口述，便于后期剪辑。

## 0. 录制前准备

```bash
# 1) 后端（:8080）+ 数据库（MySQL :3306 / phpMyAdmin :8088）
docker compose up -d
docker compose ps                       # 两个容器均 healthy
cd server/qa-service-user && ./mvnw spring-boot:run

# 2) 前端（:5173）
cd web/qa-web && npm run dev

# 3) 浏览器打开
#    首页        http://localhost:5173/
#    医生页      http://localhost:5173/doctors
#    登录注册页  http://localhost:5173/patient/login
#    phpMyAdmin  http://localhost:8088  （账号 root / root123456，库 qa_live）
```

> 录制分辨率建议 1280×800 以上；浏览器窗口与终端分屏同框，能同时看到「操作」与「结果」最佳。

## 1. 镜头清单（S1~S13）

| # | 场景 | 画面 / 操作 | 证明点 |
|---|---|---|---|
| S1 | 环境演示 | 终端 `docker compose ps`（healthy）→ 浏览器开 phpMyAdmin → 展开 `qa_live.t_doctor` 看 5 条数据 + `specialties` JSON 列；再展开 `t_patient` | 需求2 基建 |
| S2 | 医生列表接口 | 终端 `curl -s localhost:8080/api/doctors \| jq` → 指向响应**无 password**、`specialties` 为数组、`isActive` 为布尔 | 需求2 后端 + 契约 |
| S3 | 医生页联调 | 浏览器 `/doctors` 渲染 5 张卡片（doc004「暂未开放」）→ **停掉后端容器** → 刷新页面显示错误/重试态 → 重启后端 → 恢复正常 | 需求2 前后端联调（真实性最强，勿剪快） |
| S4 | 首页无回归 | `/` 页面统计数字与「开放诊室」列表正确 | 需求2 无回归 |
| S5 | 中文 → English | 首页右上角下拉选 English → 15 条文案**即时**切换（横向扫过 hero / 统计 / 诊室三区） | 需求1 |
| S6 | English → 中文 + 持久化 | 切回中文 → **刷新浏览器** → 仍为中文 | 需求1 持久化 |
| S7 | 边界证明 | 切到 English 后跳 `/doctors` 与 `/about`，展示**仅首页翻译**、其余仍中文 | 需求1 范围符合 README |
| S8 | 患者注册 | `/patient/login` 注册 Tab → 填用户名/密码/姓名 → 提交成功 | 需求3 |
| S9 | 数据落库 | phpMyAdmin 刷新 `t_patient` → 展示该行 `password` 为 **`$2a$10$...` 密文** | 需求3 安全性 |
| S10 | 患者登录 + 保持 | 登录 Tab 登录 → 跳 `/consultation` → **刷新浏览器仍在线** → 向某医生提交一个问题成功 | 需求3 登录态持久化 + 主链路 |
| S11 | 鉴权拦截 | 退出登录 → 直接访问 `/consultation` 被重定向登录页；终端 `curl localhost:8080/api/patient/me` 返 401 | 需求3 守卫 + JWT |
| S12 | 测试执行 | 运行 `bash scripts/api-test.sh` → 展示 `PASS/FAIL` 汇总 → 打开 `docs/TEST_REPORT.md` | 测试要求 |
| S13 | 分支与 PR | 终端 `git log --oneline`（分支提交）→ GitHub PR 页面（含 diff）| 流程要求 |

> **重点保留**：S3（停后端看错误态再恢复）与 S9（密码密文）最能体现「真的接了数据库 + 安全存储」，务必完整。

## 2. 口述要点（每条一句话）

- S1：容器化管理 MySQL，数据来自数据库而非前端 JSON。
- S2：接口契约正确，敏感字段 password 不下发。
- S3：前端真实消费后端 API，断网有错误态，恢复即正常。
- S5/S6/S7：仅首页国际化，刷新保持，范围边界正确。
- S8/S9：注册落库，密码 BCrypt 加盐存储。
- S10：登录态持久化，刷新不丢。
- S11：未登录访问受保护页被拦截，token 缺失即 401。
- S12：53 项 curl 用例全绿，报告真实生成。
- S13：所有改动在特性分支，PR 指向 main。

## 3. 录制后

1. 导出为 `demo.mp4`（建议 H.264，分辨率 ≥ 1280×720，时长 4~8 分钟）。
2. 按 README「面试流程要求」将视频与 PR 链接一并发送。
3. 视频文件较大，建议放在仓库根目录 `demo.mp4` 或外链（如 GitHub Release / 云盘），
   并在 PR 描述中附上链接。

---

## 4. 本环境无法自动录制说明（给执行者）

当前自动化环境**未安装 ffmpeg / Playwright / Chromium**，无法由 AI 直接产出 `.mp4`。
因此本交付只提供「可直接照拍的脚本」；实际录制需由你在本地完成。

若希望 AI 辅助生成**静态画面证据**（截图），可在本地执行：

```bash
npm i -D playwright && npx playwright install chromium
# 然后让 AI 用 Playwright 对 S3/S5/S8/S10/S11 截图到 docs/demo-screenshots/
```

截图只能作为辅助证据，**不能替代** README 要求的完整演示视频。
