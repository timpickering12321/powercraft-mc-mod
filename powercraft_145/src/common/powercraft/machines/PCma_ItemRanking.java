package powercraft.machines;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.TreeMap;

import net.minecraft.src.Block;
import net.minecraft.src.IRecipe;
import net.minecraft.src.Item;
import net.minecraft.src.ItemBlock;
import net.minecraft.src.ItemStack;
import net.minecraft.src.ShapedRecipes;
import net.minecraft.src.ShapelessRecipes;
import powercraft.management.PC_IItemInfo;
import powercraft.management.PC_IRecipeInputInfo;
import powercraft.management.PC_ItemStack;
import powercraft.management.PC_Property;
import powercraft.management.PC_Struct2;
import powercraft.management.PC_Utils.GameInfo;
import powercraft.management.PC_Utils.ValueWriting;

public class PCma_ItemRanking {

	private static TreeMap<PC_ItemStack, PC_Struct2<Float, Integer>> ranking = new TreeMap<PC_ItemStack, PC_Struct2<Float, Integer>>();
	private static List<PC_ItemStack> allreadyDone = new ArrayList<PC_ItemStack>();
	private static boolean hasInit = false;
	
	private static void setRank(PC_ItemStack itemstack, int rank){
		PC_Struct2<Float, Integer> s;
		if(ranking.containsKey(itemstack)){
			s = ranking.get(itemstack);
		}else{
			ranking.put(itemstack, s = new PC_Struct2<Float, Integer>(0.0f, 0));
		}
		s.a *= s.b;
		s.a += rank;
		s.b++;
		s.a /= s.b;
	}
	
	public static float getRank(PC_ItemStack itemstack){
		if(ranking.containsKey(itemstack)){
			return ranking.get(itemstack).a;
		}
		return 0;
	}
	
	private static boolean makeTree(boolean print){
		boolean anyDone = false;
		for(Item item: Item.itemsList){
			if(item!=null){
				List<ItemStack> l = null;
				if(item instanceof PC_IItemInfo){
					l = ((PC_IItemInfo)item).getItemStacks(new ArrayList<ItemStack>());
				}else if (item instanceof ItemBlock) {
                    int id = ((ItemBlock)item).getBlockID();
                    Block b = Block.blocksList[id];
                    if (b != null){
	                    if (b instanceof PC_IItemInfo){
	                        l = ((PC_IItemInfo)b).getItemStacks(new ArrayList<ItemStack>());
	                    }else{
	                    	if (item.getHasSubtypes()){
	                        	l = new ArrayList<ItemStack>();
	                        	boolean bo = false;
	                            for (int j = 0; true; j++){
	                                ItemStack is = new ItemStack(b, 1, j);
	                                if (GameInfo.getRecipesForProduct(is).size() > 0 || GameInfo.getFeedstock(is).size() > 0) {
	                                	l.add(is);
	                                	bo = false;
	                                }else{
	                                	if(bo)
	                                		break;
	                                	bo = true;
	                                }
	                            }
	                        }else{
	                        	l = new ArrayList<ItemStack>();
	                        	l.add(new ItemStack(b));
	                        }
	                    }
                    }
                }else if (item.getHasSubtypes()){
                	l = new ArrayList<ItemStack>();
                	boolean b = false;
                    for (int j = 0; true; j++){
                        ItemStack is = new ItemStack(item, 1, j);
                        if (GameInfo.getRecipesForProduct(is).size() > 0 || GameInfo.getFeedstock(is).size() > 0) {
                        	l.add(is);
                        	b = false;
                        }else{
                        	if(b)
                        		break;
                        	b = true;
                        }
                    }
                }else{
                	l = new ArrayList<ItemStack>();
                	l.add(new ItemStack(item));
                }
				if(l!=null){
					for(ItemStack is:l){
						PC_ItemStack pcis = new PC_ItemStack(is);
						if(allreadyDone.contains(pcis))
							continue;
						List<IRecipe> recipes = GameInfo.getRecipesForProduct(is);
						List<ItemStack> feedstocks = GameInfo.getFeedstock(is);
						for(ItemStack itemstack:feedstocks){
							PC_ItemStack pcisi = new PC_ItemStack(itemstack);
							PC_Struct2<Float, Integer> s =ranking.get(pcisi);
							if(s != null){
								setRank(pcis, (int)(s.a+100));
								anyDone = true;
								if(!allreadyDone.contains(pcis))
									allreadyDone.add(pcis);
							}
						}
						for(IRecipe recipe:recipes){
							ItemStack[] input = GameInfo.getExpectedInput(recipe);
							if(input==null){
				                continue;
				            }
							float rank=0;
							for(ItemStack itemstack:input){
								if(itemstack==null)
									continue;
								PC_ItemStack pcisi = new PC_ItemStack(itemstack);
								PC_Struct2<Float, Integer> s =ranking.get(pcisi);
								if(s != null){
									rank += s.a;
								}else{
									rank = -1;
									break;
								}
							}
							if(rank>0){
								rank /= recipe.getRecipeOutput().stackSize;
								setRank(pcis, (int)(rank+2.5));
								anyDone = true;
								if(!allreadyDone.contains(pcis))
									allreadyDone.add(pcis);
							}
						}
					}
				}
			}
		}
		return anyDone;
	}
	
	private static Item getItem(String key){
		for(Item i:Item.itemsList){
			if(i!=null && key.equals(i.getItemName()))
				return i;
		}
		return null;
	}
	
