package net.minecraft.src;

import net.minecraft.src.PC_GresWidget.PC_GresAlignH;

import org.lwjgl.input.Keyboard;


public class PCma_GuiReplacer implements PC_IGresBase {

	private IInventory playerinv;
	private PCma_TileEntityReplacer teReplacer;
	
	private PC_GresTextEdit textedit[] = new PC_GresTextEdit[3];
	private PC_GresButton button[] = new PC_GresButton[2];
	private PC_GresLabel errorLabel;
	
	private boolean valid;

	
	public PCma_GuiReplacer(IInventory playerinv, PCma_TileEntityReplacer teReplacer){
		this.playerinv = playerinv;
		this.teReplacer = teReplacer; 
	}
	
	@Override
	public void initGui(PC_IGresGui gui) {
		PC_GresWidget w = new PC_GresWindow(225, 50, PC_Lang.tr("pc.gui.blockReplacer.title"), "/PowerCraft/betterGui/MP_Overlay.png")
				.setHorizontalAligner(PC_GresAlignH.STRETCH);
		
		PC_GresWidget hg;
		
		
		hg = new PC_GresHGroup();
		
		hg.add(new PC_GresVGroup().add(new PC_GresLabel("X")).add(textedit[0] = new PC_GresTextEdit("" + teReplacer.coordOffset[0], 4)));
		hg.add(new PC_GresVGroup().add(new PC_GresLabel("Y")).add(textedit[1] = new PC_GresTextEdit("" + teReplacer.coordOffset[1], 4)));
		hg.add(new PC_GresVGroup().add(new PC_GresLabel("Z")).add(textedit[2] = new PC_GresTextEdit("" + teReplacer.coordOffset[2], 4)));

		w.add(hg);
		
		
		hg = new PC_GresHGroup().setHorizontalAligner(PC_GresAlignH.CENTER);
		
		hg.add(errorLabel = new PC_GresLabel(""));
		errorLabel.color = 0x990000;
		
		w.add(hg);
		
		
		hg = new PC_GresHGroup().setHorizontalAligner(PC_GresAlignH.RIGHT);
		hg.add(button[0] = new PC_GresButton(PC_Lang.tr("pc.gui.cancel")));
		hg.add(button[1] = new PC_GresButton(PC_Lang.tr("pc.gui.ok")));
		
		w.add(hg);
		
		gui.add(w);
	}

	@Override
	public void onGuiClosed(PC_IGresGui gui) {
		Keyboard.enableRepeatEvents(false);
	}
	
	@Override
	public boolean isKeyValid(char c, int i, PC_GresWidget widget, PC_IGresGui gui){
		if (PC_KeyUtils.filterKeyIntMinus(c, i))
			return true;
		return false;
	}
	
	@Override
	public void actionPerformed(PC_GresWidget widget, PC_IGresGui gui) {
		if(widget == button[0]){
			gui.close();
		}else if(widget == button[1]){
			valid = false;
			for(int count=0; count<=2; count++){
				if (!textedit[count].getTitle().equals("") && !textedit[count].getTitle().equals("-")) {
					if(Integer.valueOf(textedit[count].getTitle())<-16 || Integer.valueOf(textedit[count].getTitle())>16){
						errorLabel.setTitle(PC_Lang.tr("pc.gui.blockReplacer.errWrongValue"));
						button[1].enable(false);
						return;
					}else{
						if(Integer.valueOf(textedit[count].getTitle())!=0){
							valid = true;
						}
					}
					
				} else {
					errorLabel.setTitle(PC_Lang.tr("pc.gui.blockReplacer.errWrongValue"));
					button[1].enable(false);
					return;
				}
			}
			if(valid){
				for(int count=0; count<=2; count++)
					teReplacer.coordOffset[count] = Integer.valueOf(textedit[count].getTitle());
				gui.close();
			}else
				errorLabel.setTitle(PC_Lang.tr("pc.gui.blockReplacer.err3zeros"));
		}else if(widget == textedit[0] || widget == textedit[1] || widget == textedit[2]){
			valid=false;
			
			try{
				for(int count=0; count<=2; count++){
					if (!textedit[count].getTitle().equals("") && !textedit[count].getTitle().equals("-")) {
						if(Integer.valueOf(textedit[count].getTitle())<-16 || Integer.valueOf(textedit[count].getTitle())>16){
							errorLabel.setTitle(PC_Lang.tr("pc.gui.blockReplacer.errWrongValue"));
							button[1].enable(false);
							return;
						}else{
							if(Integer.valueOf(textedit[count].getTitle())!=0){
								valid = true;
							}
						}
						
					} else {
						errorLabel.setTitle(PC_Lang.tr("pc.gui.blockReplacer.errWrongValue"));
						button[1].enable(false);
						return;
					}
				}
			}catch(NumberFormatException nfe){
				valid = false;
			}
			
			if(!valid){errorLabel.setTitle(PC_Lang.tr("pc.gui.blockReplacer.err3zeros"));}
			
			if(valid) errorLabel.setTitle("");
			
			button[1].enable(valid);
		}
	}

}
