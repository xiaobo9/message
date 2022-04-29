[![codecov](https://codecov.io/gh/xiaobo9/gitlab-message/branch/master/graph/badge.svg)](https://codecov.io/gh/xiaobo9/gitlab-message)

# gitalb 事件发送消息

## test

```bash
mvn clean test -DsurefireArgLine="-Dfoo=bar"
```

## 构建

通过 google jib maven plugin 生成 docker 镜像

mvn jib:build

## gitlab hook

## 目前的功能

### 合并请求的消息

| 消息  |  接收者 |
| ------------ | ------------ |
|  开启合并请求 |  被指定的合并人 |
|  合并请求被合并 |  创建合并请求的人 |

### pipeline 流水线的消息

| 消息  |  接收者 |
| ------------ | ------------ |
|  `pipeline` 构建失败 | `pipeline` 的触发者  |

### issues 的消息

| 消息  |  接收者 |
| ------------ | ------------ |
|  开启 issue |  issue 中被指定的处理人 |

### 评论 的消息

| 消息  |  接收者 |
| ------------ | ------------ |
|  合并请求的评论信息 | 提交合并请求的人和合并请求中 `@` 的人(会排除合并请求的创建者)  |

### sonar 问题 的消息

每天定时 10点 获取 `http://sonarqube.server` 上的检查结果，根据 sonar 上的信息给引入人发送提醒信息

## 后续的功能规划

* `pipeline` 部署成功后，发送提示消息
* ~~执行 `sonar` 分析后，如果有新增的缺陷，发出提示信息(部分实现)~~
* 下班的时候统计当天的消息状态
* ~~评论什么的也慢慢加上吧~~

### 统计些什么呢？

* 构建情况，构建次数，成功次数
* 合并情况，提了几个合并，合并了几个别人的请求
* `sonar` 检查结果

## CHANGE LOG

2022年4月4日 升级到 java11, spring boot 2.6.6 和 junit 5，删除对 junit 4 的依赖，调整单元测试代码

## 依赖

试试 <https://github.com/springdoc/springdoc-openapi>

## restdocs

https://docs.spring.io/spring-restdocs/docs/current/reference/html5/#getting-started-build-configuration
