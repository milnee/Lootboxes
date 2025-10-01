package me.millen.lootboxes.management.managers;
/*
 *  created by Turben on 01/06/2020
 */

import com.google.common.collect.Maps;
import me.millen.lootboxes.Base;
import me.millen.lootboxes.events.CollectRewardEvent;
import me.millen.lootboxes.framework.Box;
import me.millen.lootboxes.framework.Request;
import me.millen.lootboxes.injection.Injector;
import me.millen.lootboxes.management.Manager;
import me.millen.lootboxes.utils.general.RandomCollection;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicReference;

public class BoxManager implements Manager, Listener{

	private Map<UUID, Request> opening;
	private Set<Box> boxes;

	public void setup(){
		opening = Maps.newConcurrentMap();
		boxes = new CopyOnWriteArraySet<>();
	}

	public void setOpening(UUID uuid, Request request){
		opening.put(uuid, request);
	}

	public void removeOpening(UUID uuid){
		opening.remove(uuid);
	}

	public Request getCurrent(UUID uuid){
		return opening.get(uuid);
	}

	public boolean isOpening(UUID uuid){
		return opening.containsKey(uuid);
	}

	public void add(Box box){
		boxes.add(box);
	}

	public void remove(String label){
		boxes.removeIf(box -> box.getLabel().equalsIgnoreCase(label));
	}

	public boolean exists(String label){
		return boxes.stream().anyMatch(box -> box.getLabel().equalsIgnoreCase(label));
	}

	public Box getBox(String label){
		for(Box box : getBoxes())
			if(box.getLabel().equalsIgnoreCase(label))
				return box;

		return null;
	}

	public Set<Box> getBoxes(){
		return boxes;
	}

	public void open(Player player){
		Box box = getBox(Injector.getKey("box", player.getItemInHand()));

		if(!rewardsSet(box))
			return;

		Inventory inv = Bukkit.createInventory(null, 54, ChatColor.GRAY +box.getLabel());
		ItemStack pane = new ItemStack(Material.STAINED_GLASS_PANE);
		ItemMeta meta = pane.getItemMeta();
		meta.setDisplayName(" ");
		pane.setItemMeta(meta);
		for(int i = 0; i < 54; i++){
			inv.setItem(i, pane);
		}

		ItemStack normal = new ItemStack(Material.CHEST);
		ItemMeta normalMeta = normal.getItemMeta();
		normalMeta.setDisplayName(Base.get().getCache().NORMAL_REWARD_NAME());
		normalMeta.setLore(Base.get().getCache().NORMAL_REWARD_LORE());
		normal.setItemMeta(normalMeta);

		ItemStack finali = new ItemStack(Material.ENDER_CHEST);
		ItemMeta finaliMeta = finali.getItemMeta();
		finaliMeta.setDisplayName(Base.get().getCache().FINAL_REWARD_NAME());
		finaliMeta.setLore(Base.get().getCache().FINAL_REWARD_LORE());
		finali.setItemMeta(finaliMeta);

		int[] slots = new int[]{ 12, 13, 14, 21, 22, 23, 30, 31, 32 };
		for(int i = 0; i < slots.length; i++){
			inv.setItem(slots[i], normal);
		}
		inv.setItem(40, finali);

		if(player.getItemInHand().getAmount() == 1)
			player.getInventory().remove(player.getItemInHand());
		else
			player.getItemInHand().setAmount(player.getItemInHand().getAmount() -1);
		player.openInventory(inv);

		setOpening(player.getUniqueId(), new Request(inv, box));
	}

	@EventHandler
	public void onClick(CollectRewardEvent event){
		if(event.getType().equals(CollectRewardEvent.Type.FINAL)){
			Inventory loot = event.getInventory();
			for(int height = 1; height < 4; height ++){
				for(int length = 0; length < 3; length ++){
					ItemStack stack = loot.getItem((height * 9) +length +3);
					if(stack == null || stack.getType().equals(Material.AIR) || !stack.getItemMeta().hasDisplayName())
						continue;

					if(stack.getItemMeta().getDisplayName().equalsIgnoreCase(Base.get().getCache().NORMAL_REWARD_NAME())){
						event.getPlayer().sendMessage(ChatColor.RED +"You must open normal rewards first.");
						return;
					}
				}
			}
		}

		ItemStack reward = getReward(event.getType(), event.getPlayer(), event.getBox());
		event.getInventory().setItem(event.getSlot(), reward);
		World world = event.getPlayer().getWorld();
		if(!Injector.hasKey("flag", reward)){
			HashMap<Integer, ItemStack> left = event.getPlayer().getInventory().addItem(reward);
			left.keySet().forEach(key -> world.dropItem(event.getPlayer().getLocation(), left.get(key)));
		}
	}

