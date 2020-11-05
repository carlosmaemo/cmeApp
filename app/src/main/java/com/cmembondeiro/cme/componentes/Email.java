package com.cmembondeiro.cme.componentes;

import android.util.Log;

import javax.mail.*;
import java.util.Properties;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

public class Email {

    private String emissor, password, receptor;
    private String assunto, mensagem;

    public void EnviarEmail(String emissor_conf, String password_conf, String receptor_conf, String assunto_config, String mensagem_config) {

        emissor = emissor_conf;
        password = password_conf;
        receptor = receptor_conf;

        assunto = assunto_config;
        mensagem = mensagem_config;

        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.socketFactory.class",
                "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", "465");

        Session session = Session.getInstance(props,
                new javax.mail.Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(emissor, password);
                    }
                });

        try {

            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(emissor));
            message.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse(receptor));
            message.setSubject(assunto);

            Multipart multipart = new MimeMultipart();
            MimeBodyPart messageBodyPart = new MimeBodyPart();
            messageBodyPart.setText(mensagem);

            multipart.addBodyPart(messageBodyPart);
            message.setContent(multipart);

            Transport.send(message);

        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

}