package me.millen.lootboxes.listeners;
/*
 *  created by Turben on 01/06/2020
 */

import me.millen.lootboxes.Base;
import me.millen.lootboxes.events.CollectRewardEvent;
import me.millen.lootboxes.injection.Injector;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class ClickBox implements Listener{

	@EventHandler
	public void click(PlayerInteractEvent event){
		Player player = event.getPlayer();
		if(player.hasPermission("lootboxes.use") && (!(player.getItemInHand() == null || player.getItemInHand().getType().equals(Material.AIR))) && (event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_AIR)){
			if(Injector.hasKey("box", player.getItemInHand())){
				if(!Base.get().getBoxManager().isOpening(event.getPlayer().getUniqueId()))
					Base.get().getBoxManager().open(player);
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void invClick(InventoryClickEvent event){
		if(event.getInventory() == null || event.getCurrentItem() == null || event.getCurrentItem().getType().equals(Material.AIR)) return;
		if(Base.get().getBoxManager().isOpening(event.getWhoClicked().getUniqueId())){
			if(event.getInventory().equals(Base.get().getBoxManager().getCurrent(event.getWhoClicked().getUniqueId()).getInventory())){
				event.setCancelled(true);

				ItemStack current = event.getCurrentItem();
				if(!current.getItemMeta().hasDisplayName()) return;
				if(current.getItemMeta().getDisplayName().equalsIgnoreCase(Base.get().getCache().NORMAL_REWARD_NAME()) || current.getItemMeta().getDisplayName().equalsIgnoreCase(Base.get().getCache().FINAL_REWARD_NAME()))
					Bukkit.getPluginManager().callEvent(
							new CollectRewardEvent(current.getItemMeta().getDisplayName().equalsIgnoreCase(Base.get().getCache().NORMAL_REWARD_NAME()) ? CollectRewardEvent.Type.NORMAL : CollectRewardEvent.Type.FINAL
							, event.getInventory()
							, event.getSlot()
							, ((Player) event.getWhoClicked())
							, Base.get().getBoxManager().getCurrent(event.getWhoClicked().getUniqueId()).getBox()));
			}
		}
	}

	@EventHandler
	public void invClose(InventoryCloseEvent event){
		if(!Base.get().getBoxManager().isOpening(event.getPlayer().getUniqueId())) return;

		if(event.getInventory().equals(Base.get().getBoxManager().getCurrent(event.getPlayer().getUniqueId()).getInventory())){
			Inventory loot = event.getInventory();
			boolean rewards = false;
			for(int height = 1; height < 4; height ++){
				if(rewards) break;

				for(int length = 0; length < 3; length ++){
					ItemStack stack = loot.getItem((height * 9) +length +3);
					if(stack == null || stack.getType().equals(Material.AIR) || !stack.getItemMeta().hasDisplayName())
						continue;

					if(stack.getItemMeta().getDisplayName().equalsIgnoreCase(Base.get().getCache().NORMAL_REWARD_NAME()))
						rewards = true;
				}
			}

			ItemStack finali = loot.getItem(40);
			if(!(finali == null || finali.getType().equals(Material.AIR))){
				if(finali.getItemMeta().hasDisplayName()){
					if(finali.getItemMeta().getDisplayName().equalsIgnoreCase(Base.get().getCache().FINAL_REWARD_NAME()))
						rewards = true;
				}
			}

			if(rewards)
				Bukkit.getScheduler().runTaskLater(Base.get(), () -> event.getPlayer().openInventory(loot), 0);
			else
				if(Base.get().getBoxManager().isOpening(event.getPlayer().getUniqueId()))
					Base.get().getBoxManager().removeOpening(event.getPlayer().getUniqueId());
		}
	}
}
