package com.example.nftmarket.UserDetails;
import java.io.UnsupportedEncodingException;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.nftmarket.Entity.Provider;
import com.example.nftmarket.Entity.Users;
import com.example.nftmarket.Entity.Wallet;
import com.example.nftmarket.Repository.UsersRepo;
import com.example.nftmarket.Repository.WalletRepo;

import net.bytebuddy.utility.RandomString;


@Service
public class UserServices {
    @Autowired
    private UsersRepo repo;
    
    @Autowired
    private WalletRepo wrepo;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
     
    @Autowired
    private JavaMailSender mailSender;
 
     
    public void register(Users user, String siteURL) throws UnsupportedEncodingException, MessagingException {
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        
	        user.setPassword(encodedPassword);
	         
	        String randomCode = RandomString.make(64);
	        user.setVerificationCode(randomCode);
	        user.setEnabled(false);
	         
	        repo.save(user);
	        System.out.println("Inside register"); 
	        sendVerificationEmail(user, siteURL);        
    }
    
    public boolean checkUserExists(Users user) {
    	System.out.println("Checking if user exists");
    	Users existingUserEmail = repo.findByEmail(user.getUsername());
    	Users existingUserNickname = repo.findByNickName(user.getNickName());
    	if(existingUserEmail!=null || existingUserNickname!=null) 
    		return true;
    	else
    		return false;
    }
     
    public void processOAuthPostLogin(String username) {
        Users existUser = repo.getUserByUsername(username);
         
        if (existUser == null) {
            Users newUser = new Users();
            newUser.setUsername(username);
            newUser.setProvider(Provider.GOOGLE);
            newUser.setEnabled(true);          

             
            Wallet wallet = new Wallet(newUser);
            newUser.setWallet(wallet);
            repo.save(newUser);
            wrepo.save(wallet); 
             
        }
         
    }
    
    private void sendVerificationEmail(Users user, String siteURL) throws MessagingException, UnsupportedEncodingException {
    	   String toAddress = user.getUsername();
    	    String fromAddress = "nftmarketplace773@gmail.com";
    	    String senderName = "NTM";
    	    String subject = "Please verify your registration";
    	    String content = "Dear [[name]],<br>"
    	            + "Please click the link below to verify your registration:<br>"
    	            + "<h3><a href=\"[[URL]]\" target=\"_self\">VERIFY</a></h3>"
    	            + "Thank you,<br>"
    	            + "NTM";
    	     
    	    MimeMessage message = mailSender.createMimeMessage();
    	    MimeMessageHelper helper = new MimeMessageHelper(message);
    	     
    	    helper.setFrom(fromAddress, senderName);
    	    helper.setTo(toAddress);
    	    helper.setSubject(subject);
    	     
    	    content = content.replace("[[name]]", user.getUsername());
    	    String verifyURL = siteURL + "/verify?code=" + user.getVerificationCode();
    	     
    	    content = content.replace("[[URL]]", verifyURL);
    	    System.out.println(content);
    	    helper.setText(content, true);
    	    System.out.println("About to send verification mail"); 
    	    mailSender.send(message);
    	    System.out.println("Sent message");
    }

    public boolean verify(String verificationCode) {
        Users user = repo.findByVerificationCode(verificationCode);
        System.out.println("Verifying User in UserServices");
        System.out.println(verificationCode);
        if (user == null || user.isEnabled()) {
            return false;
        } else {
        	System.out.println("Updating and enabling User");
            user.setVerificationCode(null);
            user.setEnabled(true);
            Wallet wallet = new Wallet(user);
            user.setWallet(wallet);
            repo.save(user);
            wrepo.save(wallet); 
            return true;
        }
         
    }
}
