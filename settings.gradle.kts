/*
 * Copyright 2024 flex Inc. - All Rights Reserved.
 */

rootProject.name = "flex-module-sample"

include(":corehr:api")
include(":corehr:application-api")
include(":corehr:exception")
include(":corehr:infrastructure")
include(":corehr:model")
include(":corehr:repository-jdbc")
include(":corehr:schema")
include(":corehr:service")

include(":payroll:api")
include(":payroll:application-api")
include(":payroll:exception")
include(":payroll:infrastructure")
include(":payroll:model")
include(":payroll:repository-jdbc")
include(":payroll:schema")
include(":payroll:service")


include(":enterprise:api")
include(":enterprise:application-api")
include(":enterprise:exception")
include(":enterprise:infrastructure")
include(":enterprise:model")
include(":enterprise:repository-jdbc")
include(":enterprise:schema")
include(":enterprise:service")

include(":application-api")
include(":arch-test")
include(":common:exception")
include(":common:model")

pluginManagement {
    buildscript {
        repositories {
            gradlePluginPortal()
        }
    }

    repositories {
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositories {
        mavenCentral()
    }
}