	public boolean rewardsSet(Box box){
		Inventory loot = box.getLoot();
		ItemStack normal = null;
		ItemStack finali = null;

		for(int height = 0; height < 6; height++){
			if(normal != null)
				break;

			for(int length = 0; length < 6; length++){
				ItemStack stack = loot.getItem(((height * 9) + length));
				if(stack == null || stack.getType().equals(Material.AIR))
					continue;

				if(Injector.hasKey("chance", stack))
					normal = stack;
			}
		}

		for(int height = 0; height < 6; height++){
			if(finali != null)
				break;

			for(int length = 0; length < 2; length++){
				ItemStack stack = loot.getItem(((height * 9) +length +7));
				if(stack == null || stack.getType().equals(Material.AIR))
					continue;

				if(Injector.hasKey("chance", stack))
					finali = stack;
			}
		}

		return normal != null && finali != null;
	}

	public ItemStack getReward(CollectRewardEvent.Type type, Player player, Box box){
		AtomicReference<ItemStack> ref = new AtomicReference<>();
		Inventory loot = box.getLoot();

		RandomCollection<ItemStack> chances = new RandomCollection<>();
		switch(type){
			case NORMAL:
				for(int height = 0; height < 6; height++){
					for(int length = 0; length < 6; length++){
						ItemStack stack = loot.getItem(((height * 9) + length));
						if(stack == null || stack.getType().equals(Material.AIR))
							continue;

						if(Injector.hasKey("chance", stack))
							chances.add(Double.parseDouble(Injector.getKey("chance", stack)), stack);
					}
				}
				break;
			case FINAL:
				for(int height = 0; height < 6; height++){
					for(int length = 0; length < 2; length++){
						ItemStack stack = loot.getItem(((height * 9) +length +7));
						if(stack == null || stack.getType().equals(Material.AIR))
							continue;

						if(Injector.hasKey("chance", stack))
							chances.add(Double.parseDouble(Injector.getKey("chance", stack)), stack);
					}
				}
		}

		ItemStack selection = chances.next().clone();
		Injector injector = new Injector(selection);
		if(injector.hasKey("command")){
			String command = injector.getCommand();
			for(int times = 0; times < injector.getTimes(); times++)
				Base.get().getServer().dispatchCommand(Base.get().getServer().getConsoleSender(), command.replace("{player}", player.getName()).replace("{amount}", String.valueOf(selection.getAmount())));
		}

		ItemMeta meta = selection.getItemMeta();
		List<String> lore = meta.getLore();
		lore.removeIf(entry -> entry.contains("Chance: "));
		meta.setLore(lore);
		selection.setItemMeta(meta);

		ref.set(selection);

		return ref.get();
	}

	public Inventory getNewInventory(Player player){
		Inventory inventory = Bukkit.createInventory(null, 54, ChatColor.GRAY +"Box List");

		ItemStack empty = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 15);
		for(int i = 0; i < 17; i++){
			inventory.setItem(i < 9 ? i : i +(9 * 4), empty);
		}
		for(int i = 0; i < 11; i++){
			inventory.setItem(i < 5 ? i * 9 : ((i -5) * 9) +8, empty);
		}

		if(getBoxes().isEmpty())
			return inventory;

		for(Box box : getBoxes()){
			ItemStack stack = box.getStack().clone();

//			List<String> lores = new ArrayList<>();
//			for(ItemStack entry : box.getLoot().getContents()){
//				if(entry == null || entry.getType().equals(Material.AIR))
//					continue;
//
//				if(entry.getItemMeta().hasDisplayName())
//					if(!entry.getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.BOLD +"Separator"))
//						lores.add(entry.getItemMeta().getDisplayName());
//			}
//
//			ItemMeta meta = stack.getItemMeta();
//			meta.setLore(lores);
//			stack.setItemMeta(meta);
			if(player.hasPermission("lootboxes.list.details")){
				ItemMeta meta = stack.getItemMeta();
				if(meta.hasDisplayName()){
					meta.setDisplayName(meta.getDisplayName() + ChatColor.RESET +" (" +ChatColor.ITALIC +box.getLabel() +ChatColor.RESET +")");
				}else{
					meta.setDisplayName(ChatColor.RESET +" (" +ChatColor.ITALIC +box.getLabel() +ChatColor.RESET +")");
				}

				stack.setItemMeta(meta);
			}

			inventory.addItem(stack);
		}

		return inventory;
	}
}
