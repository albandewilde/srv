image:
	docker build --no-cache -t srv .
ctn-run:
	docker run --name srv -p 7000:7000 srv
ctn-rm:
	docker container rm srv

srv:
	docker run --name srv --volume ${PWD}/app/build/:/usr/src/srv/app/build/ srv ./gradlew build
	@echo Build is in ./app/build/distributions/
