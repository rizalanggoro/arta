PROJECT_ROOT := $(shell cmd /c cd)

download-swagger:
	curl -o swagger.json http://localhost:8080/swagger/doc.json

copy-swagger:
	copy ..\backend\docs\swagger.json .\swagger.json

delete-old-generated:
	@if exist "${PROJECT_ROOT}\\openapi" ( rmdir /s /q "${PROJECT_ROOT}\\openapi" ) else ( echo "Directory does not exist. Skipping deletion." )

api: delete-old-generated copy-swagger
	openapi-generator-cli generate \
      -i ./swagger.json \
      -g kotlin \
      -o ./openapi \
      --additional-properties=library=jvm-retrofit2,packageName=id.my.rizalanggoro.arta.openapi,serializationLibrary=kotlinx_serialization,useCoroutines=true \
      --global-property=apiTests=false,modelTests=false

.PHONY: download-swagger api