/*
 * Created on 2024-03-31 ( Date ISO 2024-03-31 - Time 19:08:03 )
 * @author Pascal
*/
package org.itech.labSampleTracker.service_impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.itech.labSampleTracker.dao.CircuitSiteRepository;
import org.itech.labSampleTracker.entities.CircuitSite;
import org.itech.labSampleTracker.service.CircuitSiteService;

import java.util.Collections;
import java.util.List;

/**
 * <h2>CircuitSiteServiceimpl</h2>
 */
@Service
@Transactional
public class CircuitSiteServiceImpl implements CircuitSiteService {

	@Autowired
	private CircuitSiteRepository circuitsiteRepo;

	@Override
	public CircuitSite create(CircuitSite d) {

		CircuitSite entity;

		try {
			entity = circuitsiteRepo.save(d);

		} catch (Exception ex) {
			return null;
		}
		return entity;
	}

	@Override
	public CircuitSite update(CircuitSite d) {
		CircuitSite c;

		try {
			c = circuitsiteRepo.saveAndFlush(d);

		} catch (Exception ex) {
			return null;
		}
		return c;
	}

	@Override
	public CircuitSite getOne(int id) {
		CircuitSite t;

		try {
			t = circuitsiteRepo.findById(id).orElse(null);

		} catch (Exception ex) {
			return null;
		}
		return t;
	}

	@Override
	public List<CircuitSite> getAll() {
		List<CircuitSite> lst;

		try {
			lst = circuitsiteRepo.findAll();

		} catch (Exception ex) {
			return Collections.emptyList();
		}
		return lst;
	}

	@Override
	public long getTotal() {
		long total;

		try {
			total = circuitsiteRepo.count();
		} catch (Exception ex) {
			return 0;
		}
		return total;
	}

	@Override
	public boolean delete(int id) {
		try {
			circuitsiteRepo.deleteById(id);
			return true;

		} catch (Exception ex) {
			return false;
		}
	}

	@Override
	public List<CircuitSite> getAllByCircuit(Integer circuitId) {
		List<CircuitSite> lst;

		try {
			lst = circuitsiteRepo.findAllByCircuitId(circuitId);

		} catch (Exception ex) {
			return Collections.emptyList();
		}
		return lst;
	}

	@Override
	public boolean removeAllByCircuitId(Integer circuitId) {
		long total = 0;
		try {
			total = circuitsiteRepo.removeByCircuitId(circuitId);
		} catch (Exception ex) {
			return false;
		}
		return total >= 0;
	}

	@Override
	public boolean removeAllBySiteId(Integer siteId) {
		long total = 0;
		try {
			total = circuitsiteRepo.removeBySiteId(siteId);
		} catch (Exception ex) {
			return false;
		}
		return total > 0;
	}

}
