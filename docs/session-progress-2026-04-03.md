# Session Progress 2026-04-03

这份文档用于下次继续开发时快速恢复上下文。

## 当前状态

- 项目目录：`D:\SoftwareProjects\ai-ops-agent`
- 远端仓库：`origin/main`
- 当前已经实现第一阶段的最小 Agent 闭环
- 本地环境已经打通，可以直接运行和调试
- 已验证 `POST /api/chat` 能触发真实工具调用并返回结果

## 当前功能

- 基础 Spring Boot 项目骨架
- Spring AI `ChatClient`
- 只读工具调用
- 工具执行审计
- 模拟运维数据源
- 模型错误统一处理
- 提示词驱动的多工具调用策略

## 关键代码位置

- Controller
  - `src/main/java/com/example/aiopsagent/controller/AgentController.java`
- 全局异常处理
  - `src/main/java/com/example/aiopsagent/controller/GlobalExceptionHandler.java`
- Agent 编排
  - `src/main/java/com/example/aiopsagent/service/AgentService.java`
- 工具调用审计
  - `src/main/java/com/example/aiopsagent/service/ToolAuditService.java`
- 模拟业务数据
  - `src/main/java/com/example/aiopsagent/service/MockOpsDataService.java`
- 工具定义
  - `src/main/java/com/example/aiopsagent/tool/OperationsToolbox.java`
- 系统提示词
  - `src/main/resources/prompts/ops-agent-system.txt`

## 本机环境情况

- JDK
  - `C:\Program Files\Eclipse Adoptium\jdk-21.0.10.7-hotspot`
- Maven
  - `D:\SoftwareProjects\tools\apache-maven-3.9.14`
- Maven 国内镜像
  - `C:\Users\Jack\.m2\settings.xml`
  - 当前配置为腾讯云 Maven 镜像

## 当前推荐启动方式

复用 Codex 当前配置里的网关和密钥：

```powershell
cd D:\SoftwareProjects\ai-ops-agent

$env:OPENAI_API_KEY = (Get-Content C:\Users\Jack\.codex\auth.json | ConvertFrom-Json).OPENAI_API_KEY
$env:OPENAI_BASE_URL = "https://globalai.vip"
$env:OPENAI_MODEL = "gpt-5.4"

mvn spring-boot:run
```

## 已验证接口

### 1. 服务清单

```powershell
Invoke-WebRequest -UseBasicParsing http://localhost:8080/api/services
```

结果：正常返回 `payment-service`、`order-service`、`inventory-service`

### 2. Agent 调用

推荐测试请求：

```powershell
Invoke-RestMethod http://localhost:8080/api/chat `
  -Method Post `
  -ContentType "application/json" `
  -Body '{"message":"payment-service 最近超时，这次像不像上次事故？先看当前状态，再结合最近事故告诉我优先排查什么"}' |
ConvertTo-Json -Depth 6
```

已验证现象：

- `answer` 正常返回
- `toolExecutions` 至少能看到 `getServiceStatus`
- 当前提示词已经增强为更倾向同时调用状态工具和事故历史工具

## 今天遇到的问题与结论

### 1. Java / Maven 不在 PATH

处理：

- 安装 JDK 21
- 解压 Maven 到 `D:\SoftwareProjects\tools`
- 配置用户级 `JAVA_HOME` 和 `Path`

### 2. Maven 下载慢

处理：

- 使用腾讯云 Maven 镜像
- 配置文件位置：`C:\Users\Jack\.m2\settings.xml`

### 3. 8080 端口被占用

原因：

- 之前的 `spring-boot:run` 进程未退出

### 4. OpenAI 官方地址不可达

结论：

- 当前网络环境下不适合直接连 `api.openai.com`
- 改为使用 Codex 当前正在使用的兼容 OpenAI 网关

### 5. `Invalid URL (POST /v1/v1/chat/completions)`

原因：

- `OPENAI_BASE_URL` 误写成 `https://globalai.vip/v1`

结论：

- 正确值必须是根地址 `https://globalai.vip`

### 6. DeepSeek `Insufficient Balance`

结论：

- 是模型供应商余额问题，不是项目代码问题
- 为了继续推进，后续切换到 Codex 当前网关

### 7. PowerShell 控制台中文乱码

结论：

- 接口 JSON 本身正常
- 多半是终端显示问题

## 当前确认可用的链路

- 用户问题
- `AgentController`
- `AgentService`
- Spring AI `ChatClient`
- 工具选择
- `OperationsToolbox`
- `MockOpsDataService`
- 返回 `answer + toolExecutions`

## 下次继续时建议的优先级

1. 先验证双工具调用是否稳定出现
2. 如果双工具调用还不稳定，继续调提示词和工具描述
3. 第二阶段开始接 RAG
4. 目标是导入 runbook / FAQ / 架构文档，并返回引用来源

## 下次开新 Codex 可直接复制的提示

```text
项目在 D:\SoftwareProjects\ai-ops-agent。
先阅读 README.md 和 docs/session-progress-2026-04-03.md。
当前第一阶段最小 agent 已跑通，使用 Spring Boot + Spring AI + OpenAI-compatible gateway。
先验证 /api/chat 的双工具调用是否稳定，再开始第二阶段 RAG 接入。
```

## 注意事项

- 不要把任何真实 API Key 提交进仓库
- 如果再次使用 DeepSeek，需要先确认账户余额
- 如果继续复用 Codex 网关，`OPENAI_BASE_URL` 必须是 `https://globalai.vip`
