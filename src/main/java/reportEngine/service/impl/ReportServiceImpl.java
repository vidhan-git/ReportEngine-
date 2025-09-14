package reportEngine.service.impl;

import java.io.ByteArrayOutputStream;
import java.sql.Connection;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.design.JRDesignBand;
import net.sf.jasperreports.engine.design.JRDesignExpression;
import net.sf.jasperreports.engine.design.JRDesignField;
import net.sf.jasperreports.engine.design.JRDesignQuery;
import net.sf.jasperreports.engine.design.JRDesignSection;
import net.sf.jasperreports.engine.design.JRDesignTextField;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;
import net.sf.jasperreports.engine.type.WhenNoDataTypeEnum;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import reportEngine.service.ReportService;

@Service
public class ReportServiceImpl implements ReportService{

	@Autowired
    private final DataSource dataSource;

    public ReportServiceImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }
    
	@Override
	public JasperReport getReportOnEmployee() {
		
        // 1. Create simple report design
        JasperDesign jasperDesign = new JasperDesign();
        jasperDesign.setName("employee_report");
        jasperDesign.setPageWidth(595);
        jasperDesign.setPageHeight(842);
        jasperDesign.setWhenNoDataType(WhenNoDataTypeEnum.ALL_SECTIONS_NO_DETAIL);

        JRDesignQuery query = new JRDesignQuery();
        query.setText("SELECT EMP_FNAME || ' ' || EMP_LNAME AS FULL_NAME, CTC FROM KRC_EMPLOYEE_INFO");
        jasperDesign.setQuery(query);

        // Define fields
        JRDesignField fieldName = new JRDesignField();
        fieldName.setName("FULL_NAME");
        fieldName.setValueClass(String.class);
        try {
			jasperDesign.addField(fieldName);
		} catch (JRException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        JRDesignField fieldSalary = new JRDesignField();
        fieldSalary.setName("CTC");
        fieldSalary.setValueClass(Double.class);
        try {
			jasperDesign.addField(fieldSalary);
		} catch (JRException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        // Detail section
        JRDesignBand detailBand = new JRDesignBand();
        detailBand.setHeight(20);
        
        // Detail Band
        JRDesignTextField nameField = new JRDesignTextField();
        nameField.setX(0);
        nameField.setY(0);
        nameField.setWidth(200);
        nameField.setHeight(20);
        nameField.setExpression(new JRDesignExpression("$F{FULL_NAME}"));
        detailBand.addElement(nameField);

        JRDesignTextField salaryField = new JRDesignTextField();
        salaryField.setX(200);
        salaryField.setY(0);
        salaryField.setWidth(100);
        salaryField.setHeight(20);
        salaryField.setExpression(new JRDesignExpression("$F{CTC}"));
        detailBand.addElement(salaryField);

        ((JRDesignSection) jasperDesign.getDetailSection()).addBand(detailBand);

        // 2. Compile report
        JasperReport jasperReport = null;
        
        try {
        	 jasperReport = JasperCompileManager.compileReport(jasperDesign);
        }	catch(Exception e) {
        	e.printStackTrace();
        }
		
		return jasperReport;
	}

	public byte[] getByteDataForExportForPdfAndExcel(JasperReport jasperReport, String exportType) {

		byte[] bytes = null;
		
		if	(exportType.equalsIgnoreCase("pdf")) {
	        try (Connection conn = dataSource.getConnection();
	                ByteArrayOutputStream out = new ByteArrayOutputStream()) {

	               	// 4. Fill report with DB data
	               	JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, null, conn);

	               	// 5. Export to PDF
	               	JasperExportManager.exportReportToPdfStream(jasperPrint, out);
	               	bytes = out.toByteArray();
	           }	catch(Exception e) {
	           		e.printStackTrace();
	           }
		}
		else if (exportType.equalsIgnoreCase("excel")) {
	        //JasperReport jasperReport = getJasperReport();
	        JasperPrint jasperPrint = null;
	        try (Connection conn = dataSource.getConnection();
	                ByteArrayOutputStream out = new ByteArrayOutputStream()) {
				jasperPrint = JasperFillManager.fillReport(jasperReport, null, conn);
				
				JRXlsxExporter exporter = new JRXlsxExporter();
		        exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
		        exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(out));
				exporter.exportReport();

		        bytes = out.toByteArray();

			} 	catch (Exception e) {
				e.printStackTrace();
			}
		}
		return bytes;
	}
}
