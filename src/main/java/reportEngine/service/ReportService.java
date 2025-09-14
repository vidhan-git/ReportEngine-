package reportEngine.service;

import net.sf.jasperreports.engine.JasperReport;

public interface ReportService {

	public JasperReport getReportOnEmployee();	
	
	public byte[] getByteDataForExportForPdfAndExcel(JasperReport jasperReport, String exportType);
}
