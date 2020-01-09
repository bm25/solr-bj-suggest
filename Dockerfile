# FROM parrotstream/centos-openjdk:8
# FROM centos:7.4.7247
FROM centos:7.5.1804

RUN yum update -y
RUN yum install -y java-1.8.0-openjdk java-1.8.0-openjdk-devel
RUN yum clean all

ENV HOME /root
ENV WORKDIR /root
ENV JAVA_HOME /etc/alternatives/java