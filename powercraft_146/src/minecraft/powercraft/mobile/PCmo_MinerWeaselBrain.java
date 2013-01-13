package powercraft.mobile;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import powercraft.management.PC_InvUtils;
import powercraft.management.PC_Utils.GameInfo;
import powercraft.management.PC_Utils.ModuleInfo;
import powercraft.management.PC_VecI;
import powercraft.mobile.PCmo_Command.ParseException;
import powercraft.mobile.PCmo_EntityMiner.Agree;
import powercraft.weasel.PCws_WeaselManager;
import weasel.Calc;
import weasel.IWeaselHardware;
import weasel.WeaselEngine;
import weasel.exception.WeaselRuntimeException;
import weasel.lang.Instruction;
import weasel.obj.WeaselBoolean;
import weasel.obj.WeaselDouble;
import weasel.obj.WeaselObject;
import weasel.obj.WeaselString;


public class PCmo_MinerWeaselBrain  implements PCmo_IMinerBrain, IWeaselHardware {

	private PCmo_EntityMiner miner;
	/** weasel engine */
	private WeaselEngine engine = new WeaselEngine(this);
	private int sleep = 0;
	private String program;
	private String error;
	private static Random rand = new Random();
	
	public PCmo_MinerWeaselBrain(PCmo_EntityMiner miner) {
		this.miner = miner;
	}

	@Override
	public void setProgram(String prog){
		program = prog;
	}
	
	@Override
	public String getProgram(){
		return program;
	}
	
	@Override
	public void restart() {
		error = null;
		engine.restartProgramClearGlobals();
	}

	@Override
	public void launch() throws ParseException {
		restart();
		try {
			List<Instruction> list = WeaselEngine.compileProgram(program);
			engine.insertNewProgram(list);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ParseException(e.getMessage());
		}
	}

	private void setError(String error){
		this.error = error;
	}
	
	@Override
	public boolean hasError() {
		return error != null;
	}

	@Override
	public String getError() {
		return error;
	}

	@Override
	public void run() {
		if (!hasError()) {
			try {

				if (sleep > 0) {
					sleep--;
					return;
				}

				engine.run(100);
			} catch (WeaselRuntimeException wre) {
				wre.printStackTrace();
				setError(wre.getMessage());
			}
		}
	}
	
	@Override
	public boolean doesProvideFunction(String functionName) {
		return getProvidedFunctionNames().contains(functionName);
	}

	@Override
	public List<String> getProvidedFunctionNames() {
		List<String> list = new ArrayList<String>();
		list.add("run");
		list.add("do");
		
		list.add("fw");
		list.add("forward");
		list.add("go");
		
		list.add("bw");
		list.add("back");
		list.add("backward");
		
		list.add("up");
		list.add("down");			
		
		list.add("left");
		list.add("right");
		
		list.add("turn");
		
		list.add("north");
		list.add("south");
		list.add("east");
		list.add("west");
		list.add("xplus");
		list.add("xminus");
		list.add("zplus");
		list.add("zminus");

		list.add("deminer.posit");
		list.add("depo");
		list.add("store");
		list.add("depoKeep");
		list.add("storeKeep");
		list.add("countStacks");
		list.add("stacks");
		list.add("countItems");
		list.add("items");
		list.add("countEmpty");
		list.add("full");
		list.add("isFull");
		list.add("empty");
		list.add("isEmpty");
		list.add("countFuel");
		list.add("fuel");
		
		list.add("destroyMiner");

		list.add("idMatch");
		list.add("ideq");
		list.add("getBlock");
		list.add("setBlock");
		list.add("place");
		list.add("getId");
		list.add("idAt");
		list.add("blockAt");
		list.add("canHarvest");

		list.add("cleanup");
		list.add("burn");
		list.add("destroy");
		list.add("burnItems");
		list.add("destroyItems");
		list.add("burnKeep");
		list.add("destroyKeep");

		list.add("bell");
		list.add("isDay");
		list.add("isNight");
		list.add("isRaining");

		list.add("sleep");

		list.add("global.get");
		list.add("global.set");
		list.add("global.has");

		list.add("cap");
		list.add("opt");
		list.add("miner.cfg");
		list.add("can");
		list.add("hasCap");
		list.add("hasOpt");
		list.add("clearOpt");
		list.add("clearCap");
		list.add("clearminer.cfg");
		list.add("resetOpt");
		list.add("resetCap");
		list.add("resetminer.cfg");
		list.add("capOn");
		list.add("optOn");
		list.add("miner.cfgOn");
		list.add("capOff");
		list.add("optOff");
		list.add("miner.cfgOff");
		return list;
	}
	
