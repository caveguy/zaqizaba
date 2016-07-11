package com.tt.pays;

/**
 * 服务器通信回调接口
 * 创建时间：2016-07-01
 *
 */
public interface  ServerCallback {
	//注册成功
	public void onLoginSuccess();
	public void onLoginFailed(String msg);
	//工艺表更新
	public void onFormulaUpdate(String xml);
	//文字更新
	public void onTextUpdate(String text);
	//有新文本更新
	public void onHaveNewText(String serial);
	//有更工艺表更新
	public void onHaveNewTech(String serial);
	//得到支付宝二维码
	public void onGetZfbQrCode(String qr);
	public void onGetWeixinQrCode(String qr);
	//支付成功
	public void onPaySuccess(String type,String buyerId);
	//支付失败
	public void onPayFailed();
	//有新app版本更新
	public void onGetNewVersion(String ver,String path);

}
