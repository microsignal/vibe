package io.microvibe.booster.core.base.repository.support;

import io.microvibe.booster.core.base.repository.BaseRepository;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactory;
import org.springframework.data.repository.core.RepositoryInformation;
import org.springframework.data.repository.core.RepositoryMetadata;

import javax.persistence.EntityManager;
import java.io.Serializable;

class SimpleBaseRepositoryFactory<M, ID extends Serializable> extends JpaRepositoryFactory {

	private EntityManager entityManager;

	public SimpleBaseRepositoryFactory(EntityManager entityManager) {
		super(entityManager);
		this.entityManager = entityManager;
	}

	@Override
	protected Object getTargetRepository(RepositoryInformation metadata) {
		Class<?> repositoryInterface = metadata.getRepositoryInterface();

		if (isBaseRepository(repositoryInterface)) {
			JpaEntityInformation<M, ID> entityInformation = getEntityInformation((Class<M>) metadata.getDomainType());
			SimpleBaseRepository repository = new SimpleBaseRepository<M, ID>(entityInformation, entityManager);
			return repository;
		}
		return super.getTargetRepository(metadata);
	}

	@Override
	protected Class<?> getRepositoryBaseClass(RepositoryMetadata metadata) {
		if (isBaseRepository(metadata.getRepositoryInterface())) {
			return SimpleBaseRepository.class;
		}
		return super.getRepositoryBaseClass(metadata);
	}

	private boolean isBaseRepository(Class<?> repositoryInterface) {
		return BaseRepository.class.isAssignableFrom(repositoryInterface);
	}
}
