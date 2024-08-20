package org.itech.labSampleTracker.service_impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import org.apache.commons.lang3.ObjectUtils;
import org.itech.labSampleTracker.service.DashboardService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Service
@Transactional
public class DashboardServiceImpl implements DashboardService {

	@PersistenceContext
	private EntityManager em;

//	@Override
//	public Map<String, Object> getEquipementCount(Integer labId, Integer equipementId) {
//		StringBuffer sql = new StringBuffer("");
//		sql.append("SELECT COUNT(*) 'all', COUNT(IF(labEquip.status = 1,labEquip.id,null)) 'fonctional', "
//				+ " COUNT(IF(labEquip.status <> 1 AND labEquip.status <> 4 ,labEquip.id,null)) 'stop', "
//				+ " COUNT(IF(labEquip.status = 3,labEquip.id,null)) 'current' "
//				+ " FROM lab_has_equipement labEquip JOIN lab l ON l.id = labEquip.lab_id JOIN equipement e ON e.id = labEquip.equipement_id WHERE 1");
//
//		if (ObjectUtils.isNotEmpty(labId)) {
//			sql.append(" AND l.id = ").append(labId);
//		}
//		if (ObjectUtils.isNotEmpty(equipementId)) {
//			sql.append(" AND e.id = ").append(equipementId);
//		}
//
//		// System.out.println(sql);
//		Map<String, Object> response = new HashMap<String, Object>();
//		try {
//			Query query = em.createNativeQuery(sql.toString());
//			List<Object[]> results = query.getResultList();
//			int all = 0;
//			int fonctional = 0;
//			int stop = 0;
//			int current = 0;
//			for (Object[] o : results) {
//				all += Integer.parseInt(o[0] + "");
//				fonctional += Integer.parseInt(o[1] + "");
//				stop += Integer.parseInt(o[2] + "");
//				current += Integer.parseInt(o[3] + "");
//			}
//			response.put("all", all);
//			response.put("fonctional", fonctional);
//			response.put("stop", stop);
//			response.put("current", current);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return response;
//	}


}
