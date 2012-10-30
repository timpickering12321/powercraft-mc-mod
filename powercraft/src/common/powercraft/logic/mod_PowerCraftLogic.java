package powercraft.logic;

import java.util.List;

import net.minecraft.src.Block;
import net.minecraft.src.Item;
import net.minecraft.src.ItemStack;
import net.minecraftforge.common.Configuration;
import powercraft.core.PC_Block;
import powercraft.core.PC_Module;
import powercraft.core.PC_Utils;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.Init;
import cpw.mods.fml.common.Mod.PostInit;
import cpw.mods.fml.common.Mod.PreInit;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;

@Mod(modid="PowerCraft-Logic", name="PowerCraft-Logic", version="0.0.1Alpha", dependencies="required-after:PowerCraft-Core")
@NetworkMod(clientSideRequired=true, serverSideRequired=true)
public class mod_PowerCraftLogic extends PC_Module {

	@SidedProxy(clientSide = "powercraft.logic.PClo_ClientProxy", serverSide = "powercraft.logic.PClo_CommonProxy")
	public static PClo_CommonProxy proxy;
	
	public static PC_Block pulsar;
	public static PC_Block gate;
	public static PC_Block flipFlop;
	public static PC_Block delayer;
	
	public static mod_PowerCraftLogic getInstance(){
		return (mod_PowerCraftLogic)PC_Module.getModule("PowerCraft-Logic");
	}
	
	@PreInit
	public void preInit(FMLPreInitializationEvent event){
		
		preInit(event, proxy);
		
	}
	
	@Init
	public void init(FMLInitializationEvent event){
		
		init();
		
	}

	@PostInit
	public void postInit(FMLPostInitializationEvent event){
		
		postInit();
		
	}
	
	@Override
	protected void initProperties(Configuration config) {
		
	}

	@Override
	protected List<String> loadTextureFiles(List<String> textures) {
		textures.add(getTerrainFile());
		return textures;
	}

	@Override
	protected void initLanguage() {
		PC_Utils.registerLanguage(this,
				"pc.pulsar.clickMsg", "Period %s ticks (%s s)",
				"pc.pulsar.clickMsgTime", "Period %s ticks (%s s), remains %s",
				"pc.gui.pulsar.silent", "Silent",
				"pc.gui.pulsar.paused", "Pause",
				"pc.gui.pulsar.delay", "Delay (sec)",
				"pc.gui.pulsar.hold", "Hold time (sec)",
				"pc.gui.pulsar.ticks", "ticks",
				"pc.gui.pulsar.errDelay", "Bad delay time!",
				"pc.gui.pulsar.errHold", "Bad hold time!",
				"pc.gate.not.desc", "negates input",
				"pc.gate.and.desc", "both inputs on",
				"pc.gate.nand.desc", "some inputs off",
				"pc.gate.or.desc", "at least one input on",
				"pc.gate.nor.desc", "all inputs off",
				"pc.gate.xor.desc", "inputs different",
				"pc.gate.xnor.desc", "inputs equal",
				"pc.gate.xnor3.desc", "all inputs equal",
				"pc.gate.and3.desc", "all inputs on",
				"pc.gate.nand3.desc", "some inputs off",
				"pc.gate.or3.desc", "at least one input on",
				"pc.gate.nor3.desc", "all inputs off",
				"pc.gate.xor3.desc", "inputs different",
				"pc.flipflop.D.desc", "latch memory",
				"pc.flipflop.RS.desc", "set/reset memory",
				"pc.flipflop.T.desc", "divides signal by 2",
				"pc.flipflop.random.desc", "changes state randomly on pulse",
				"pc.gate.buffer.desc", "slows down signal",
				"pc.gate.slowRepeater.desc", "makes pulses longer"
		);
	}

	@Override
	protected void initBlocks() {
		pulsar = (PC_Block)PC_Utils.register(this, 461, PClo_BlockPulsar.class, PClo_TileEntityPulsar.class);
		gate = (PC_Block)PC_Utils.register(this, 462, PClo_BlockGate.class, PClo_ItemBlockGate.class, PClo_TileEntityGate.class);
		flipFlop = (PC_Block)PC_Utils.register(this, 463, PClo_BlockFlipFlop.class, PClo_ItemBlockFlipFlop.class, PClo_TileEntityFlipFlop.class);
		delayer = (PC_Block)PC_Utils.register(this, 464, PClo_BlockDelayer.class, PClo_ItemBlockDelayer.class, PClo_TileEntityDelayer.class);
	}

	@Override
	protected void initItems() {

	}
	
	@Override
	protected void initRecipes() {
		// *** pulsar ***
		
		PC_Utils.addRecipe(new ItemStack(pulsar, 1, 0),
				new Object[] { " r ", "ror", " r ",
				'r', Item.redstone, 'o', Block.obsidian });
	}

	@Override
	protected List<String> addSplashes(List<String> list) {
		return list;
	}

}
