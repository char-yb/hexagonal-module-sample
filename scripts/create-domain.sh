#!/bin/bash

# 새로운 도메인 모듈을 생성하는 스크립트
# Usage: ./scripts/create-domain.sh <domain-name>

set -e

if [ -z "$1" ]; then
    echo "Usage: ./scripts/create-domain.sh <domain-name>"
    echo "Example: ./scripts/create-domain.sh attendance"
    exit 1
fi

DOMAIN_NAME=$1
BASE_PACKAGE="team/flex/module/sample/${DOMAIN_NAME}"
DOMAIN_CAPITALIZED="$(tr '[:lower:]' '[:upper:]' <<< ${DOMAIN_NAME:0:1})${DOMAIN_NAME:1}"

echo "Creating domain: ${DOMAIN_NAME}"

# 모듈 목록
MODULES=("model" "exception" "infrastructure" "service" "repository-jdbc" "api" "schema" "application-api")

for MODULE in "${MODULES[@]}"; do
    MODULE_PATH="${DOMAIN_NAME}/${MODULE}"
    echo "Creating module: ${MODULE_PATH}"
    
    # 디렉토리 생성
    mkdir -p "${MODULE_PATH}/src/main/kotlin/${BASE_PACKAGE}"
    mkdir -p "${MODULE_PATH}/src/main/resources"
    
    # build.gradle.kts 생성
    cat > "${MODULE_PATH}/build.gradle.kts" << EOF
/*
 * Copyright 2024 flex Inc. - All Rights Reserved.
 */

dependencies {
EOF

    # 모듈별 의존성 추가
    case $MODULE in
        "model")
            echo "    api(project(\":common:model\"))" >> "${MODULE_PATH}/build.gradle.kts"
            ;;
        "exception")
            echo "    api(project(\":common:exception\"))" >> "${MODULE_PATH}/build.gradle.kts"
            ;;
        "infrastructure")
            echo "    api(project(\":${DOMAIN_NAME}:model\"))" >> "${MODULE_PATH}/build.gradle.kts"
            ;;
        "service")
            echo "    api(project(\":${DOMAIN_NAME}:model\"))" >> "${MODULE_PATH}/build.gradle.kts"
            echo "    implementation(project(\":${DOMAIN_NAME}:exception\"))" >> "${MODULE_PATH}/build.gradle.kts"
            echo "    implementation(project(\":${DOMAIN_NAME}:infrastructure\"))" >> "${MODULE_PATH}/build.gradle.kts"
            ;;
        "repository-jdbc")
            echo "    implementation(project(\":${DOMAIN_NAME}:infrastructure\"))" >> "${MODULE_PATH}/build.gradle.kts"
            echo "    implementation(project(\":${DOMAIN_NAME}:model\"))" >> "${MODULE_PATH}/build.gradle.kts"
            mkdir -p "${MODULE_PATH}/src/integrationTest/kotlin/${BASE_PACKAGE}"
            mkdir -p "${MODULE_PATH}/src/integrationTest/resources"
            ;;
        "api")
            echo "    implementation(project(\":${DOMAIN_NAME}:service\"))" >> "${MODULE_PATH}/build.gradle.kts"
            echo "    implementation(project(\":${DOMAIN_NAME}:exception\"))" >> "${MODULE_PATH}/build.gradle.kts"
            ;;
        "application-api")
            echo "    implementation(project(\":${DOMAIN_NAME}:schema\"))" >> "${MODULE_PATH}/build.gradle.kts"
            echo "    implementation(project(\":${DOMAIN_NAME}:api\"))" >> "${MODULE_PATH}/build.gradle.kts"
            echo "    implementation(project(\":${DOMAIN_NAME}:repository-jdbc\"))" >> "${MODULE_PATH}/build.gradle.kts"
            echo "" >> "${MODULE_PATH}/build.gradle.kts"
            echo "    implementation(\"org.testcontainers:mysql\")" >> "${MODULE_PATH}/build.gradle.kts"
            echo "    runtimeOnly(\"com.mysql:mysql-connector-j\") {" >> "${MODULE_PATH}/build.gradle.kts"
            echo "        exclude(group = \"com.google.protobuf\", module = \"protobuf-java\")" >> "${MODULE_PATH}/build.gradle.kts"
            echo "    }" >> "${MODULE_PATH}/build.gradle.kts"
            mkdir -p "${MODULE_PATH}/src/integrationTest/kotlin/${BASE_PACKAGE}"
            ;;
    esac
    
    echo "}" >> "${MODULE_PATH}/build.gradle.kts"
    
    # gradle.properties 생성
    TYPE="kotlin-lib"
    case $MODULE in
        "service"|"repository-jdbc")
            TYPE="kotlin-boot"
            ;;
        "api")
            TYPE="kotlin-boot-mvc"
            ;;
        "repository-jdbc")
            TYPE="kotlin-boot-jdbc-repository"
            ;;
        "application-api")
            TYPE="kotlin-boot-mvc-application"
            ;;
    esac
    
    cat > "${MODULE_PATH}/gradle.properties" << EOF
#
# Copyright 2024 flex Inc. - All Rights Reserved.
#
type=${TYPE}
group=team.flex.module.sample.${DOMAIN_NAME}
EOF

done

# settings.gradle.kts 자동 업데이트
echo ""
echo "Updating settings.gradle.kts..."

SETTINGS_FILE="settings.gradle.kts"

# 백업 생성
cp "$SETTINGS_FILE" "${SETTINGS_FILE}.backup"
echo "Backup created: ${SETTINGS_FILE}.backup"

# 이미 추가되어 있는지 확인
if grep -q "include(\":${DOMAIN_NAME}:" "$SETTINGS_FILE"; then
    echo "⚠️  Warning: Domain '${DOMAIN_NAME}' already exists in settings.gradle.kts"
    echo "Skipping settings.gradle.kts update."
    rm "${SETTINGS_FILE}.backup"
else
    # application-api 앞에 새 도메인 include 문들 삽입
    # macOS와 Linux 호환을 위해 임시 파일 사용
    awk -v domain="${DOMAIN_NAME}" '
    /^include\(":application-api"\)/ {
        # application-api 앞에 빈 줄과 새 도메인 추가
        printf "\n"
        print "include(\":" domain ":api\")"
        print "include(\":" domain ":application-api\")"
        print "include(\":" domain ":exception\")"
        print "include(\":" domain ":infrastructure\")"
        print "include(\":" domain ":model\")"
        print "include(\":" domain ":repository-jdbc\")"
        print "include(\":" domain ":schema\")"
        print "include(\":" domain ":service\")"
        print ""
        print $0
        next
    }
    { print }
    ' "$SETTINGS_FILE" > "${SETTINGS_FILE}.tmp"
    
    mv "${SETTINGS_FILE}.tmp" "$SETTINGS_FILE"
    
    echo "✅ Successfully added ${DOMAIN_NAME} modules to settings.gradle.kts"
    rm "${SETTINGS_FILE}.backup"
fi

echo ""
echo "========================================="
echo "✨ Domain '${DOMAIN_NAME}' created successfully!"
echo "========================================="
echo ""
echo "Next steps:"
echo "1. Review the generated modules in ${DOMAIN_NAME}/"
echo "2. Run: ./gradlew build"
echo "3. Start implementing your domain logic"
echo ""
echo "Created modules:"
for MODULE in "${MODULES[@]}"; do
    echo "  - ${DOMAIN_NAME}:${MODULE}"
done
