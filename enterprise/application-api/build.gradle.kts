/*
 * Copyright 2024 flex Inc. - All Rights Reserved.
 */

dependencies {
    implementation(project(":enterprise:schema"))
    implementation(project(":enterprise:api"))
    implementation(project(":enterprise:repository-jdbc"))

    implementation("org.testcontainers:mysql")
    runtimeOnly("com.mysql:mysql-connector-j") {
        exclude(group = "com.google.protobuf", module = "protobuf-java")
    }
}
