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
    static final ArchRule domain_model_must_not_depend_on_any_other_package = noClasses()
            .that().resideInAPackage("..domain.model..")
            .should().dependOnClassesThat()
            .resideInAnyPackage("..domain.service..", "..port..", "..adapter..");

    @ArchTest
    static final ArchRule domain_services_must_not_depend_on_adapters = noClasses()
            .that().resideInAPackage("..domain.service..")
            .should().dependOnClassesThat().resideInAPackage("..adapter..");

    @ArchTest
    static final ArchRule ports_must_not_depend_on_adapters = noClasses()
            .that().resideInAPackage("..port..")
            .should().dependOnClassesThat().resideInAPackage("..adapter..");

    @ArchTest
    static final ArchRule ports_may_only_depend_on_the_domain_model = classes()
            .that().resideInAPackage("..port..")
            .should().onlyDependOnClassesThat()
            .resideInAnyPackage("java..", "..domain.model..", "..port..");

    @ArchTest
    static final ArchRule adapters_must_not_depend_on_domain_services = noClasses()
            .that().resideInAPackage("..adapter..")
            .should().dependOnClassesThat().resideInAPackage("..domain.service..");

    @ArchTest
    static final ArchRule inbound_adapters_do_not_depend_on_outbound_adapters = noClasses()
            .that().resideInAPackage("..adapter.in..")
            .should().dependOnClassesThat().resideInAPackage("..adapter.out..");

    @ArchTest
    static final ArchRule outbound_adapters_do_not_depend_on_inbound_adapters = noClasses()
            .that().resideInAPackage("..adapter.out..")
            .should().dependOnClassesThat().resideInAPackage("..adapter.in..");

    @ArchTest
    static final ArchRule inbound_ports_reside_in_the_port_in_package = classes()
            .that().haveSimpleNameEndingWith("Port")
            .should().resideInAPackage("..port.in..");

    @ArchTest
    static final ArchRule inbound_ports_are_interfaces = classes()
            .that().resideInAPackage("..port.in..")
            .and().haveSimpleNameEndingWith("Port")
            .should().beInterfaces();

    @ArchTest
    static final ArchRule outbound_ports_are_interfaces = classes()
            .that().resideInAPackage("..port.out..")
            .should().beInterfaces();

    @ArchTest
    static final ArchRule services_reside_in_the_domain_layer = classes()
            .that().haveSimpleNameEndingWith("Service")
            .should().resideInAPackage("..domain.service..");

    @ArchTest
    static final ArchRule adapters_are_named_consistently = classes()
            .that().resideInAnyPackage("..adapter..")
            .should().haveSimpleNameEndingWith("Adapter");
}
