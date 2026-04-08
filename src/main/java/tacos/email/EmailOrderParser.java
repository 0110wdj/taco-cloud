package tacos.email;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import tacos.TacoOrder;

@Component
public class EmailOrderParser {

    private static final Logger log = LoggerFactory.getLogger(EmailOrderParser.class);

    public TacoOrder parse(String emailContent) {
        TacoOrder order = new TacoOrder();
        Map<String, String> values = parseKeyValues(emailContent);

        if (values.containsKey("deliveryName")) {
            order.setDeliveryName(values.get("deliveryName"));
        }
        if (values.containsKey("deliveryStreet")) {
            order.setDeliveryStreet(values.get("deliveryStreet"));
        }
        if (values.containsKey("deliveryCity")) {
            order.setDeliveryCity(values.get("deliveryCity"));
        }
        if (values.containsKey("deliveryState")) {
            order.setDeliveryState(values.get("deliveryState"));
        }
        if (values.containsKey("deliveryZip")) {
            order.setDeliveryZip(values.get("deliveryZip"));
        }
        if (values.containsKey("ccNumber")) {
            order.setCcNumber(values.get("ccNumber"));
        }
        if (values.containsKey("ccExpiration")) {
            order.setCcExpiration(values.get("ccExpiration"));
        }
        if (values.containsKey("ccCVV")) {
            order.setCcCVV(values.get("ccCVV"));
        }

        log.info("Parsed order from email: deliveryName={}", order.getDeliveryName());
        return order;
    }

    private Map<String, String> parseKeyValues(String content) {
        Map<String, String> result = new HashMap<>();

        if (content == null) {
            return result;
        }

        // First try line-based parsing (one key=value per line)
        String[] lines = content.split("\\r?\\n");
        for (String line : lines) {
            line = line.trim();
            if (line.isEmpty() || line.startsWith("#")) {
                continue;
            }
            int eqIndex = line.indexOf('=');
            if (eqIndex >= 0) {
                String key = line.substring(0, eqIndex).trim();
                String value = line.substring(eqIndex + 1).trim();
                result.put(key, value);
                log.debug("Parsed line: {} = {}", key, value);
            }
        }

        // Always do a global search for any "key=" pattern anywhere in content
        // This handles cases where key=value is inside HTML or doesn't follow line boundaries
        String lowerContent = content.toLowerCase();
        // Try both lowercase and with spaces around equals
        String[] searchPatterns = {"deliveryname=", "deliveryname =", "deliveryname=", "deliveryname =", "deliverystreet=", "deliverycity=", "deliverystate=", "deliveryzip=", "ccnumber=", "ccexpiration=", "cccvv=", "username=", "deliveryName=", "deliveryName =", "deliveryStreet=", "deliveryCity=", "deliveryState=", "deliveryZip=", "ccNumber=", "ccNumber =", "ccExpiration=", "ccCVV="};
        String[] originalKeys = {"deliveryName", "deliveryName", "deliveryName", "deliveryName", "deliveryStreet", "deliveryCity", "deliveryState", "deliveryZip", "ccNumber", "ccExpiration", "ccCVV", "username", "deliveryName", "deliveryName", "deliveryStreet", "deliveryCity", "deliveryState", "deliveryZip", "ccNumber", "ccNumber", "ccExpiration", "ccCVV"};

        for (int i = 0; i < searchPatterns.length; i++) {
            String searchPattern = searchPatterns[i].toLowerCase();
            int idx = lowerContent.indexOf(searchPattern);
            if (idx >= 0) {
                // Extract the actual key length before equals
                int eqPos = searchPattern.indexOf('=');
                int keyLength = eqPos;
                if (!result.containsKey(originalKeys[i])) {
                    // Find the next whitespace or newline or HTML tag or end after this
                    int endIdx = content.length();
                    int newlineIdx = content.indexOf('\n', idx + searchPattern.length());
                    int spaceIdx = content.indexOf(' ', idx + searchPattern.length());
                    int tabIdx = content.indexOf('\t', idx + searchPattern.length());
                    int tagIdx = content.indexOf('<', idx + searchPattern.length());
                    if (newlineIdx > idx + searchPattern.length() && newlineIdx < endIdx) endIdx = newlineIdx;
                    if (spaceIdx > idx + searchPattern.length() && spaceIdx < endIdx) endIdx = Math.min(endIdx, spaceIdx);
                    if (tabIdx > idx + searchPattern.length() && tabIdx < endIdx) endIdx = Math.min(endIdx, tabIdx);
                    if (tagIdx > idx + searchPattern.length() && tagIdx < endIdx) endIdx = Math.min(endIdx, tagIdx);

                    String originalKey = originalKeys[i];
                    String value = content.substring(idx + searchPattern.length(), endIdx).trim();
                    // Strip any HTML tags that might be around the value
                    value = value.replaceAll("<[^>]*>", "").trim();
                    // Remove any quote characters
                    value = value.replace("\"", "").replace("'", "").replace("=", "").trim();
                    if (!value.isEmpty()) {
                        result.put(originalKey, value);
                        log.info("Global parsed: {} = {}", originalKey, value);
                    }
                }
            }
        }

        log.info("Found {} parsed keys: {}", result.size(), result.keySet());
        return result;
    }
}
