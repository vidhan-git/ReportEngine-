package reportEngine.service.impl;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ar.com.fdvs.dj.core.DynamicJasperHelper;
import ar.com.fdvs.dj.core.layout.ClassicLayoutManager;
import ar.com.fdvs.dj.domain.DynamicReport;
import ar.com.fdvs.dj.domain.Style;
import ar.com.fdvs.dj.domain.builders.ColumnBuilder;
import ar.com.fdvs.dj.domain.builders.FastReportBuilder;
import ar.com.fdvs.dj.domain.builders.StyleBuilder;
import ar.com.fdvs.dj.domain.constants.Font;
import ar.com.fdvs.dj.domain.constants.HorizontalAlign;
import ar.com.fdvs.dj.domain.constants.Page;
import ar.com.fdvs.dj.domain.constants.Transparency;
import ar.com.fdvs.dj.domain.constants.VerticalAlign;
import ar.com.fdvs.dj.domain.entities.columns.AbstractColumn;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRResultSetDataSource;
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
    Logger log = LoggerFactory.getLogger(ReportServiceImpl.class);

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
		}	catch (JRException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        JRDesignField fieldSalary = new JRDesignField();
        fieldSalary.setName("CTC");
        fieldSalary.setValueClass(Double.class);
        try {
			jasperDesign.addField(fieldSalary);
		}	catch (JRException e) {
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

	public byte[] getByteDataForExportForPdfAndExcel(JasperPrint jasperPrint, JasperReport jasperReport, String exportType) {

		byte[] bytes = null;
		
		if	(exportType.equalsIgnoreCase("pdf")) {
	        try (Connection conn = dataSource.getConnection();
	                ByteArrayOutputStream out = new ByteArrayOutputStream()) {

               	// 4. Fill report with DB data
	        	if	(null == jasperPrint)
	        		jasperPrint = JasperFillManager.fillReport(jasperReport, null, conn);

	               	// 5. Export to PDF
	        	JasperExportManager.exportReportToPdfStream(jasperPrint, out);
	        	bytes = out.toByteArray();
	        }	catch(Exception e) {
	           	e.printStackTrace();
	        }
		}
		else if (exportType.equalsIgnoreCase("excel")) {
	        //JasperReport jasperReport = getJasperReport();

	        try (Connection conn = dataSource.getConnection();
	                ByteArrayOutputStream out = new ByteArrayOutputStream()) {
	        	if	(null == jasperPrint)
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
	
	public JasperPrint getByteDataUsingDynamicReport() {
	    JasperPrint jasperPrint = null;
	    Statement stmt = null;
	    ResultSet rs = null;
	    String sql = "SELECT EMP_ID, EMP_FNAME, EMP_LNAME, COMP_ID, EMP_NO, EMP_EMAIL_OFF FROM KRC_EMPLOYEE_INFO";

	    try (Connection conn = dataSource.getConnection()) {
	        stmt = conn.createStatement();
	        rs = stmt.executeQuery(sql);

	        ResultSetMetaData metaData = rs.getMetaData();
	        int columnCount = metaData.getColumnCount();

	        FastReportBuilder drb = new FastReportBuilder();

	        // Header style with safe font
	        Style headerStyle = new StyleBuilder(false)
	                .setFont(new Font(10, "DejaVu Sans", false, false, false))
	                .setHorizontalAlign(HorizontalAlign.CENTER)
	                .setBackgroundColor(Color.LIGHT_GRAY)
	                .setTransparency(Transparency.OPAQUE)
	                .build();
	        
	        Style detailStyle = new StyleBuilder(false)
	                .setFont(new Font(10, "DejaVu Sans", false))
	                .setHorizontalAlign(HorizontalAlign.LEFT)
	                .setVerticalAlign(VerticalAlign.TOP)
	                .setStretchWithOverflow(true) // ✅ Prevents letter-by-letter split
	               // .setTextWrap(true)            // ✅ Allows wrapping
	                .build();

	        // Add columns dynamically
	        for (int i = 1; i <= columnCount; i++) {
	            String colLabel = metaData.getColumnLabel(i);
	            if (colLabel == null || colLabel.isEmpty()) {
	                colLabel = metaData.getColumnName(i);
	            }
	            //drb.addColumn(colLabel, colLabel, String.class.getName(), 50, headerStyle, null);

	            AbstractColumn column = ColumnBuilder.getNew()
	                    .setColumnProperty(colLabel, String.class.getName())
	                    .setTitle(colLabel)
	                    .setWidth(120)               // wider column
	                    .setHeaderStyle(headerStyle)
	                    .setStyle(detailStyle)       // apply detail style
	                    .build();

	            drb.addColumn(column);
	        }

	        drb.setTitle("Dynamic Report").setUseFullPageWidth(true);
	        drb.setPrintBackgroundOnOddRows(true);
	        drb.setUseFullPageWidth(true);
	        drb.setPageSizeAndOrientation(Page.Page_A4_Landscape()); 
	        DynamicReport dr = drb.build();

	        JRResultSetDataSource jrDataSource = new JRResultSetDataSource(rs);

	        // ✅ This already fills the report — don’t call JasperFillManager again
	        jasperPrint = DynamicJasperHelper.generateJasperPrint(dr, new ClassicLayoutManager(), jrDataSource);

	    } catch (Exception e) {
	        log.error("Error generating report", e);
	    } finally {
	        try { if (rs != null) rs.close(); } catch (Exception ignored) {}
	        try { if (stmt != null) stmt.close(); } catch (Exception ignored) {}
	    }
	    return jasperPrint;
	}
}
