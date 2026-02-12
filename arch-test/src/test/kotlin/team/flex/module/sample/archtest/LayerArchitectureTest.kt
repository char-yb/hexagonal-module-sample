/*
 * Copyright 2024 flex Inc. - All Rights Reserved.
 */

package team.flex.module.sample.archtest

import com.tngtech.archunit.core.domain.JavaClasses
import com.tngtech.archunit.core.importer.ClassFileImporter
import com.tngtech.archunit.core.importer.ImportOption
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses
import com.tngtech.archunit.library.Architectures.layeredArchitecture
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

/**
 * 헥사고날 아키텍처의 레이어 간 의존성 규칙을 검증하는 테스트
 */
class LayerArchitectureTest {

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
    @DisplayName("Model 레이어는 다른 레이어에 의존하지 않아야 함")
    fun modelLayerShouldNotDependOnOtherLayers() {
        val rule = noClasses()
            .that().resideInAPackage("..model..")
            .and().haveSimpleNameNotEndingWith("Model")
            .and().haveSimpleNameNotEndingWith("Identity")
            .should().dependOnClassesThat()
            .resideInAnyPackage(
                "..service..",
                "..infrastructure..",
                "..repository..",
                "..api.."
            )
            .because("Model should be independent of other layers")

        rule.check(importedClasses)
    }

    @Test
    @DisplayName("Service 레이어는 Model과 Infrastructure만 의존해야 함")
    fun serviceLayerShouldOnlyDependOnModelAndInfrastructure() {
        val rule = noClasses()
            .that().resideInAPackage("..service..")
            .should().dependOnClassesThat()
            .resideInAnyPackage(
                "..api..",
                "..repository.."
            )
            .because("Service should only depend on Model and Infrastructure")

        rule.check(importedClasses)
    }

    @Test
    @DisplayName("Infrastructure 레이어는 Model만 의존해야 함")
    fun infrastructureLayerShouldOnlyDependOnModel() {
        val rule = noClasses()
            .that().resideInAPackage("..infrastructure..")
            .should().dependOnClassesThat()
            .resideInAnyPackage(
                "..service..",
                "..api..",
                "..repository.."
            )
            .because("Infrastructure should only depend on Model")

        rule.check(importedClasses)
    }

    @Test
    @DisplayName("Repository 레이어는 Infrastructure와 Model만 의존해야 함")
    fun repositoryLayerShouldOnlyDependOnInfrastructureAndModel() {
        val rule = noClasses()
            .that().resideInAPackage("..repository..")
            .should().dependOnClassesThat()
            .resideInAnyPackage(
                "..service..",
                "..api.."
            )
            .because("Repository should only depend on Infrastructure and Model")

        rule.check(importedClasses)
    }

    @Test
    @DisplayName("API 레이어는 Service만 의존해야 함 (Infrastructure나 Repository 직접 의존 금지)")
    fun apiLayerShouldOnlyDependOnService() {
        val rule = noClasses()
            .that().resideInAPackage("..api..")
            .should().dependOnClassesThat()
            .resideInAnyPackage(
                "..infrastructure..",
                "..repository.."
            )
            .because("API should only depend on Service, not Infrastructure or Repository")

        rule.check(importedClasses)
    }

    @Test
    @DisplayName("전체 레이어 아키텍처 규칙 검증")
    fun layeredArchitectureShouldBeRespected() {
        val rule = layeredArchitecture()
            .consideringAllDependencies()
            .layer("Common").definedBy("team.flex.module.sample.common..")
            .layer("Model").definedBy("..model..")
            .layer("Exception").definedBy("..exception..")
            .layer("Infrastructure").definedBy("..infrastructure..")
            .layer("Service").definedBy("..service..")
            .layer("Repository").definedBy("..repository..")
            .layer("Api").definedBy("..api..")
            
            .whereLayer("Model").mayOnlyBeAccessedByLayers("Service", "Infrastructure", "Repository", "Api")
            .whereLayer("Exception").mayOnlyBeAccessedByLayers("Service", "Api")
            .whereLayer("Infrastructure").mayOnlyBeAccessedByLayers("Service", "Repository")
            .whereLayer("Service").mayOnlyBeAccessedByLayers("Api")
            .whereLayer("Repository").mayNotBeAccessedByAnyLayer()
            .whereLayer("Common").mayOnlyBeAccessedByLayers("Model", "Exception", "Service", "Api", "Repository")

        rule.check(importedClasses)
    }

    @Test
    @DisplayName("Model은 Spring Framework에 의존하지 않아야 함")
    fun modelShouldNotDependOnSpringFramework() {
        val rule = noClasses()
            .that().resideInAPackage("..model..")
            .should().dependOnClassesThat()
            .resideInAnyPackage(
                "org.springframework..",
                "org.springframework.boot.."
            )
            .because("Model should be framework-independent")

        rule.check(importedClasses)
    }

    @Test
    @DisplayName("Exception은 Spring Framework에 의존하지 않아야 함")
    fun exceptionShouldNotDependOnSpringFramework() {
        val rule = noClasses()
            .that().resideInAPackage("..exception..")
            .should().dependOnClassesThat()
            .resideInAnyPackage(
                "org.springframework..",
                "org.springframework.boot.."
            )
            .because("Exception should be framework-independent")

        rule.check(importedClasses)
    }
}
