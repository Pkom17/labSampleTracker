/*
 * Created on 2024-03-31 ( Date ISO 2024-03-31 - Time 19:08:03 )
 * @author Pascal
*/
package org.itech.labSampleTracker.service_impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.apache.commons.lang3.ObjectUtils;
import org.itech.labSampleTracker.dao.AppUserHasCircuitRepository;
import org.itech.labSampleTracker.dao.AppUserHasDistrictRepository;
import org.itech.labSampleTracker.dao.AppUserHasLabRepository;
import org.itech.labSampleTracker.dao.AppUserHasRegionRepository;
import org.itech.labSampleTracker.dao.AppUserHasSiteRepository;
import org.itech.labSampleTracker.dao.AppUserRepository;
import org.itech.labSampleTracker.entities.AppUser;
import org.itech.labSampleTracker.entities.AppUserHasCircuit;
import org.itech.labSampleTracker.entities.AppUserHasDistrict;
import org.itech.labSampleTracker.entities.AppUserHasLab;
import org.itech.labSampleTracker.entities.AppUserHasRegion;
import org.itech.labSampleTracker.entities.AppUserHasSite;
import org.itech.labSampleTracker.exception.OperationFailedException;
import org.itech.labSampleTracker.service.AppUserService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * <h2>AppUserServiceimpl</h2>
 */
@Service
@Transactional
public class AppUserServiceImpl implements AppUserService, ApplicationListener<AuthenticationSuccessEvent> {

	@Autowired
	private AppUserRepository appuserRepo;

	@Autowired
	private AppUserHasRegionRepository appUserHasRegionRepository;

	@Autowired
	private AppUserHasDistrictRepository appUserHasDistrictRepository;

	@Autowired
	private AppUserHasSiteRepository appUserHasSiteRepository;

	@Autowired
	private AppUserHasLabRepository appUserHasLabRepository;

	@Autowired
	private AppUserHasCircuitRepository appUserHasCircuitRepository;

	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;

	@PersistenceContext
	private EntityManager em;

	@Override
	public void onApplicationEvent(AuthenticationSuccessEvent event) {
		String userName = ((UserDetails) event.getAuthentication().getPrincipal()).getUsername();
		updateLastLogin(userName);
	}

