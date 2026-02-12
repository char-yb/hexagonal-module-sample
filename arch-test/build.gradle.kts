/*
 * Copyright 2024 flex Inc. - All Rights Reserved.
 */

dependencies {
    // ArchUnit 의존성
    testImplementation("com.tngtech.archunit:archunit-junit5:1.3.0")
    
    // 프로젝트 모듈 의존성 (테스트 대상)
    testImplementation(project(":common:model"))
    testImplementation(project(":common:exception"))
    
    testImplementation(project(":corehr:model"))
    testImplementation(project(":corehr:exception"))
    testImplementation(project(":corehr:infrastructure"))
    testImplementation(project(":corehr:service"))
    testImplementation(project(":corehr:repository-jdbc"))
    testImplementation(project(":corehr:api"))
    
    testImplementation(project(":payroll:model"))
    testImplementation(project(":payroll:exception"))
    testImplementation(project(":payroll:infrastructure"))
    testImplementation(project(":payroll:service"))
    testImplementation(project(":payroll:repository-jdbc"))
    testImplementation(project(":payroll:api"))
}
