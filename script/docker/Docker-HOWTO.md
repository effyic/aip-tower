# Docker 部署（外接 PostgreSQL / Redis / Nacos）

仅构建并启动 **server**、**admin** 容器。中间件外接；**地址/用户名** 在 `application-{profile}.yaml`，**密码** 在根目录 `aiptower.env`。

## 前置

```bash
cp aiptower.env.example aiptower.env
vim aiptower.env   # 仅填 DB_PASSWORD、REDIS_PASSWORD、NACOS_PASSWORD
```

## 构建 jar 并启动

```bash
mvn clean package -DskipTests

cd script/docker
docker compose --env-file ../../aiptower.env \
  -f docker-compose.yml \
  --project-directory ../.. \
  up -d --build
```

`--env-file ../../aiptower.env` 供 compose 变量替换（相对当前目录 `script/docker`）；容器内密码由 compose 里 `env_file: aiptower.env` 注入（相对 `--project-directory` 即仓库根目录）。勿混用 `../../aiptower.env` 作 `env_file`，否则会解析到错误路径。

仅后端：

```bash
docker compose --env-file ../../aiptower.env \
  -f docker-compose.yml \
  --project-directory ../.. \
  up -d --build server
```

## 端口

| 服务 | 地址 |
|------|------|
| API | http://localhost:48090/admin-api |
| 管理端 | http://localhost:8080 |

## 容器访问宿主机上的中间件

`docker-compose.yml` 已配置 `host.docker.internal`。若中间件与容器同机部署，可在对应 `application-{profile}.yaml` 中将地址改为 `host.docker.internal`。

## 拉取 `eclipse-temurin:21-jre` 超时

与官方一致使用 Docker Hub 镜像。若出现 `DeadlineExceeded`，在 **Docker Desktop → Settings → Docker Engine** 配置 `registry-mirrors` 后重启，再重新 build。
