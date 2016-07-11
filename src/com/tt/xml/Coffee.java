package com.tt.xml;

public class Coffee {
	private Integer id=null;
	private Integer order=null;
	private String name=null;
	private String price=null;

	
	private Integer needCoffee=0;
	private Integer coffeePowder=0;
	private Integer coffeeWater=0;
	private Integer coffeePreWater=0;

	/*
	 * 糖比较特殊，用户需要选择，用string splite四份
	 */
	private String ch1L_sugar_level=null;//用string是为了一个中分出4个
	private Integer ch1R_powder_level=0;
	private Integer ch2L_powder_level=0;
	private Integer ch2R_powder_level=0;
	private Integer ch3L_powder_level=0;
	private Integer ch3R_powder_level=0;
	private Integer ch4L_powder_level=0;
	private Integer ch4R_powder_level=0;
	private Integer ch1_water=0;
	private Integer ch2_water=0;
	private Integer ch3_water=0;
	private Integer ch4_water=0;
	
	
//	
//	private Integer sugar_preWater=null;
//	private Integer sugar_water=null;
//	private Integer milk_level=null;
//	private Integer milk_preWater=null;
//	private Integer milk_water=null;
//	private Integer chocolate_level=null;
//	private Integer chocolate_preWater=null;
//	private Integer chocolate_water=null;
	
	
	
	
	
	public Integer getId() {
		return id;
	}
	public Integer getOrder() {
		return order;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public void setorder(Integer o) {
		this.order = o;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPrice() {
		return price;
	}
	public void setPrice(String price) {
		this.price = price;
	}
	public Integer getNeedCoffee() {
		return needCoffee;
	}
	public Integer getCoffeePowder() {
		return coffeePowder;
	}
	public Integer getCoffeeWater() {
		return coffeeWater;
	}
	public Integer getCoffeePreWater() {
		return coffeePreWater;
	}
	public void setNeedCoffee(Integer value) {
		this.needCoffee = value;
	}
	public void setCoffeePowder(Integer value) {
		this.coffeePowder = value;
	}
	public void setCoffeeWater(Integer value) {
		this.coffeeWater = value;
	}
	public void setCoffeePreWater(Integer value) {
		this.coffeePreWater = value;
	}
	public String getSugarLever() {
		return ch1L_sugar_level;
	}
	public void setSugarLever(String l) {
		this.ch1L_sugar_level = l;
	}
	public void setCh1R_powder_level(Integer l) {
		this.ch1R_powder_level=l;
	}
	public void setCh2L_powder_level(Integer l) {
		this.ch2L_powder_level=l;
	}
	public void setCh2R_powder_level(Integer l) {
		this.ch2R_powder_level=l;
	}
	public void setCh3L_powder_level(Integer l) {
		this.ch3L_powder_level=l;
	}
	public void setCh3R_powder_level(Integer l) {
		this.ch3R_powder_level=l;
	}
	public void setCh4L_powder_level(Integer l) {
		this.ch4L_powder_level=l;
	}
	public void setCh4R_powder_level(Integer l) {
		this.ch4R_powder_level=l;
	}
	public Integer getCh1R_powder_level() {
		return ch1R_powder_level;
	}
	public Integer getCh2L_powder_level() {
		return ch2L_powder_level;
	}
	public Integer getCh2R_powder_level() {
		return ch2R_powder_level;
	}
	public Integer getCh3L_powder_level() {
		return ch3L_powder_level;
	}
	public Integer getCh3R_powder_level() {
		return ch3R_powder_level;
	}
	public Integer getCh4L_powder_level() {
		return ch4L_powder_level;
	}
	public Integer getCh4R_powder_level() {
		return ch4R_powder_level;
	}
	
	public Integer getCh1Water() {
		return ch1_water;
	}
	public Integer getCh2Water() {
		return ch2_water;
	}
	public Integer getCh3Water() {
		return ch3_water;
	}
	public Integer getCh4Water() {
		return ch4_water;
	}
	public void setCh1Water(Integer l) {
		this.ch1_water = l;
	}
	public void setCh2Water(Integer l) {
		this.ch2_water = l;
	}
	public void setCh3Water(Integer l) {
		this.ch3_water = l;
	}
	public void setCh4Water(Integer l) {
		this.ch4_water = l;
	}

	@Override
	public String toString() { 
	
		return "Coffee [order="+order+" id="+id + ", name=" + name+"" +" needCoffee="+needCoffee+
				",\n coffeePowder="+coffeePowder+" coffeeWater="+coffeeWater+" coffeePreWater="+coffeePreWater+
				",\n ch1L_sugar_level="+ch1L_sugar_level+" ch1R_powder_level="+ch1R_powder_level+
				",\n ch2L_powder_level="+ch2L_powder_level+" ch2R_powder_level="+ch2R_powder_level+
				",\n ch3L_powder_level="+ch3L_powder_level+" ch3R_powder_level="+ch3R_powder_level+
				",\n ch4L_powder_level="+ch4L_powder_level+" ch4R_powder_level="+ch4R_powder_level+
				",\n ch1_water="+ch1_water+" ch2_water="+ch2_water+
				",\n ch3_water="+ch3_water+" ch4_water="+ch4_water+
				
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
