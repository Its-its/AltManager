package net.itstjf.accountmanager.gui;

import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import javax.swing.JFileChooser;
import javax.swing.UIManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.multiplayer.GuiConnecting;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.MathHelper;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import net.itstjf.accountmanager.alts.AccountInfo;
import net.itstjf.accountmanager.alts.AltManager;

import net.itstjf.accountmanager.main.LiteModAccountManager;
import net.itstjf.accountmanager.util.Encryption;
import net.itstjf.accountmanager.util.GLGui;

/*
 * Ugh, I need to recode the Bar Selection, 
 * It was kinda hardcoded. It works for now so I'll keep it
 * but at some point in the future I will recode the bar selection
 * to make it easier to read and use.
 */

public class GuiAlts extends GuiScreen {
	Minecraft mc = Minecraft.getMinecraft();
	LiteModAccountManager liteMod = LiteModAccountManager.instance;
	
	public AccountInfo alt = null;
	public Throwable hasFailed;
	
	public int page = 0;
	
	public boolean help = false;
	
	public GuiTextField textBox = new GuiTextField(mc.fontRendererObj, 4, 4, 80, 20);
	public GuiTextField searchBox;
	
	public GuiButton buttonAddAlt;
	public GuiButton buttonEditAlt;
	public GuiButton buttonRemoveAlt;
	public GuiButton buttonChangeAcc;
	public GuiButton buttonReconnectAcc;
	
	public GuiButton buttonImportAlts;
	
	public GuiButton buttonHelp;
	
	public GuiButton buttonPagePlus;
	public GuiButton buttonPageMinus;
	
	public GuiAlts() {
		liteMod.altList.clear();
		Encryption.setKey(textBox.getText());
		liteMod.loadAlts();
		if(!liteMod.altList.isEmpty()) alt = liteMod.altList.get(0);
	}
	
	@Override
	public void updateScreen() {
		textBox.updateCursorCounter();
	}
	
	@Override
	public void initGui() {
		textBox.setFocused(false);
		textBox.setText("Password");
		
		int bwidth = 100;
		int bheight = 20;
		int dist = 4;
		
		int middle = width/2 - bwidth/2;
		int bottom = height - bheight - 4;
		
		int sWidth = 98;
		searchBox = new GuiTextField(mc.fontRendererObj, width - sWidth - 1 - dist, 14, sWidth, 20);
		searchBox.setFocused(false);
		
		buttonList.clear();
		buttonList.add(buttonAddAlt = 		new GuiButton(0, middle - (bwidth/2) - (dist/2), bottom - bheight - dist, bwidth, bheight, I18n.format("net.itstjf.acc.button.add", new Object[0])));
		buttonList.add(buttonEditAlt = 		new GuiButton(1, middle - (bwidth/2) - (dist/2), bottom, bwidth, bheight, I18n.format("net.itstjf.acc.button.edit", new Object[0])));
		buttonList.add(buttonRemoveAlt = 	new GuiButton(2, middle + (bwidth/2) + (dist/2), bottom - bheight - dist, bwidth, bheight, I18n.format("net.itstjf.acc.button.remove", new Object[0])));
		buttonList.add(buttonChangeAcc = 	new GuiButton(3, middle + (bwidth/2) + (dist/2), bottom, 75, bheight, I18n.format("net.itstjf.acc.button.login", new Object[0])));
		buttonList.add(buttonReconnectAcc = new GuiButton(7, middle + bwidth + dist + 24, bottom, bwidth/4, bheight, I18n.format("net.itstjf.acc.button.reconnect", new Object[0])));
		
		buttonList.add(buttonImportAlts = 	new GuiButton(6, width - bwidth - dist, 34 + dist, bwidth, bheight, I18n.format("net.itstjf.acc.button.import", new Object[0])));
		
		buttonList.add(buttonHelp = 		new GuiButton(8, width - 24, bottom, 20, 20, "?"));
		
		buttonList.add(buttonPagePlus = 	new GuiButton(4, middle + bwidth + 6 + bwidth/2, bottom, bwidth/2, bheight, I18n.format("net.itstjf.acc.button.pagenext", new Object[0])));
		buttonList.add(buttonPageMinus = 	new GuiButton(5, middle - bwidth - dist - (dist/2), bottom, bwidth/2, bheight, I18n.format("net.itstjf.acc.button.pageprev", new Object[0])));
		
		if(liteMod.altList.isEmpty()) buttonChangeAcc.enabled = false;
	}

