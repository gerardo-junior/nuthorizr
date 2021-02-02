# Setting packages version
ARG LEIN_VERSION="2.8.3"
ARG OPENJDK_VERSION="8"

FROM library/clojure:openjdk-$OPENJDK_VERSION-lein-$LEIN_VERSION-alpine 

ENV USER "clojure"
ENV WORKDIR "/src"

# Creating project directory
RUN mkdir -p $WORKDIR

# Creating user and empowering sudo
RUN set -xe && \
    addgroup -g 1000 $USER && \
    adduser -u 1000 -G $USER -s /bin/sh -D $USER && \
    apk --update add --virtual .persistent-deps sudo && \
    echo "$USER ALL=(ALL) NOPASSWD: ALL" >> /etc/sudoers.d/default && \
    chown -Rf $USER $WORKDIR

COPY ./tools /opt/tools
RUN chmod -R +x /opt/tools/

# Setting project directory
VOLUME [$WORKDIR]
WORKDIR $WORKDIR
USER $USER
ENTRYPOINT ["/bin/sh", "/opt/tools/entrypoint.sh"]