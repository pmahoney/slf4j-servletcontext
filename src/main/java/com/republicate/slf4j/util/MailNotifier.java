package com.republicate.slf4j.util;

import java.io.IOException;
import java.util.LinkedList;
import java.util.Map;
import java.util.Properties;

import com.republicate.mailer.EmailSender;
import com.republicate.mailer.SmtpLoop;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MailNotifier
{
    private String sender;
    private String recipient;
    private boolean running = false;
    private SmtpLoop smtpLoop = null;

    class Notification
    {
        String subject;
        String body;

        Notification(String subject, String body)
        {
            this.subject = subject;
            this.body = body;
        }
    }

    private MailNotifier(String sender, String recipient)
    {
        this.sender = sender;
        this.recipient = recipient;
    }

    public static MailNotifier singleton = null;

    public static MailNotifier getInstance(String sender, String recipient)
    {
        // TODO - could return a different instance for each set of parameters
        if (singleton == null)
        {
            singleton = new MailNotifier(sender, recipient);
        }
        return singleton;
    }

    public void start()
    {
        Map<String, String> env = System.getenv();
        Properties config = new Properties();
        config.put("smtp.host", env.get("SMTP_HOST"));
        config.put("smtp.port", env.get("SMTP_PORT"));
        config.put("smtp.user", env.get("SMTP_USER"));
        config.put("smtp.password", env.get("SMTP_PASSWORD"));
        smtpLoop = new SmtpLoop(config);
        new Thread(smtpLoop, "smtp-loop").start();
    }

    public boolean isRunning()
    {
        return SmtpLoop.isRunning();
    }

    public void stop()
    {
        // NOP - TODO upstream in SmtpLoop
    }

    public void sendNotification(String subject, String body) throws IOException
    {
        EmailSender.send(sender, recipient, subject, body);
    }
}
