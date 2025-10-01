package me.millen.lootboxes.utils.serialization;
/*
 *  created by Turben on 01/06/2020
 */

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.math.BigInteger;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;

import com.google.common.collect.Maps;

public class Serializer{

	public static Map<String, String> deserialize(String string){
		if(!string.contains(":"))
			return null;

		Map<String, String> map = Maps.newConcurrentMap();
		try{
			String[] entries;
			if(string.contains("~"))
				entries = string.split("~");
			else
				entries = new String[]{ string };

			if(entries.length < 1)
				return null;

			for(String entry : entries){
				String key = entry.split(":")[0];
				String value = entry.split(":")[1];
				map.put(key, value);
			}
		}catch(IndexOutOfBoundsException ignored){}

		return map;
	}

	public static String serialize(Map<String, String> map){
		StringBuilder builder = new StringBuilder();
		for(String string : map.keySet())
			builder.append(string).append(":").append(map.get(string)).append("~");

		return builder.toString().substring(0, builder.toString().length() -1);
	}

	public static ItemStack deserializeItemStack(String data){
		ByteArrayInputStream inputStream = new ByteArrayInputStream(new BigInteger(data, 32).toByteArray());
		DataInputStream dataInputStream = new DataInputStream(inputStream);

		ItemStack itemStack = null;
		try {
			Class<?> nbtTagCompoundClass = getNMSClass("NBTTagCompound");
			Class<?> nmsItemStackClass = getNMSClass("ItemStack");
			Object nbtTagCompound = getNMSClass("NBTCompressedStreamTools").getMethod("a", DataInputStream.class).invoke(null, dataInputStream);
			//Object nbtTagCompound = getNMSClass("NBTCompressedStreamTools").getMethod("a", DataInputStream.class).invoke(null, inputStream);
			Object craftItemStack = nmsItemStackClass.getMethod("createStack", nbtTagCompoundClass).invoke(null, nbtTagCompound);
			itemStack = (ItemStack) getOBClass("inventory.CraftItemStack").getMethod("asBukkitCopy", nmsItemStackClass).invoke(null, craftItemStack);
		} catch(IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
		}

		return itemStack;
	}

	public static String serializeItemStack(ItemStack item) {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		DataOutputStream dataOutput = new DataOutputStream(outputStream);

		try {
			Class<?> nbtTagCompoundClass = getNMSClass("NBTTagCompound");
			Constructor<?> nbtTagCompoundConstructor = nbtTagCompoundClass.getConstructor();
			Object nbtTagCompound = nbtTagCompoundConstructor.newInstance();
			Object nmsItemStack = getOBClass("inventory.CraftItemStack").getMethod("asNMSCopy", ItemStack.class).invoke(null, item);
			getNMSClass("ItemStack").getMethod("save", nbtTagCompoundClass).invoke(nmsItemStack, nbtTagCompound);
			getNMSClass("NBTCompressedStreamTools").getMethod("a", nbtTagCompoundClass, DataOutput.class).invoke(null, nbtTagCompound, (DataOutput)dataOutput);

		} catch (SecurityException | NoSuchMethodException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
		}

		//return BaseEncoding.base64().encode(outputStream.toByteArray());
		return new BigInteger(1, outputStream.toByteArray()).toString(32);
	}

	private static Class<?> getNMSClass(String name) {
		String version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
		try {
			return Class.forName("net.minecraft.server." + version + "." + name);
		} catch (ClassNotFoundException var3) {
			var3.printStackTrace();
			return null;
		}
	}

	private static Class<?> getOBClass(String name) {
		String version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
		try {
			return Class.forName("org.bukkit.craftbukkit." + version + "." + name);
		} catch (ClassNotFoundException var3) {
			var3.printStackTrace();
			return null;
		}
	}
}
