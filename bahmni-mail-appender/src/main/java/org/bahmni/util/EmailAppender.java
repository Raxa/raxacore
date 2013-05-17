package org.bahmni.util;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.log4j.Layout;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.helpers.LogLog;
import org.apache.log4j.net.SMTPAppender;
import org.apache.log4j.spi.LoggingEvent;

import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Transport;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;
import java.io.UnsupportedEncodingException;
import java.util.Date;

public class EmailAppender extends SMTPAppender {

	@Override
	protected void sendBuffer() {
		try {
			MimeBodyPart mimeBodyPart = new MimeBodyPart();
			StringBuffer buffer = new StringBuffer();
			addLayoutHeader(buffer);
			addLogEvent(buffer);
			addLayoutFooter(buffer);
			mimeBodyPart.setContent(buffer.toString(), layout.getContentType());
			sendEmail(mimeBodyPart);
		} catch (Exception e) {
			LogLog
					.error("Error occurred while sending e-mail notification.",
							e);
		}
	}

	private void addLogEvent(StringBuffer buffer) throws MessagingException,
			UnsupportedEncodingException {
		for (int i = 0; i < cb.length(); i++) {
			LoggingEvent logEvent = cb.get();
			if (i == 0) {
				Layout subjectLayout = new PatternLayout(getSubject());
				String subject = subjectLayout.format(logEvent);
				subject = subject + getHostName() + getSubjectHash(logEvent);
				msg.setSubject(MimeUtility.encodeText(subject, "UTF-8", null));
			}
			buffer.append(layout.format(logEvent));

			if (!layout.ignoresThrowable())
				continue;
			String[] throwableStrRep = logEvent.getThrowableStrRep();
			if (throwableStrRep != null)
				for (String aThrowableStrRep : throwableStrRep)
					buffer.append(aThrowableStrRep).append(Layout.LINE_SEP);
		}
	}

	private String getHostName() {
		String hostname = System.getenv("HOSTNAME");
		hostname = hostname == null ? "Unknown Host" : hostname;
		return hostname + " | ";
	}

	private String getSubjectHash(LoggingEvent loggingEvent) {
		String[] strings = loggingEvent.getThrowableStrRep();
		if (strings == null || strings.length == 0)
			return "";

		StringBuffer sb = new StringBuffer();
		for (String str : strings) {
			sb.append(str);
		}
		return DigestUtils.md5Hex(sb.toString());
	}

	private void sendEmail(MimeBodyPart mimeBodyPart) throws MessagingException {
		Multipart multipart = new MimeMultipart();
		multipart.addBodyPart(mimeBodyPart);
		msg.setContent(multipart);
		msg.setSentDate(new Date());
		Transport.send(msg);
	}

	private void addLayoutFooter(StringBuffer buffer) {
		String layoutText = layout.getFooter();
		if (layoutText != null)
			buffer.append(layoutText);
	}

	private void addLayoutHeader(StringBuffer buffer) {
		String layoutText = layout.getHeader();
		if (layoutText != null)
			buffer.append(layoutText);
	}
}
