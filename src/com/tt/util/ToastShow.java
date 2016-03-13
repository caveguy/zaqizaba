package com.tt.util;



import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.coffemachinev2.R;
public class ToastShow {
	  private Context context;
	  private Toast toast = null;
	  TextView message ;
	  View toastRoot;
	  public ToastShow(Context context) {
		  LayoutInflater inflater = LayoutInflater.from(context);  
		//  convertView = inflater.inflate(R.layout.adapter_item, null); 
	  		 toastRoot =inflater.inflate(R.layout.toast, null);
	  		 message = (TextView) toastRoot.findViewById(R.id.message);
		  		toast = new Toast(context);
		  		toast.setGravity(Gravity.BOTTOM, 0, 10);
		  		toast.setDuration(Toast.LENGTH_SHORT);
		  		toast.setView(toastRoot);
	   this.context = context;
	  }
	  

	  
	  
	  
	  
	  
	  public void toastShow(String text) {
	  		
	  		message.setText(text);


	  		toast.show();  
		  /*
	   if(toast == null)
	   {
	    toast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
	   }
	   else {
	    toast.setText(text);
	   }
	   toast.show();
	   */
	  }
	  
	  public void toastShow(int resid) {
	  		message.setText(resid);

	  	//	toast = new Toast(context);
	  	//	toast.setGravity(Gravity.BOTTOM, 0, 10);
	  	//	toast.setDuration(Toast.LENGTH_SHORT);
	  	//	toast.setView(toastRoot);
	  		toast.show(); 
		  
		  
		  /*
		   if(toast == null)
		   {
		    toast = Toast.makeText(context, resid, Toast.LENGTH_SHORT);
		   }
		   else {
		    toast.setText(resid);
		   }
		   toast.show();*/
		  }
	 }