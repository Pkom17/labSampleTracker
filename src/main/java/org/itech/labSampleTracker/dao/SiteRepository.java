/*
 * Java domain class for entity "Site" 
 * Created on 2024-03-31 ( Date ISO 2024-03-31 - Time 19:08:04 )
 * @author Pascal
 */
package org.itech.labSampleTracker.dao;

import org.itech.labSampleTracker.entities.Site;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * <h2>SiteRepository</h2>
 *
 * createdAt : 2024-03-31 - Time 19:08:04
 * <p>
 * Description: "Site" Repository
 */

public interface SiteRepository extends JpaRepository<Site, Integer>, JpaSpecificationExecutor<Site> {
	public Site findByDhisCode(String dhisCode);

}
