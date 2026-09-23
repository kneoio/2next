package com.semantyca.core.service.mail;

import com.semantyca.core.model.cnst.LanguageCode;
import com.semantyca.core.service.template.TemplateService;
import io.quarkus.mailer.Mail;
import io.quarkus.mailer.reactive.ReactiveMailer;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@ApplicationScoped
public class MailService {

    private static final Logger LOG = Logger.getLogger(MailService.class);
    private static final DateTimeFormatter PLAYING_SOON_TIME_FORMAT =
            DateTimeFormatter.ofPattern("HH:mm zzz", Locale.ENGLISH);

    private final ReactiveMailer mailer;
    private final TemplateService templateService;
    private final MailMessageService mailMessageService;
    private final String fromAddress;

    @Inject
    public MailService(
            ReactiveMailer mailer,
            TemplateService templateService,
            MailMessageService mailMessageService,
            @ConfigProperty(name = "quarkus.mailer.from", defaultValue = "noreply@mixpla.io") String fromAddress
    ) {
        this.mailer = mailer;
        this.templateService = templateService;
        this.mailMessageService = mailMessageService;
        this.fromAddress = fromAddress;
    }

    public Uni<Void> send(String to, String subject, String htmlBody, String textBody) {
        return send(to, subject, htmlBody, textBody, null);
    }

    public Uni<Void> send(String to, String subject, String htmlBody, String textBody, String replyTo) {
        Mail mail = Mail.withHtml(to, subject, htmlBody)
                .setText(textBody)
                .setFrom("Mixpla <" + fromAddress + ">");
        if (replyTo != null && !replyTo.isBlank()) {
            mail.setReplyTo(replyTo);
        }
        return mailer.send(mail)
                .onFailure().invoke(failure -> LOG.errorf(failure, "Failed to send mail to %s subject=%s", to, subject));
    }

    public Uni<Void> sendTemplate(String to, String subject, String template, LanguageCode language, Map<String, Object> data) {
        return sendTemplate(to, subject, template, language, data, null);
    }

    public Uni<Void> sendTemplate(String to, String subject, String template, LanguageCode language, Map<String, Object> data, String replyTo) {
        Locale locale = mailMessageService.toLocale(language);
        Map<String, Object> payload = new HashMap<>(data != null ? data : Map.of());
        payload.put("footer", mailMessageService.get(language, "mail.footer"));
        String html = templateService.render("mail/" + template + ".html", locale, payload);
        String text = templateService.render("mail/" + template + ".txt", locale, payload);
        return send(to, subject, html, text, replyTo);
    }

    public Uni<Void> sendOtp(String email, String code, LanguageCode language, OtpPurpose purpose) {
        LOG.infof("Sending %s OTP email to %s in %s", purpose, email, language);
        String prefix = purpose.getMessageKeyPrefix();
        Map<String, Object> data = new HashMap<>();
        data.put("code", code);
        data.put("title", mailMessageService.get(language, prefix + ".title"));
        data.put("subtitle", mailMessageService.get(language, prefix + ".subtitle"));
        data.put("note", mailMessageService.get(language, prefix + ".note"));
        return sendTemplate(email, mailMessageService.get(language, prefix + ".subject"), "otp", language, data);
    }

    public Uni<Void> sendContributionPlayingSoonAsync(String email, String songTitle, String stationUrl, String brandName, String djName,
                                                      int etaSeconds, ZoneId brandZone, String previousSongTitle) {
        LOG.infof("Sending 'playing soon' email to: %s for song: %s", email, songTitle);
        boolean hasDjName = djName != null && !djName.isBlank();
        boolean hasPreviousSong = previousSongTitle != null && !previousSongTitle.isBlank();
        String listenLabel = brandName != null && !brandName.isBlank()
                ? "Listen " + brandName + " now..."
                : "Listen live now...";
        ZoneId zone = brandZone != null ? brandZone : ZoneId.systemDefault();
        ZonedDateTime now = ZonedDateTime.now(zone);
        ZonedDateTime estimatedPlayTime = now.plusSeconds(Math.max(etaSeconds, 0));

        Map<String, Object> data = new HashMap<>();
        data.put("songTitle", songTitle);
        data.put("hasDjName", hasDjName);
        data.put("djName", hasDjName ? djName : "");
        data.put("hasPreviousSong", hasPreviousSong);
        data.put("previousSongTitle", hasPreviousSong ? previousSongTitle : "");
        data.put("roughDuration", formatRoughDuration(etaSeconds));
        data.put("etaLabel", estimatedPlayTime.format(PLAYING_SOON_TIME_FORMAT));
        data.put("nowLabel", now.format(PLAYING_SOON_TIME_FORMAT));
        data.put("stationUrl", stationUrl);
        data.put("listenLabel", listenLabel);
        return sendTemplate(email, "Your song is playing soon - " + songTitle, "playing-soon", LanguageCode.en, data);
    }

    public Uni<Void> sendActionDebugEmail(String email, String actionName, String instruction, Map<String, Object> variables, String result) {
        LOG.infof("Sending action debug email to: %s for action: %s", email, actionName);
        Map<String, Object> data = new HashMap<>();
        data.put("actionName", actionName);
        data.put("instruction", instruction != null ? instruction : "");
        data.put("variables", variables != null ? variables : Map.of());
        data.put("result", result != null ? result : "");
        return sendTemplate(email, "Action Debug: " + actionName, "action-debug", LanguageCode.en, data);
    }

    public Uni<Void> sendMessageToOwner(String to, String replyTo, String subject, String stationSlug, String message) {
        LOG.infof("Sending message to owner %s station=%s", to, stationSlug);
        Map<String, Object> data = new HashMap<>();
        data.put("stationSlug", stationSlug);
        data.put("subject", subject);
        data.put("message", message);
        return sendTemplate(to, subject, "message-to-owner", LanguageCode.en, data, replyTo);
    }

    private static String formatRoughDuration(int seconds) {
        if (seconds < 60) {
            return Math.max(seconds, 0) + " sec";
        }
        long minutes = Math.round(seconds / 60.0);
        return minutes + " min";
    }
}
