package io.microvibe.booster.core.env;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Getter
public class SpringJpaEnv {

	@Value("${spring.jpa.persistenceUnitName:www.booster.io}")
	String persistenceUnitName;
	@Value("${spring.jpa.database:MYSQL}")
	String database;
	@Value("${spring.jpa.database-platform:org.hibernate.dialect.MySQL5InnoDBDialect}")
	String databasePlatform;
	@Value("${spring.jpa.show-sql:true}")
	boolean showSql;
	@Value("${spring.jpa.generate-ddl:false}")
	boolean generateDdl;
	@Value("${spring.jpa.hibernate.ddl-auto:none}")
	String hibernateDdlAuto;

	@Value("${spring.jpa.hibernate.naming.implicit-strategy:org.springframework.boot.orm.jpa.hibernate.SpringImplicitNamingStrategy}")
	String hibernateNamingImplicitStrategy;
	@Value("${spring.jpa.hibernate.naming.physical-strategy:org.springframework.boot.orm.jpa.hibernate.SpringPhysicalNamingStrategy}")
	String hibernateNamingPhysicalStrategy;
	@Value("${spring.jpa.properties.hibernate.dialect:org.hibernate.dialect.MySQL5InnoDBDialect}")
	String hibernateDialect;
}
