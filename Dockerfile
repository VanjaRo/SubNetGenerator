FROM openjdk:12.0.2

COPY /out/production/SubNetGenerator .
WORKDIR .
ENTRYPOINT ["java","com.ivan.Main"]

