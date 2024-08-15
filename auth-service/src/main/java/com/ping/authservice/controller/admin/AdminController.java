package com.ping.authservice.controller.admin;

import com.ping.authservice.dto.ReportPostResponse;
import com.ping.authservice.model.User;
import com.ping.authservice.repository.UserRepository;
import com.ping.authservice.service.AdminService;
import com.ping.authservice.util.BasicResponse;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;


import java.io.OutputStream;

@RestController
@RequestMapping("/user/api/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @Autowired
    private UserRepository userRepository;

    private static final Logger logger = LoggerFactory.getLogger(AdminController.class);

    @GetMapping("/getAllUsers")
    public List<User> getAllUsers() {
        return adminService.getAllUsers();
    }

    @GetMapping("/block-user")
    public ResponseEntity<BasicResponse> manageBlockAndUnBlock(@RequestParam int id,
                                                               @RequestParam String reason) {
        return ResponseEntity.ok(adminService.manageBlockAndUnBlock(id,reason));
    }

    @GetMapping("/getAllUsersOnSearch")
    public Page<User> getAllUsers(
            @RequestParam(defaultValue = "") String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "4") int size
    ) {
        return adminService.getAllUsersBasedOnSearch(search, PageRequest.of(page, size));
    }


    @GetMapping("/chartDetails/{period}")
    public Map<String, String> getChartDetails(@PathVariable String period) {
        Map<String, String> chartDetails = adminService.getSalesDataForLastNPeriods(period);
        return chartDetails;
    }

    @GetMapping("/reports")
    public Map<String,Long> getReports(){
        Map<String, Long> reports = adminService.getReports();
        return reports;
    }

    @GetMapping("/getReports")
    public List<ReportPostResponse> getAllReports(){
        return adminService.getAllPost();
    }




//    @GetMapping("/downloadPdf/{period}")
//    public void downloadPdf(@PathVariable String period, HttpServletResponse response) throws IOException {
//        try {
//            // Fetch data from the service
//            Map<String, String> chartDetails = adminService.getSalesDataForLastNPeriods(period);
//            System.out.println(" ==========="+chartDetails);
//            // Generate PDF from HTML template
//            byte[] pdfBytes = generatePdfReport(chartDetails);
//
//            // Check if PDF bytes are empty
//            if (pdfBytes == null || pdfBytes.length == 0) {
//                logger.error("PDF generation returned no content.");
//                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error generating PDF.");
//                return;
//            }
//
//            // Set headers and return PDF file
//            response.setContentType("application/pdf");
//            response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=active_users_report.pdf");
//            response.setContentLength(pdfBytes.length);
//            try (OutputStream os = response.getOutputStream()) {
//                os.write(pdfBytes);
//                os.flush();
//            }
//        } catch (Exception e) {
//            logger.error("Error generating PDF", e);
//            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error generating PDF.");
//        }
//    }


//    private byte[] generatePdfReport(Map<String, String> chartDetails) throws IOException {
//        Context context = new Context();
//        context.setVariable("chartDetails", chartDetails);
//
//        // Process Thymeleaf template to HTML
//        String htmlContent = templateEngine.process("pdfTemplate", context);
//
//        ByteArrayOutputStream os = new ByteArrayOutputStream();
//        PdfWriter pdfWriter = new PdfWriter(os);
//        ConverterProperties properties = new ConverterProperties();
//        HtmlConverter.convertToPdf(htmlContent, pdfWriter, properties);
//        os.close();
//
//        return os.toByteArray();
//    }

    @PostMapping("/send-response")
    public ResponseEntity<BasicResponse> sendReportResponse (@RequestParam Integer reporterId,
                                                             @RequestParam String response,
                                                             @RequestParam String userName) {
        return ResponseEntity.ok(adminService.sendReportResponse(reporterId,response,userName));
    }

}
