package tacos.email;

import java.util.Properties;

import jakarta.mail.Flags;
import jakarta.mail.Folder;
import jakarta.mail.search.AndTerm;
import jakarta.mail.search.FlagTerm;
import jakarta.mail.search.SearchTerm;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.Pollers;
import org.springframework.integration.mail.ImapMailReceiver;
import org.springframework.integration.mail.SearchTermStrategy;
import org.springframework.integration.mail.dsl.Mail;

@Configuration
public class EmailOrderIntegrationConfig {

    @Bean
    public IntegrationFlow emailOrderFlow(
            @Value("${taco.email.imap.host}") String host,
            @Value("${taco.email.imap.port}") int port,
            @Value("${taco.email.imap.username}") String username,
            @Value("${taco.email.imap.password}") String password,
            @Value("${taco.email.imap.folder:INBOX}") String folder,
            @Value("${taco.email.imap.delete-after-processing:false}") boolean deleteAfterProcessing,
            EmailOrderService emailOrderService) {

        String encodedUsername = java.net.URLEncoder.encode(username, java.nio.charset.StandardCharsets.UTF_8);
        String encodedPassword = java.net.URLEncoder.encode(password, java.nio.charset.StandardCharsets.UTF_8);
        String url = "imaps://" + encodedUsername + ":" + encodedPassword + "@" + host + ":" + port + "/" + folder;

        Properties props = new Properties();
        props.put("mail.imaps.auth", "true");
        props.put("mail.imaps.starttls.enable", "true");
        props.put("mail.imaps.ssl.enable", "true");
        props.put("mail.imaps.connectiontimeout", "10000");
        props.put("mail.imaps.timeout", "15000");
        props.put("mail.debug", "true");
        props.put("mail.imaps.debug", "true");

        ImapMailReceiver imapMailReceiver = new ImapMailReceiver(url);
        imapMailReceiver.setJavaMailProperties(props);
        imapMailReceiver.setShouldDeleteMessages(deleteAfterProcessing);
        // Don't mark as read when processed - so it will be picked up repeatedly
        // This guarantees the test email will be processed even if it was already read
        imapMailReceiver.setShouldMarkMessagesAsRead(false);
        imapMailReceiver.setAutoCloseFolder(false);

        // Custom search strategy: only process UNREAD messages that are NOT deleted
        // This way we don't process all 400+ old emails - only new/unread ones
        SearchTermStrategy searchTermStrategy = new SearchTermStrategy() {
            @Override
            public SearchTerm generateSearchTerm(Flags flags, Folder folder) {
                // Find messages that are:
                // 1. NOT deleted
                // 2. NOT seen (unread) - only process new/unread orders
                AndTerm andTerm = new AndTerm(
                    new FlagTerm(new Flags(Flags.Flag.DELETED), false),
                    new FlagTerm(new Flags(Flags.Flag.SEEN), false)
                );
                return andTerm;
            }
        };
        imapMailReceiver.setSearchTermStrategy(searchTermStrategy);
        // Mark messages as seen after processing - they won't be processed again next poll
        imapMailReceiver.setShouldMarkMessagesAsRead(true);

        imapMailReceiver.setMaxFetchSize(100);
        return IntegrationFlow
                .from(Mail.imapInboundAdapter(imapMailReceiver),
                      p -> p.poller(Pollers.fixedDelay(10000)))
                // Handle the raw Message ourselves to extract text content manually
                .handle((jakarta.mail.Message jakartaMail, org.springframework.messaging.MessageHeaders headers) -> {
                    emailOrderService.processEmailOrder(jakartaMail);
                    return null;
                })
                .get();
    }
}
