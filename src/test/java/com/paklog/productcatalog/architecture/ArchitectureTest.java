package com.paklog.productcatalog.architecture;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.library.Architectures.LayeredArchitecture;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.*;
import static com.tngtech.archunit.library.Architectures.layeredArchitecture;

@DisplayName("Architecture Tests")
class ArchitectureTest {
    
    private static JavaClasses classes;
    
    @BeforeAll
    static void setUp() {
        classes = new ClassFileImporter()
                .importPackages("com.paklog.productcatalog");
    }
    
    @Nested
    @DisplayName("Hexagonal Architecture Rules")
    class HexagonalArchitectureRules {
        
        /*@Test
        @DisplayName("Should enforce hexagonal architecture layers")
        void shouldEnforceHexagonalArchitectureLayers() {
            ArchRule rule = layeredArchitecture()
                    .layer("Domain").definedBy("..domain..")
                    .layer("Application").definedBy("..application..")
                    .layer("Infrastructure").definedBy("..infrastructure..")
                    
                    .whereLayer("Domain").mayOnlyBeAccessedByLayers("Application", "Infrastructure")
                    .whereLayer("Application").mayOnlyBeAccessedByLayers("Infrastructure")
                    .whereLayer("Infrastructure").mayNotBeAccessedByAnyLayer();
            
            rule.check(classes);
        }*/
        
        @Test
        @DisplayName("Domain should not depend on infrastructure")
        void domainShouldNotDependOnInfrastructure() {
            ArchRule rule = noClasses()
                    .that().resideInAPackage("..domain..")
                    .should().dependOnClassesThat()
                    .resideInAPackage("..infrastructure..");
            
            rule.check(classes);
        }
        
        @Test
        @DisplayName("Domain should not depend on application")
        void domainShouldNotDependOnApplication() {
            ArchRule rule = noClasses()
                    .that().resideInAPackage("..domain..")
                    .should().dependOnClassesThat()
                    .resideInAPackage("..application..");
            
            rule.check(classes);
        }
    }
    
    @Nested
    @DisplayName("Domain Driven Design Rules")
    class DomainDrivenDesignRules {
        
        @Test
        @DisplayName("Aggregate roots should be in domain model package")
        void aggregateRootsShouldBeInDomainModelPackage() {
            ArchRule rule = classes()
                    .that().haveNameMatching(".*Product")
                    .and().areNotInterfaces()
                    .should().resideInAPackage("..domain.model..");
            
            rule.check(classes);
        }
        
        @Test
        @DisplayName("Repository interfaces should be in domain repository package")
        void repositoryInterfacesShouldBeInDomainRepositoryPackage() {
            ArchRule rule = classes()
                    .that().haveNameMatching(".*Repository")
                    .and().areInterfaces()
                    .and().resideOutsideOfPackages("..infrastructure..")
                    .should().resideInAPackage("..domain.repository..");
            
            rule.check(classes);
        }
        
        @Test
        @DisplayName("Domain events should be in domain event package")
        void domainEventsShouldBeInDomainEventPackage() {
            ArchRule rule = classes()
                    .that().haveNameMatching(".*Event")
                    .should().resideInAPackage("..domain.event..");
            
            rule.check(classes);
        }
    }
    
    @Nested
    @DisplayName("Spring Framework Rules")
    class SpringFrameworkRules {
        
        @Test
        @DisplayName("Controllers should be annotated with @RestController")
        void controllersShouldBeAnnotatedWithRestController() {
            ArchRule rule = classes()
                    .that().haveNameMatching(".*Controller")
                    .should().beAnnotatedWith("org.springframework.web.bind.annotation.RestController");
            
            rule.check(classes);
        }
        
        @Test
        @DisplayName("Services should be annotated with @Service")
        void servicesShouldBeAnnotatedWithService() {
            ArchRule rule = classes()
                    .that().haveNameMatching(".*Service")
                    .and().resideInAPackage("..application.service..")
                    .should().beAnnotatedWith("org.springframework.stereotype.Service");
            
            rule.check(classes);
        }
        
        @Test
        @DisplayName("Repository implementations should be annotated with @Repository")
        void repositoryImplementationsShouldBeAnnotatedWithRepository() {
            ArchRule rule = classes()
                    .that().haveNameMatching(".*Repository")
                    .and().areNotInterfaces()
                    .and().resideInAPackage("..infrastructure.persistence.repository..")
                    .should().beAnnotatedWith("org.springframework.stereotype.Repository");
            
            rule.check(classes);
        }
    }
    
    @Nested
    @DisplayName("General Design Rules")
    class GeneralDesignRules {
        
        @Test
        @DisplayName("Classes should not use field injection")
        void classesShouldNotUseFieldInjection() {
            ArchRule rule = noFields()
                    .should().beAnnotatedWith("org.springframework.beans.factory.annotation.Autowired");
            
            rule.check(classes);
        }
        
        @Test
        @DisplayName("Interfaces should not have names ending with Impl")
        void interfacesShouldNotHaveNamesEndingWithImpl() {
            ArchRule rule = noClasses()
                    .that().areInterfaces()
                    .should().haveNameMatching(".*Impl");
            
            rule.check(classes);
        }
        
        @Test
        @DisplayName("Exception classes should end with Exception")
        void exceptionClassesShouldEndWithException() {
            ArchRule rule = classes()
                    .that().areAssignableTo(Exception.class)
                    .and().resideInAPackage("com.paklog.productcatalog..")
                    .should().haveNameMatching(".*Exception");
            
            rule.allowEmptyShould(true).check(classes);
        

}
}
}
