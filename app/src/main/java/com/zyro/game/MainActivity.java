package com.zyro.game;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import java.net.HttpURLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends Activity {
    private final int BLUE = Color.rgb(28,105,235), BLUE_DARK = Color.rgb(18,72,178);
    private static final String API_BASE_URL = "https://YOUR-BACKEND-DOMAIN";
    private final int BG = Color.rgb(244,248,255), INK = Color.rgb(24,42,72), MUTED = Color.rgb(111,130,158), WHITE = Color.WHITE;
    private LinearLayout content;
    private TextView balanceText, historyText;
    private boolean balanceVisible = true;
    private final ArrayList<String> transactions = new ArrayList<>();

    @Override public void onCreate(Bundle state) {
        super.onCreate(state);
        getWindow().setStatusBarColor(BG);
        getWindow().setNavigationBarColor(BG);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        buildHome();
    }
    private GradientDrawable shape(int color, int radius) {
        GradientDrawable d = new GradientDrawable(); d.setColor(color); d.setCornerRadius(radius); return d;
    }
    private TextView label(String value, int size, int color, boolean bold) {
        TextView t = new TextView(this); t.setText(value); t.setTextSize(size); t.setTextColor(color);
        t.setTypeface(Typeface.DEFAULT, bold ? Typeface.BOLD : Typeface.NORMAL); t.setGravity(Gravity.CENTER_VERTICAL); return t;
    }
    private LinearLayout column() { LinearLayout l = new LinearLayout(this); l.setOrientation(LinearLayout.VERTICAL); return l; }
    private LinearLayout.LayoutParams lp(int w, int h, int top, int bottom) {
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(w,h); p.setMargins(0,top,0,bottom); return p;
    }
    private TextView centered(String s,int size,int color,boolean bold) {
        TextView t=label(s,size,color,bold); t.setGravity(Gravity.CENTER); return t;
    }
    private void buildHome() {
        LinearLayout root=column(); root.setBackgroundColor(BG);
        ScrollView scroll=new ScrollView(this); scroll.setFillViewport(true);
        content=column(); content.setPadding(18,12,18,24); scroll.addView(content);
        root.addView(scroll,new LinearLayout.LayoutParams(-1,-1)); setContentView(root);

        LinearLayout header=new LinearLayout(this); header.setGravity(Gravity.CENTER_VERTICAL);
        TextView logo=centered("Z",25,WHITE,true); logo.setBackground(shape(BLUE,16));
        header.addView(logo,new LinearLayout.LayoutParams(48,48));
        LinearLayout brand=column(); brand.addView(label("Zyro Wallet",21,INK,true)); brand.addView(label("กระเป๋าเงินดิจิทัล",12,MUTED,false));
        LinearLayout.LayoutParams bp=new LinearLayout.LayoutParams(0,-2,1); bp.setMargins(12,0,0,0); header.addView(brand,bp);
        Button profile=smallButton("โปรไฟล์"); profile.setOnClickListener(v->info("โปรไฟล์","เวอร์ชันทดสอบนี้ยังไม่มีบัญชีผู้ใช้จริงหรือการยืนยันตัวตน")); header.addView(profile);
        content.addView(header,lp(-1,-2,6,18));

        LinearLayout wallet=column(); wallet.setPadding(20,20,20,20); wallet.setBackground(shape(BLUE,24));
        LinearLayout row=new LinearLayout(this); row.setGravity(Gravity.CENTER_VERTICAL);
        row.addView(label("ยอดเงินในกระเป๋า",14,WHITE,false),new LinearLayout.LayoutParams(0,-2,1));
        Button eye=new Button(this); eye.setText("◉"); eye.setTextColor(WHITE); eye.setTextSize(17); eye.setAllCaps(false); eye.setBackground(shape(BLUE_DARK,14));
        eye.setOnClickListener(v->{balanceVisible=!balanceVisible;updateBalance();}); row.addView(eye,new LinearLayout.LayoutParams(46,42)); wallet.addView(row);
        balanceText=label("",30,WHITE,true); wallet.addView(balanceText,lp(-1,-2,8,2));
        wallet.addView(label("THB  •  กระเป๋าทดลอง",12,Color.rgb(218,233,255),false));
        wallet.addView(label("ⓘ  ยังไม่เชื่อมต่อระบบเงินจริง",12,Color.rgb(255,244,196),true),lp(-1,-2,14,0));
        content.addView(wallet,lp(-1,-2,0,20)); updateBalance();

        content.addView(label("บริการของฉัน",18,INK,true),lp(-1,-2,0,10));
        LinearLayout services=new LinearLayout(this); services.setGravity(Gravity.CENTER);
        services.addView(service("▦","สแกนจ่าย",()->info("สแกน QR","การสแกนและชำระเงินจริงยังไม่พร้อมใช้งาน กรุณารอการเชื่อมต่อผู้ให้บริการชำระเงิน")));
        services.addView(service("↗","โอนเงิน",()->transferDialog()));
        services.addView(service("＋","เติมเงิน",()->info("เติมเงิน","ยังไม่สามารถเติมเงินเข้ากระเป๋าได้ เพราะยังไม่มีผู้ให้บริการเงินจริง")));
        services.addView(service("⇩","รับเงิน",()->info("รับเงิน","QR รับเงินจริงจะเปิดใช้งานหลังตั้งค่าระบบบัญชีและผู้ให้บริการ")));
        content.addView(services,lp(-1,108,0,20));

        LinearLayout promo=column(); promo.setPadding(16,16,16,16); promo.setBackground(shape(WHITE,20));
        promo.addView(label("บัญชีของคุณปลอดภัย",16,INK,true));
        promo.addView(label("ก่อนใช้งานเงินจริง ต้องเชื่อมต่อผู้ให้บริการที่ได้รับอนุญาตและระบบยืนยันตัวตน",13,MUTED,false),lp(-1,-2,6,0));
        content.addView(promo,lp(-1,-2,0,18));

        LinearLayout security=column(); security.setPadding(16,16,16,16); security.setBackground(shape(WHITE,20));
        security.addView(label("🛡️  AI ผู้ช่วยและตรวจสอบภัย",16,INK,true));
        security.addView(label("ตรวจลิงก์น่าสงสัยและถามคำถามด้านความปลอดภัยออนไลน์",13,MUTED,false),lp(-1,-2,5,10));
        Button secureBtn=smallButton("เปิด AI & ตรวจภัย"); secureBtn.setOnClickListener(v->securityMenu()); security.addView(secureBtn,new LinearLayout.LayoutParams(-1,48));
        content.addView(security,lp(-1,-2,0,18));

        LinearLayout hh=new LinearLayout(this); hh.setGravity(Gravity.CENTER_VERTICAL);
        hh.addView(label("รายการล่าสุด",18,INK,true),new LinearLayout.LayoutParams(0,-2,1));
        Button all=smallButton("ดูทั้งหมด"); all.setOnClickListener(v->showHistory()); hh.addView(all);
        content.addView(hh,lp(-1,-2,0,10));
        historyText=label("ยังไม่มีรายการ",14,MUTED,false); historyText.setGravity(Gravity.TOP); historyText.setPadding(16,16,16,16); historyText.setBackground(shape(WHITE,18));
        content.addView(historyText,lp(-1,-2,0,18));
        content.addView(centered("ZYRO WALLET  •  เวอร์ชันทดสอบ",11,MUTED,false),lp(-1,-2,8,0));
    }
    private View service(String icon,String title,Runnable action) {
        LinearLayout item=column(); item.setGravity(Gravity.CENTER);
        TextView glyph=centered(icon,25,BLUE,true); glyph.setBackground(shape(Color.rgb(225,237,255),18)); item.addView(glyph,new LinearLayout.LayoutParams(58,58));
        item.addView(centered(title,12,INK,true),lp(-1,-2,7,0)); item.setOnClickListener(v->action.run()); return item;
    }
    private Button smallButton(String title) {
        Button b=new Button(this); b.setText(title); b.setTextSize(12); b.setTextColor(BLUE); b.setAllCaps(false); b.setPadding(10,0,10,0); b.setBackground(shape(Color.rgb(229,239,255),14)); return b;
    }
    private void updateBalance() { if(balanceText!=null) balanceText.setText(balanceVisible?"฿ 0.00":"฿ ••••••"); }
    private void transferDialog() {
        LinearLayout form=column(); form.setPadding(4,8,4,0);
        EditText recipient=new EditText(this); recipient.setHint("รหัสผู้รับทดลอง (เช่น ZYRO123)"); recipient.setSingleLine(true); form.addView(recipient);
        EditText amount=new EditText(this); amount.setHint("จำนวนเงิน (บาท)"); amount.setInputType(InputType.TYPE_CLASS_NUMBER|InputType.TYPE_NUMBER_FLAG_DECIMAL); form.addView(amount);
        new AlertDialog.Builder(this).setTitle("ทดลองโอนเงิน")
        .setMessage("โหมดทดลองเท่านั้น ไม่ส่งเงินจริงและไม่ติดต่อธนาคาร").setView(form).setNegativeButton("ยกเลิก",null)
        .setPositiveButton("บันทึกรายการทดลอง",(dialog,which)->{
            String to=recipient.getText().toString().trim(), amt=amount.getText().toString().trim();
            if(to.isEmpty()||amt.isEmpty()){Toast.makeText(this,"กรุณากรอกผู้รับและจำนวนเงิน",Toast.LENGTH_LONG).show();return;}
            try {
                double value=Double.parseDouble(amt); if(value<=0||value>1000000) throw new NumberFormatException();
                String time=new SimpleDateFormat("dd/MM HH:mm",Locale.getDefault()).format(new Date());
                transactions.add(0,"↗  โอนทดลองให้ "+to+"\n     ฿ "+String.format(Locale.getDefault(),"%.2f",value)+"  •  "+time);
                refreshHistory(); Toast.makeText(this,"บันทึกเป็นรายการทดลองแล้ว — ไม่มีการโอนเงินจริง",Toast.LENGTH_LONG).show();
            } catch(NumberFormatException e){Toast.makeText(this,"จำนวนเงินไม่ถูกต้อง",Toast.LENGTH_LONG).show();}
        }).show();
    }
    private void refreshHistory() {
        if(historyText==null)return; if(transactions.isEmpty()){historyText.setText("ยังไม่มีรายการ");return;}
        StringBuilder sb=new StringBuilder(); for(int i=0;i<Math.min(5,transactions.size());i++){if(i>0)sb.append("\n\n");sb.append(transactions.get(i));} historyText.setText(sb.toString());
    }
    private void showHistory() {
        if(transactions.isEmpty()){info("ประวัติรายการ","ยังไม่มีรายการทดลอง");return;}
        StringBuilder sb=new StringBuilder();for(String item:transactions)sb.append(item).append("\n\n");info("ประวัติรายการทดลอง",sb.toString());
    }

    private void securityMenu() {
        String[] options={"ตรวจสอบ URL / ลิงก์","ถาม AI เรื่องความปลอดภัย","ข้อควรระวัง"};
        new AlertDialog.Builder(this).setTitle("AI ผู้ช่วยและตรวจสอบภัย").setItems(options,(d,which)->{
            if(which==0) scanUrlDialog();
            else if(which==1) aiQuestionDialog();
            else info("ข้อควรระวัง","อย่ากรอกรหัสผ่าน, OTP, PIN หรือ API key ในแชต/ลิงก์ที่ไม่รู้จัก แอปนี้ไม่สามารถสแกนไวรัสทั้งเครื่องแทนระบบป้องกันของ Android ได้");
        }).show();
    }
    private void scanUrlDialog() {
        EditText urlInput=new EditText(this); urlInput.setHint("https://example.com"); urlInput.setSingleLine(true);
        new AlertDialog.Builder(this).setTitle("ตรวจสอบลิงก์").setMessage("ลิงก์ที่ส่งไปยังบริการตรวจภัยภายนอกอาจถูกจัดเก็บหรือแชร์เพื่อการวิเคราะห์ ห้ามส่งลิงก์ส่วนตัวหรือข้อมูลลับ")
        .setView(urlInput).setNegativeButton("ยกเลิก",null).setPositiveButton("ตรวจสอบ",(d,w)->{
            String url=urlInput.getText().toString().trim();
            if(!(url.startsWith("https://")||url.startsWith("http://"))){info("ลิงก์ไม่ถูกต้อง","กรุณาใส่ URL ที่ขึ้นต้นด้วย https:// หรือ http://");return;}
            callBackend("/api/security/url", "{\"url\":\""+jsonEscape(url)+"\"}", "ผลตรวจลิงก์");
        }).show();
    }
    private void aiQuestionDialog() {
        EditText question=new EditText(this); question.setHint("เช่น ลิงก์นี้ดูน่าสงสัยอย่างไร?");
        question.setMinLines(2);
        new AlertDialog.Builder(this).setTitle("ถาม AI ด้านความปลอดภัย")
        .setMessage("อย่าส่งรหัสผ่าน, OTP, ข้อมูลบัญชี หรือข้อมูลส่วนตัว")
        .setView(question).setNegativeButton("ยกเลิก",null).setPositiveButton("ถาม AI",(d,w)->{
            String q=question.getText().toString().trim();
            if(q.isEmpty()){info("ยังไม่มีคำถาม","กรุณาพิมพ์คำถามก่อน");return;}
            callBackend("/api/assistant", "{\"question\":\""+jsonEscape(q)+"\"}", "คำตอบจาก AI");
        }).show();
    }
    private String jsonEscape(String s) {
        return s.replace("\\","\\\\").replace("\"","\\\"").replace("\n","\\n").replace("\r","\\r");
    }
    private void callBackend(String path,String body,String title) {
        if("https://YOUR-BACKEND-DOMAIN".equals(API_BASE_URL)) {
            info("ยังไม่ได้เชื่อม API จริง","ต้องนำ backend ในโฟลเดอร์ backend ไปติดตั้งบนเซิร์ฟเวอร์ HTTPS แล้วตั้งค่า API_BASE_URL ในแอปก่อนใช้งาน");
            return;
        }
        Toast.makeText(this,"กำลังเชื่อมต่อบริการ...",Toast.LENGTH_SHORT).show();
        new Thread(()->{
            HttpURLConnection conn=null;
            try {
                java.net.URL endpoint=new java.net.URL(API_BASE_URL+path);
                conn=(HttpURLConnection)endpoint.openConnection();
                conn.setRequestMethod("POST"); conn.setConnectTimeout(12000); conn.setReadTimeout(20000);
                conn.setDoOutput(true); conn.setRequestProperty("Content-Type","application/json; charset=UTF-8");
                byte[] bytes=body.getBytes(java.nio.charset.StandardCharsets.UTF_8);
                conn.getOutputStream().write(bytes);
                int status=conn.getResponseCode();
                java.io.InputStream stream=status>=200&&status<300?conn.getInputStream():conn.getErrorStream();
                java.io.ByteArrayOutputStream out=new java.io.ByteArrayOutputStream();
                byte[] buf=new byte[2048]; int n; while(stream!=null&&(n=stream.read(buf))!=-1)out.write(buf,0,n);
                String raw=new String(out.toByteArray(),java.nio.charset.StandardCharsets.UTF_8);
                org.json.JSONObject json=new org.json.JSONObject(raw);
                String result=json.optString("result",json.optString("answer",json.optString("message",raw)));
                final String message=status>=200&&status<300?result:"บริการตอบกลับผิดพลาด ("+status+"): "+result;
                runOnUiThread(()->info(title,message));
            } catch(Exception e) {
                runOnUiThread(()->info("เชื่อมต่อไม่ได้","ตรวจสอบว่า backend เปิดใช้งานแล้ว ตั้งค่า API_BASE_URL ถูกต้อง และเซิร์ฟเวอร์เข้าถึงได้"));
            } finally { if(conn!=null)conn.disconnect(); }
        }).start();
    }

    private void info(String title,String message){new AlertDialog.Builder(this).setTitle(title).setMessage(message).setPositiveButton("ตกลง",null).show();}
}
