package pt.ipp.isep.dei.application.session.emailService.adapters;

import pt.ipp.isep.dei.application.session.emailService.EmailService;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * The type Gmail service.
 */
public class GmailService implements EmailService {

    @Override
    public void sendEmail(String name, String body) {
        try (PrintWriter out = new PrintWriter(new FileWriter("src/main/java/pt/ipp/isep/dei/esoft/project/application/session/emailService/emails.txt", true))) {
            out.println("To: " + name);
            out.println("Body: " + body);
            out.println();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
