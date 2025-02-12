package com.api.demo.iccard;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.common.demo.R;
import com.common.apiutil.reader.SLE4442Reader;
import com.common.apiutil.util.StringUtil;
import com.common.apiutil.util.SystemUtil;
import com.api.demo.bean.BaseActivity;

public class SLE4442Activity extends BaseActivity
{
	SLE4442Reader reader;
	Button readButton;
	Button writeButton;
	Button poweronButton;
	Button poweroffButton;
	Button openButton;
	Button closeButton;
	EditText addrEditText;
	EditText numEditText;
	EditText readResultEditText;
	EditText writeEditText;
	EditText writeContent;
	Button verifyPscButton;
	Button modifyPscButton;
	EditText pscEditText;
	
	
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.sle4442);
		
		addrEditText = (EditText)findViewById(R.id.addr_edt);
		numEditText = (EditText)findViewById(R.id.num_edt);
		readResultEditText = (EditText)findViewById(R.id.read_result_edt);
		writeEditText = (EditText)findViewById(R.id.write_addr_edt);
		writeContent = (EditText)findViewById(R.id.write_content_edt);
		pscEditText = (EditText)findViewById(R.id.psc_edt);
		
		reader = new SLE4442Reader(SLE4442Activity.this);
		
		OnClickListener listener = new OnClickListener()
		{
			
			
			public void onClick(View v)
			{
				switch (v.getId())
				{
				case R.id.open_btn:
					new openTask().execute();
					break;
					
				case R.id.close_btn:
					reader.close();
					closeButton.setEnabled(false);
					openButton.setEnabled(true);
					poweroffButton.setEnabled(false);
					poweronButton.setEnabled(false);
					readButton.setEnabled(false);
					writeButton.setEnabled(false);
					verifyPscButton.setEnabled(false);
					modifyPscButton.setEnabled(false);
					break;
					
				case R.id.read_btn:
					String addr_s = addrEditText.getText().toString();
					if (addr_s.length() == 0)
					{
						Toast.makeText(SLE4442Activity.this, "addr can not be null", Toast.LENGTH_SHORT).show();
						break;
					}
					String num_s = numEditText.getText().toString();
					if (num_s.length() == 0)
					{
						Toast.makeText(SLE4442Activity.this, "num can not be null", Toast.LENGTH_SHORT).show();
						break;
					}
					new readTask().execute(Integer.valueOf(addr_s), Integer.valueOf(num_s));
					break;
					
				case R.id.poweron_btn:
					if (reader.iccPowerOn())
					{
						poweronButton.setEnabled(false);
						poweroffButton.setEnabled(true);
						readButton.setEnabled(true);
						writeButton.setEnabled(true);
						verifyPscButton.setEnabled(true);
						modifyPscButton.setEnabled(true);
					}
					else 
					{
						Toast.makeText(SLE4442Activity.this, "ICC power on failed", Toast.LENGTH_SHORT).show();
					}
					break;
					
				case R.id.poweroff_btn:
					reader.iccPowerOff();
					poweroffButton.setEnabled(false);
					poweronButton.setEnabled(true);
					readButton.setEnabled(false);
					writeButton.setEnabled(false);
					verifyPscButton.setEnabled(false);
					modifyPscButton.setEnabled(false);
					break;

				case R.id.write_btn:
					String addr_w = writeEditText.getText().toString();
					if (addr_w.length() == 0)
					{
						Toast.makeText(SLE4442Activity.this, "addr can not be null", Toast.LENGTH_SHORT).show();
						break;
					}
					String content = writeContent.getText().toString();
					if (content.length() == 0)
					{
						Toast.makeText(SLE4442Activity.this, "no content", Toast.LENGTH_SHORT).show();
					}
					new writeTask().execute();
					break;
					
				case R.id.verify_psc_btn:
				{
					String psc = pscEditText.getText().toString();
					if (psc.length() != 6)
					{
						Toast.makeText(SLE4442Activity.this, "psc string length must be 6", Toast.LENGTH_SHORT).show();
					}
					else 
					{
						if (reader.pscVerify(str2BCD(psc)))
						{
							Toast.makeText(SLE4442Activity.this, "psc verify successful", Toast.LENGTH_SHORT).show();
						}
						else
						{
							Toast.makeText(SLE4442Activity.this, "psc verify failed", Toast.LENGTH_SHORT).show();
						}
					}
				}
					break;
					
				case R.id.modify_psc_btn:
				{
					String psc = pscEditText.getText().toString();
					if (psc.length() != 4)
					{
						Toast.makeText(SLE4442Activity.this, "psc string length must be 4", Toast.LENGTH_SHORT).show();
					}
					else 
					{
						if (reader.pscModify(str2BCD(psc)))
						{
							Toast.makeText(SLE4442Activity.this, "psc verify successful", Toast.LENGTH_SHORT).show();
						}
						else
						{
							Toast.makeText(SLE4442Activity.this, "psc verify failed", Toast.LENGTH_SHORT).show();
						}
					}
					break;
				}
				default:
					break;
				}
			}
		};		
		
		readButton = (Button)findViewById(R.id.read_btn);
		readButton.setOnClickListener(listener);
		readButton.setEnabled(false);
		writeButton = (Button)findViewById(R.id.write_btn);
		writeButton.setOnClickListener(listener);
		writeButton.setEnabled(false);
		poweronButton = (Button)findViewById(R.id.poweron_btn);
		poweronButton.setOnClickListener(listener);
		poweronButton.setEnabled(false);
		poweroffButton = (Button)findViewById(R.id.poweroff_btn);
		poweroffButton.setOnClickListener(listener);
		poweroffButton.setEnabled(false);
		openButton = (Button)findViewById(R.id.open_btn);
		openButton.setOnClickListener(listener);
		closeButton = (Button)findViewById(R.id.close_btn);
		closeButton.setOnClickListener(listener);
		closeButton.setEnabled(false);
		verifyPscButton = (Button)findViewById(R.id.verify_psc_btn);
		verifyPscButton.setOnClickListener(listener);
		verifyPscButton.setEnabled(false);
		modifyPscButton = (Button)findViewById(R.id.modify_psc_btn);
		modifyPscButton.setOnClickListener(listener);
		modifyPscButton.setEnabled(false);
	}
	
	
	protected void onDestroy()
	{
		super.onDestroy();
	}

	private class openTask extends AsyncTask<Void, Integer, Boolean>
	{

		
		protected Boolean doInBackground(Void... params)
		{
			if(SystemUtil.getDeviceType() == StringUtil.DeviceModelEnum.TPS450C.ordinal()) {
				return reader.open(1);
			}else {
				return reader.open();
			}
		}
		
		
		protected void onPostExecute(Boolean result)
		{
			if (result)
			{
				openButton.setEnabled(false);
				closeButton.setEnabled(true);
				poweronButton.setEnabled(true);
				writeButton.setEnabled(false);
			}
			else 
			{
				Toast.makeText(SLE4442Activity.this, "Open reader failed", Toast.LENGTH_SHORT).show();
			}
		}
	}
	
	private class readTask extends AsyncTask<Integer, Integer, byte[]>
	{
		ProgressDialog dialog;
		
		
		protected void onPreExecute()
		{
			super.onPreExecute();
			
			dialog = ProgressDialog.show(SLE4442Activity.this, "SLE4442", "正在读主存储器，请稍候...");
		}

		
		protected byte[] doInBackground(Integer... params)
		{
			byte[] data = null;
			try
			{
				data = reader.readMainMemory(params[0], params[1]);
			}
			catch (Exception e)
			{
				e.printStackTrace();
				return null;
			}
			
			return data;
		}
		
		
		protected void onPostExecute(byte[] result)
		{
			super.onPostExecute(result);
			
			dialog.dismiss();
			if (result != null)
			{
				readResultEditText.setText(BCD2Str(result));
			}
			else 
			{
				Toast.makeText(SLE4442Activity.this, "Read main memory failed", Toast.LENGTH_SHORT).show();
			}
		}
		
	}
	
	private class writeTask extends AsyncTask<Void, Integer, Boolean>
	{
		ProgressDialog dialog;
		
		
		protected void onPreExecute()
		{
			super.onPreExecute();
			
			dialog = ProgressDialog.show(SLE4442Activity.this, "SLE4442", "正在写主存储器，请稍候...");
		}
		
		
		protected Boolean doInBackground(Void... params)
		{
			Boolean ret = false;
			String addr = writeEditText.getText().toString();
			String content = writeContent.getText().toString().replace(" ", "").toUpperCase();
			try
			{
				ret = reader.updateMainMemory(Integer.valueOf(addr), str2BCD(content));
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
			
			return ret;
		}

		
		protected void onPostExecute(Boolean result)
		{
			super.onPostExecute(result);
			
			dialog.dismiss();
			if (result)
			{
				Toast.makeText(SLE4442Activity.this, "update main memory success", Toast.LENGTH_SHORT).show();
			}
			else 
			{
				Toast.makeText(SLE4442Activity.this, "update main memory failed", Toast.LENGTH_SHORT).show();
			}
		}
		
		
		
	}
	
	private String BCD2Str(byte[] data)
	{
		String string;
		StringBuilder stringBuilder = new StringBuilder();
		
		for (int i = 0; i < data.length; i++)
		{
			string = Integer.toHexString(data[i] & 0xFF);
			if (string.length() == 1)
			{
				stringBuilder.append("0");
			}
			
			stringBuilder.append(string.toUpperCase());
			stringBuilder.append(" ");
		}
		
		return stringBuilder.toString();
	}
	
	private byte[] str2BCD(String string)
	{
		int len;
		String str;
		String hexStr = "0123456789ABCDEF";
		
		String s = string.toUpperCase();
		
		len = s.length();
		if ((len % 2) == 1)
		{
			// 长度不为偶数，右补0
			str = s + "0";
			len = (len + 1) >> 1;
		}
		else 
		{
			str = s;
			len >>= 1;
		}
		
		byte[] bytes = new byte[len];
		byte high;
		byte low;
		
		for (int i = 0, j = 0; i < len; i++, j += 2)
		{
			high = (byte)(hexStr.indexOf(str.charAt(j)) << 4);
			low = (byte)hexStr.indexOf(str.charAt(j + 1));
			bytes[i] = (byte)(high | low);
		}
		
		return bytes;
	}
	
}
