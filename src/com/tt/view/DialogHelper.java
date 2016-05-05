package com.tt.view;

/**
 * @项目名称 :CellNote
 * @文件名称 :DialogHelper.java
 * @所在包 :org.nerve.cellnote.view.dialog
 * @功能描述 :
 *	辅助类
 * @创建者 :集成显卡	1053214511@qq.com
 * @创建日期 :2013-1-24
 * @修改记录 :
 */
public class DialogHelper {
	/**
	 * @项目名称 :CellNote
	 * @文件名称 :MenuDialog.java
	 * @所在包 :org.nerve.cellnote.view
	 * @功能描述 :
	 *	菜单数据体，其中 icons 是菜单的图像引用，labels 是菜单文字
	 * @创建者 :集成显卡	1053214511@qq.com
	 * @创建日期 :2013-1-23
	 * @修改记录 :
	 */
	public static class MenuData{

		/**背景的drawable，如果为-1，则使用默认的背景*/
		public int bgResource = -1;
		/**背景颜色*/
		public int bgColor = -1;
		
		/**菜单点击监听器*/
		public MenuListener listener;
		/**为true时，菜单被点击后后自动消失*/
		public boolean onlyOneTime;
		
		public int[] icons = new int[0];
		public String[] labels;
		
		public MenuData(){
			this.onlyOneTime = true;
		}
		
		public MenuData(int [] is,String[] ls){
			this();
			this.icons = is;
			this.labels = ls;
		}
	}
	
	public interface MenuListener{
		/**
		 * @方法名称 :onClick
		 * @功能描述 :
		 * @param position 菜单项的下标
		 * @return :void
		 */
		public void onMenuClick(int code, int position);
	}
	
	public interface ConfirmListener{
		
		/**
		 * @方法名称 :onConfirmClick
		 * @功能描述 :当confirm对话框中的按钮被点击时
		 * @param position
		 * @return :void
		 */
		public void onConfirmClick(int position, Object obj);
	}
}
