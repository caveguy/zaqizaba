package com.tt.xml;

public class MachineTemper{
	private  int temper_goal=0;
	private  int temper_backLash=0;
	private  int temper_min=0;
	
	
	public MachineTemper(int goal,int back,int min){
		super();
			temper_goal=goal;
			temper_backLash=back;
			temper_min=min;
			
		}

		public int getTemper_goal(){
			return temper_goal;
		}
		public int getTemper_backLash(){
			return temper_backLash;
		}
		public int getTemper_min(){
			return temper_min;
		}
}