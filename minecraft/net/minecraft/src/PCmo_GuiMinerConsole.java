package net.minecraft.src;

import java.util.LinkedHashMap;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;


public class PCmo_GuiMinerConsole extends GuiScreen {
	private PCmo_EntityMiner miner;
	private PC_GuiTextBox programBox;
	private PC_GuiTextBox appendBox;

	private PC_GuiCheckBox checkBridge;
	private PC_GuiCheckBox checkMining;
	private PC_GuiCheckBox checkLava;
	private PC_GuiCheckBox checkWater;
	private PC_GuiCheckBox checkKeepFuel;

	private PC_GuiCheckBox checkCobble;
	private PC_GuiCheckBox checkGravel;
	private PC_GuiCheckBox checkDirt;
	private PC_GuiCheckBox checkTorchFloor;
	private PC_GuiCheckBox checkCompress;

	private String errorString = "";

	private String TITLE = PC_Lang.tr("pc.gui.miner.title");

	public PCmo_GuiMinerConsole(PCmo_EntityMiner machine) {
		miner = machine;
	}

	@Override
	public void updateScreen() {
		programBox.updateCursorCounter();
		appendBox.updateCursorCounter();
	}

	private static final int QUIT = 0, PGM_CLEAR = 1, PGM_RUN = 2, DIR_GO = 3, CLEAR_BUFFER = 4, INSERT = 5, COPY = 6;

	private static int yCheckboxStart = 45;

	@SuppressWarnings("unchecked")
	@Override
	public void initGui() {
		Keyboard.enableRepeatEvents(true);
		controlList.clear();

		PC_GuiButtonAligner.alignSingleToRight(controlList, QUIT, "pc.gui.miner.quit", 45, height / 2 + 85, width / 2 + 110);

		LinkedHashMap<Integer, String> btns = new LinkedHashMap<Integer, String>();
		btns.put(PGM_CLEAR, "pc.gui.miner.clear");
		btns.put(PGM_RUN, "pc.gui.miner.run");
		PC_GuiButtonAligner.alignToRight(controlList, btns, 40, 4, height / 2 - 10, width / 2 + 110);

		PC_GuiButtonAligner.alignSingleToRight(controlList, DIR_GO, "pc.gui.miner.go", 26, height / 2 + 15, width / 2 + 110);

		PC_GuiButtonAligner.alignSingleToRight(controlList, CLEAR_BUFFER, "pc.gui.miner.reset", 45, height / 2 + 55, width / 2 + 110);

		GuiButton but;

		String cpy = PC_Lang.tr("pc.gui.miner.copy");
		String pst = PC_Lang.tr("pc.gui.miner.paste");

		int cpy_w = fontRenderer.getStringWidth(cpy);
		int pst_w = fontRenderer.getStringWidth(pst);

		but = (new PC_GuiClickableText(fontRenderer, INSERT, width / 2 + 105 - pst_w, height / 2 - 100, pst));
		controlList.add(but);

		but = (new PC_GuiClickableText(fontRenderer, COPY, width / 2 + 105 - pst_w - 5 - cpy_w, height / 2 - 100, cpy));
		controlList.add(but);

		String s = miner.program;
		programBox = new PC_GuiTextBox(this, fontRenderer, width / 2 - 110, height / 2 - 86, 220, 0, 6, s);
		programBox.isFocused = false;

		appendBox = new PC_GuiTextBox(this, fontRenderer, width / 2 - 110, height / 2 + 16, 210 - fontRenderer.getStringWidth(PC_Lang
				.tr("pc.gui.miner.go")) - 16, -1, 1, "");
		appendBox.isFocused = true;
		appendBox.setTextColors(0x99ff99, 0x669966);

		checkMining = new PC_GuiCheckBox(this, fontRenderer, width / 2 - 110, height / 2 + yCheckboxStart, miner.miningEnabled,
				PC_Lang.tr("pc.gui.miner.opt.mining"));
		checkBridge = new PC_GuiCheckBox(this, fontRenderer, width / 2 - 110, height / 2 + yCheckboxStart + 12 * 1, miner.bridgeEnabled,
				PC_Lang.tr("pc.gui.miner.opt.bridge"));
		checkLava = new PC_GuiCheckBox(this, fontRenderer, width / 2 - 110, height / 2 + yCheckboxStart + 12 * 2, miner.lavaFillingEnabled,
				PC_Lang.tr("pc.gui.miner.opt.lavaFill"));
		checkWater = new PC_GuiCheckBox(this, fontRenderer, width / 2 - 110, height / 2 + yCheckboxStart + 12 * 3,
				miner.waterFillingEnabled, PC_Lang.tr("pc.gui.miner.opt.waterFill"));
		checkKeepFuel = new PC_GuiCheckBox(this, fontRenderer, width / 2 - 110, height / 2 + yCheckboxStart + 12 * 4, miner.keepAllFuel,
				PC_Lang.tr("pc.gui.miner.opt.keepFuel"));

		checkBridge.isEnabled = miner.level >= 3;
		checkLava.isEnabled = miner.level >= 4;
		checkWater.isEnabled = miner.level >= 6;

		checkCobble = new PC_GuiCheckBox(this, fontRenderer, width / 2 - 30, height / 2 + yCheckboxStart,
				((miner.DESTROY & PCmo_EntityMiner.COBBLE) != 0), PC_Lang.tr("pc.gui.miner.opt.destroyCobble"));
		checkGravel = new PC_GuiCheckBox(this, fontRenderer, width / 2 - 30, height / 2 + yCheckboxStart + 12 * 1,
				((miner.DESTROY & PCmo_EntityMiner.GRAVEL) != 0), PC_Lang.tr("pc.gui.miner.opt.destroyGravel"));
		checkDirt = new PC_GuiCheckBox(this, fontRenderer, width / 2 - 30, height / 2 + yCheckboxStart + 12 * 2,
				((miner.DESTROY & PCmo_EntityMiner.DIRT) != 0), PC_Lang.tr("pc.gui.miner.opt.destroyDirt"));
		checkTorchFloor = new PC_GuiCheckBox(this, fontRenderer, width / 2 - 30, height / 2 + yCheckboxStart + 12 * 4,
				miner.torchesOnlyOnFloor, PC_Lang.tr("pc.gui.miner.opt.torchesOnFloor"));
		checkCompress = new PC_GuiCheckBox(this, fontRenderer, width / 2 - 30, height / 2 + yCheckboxStart + 12 * 3, miner.compressBlocks,
				PC_Lang.tr("pc.gui.miner.opt.compress"));

		checkTorchFloor.isEnabled = miner.level >= 3;
		checkCompress.isEnabled = miner.level >= 5;

		((GuiButton) controlList.get(PGM_RUN)).enabled = programBox.getText().trim().length() > 0;

		((GuiButton) controlList.get(DIR_GO)).enabled = false;

	}

