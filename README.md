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
- OpenAI Chat Model

## 启动前准备

1. 安装 JDK 21
2. 安装 Maven 3.9+
3. 设置环境变量 `OPENAI_API_KEY`
4. 可选设置 `OPENAI_MODEL`，默认是 `gpt-4o-mini`

PowerShell 示例：

```powershell
$env:OPENAI_API_KEY="your_api_key"
$env:OPENAI_MODEL="gpt-4o-mini"
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

## 建议你怎么练

先不要急着扩展功能，先重复做这几件事：

1. 用不同问题测试模型会不会正确选择工具
2. 改写工具描述，观察调用效果变化
3. 改动模拟数据，观察模型回答是否稳定
4. 故意提一个模糊问题，观察模型是否会先询问澄清

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
