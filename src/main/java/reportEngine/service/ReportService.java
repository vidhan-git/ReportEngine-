package reportEngine.service;

import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;

public interface ReportService {

	public JasperReport getReportOnEmployee();	
	
	public byte[] getByteDataForExportForPdfAndExcel(JasperPrint jasperPrint, JasperReport jasperReport, String exportType);
	
	public JasperPrint getByteDataUsingDynamicReport();
}