	@Override
	public AppUser create(AppUser d, boolean mustCryptPassword) {
		AppUser entity;
		try {
			if (mustCryptPassword)
				d.setPassword(bCryptPasswordEncoder.encode(d.getPassword()));
			entity = appuserRepo.save(d);
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
		return entity;
	}

	@Override
	public AppUser update(AppUser d, boolean mustCryptPassword) {
		AppUser c;
		try {
			if (mustCryptPassword)
				d.setPassword(bCryptPasswordEncoder.encode(d.getPassword()));
			c = appuserRepo.saveAndFlush(d);
		} catch (Exception ex) {
			return null;
		}
		return c;
	}

	@Override
	public AppUser getOne(int id) {
		AppUser t;

		try {
			t = appuserRepo.findById(id).orElse(null);

		} catch (Exception ex) {
			return null;
		}
		return t;
	}

	@Override
	public List<AppUser> getAll() {
		List<AppUser> lst;

		try {
			lst = appuserRepo.findAll();

		} catch (Exception ex) {
			return Collections.emptyList();
		}
		return lst;
	}

	@Override
	public long getTotal() {
		long total;

		try {
			total = appuserRepo.count();
		} catch (Exception ex) {
			return 0;
		}
		return total;
	}

	@Override
	public boolean delete(int id) {
		try {
			appuserRepo.deleteById(id);
			return true;

		} catch (Exception ex) {
			return false;
		}
	}

	@Override
	public AppUser findUserByLogin(String login) {
		AppUser t;
		try {
			t = appuserRepo.findByLogin(login);

		} catch (Exception ex) {
			return null;
		}
		return t;
	}

	@Override
	public Optional<AppUser> findUserById(int id) {
		return appuserRepo.findById(id);
	}

	@Override
	public AppUser findUserByPhoneContact(String contact) {
		return appuserRepo.findByPhoneContact(contact);
	}

	@Override
	public List<AppUser> findUsers() {
		return appuserRepo.findAll();
	}

	@Override
	public List<AppUser> findActiveUser() {
		return appuserRepo.findByIsActive(true);
	}

	@Override
	public void updateLastLogin(String userName) {
		AppUser u = this.findUserByLogin(userName);
		if (ObjectUtils.isNotEmpty(u)) {
			u.setLastLogin(new Date());
			this.create(u, false);
		}
	}

	@Override
	public void removeAllUserSites(Integer userId) {
		try {
			appUserHasSiteRepository.removeByAppUserId(userId);
		} catch (Exception e) {
			throw new OperationFailedException(e.getMessage());
		}

	}

	@Override
	public void addSitesToUser(Integer userId, List<Integer> siteIds) {
		try {
			removeAllUserSites(userId);
			siteIds.forEach(id -> {
				AppUserHasSite appUserHasSite = new AppUserHasSite();
				appUserHasSite.setAppUserId(userId);
				appUserHasSite.setSiteId(id);
				appUserHasSiteRepository.save(appUserHasSite);
			});
		} catch (Exception e) {
			throw new OperationFailedException(e.getMessage());
		}

	}

	@Override
	public void addRegionsToUser(Integer userId, List<Integer> regionIds) {
		try {
			removeAllUserRegions(userId);
			regionIds.forEach(id -> {
				AppUserHasRegion appUserHasRegion = new AppUserHasRegion();
				appUserHasRegion.setAppUserId(userId);
				appUserHasRegion.setRegionId(id);
				appUserHasRegionRepository.save(appUserHasRegion);
			});
		} catch (Exception e) {
			throw new OperationFailedException(e.getMessage());
		}

	}

	@Override
	public void removeAllUserRegions(Integer userId) {
		try {
			appUserHasRegionRepository.removeByAppUserId(userId);
		} catch (Exception e) {
			throw new OperationFailedException(e.getMessage());
		}

	}

	@Override
	public void addLabsToUser(Integer userId, List<Integer> labIds) {
		try {
			removeAllUserLabs(userId);
			labIds.forEach(id -> {
				AppUserHasLab appUserHasLab = new AppUserHasLab();
				appUserHasLab.setAppUserId(userId);
				appUserHasLab.setLabId(id);
				appUserHasLabRepository.save(appUserHasLab);
			});
		} catch (Exception e) {
			throw new OperationFailedException(e.getMessage());
		}

	}

	@Override
	public void removeAllUserLabs(Integer userId) {
		try {
			appUserHasLabRepository.removeByAppUserId(userId);
		} catch (Exception e) {
			throw new OperationFailedException(e.getMessage());
		}

	}

	@Override
	public void addDistrictsToUser(Integer userId, List<Integer> districtIds) {
		try {
			removeAllUserDistricts(userId);
			districtIds.forEach(id -> {
				AppUserHasDistrict appUserHasDistrict = new AppUserHasDistrict();
				appUserHasDistrict.setAppUserId(userId);
				appUserHasDistrict.setDistrictId(id);
				appUserHasDistrictRepository.save(appUserHasDistrict);
			});
		} catch (Exception e) {
			throw new OperationFailedException(e.getMessage());
		}

	}

	@Override
	public void removeAllUserDistricts(Integer userId) {
		try {
			appUserHasDistrictRepository.removeByAppUserId(userId);
		} catch (Exception e) {
			throw new OperationFailedException(e.getMessage());
		}

	}

	@Override
	public void addCircuitsToUser(Integer userId, List<Integer> circuitIds) {
		try {
			removeAllUserCircuits(userId);
			circuitIds.forEach(id -> {
				AppUserHasCircuit appUserHasCircuit = new AppUserHasCircuit();
				appUserHasCircuit.setAppUserId(userId);
				appUserHasCircuit.setCircuitId(id);
				appUserHasCircuitRepository.save(appUserHasCircuit);
			});
		} catch (Exception e) {
			throw new OperationFailedException(e.getMessage());
		}

	}

	@Override
	public void removeAllUserCircuits(Integer userId) {
		try {
			appUserHasCircuitRepository.removeByAppUserId(userId);
		} catch (Exception e) {
			throw new OperationFailedException(e.getMessage());
		}

	}

	@Override
	public List<AppUser> findUsersIdName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<AppUserHasSite> getUserSites(Integer userId) {
		return appUserHasSiteRepository.findByAppUserId(userId);
	}

	@Override
	public List<AppUserHasRegion> getUserRegions(Integer userId) {
		return appUserHasRegionRepository.findByAppUserId(userId);

	}

	@Override
	public List<AppUserHasLab> getUserLabs(Integer userId) {
		return appUserHasLabRepository.findByAppUserId(userId);

	}

	@Override
	public List<AppUserHasDistrict> getUserDistricts(Integer userId) {
		return appUserHasDistrictRepository.findByAppUserId(userId);

	}

	@Override
	public List<AppUserHasCircuit> getUserCircuits(Integer userId) {
		return appUserHasCircuitRepository.findByAppUserId(userId);

	}

	@Override
	public List<Map<String, Object>> getRiderIdAndName() {
		String sql = "SELECT id,last_name,first_name FROM app_user WHERE user_type = 'CONVOYEUR'  ORDER BY last_name,first_name ";
		List<Map<String, Object>> response = new ArrayList<Map<String, Object>>();
		try {
			Query query = em.createNativeQuery(sql);
			List<Object[]> results = query.getResultList();
			for (Object[] o : results) {
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("id", o[0]);
				map.put("name", o[1] + " " + o[2]);
				response.add(map);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return response;
	}

}
