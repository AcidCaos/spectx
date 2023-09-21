FROM nginx:1.25.2-alpine
LABEL maintainer="AcidCaos <acidcaos1@gmail.com>"

# SpectX Desktop forces listening to localhost (0.0.0.0 or others cannot be set even modifying wgui.host at sx.conf).
# Instead, the traffic is proxied through Nginx. Not too proud about this.

# Setup NGINX to be runned as daemon and proxy to SpectX
RUN echo "daemon on;" >> /etc/nginx/nginx.conf
RUN rm /etc/nginx/conf.d/default.conf
COPY nginx/spectx.conf /etc/nginx/conf.d/default.conf

# Install Java JDK 11
RUN apk add openjdk11

# Setup SpectX
WORKDIR /usr/share/java/spectx
COPY lib/ lib
COPY conf/ conf
ENV SPECTX_HOME=/usr/share/java/spectx

EXPOSE 80/tcp

STOPSIGNAL SIGTERM

# Start Nginx daemon and SpectX
CMD ["/bin/sh", "-c", "nginx; java -jar /usr/share/java/spectx/lib/spectx.jar"]