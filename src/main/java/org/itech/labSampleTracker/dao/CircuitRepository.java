/*
 * Java domain class for entity "Circuit" 
 * Created on 2024-03-31 ( Date ISO 2024-03-31 - Time 19:08:03 )
 * @author Pascal
 */
package org.itech.labSampleTracker.dao;

import org.itech.labSampleTracker.entities.Circuit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * <h2>CircuitRepository</h2>
 *
 * createdAt : 2024-03-31 - Time 19:08:03
 * <p>
 * Description: "Circuit" Repository
 */

public interface CircuitRepository extends JpaRepository<Circuit, Integer>, JpaSpecificationExecutor<Circuit> {

	public Circuit findByCircuitNumber(String circuitNumber);
}