	@Override
	public WeaselObject callProvidedFunction(WeaselEngine engine, String functionName, WeaselObject[] args) {
		try {
			if (functionName.equals("bell")) {
				miner.worldObj.playSoundEffect(miner.posX + 1D, miner.posY + 2D, miner.posZ + 1D, "random.orb", 0.8F, (rand.nextFloat() - rand.nextFloat()) * 0.2F + 1.0F);
				miner.worldObj.spawnParticle("note", miner.posX, miner.posY + 1.5D, miner.posZ, (functionName.length() * (3 + args.length)) / 24D, 0.0D, 0.0D);
				return null;
			}

			if (functionName.equals("clearCap") || functionName.equals("clearminer.cfg") || functionName.equals("clearOpt") || functionName.equals("resetCap")
					|| functionName.equals("resetminer.cfg") || functionName.equals("resetOpt")) {
				miner.cfg.airFillingEnabled = false;
				miner.cfg.bridgeEnabled = false;
				miner.cfg.compressBlocks = false;
				miner.cfg.keepAllFuel = false;
				miner.cfg.lavaFillingEnabled = false;
				miner.cfg.miningEnabled = false;
				miner.cfg.torches = false;
				miner.cfg.torchesOnlyOnFloor = false;
				miner.cfg.waterFillingEnabled = false;
				miner.cfg.cobbleMake = false;
				return null;
			}

			if (functionName.equals("sleep")) {
				sleep += Calc.toInteger(args[0].get());
				engine.requestPause();
				return null;
			}

			if (functionName.equals("run")) functionName = "do";



			if (functionName.equals("forward")) functionName = "fw";
			if (functionName.equals("go")) functionName = "fw";

			if (functionName.equals("fw")) {
				int num = 1;
				if (args.length == 1) {
					num = Calc.toInteger(args[0].get());
				}

				// spaces are for safety - when there are two numbers next to each other.
				miner.appendCode(" " + num + " ");
				engine.requestPause();
				return null;
			}

			if (functionName.equals("backward")) functionName = "bw";
			if (functionName.equals("back")) functionName = "bw";

			if (functionName.equals("bw")) {
				int num = 1;
				if (args.length == 1) {
					num = Calc.toInteger(args[0].get());
				}
				num = -num;
				// spaces are for safety - when there are two numbers next to each other.
				miner.appendCode(" " + num + " ");
				engine.requestPause();
				return null;
			}


			if (functionName.equals("left")) {
				int num = 1;
				if (args.length == 1) {
					num = Calc.toInteger(args[0].get());
				}
				boolean R = num < 0;
				if (R) num = -num;
				for (int i = 0; i < num; i++) {
					miner.appendCode(R ? "R" : "L");
				}
				engine.requestPause();
				return null;
			}

			if (functionName.equals("up")) {
				int num = 1;
				if (args.length == 1) {
					num = Calc.toInteger(args[0].get());
				}

				for (int i = 0; i < num; i++) {
					miner.appendCode("U");
				}
				engine.requestPause();
				return null;
			}

			if (functionName.equals("down")) {
				int num = 1;
				if (args.length == 1) {
					num = Calc.toInteger(args[0].get());
				}

				for (int i = 0; i < num; i++) {
					miner.appendCode("D");
				}
				engine.requestPause();
				return null;
			}

			if (functionName.equals("right")) {
				int num = 1;
				if (args.length == 1) {
					num = Calc.toInteger(args[0].get());
				}
				boolean L = num < 0;
				if (L) num = -num;
				for (int i = 0; i < num; i++) {
					miner.appendCode(L ? "L" : "R");
				}
				engine.requestPause();
				return null;
			}

			if (functionName.equals("turn")) {
				do {
					if (args.length > 0 && args[0] instanceof WeaselString) {
						functionName = "do";
						break; //redir to do
					}
					int num = 2;
					if (args.length == 1) {
						num = Calc.toInteger(args[0].get());
					}
					boolean L = num < 0;
					if (L) num = -num;
					for (int i = 0; i < num; i++) {
						miner.appendCode(L ? "L" : "R");
					}
					engine.requestPause();
					return null;
				} while (false);
			}


			if (functionName.equals("do")) {
				int num = 1;
				if (args.length == 2) {
					num = Calc.toInteger(args[1].get());
				}
				for (int i = 0; i < num; i++) {
					miner.appendCode(Calc.toString(args[0].get()));
				}
				engine.requestPause();
				return null;
			}


			if (functionName.equals("xplus")) functionName = "east";
			if (functionName.equals("xminus")) functionName = "west";
			if (functionName.equals("zplus")) functionName = "south";
			if (functionName.equals("zminus")) functionName = "north";

			if (functionName.equals("north")) {
				miner.appendCode("N");
				engine.requestPause();
				return null;
			}
			if (functionName.equals("south")) {
				miner.appendCode("S");
				engine.requestPause();
				return null;
			}
			if (functionName.equals("east")) {
				miner.appendCode("E");
				engine.requestPause();
				return null;
			}
			if (functionName.equals("west")) {
				miner.appendCode("W");
				engine.requestPause();
				return null;
			}
			if (functionName.equals("capOff") || functionName.equals("miner.cfgOff") || functionName.equals("optOff") || functionName.equals("capOn") || functionName.equals("miner.cfgOn")
					|| functionName.equals("optOn") || functionName.equals("miner.cfg") || functionName.equals("opt") || functionName.equals("cap")) {

				int state = -1;
				if (functionName.equals("capOff") || functionName.equals("miner.cfgOff") || functionName.equals("optOff")) state = 0;
				if (functionName.equals("capOn") || functionName.equals("miner.cfgOn") || functionName.equals("optOn")) state = 1;

				for (int i = 0; i < (state == -1 ? 1 : args.length); i++) {

					if (args[i] instanceof WeaselDouble) {
						int cap = Calc.toInteger(args[i].get());
						if (cap == Block.cobblestone.blockID) {
							args[i] = new WeaselString("COBBLE");
						} else if (cap == i) {
							args[i] = new WeaselString("TUNNEL");
						} else if (cap == i) {
							args[i] = new WeaselString("TUNNEL");
						} else if (cap == Block.waterMoving.blockID) {
							args[i] = new WeaselString("WATER");
						} else if (cap == Block.lavaMoving.blockID) {
							args[i] = new WeaselString("LAVA");
						}
					}

					String capname = Calc.toString(args[i].get());
					int argl = args.length;
					
					boolean flag = false;
					if(state == -1) {
						if(argl == 1) {
							flag = false;
						}else {
							flag = Calc.toBoolean(args[1].get());
						}
					}else {
						flag = state > 0;
					}

					if (capname.equals("KEEP_FUEL")) {
						if (argl == 1&&state == -1) return new WeaselBoolean(miner.cfg.keepAllFuel);
						miner.cfg.keepAllFuel = flag;
						continue;
					}
					if (capname.equals("COBBLE")) {
						if (argl == 1&&state == -1) return new WeaselBoolean(miner.cfg.cobbleMake);
						miner.cfg.cobbleMake = flag;
						continue;
					}
					if (capname.equals("TORCHES")) {
						if (argl == 1&&state == -1) return new WeaselBoolean(miner.cfg.torches);
						miner.cfg.torches = flag;
						continue;
					}
					if (capname.equals("TORCH_FLOOR")) {
						if (argl == 1&&state == -1) return new WeaselBoolean(miner.cfg.torchesOnlyOnFloor);
						miner.cfg.torchesOnlyOnFloor = flag;
						continue;
					}
					if (capname.equals("COMPRESS")) {
						if (argl == 1&&state == -1) return new WeaselBoolean(miner.cfg.compressBlocks);
						miner.cfg.compressBlocks = flag;
						continue;
					}
					if (capname.equals("MINING")) {
						if (argl == 1&&state == -1) return new WeaselBoolean(miner.cfg.miningEnabled);
						miner.cfg.miningEnabled = flag;
						continue;
					}
					if (capname.equals("BRIDGE")) {						
						if (argl == 1&&state == -1) return new WeaselBoolean(miner.cfg.bridgeEnabled);
						miner.cfg.bridgeEnabled = flag;
						continue;
					}
					if (capname.equals("LAVA")) {
						if (argl == 1&&state == -1) return new WeaselBoolean(miner.cfg.lavaFillingEnabled);
						miner.cfg.lavaFillingEnabled = flag;
						continue;
					}
					if (capname.equals("WATER")) {
						if (argl == 1&&state == -1) return new WeaselBoolean(miner.cfg.waterFillingEnabled);
						miner.cfg.waterFillingEnabled = flag;
						continue;
					}
					if (capname.equals("TUNNEL")) {
						if (argl == 1&&state == -1) return new WeaselBoolean(miner.cfg.airFillingEnabled);
						miner.cfg.airFillingEnabled = flag;
						continue;
					}
					throw new WeaselRuntimeException(functionName + "(): Unknown option " + capname);
				}

				//what else?
				return null;
			}

			if (functionName.equals("can") || functionName.equalsIgnoreCase("hasOpt") || functionName.equalsIgnoreCase("hasCap")) {
				String capname = (String) args[0].get();

				if (capname.equals("KEEP_FUEL")) {
					return new WeaselBoolean(true);
				}
				if (capname.equals("TORCHES")) {
					return new WeaselBoolean(miner.st.level >= PCmo_EntityMiner.LTORCH);
				}
				if (capname.equals("TORCH_FLOOR")) {
					return new WeaselBoolean(miner.st.level >= PCmo_EntityMiner.LTORCH);
				}
				if (capname.equals("COMPRESS")) {
					return new WeaselBoolean(miner.st.level >= PCmo_EntityMiner.LCOMPRESS);
				}
				if (capname.equals("MINING")) {
					return new WeaselBoolean(true);
				}
				if (capname.equals("BRIDGE")) {
					return new WeaselBoolean(miner.st.level >= PCmo_EntityMiner.LBRIDGE);
				}
				if (capname.equals("LAVA")) {
					return new WeaselBoolean(miner.st.level >= PCmo_EntityMiner.LLAVA);
				}
				if (capname.equals("WATER")) {
					return new WeaselBoolean(miner.st.level >= PCmo_EntityMiner.LWATER);
				}
				if (capname.equals("COBBLE")) {
					return new WeaselBoolean(miner.st.level >= PCmo_EntityMiner.LCOBBLE);
				}
				throw new WeaselRuntimeException(functionName + "(): Unknown option " + capname);
			}

			if (functionName.equals("destroyItems") || functionName.equals("burnItems") || functionName.equals("destroy") || functionName.equals("burn") || functionName.equals("depo")
					|| functionName.equals("deminer.posit") || functionName.equals("store")) {

				boolean kill = functionName.equals("destroyItems") || functionName.equals("burnItems") || functionName.equals("destroy") || functionName.equals("burn");

				if (args.length == 0) {
					miner.cargo.depositToNearbyChest(kill, null);
				} else {
					int num = 0;
					if (args[0] instanceof WeaselDouble) {
						num = Calc.toInteger(args[0]);

						if (args.length == 1) {
							final int id = num;
							// if args length == 1, then this is type, not amount
							miner.cargo.depositToNearbyChest(kill, new Agree() {
								@Override
								public boolean agree(ItemStack stack) {
									return stack.itemID == id;
								}
							});
							return null;
						}

					} else {
						if (args.length == 1) {

							// if args length == 1, then this is type, not amount
							miner.cargo.depositToNearbyChest(kill, new Agree() {
								private WeaselObject obj;
								
								public Agree set(WeaselObject obj) {
									this.obj = obj;
									return this;
								}
								
								@Override
								public boolean agree(ItemStack stack) {
									return miner.matchStackIdentifier(obj, stack);
								}
							}.set(args[0]));

							return null;
						}

						num = -1;
					}

					// num = count, others are types.
					miner.cargo.depositToNearbyChest(kill, new Agree() {
						
						private WeaselObject args[];
						private int n = 0;
						
						public Agree set(int n, WeaselObject args[]) {
							this.n = n;
							this.args = args;
							return this;
						}
						@Override
						public boolean agree(ItemStack stack) {
							if (agree_do(stack)) {
								if (n > 0) n--;
								return true;
							} else
								return false;
						}

						private boolean agree_do(ItemStack stack) {
							if (n > 0 || n == -1) {
								for (int i = 1; i < args.length; i++) {
									WeaselObject arg = args[i];
									if (PCmo_EntityMiner.matchStackIdentifier(arg, stack)) return true;
								}
							}
							return false;
						}

					}.set(num, args));
				}
				engine.requestPause();
				return null;
			}


			if (functionName.equals("destroyKeep") || functionName.equals("burnKeep") || functionName.equals("storeKeep") || functionName.equals("depoKeep")) {
				final boolean kill = functionName.equals("destroyKeep") || functionName.equals("burnKeep");
				if (args.length == 0) {
					throw new WeaselRuntimeException("depoKeep needs at least 1 argument, 0 given.");
				} else {
					int num = 0;
					if (args[0] instanceof WeaselDouble) {
						num = Calc.toInteger(args[0]);
						if (args.length == 1) {
							final int id = num;
							// if args length == 1, then this is type, not amount
							miner.cargo.depositToNearbyChest(kill, new Agree() {
								@Override
								public boolean agree(ItemStack stack) {
									return stack.itemID != id;
								}
							});
							return null;
						}
					} else {

						if (args.length == 1) {

							// if args length == 1, then this is type, not amount
							miner.cargo.depositToNearbyChest(kill, new Agree() {
								private WeaselObject obj;
								
								public Agree set(WeaselObject obj) {
									this.obj = obj;
									return this;
								}
								
								@Override
								public boolean agree(ItemStack stack) {
									return !agree_do(stack);
								}

								private boolean agree_do(ItemStack stack) {
									return PCmo_EntityMiner.matchStackIdentifier(obj, stack);
								}
							}.set(args[0]));

							return null;
						}// end of "len 1 string"

						num = -1;
					}

					// num = count, others are types.
					miner.cargo.depositToNearbyChest(kill, new Agree() {
						
						private WeaselObject args[];
						private int n = 0;
						
						public Agree set(int n, WeaselObject args[]) {
							this.n = n;
							this.args = args;
							return this;
						}

						@Override
						public boolean agree(ItemStack stack) {
							if (agree_do(stack)) {
								if (n == -1 || n > 0) {
									if (n != -1) n--;
									return false;
								} else {
									return true;
								}
							} else
								return true;
						}

						private boolean agree_do(ItemStack stack) {
							if (n > 0 || n == -1) {
								for (int i = 1; i < args.length; i++) {
									WeaselObject arg = args[i];
									if (PCmo_EntityMiner.matchStackIdentifier(arg, stack)) return true;
								}
							}
							return false;
						}

					}.set(num, args));
				}
				engine.requestPause();
				return null;
			}

			if (functionName.equals("items") || functionName.equals("countItems")) {
				int cnt = 0;
				oo:
				for (int i = 0; i < miner.cargo.getSizeInventory(); i++) {
					ItemStack stack = miner.cargo.getStackInSlot(i);
					for (int j = 0; j < args.length; j++) {
						WeaselObject arg = args[j];
						if (stack == null) continue oo;
						if (arg instanceof WeaselDouble) {
							if (stack.itemID == Calc.toInteger(arg)) {
								cnt += stack.stackSize;
								continue oo;
							}
						} else {
							if (PCmo_EntityMiner.matchStackIdentifier(arg, stack)) {
								cnt += stack.stackSize;
								continue oo;
							}
						}
					}
				}

				return new WeaselDouble(cnt);
			}


			if (functionName.equals("stacks") || functionName.equals("countStacks")) {
				int cnt = 0;
				oo:
				for (int i = 0; i < miner.cargo.getSizeInventory(); i++) {
					ItemStack stack = miner.cargo.getStackInSlot(i);
					for (int j = 0; j < args.length; j++) {
						WeaselObject arg = args[j];
						if (stack == null) continue oo;
						if (arg instanceof WeaselDouble) {
							if (stack.itemID == Calc.toInteger(arg)) {
								cnt++;
								continue oo;
							}
						} else {
							if (PCmo_EntityMiner.matchStackIdentifier(arg, stack)) {
								cnt++;
								continue oo;
							}
						}
					}
				}

				return new WeaselDouble(cnt);
			}

			if (functionName.equals("idMatch") || functionName.equals("ideq")) {

				int id1 = Calc.toInteger(args[0].get());

				WeaselObject arg = args[1];

				ItemStack stack = new ItemStack(id1, 1, 0);

				if (stack.itemID == 0) return new WeaselBoolean(arg instanceof WeaselDouble && (Integer) arg.get() == 0);

				if (stack.getItem() == null) throw new WeaselRuntimeException(args[0] + " is not a valid block/item ID.");

				return new WeaselBoolean((PCmo_EntityMiner.matchStackIdentifier(arg, stack)));
			}


			if (functionName.equals("countEmpty")) {
				int cnt = 0;
				for (int i = 0; i < miner.cargo.getSizeInventory(); i++) {
					ItemStack stack = miner.cargo.getStackInSlot(i);
					if (stack == null) cnt++;
				}

				return new WeaselDouble(cnt);
			}

			if (functionName.equals("full") || functionName.equals("isFull")) {
				boolean str = args.length == 1 && Calc.toBoolean(args[0]);
				if (str) return new WeaselBoolean(PC_InvUtils.isInventoryFull(miner.cargo));
				return new WeaselBoolean(PC_InvUtils.hasInventoryNoFreeSlots(miner.cargo));
			}

			if (functionName.equals("empty") || functionName.equals("isEmpty")) {
				return new WeaselBoolean(PC_InvUtils.isInventoryEmpty(miner.cargo));
			}

			if (functionName.equals("destroyMiner")) {
				miner.turnIntoBlocks();
				return null;
			}

			if (functionName.equals("isDay")) {
				return new WeaselBoolean(miner.worldObj.isDaytime());
			}
			if (functionName.equals("idNight")) {
				return new WeaselBoolean(!miner.worldObj.isDaytime());
			}
			if (functionName.equals("isRaining")) {
				return new WeaselBoolean(miner.worldObj.isRaining());
			}

			if (functionName.equals("global.set")) {
				PCws_WeaselManager.setGlobalVariable(Calc.toString(args[0].get()), args[1]);
				return null;
			}
			
			if (functionName.equals("global.has")) {
				return new WeaselBoolean(PCws_WeaselManager.hasGlobalVariable(Calc.toString(args[0].get())));

			} 

			if (functionName.equals("global.get")) {
				return PCws_WeaselManager.getGlobalVariable(Calc.toString(args[0].get()));
			}

			if (functionName.equals("countFuel") || functionName.equals("fuel")) {
				int cnt = 0;
				for (int i = 0; i < miner.cargo.getSizeInventory(); i++) {
					ItemStack stack = miner.cargo.getStackInSlot(i);
					if (stack == null) continue;
					if (stack.itemID != Item.bucketLava.shiftedIndex || !miner.cfg.cobbleMake) {
						cnt += GameInfo.getFuelValue(stack, PCmo_EntityMiner.FUEL_STRENGTH) * stack.stackSize;
					}
				}

				return new WeaselDouble(cnt + miner.st.fuelBuffer);
			}

			if (functionName.equals("canHarvest")) {
				String side = Calc.toString(args[0].get());
				char sid = side.charAt(0);
				String num = side.substring(1);

				PC_VecI pos = miner.getCoordOnSide(sid, Integer.valueOf(num));
				return new WeaselDouble(miner.canHarvestBlockWithCurrentLevel(pos, GameInfo.getBID(miner.worldObj, pos)));
			}

			if (functionName.equals("getBlock") || functionName.equals("getId") || functionName.equals("idAt") || functionName.equals("blockAt")) {
				String side = Calc.toString(args[0].get());
				char sid = side.charAt(0);
				String num = side.substring(1);

				return new WeaselDouble(GameInfo.getBID(miner.worldObj, miner.getCoordOnSide(sid, Integer.valueOf(num))));
			}

			if (functionName.equals("cleanup")) {
				miner.cargo.order();
				return null;
			}

			if (functionName.equals("place") || functionName.equals("setBlock")) {
				String side = Calc.toString(args[0].get());
				char sid = side.charAt(0);
				String num = side.substring(1);

				Object id = args[1].get();
				String str = "";

				int numid = -1;

				if (id instanceof Integer) {
					numid = Calc.toInteger(id);
				}

				if (id instanceof String) {
					numid = -2;
					str = Calc.toString(id);
				}

				if (numid == -1) throw new WeaselRuntimeException(id + " is not a valid block id or group.");

				PC_VecI pos = miner.getCoordOnSide(sid, Integer.valueOf(num));
				if (pos == null) {
					throw new WeaselRuntimeException(functionName + "(): " + side + " is not a valid side [FBLRUD][1234] or [ud][12].");
				}

				if (str.equals("BUILDING_BLOCK") || str.equals("BLOCK")) {
					ItemStack placed = miner.cargo.getBlockForBuilding();
					if (placed == null) {
						return new WeaselBoolean(false);
					} else {
						placed.onBlockDestroyed(miner.worldObj, pos.x, pos.y + 1, pos.z, 0, miner.fakePlayer);
						return new WeaselBoolean(true);
						/** TODO right??
						if (!placed.useItem(fakePlayer, miner.worldObj, miner.pos.x, miner.pos.y + 1, miner.pos.z, 0)) {
							PC_InvUtils.addItemStackToInventory(miner.cargo, placed);
						} else {
							return new WeaselBoolean(true);
						}*/
					}
				}

				if (numid != -2) {
					for (int i = 0; i < miner.cargo.getSizeInventory(); i++) {
						ItemStack stack = miner.cargo.getStackInSlot(i);
						if (stack == null) continue;

						if (stack.itemID == numid) {
							ItemStack placed = miner.cargo.decrStackSize(i, 1);
							placed.onBlockDestroyed(miner.worldObj, pos.x, pos.y + 1, pos.z, 0, miner.fakePlayer);
							return new WeaselBoolean(true);
							/** TODO right??
							if (!placed.useItem(fakePlayer, miner.worldObj, miner.pos.x, miner.pos.y + 1, miner.pos.z, 0)) {
								PC_InvUtils.addItemStackToInventory(miner.cargo, placed);
							} else {
								return new WeaselBoolean(true);
							}*/
						}
					}
					
					if(numid == Block.cobblestone.blockID && miner.canMakeCobble()) {
						(new ItemStack(Block.cobblestone)).onBlockDestroyed(miner.worldObj, pos.x, pos.y + 1, pos.z, 0, miner.fakePlayer);
						return new WeaselBoolean(true);
						/** TODO right??
						return new WeaselBoolean((new ItemStack(Block.cobblestone)).useItem(fakePlayer, miner.worldObj, miner.pos.x, miner.pos.y + 1, miner.pos.z, 0));							
						*/
					}
						
				}

				return new WeaselBoolean(false);
			}


		} catch (ParseException e) {
			e.printStackTrace();
			throw new WeaselRuntimeException(e.getMessage());
		}

		throw new WeaselRuntimeException(functionName + " not implemented or not ended properly.");
	}

