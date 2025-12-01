package org.ark.framework.infrastructure.repositoryframework;

import io.arkx.framework.data.jdbc.Entity;
import org.ark.framework.infrastructure.IUnitOfWork;
import org.ark.framework.infrastructure.ioc.IocManager;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;


/**
 * @class org.ark.framework.infrastructure.repositoryframework.RepositoryFactory
 * @author Darkness
 * @date 2012-9-25 下午7:08:18
 * @version V1.0
 */
public class RepositoryFactory {
	
	// Dictionary to enforce the singleton pattern
	private static Map<String, IRepository<? extends Entity>> repositories = new HashMap<String, IRepository<? extends Entity>>();

	// / <summary>
	// / Gets or creates an instance of the requested interface. Once a
	// / repository is created and initialized, it is cached, and all
	// / future requests for the repository will come from the cache.
	// / </summary>
	// / <typeparam name="TRepository">The interface of the repository
	// / to create.</typeparam>
	// / <typeparam name="TEntity">The type of the EntityBase that the
	// / repository is for.</typeparam>
	// / <param name="unitOfWork">The unit of work that the repository
	// / will be participating in.</param>
	// / <returns>An instance of the interface requested.</returns>
	@SuppressWarnings("unchecked")
	public static <TRepository extends IRepository<? extends Entity>> TRepository getRepository(Class<TRepository> repositoryClass, IUnitOfWork unitOfWork) {
		// Initialize the provider's default value
		TRepository repository = null;
		
		String interfaceShortName = repositoryClass.getSimpleName();

		// See if the provider was already created and is in the cache
		if (!repositories.containsKey(interfaceShortName)) {
			// Not there, so create it

			// Get the type to be created
			String repositoryClassName = IocManager.getBeanClass(firstToLowerCase(interfaceShortName));
			Class<?> repositoryType = null;
			try {
				repositoryType = Class.forName(repositoryClassName);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}

			// See if an IUnitOfWork needs to be injected to the repository's constructor
			// Create the repository, and cast it to the interface specified
			if (unitOfWork != null && repositoryType.isAssignableFrom(RepositoryBase.class)) {
				try {
					repository = (TRepository) repositoryType.getConstructor(unitOfWork.getClass()).newInstance(unitOfWork);
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (SecurityException e) {
					e.printStackTrace();
				} catch (InstantiationException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				} catch (NoSuchMethodException e) {
					e.printStackTrace();
				}
			} else {
				try {
					repository = (TRepository) repositoryType.newInstance();
				} catch (InstantiationException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
			}

			// Add the new provider instance to the cache
			repositories.put(interfaceShortName, repository);
		} else {
			// The provider was in the cache, so retrieve it
			repository = (TRepository) repositories.get(interfaceShortName);
		}
		return repository;
	}

	// / <summary>
	// / Gets or creates an instance of the requested interface. Once a
	// / repository is created and initialized, it is cached, and all
	// / future requests for the repository will come from the cache.
	// / </summary>
	// / <typeparam name="TRepository">The interface of the repository
	// / to create.</typeparam>
	// / <typeparam name="TEntity">The type of the EntityBase that the
	// / repository is for.</typeparam>
	// / <returns>An instance of the interface requested.</returns>
	public static <TRepository extends IRepository<? extends Entity>> TRepository getRepository(Class<TRepository> repositoryClass) {
		return getRepository(repositoryClass, null);
	}
	
	private static String firstToLowerCase(String str) {
		return str.substring(0, 1).toLowerCase() + str.substring(1);
	}
	
	public static void main(String[] args) {
		System.out.println(firstToLowerCase("PersonRepository"));
	}
}