	@Override
	public void drawScreen(int par1, int par2, float par3) {
		drawDefaultBackground();
		drawCenteredString(fontRendererObj, I18n.format("net.itstjf.acc.menu.string.alts", new Object[] { page + 1 }), this.width / 2, 7, -1);
		
		if (hasFailed != null) {
			drawCenteredString(fontRendererObj, "\u00A7c Failed Login: " + hasFailed.getLocalizedMessage(), this.width / 2, this.height - 60, -1);
		}
		
		drawString(fontRendererObj, "\u00A7cLogged in as:", 2, height - 20, 0xffffffff);
		String user = mc.getSession().getUsername();
		if (mc.getSession().getToken().equals("0")) user += " \u00A7cOFFLINE";
		drawString(fontRendererObj, "\u00A72" + user, 22, height - 10, 0xffffffff);
		
		int height = 40;
		int multi = height + 5;
		int selWidth = 200;
		int x = width/2 - selWidth/2;
		int maxAmount = ((this.height - 74)/multi);
		
		GL11.glPushMatrix();
		int i = 0;
		int re = 0;
		
		//My temp retarded alt display
		for (AccountInfo acc : liteMod.altList) {
			if(!searchBox.getText().isEmpty() && !acc.alias.contains(searchBox.getText())) continue;
			
			if(i < maxAmount * page) {
				i++;
				re = i;
				continue;
			}
			
			if(i >= maxAmount * (page + 1)) break;
			
			int pos = (i - re) * multi;
			
			GLGui.drawBorderedRect(x, pos + 20, selWidth, 40, 0x55000000, 0x88000000);
			GLGui.drawBorderedRect(x + 1, pos + 21, 38, 38, 0x55000000, 0x88000000);
			
			String alias = acc.alias;
			if(alias.length() > 20) alias = alias.substring(0, 21) + ".";
			drawString(fontRendererObj, "\u00A77Alias: " + alias, x + 42, pos + 30, -1);
			
			String name = acc.disuser;
			drawString(fontRendererObj, "\u00A77User: " + name, x + 42, pos + 42, -1);
			
			if(alt != null && alt.equals(acc)) {
				GLGui.drawHLine(x - 2, pos + 20, selWidth + 4, 0xff9999ff);
				GLGui.drawHLine(x - 2, pos + 59, selWidth + 4, 0xff9999ff);
			}
			
			i++;
		}
		
		GL11.glPopMatrix();
		String encryptionText = I18n.format("net.itstjf.acc.menu.string.helpencryption1", new Object[0]);
		if(help) encryptionText = I18n.format("net.itstjf.acc.menu.string.helpencryption2", new Object[0]);
		mc.fontRendererObj.drawSplitString(encryptionText, 4, 30, 85, 0xffffffff);
		mc.fontRendererObj.drawSplitString(I18n.format("net.itstjf.acc.menu.string.helpimport", new Object[0]), width - 102, 60, 100, 0xffffffff);
		String sname = I18n.format("net.itstjf.acc.menu.string.searchalias", new Object[0]);
		drawString(fontRendererObj, sname, width - 65 - mc.fontRendererObj.getStringWidth(sname)/2, 4, 0xffffffff);

		if(liteMod.nextVersion != null) {
			String name = "New Version: " + liteMod.nextVersion;
			mc.fontRendererObj.drawStringWithShadow(name, 2, this.height - 34, 0xffff0000);
		}
		
		textBox.drawTextBox();
		searchBox.drawTextBox();
		
		super.drawScreen(par1, par2, par3);
	}

	@Override
	protected void keyTyped(char character, int key) {
		textBox.textboxKeyTyped(character, key);
		if(searchBox.textboxKeyTyped(character, key)) {
			page = 0;
			alt = null;
		}
		
		Encryption.setKey(textBox.getText());
		
		if (key == Keyboard.KEY_ESCAPE) {
			mc.displayGuiScreen(null);
		}
	}
	
	@Override
	public void mouseClicked(int mx, int my, int b) {
		textBox.mouseClicked(mx, my, b);
		searchBox.mouseClicked(mx, my, b);
		
		Encryption.setKey(textBox.getText());
		
		int height = 40;
		int multi = height + 5;
		int selWidth = 200;
		int x = width/2 - selWidth/2;
		
		int maxAmount = ((this.height - 74)/multi);
		
		int i = 0;
		int re = 0;
		
		//My temp retarded alt selection
		for (AccountInfo acc : liteMod.altList) {
			if(!searchBox.getText().isEmpty() && !acc.alias.contains(searchBox.getText())) continue;
			
			if(i < maxAmount * page) {
				i++;
				re = i;
				continue;
			}
			
			if(i >= maxAmount * (page + 1)) break;
			int pos = (i - re) * multi + 20;
			
			if(mx >= x && my >= pos && mx < x + selWidth && my < pos + 40) {
				alt = acc;
			}
			i++;
		}
		super.mouseClicked(mx, my, b);
	}
	
