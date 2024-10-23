# Copyright 2022, OK2 Consulting Ltd
#
# Licensed to the Apache Software Foundation (ASF) under one or more
# contributor license agreements.  See the NOTICE file distributed with
# this work for additional information regarding copyright ownership.
# The ASF licenses this file to You under the Apache License, Version 2.0
# (the "License"); you may not use this file except in compliance with
# the License.  You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

FROM httpd:2.4
MAINTAINER olegk@apache.org

ENV var_dir /var/httpd
ENV www_dir ${var_dir}/www
ENV conf_dir /usr/local/apache2/conf

RUN mkdir -p ${var_dir}
RUN mkdir -p ${www_dir}

RUN echo 'LoadModule http2_module modules/mod_http2.so' >> ${conf_dir}/httpd.conf && \
 echo 'Include conf/extra/httpd-default.conf' >> ${conf_dir}/httpd.conf && \
 echo 'Include conf/extra/httpd-h2c.conf' >> ${conf_dir}/httpd.conf

COPY httpd-default.conf ${conf_dir}/extra/httpd-default.conf
COPY httpd-h2c.conf ${conf_dir}/extra/httpd-h2c.conf

RUN echo 'aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa' >> ${www_dir}/aaa
RUN echo 'bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb' >> ${www_dir}/bbb
RUN echo 'ccccccccccccccccccccccccccccccccccccccccccccccccccc' >> ${www_dir}/ccc
RUN echo 'I am being very pushy' >> ${www_dir}/pushy

#          return new GenericContainer<>(new ImageFromDockerfile()
#                   .withFileFromClasspath("httpd-default.conf", "docker/httpd/httpd-default.conf")
#                   .withFileFromClasspath("httpd-h2c.conf", "docker/httpd/httpd-h2c.conf")
#                   .withFileFromClasspath("httpd-ssl.conf", "docker/httpd/httpd-ssl.conf")
#                   .withFileFromClasspath("server-cert.pem", "docker/server-cert.pem")
#                   .withFileFromClasspath("server-key.pem", "docker/server-key.pem")
#                   .withFileFromString("pushy", PUSHY)
#                   .withFileFromString("aaa", AAA)
#                   .withFileFromString("bbb", BBB)
#                   .withFileFromString("ccc", CCC)
#                   .withDockerfileFromBuilder(builder -> builder
#                           .from("httpd:2.4")
#                           .env("var_dir", "/var/httpd")
#                           .env("www_dir", "${var_dir}/www")
#                           .run("mkdir -p ${var_dir}")
#                           .run("mkdir -p ${www_dir}")
#                           .run("echo '\\n" +
#                                   "LoadModule http2_module modules/mod_http2.so\\n" +
#                                   "LoadModule ssl_module modules/mod_ssl.so\\n" +
#                                   "Include conf/extra/httpd-default.conf\\n" +
#                                   "Include conf/extra/httpd-h2c.conf\\n" +
#                                   "Include conf/extra/httpd-ssl.conf\\n" +
#                                   "'" +
#                                   " >> /usr/local/apache2/conf/httpd.conf")
#                           .copy("httpd-default.conf", "/usr/local/apache2/conf/extra/httpd-default.conf")
#                           .copy("httpd-h2c.conf", "/usr/local/apache2/conf/extra/httpd-h2c.conf")
#                           .copy("httpd-ssl.conf", "/usr/local/apache2/conf/extra/httpd-ssl.conf")
#                           .copy("server-cert.pem", "/usr/local/apache2/conf/server-cert.pem")
#                           .copy("server-key.pem", "/usr/local/apache2/conf/server-key.pem")
#                           .copy("pushy", "${www_dir}/")
#                           .copy("aaa", "${www_dir}/")
#                           .copy("bbb", "${www_dir}/")
#                           .copy("ccc", "${www_dir}/")
#                           .build()))
#                   .withNetwork(network)
#                   .withNetworkAliases(APACHE_HTTPD)
#                   .withLogConsumer(new Slf4jLogConsumer(LOG))
#                   .withExposedPorts(HTTP_PORT, H2C_PORT, HTTPS_PORT);