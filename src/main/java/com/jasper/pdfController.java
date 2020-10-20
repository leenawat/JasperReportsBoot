package com.jasper;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletResponse;

import net.sf.jasperreports.engine.export.JRXlsExporter;
import net.sf.jasperreports.engine.export.JRXlsExporterParameter;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimpleXlsExporterConfiguration;
import net.sf.jasperreports.export.SimpleXlsReportConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;

@RestController
public class pdfController {

	@Autowired
    ApplicationContext context;
    	
	@GetMapping(path = "pdf/{jrxml}")
	@ResponseBody
    public void getPdf(@PathVariable String jrxml ,HttpServletResponse response) throws Exception {
		//Get JRXML template from resources folder
		Resource resource = context.getResource("classpath:jasperreports/"+jrxml+".jrxml");
        //Compile to jasperReport
        InputStream inputStream = resource.getInputStream();
        JasperReport report=JasperCompileManager.compileReport(inputStream);		
		//Parameters Set
        Map<String, Object> params = new HashMap<>();
        
        //Data source Set
        JRDataSource dataSource = new JREmptyDataSource();
        //Make jasperPrint
        JasperPrint jasperPrint = JasperFillManager.fillReport(report, params, dataSource);
        //Media Type
        response.setContentType(MediaType.APPLICATION_PDF_VALUE);
        //Export PDF Stream
        JasperExportManager.exportReportToPdfStream(jasperPrint, response.getOutputStream());
    }
    @GetMapping(path = "xls/{jrxml}")
    @ResponseBody
    public void getXml(@PathVariable String jrxml ,HttpServletResponse response) throws Exception {
        //Get JRXML template from resources folder
        Resource resource = context.getResource("classpath:jasperreports/"+jrxml+".jrxml");
        //Compile to jasperReport
        InputStream inputStream = resource.getInputStream();
        JasperReport report=JasperCompileManager.compileReport(inputStream);
        //Parameters Set
        Map<String, Object> params = new HashMap<>();

        //Data source Set
        JRDataSource dataSource = new JREmptyDataSource();
        //Make jasperPrint
        JasperPrint jasperPrint = JasperFillManager.fillReport(report, params, dataSource);

        //Media Type
//        response.setContentType(MediaType.APPLICATION_XML_VALUE);
        response.setContentType("application/vnd.ms-excel");
        response.setHeader("Content-disposition", "attachment; filename=" + "test.xls");

        //Export PDF Stream
        Map<String, Object> parametro = new HashMap<String, Object>();
//        parametro.put("USUARIO", UConstante.NAME_MINISTERIO_USER);
//        parametro.put("RUTA_LOGO", PuenteFile.getRutaFiles(FacesContext.getCurrentInstance(), PuenteFile.RUTA_IMG_LOGO));
//        parametro.put("PATH_SYSTEM", rutaFileSystemHD);
//        parametro.put("WHERE_DATA", WHERE_REGISTRO);
//        parametro.put("WHERE_PROYECTO_USUARIO", WHERE_PROYECTO_USUARIO);
//        parametro.put("WHERE_ZONA", WHERE_ZONA);
//        parametro.put("NAME_APP", RutaFile.NAME_APP);
//        parametro.put("ID_USUARIO", getUsuario().getId());
//        parametro.put("ID_PROYECTO", beanProyecto.getId());
//        parametro.put("SUBREPORT_DIR", SUBREPORT_DIR);

        System.out.println(">>>>>> PARAMETROS :" + parametro.toString());

        try {
//            JasperPrint jasperPrint = JasperFillManager.fillReport(path, parametro, PgConnector.getConexion());
            JRXlsExporter xlsExporter = new JRXlsExporter();
            xlsExporter.setExporterInput(new SimpleExporterInput(jasperPrint));

//            xlsExporter.setExporterOutput(new SimpleOutputStreamExporterOutput("test.xls"));
            xlsExporter.setExporterOutput(new SimpleOutputStreamExporterOutput(response.getOutputStream()));

            SimpleXlsReportConfiguration xlsReportConfiguration = new SimpleXlsReportConfiguration();
//            SimpleXlsExporterConfiguration xlsExporterConfiguration = new SimpleXlsExporterConfiguration();
            xlsReportConfiguration.setOnePagePerSheet(true);
            xlsReportConfiguration.setRemoveEmptySpaceBetweenRows(false);
            xlsReportConfiguration.setDetectCellType(true);
            xlsReportConfiguration.setWhitePageBackground(false);
            xlsExporter.setConfiguration(xlsReportConfiguration);
            xlsExporter.exportReport();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}