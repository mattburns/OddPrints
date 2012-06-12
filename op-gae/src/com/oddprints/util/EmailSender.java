/*******************************************************************************
 * Copyright 2011 Matt Burns
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package com.oddprints.util;

import java.io.UnsupportedEncodingException;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public enum EmailSender {

    INSTANCE;

    private final InternetAddress adminEmail;

    private EmailSender() {
        try {
            adminEmail = new InternetAddress("matt@oddprints.com", "OddPrints");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendToAdmin(String htmlMessage, String subject) {
        send(adminEmail.getAddress(), htmlMessage, subject);
    }

    public void send(String to, String htmlMessage, String subject) {
        Properties props = new Properties();
        Session session = Session.getDefaultInstance(props, null);
        htmlMessage += EmailTemplates.footer();

        try {
            Message msg = new MimeMessage(session);
            msg.setFrom(adminEmail);
            msg.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
            msg.addRecipient(Message.RecipientType.BCC, adminEmail);
            msg.setSubject(subject);
            MimeBodyPart textPart = new MimeBodyPart();
            textPart.setText(htmlToPlainText(htmlMessage));
            MimeBodyPart htmlPart = new MimeBodyPart();
            htmlPart.setContent(htmlMessage, "text/html");
            Multipart mp = new MimeMultipart();
            mp.addBodyPart(textPart);
            mp.addBodyPart(htmlPart);
            msg.setContent(mp);

            Transport.send(msg);

        } catch (AddressException e) {
        } catch (MessagingException e) {
        }
    }

    public String htmlToPlainText(String htmlContent) {
        Document doc = Jsoup.parse(htmlContent);
        return doc.text();
    }

}
