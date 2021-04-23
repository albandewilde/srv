FROM gradle

WORKDIR /usr/src/srv/

COPY . .

RUN ./gradlew build
RUN tar -C /bin -xvf app/build/distributions/app.tar

CMD ["/bin/app/bin/./app"]
