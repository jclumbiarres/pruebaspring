.PHONY: build clean test run-dev run-prod dependencies check help all

# Variables
GRADLE = ./gradlew

build:
	$(GRADLE) build

clean:
	$(GRADLE) clean

test:
	$(GRADLE) test

run-dev:
	docker compose --env-file .env.dev up

run-prod:
	docker compose --env-file .env.prod up -d

dependencies:
	$(GRADLE) dependencies


all: clean build test

help:
	@echo "Targets disponibles:"
	@echo "  build        - Construir el proyecto"
	@echo "  clean        - Limpiar archivos generados"
	@echo "  test         - Ejecutar tests"
	@echo "  run-dev      - Ejecutar aplicacion en modo desarrollo"
	@echo "  run-prod     - Ejecutar aplicacion en modo produccion"
	@echo "  dependencies - Mostrar dependencias del proyecto"
	@echo "  all          - Ejecutar clean, build y test"
