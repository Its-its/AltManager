package net.itstjf.accountmanager.alts;

import java.util.UUID;

import net.minecraft.client.Minecraft;
import net.minecraft.util.Session;
import net.itstjf.accountmanager.util.Encryption;
import net.itstjf.accountmanager.util.SessionChanger;
import net.itstjf.accountmanager.main.LiteModAccountManager;

import com.mojang.authlib.Agent;
import com.mojang.authlib.AuthenticationService;
import com.mojang.authlib.UserAuthentication;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import com.mojang.util.UUIDTypeAdapter;

public class AltManager {
	LiteModAccountManager liteMod = LiteModAccountManager.instance;
	
	private static AltManager manager = null;
	public AuthenticationService authService;
	public MinecraftSessionService sessionService;
	public UUID uuid;
	public UserAuthentication auth;
	private String currentUser;
	private String currentPass;

	private AltManager() {
		uuid = UUID.randomUUID();
		authService = new YggdrasilAuthenticationService(Minecraft.getMinecraft().getProxy(), uuid.toString());
		auth = authService.createUserAuthentication(Agent.MINECRAFT);
		sessionService = authService.createMinecraftSessionService();
	}
	
	public static AltManager getInstance() {
		if (manager == null) {
			manager = new AltManager();
		}

		return manager;
	}

	public Throwable setUser(String username, String password) {
		String newUser = null;
		String newPass = null;
		
		try {
			newUser = Encryption.decrypt(username);
		} catch (Exception e) {
        	e.printStackTrace();
        	return e;
        }
		
		try {
			newPass = Encryption.decrypt(password);
		} catch (Exception e) {
        	e.printStackTrace();
        	return e;
        }
		
		auth.logOut();
		auth.setUsername(newUser);
		auth.setPassword(newPass);
		try {
			auth.logIn();
			Session session = new Session(this.auth.getSelectedProfile().getName(), UUIDTypeAdapter.fromUUID(auth.getSelectedProfile().getId()), auth.getAuthenticatedToken(), auth.getUserType().getName());
			SessionChanger.setSession(session);
			for (int i = 0; i < liteMod.altList.size(); i++) {
				AccountInfo data = liteMod.altList.get(i);
				if (data.user.equals(newUser) && data.pass.equals(newPass)) {
					data.alias = session.getUsername();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return e;
		}
		return null;
	}

	public boolean setUserOffline(String username) {
		auth.logOut();
		Session session = new Session(username, username, "0", "legacy");
		try {
			SessionChanger.setSession(session);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}
}
