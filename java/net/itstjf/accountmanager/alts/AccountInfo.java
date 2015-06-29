package net.itstjf.accountmanager.alts;

import net.itstjf.accountmanager.util.Encryption;

public class AccountInfo {
	public String user, pass, alias, disuser;

	public AccountInfo(String user, String pass, String alias) {
		this.disuser = user;
		setUsername(user);
		this.pass = Encryption.encrypt(pass);
		this.alias = alias;
	}
	
	public AccountInfo(String user, String disuser, String pass, String alias) {
		this.user = user;
		this.pass = pass;
		this.alias = alias;
		this.disuser = disuser;
	}
	
	public void setUsername(String username) {
		if(username.contains("@")) {
			String[] reg5 = username.split("@");
			String b4 = reg5[0];
			if(b4.length() > 3) b4 = b4.substring(0, 3) + "...";
			else b4 = b4 + "...";
			String after = reg5[1];
			this.disuser = b4 + "@" + after;
		}
		this.user = Encryption.encrypt(username);
	}
}