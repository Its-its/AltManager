package net.itstjf.accountmanager.gui;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.resources.I18n;

import org.lwjgl.input.Keyboard;

import net.itstjf.accountmanager.alts.AccountInfo;
import net.itstjf.accountmanager.main.LiteModAccountManager;
import net.itstjf.accountmanager.util.Encryption;

public class GuiAddAlt extends GuiScreen {
	private String lastName = "";
	private String lastPass = "";
	private String lastAlias = "";
	
	public GuiExtendedTextField userField;
	public GuiExtendedTextField passField;
	public GuiTextField aliasField;
	
	public GuiButton buttonSubmit;
	public GuiButton buttonReturn;
	
	public GuiButton buttonShowUser;
	public GuiButton buttonShowPass;
	
	private boolean newAlt;
	private AccountInfo alt;
	
	public int x = 12;
	public int y = 40;
	
	private int loc = 0;
	
	public GuiAddAlt() {
		newAlt = true;
	}
	
	public GuiAddAlt(String user, String pass, String alias, String encryption, AccountInfo alt) {
		this.lastAlias = alias;
		try {
			this.lastName = Encryption.decrypt(user);
		} catch(Exception e) {
			this.lastName = "Wrong Password";
		}
		
		try {
			this.lastPass = Encryption.decrypt(pass);
		} catch(Exception e) {
			this.lastPass = "Wrong Password";
		}
		
		this.alt = alt;
		newAlt = false;
	}
	
	@Override
	public void initGui() {
		y = 40;
		int width = 180;
		int height = 20;
		int space = 14;
		
		if(userField != null) lastName = userField.getText();
		if(passField != null) lastPass = passField.getText();
		if(aliasField != null) lastAlias = aliasField.getText();
		
		userField = new GuiExtendedTextField(x, y, width, height);
		y += space + height;
		passField = new GuiExtendedTextField(x, y, width, height);
		y += space + height;
		aliasField = new GuiTextField(mc.fontRendererObj, x, y, width + 20, height);
		
		userField.setMaxStringLength(512);
		passField.setMaxStringLength(512);
		aliasField.setMaxStringLength(512);
		
		userField.setText(lastName);
		passField.setText(lastPass);
		aliasField.setText(lastAlias);
		
		userField.setSecret(true);
		passField.setSecret(true);
		
		buttonList.add(buttonSubmit = new GuiButton(0, this.width/2 - 102, this.height - 24, 100, 20, I18n.format("net.itstjf.acc.button.submit", new Object[0])));
		buttonList.add(buttonReturn = new GuiButton(1, this.width/2 + 2, this.height - 24, 100, 20, I18n.format("net.itstjf.acc.button.back", new Object[0])));
		y -= space + height;
		buttonList.add(buttonSubmit = new GuiButton(2, x + width + 2, y, 20, 20, "S"));
		y -= space + height;
		buttonList.add(buttonReturn = new GuiButton(3, x + width + 2, y, 20, 20, "S"));
	}
	
	@Override
	public void drawScreen(int mx, int my, float pt) {
		drawDefaultBackground();
		drawCenteredString(fontRendererObj, I18n.format("net.itstjf.acc.menu.string.addalt", new Object[0]), width / 2, 7, -1);
		
		drawString(fontRendererObj, I18n.format("net.itstjf.acc.menu.string.user", new Object[0]), x, 29, 0xffffffff);
		drawString(fontRendererObj, I18n.format("net.itstjf.acc.menu.string.pass", new Object[0]), x, 29 + 34, 0xffffffff);
		drawString(fontRendererObj, I18n.format("net.itstjf.acc.menu.string.alias", new Object[0]), x, 29 + 68, 0xffffffff);
		
		userField.drawTextBox();
		passField.drawTextBox();
		aliasField.drawTextBox();
		
		super.drawScreen(mx, my, pt);
	}
	
	@Override
	protected void keyTyped(char character, int button) {
		Keyboard.enableRepeatEvents(true);
		
		userField.textboxKeyTyped(character, button);
		passField.textboxKeyTyped(character, button);
		aliasField.textboxKeyTyped(character, button);
	}
	
	@Override
	public void mouseClicked(int x, int y, int b) {
		userField.mouseClicked(x, y, b);
		passField.mouseClicked(x, y, b);
		aliasField.mouseClicked(x, y, b);
		
		super.mouseClicked(x, y, b);
	}
	
	@Override
	protected void actionPerformed(GuiButton button) {
		switch(button.id) {
			case 0:
				if(newAlt) {
					LiteModAccountManager.instance.altList.add(new AccountInfo(userField.getText(), passField.getText(), aliasField.getText()));
				} else {
					alt.pass = Encryption.encrypt(passField.getText());
					alt.setUsername(userField.getText());
					alt.alias = aliasField.getText();
				}
				LiteModAccountManager.instance.saveAlts();
				mc.displayGuiScreen(new GuiAlts());
				break;
			case 1:
				mc.displayGuiScreen(new GuiAlts());
				break;
			case 2:
				passField.showText(!passField.isTextShowing());
				break;
			case 3:
				userField.showText(!userField.isTextShowing());
				break;
		}
	}
}
