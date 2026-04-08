package tacos.email;

import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Multipart;
import jakarta.mail.Part;
import jakarta.mail.internet.MimePart;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import tacos.TacoOrder;
import tacos.User;
import tacos.data.OrderRepository;
import tacos.data.UserRepository;

import java.io.IOException;

@Service
public class EmailOrderService {

    private static final Logger log = LoggerFactory.getLogger(EmailOrderService.class);

    private final EmailOrderParser parser;
    private final OrderRepository orderRepo;
    private final UserRepository userRepo;

    public EmailOrderService(EmailOrderParser parser, OrderRepository orderRepo, UserRepository userRepo) {
        this.parser = parser;
        this.orderRepo = orderRepo;
        this.userRepo = userRepo;
    }

    public void processEmailOrder(String emailContent, Message message) {
        try {
            log.info("Processing new order from email...");
            log.debug("Email content: {}", emailContent);

            TacoOrder order = parser.parse(emailContent);

            // Find user by username if provided in email
            String username = extractUsername(emailContent);
            if (username != null) {
                User user = userRepo.findByUsername(username);
                if (user != null) {
                    order.setUser(user);
                    log.info("Associating order with user: {}", username);
                } else {
                    log.warn("User '{}' not found for email order - order will be saved without user", username);
                }
            }

            TacoOrder savedOrder = orderRepo.save(order);
            log.info("✅ Successfully saved order #{} from email to database", savedOrder.getId());

        } catch (Exception e) {
            log.error("❌ Failed to process order from email", e);
        }
    }

    public void processEmailOrder(Message jakartaMailMessage) {
        try {
            log.info("Processing new order from raw email message...");
            String content = extractTextFromMessage(jakartaMailMessage);
            if (content == null) {
                content = "";
            }
            log.info("Extracted email content length: {} characters", content.length());
            log.info("Extracted email content: {}", content);

            TacoOrder order = parser.parse(content);

            // Find user by username if provided in email
            String username = extractUsername(content);
            if (username != null) {
                User user = userRepo.findByUsername(username);
                if (user != null) {
                    order.setUser(user);
                    log.info("Associating order with user: {}", username);
                } else {
                    log.warn("User '{}' not found for email order - order will be saved without user", username);
                }
            }

            TacoOrder savedOrder = orderRepo.save(order);
            log.info("✅ Successfully saved order #{} from email to database", savedOrder.getId());

        } catch (Exception e) {
            log.error("❌ Failed to process raw email message", e);
        }
    }

    private String extractTextFromMessage(Part part) throws MessagingException, IOException {
        if (part == null) {
            return "";
        }

        Object content = part.getContent();
        if (content == null) {
            return "";
        }

        if (part.isMimeType("text/plain") && content instanceof String) {
            return (String) content;
        } else if (part.isMimeType("text/html") && content instanceof String) {
            // Get HTML content - we still can extract key=value lines
            return (String) content;
        } else if (content instanceof Multipart) {
            Multipart multipart = (Multipart) content;
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < multipart.getCount(); i++) {
                sb.append(extractTextFromMessage(multipart.getBodyPart(i)));
                sb.append("\n");
            }
            return sb.toString();
        } else if (content instanceof MimePart) {
            return extractTextFromMessage((MimePart) content);
        }

        // Fallback: try to convert to string anyway
        return content.toString();
    }

    private String extractUsername(String emailContent) {
        // Check for username in key-value format
        int idx = emailContent.indexOf("username=");
        if (idx >= 0) {
            int endIdx = emailContent.indexOf('\n', idx);
            if (endIdx < 0) {
                endIdx = emailContent.length();
            }
            return emailContent.substring(idx + 9, endIdx).trim();
        }
        return null;
    }
}
