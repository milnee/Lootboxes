package me.millen.lootboxes.events;
/*
 *  created by Turben on 01/06/2020
 */

import me.millen.lootboxes.framework.Box;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.Inventory;

public class CollectRewardEvent extends Event{

	private Type type;
	private int slot;
	private Inventory inventory;
	private Player player;
	private Box box;
	private static final HandlerList HANDLERS = new HandlerList();

	public CollectRewardEvent(Type type, Inventory inventory, int slot, Player player, Box box){
		super(false);

		this.type = type;
		this.inventory = inventory;
		this.slot = slot;
		this.player = player;
		this.box = box;
	}

	public HandlerList getHandlers() {
		return HANDLERS;
	}

	public static HandlerList getHandlerList() {
		return HANDLERS;
	}

	public Type getType(){
		return type;
	}

	public Inventory getInventory(){
		return inventory;
	}

	public int getSlot(){
		return slot;
	}

	public Player getPlayer(){
		return player;
	}

	public Box getBox(){
		return box;
	}

	public enum Type{
		NORMAL, FINAL;
	}
}
