package net.itstjf.accountmanager.main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map.Entry;

import org.lwjgl.input.Keyboard;

import net.itstjf.accountmanager.alts.AccountInfo;
import net.itstjf.accountmanager.gui.GuiAlts;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiMultiplayer;
import net.minecraft.client.settings.KeyBinding;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mumfrey.liteloader.Tickable;
import com.mumfrey.liteloader.core.LiteLoader;

public class LiteModAccountManager implements Tickable {
	public static LiteModAccountManager instance;
	public final File location = new File(LiteLoader.getCommonConfigFolder(), "Alts.json");
	private Gson gson = new GsonBuilder().setPrettyPrinting().create();
	
	public KeyBinding keyGui = new KeyBinding("Display Alts", Keyboard.KEY_P, "key.categories.accountmanager.gui");
	
	public ArrayList<AccountInfo> altList = new ArrayList();
	
	public String nextVersion = null;
	
	public LiteModAccountManager() {
		instance = this;
		String update = isUpdateAvailable();
		
		if(update != null) {
			nextVersion = update;
		}
	}
	
	private String isUpdateAvailable() {
		try {
	        URL url = new URL("https://dl.dropboxusercontent.com/s/jg6pt83zqsqz9cy/AccManager1710.txt");
	        BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
	        String str = in.readLine();
	        if(!str.equalsIgnoreCase(getVersion())) {
	        	in.close();
	        	return str;
	        }
	        in.close();
	        return null;
	    } catch (IOException e) {}
		return null;
	}
	
	@Override
	public String getVersion() {
		return "v1.6";
	}

	@Override
	public void init(File configPath) {
		LiteLoader.getInput().registerKeyBinding(keyGui);
		
		if(!location.exists()) saveAlts();
		else loadAlts();
	}

	@Override
	public void upgradeSettings(String version, File configPath, File oldConfigPath) {
		
	}

	@Override
	public String getName() {
		return "Account Manager";
	}

	@Override
	public void onTick(Minecraft mc, float partialTicks, boolean inGame, boolean clock) {
		if(inGame) {
			if(keyGui.isPressed()) {
				mc.displayGuiScreen(new GuiAlts());
			}
		}
		
		if(mc.currentScreen instanceof GuiMainMenu || mc.currentScreen instanceof GuiMultiplayer) {
			if(Keyboard.isKeyDown(keyGui.getKeyCode())) {
				mc.displayGuiScreen(new GuiAlts());
			}
		}
	}
	
	public void loadAlts() {
		try {
			BufferedReader loader = new BufferedReader(new FileReader(location));
			JsonObject json = (JsonObject)new JsonParser().parse(loader);
			loader.close();
			Iterator<Entry<String, JsonElement>> itr = json.entrySet().iterator();
			while(itr.hasNext()) {
				Entry<String, JsonElement> entry = itr.next();
				JsonObject element = (JsonObject)entry.getValue();
				
				String user = element.get("Username").getAsString();
				String alias = element.get("Alias").getAsString();
				String pass = element.get("Password").getAsString();
				String shou = element.get("User").getAsString();
				AccountInfo accData = new AccountInfo(user, shou, pass, alias);
				altList.add(accData);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void saveAlts() {
		try {
			JsonObject json = new JsonObject();
			int x = 1;
			
			for(AccountInfo accData : altList) {
				JsonObject jsonData = new JsonObject();
				jsonData.addProperty("Alias", accData.alias);
				jsonData.addProperty("User", accData.disuser);
				jsonData.addProperty("Username", accData.user);
				jsonData.addProperty("Password", accData.pass);
				json.add("Alt #" + x, jsonData);
				x++;
			}
			PrintWriter save = new PrintWriter(new FileWriter(location));
			save.println(gson.toJson(json));
			save.close();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
}