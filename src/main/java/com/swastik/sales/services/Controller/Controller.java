package com.swastik.sales.services.Controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.*;
import java.util.*;

@org.springframework.stereotype.Controller
@CrossOrigin
public class Controller implements ErrorController {

    @Value("${swastik.fromEmail}")
    private String fromEmail;

    @Value("${swastik.pass}")
    private String pass;

    @Value("${swastik.toEmail}")
    private String toEmail;

    @Value("${document.templates.filetypes:}")
    private String[] array;

    @RequestMapping(value = "/sendEmail", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    public ResponseEntity<String> sendEmail(@RequestParam String mobile, String location) throws JsonProcessingException {

        if (mobile == null || location == null){
            return new ResponseEntity<>(ExceptionHandling("104", "Fields cannot be empty"), HttpStatus.BAD_REQUEST);
        }

        if (mobile.length() != 10){
            return new ResponseEntity<>(ExceptionHandling("103", "Length of contact number is invalid"), HttpStatus.BAD_REQUEST);
        }

        Map<String, Object> response = new HashMap<>();
        ObjectMapper mapper = new ObjectMapper();

        Properties prop= new Properties();
        prop.put("mail.smtp.ssl.trust", "smtp.gmail.com");
        prop.put("mail.smtp.auth","true");
        prop.put("mail.smtp.starttls.enable","true");
        prop.put("mail.smtp.host","smtp.gmail.com");
        prop.put("mail.smtp.port","587");
        prop.put("mail.debug", "true");
        prop.put("mail.smtp.socketFactory.port", "465");
        prop.put("mail.smtp.socketFactory.class","javax.net.ssl.SSLSocketFactory");
        prop.put("mail.smtp.socketFactory.fallback", false);

        Session session = Session.getInstance(prop, new javax.mail.Authenticator()
            {
                protected PasswordAuthentication getPasswordAuthentication()
                {
                return new PasswordAuthentication(fromEmail,pass);
                }
            }
        );
        try
        {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(fromEmail));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            message.setHeader("name", "Swastik Sales & Services");
            message.setSubject("A new request for the service @ "+new Date());
            message.setContent("<html><head></head><body>Hello,<br/><br/>A new service request has came from below contact:<br/><br/>MobileNumber: <b>" + mobile + "</b><br />Location: <b>"+location+"</b><br /><br /> Thank you!<br/><br/>Regards,<br/>Swastik Sales & Services<br/>Contact: +91-7666666929<br/></body></html>", "text/html");

            Transport.send(message);
            response.put("status", "Success");
            response.put("message", "Your request has successfully submitted. You will receive our best service soon. Thank you");
            return new ResponseEntity<>(mapper.writeValueAsString(response), HttpStatus.OK);
        }
        catch(Exception e)
        {
            return new ResponseEntity<>(ExceptionHandling("101", e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(value = "/getDropDown", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    public ResponseEntity<String> getDropDown() throws JsonProcessingException {

        try
        {
            //ClassLoader classLoader = new Controller().getClass().getClassLoader();
            //File file = new File(classLoader.getResource("dropdown.txt").getFile());
//            Resource resource = new ClassPathResource("dropdown.txt");
//            InputStream input = resource.getInputStream();
//            File file = resource.getFile();

            Resource resource = new ClassPathResource("dropdown.txt");
            BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream()));

            //BufferedReader reader = new BufferedReader(new FileReader(file.getPath()));
            String line;
            List<String> list = new ArrayList<>();
            Map<String, Object> result = new HashMap<>();
            while((line = reader.readLine()) != null) {
                list.add(line);
            }
            result.put("location", list);
            ObjectMapper mapper = new ObjectMapper();
            return new ResponseEntity<>(mapper.writeValueAsString(result), HttpStatus.OK);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return new ResponseEntity<>(ExceptionHandling("102", e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    private String ExceptionHandling(String code, String message) throws JsonProcessingException {
        Map<String, Object> response = new HashMap<>();
        ObjectMapper mapper = new ObjectMapper();
        response.put("code", code);
        response.put("message", message);
        return mapper.writeValueAsString(response);
    }

    @RequestMapping({"/error"})
    public String error(){
        return "forward:/error.html";
    }

    @RequestMapping({"/home"})
    public String home() {
        return "forward:/index.html";
    }

    @Override
    public String getErrorPath() {
        return "/error";
    }
}