	@Override
	public void onGuiClosed() {
		Keyboard.enableRepeatEvents(false);
		miner.openedGui = null;
		miner.programmingGuiOpen = false;
		miner.miningEnabled = checkMining.isChecked();
		miner.bridgeEnabled = checkBridge.isChecked();
		miner.lavaFillingEnabled = checkLava.isChecked();
		miner.waterFillingEnabled = checkWater.isChecked();
		miner.keepAllFuel = checkKeepFuel.isChecked();
		miner.torchesOnlyOnFloor = checkTorchFloor.isChecked();
		miner.compressBlocks = checkCompress.isChecked();

		miner.DESTROY = (byte) ((checkCobble.isChecked() ? PCmo_EntityMiner.COBBLE : 0)
				| (checkGravel.isChecked() ? PCmo_EntityMiner.GRAVEL : 0) | (checkDirt.isChecked() ? PCmo_EntityMiner.DIRT : 0));

	}

	@Override
	protected void actionPerformed(GuiButton guibutton) {
		if (!guibutton.enabled) { return; }

		if (guibutton.id == QUIT) {
			// Close
			miner.program = programBox.getText().trim();
			// miner.runNewProgram();
			mc.displayGuiScreen(null);
			mc.setIngameFocus();
		}

		if (guibutton.id == INSERT) {
			keyTyped('\026', -1);
		}

		if (guibutton.id == COPY) {
			String copied = "";

			if (programBox.isFocused) {
				copied = programBox.getText();
			}

			if (appendBox.isFocused) {
				copied = appendBox.getText();
			}

			copied = copied.trim();
			if (copied.length() > 0) {
				PC_Logger.finest("copying text: " + copied);
				java.awt.Toolkit.getDefaultToolkit().getSystemClipboard()
						.setContents(new java.awt.datatransfer.StringSelection(new String(copied)), null);
			}
		}

		if (guibutton.id == PGM_CLEAR) {
			programBox.setText("");
		}

		if (guibutton.id == CLEAR_BUFFER) {
			miner.resetEverything();
		}

		if (guibutton.id == PGM_RUN) {
			miner.program = programBox.getText().trim();
			try {
				miner.runNewProgram();
				mc.displayGuiScreen(null);
				mc.setIngameFocus();

			} catch (PCmo_CommandException err) {
				errorString = err.getError();
			}
		}

		if (guibutton.id == DIR_GO) {
			try {
				miner.setCode(appendBox.getText().trim());
				mc.displayGuiScreen(null);
				mc.setIngameFocus();

			} catch (PCmo_CommandException err) {
				errorString = err.getError();
			}
		}
	}

