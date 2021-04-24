FROM gradle

WORKDIR /usr/src/srv/

COPY . .

RUN ./gradlew build
RUN tar -C /bin -xvf app/build/distributions/app.tar

# Directory where files are stored
RUN mkdir /files/

CMD ["/bin/app/bin/./app"]
