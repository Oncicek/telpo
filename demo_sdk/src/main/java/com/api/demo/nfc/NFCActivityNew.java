package com.api.demo.nfc;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcEvent;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.common.apiutil.util.StringUtil;
import com.common.apiutil.util.SystemUtil;
import com.common.demo.R;
import com.common.demo.databinding.ActivityNfcNewBinding;

/**
 * NFC 拉起页面
 */
public class NFCActivityNew extends Activity implements NfcAdapter.CreateNdefMessageCallback, NfcAdapter.ReaderCallback {

    //支持的标签类型
    private NfcAdapter nfcAdapter;
    private ActivityNfcNewBinding binding;
    int successCount = 0;

    private MyReceiver myReceiver;

    private final static String NFC_RESET_FINISH_ACTION = "android.intent.action.NFC_RESET_FINISH";
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        binding = ActivityNfcNewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (nfcAdapter == null) {
            Toast.makeText(this, "该机型不支持NFC", Toast.LENGTH_LONG).show();
            finish();
        }
        // Register callback  *设置一个回调，使用Android Beam（TM）动态生成要发送的NDEF消息。
        nfcAdapter.setNdefPushMessageCallback(this, this);
        nfcAdapter.enableReaderMode(this, this, NfcAdapter.FLAG_READER_NFC_A | NfcAdapter.FLAG_READER_SKIP_NDEF_CHECK, null);
        binding.boundingBox.setVisibility(View.GONE);
        if(SystemUtil.getInternalModel().equals("C9") ||
                SystemUtil.getInternalModel().equals("C11") ||
                SystemUtil.getInternalModel().equals("C2")){
            binding.boundingBox.setVisibility(View.VISIBLE);

            Toast.makeText(this, getString(R.string.nfc_area_text), Toast.LENGTH_SHORT).show();
        }

        myReceiver = new MyReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(NFC_RESET_FINISH_ACTION);

        // 注册广播接收器
        registerReceiver(myReceiver, intentFilter);

    }

    public class MyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            // 处理接收到的广播事件
            if (intent.getAction().equals(NFC_RESET_FINISH_ACTION)) {
                if(nfcAdapter != null) {
                    nfcAdapter.disableReaderMode(NFCActivityNew.this);
                    nfcAdapter = NfcAdapter.getDefaultAdapter(NFCActivityNew.this);

                    // Register callback  *设置一个回调，使用Android Beam（TM）动态生成要发送的NDEF消息。
                    nfcAdapter.setNdefPushMessageCallback(NFCActivityNew.this, NFCActivityNew.this);
                    nfcAdapter.enableReaderMode(NFCActivityNew.this, NFCActivityNew.this, NfcAdapter.FLAG_READER_NFC_A | NfcAdapter.FLAG_READER_SKIP_NDEF_CHECK, null);
                }
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Check to see that the Activity started due to an Android Beam
        if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(getIntent().getAction())) {
            processIntent(getIntent());
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        nfcAdapter.disableReaderMode(this);

        // 解注册广播接收器
        if(myReceiver != null) {
            unregisterReceiver(myReceiver);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }

    /**
     * 处理Intent携带的数据
     */
    private void processIntent(Intent intent) {
        Log.d("tagg","nfc processIntent");
        long startTime = System.currentTimeMillis();
//        Bundle tag = intent.getExtras();
        Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        if (tag == null) return;

//        String content = StringUtil.toHexString(((Tag) tag.get("android.nfc.extra.TAG")).getId());

        String[] techList = tag.getTechList();
        String data = tag.toString();

        byte[] ID =  tag.getId();
        if(ID != null && !data.isEmpty()){
            data += "\n\nUID:\n" +StringUtil.toHexString(ID);
            data += "\nData format:";
            for (String tech : techList) {
                data += "\n" + tech;
            }
            successCount++;
        }
        long endTime = System.currentTimeMillis();
        binding.successCount.setText(getString(R.string.nfc_tv_scann_count) + ": "+successCount + "\n" + getString(R.string.nfc_tv_scann_time) + "：" + (endTime - startTime) + " ms");
        binding.tvShowNfc.setText(data);
//        Toast.makeText(this, "获取NFC数据:" + data, Toast.LENGTH_LONG).show();
    }

    @Override
    public NdefMessage createNdefMessage(NfcEvent event) {
        String text = "Beam me up, Android!\n\n" +
                "Beam Time: " + System.currentTimeMillis();
        return new NdefMessage(
                new NdefRecord[]{
                        NdefRecord.createMime("application/vnd.com.example.android.beam", text.getBytes())
                }
        );
    }

    @Override
    public void onTagDiscovered(Tag tag) {
        Log.d("tagg","nfc onTagDiscovered");
        long startTime = System.currentTimeMillis();

        String[] techList = tag.getTechList();
        String data = tag.toString();
        byte[] ID =  tag.getId();
        if(ID != null && !data.isEmpty()){
            data += "\n\nUID:\n" +StringUtil.toHexString(ID);
            data += "\nData format:";
            for (String tech : techList) {
                data += "\n" + tech;
            }
            successCount++;
        }
        long endTime = System.currentTimeMillis();
        String finalData = data;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                binding.successCount.setText(getString(R.string.nfc_tv_scann_count) + ": "+successCount + "\n" + getString(R.string.nfc_tv_scann_time) + "：" + (endTime - startTime) + " ms");
                binding.tvShowNfc.setText(finalData);
            }
        });
    }
}
