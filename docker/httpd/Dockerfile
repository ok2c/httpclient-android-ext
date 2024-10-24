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
 echo 'Include conf/extra/httpd-h2c.conf' >> ${conf_dir}/httpd.conf && \
 echo 'Include conf/extra/httpd-ssl.conf' >> ${conf_dir}/httpd.conf

COPY httpd-default.conf ${conf_dir}/extra/httpd-default.conf
COPY httpd-h2c.conf ${conf_dir}/extra/httpd-h2c.conf
COPY httpd-ssl.conf ${conf_dir}/extra/httpd-ssl.conf
COPY server-cert.pem ${conf_dir}/server-cert.pem
COPY server-key.pem ${conf_dir}/server-key.pem

RUN echo 'aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa' >> ${www_dir}/aaa
RUN echo 'bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb' >> ${www_dir}/bbb
RUN echo 'ccccccccccccccccccccccccccccccccccccccccccccccccccc' >> ${www_dir}/ccc
RUN echo 'I am being very pushy' >> ${www_dir}/pushy