	@Override
	public boolean doesGuiPauseGame() {
		return false;
	}

	@Override
	protected void keyTyped(char c, int i) {

		if (i == Keyboard.KEY_ESCAPE) {
			onGuiClosed();
			return;
		}

		programBox.textboxKeyTyped(c, i);
		appendBox.textboxKeyTyped(c, i);

		((GuiButton) controlList.get(PGM_RUN)).enabled = programBox.getText().trim().length() > 0;

		((GuiButton) controlList.get(DIR_GO)).enabled = appendBox.getText().trim().length() > 0;

		// if(c == '\r')
		// {
		// actionPerformed((GuiButton)controlList.get(QUIT));
		// }
	}

	@Override
	protected void mouseClicked(int i, int j, int k) {
		super.mouseClicked(i, j, k);
		programBox.mouseClicked(i, j, k);
		appendBox.mouseClicked(i, j, k);

		checkBridge.mouseClicked(i, j, k);
		checkMining.mouseClicked(i, j, k);
		checkWater.mouseClicked(i, j, k);
		checkLava.mouseClicked(i, j, k);
		checkKeepFuel.mouseClicked(i, j, k);

		checkCobble.mouseClicked(i, j, k);
		checkDirt.mouseClicked(i, j, k);
		checkGravel.mouseClicked(i, j, k);

		checkTorchFloor.mouseClicked(i, j, k);
		checkCompress.mouseClicked(i, j, k);
	}

	@Override
	public void handleMouseInput() {
		super.handleMouseInput();
		int e = Mouse.getEventDWheel();
		if (e != 0) {
			int i = (Mouse.getEventX() * width) / mc.displayWidth;
			int k = height - (Mouse.getEventY() * height) / mc.displayHeight - 1;

			if (programBox.checkClicked(i, k)) {
				programBox.textboxKeyTyped('\0', e > 0 ? Keyboard.KEY_UP : Keyboard.KEY_DOWN);
			}
		}
	}

	@Override
	public void drawScreen(int i, int j, float f) {
		drawDefaultBackground();

		drawGuiRadioBackgroundLayer(f);

		GL11.glPushMatrix();
		GL11.glRotatef(120F, 1.0F, 0.0F, 0.0F);
		RenderHelper.enableStandardItemLighting();
		GL11.glPopMatrix();

		GL11.glPushMatrix();

		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		GL11.glDisable(32826 /* GL_RESCALE_NORMAL_EXT */);
		RenderHelper.disableStandardItemLighting();
		GL11.glDisable(2896 /* GL_LIGHTING */);
		GL11.glDisable(2929 /* GL_DEPTH_TEST */);

		fontRenderer.drawString(TITLE, width / 2 - (fontRenderer.getStringWidth(TITLE) / 2), (height / 2 - 109), 0x000000);
		fontRenderer.drawString(PC_Lang.tr("pc.gui.miner.programCode"), width / 2 - 110, height / 2 - 96, 0x404040);

		if (errorString.length() > 0) {
			fontRenderer.drawString(errorString, width / 2 - 110 + 1, height / 2 + 5 + 1, 0x999999);
			fontRenderer.drawString(errorString, width / 2 - 110, height / 2 + 5, 0x770000);
		}

		fontRenderer.drawString(".." + miner.commandList.length(), width / 2 + 72, height / 2 + 45, 0x777777);

		programBox.drawTextBox();
		appendBox.drawTextBox();
		// bufferBox.drawTextBox();

		checkBridge.drawCheckBox();
		checkMining.drawCheckBox();
		checkWater.drawCheckBox();
		checkLava.drawCheckBox();
		checkKeepFuel.drawCheckBox();

		checkCobble.drawCheckBox();
		checkDirt.drawCheckBox();
		checkGravel.drawCheckBox();
		checkTorchFloor.drawCheckBox();
		checkCompress.drawCheckBox();

		GL11.glPopMatrix();
		super.drawScreen(i, j, f);
		GL11.glEnable(2896 /* GL_LIGHTING */);
		GL11.glEnable(2929 /* GL_DEPTH_TEST */);
	}

	protected void drawGuiRadioBackgroundLayer(float f) {

		PC_GresWidget.renderTextureSliced_static(this, new PC_CoordI((width-240)/2, (height-230)/2), mod_PCcore.getImgDir() + "gres/dialog.png",
				new PC_CoordI(240, 230), new PC_CoordI(0, 0), new PC_CoordI(256, 256));

	}
}
