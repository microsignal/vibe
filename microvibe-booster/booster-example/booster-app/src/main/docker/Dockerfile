FROM tomcat:8.5-jre8
LABEL author="Qt <kylinmania@gmail.com>"

ENV JAVA_OPTS="-Xms512m -XX:MetaspaceSize=256m -XX:MaxMetaspaceSize=256m -XX:MinHeapFreeRatio=40 -XX:MaxHeapFreeRatio=70 \
  -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=/data/dump -Duser.timezone=GMT+08" \
  # 默认保留内存  Heap + Method Area + Constant Pool + Thread Stack * num of thread
  RESERVED_MEGABYTES="256"

RUN set -ex; \
  echo "deb http://mirrors.aliyun.com/debian/ stretch main non-free contrib \
    deb http://mirrors.aliyun.com/debian/ stretch-proposed-updates main non-free contrib \
    deb-src http://mirrors.aliyun.com/debian/ stretch main non-free contrib \
    deb-src http://mirrors.aliyun.com/debian/ stretch-proposed-updates main non-free contrib" > /etc/apt/sources.list; \
  rm -rf /var/lib/apt/lists/*; \
  rm -rf webapps/*; \
  ln -sf /usr/share/zoneinfo/Asia/Shanghai /etc/localtime

# 拷 war 包 当前目录为 /xx/xx/tomcat
COPY booster-app.war webapps/ROOT.war
# 修改 tomcat 日志目录
COPY logging.properties conf/logging.properties
COPY entrypoint.sh entrypoint.sh

# 自动设置 jvm Xmx
CMD ["sh", "entrypoint.sh"]

# 健康检查
# interval 两次健康检查的间隔，默认为 30 秒
# timeout 健康检查命令运行超时时间，如果超过这个时间，本次健康检查就被视为失败，默认 30 秒
# retries 当连续失败指定次数后，则将容器状态视为 unhealthy，默认 3 次
# start-period 应用的启动的初始化时间，在启动过程中的健康检查失效不会计入，默认 0 秒；
HEALTHCHECK --interval=5s --timeout=10s --start-period=60s --retries=10 CMD curl -fs http://localhost:8080/ || exit 1

EXPOSE 8080