	@Override
	public List<String> getProvidedVariableNames() {
		List<String> list = new ArrayList<String>(0);
		list.add("miner.pos.x");
		list.add("miner.pos.y");
		list.add("miner.pos.z");
		list.add("dir");
		list.add("dir.axis");
		list.add("dir.compass");
		list.add("dir.angle");
		list.add("axis");
		list.add("angle");
		list.add("compass");
		list.add("level");
		return list;
	}
	
	@Override
	public WeaselObject getVariable(String name) {
		if (name.equals("miner.pos.x")) {
			return new WeaselDouble(Math.round(miner.posX));
		}
		if (name.equals("level")) {
			miner.updateLevel();
			return new WeaselDouble(miner.st.level);
		}
		if (name.equals("miner.pos.y")) {
			return new WeaselDouble(Math.round(miner.posY) + (miner.isOnHalfStep() ? 1 : 0));
		}
		if (name.equals("miner.pos.z")) {
			return new WeaselDouble(Math.round(miner.posZ));
		}
		if (name.equals("angle") || name.equals("dir.angle")) {
			int rot = miner.getRotationRounded();
			return new WeaselString(rot);
		}
		if (name.equals("dir") || name.equals("dir.axis") || name.equals("axis")) {
			int rot = miner.getRotationRounded();
			return new WeaselString(rot == 0 ? "x-" : rot == 90 ? "z-" : rot == 180 ? "x+" : "z+");
		}
		if (name.equals("dir.compass") || name.equals("compass")) {
			int rot = miner.getRotationRounded();
			return new WeaselString(rot == 0 ? "W" : rot == 90 ? "N" : rot == 180 ? "E" : "S");
		}

		return null;
	}

	@Override
	public void setVariable(String name, Object object) {
	}

	@Override
	public PCmo_MinerBrain readFromNBT(NBTTagCompound nbttag) {
		return null;
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbttag) {
		return null;
	}
	
}
