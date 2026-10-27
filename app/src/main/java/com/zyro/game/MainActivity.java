package com.zyro.game;

import android.app.Activity;
import android.os.Bundle;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.content.SharedPreferences;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import java.util.Random;

public class MainActivity extends Activity {
    int level, xp, coins, hp, maxHp, potions, wins, enemyHp, enemyMax, questKills;
    String enemy;
    boolean questClaimed;
    Random random = new Random();
    SharedPreferences save;
    LinearLayout root, content;
    TextView stats, log, enemyView, questView;
    final int BG = Color.rgb(10, 13, 25), PANEL = Color.rgb(24, 30, 52), CYAN = Color.rgb(71, 224, 255), WHITE = Color.rgb(240, 244, 255), MUTED = Color.rgb(164, 177, 204);

    @Override public void onCreate(Bundle b) {
        super.onCreate(b);
        save = getSharedPreferences("zyro_save", MODE_PRIVATE);
        level=save.getInt("level",1); xp=save.getInt("xp",0); coins=save.getInt("coins",20);
        maxHp=save.getInt("maxHp",100); hp=save.getInt("hp",maxHp); potions=save.getInt("potions",3);
        wins=save.getInt("wins",0); questKills=save.getInt("questKills",0); questClaimed=save.getBoolean("questClaimed",false);
        if(hp<=0) hp=maxHp;
        spawnEnemy(); buildUi(); render();
    }
    GradientDrawable shape(int color, int radius) {
        GradientDrawable d=new GradientDrawable(); d.setColor(color); d.setCornerRadius(radius); d.setStroke(1, Color.rgb(55,68,99)); return d;
    }
    TextView text(String s,int size,int color,boolean bold) {
        TextView t=new TextView(this); t.setText(s); t.setTextSize(size); t.setTextColor(color);
        t.setTypeface(Typeface.DEFAULT, bold?Typeface.BOLD:Typeface.NORMAL); t.setGravity(Gravity.CENTER_VERTICAL);
        t.setPadding(14,8,14,8); return t;
    }
    void buildUi() {
        root=new LinearLayout(this); root.setOrientation(LinearLayout.VERTICAL); root.setBackgroundColor(BG);
        ScrollView scroll=new ScrollView(this); scroll.setFillViewport(true);
        content=new LinearLayout(this); content.setOrientation(LinearLayout.VERTICAL); content.setPadding(16,12,16,24);
        scroll.addView(content); root.addView(scroll,new LinearLayout.LayoutParams(-1,-1)); setContentView(root);
        TextView title=text("✦ ZYRO GAME",27,CYAN,true); title.setGravity(Gravity.CENTER); content.addView(title);
        TextView sub=text("FANTASY RPG  •  ADVENTURE BEGINS",11,MUTED,true); sub.setGravity(Gravity.CENTER); content.addView(sub);
        stats=text("",15,WHITE,true); stats.setBackground(shape(PANEL,18)); content.addView(stats,margin(0,14,0,8));
        TextView heading=text("⚔  BATTLE ARENA",19,WHITE,true); content.addView(heading);
        enemyView=text("",17,WHITE,true); enemyView.setGravity(Gravity.CENTER); enemyView.setMinHeight(100); enemyView.setBackground(shape(Color.rgb(36,27,54),18)); content.addView(enemyView,margin(0,8,0,8));
        Button attack=button("⚔  โจมตี"); attack.setOnClickListener(v->attack());
        Button special=button("✦  สกิลพิเศษ  (ใช้ 10 XP)"); special.setOnClickListener(v->special());
        Button potion=button("♥  ใช้ยาเพิ่ม HP"); potion.setOnClickListener(v->heal());
        content.addView(attack,margin(0,4,0,4)); content.addView(special,margin(0,4,0,4)); content.addView(potion,margin(0,4,0,12));
        content.addView(text("📜 ภารกิจนักผจญภัย",19,CYAN,true));
        questView=text("",14,WHITE,false); questView.setBackground(shape(PANEL,16)); content.addView(questView,margin(0,6,0,4));
        Button quest=button("🎁 รับรางวัลภารกิจ"); quest.setOnClickListener(v->claimQuest()); content.addView(quest,margin(0,4,0,10));
        content.addView(text("📖 บันทึกการผจญภัย",18,CYAN,true));
        log=text("ยินดีต้อนรับ นักผจญภัย! แตะโจมตีเพื่อเริ่มต่อสู้",14,WHITE,false); log.setGravity(Gravity.TOP); log.setMinHeight(76); log.setBackground(shape(PANEL,16)); content.addView(log,margin(0,6,0,10));
        Button rest=button("🏕 พักฟื้นฟรี"); rest.setOnClickListener(v->{hp=maxHp; setLog("พักฟื้นเรียบร้อย HP กลับมาเต็มแล้ว"); render(); save();}); content.addView(rest,margin(0,3,0,4));
        Button reset=button("↻ เริ่มเกมใหม่"); reset.setOnClickListener(v->resetGame()); content.addView(reset,margin(0,3,0,4));
        TextView foot=text("ZYRO • Your journey, your legend",11,MUTED,false); foot.setGravity(Gravity.CENTER); content.addView(foot);
    }
    LinearLayout.LayoutParams margin(int l,int t,int r,int b){ LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(-1,-2); p.setMargins(l,t,r,b); return p; }
    Button button(String label){ Button b=new Button(this); b.setText(label); b.setTextColor(WHITE); b.setTextSize(15); b.setAllCaps(false); b.setBackground(shape(Color.rgb(30,43,70),16)); return b; }
    void spawnEnemy(){ String[] names={"Slime แห่งเงา","หมาป่าจันทรา","ก็อบลินนักรบ","อัศวินรัตติกาล","มังกรน้อย"}; enemy=names[random.nextInt(names.length)]; enemyMax=35+level*15+random.nextInt(15); enemyHp=enemyMax; }
    void attack(){ if(enemyHp<=0){spawnEnemy();} int dmg=8+level*4+random.nextInt(10); enemyHp=Math.max(0,enemyHp-dmg); String msg="คุณโจมตี "+enemy+" สร้างความเสียหาย "+dmg+"!"; if(enemyHp==0){victory();}else{int hit=3+random.nextInt(7)+level; hp=Math.max(1,hp-hit); msg+="\n"+enemy+" โต้กลับ -"+hit+" HP";} setLog(msg); render(); save(); }
    void special(){ if(xp<10){setLog("EXP ยังไม่พอ! ต้องมีอย่างน้อย 10 XP");return;} xp-=10; int dmg=22+level*7; enemyHp=Math.max(0,enemyHp-dmg); String msg="✦ สกิล Zyro Burst! สร้างความเสียหาย "+dmg+"!"; if(enemyHp==0) victory(); else {int hit=5+level;hp=Math.max(1,hp-hit);msg+="\nศัตรูสวนกลับ -"+hit+" HP";} setLog(msg);render();save(); }
    void heal(){ if(potions<=0){setLog("ยาหมดแล้ว! ชนะศัตรูเพื่อรับเหรียญไปซื้อยาไม่ได้ในเวอร์ชันนี้");return;} if(hp==maxHp){setLog("HP เต็มอยู่แล้ว เก็บยาไว้ก่อนนะ");return;} int before=hp;hp=Math.min(maxHp,hp+40);potions--;setLog("ใช้ยา ฟื้นฟู "+(hp-before)+" HP");render();save(); }
    void victory(){ wins++;questKills++;coins+=10+level*3;xp+=15+level*5;setLog("🏆 ชนะ "+enemy+"! ได้เหรียญและ EXP แล้ว"); while(xp>=level*30){xp-=level*30;level++;maxHp+=20;hp=maxHp;coins+=15;} spawnEnemy(); }
    void claimQuest(){ if(questClaimed){setLog("รับรางวัลภารกิจนี้ไปแล้ว ลองเล่นต่อเพื่อสะสมชัยชนะ!");return;} if(questKills<3){setLog("ภารกิจยังไม่สำเร็จ: ต้องชนะศัตรู 3 ตัว ("+questKills+"/3)");return;} questClaimed=true;coins+=50;potions+=2;setLog("🎉 สำเร็จภารกิจ! ได้ 50 เหรียญ และยา 2 ขวด");render();save(); }
    void resetGame(){level=1;xp=0;coins=20;maxHp=100;hp=100;potions=3;wins=0;questKills=0;questClaimed=false;spawnEnemy();setLog("เริ่มการผจญภัยใหม่แล้ว! ขอให้โชคดี");render();save();}
    void setLog(String s){if(log!=null)log.setText(s);}
    void render(){if(stats==null)return;stats.setText("  ⭐ เลเวล "+level+"     ✨ EXP "+xp+"/"+(level*30)+"\n  ♥ HP "+hp+"/"+maxHp+"     🪙 "+coins+" เหรียญ\n  🧪 ยาฟื้นฟู "+potions+"     🏆 ชนะ "+wins+" ครั้ง");enemyView.setText("👾  "+enemy+"\n♥ HP: "+enemyHp+"/"+enemyMax+"\n\nแตะโจมตีเพื่อสู้!");questView.setText("  ภารกิจ: ชนะมอนสเตอร์ 3 ตัว\n  ความคืบหน้า: "+Math.min(questKills,3)+"/3"+(questClaimed?"\n  สถานะ: รับรางวัลแล้ว":"\n  รางวัล: 50 เหรียญ + ยา 2 ขวด"));}
    void save(){save.edit().putInt("level",level).putInt("xp",xp).putInt("coins",coins).putInt("maxHp",maxHp).putInt("hp",hp).putInt("potions",potions).putInt("wins",wins).putInt("questKills",questKills).putBoolean("questClaimed",questClaimed).apply();}
}
