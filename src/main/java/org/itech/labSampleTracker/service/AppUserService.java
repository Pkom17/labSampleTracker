/* 
 * Created on 2024-03-31 ( Date ISO 2024-03-31 - Time 19:08:03 )
 * @author Pascal
 */
package org.itech.labSampleTracker.service;

import org.itech.labSampleTracker.entities.AppUser;
import org.itech.labSampleTracker.entities.AppUserHasCircuit;
import org.itech.labSampleTracker.entities.AppUserHasDistrict;
import org.itech.labSampleTracker.entities.AppUserHasLab;
import org.itech.labSampleTracker.entities.AppUserHasRegion;
import org.itech.labSampleTracker.entities.AppUserHasSite;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * <h2>AppUserServiceimpl</h2>
 */
public interface AppUserService {
	AppUser create(AppUser d, boolean mustCryptPassword);

	AppUser update(AppUser d, boolean mustCryptPassword);

	AppUser getOne(int id);

	AppUser findUserByLogin(String login);

	public void updateLastLogin(String userName);

	public Optional<AppUser> findUserById(int id);

	public List<AppUser> findActiveUser();

	List<AppUser> getAll();

	long getTotal();

	boolean delete(int id);

	public void addSitesToUser(Integer userId, List<Integer> siteIds);

	public void removeAllUserSites(Integer userId);
	
	public List<AppUserHasSite> getUserSites(Integer userId);

	public void addRegionsToUser(Integer userId, List<Integer> regionIds);

	public void removeAllUserRegions(Integer userId);
	
	public List<AppUserHasRegion> getUserRegions(Integer userId);

	public void addLabsToUser(Integer userId, List<Integer> labIds);

	public void removeAllUserLabs(Integer userId);
	
	public List<AppUserHasLab> getUserLabs(Integer userId);

	public void addDistrictsToUser(Integer userId, List<Integer> districtIds);

	public void removeAllUserDistricts(Integer userId);
	
	public List<AppUserHasDistrict> getUserDistricts(Integer userId);

	public void addCircuitsToUser(Integer userId, List<Integer> circuitIds);

	public void removeAllUserCircuits(Integer userId);
	
	public List<AppUserHasCircuit> getUserCircuits(Integer userId);

	public List<AppUser> findUsersIdName();

	public AppUser findUserByPhoneContact(String contact);

	public List<AppUser> findUsers();
	
	List<Map<String, Object>> getRiderIdAndName();
}
