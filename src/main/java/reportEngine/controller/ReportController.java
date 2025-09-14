package reportEngine.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import reportEngine.service.ReportService;
import reportEngine.service.impl.ReportServiceImpl;

@RestController
@RequestMapping("/report")
@Slf4j
@Tag(name = "Report", description = "Sample Reporting")
public class ReportController {

    private final ReportService reportService;
    
    Logger log = LoggerFactory.getLogger(ReportController.class);

    
    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }
    
    @GetMapping(value = "/excel", produces = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
    @Operation(summary = "sample reports")
    public ResponseEntity<byte[]>  sampleReportExcel() {

    	byte[] bytes = reportService.getByteDataForExportForPdfAndExcel(null, reportService.getReportOnEmployee(), "excel");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
        headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=employee_report.xlsx");

        return new ResponseEntity<>(bytes, headers, HttpStatus.OK);
    }
    
    @GetMapping(value = "/pdf", produces = "application/pdf")
    @Operation(summary = "sample reports")
    public ResponseEntity<byte[]>  sampleReportPdf() {

    	byte[] bytes = reportService.getByteDataForExportForPdfAndExcel(null, reportService.getReportOnEmployee(), "pdf");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("application/pdf"));
        headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=employee_report.pdf");
    	
        return new ResponseEntity<>(bytes, headers, HttpStatus.OK);  	
    }
    
    @GetMapping(value = "/pdfdynamic", produces = "application/pdf")
    @Operation(summary = "sample dynamic reports")
    public ResponseEntity<byte[]>  sampleReportPdfDynamic() {

    	byte[] bytes = reportService.getByteDataForExportForPdfAndExcel(reportService.getByteDataUsingDynamicReport(), null, "pdf");

    	log.debug("byte size is "+bytes.length);
    	
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("application/pdf"));
        headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=employee_report_dynamic.pdf");
    	
        return new ResponseEntity<>(bytes, headers, HttpStatus.OK);  	
    }

}
