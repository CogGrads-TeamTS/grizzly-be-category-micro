FROM openjdk:8-jre-alpine
MAINTAINER Piotr Minkowski <piotr.minkowski@gmail.com>
ADD target/category-service.jar category-service.jar
ENTRYPOINT ["java", "-jar", "/category-service.jar", "-Xmx512m"]
EXPOSE 3333