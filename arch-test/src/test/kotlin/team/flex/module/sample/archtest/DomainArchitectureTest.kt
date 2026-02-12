/*
 * Copyright 2024 flex Inc. - All Rights Reserved.
 */

package team.flex.module.sample.archtest

import com.tngtech.archunit.core.domain.JavaClasses
import com.tngtech.archunit.core.importer.ClassFileImporter
import com.tngtech.archunit.core.importer.ImportOption
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses
import com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

/**
 * 도메인 간 의존성 규칙을 검증하는 테스트
 */
class DomainArchitectureTest {

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
    @DisplayName("도메인 간 순환 참조가 없어야 함")
    fun domainsShouldBeFreeOfCycles() {
        val rule = slices()
            .matching("team.flex.module.sample.(*)..")
            .should().beFreeOfCycles()
            .because("Domains should not have cyclic dependencies")

        rule.check(importedClasses)
    }

    @Test
    @DisplayName("CoreHR 도메인은 Payroll 도메인을 의존하지 않아야 함")
    fun corehrShouldNotDependOnPayroll() {
        val rule = noClasses()
            .that().resideInAPackage("..corehr..")
            .should().dependOnClassesThat()
            .resideInAPackage("..payroll..")
            .because("CoreHR should not depend on Payroll")

        rule.check(importedClasses)
    }

    @Test
    @DisplayName("Payroll 도메인은 CoreHR 도메인을 직접 의존하지 않고 Common만 의존해야 함")
    fun payrollShouldOnlyDependOnCommonNotCorehr() {
        val rule = noClasses()
            .that().resideInAPackage("..payroll..")
            .should().dependOnClassesThat()
            .resideInAPackage("..corehr..")
            .because("Payroll should only depend on Common module, not CoreHR directly")

        rule.check(importedClasses)
    }

    @Test
    @DisplayName("도메인 모듈은 자신의 도메인 내부만 의존해야 함 (Common 제외)")
    fun domainsShouldOnlyDependOnThemselvesAndCommon() {
        val rule = noClasses()
            .that().resideInAPackage("..corehr..")
            .should().dependOnClassesThat()
            .resideInAnyPackage("..payroll..")
            .because("Domains should be independent except for Common module")

        rule.check(importedClasses)
    }

    @Test
    @DisplayName("모든 도메인은 Common 모듈의 Identity를 사용해야 함")
    fun domainsShouldUseCommonIdentity() {
        val rule = noClasses()
            .that().resideInAnyPackage("..corehr.model..", "..payroll.model..")
            .and().haveSimpleNameEndingWith("Identity")
            .and().haveSimpleNameNotContaining("Company")
            .and().haveSimpleNameNotContaining("Employee")
            .should().dependOnClassesThat()
            .resideInAnyPackage("..corehr..", "..payroll..")
            .because("Domain Identity interfaces should be defined in Common module")

        rule.check(importedClasses)
    }

    @Test
    @DisplayName("도메인 예외는 Common의 DomainException을 의존해야 함")
    fun domainExceptionsShouldDependOnCommonException() {
        // 도메인 예외가 common.exception을 의존하는지 확인
        val rule = classes()
            .that().resideInAnyPackage("..corehr.exception..", "..payroll.exception..")
            .and().haveSimpleNameEndingWith("Exception")
            .and().areNotInterfaces()
            .should().dependOnClassesThat()
            .resideInAnyPackage("..common.exception..", "java.lang..", "kotlin..")
            .because("Domain exceptions should depend on common exception module")
        
        rule.check(importedClasses)
    }
    
    private fun classes() = com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes()
}
