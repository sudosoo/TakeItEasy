dependencies {
    api(project(":takeItEasy-domain"))

    implementation("org.springframework.boot:spring-boot-starter-data-elasticsearch")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.17.0")
    implementation("com.github.ulisesbocchio:jasypt-spring-boot-starter")

    testImplementation("org.springframework.batch:spring-batch-test")

}