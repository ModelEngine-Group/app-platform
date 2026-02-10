# ECE场景安装指导

## 安装命令

执行以下命令进行ECE场景安装：

```bash
bash tools/install.sh --ns model-engine --storage-class=sc-system-manage --repo=11.11.11.11:12345/xxxxx/repo/ --repo-user=xxx
```

## 命令参数说明

| 参数 | 说明 |
|------|------|
| ns | 指定命名空间。命名空间需要与OMS安装的命名空间一致 |
| storage-class | 用于指定使用的存储类参数。保持和OMS、其他使能模块使用的一致 |
| repo | 指定远程镜像仓地址。保持和OMS、其他使能模块使用的一致 |
| repo-user | 指定VDC管理员镜像仓库用户名。保持和OMS、其他使能模块使用的一致 |

## 安装过程说明

执行安装命令后，系统会提示输入VDC管理员镜像仓库密码。输入正确密码后，会显示"Docker login success"提示信息，安装将继续进行。如果密码输入错误，安装将失败，此时需要重新执行安装指令，再次输入正确的密码即可。

## 查看安装状态

安装完成后，可通过以下命令查看Pod状态：

```bash
kubectl get pods -n model-engine
```

确保所有服务Pod状态为Running。正常情况下，应显示如下状态：

| NAME | READY | STATUS |
|------|-------|--------|
| app-engine-builder-575787b478-7llhd | 1/1 | Running |
| app-engine-fit-registry-7b58c796b8-xwz57 | 1/1 | Running |
| app-engine-fit-runtime-7fcd587659-n742w | 3/3 | Running |
| app-engine-gateway-8574bf6985-wvqjc | 1/1 | Running |
| app-engine-jade-db-578f86fd7c-gdp27 | 1/1 | Running |