	private static void load(String key, PC_Property prop){
		if(prop.hasChildren()){
			if(!key.equals("")){
				key += ".";
			}
			HashMap<String, PC_Property> porps = prop.getPropertys();
			for(Entry<String, PC_Property> e:porps.entrySet()){
				load(key + e.getKey(), e.getValue());
			}
		}else{
			Item item;
			if(key.startsWith("id.")){
				item = Item.itemsList[Integer.parseInt(key.substring(3))];
			}else{
				item = getItem(key);
			}
			List<Integer> l = GameInfo.parseIntList(prop.getString());
			int num=0;
			for(Integer i:l){
				if(i>0)
					setRank(new PC_ItemStack(item, 1, num), i);
				num++;
			}
		}
	}
	
	private static void reg(PC_Property prop, Item item, String nums){
		prop.getString("id."+item.shiftedIndex, nums, item.getItemName());
	}
	
	private static void reg(PC_Property prop, Block block, String nums){
		prop.getString("id."+block.blockID, nums, Item.itemsList[block.blockID].getItemName());
	}
	
	public static void init(){
		if(hasInit)
			return;
		
		File file = new File(GameInfo.getPowerCraftFile(), "/itemRanks.cfg");
		
		PC_Property prop;
		
		try {
			prop = PC_Property.loadFromFile(new FileInputStream(file));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			prop = new PC_Property();
		}
		
		reg(prop, Block.grass, "1");
		reg(prop, Block.dirt, "1");
		reg(prop, Block.cobblestone, "1");
		reg(prop, Block.stoneBrick, "1");
		reg(prop, Block.sand, "1");
		reg(prop, Block.gravel, "1");
		reg(prop, Block.oreGold, "1000");
		reg(prop, Block.oreIron, "500");
		reg(prop, Block.oreCoal, "250");
		reg(prop, Block.sponge, "1");
		reg(prop, Block.oreLapis, "500");
		reg(prop, Block.web, "1");
		reg(prop, Block.cobblestoneMossy, "500");
		reg(prop, Block.obsidian, "2000");
		reg(prop, Block.fire, "1");
		reg(prop, Block.mobSpawner, "4000");
		reg(prop, Block.oreDiamond, "2000");
		reg(prop, Block.crops, "1");
		reg(prop, Block.oreRedstone, "250");
		reg(prop, Block.snow, "1");
		reg(prop, Block.ice, "1");
		reg(prop, Block.cactus, "20");
		reg(prop, Block.pumpkin, "100");
		reg(prop, Block.netherrack, "1");
		reg(prop, Block.slowSand, "10");
		reg(prop, Block.vine, "1");
		reg(prop, Block.mycelium, "1");
		reg(prop, Block.waterlily, "1000");
		reg(prop, Block.netherBrick, "10");
		reg(prop, Block.netherStalk, "10");
		reg(prop, Block.cauldron, "1");
		reg(prop, Block.whiteStone, "100");
		reg(prop, Block.dragonEgg, "100000");
		reg(prop, Block.oreEmerald, "4000");
		reg(prop, Block.tripWire, "100");
		reg(prop, Block.commandBlock, "1000");
		reg(prop, Block.flowerPot, "10");
		reg(prop, Block.skull, "1");
		reg(prop, Item.appleRed, "10");
		reg(prop, Item.silk, "10");
		reg(prop, Item.feather, "1");
		reg(prop, Item.gunpowder, "100");
		reg(prop, Item.seeds, "10");
		reg(prop, Item.wheat, "10");
		reg(prop, Item.flint, "10");
		reg(prop, Item.porkRaw, "1");
		reg(prop, Item.bucketWater, "1000");
		reg(prop, Item.bucketLava, "10000");
		reg(prop, Item.saddle, "1000");
		reg(prop, Item.snowball, "10");
		reg(prop, Item.leather, "1");
		reg(prop, Item.bucketMilk, "1000");
		reg(prop, Item.clay, "1");
		reg(prop, Item.reed, "1");
		reg(prop, Item.slimeBall, "100");
		reg(prop, Item.egg, "10");
		reg(prop, Item.lightStoneDust, "100");
		reg(prop, Item.fishRaw, "100");
		reg(prop, Item.bone, "10");
		reg(prop, Item.melon, "10");
		reg(prop, Item.beefRaw, "10");
		reg(prop, Item.chickenRaw, "10");
		reg(prop, Item.rottenFlesh, "1");
		reg(prop, Item.enderPearl, "1000");
		reg(prop, Item.blazeRod, "10000");
		reg(prop, Item.ghastTear, "5000");
		reg(prop, Item.netherStalkSeeds, "1");
		reg(prop, Item.spiderEye, "10");
		reg(prop, Item.expBottle, "1000");
		reg(prop, Item.writtenBook, "1000");
		reg(prop, Item.carrot, "10");
		reg(prop, Item.potato, "10");
		reg(prop, Item.poisonousPotato, "1");
		reg(prop, Item.netherStar, "100000");
		reg(prop, Item.record13, "1");
		reg(prop, Item.recordCat, "1");
		reg(prop, Item.recordBlocks, "1");
		reg(prop, Item.recordChirp, "1");
		reg(prop, Item.recordFar, "1");
		reg(prop, Item.recordMall, "1");
		reg(prop, Item.recordMellohi, "1");
		reg(prop, Item.recordStal, "1");
		reg(prop, Item.recordStrad, "1");
		reg(prop, Item.recordWard, "1");
		reg(prop, Item.record11, "1");
		reg(prop, Item.field_85180_cf, "1");
		
		load("", prop);
		
		while(makeTree(false));
		
		makeTree(true);
		
		for(Entry<PC_ItemStack, PC_Struct2<Float, Integer>> e : ranking.entrySet()){
			System.out.println(e.getKey().toString() + ", ranked:"+e.getValue().a);
		}
		
		try {
			prop.save(new FileOutputStream(file));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		hasInit = true;
	}
	
}
