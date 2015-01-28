package user.com.profiling.util;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.util.Log;
import android.util.Patterns;

import java.io.UnsupportedEncodingException;
import java.util.Properties;
import java.util.regex.Pattern;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 * Created by suresh.kumar on 2015-01-28.
 */
public class EmailUtil {
	private static final String TAG = EmailUtil.class.getSimpleName();

	private static Session createSessionObject(final String username, final String password) {
		Properties properties = new Properties();
		properties.put("mail.smtp.auth", "true");
		properties.put("mail.smtp.starttls.enable", "true");
		properties.put("mail.smtp.host", "smtp.gmail.com");
		properties.put("mail.smtp.port", "587");

		return Session.getInstance(properties, new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(username, password);
			}
		});
	}

	public static boolean sendEmail(String fromEmail, String fromPwd, String toEmail, String subject, String body) {
		try {
			Message msg = createMessage(fromEmail, toEmail, subject, body, createSessionObject(fromEmail, fromPwd));
			Transport.send(msg);
			return true;
		} catch (Exception ex) {
			Log.e(TAG, ex.toString());
			return false;
		}
	}

	private static Message createMessage(String fromEmail, String toEmail, String subject, String messageBody, Session session) throws MessagingException,
			UnsupportedEncodingException {
		Message message = new MimeMessage(session);
		message.setFrom(new InternetAddress(fromEmail, ""));
		message.addRecipient(Message.RecipientType.TO, new InternetAddress(toEmail, toEmail));
		message.setSubject(subject);
		message.setText(messageBody);
		return message;
	}

	public static String getUserPrimaryEmail(Context context) {
		Pattern emailPattern = Patterns.EMAIL_ADDRESS; // API level 8+
		Account[] accounts = AccountManager.get(context).getAccounts();
		for (Account account : accounts) {
			if (emailPattern.matcher(account.name).matches()) {
				return account.name;
			}
		}
        return "";
	}
}
