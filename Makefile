#
# Makefile to manage this project's development environment.
# This will be mostly clojure!
#
TAG=mjamesruggiero/swanson

#Directory that this Makefile is in.
mkfile_path := $(abspath $(lastword $(MAKEFILE_LIST)))
current_path := $(dir $(mkfile_path))

# Builds the development docker file
docker-build:
	docker build -t ${TAG} .

# Clean this docker image
docker-clean:
	-docker rmi $(TAG)

docker-run:
	docker run --rm -p 8080:8080 ${TAG}

# Reset everything back to the original version (last git commit)
src-reset:
	git reset --hard
	git clean -fd
