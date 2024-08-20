/* 
 * Created on 2024-03-31 ( Date ISO 2024-03-31 - Time 19:08:03 )
 * @author Pascal
 */
package org.itech.labSampleTracker.service;

import org.itech.labSampleTracker.entities.CircuitSite;

import java.util.List;

/**
 * <h2>CircuitSiteServiceimpl</h2>
 */
public interface CircuitSiteService {
	CircuitSite create(CircuitSite d);

	CircuitSite update(CircuitSite d);

	CircuitSite getOne(int id);

	List<CircuitSite> getAll();

	long getTotal();

	boolean delete(int id);

	List<CircuitSite> getAllByCircuit(Integer circuitId);

	boolean removeAllByCircuitId(Integer circuitId);

	boolean removeAllBySiteId(Integer siteId);
}
