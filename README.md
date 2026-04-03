# AI Ops Agent

一个面向 Java 工程师的入门型 AI Agent 项目。

这个项目刻意不从“多智能体”开始，而是先把一个真实企业场景里的单 Agent 跑通：

- 用户提问运维/排障问题
- Agent 根据问题决定是否调用工具
- 工具返回服务健康状态、历史故障摘要等结构化数据
- Agent 基于工具结果生成最终答复
- 接口返回本次实际触发的工具执行记录，便于学习和调试

## 第一阶段目标

当前版本只覆盖 Agent 最小闭环：

- `Spring Boot + Spring AI`
- `ChatClient`
- `@Tool` 工具定义
- 受控、只读工具调用
- 简单的接口层和调用审计

第二阶段再接：

- RAG
- pgvector
- 文档导入
- 引用来源

## 你会学到什么

- AI Agent 不等于聊天机器人，本质是 `LLM + tools + control loop`
- 模型不能直接访问你的系统，真正执行工具的是你的 Java 应用
- 工具描述写得越清楚，模型越容易正确选择它
- 工程里必须记录工具调用，否则你无法定位 hallucination 和误判

## 技术栈

- Java 21
- Spring Boot 3.5
- Spring AI 1.1.2
- OpenAI-Compatible Chat Model

## 启动前准备

1. 安装 JDK 21
2. 安装 Maven 3.9+
3. 设置环境变量 `OPENAI_API_KEY`
4. 可选设置 `OPENAI_BASE_URL`
5. 可选设置 `OPENAI_MODEL`

PowerShell 示例：

```powershell
$env:OPENAI_API_KEY="your_api_key"
$env:OPENAI_BASE_URL="https://api.openai.com"
$env:OPENAI_MODEL="gpt-4o-mini"
```

如果你使用兼容 OpenAI 协议的中转或聚合服务，只需要替换 `OPENAI_BASE_URL` 和 `OPENAI_MODEL` 即可。

例如当前 Codex 配置使用的是一个 OpenAI 兼容网关：

```powershell
$env:OPENAI_API_KEY="your_gateway_key"
$env:OPENAI_BASE_URL="https://globalai.vip"
$env:OPENAI_MODEL="gpt-5.4"
```

注意：`OPENAI_BASE_URL` 这里必须填根地址，不要手动带 `/v1`，因为 Spring AI 会自动拼接 `/v1/chat/completions`。

## 当前推荐启动方式

如果你想直接复用本机 Codex 的网关认证，可以这样启动：

```powershell
cd D:\SoftwareProjects\ai-ops-agent

$env:OPENAI_API_KEY = (Get-Content C:\Users\Jack\.codex\auth.json | ConvertFrom-Json).OPENAI_API_KEY
$env:OPENAI_BASE_URL = "https://globalai.vip"
$env:OPENAI_MODEL = "gpt-5.4"

mvn spring-boot:run
```

## 运行

```powershell
mvn spring-boot:run
```

应用默认运行在 `http://localhost:8080`

## 接口

### 1. 查看模拟服务数据

```http
GET /api/services
```

### 2. 发送排障问题

```http
POST /api/chat
Content-Type: application/json
```

请求体：

```json
{
  "message": "payment-service 最近超时，先帮我看看状态，再告诉我优先排查什么"
}
```

响应会包含：

- `answer`: 模型最终回答
- `toolExecutions`: 本次调用中实际触发的工具
- `createdAt`: 响应时间

如果你想观察多工具调用，优先测试这类问题：

```json
{
  "message": "payment-service 最近超时，这次像不像上次事故？先看当前状态，再结合最近事故告诉我优先排查什么"
}
```

PowerShell 调试时推荐：

```powershell
Invoke-RestMethod http://localhost:8080/api/chat `
  -Method Post `
  -ContentType "application/json" `
  -Body '{"message":"payment-service 最近超时，这次像不像上次事故？先看当前状态，再结合最近事故告诉我优先排查什么"}' |
ConvertTo-Json -Depth 6
```

## 建议你怎么练

先不要急着扩展功能，先重复做这几件事：

1. 用不同问题测试模型会不会正确选择工具
2. 改写工具描述，观察调用效果变化
3. 改动模拟数据，观察模型回答是否稳定
4. 故意提一个模糊问题，观察模型是否会先询问澄清

## 已完成能力

- 基于 `Spring Boot + Spring AI` 搭好最小 Agent 骨架
- 接通 `ChatClient`
- 接入只读工具：
  - `listInspectableServices`
  - `getServiceStatus`
  - `getRecentIncidentSummary`
- 返回工具执行审计信息 `toolExecutions`
- 增加统一异常处理，能把上游模型错误转成可读 JSON
- 已验证：
  - `GET /api/services`
  - `POST /api/chat`
  - 使用兼容 OpenAI 协议的网关调用成功

## 常见问题

### 1. `Port 8080 was already in use`

说明上一次 `spring-boot:run` 还没停掉。先结束旧进程，或者换端口：

```powershell
mvn spring-boot:run "-Dspring-boot.run.arguments=--server.port=8081"
```

### 2. `Invalid URL (POST /v1/v1/chat/completions)`

说明 `OPENAI_BASE_URL` 多写了 `/v1`。

错误示例：

```powershell
$env:OPENAI_BASE_URL="https://globalai.vip/v1"
```

正确示例：

```powershell
$env:OPENAI_BASE_URL="https://globalai.vip"
```

### 3. `Insufficient Balance`

说明上游模型服务余额不足，不是本项目代码问题。

### 4. PowerShell 中文乱码

接口返回通常是正常 UTF-8，乱码多半是控制台显示问题。优先用：

```powershell
... | ConvertTo-Json -Depth 6
```

## 下一阶段

- 加入 `pgvector + PostgreSQL`
- 导入 runbook、FAQ、架构文档
- 让 Agent 在工具调用前后都能做文档检索
- 给敏感动作增加人工确认
- 补指标、trace、评测集

## 目录说明

`src/main/java/com/example/aiopsagent/controller`

HTTP 接口。

`src/main/java/com/example/aiopsagent/service`

Agent 调用编排、工具审计、模拟业务数据。

`src/main/java/com/example/aiopsagent/tool`

模型可调用的工具定义。

`src/main/resources/prompts`

系统提示词。

## 交接文档

当前阶段总结、调试记录和下次继续的入口见：

- [docs/session-progress-2026-04-03.md](/D:/SoftwareProjects/ai-ops-agent/docs/session-progress-2026-04-03.md)