	@Override
	protected void actionPerformed(GuiButton button) {
		switch(button.id) {
			case 0:	//Add Alt
				mc.displayGuiScreen(new GuiAddAlt());
				if(!liteMod.altList.isEmpty()) buttonChangeAcc.enabled = true;
				break;
			case 1:	//Edit Alt
				if(alt == null) break;
				
				mc.displayGuiScreen(new GuiAddAlt(alt.user, alt.pass, alt.alias, textBox.getText(), alt));
				if(!liteMod.altList.isEmpty()) buttonChangeAcc.enabled = true;
				break;
			case 2: //Remove Alt
				if(alt == null) break;
				
				try {
					liteMod.altList.remove(alt);
					liteMod.saveAlts();
					if(liteMod.altList.isEmpty()) buttonChangeAcc.enabled = false;
					alt = null;
				} catch (Exception e) {}
				break;
			case 3: //Use Alt
				if(liteMod.altList.isEmpty()) break;
				if(alt == null) break;
				
				if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
					AltManager.getInstance().setUserOffline(alt.alias);
					hasFailed = null;
					mc.displayGuiScreen(null);
					break;
				}
				
				try {
					hasFailed = AltManager.getInstance().setUser(alt.user, alt.pass);
					if (hasFailed == null) {
						mc.displayGuiScreen(null);
					}
				} catch (Exception e) {}
				break;
				
			case 4: //Page +
				page++;
				if(page >= maxPages() - 1) page = maxPages() - 1;
				alt = null;
				break;
			case 5: //Page -
				page--;
				if(page < 0) page = 0;
				alt = null;
				break;
			case 6: //Import
				JFileChooser chooser = new JFileChooser();
				chooser.setFileView(null);
				try { 
				    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
				} catch (Exception e) {
				    e.printStackTrace();
				}
				
				chooser.showOpenDialog(null);
				if(chooser.getSelectedFile() == null) break;
				
				List<String> alts = null;
				
				try {
					alts = Files.readAllLines(chooser.getSelectedFile().toPath());
				} catch (IOException e) {
					e.printStackTrace();
				}
				if(alts != null) {
					for(String str : alts) {
						if(!str.contains(":")) continue;
						String[] spl = str.split(":");
						
						String user = str.split(":")[0];
						String pass = str.split(":")[1];
						String alias = "Imported Alt";
						
						if(spl.length == 2) {
							user = spl[0];
							pass = spl[1];
						} else if(spl.length == 3) {
							alias = spl[0];
							user = spl[1];
							pass = spl[2];
						}
						
						AccountInfo data = new AccountInfo(user, pass, alias);
						
						contLabel:
						for(AccountInfo acc : liteMod.altList) {
							if(acc.user.equals(data.user)) {
								continue contLabel;
							}
						}
						liteMod.altList.add(data);
					}
					liteMod.saveAlts();
				}
				break;
			case 7: //Login & Reconnect
				if(liteMod.altList.isEmpty()) break;
				if(alt == null) break;
				
				if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
					AltManager.getInstance().setUserOffline(alt.alias);
					hasFailed = null;
					mc.displayGuiScreen(null);
					break;
				}
				
				try {
					hasFailed = AltManager.getInstance().setUser(alt.user, alt.pass);
					if (hasFailed == null) {
						mc.displayGuiScreen(null);
					}
				} catch (Exception e) {}
				
				if(mc.getCurrentServerData() != null) {
					mc.theWorld.sendQuittingDisconnectingPacket();
					mc.displayGuiScreen(new GuiConnecting(this, this.mc, mc.getCurrentServerData()));
				}
				break;
				
			case 8: //Help
				help = !help;
				break;
		}
	}
	
	private int maxPages() {
		int amount = liteMod.altList.size();
		if(!searchBox.getText().isEmpty()) {
			amount = 0;
			for (AccountInfo acc : liteMod.altList) {
				if(acc.alias.contains(searchBox.getText())) amount++;
			}
		}
		return MathHelper.ceiling_float_int(amount/(float)((height - 74)/45));
	}
}