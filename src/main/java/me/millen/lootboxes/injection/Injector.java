package me.millen.lootboxes.injection;
/*
 *  created by Turben on 01/06/2020
 */

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.comphenix.attribute.AttributeStorage;
import com.comphenix.attribute.Attributes;
import com.google.common.collect.Maps;

import me.millen.lootboxes.utils.serialization.Serializer;

public class Injector{

	private ItemStack stack;
	private UUID uuid;

	public Injector(ItemStack stack){
		this.stack = stack;

		if(hasKey("uuid", stack))
			setUUID(UUID.fromString(getKey("uuid", stack)));
		else
			init();
	}

	public static boolean hasKey(String key, ItemStack stack){
		Attributes attributes = new Attributes(stack);
		if(attributes.size() >= 1){
			for(Attributes.Attribute attribute : attributes.values()){
				Map<String, String> keys = Serializer.deserialize(attribute.getName());
				if(!(keys == null || keys.isEmpty())){
					return keys.containsKey(key);
				}
			}
		}

		return false;
	}

	public static String getKey(String key, ItemStack stack){
		return Objects.requireNonNull(getKeys(stack)).get(key);
	}

	public static boolean isBox(ItemStack stack){
		return hasKey("box", stack);
	}

	public static Map<String, String> getKeys(ItemStack from){
		Attributes attributes = new Attributes(from);
		if(attributes.size() >= 1){
			for(Attributes.Attribute attribute : attributes.values()){
				Map<String, String> keys = Serializer.deserialize(attribute.getName());
				if(!(keys == null || keys.isEmpty())){
					return keys;
				}
			}
		}

		return null;
	}

	public void init(){
		UUID uuid = UUID.randomUUID();
		this.uuid = uuid;
		AttributeStorage storage = AttributeStorage.newTarget(stack, uuid);
		Map<String, String> keys = Maps.newConcurrentMap();
		keys.put("uuid", uuid.toString());
		storage.setData(Serializer.serialize(keys));
		this.stack = storage.getTarget();
	}

	public Injector setKeys(Map<String, String> keys){
		for(String string : keys.keySet())
			set(string, keys.get(string));

		return this;
	}

	public Injector setBox(String label){
		set("box", label);
		return this;
	}

	public Injector setChances(double chances){
		set("chance", String.valueOf(chances));

		ItemMeta meta = stack.getItemMeta();
		List<String> lore;
		if(meta.hasLore())
			lore = meta.getLore();
		else
			lore = new ArrayList<>();

		lore.removeIf(line -> line.startsWith(ChatColor.GRAY +"Chance: "));
		lore.add(ChatColor.GRAY +"Chance: " +ChatColor.YELLOW +chances +"%");
		meta.setLore(lore);
		stack.setItemMeta(meta);

		return this;
	}

	public double getChances(){
		return Double.parseDouble(getKey("chance", stack));
	}

	public Injector setCommand(String command){
		set("command", command);
		return this;
	}

	public String getCommand(){
		return getKey("command", stack);
	}

	public Injector setTimes(int times){
		set("times", String.valueOf(times));
		return this;
	}

	public int getTimes(){
		return Integer.parseInt(getKey("times", stack));
	}

	public Injector addFlag(Flag flag){
		set(flag.name().toLowerCase(), String.valueOf(true));
		return this;
	}

	public Flag getFlag(){
		return Flag.valueOf(getKey("flag", stack));
	}

	public void set(String key, String value){
		Map<String, String> keys = getKeys(stack);
		keys.put(key, value);
		setData(Serializer.serialize(keys));
	}

	public void removeKey(String key){
		Map<String, String> keys = getKeys(stack);
		if(Objects.nonNull(keys)){
			keys.remove(key);
			setData(Serializer.serialize(keys));
		}
	}

	public boolean hasKey(String key){
		return getKeys(stack).containsKey(key);
	}

	public ItemStack getStack(){
		return this.stack;
	}

	public UUID getUUID(){
		return this.uuid;
	}

	public void setUUID(UUID uuid){
		this.uuid = uuid;
	}

	public String getData(){
		AttributeStorage storage = AttributeStorage.newTarget(stack, uuid);
		return storage.getData(null);
	}

	public void setData(String data){
		AttributeStorage storage = AttributeStorage.newTarget(stack, uuid);
		storage.setData(data);
		stack = storage.getTarget();
	}

	public enum Flag{

		DISPLAY;
	}
}
