package net.itstjf.accountmanager.util;

import static org.lwjgl.opengl.GL11.*;

public class GLGui {
	public static void drawBorderedRect(float x, float y, float x1, float y1, int insideC, int borderC) {
		enableGL2D();
		x *= 2;
		x1 *= 2;
		y *= 2;
		y1 *= 2;
		glScalef(0.5F, 0.5F, 0.5F);
		drawVLine(x, y, y1, borderC);
		drawVLine(x + x1 - 1, y, y1, borderC);
		drawHLine(x, y, x1, borderC);
		drawHLine(x, y + y1 - 1, x1, borderC);
		drawRect(x + 1, y + 1, x1 - 1.5F, y1 - 2F, insideC);
		glScalef(2.0F, 2.0F, 2.0F);
		disableGL2D();
	}
	
	public static void drawHLine(float x, float y, float width, int color) {
		drawRect(x, y, width, 1, color);
	}

	public static void drawVLine(float x, float y, float height, int color) {
		drawRect(x, y, 1, height, color);
	}
	
	public static void drawLine(float x, float y, float x1, float y1) {
		glBegin(GL_LINES);
		glVertex2f(x, y);
		glVertex2f(x1, y1);
		glEnd();
	}
	
	public static void drawRect(float x, float y, float width, float height, int color) {
		enableGL2D();
		glColor(color);
		drawRect(x, y, width, height);
		disableGL2D();
	}
	
	public static void glColor(int hex) {
		float alpha = (hex >> 24 & 255) / 255.0F;
		float red = (hex >> 16 & 255) / 255.0F;
		float green = (hex >> 8 & 255) / 255.0F;
		float blue = (hex & 255) / 255.0F;
		glColor4f(red, green, blue, alpha);
	}
	
	public static void drawRect(float x, float y, float width, float height) {
		glBegin(GL_QUADS);
		glVertex2f(x, y + height);
		glVertex2f(x + width, y + height);
		glVertex2f(x + width, y);
		glVertex2f(x, y);
		glEnd();
	}
	
	public static void enableGL2D() {
		glDisable(GL_DEPTH_TEST);
		glEnable(GL_BLEND);
		glDisable(GL_TEXTURE_2D);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		glDepthMask(true);
		glEnable(GL_LINE_SMOOTH);
		glHint(GL_LINE_SMOOTH_HINT, GL_NICEST);
		glHint(GL_POLYGON_SMOOTH_HINT, GL_NICEST);
	}
	
	public static void disableGL2D() {
		glEnable(GL_TEXTURE_2D);
		glDisable(GL_BLEND);
		glEnable(GL_DEPTH_TEST);
		glDisable(GL_LINE_SMOOTH);
		glHint(GL_LINE_SMOOTH_HINT, GL_DONT_CARE);
		glHint(GL_POLYGON_SMOOTH_HINT, GL_DONT_CARE);
	}
}