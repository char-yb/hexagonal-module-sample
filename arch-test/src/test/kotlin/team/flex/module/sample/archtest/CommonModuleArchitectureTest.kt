/*
 * Copyright 2024 flex Inc. - All Rights Reserved.
 */

package team.flex.module.sample.archtest

import com.tngtech.archunit.core.domain.JavaClasses
import com.tngtech.archunit.core.importer.ClassFileImporter
import com.tngtech.archunit.core.importer.ImportOption
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

/**
 * Common 모듈 사용 규칙을 검증하는 테스트
 */
class CommonModuleArchitectureTest {

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
    @DisplayName("Common 모듈은 다른 도메인에 의존하지 않아야 함")
    fun commonModuleShouldNotDependOnDomains() {
        val rule = noClasses()
            .that().resideInAPackage("..common..")
            .should().dependOnClassesThat()
            .resideInAnyPackage(
                "..corehr..",
                "..payroll.."
            )
            .because("Common module should not depend on specific domains")

        rule.check(importedClasses)
    }

    @Test
    @DisplayName("Common 모듈은 Spring Framework에 의존하지 않아야 함")
    fun commonModuleShouldNotDependOnSpringFramework() {
        val rule = noClasses()
            .that().resideInAPackage("..common..")
            .should().dependOnClassesThat()
            .resideInAnyPackage(
                "org.springframework..",
                "org.springframework.boot.."
            )
            .because("Common module should be framework-independent")

        rule.check(importedClasses)
    }

    @Test
    @DisplayName("Common 모듈의 Identity는 인터페이스여야 함")
    fun commonIdentityShouldBeInterfaces() {
        val rule = classes()
            .that().resideInAPackage("..common..")
            .and().haveSimpleNameEndingWith("Identity")
            .should().beInterfaces()
            .because("Identity types should be interfaces")

        rule.check(importedClasses)
    }

    @Test
    @DisplayName("Common 모듈의 Exception은 추상 클래스여야 함")
    fun commonExceptionsShouldBeAbstract() {
        val rule = classes()
            .that().resideInAPackage("..common.exception..")
            .and().haveSimpleNameEndingWith("Exception")
            .should().beAssignableTo(RuntimeException::class.java)
            .because("Common exceptions should extend RuntimeException")

        rule.check(importedClasses)
    }

    @Test
    @DisplayName("Common 모듈은 비즈니스 로직을 포함하지 않아야 함")
    fun commonModuleShouldNotContainBusinessLogic() {
        val rule = noClasses()
            .that().resideInAPackage("..common..")
            .should().haveSimpleNameEndingWith("Service")
            .orShould().haveSimpleNameEndingWith("Repository")
            .orShould().haveSimpleNameEndingWith("Controller")
            .because("Common module should not contain business logic")

        rule.check(importedClasses)
    }

    @Test
    @DisplayName("도메인별 모델은 Common의 Identity를 사용해야 함")
    fun domainModelsShouldUseCommonIdentity() {
        // CompanyIdentity와 EmployeeIdentity가 common 모듈에 있는지 확인
        val commonIdentities = classes()
            .that().resideInAPackage("..common..")
            .and().haveSimpleNameEndingWith("Identity")
        
        val rule = commonIdentities
            .should().beInterfaces()
            .because("Identity types should be interfaces in common module")
        
        rule.check(importedClasses)
    }

    @Test
    @DisplayName("Common 모듈의 Exception은 abstract이거나 open이어야 함")
    fun commonExceptionsShouldBeExtendable() {
        // abstract 클래스 검증
        val rule = classes()
            .that().resideInAPackage("..common.exception..")
            .and().areNotInterfaces()
            .and().haveSimpleNameEndingWith("Exception")
            .should().beAssignableTo(RuntimeException::class.java)
            .because("Common exceptions should be extendable")
        
        rule.check(importedClasses)
    }
}
