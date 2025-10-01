package me.millen.lootboxes.utils.general;
/*
 *  created by Turben on 01/06/2020
 */

import java.util.NavigableMap;
import java.util.Random;
import java.util.TreeMap;

public class RandomCollection<E> {
	private final NavigableMap<Double, E> map = new TreeMap<>();
	private final Random random;
	private double total = 0;

	public RandomCollection() {
		this(new Random());
	}

	public RandomCollection(Random random) {
		this.random = random;
	}

	public boolean isEmpty(){
		return map.isEmpty();
	}

	public void add(double weight, E result){
		if (weight <= 0) return;
		total += weight;
		map.put(total, result);
	}

	public E next() {
		double value = random.nextDouble() * total;
		return map.higherEntry(value).getValue();
	}
}