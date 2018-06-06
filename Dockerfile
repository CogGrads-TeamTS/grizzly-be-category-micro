FROM openjdk:8.0-jre8-alpine
MAINTAINER Piotr Minkowski <piotr.minkowski@gmail.com>
ADD target/category-service.jar category-service.jar
ENTRYPOINT ["java", "-jar", "/category-service.jar"]
EXPOSE 3333