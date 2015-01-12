package com.LZP.access_internet;

import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.os.Build;

public class MainActivity extends ActionBarActivity {
private TextView state;
private EditText qqCode;
private Button query;
String result0=new String();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		state=(TextView)findViewById(R.id.state);
		qqCode=(EditText)findViewById(R.id.editText1);
		query=(Button)findViewById(R.id.button1);
		query.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String num=qqCode.getText().toString();
				
				new Thread(new DownLoadThread(num)).start();
			}
		});
		
		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}
	}

	class DownLoadThread implements Runnable {
		private String qqNumber;

		public DownLoadThread(String num) {
			Log.d("initial","initial");
			qqNumber = num;
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			state.post(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					state.setText("查询中……");
				}
			});
			final String xml;
			Log.d("Run","Run");
			HttpPost httpPost=new HttpPost("http://webservice.webxml.com.cn/webservices/qqOnlineWebService.asmx"
					+ "/qqCheckOnline");
					List<NameValuePair> params = new ArrayList<NameValuePair>();
					params.add(new BasicNameValuePair("qqCode",qqNumber));
					try{
						httpPost.setEntity(new UrlEncodedFormEntity(params,HTTP.UTF_8));
					}catch(UnsupportedEncodingException e){
						e.printStackTrace();
					}
					//执行post方法得到结果，将其转化为String
			DefaultHttpClient httpClient = new DefaultHttpClient();
			try{
				HttpResponse response = httpClient.execute(httpPost);
				HttpEntity result=response.getEntity();
				xml=EntityUtils.toString(result);
				result0=xml;
				Log.d("Try","TRY");
			}catch(IOException e){
			}
			
			XmlPullParserFactory factory;
			try {
				factory = XmlPullParserFactory.newInstance();
				factory.setNamespaceAware(true);
				XmlPullParser xpp=factory.newPullParser();
				xpp.setInput(new StringReader(result0));
				int eventType=xpp.getEventType();
				Log.d("Parser","Parser");
				while(eventType!=XmlPullParser.END_DOCUMENT){
					Log.d("While","Parser");
					switch(eventType){
					case XmlPullParser.TEXT:
						//final String tag=xpp.getName();
						final String r=xpp.getText().toString();
						//final String r=xpp.getAttributeValue(null,"xmlns");
						switch (r){
						case "V":
								result0="免费用户超过数量";
								break;
						case "Y":
								result0="在线";
								break;
						case "N":
								result0="离线";
								break;
						case "E":
								result0="QQ号码错误";
								break;
						case "A":
								result0="商业用户验证失败";
								break;
						}
						state.post(new Runnable() {
							
							@Override
							public void run() {
								// TODO Auto-generated method stub
								state.setText(result0);
							}
						});
						break;
					}
					eventType=xpp.next();
					/*
					if(eventType==XmlPullParser.START_DOCUMENT){
						System.out.println("Start document");
					}else if(eventType==XmlPullParser.START_TAG){
						
					}*/
				}
			} catch (XmlPullParserException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
	
		}
	}
/*
	public class SimpleXmlPullApp{
		public void main(String args[])
			throws XmlPullParserException,IOException
			{
				XmlPullParserFactory factory=XmlPullParserFactory.newInstance();
				factory.setNamespaceAware(true);
				XmlPullParser xpp=factory.newPullParser();
				
				xpp.setInput()
			}
	}
	*/
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_main, container,
					false);
			return rootView;
		}
	}

}
