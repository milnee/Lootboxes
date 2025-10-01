package me.millen.lootboxes.command;
/*
 *  created by Turben on 01/06/2020
 */

import me.millen.lootboxes.Base;
import me.millen.lootboxes.framework.Box;
import me.millen.lootboxes.injection.Injector;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.beans.beancontext.BeanContextServiceAvailableEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BoxCommand implements CommandExecutor{

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args){
		if(args.length < 1){
			sendUsage(sender);
		}else{
			if(args[0].equalsIgnoreCase("give")){
				if(sender.hasPermission("lootboxes.give")){
					if(args.length < 4){
						sender.sendMessage("Usage: /box give (player) (amount) (box name)");
					}else{
						StringBuilder builder = new StringBuilder();
						for(int index = 3; index < args.length; index++){
							builder.append(args[index]).append(" ");
						}
						String boxName = builder.toString().substring(0, builder.toString().length() -1);
						Box box = Base.get().getBoxManager().getBox(boxName);
						Player target = Bukkit.getPlayer(args[1]);
						if(target == null){
							sender.sendMessage(ChatColor.RED +"Unknown player '" +args[1] +"'.");
						}else if(box == null){
							sender.sendMessage(ChatColor.RED +"Unknown box '" +boxName +"'.");
						}else if(!isDouble(args[2])){
							sender.sendMessage(ChatColor.RED +"You must specify amount in numbers.");
						}else{
							ItemStack give = box.getStack().clone();
							give.setAmount(Integer.parseInt(args[2]));
							target.getInventory().addItem(give);
							sender.sendMessage(ChatColor.GRAY +"Gave " +ChatColor.GREEN +target.getName() +" x" +ChatColor.DARK_GREEN +args[2] +ChatColor.GRAY +" of " +ChatColor.WHITE +box.getLabel() +ChatColor.GRAY +".");
						}
					}
				}else{
					sender.sendMessage(Base.get().getCache().PERMISSION_DENIED());
				}
			}else if(args[0].equalsIgnoreCase("giveall")){
				if(sender.hasPermission("lootboxes.giveall")){
					if(args.length < 3){
						sender.sendMessage("Usage: /box giveall (amount) (box name)");
					}else{
						StringBuilder builder = new StringBuilder();
						for(int index = 2; index < args.length; index++){
							builder.append(args[index]).append(" ");
						}
						String boxName = builder.toString().substring(0, builder.toString().length() -1);
						Box box = Base.get().getBoxManager().getBox(boxName);
						if(box == null){
							sender.sendMessage(ChatColor.RED +"Unknown box '" +boxName +"'.");
						}else if(!isDouble(args[1])){
							sender.sendMessage(ChatColor.RED +"You must specify amount in numbers.");
						}else{
							ItemStack give = box.getStack().clone();
							give.setAmount(Integer.parseInt(args[1]));
							sender.sendMessage(ChatColor.GRAY +"Gave " +ChatColor.GREEN +"everyone " +ChatColor.DARK_GREEN  +"x" +args[1] +ChatColor.GRAY +" of " +ChatColor.WHITE +box.getLabel() +ChatColor.GRAY +".");
							Bukkit.getOnlinePlayers().forEach(player -> player.getInventory().addItem(give));
						}
					}
				}else{
					sender.sendMessage(Base.get().getCache().PERMISSION_DENIED());
				}
			}else if(args[0].equalsIgnoreCase("reload")){
				if(sender.hasPermission("lootboxes.reload")){
					Base.get().reloadCache();
					sender.sendMessage(ChatColor.GREEN +"Reloaded the configuration file.");
				}else{
					sender.sendMessage(Base.get().getCache().PERMISSION_DENIED());
				}
			}else{
				if(sender instanceof Player){
					Player player = (Player) sender;

					if(args[0].equalsIgnoreCase("create")){
						if(player.hasPermission("lootboxes.create")){
							if(args.length < 2){
								player.sendMessage(ChatColor.GRAY +"Usage: /box create (name).");
							}else{
								if(!Base.get().getBoxManager().exists(args[1])){
									if(player.getItemInHand() == null || player.getItemInHand().getType().equals(Material.AIR)){
										player.sendMessage(ChatColor.RED +"You must have an item in hand.");
									}else{
										ItemStack stack = new Injector(player.getItemInHand()).setBox(args[1]).getStack();
										Box box = new Box(args[1], stack);
										player.sendMessage(ChatColor.GREEN +"Created the box " +box.getLabel() +". Set loot table with /box loot (name).");
										player.setItemInHand(stack);
									}
								}else{
									player.sendMessage(ChatColor.GRAY +"The box '" +args[1] +"' already exists.");
								}
							}
						}else{
							player.sendMessage(Base.get().getCache().PERMISSION_DENIED());
						}
					}else if(args[0].equalsIgnoreCase("remove")){
						if(player.hasPermission("lootboxes.remove")){
							if(args.length < 2){
								player.sendMessage(ChatColor.GRAY +"Usage: /box remove (name).");
							}else{
								if(Base.get().getBoxManager().exists(args[1])){
									Base.get().getBoxManager().remove(args[1]);
									player.sendMessage(ChatColor.GREEN +"Removed the box " +args[1] +".");
								}else{
									player.sendMessage(ChatColor.RED +"The box '" +args[1] +"' does not exist.");
								}
							}
						}else{
							player.sendMessage(Base.get().getCache().PERMISSION_DENIED());
						}
					}else if(args[0].equalsIgnoreCase("list")){
						if(player.hasPermission("lootboxes.list"))
							player.openInventory(Base.get().getBoxManager().getNewInventory(player));
						else
							player.sendMessage(Base.get().getCache().PERMISSION_DENIED());
					}else if(args[0].equalsIgnoreCase("loot")){
						if(player.hasPermission("lootboxes.loot")){
							if(args.length < 2){
								player.sendMessage(ChatColor.GRAY +"Usage: /box loot (name).");
							}else{
								if(Base.get().getBoxManager().exists(args[1])){
									Box box = Base.get().getBoxManager().getBox(args[1]);
									player.openInventory(box.getLoot());
								}else{
									player.sendMessage(ChatColor.RED +"The box '" +args[1] +"' does not exist.");
								}
							}
						}else{
							player.sendMessage(Base.get().getCache().PERMISSION_DENIED());
						}
					}else if(args[0].equalsIgnoreCase("item")){
						if(player.hasPermission("lootboxes.item")){
							if(player.getItemInHand() == null || player.getItemInHand().getType().equals(Material.AIR)){
								player.sendMessage(ChatColor.RED + "You must have an item in hand.");
							}else{
								if(args.length == 1){
									Map<String, String> keys = Injector.getKeys(player.getItemInHand());
									if(keys == null || keys.isEmpty())
										return false;
									for(String string : keys.keySet())
										if(!string.contains("uuid"))
											player.sendMessage(ChatColor.GRAY + string + ": " + ChatColor.WHITE + keys.get(string));
								}else{
									if(args[1].equalsIgnoreCase("chance")){
										if(args.length == 3){
											if(isDouble(args[2])){
												double percent = Double.parseDouble(args[2]);
												if(percent <= 0 || percent > 100){
													player.sendMessage(ChatColor.RED +"The percentage should be within 0.X% and 100%");
												}else{
													ItemStack stack = new Injector(player.getItemInHand()).setChances(Double.parseDouble(args[2])).getStack();

													player.setItemInHand(stack);
													player.sendMessage(ChatColor.GRAY +"Set item chance to " +ChatColor.GOLD +args[2] +"%" + ChatColor.GRAY +".");
												}
											}else{
												player.sendMessage(ChatColor.RED +"You must specify chance in numbers. For example: 13");
											}
										}else{
											player.sendMessage(ChatColor.GRAY +"Usage: /box item chance (percentage).");
										}
									}else if(args[1].equalsIgnoreCase("flag")){
										if(args.length < 3){
											player.sendMessage(ChatColor.GRAY +"Usage: /box item flag (display).");
										}else{
											if(args[2].equalsIgnoreCase("display")){
												Injector injector = new Injector(player.getItemInHand());

												if(injector.hasKey("flag"))
													injector.removeKey("flag");
												else
													injector.set("flag", "display");

												player.sendMessage(ChatColor.GRAY +(injector.hasKey("flag") ? "Added" : "Removed") +" the flag " +ChatColor.WHITE +"display" + ChatColor.GRAY +".");
												player.setItemInHand(injector.getStack());
											}else{
												player.sendMessage(ChatColor.GRAY +"Available flags: " +ChatColor.BOLD +"display.");
											}
										}
									}else if(args[1].equalsIgnoreCase("command")){
										if(args.length < 4 ||!isDouble(args[2])){
											player.sendMessage(ChatColor.GRAY +"Usage: /box item command (times-executed) command..");
											return false;
										}
										StringBuilder cmd = new StringBuilder();
										for(int index = 3; index < args.length; index++){
											cmd.append(args[index]).append(" ");
										}

										Injector injector = new Injector(player.getItemInHand());

										injector.setTimes(Integer.parseInt(args[2]));
										injector.setCommand(cmd.toString());
										player.sendMessage(ChatColor.GRAY +"Set item command to: " +ChatColor.WHITE +cmd.toString());
										player.setItemInHand(injector.getStack());
									}else if(args[1].equalsIgnoreCase("rename")){
										if(args.length < 3){
											player.sendMessage(ChatColor.GRAY +"Usage: /box item rename (name)");
										}else{
											StringBuilder builder = new StringBuilder();
											for(int index = 2; index < args.length; index++){
												builder.append(args[index]).append(" ");
											}

											String name = builder.toString().substring(0, builder.toString().length() -1);

											ItemMeta meta = player.getItemInHand().getItemMeta();
											meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
											ItemStack hand = player.getItemInHand();
											hand.setItemMeta(meta);
											player.setItemInHand(hand);
										}
									}else if(args[1].equalsIgnoreCase("lore")){
										if(args.length < 3){
											player.sendMessage(ChatColor.GRAY +"Usage: /box item lore (add/remove)");
										}else{
											if(args[2].equalsIgnoreCase("add")){
												if(args.length < 4){
													player.sendMessage(ChatColor.GRAY +"Usage: /box item lore add (lore..)");
												}else{
													StringBuilder builder = new StringBuilder();
													for(int index = 3; index < args.length; index++){
														builder.append(args[index]).append(" ");
													}

													String lore = builder.toString().substring(0, builder.toString().length() -1);
													ItemMeta meta = player.getItemInHand().getItemMeta();
													List<String> lores = meta.hasLore() ? meta.getLore() : new ArrayList<>();
													lores.add(ChatColor.translateAlternateColorCodes('&', lore));
													meta.setLore(lores);
													ItemStack hand = player.getItemInHand();
													hand.setItemMeta(meta);
													player.setItemInHand(hand);
												}
											}else if(args[2].equalsIgnoreCase("remove")){
												if(args.length < 4){
													player.sendMessage(ChatColor.GRAY +"Usage: /box item lore remove (line number)");
												}else{
													if(isDouble(args[3])){
														ItemStack hand = player.getItemInHand();
														ItemMeta meta = hand.getItemMeta();

														int integer = Integer.parseInt(args[3]);
														if(!meta.hasLore() || meta.getLore().size() < integer){
															player.sendMessage(ChatColor.RED +"The line is unreachable.");
														}else{
															List<String> modified = new ArrayList<>();
															for(int index = 0; index < meta.getLore().size(); index++){
																if(index == (integer -1))
																	continue;

																modified.add(meta.getLore().get(index));
															}

															meta.setLore(modified);
															hand.setItemMeta(meta);
															player.setItemInHand(hand);
														}
													}else{
														player.sendMessage(ChatColor.RED +"Line number must be a number..");
													}
												}
											}else{
												player.sendMessage(ChatColor.GRAY +"Usage: /box item lore (add/remove)");
											}
										}
									}
								}
							}
						}else{
							player.sendMessage(Base.get().getCache().PERMISSION_DENIED());
						}
					}
				}
			}
		}
		return false;
	}

	public void sendUsage(CommandSender sender){
		sender.sendMessage(ChatColor.GRAY +"" +ChatColor.STRIKETHROUGH +"------------------------------------------------------");
		if(sender.hasPermission("lootboxes.create"))
			sender.sendMessage(ChatColor.GRAY +"/box create (name) - Create a box");
		if(sender.hasPermission("lootboxes.remove"))
			sender.sendMessage(ChatColor.GRAY +"/box remove (name) - Remove a box");
		if(sender.hasPermission("lootboxes.list"))
			sender.sendMessage(ChatColor.GRAY +"/box list - List of boxes");
		if(sender.hasPermission("lootboxes.give"))
			sender.sendMessage(ChatColor.GRAY +"/box give (player) (amount) (box name) - Give boxes");
		if(sender.hasPermission("lootboxes.giveall"))
			sender.sendMessage(ChatColor.GRAY +"/box giveall (amount) (box name) - Give boxes");
		if(sender.hasPermission("lootboxes.loot"))
			sender.sendMessage(ChatColor.GRAY +"/box loot (name) - Show the box loot");
		if(sender.hasPermission("lootboxes.item"))
			sender.sendMessage(ChatColor.GRAY +"/box item (chance/command/flag/rename/lore) - Modify an item");
		if(sender.hasPermission("lootboxes.reload"))
			sender.sendMessage(ChatColor.GRAY +"/box reload - Reload configuration");
		sender.sendMessage(ChatColor.GRAY +"" +ChatColor.STRIKETHROUGH +"------------------------------------------------------");
	}

	public boolean isDouble(String string){
		try{
			Double.parseDouble(string);
		}catch(NumberFormatException ex){
			return false;
		}

		return true;
	}
}
