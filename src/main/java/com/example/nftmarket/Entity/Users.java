package com.example.nftmarket.Entity;

import javax.persistence.*;

@Entity
public class Users {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int userId;
    private String username;
    public String nickname;
    private String password;
    private String verificationCode;
    private boolean enabled;
    
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "wallet", referencedColumnName = "walletId")
    private Wallet wallet;

    @Enumerated(EnumType.STRING)
    private Provider provider;
 
    public Provider getProvider() {
        return provider;
    }
 
    public void setProvider(Provider provider) {
        this.provider = provider;
    }
            
    
    public Users() {
    }

    public Users(int userId, String username, String nickname, String password, Wallet wallet) {
        this.userId = userId;
        this.username = username;
        this.nickname = nickname;
        this.password = password;
        this.wallet = wallet;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
    
    public String getNickName() {
    	return nickname;
    }
    
    public void setNickName(String nickname) {
    	this.nickname = nickname;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Wallet getWallet() {
        return wallet;
    }

    public void setWallet(Wallet wallet) {
        this.wallet = wallet;
    }

	public String getVerificationCode() {
		return verificationCode;
	}

	public void setVerificationCode(String verificationCode) {
		this.verificationCode = verificationCode;
	}

//	public boolean isEnabled() {
//		return enabled;
//	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public boolean isEnabled() {
		// TODO Auto-generated method stub
		return enabled;
	}
}
