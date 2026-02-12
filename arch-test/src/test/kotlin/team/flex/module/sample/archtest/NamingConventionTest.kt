/*
 * Copyright 2024 flex Inc. - All Rights Reserved.
 */

package team.flex.module.sample.archtest

import com.tngtech.archunit.core.domain.JavaClasses
import com.tngtech.archunit.core.importer.ClassFileImporter
import com.tngtech.archunit.core.importer.ImportOption
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

/**
 * 네이밍 규칙을 검증하는 테스트
 */
class NamingConventionTest {

    companion object {
        private lateinit var importedClasses: JavaClasses

        @JvmStatic
        @BeforeAll
        fun setup() {
            importedClasses = ClassFileImporter()
                .withImportOption(ImportOption.DoNotIncludeTests())
                .importPackages("team.flex.module.sample")
        }
    }

    @Test
    @DisplayName("Repository 인터페이스는 'Repository'로 끝나야 함")
    fun repositoryInterfacesShouldEndWithRepository() {
        val rule = classes()
            .that().resideInAPackage("..infrastructure..")
            .and().areInterfaces()
            .and().areTopLevelClasses()
            .should().haveSimpleNameEndingWith("Repository")
            .because("Repository interfaces should follow naming convention")

        rule.check(importedClasses)
    }

    @Test
    @DisplayName("Repository 구현체는 'Repository' 또는 'RepositoryImpl'로 끝나야 함")
    fun repositoryImplementationsShouldFollowNamingConvention() {
        val rule = classes()
            .that().resideInAPackage("..repository..")
            .and().areTopLevelClasses()
            .and().areNotInterfaces()
            .and().haveSimpleNameContaining("Repository")
            .should().haveSimpleNameEndingWith("Repository")
            .orShould().haveSimpleNameEndingWith("RepositoryImpl")
            .orShould().haveSimpleNameEndingWith("JdbcRepository")
            .orShould().haveSimpleNameEndingWith("Entity")
            .orShould().haveSimpleNameEndingWith("AutoConfiguration")
            .because("Repository implementations should follow naming convention")

        rule.check(importedClasses)
    }

    @Test
    @DisplayName("Service 인터페이스는 'Service'로 끝나야 함")
    fun serviceInterfacesShouldEndWithService() {
        val rule = classes()
            .that().resideInAPackage("..service..")
            .and().areInterfaces()
            .and().areTopLevelClasses()
            .should().haveSimpleNameEndingWith("Service")
            .because("Service interfaces should follow naming convention")

        rule.check(importedClasses)
    }

    @Test
    @DisplayName("Service 구현체는 'ServiceImpl'로 끝나야 함")
    fun serviceImplementationsShouldEndWithServiceImpl() {
        val rule = classes()
            .that().resideInAPackage("..service..")
            .and().areNotInterfaces()
            .and().areTopLevelClasses()
            .and().haveSimpleNameContaining("Service")
            .and().haveSimpleNameNotEndingWith("AutoConfiguration")
            .should().haveSimpleNameEndingWith("ServiceImpl")
            .because("Service implementations should follow naming convention")

        rule.check(importedClasses)
    }

    @Test
    @DisplayName("Controller는 'Controller' 또는 'ApiController'로 끝나야 함")
    fun controllersShouldFollowNamingConvention() {
        val rule = classes()
            .that().resideInAPackage("..api..")
            .and().areTopLevelClasses()
            .and().areNotInterfaces()
            .and().haveSimpleNameNotEndingWith("AutoConfiguration")
            .and().haveSimpleNameNotEndingWith("Request")
            .and().haveSimpleNameNotEndingWith("Response")
            .should().haveSimpleNameEndingWith("Controller")
            .orShould().haveSimpleNameEndingWith("ApiController")
            .because("Controllers should follow naming convention")

        rule.check(importedClasses)
    }

    @Test
    @DisplayName("Exception 클래스는 'Exception'으로 끝나야 함")
    fun exceptionsShouldEndWithException() {
        val rule = classes()
            .that().resideInAPackage("..exception..")
            .and().areTopLevelClasses()
            .should().haveSimpleNameEndingWith("Exception")
            .because("Exception classes should follow naming convention")

        rule.check(importedClasses)
    }

    @Test
    @DisplayName("Model 클래스는 적절한 네이밍을 따라야 함")
    fun modelClassesShouldFollowNamingConvention() {
        val rule = classes()
            .that().resideInAPackage("..model..")
            .and().areTopLevelClasses()
            .and().areNotInterfaces()
            .should().haveSimpleNameNotEndingWith("Service")
            .andShould().haveSimpleNameNotEndingWith("Repository")
            .andShould().haveSimpleNameNotEndingWith("Controller")
            .because("Model classes should not have service/repository/controller suffixes")

        rule.check(importedClasses)
    }

    @Test
    @DisplayName("AutoConfiguration 클래스는 'AutoConfiguration'으로 끝나야 함")
    fun autoConfigurationClassesShouldFollowNamingConvention() {
        val rule = classes()
            .that().areAnnotatedWith(org.springframework.boot.autoconfigure.AutoConfiguration::class.java)
            .should().haveSimpleNameEndingWith("AutoConfiguration")
            .because("AutoConfiguration classes should follow naming convention")

        rule.check(importedClasses)
    }

    @Test
    @DisplayName("Entity 클래스는 'Entity'로 끝나야 함")
    fun entityClassesShouldEndWithEntity() {
        val rule = classes()
            .that().resideInAPackage("..repository..")
            .and().areAnnotatedWith(org.springframework.data.relational.core.mapping.Table::class.java)
            .should().haveSimpleNameEndingWith("Entity")
            .because("Entity classes should follow naming convention")

        rule.check(importedClasses)
    }
}
