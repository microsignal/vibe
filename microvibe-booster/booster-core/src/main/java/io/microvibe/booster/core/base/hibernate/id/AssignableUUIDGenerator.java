package io.microvibe.booster.core.base.hibernate.id;

import org.hibernate.HibernateException;
import org.hibernate.MappingException;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.id.Configurable;
import org.hibernate.id.UUIDGenerator;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.type.Type;

import java.io.Serializable;
import java.util.Properties;

public class AssignableUUIDGenerator extends UUIDGenerator implements Configurable {

	private String entityName;

	@Override
	public void configure(Type type, Properties params, ServiceRegistry serviceRegistry) throws MappingException {
		entityName = params.getProperty(ENTITY_NAME);
		if (entityName == null) {
			throw new MappingException("no entity name");
		}
		super.configure(type, params, serviceRegistry);
	}

	public Serializable generate(SessionImplementor session, Object object) throws HibernateException {
		final Serializable id = session.getEntityPersister(entityName, object).getIdentifier(object, session);
		if (id == null) {
			return super.generate(session, object);
		}
		return id;
	}

}
