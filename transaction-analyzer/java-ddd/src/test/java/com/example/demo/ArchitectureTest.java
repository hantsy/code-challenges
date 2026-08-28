package com.example.demo;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

@AnalyzeClasses(packages = "com.example.demo", importOptions = ImportOption.DoNotIncludeTests.class)
public class ArchitectureTest {

    @ArchTest
    static final ArchRule domain_must_not_depend_on_other_layers = noClasses()
            .that().resideInAPackage("..domain..")
            .should().dependOnClassesThat()
            .resideInAnyPackage("..application..", "..infrastructure..", "..interfaces..");

    @ArchTest
    static final ArchRule application_must_not_depend_on_infrastructure_or_interfaces = noClasses()
            .that().resideInAPackage("..application..")
            .should().dependOnClassesThat().resideInAnyPackage("..infrastructure..", "..interfaces..");

    @ArchTest
    static final ArchRule interfaces_must_not_depend_on_domain_or_infrastructure = noClasses()
            .that().resideInAPackage("..interfaces..")
            .should().dependOnClassesThat().resideInAnyPackage("..domain..", "..infrastructure..");

    @ArchTest
    static final ArchRule infrastructure_must_not_depend_on_interfaces_or_service_implementations = noClasses()
            .that().resideInAPackage("..infrastructure..")
            .should().dependOnClassesThat().resideInAnyPackage("..interfaces..", "..application.internal..");

    @ArchTest
    static final ArchRule application_services_reside_in_the_application_layer = classes()
            .that().haveSimpleNameEndingWith("Service")
            .and().areInterfaces()
            .should().resideInAPackage("..application");

    @ArchTest
    static final ArchRule application_service_implementations_are_internal = classes()
            .that().haveSimpleNameEndingWith("Service")
            .and().areNotInterfaces()
            .should().resideInAPackage("..application.internal");

    @ArchTest
    static final ArchRule domain_services_are_interfaces = classes()
            .that().resideInAPackage("..domain.service..")
            .should().beInterfaces();

    @ArchTest
    static final ArchRule repository_interfaces_reside_in_the_domain_layer = classes()
            .that().haveSimpleNameEndingWith("Repository")
            .and().areInterfaces()
            .should().resideInAPackage("..domain.repository..");
}
