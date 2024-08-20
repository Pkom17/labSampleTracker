/*
 * Java domain class for entity "AppUser" 
 * Created on 2024-03-31 ( Date ISO 2024-03-31 - Time 19:08:03 )
 * @author Pascal
 */
package org.itech.labSampleTracker.dao;
import java.util.List;

import org.itech.labSampleTracker.entities.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

/**
 * <h2>AppUserRepository</h2>
 *
 * createdAt : 2024-03-31 - Time 19:08:03
 * <p>
 * Description: "AppUser" Repository
 */


public interface AppUserRepository  extends JpaRepository<AppUser, Integer> , JpaSpecificationExecutor<AppUser> {
	
	public AppUser findByLogin(String login);

	@Query(value = "select u from AppUser u where u.isLocked = false and u.isActive = true ")
	public List<AppUser> findUsersIdName();

	public List<AppUser> findByIsActive(boolean active);

	public List<AppUser> findByIsLocked(boolean locked);

	public AppUser findByPhoneContact(String contact);
}
