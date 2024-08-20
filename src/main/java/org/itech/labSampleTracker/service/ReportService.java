package org.itech.labSampleTracker.service;

import java.util.Date;

public interface ReportService {
	public byte[] getReportItems(Integer regionId, Integer districtId, Integer site, Integer lab, Integer rider,
			Date startDate, Date endDate, String reportName);
}
