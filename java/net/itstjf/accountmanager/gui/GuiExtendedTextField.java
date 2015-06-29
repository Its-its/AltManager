package net.itstjf.accountmanager.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiTextField;

public class GuiExtendedTextField extends GuiTextField {
	private boolean isSecret = false;
	private boolean showText = false;
	
	public GuiExtendedTextField(int x, int y, int width, int height) {
		super(Minecraft.getMinecraft().fontRendererObj, x, y, width, height);
	}
	
	
	public void setSecret(boolean secret) {
		this.isSecret = secret;
	}
	
	public void showText(boolean show) {
		if(!isSecret) this.showText = true;
		this.showText = show;
	}
	
	public boolean isTextShowing() {
		if(!isSecret) return true;
		return this.showText;
	}
	
	public boolean isSecret() {
		return this.isSecret;
	}
	
	@Override
	public void drawTextBox() {
		boolean canShow = isSecret && !showText;
		String oldText = "";
		if(canShow) {
			oldText = getText();
			String newText = "";
			for(int i = 0; i < getText().length(); i++) {
				newText += "*";
			}
			setText(newText);
		}
		
		super.drawTextBox();
		
		if(canShow) {
			setText(oldText);
		}
	}
}