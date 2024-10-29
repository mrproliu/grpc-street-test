SHELL := /bin/bash

TEST_ROOT := $(shell dirname $(realpath $(lastword $(MAKEFILE_LIST))))
GHZ_VERSION=0.120.0
BIN_DIR := /tmp/ghz-$(GHZ_VERSION)
PATH := $(BIN_DIR):$(PATH)

GRPC_JAVA_JAR := $(TEST_ROOT)/target/test-grpc-java.jar

OS := $(shell uname | tr '[:upper:]' '[:lower:]')
ARCH := $(shell uname -m | sed 's/x86_64/amd64/;s/aarch64/arm64/')

install-ghz:
	@if ! command -v ghz >/dev/null 2>&1; then \
		echo "ghz not found, proceeding with installation..."; \
		if [ ! -d $(BIN_DIR) ]; then \
			mkdir -p $(BIN_DIR); \
		fi; \
		curl -L https://github.com/bojand/ghz/releases/download/v$(GHZ_VERSION)/ghz_$(GHZ_VERSION)_$(OS)_$(ARCH).tar.gz -o $(BIN_DIR)/ghz.tar.gz; \
		tar -xzf $(BIN_DIR)/ghz.tar.gz -C $(BIN_DIR); \
		rm -f $(BIN_DIR)/ghz.tar.gz; \
		echo "Installation completed."; \
	else \
		echo "ghz is already installed."; \
	fi

build:
	./mvnw clean package

show-ips:
	@echo "Fetching all IP addresses on this machine..."
	@hostname -I 2>/dev/null || ip -o -4 addr show | awk '{print $$4}' || ifconfig | grep 'inet ' | awk '{print $$2}'

start-grpc-java: show-ips
	java -jar $(GRPC_JAVA_JAR)

test-grpc: install-ghz
	echo "-------------------------------------------------------------"
	ghz --insecure  --proto ./src/main/proto/hello.proto --call io.github.liuhan.grpc.test.protocol.HelloWorldService.sayHelloSingle -d '{"name":"Joe"}' $(SERVER_HOST):8888
	echo "-------------------------------------------------------------"
