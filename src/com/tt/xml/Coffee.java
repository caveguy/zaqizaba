package com.tt.xml;

public class Coffee {
	private Integer id=null;
	private String name=null;
	private Integer needCoffee=null;
	private String sugar_level=null;//用string是为了一个中分出4个

	private Integer sugar_preWater=null;
	private Integer sugar_water=null;
	private Integer milk_level=null;
	private Integer milk_preWater=null;
	private Integer milk_water=null;
	private Integer chocolate_level=null;
	private Integer chocolate_preWater=null;
	private Integer chocolate_water=null;
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Integer getNeedCoffee() {
		return needCoffee;
	}
	public void setNeedCoffee(Integer value) {
		this.needCoffee = value;
	}
	public String getSugarLever() {
		return sugar_level;
	}
	public void setSugarLever(String l) {
		this.sugar_level = l;
	}
	public Integer getSugarPreWater() {
		return sugar_preWater;
	}
	public void setSugarPreWater(Integer l) {
		this.sugar_preWater = l;
	}
	public Integer getSugarWater() {
		return sugar_water;
	}
	public void setSugarWater(Integer l) {
		this.sugar_water = l;
	}
	public Integer getMilkLever() {
		return milk_level;
	}
	public void setMilkLever(Integer l) {
		this.milk_level = l;
	}
	public Integer getMilkPreWater() {
		return milk_preWater;
	}
	public void setMilkPreWater(Integer l) {
		this.milk_preWater = l;
	}
	public Integer getMilkWater() {
		return milk_water;
	}
	public void setMilkWater(Integer l) {
		this.milk_water = l;
	}
	public Integer getChocolateLever() {
		return chocolate_level;
	}
	public void setChocolateLever(Integer l) {
		this.chocolate_level = l;
	}
	public Integer getChocolatePreWater() {
		return chocolate_preWater;
	}
	public void setChocolatePreWater(Integer l) {
		this.chocolate_preWater = l;
	}
	public Integer getChocolateWater() {
		return chocolate_water;
	}
	public void setChocolateWater(Integer l) {
		this.chocolate_water = l;
	}
	@Override
	public String toString() { 
	
		return "Coffee [id="+id + ", name=" + name+"" +" needCoffee="+needCoffee+
				",\n sugar_level="+sugar_level+ ",sugar_preWater"+sugar_preWater+",sugar_water"+sugar_water+
				",\n milk_level="+milk_level+ ",milk_preWater"+milk_preWater+",milk_water"+milk_water+
				",\n chocolate_level="+chocolate_level+ ",chocolate_preWater"+chocolate_preWater+",chocolate_water"+chocolate_water+
				"]";
	}
//	public Coffee(int id, String name, String sugar_lev,int sugar_pre,int sugar_water,int milk_lev,int milk_pre,int milk_water) {
//		this.id = id;
//		this.name = name;
//		this.milk_level=milk_lev;
//		this.milk_water=milk_water;
//		this.milk_preWater=milk_pre;
//		this.sugar_level=sugar_lev;
//		this.sugar_water=sugar_water;
//		this.sugar_preWater=sugar_pre;
//	}
	public Coffee(){}
}
