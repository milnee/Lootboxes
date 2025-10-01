package me.millen.lootboxes.management.managers;
/*
 *  created by Turben on 01/06/2020
 */

import me.millen.lootboxes.Base;
import me.millen.lootboxes.framework.Box;
import me.millen.lootboxes.injection.Injector;
import me.millen.lootboxes.management.Manager;
import me.millen.lootboxes.utils.serialization.Serializer;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SaveManager implements Manager{

	private File file;
	private FileConfiguration data;

	@Override
	public void setup(){
		file = new File(Base.get().getDataFolder(), "data.yml");
		data = YamlConfiguration.loadConfiguration(file);

		if(!file.exists())
			saveOnly();

		load();
	}

	public void save(){
		data.set("boxes", null);

		for(Box box : Base.get().getBoxManager().getBoxes()){
			List<String> lores = new ArrayList<>();
			if(box.getStack().getItemMeta().hasLore())
				for(String string : box.getStack().getItemMeta().getLore())
					lores.add(string.replaceAll("§", "&"));

			String prefix = "boxes." +box.getLabel() +".";
			data.set(String.format("%s%s", prefix, "name"), (box.getStack().getItemMeta().hasDisplayName() ? box.getStack().getItemMeta().getDisplayName().replaceAll("§", "&") : box.getLabel()));
			data.set(String.format("%s%s", prefix, "lores"), lores);
			data.set(String.format("%s%s", prefix, "material"), box.getStack().getType().name());
			data.set(String.format("%s%s", prefix, "data"), String.valueOf(box.getStack().getDurability()));
			data.set(String.format("%s%s", prefix, "itemstack"), Serializer.serializeItemStack(box.getStack()));

			for(int index = 0; index < 54; index++){
				ItemStack stack = box.getLoot().getItem(index);
				if(stack == null || stack.getType().equals(Material.AIR))
					continue;

				data.set(String.format("%s%s", prefix, "items." +index), Serializer.serializeItemStack(stack));
			}
		}

		saveOnly();
	}

	public void load(){
		if(!data.isSet("boxes")) return;

		for(String key : data.getConfigurationSection("boxes").getKeys(false)){
			ItemStack stack = new ItemStack(Material.matchMaterial(data.getString("boxes." +key +".material")), 1, Short.parseShort(data.getString("boxes." +key +".data")));
			ItemMeta meta = stack.getItemMeta();
			meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', data.getString("boxes." +key +".name")));
			List<String> lores = new ArrayList<>();
			for(String string : data.getStringList("boxes." +key +".lores"))
				lores.add(ChatColor.translateAlternateColorCodes('&', string));
			meta.setLore(lores);
			stack.setItemMeta(meta);

			Map<String, String> keys = Injector.getKeys(Serializer.deserializeItemStack(data.getString("boxes." +key +".itemstack")));
			if(keys == null || keys.isEmpty())
				continue;

			ItemStack boxStack = new Injector(stack).setKeys(keys).getStack();

			Box box = new Box(key, boxStack);

			for(String slot : data.getConfigurationSection("boxes." +key +".items").getKeys(false)){
				ItemStack itemStack = Serializer.deserializeItemStack(data.getString("boxes." +key +".items." +slot));

				box.getLoot().setItem(Integer.parseInt(slot), itemStack);
			}
		}
	}

	public void saveOnly(){
		try{
			data.save(file);
		}catch(IOException e){
			e.printStackTrace();
		}
	}
}
