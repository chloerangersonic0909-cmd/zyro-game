import React, { useMemo, useState } from 'react';
import { Alert, KeyboardAvoidingView, Modal, Platform, Pressable, SafeAreaView, ScrollView, StatusBar, StyleSheet, Text, TextInput, View, ActivityIndicator } from 'react-native';
import Constants from 'expo-constants';

const API_BASE_URL = String(Constants.expoConfig?.extra?.apiBaseUrl || '').trim().replace(/\/$/, '');
const BLUE = '#246BEB';
const INK = '#172B4D';
const MUTED = '#71829B';

function ActionButton({ title, onPress, secondary = false }) {
  return <Pressable accessibilityRole="button" onPress={onPress} style={[styles.actionButton, secondary && styles.secondaryButton]}><Text style={[styles.actionText, secondary && styles.secondaryText]}>{title}</Text></Pressable>;
}
function SectionTitle({ children }) { return <Text style={styles.sectionTitle}>{children}</Text>; }

export default function App() {
  const [balanceVisible, setBalanceVisible] = useState(true);
  const [modal, setModal] = useState('');
  const [input, setInput] = useState('');
  const [busy, setBusy] = useState(false);
  const [answer, setAnswer] = useState('');
  const [history] = useState([
    { id: '1', title: 'ยอดเริ่มต้น (ตัวอย่าง)', detail: 'รายการจำลอง · ไม่ใช่เงินจริง', amount: '+ ฿1,250.00', positive: true },
    { id: '2', title: 'ทดสอบกระเป๋าเงิน', detail: 'รายการจำลอง', amount: '฿0.00', positive: false }
  ]);
  const demoBalance = useMemo(() => '฿1,250.00', []);

  const showNotLive = () => Alert.alert('โหมดทดลอง', 'ฟีเจอร์การเงินยังไม่เชื่อมผู้ให้บริการที่ได้รับอนุญาต จึงไม่มีการรับ โอน หรือเก็บเงินจริง');
  const openModal = (kind) => { setInput(''); setAnswer(''); setModal(kind); };
  const sendRequest = async () => {
    if (!API_BASE_URL) {
      Alert.alert('ยังไม่ได้เชื่อมต่อ API', 'ตั้งค่า EXPO_PUBLIC_API_BASE_URL ใน GitHub Actions แล้วสร้าง APK ใหม่ก่อนใช้งาน AI หรือสแกน URL');
      return;
    }
    if (!input.trim()) { Alert.alert('กรุณากรอกข้อมูล', modal === 'ai' ? 'พิมพ์คำถามก่อนส่ง' : 'กรอก URL ที่ต้องการตรวจสอบ'); return; }
    if (modal === 'url') {
      const ok = await new Promise(resolve => Alert.alert('แจ้งเตือนความเป็นส่วนตัว', 'URL ที่ส่งไปยัง VirusTotal อาจถูกแชร์ในชุดข้อมูลความปลอดภัย ห้ามส่งลิงก์ส่วนตัวหรือลิงก์รีเซ็ตรหัสผ่าน', [{text:'ยกเลิก',style:'cancel',onPress:()=>resolve(false)},{text:'ยอมรับและตรวจสอบ',onPress:()=>resolve(true)}]));
      if (!ok) return;
    }
    setBusy(true); setAnswer('');
    try {
      const endpoint = modal === 'ai' ? '/api/assistant' : '/api/security/url';
      const payload = modal === 'ai' ? { question: input.trim() } : { url: input.trim() };
      const response = await fetch(API_BASE_URL + endpoint, { method:'POST', headers:{'Content-Type':'application/json'}, body:JSON.stringify(payload) });
      const data = await response.json();
      if (!response.ok) throw new Error(data.message || 'บริการไม่พร้อมใช้งาน');
      const text = modal === 'ai' ? data.answer : (data.message || JSON.stringify(data, null, 2));
      setAnswer(String(text || 'ไม่มีข้อมูลตอบกลับ'));
    } catch (error) {
      setAnswer('เชื่อมต่อไม่สำเร็จ: ' + (error.message || 'เกิดข้อผิดพลาด'));
    } finally { setBusy(false); }
  };

  return <SafeAreaView style={styles.safe}>
    <StatusBar barStyle="dark-content" backgroundColor="#F4F8FF" />
    <ScrollView contentContainerStyle={styles.page}>
      <View style={styles.header}>
        <View style={styles.logo}><Text style={styles.logoText}>Z</Text></View>
        <View style={{flex:1}}><Text style={styles.brand}>Zyro Wallet</Text><Text style={styles.subBrand}>กระเป๋าเงินดิจิทัล</Text></View>
        <Pressable style={styles.profile} onPress={() => Alert.alert('โปรไฟล์', 'เวอร์ชันทดลองยังไม่มีบัญชีผู้ใช้หรือระบบยืนยันตัวตนจริง')}><Text style={styles.profileText}>โปรไฟล์</Text></Pressable>
      </View>

      <View style={styles.balanceCard}>
        <View style={styles.balanceTop}><Text style={styles.balanceLabel}>ยอดเงินคงเหลือ (ตัวอย่าง)</Text><Pressable onPress={() => setBalanceVisible(!balanceVisible)}><Text style={styles.eye}>{balanceVisible ? 'ซ่อน' : 'แสดง'}</Text></Pressable></View>
        <Text style={styles.balance}>{balanceVisible ? demoBalance : '฿ ••••••'}</Text>
        <Text style={styles.demoNotice}>ยอดเงินจำลองเท่านั้น · ไม่มีเงินจริง</Text>
        <View style={styles.actions}><ActionButton title="＋ เติมเงิน" onPress={showNotLive}/><ActionButton title="↗ โอนเงิน" onPress={showNotLive} secondary/><ActionButton title="▦ รับเงิน" onPress={showNotLive} secondary/></View>
      </View>

      <SectionTitle>บริการของ Zyro</SectionTitle>
      <View style={styles.serviceGrid}>
        <Pressable style={styles.service} onPress={() => openModal('ai')}><Text style={styles.serviceIcon}>✦</Text><Text style={styles.serviceTitle}>AI ผู้ช่วย</Text><Text style={styles.serviceDesc}>ถามเรื่องมิจฉาชีพและความปลอดภัย</Text></Pressable>
        <Pressable style={styles.service} onPress={() => openModal('url')}><Text style={styles.serviceIcon}>⌕</Text><Text style={styles.serviceTitle}>ตรวจสอบ URL</Text><Text style={styles.serviceDesc}>ตรวจชื่อเสียงลิงก์ผ่าน API</Text></Pressable>
      </View>
      <View style={styles.infoCard}><Text style={styles.infoTitle}>🛡️ ความปลอดภัย</Text><Text style={styles.infoBody}>การตรวจ URL ไม่ได้สแกนทั้งเครื่อง และไม่รับประกันว่าลิงก์ปลอดภัย ควรเปิด Google Play Protect ไว้เสมอ</Text></View>

      <View style={styles.historyHeader}><SectionTitle>ประวัติรายการ</SectionTitle><Text style={styles.demoTag}>DEMO</Text></View>
      {history.map(item => <View key={item.id} style={styles.historyItem}><View style={styles.historyIcon}><Text style={{color:BLUE,fontSize:19}}>↔</Text></View><View style={{flex:1}}><Text style={styles.historyTitle}>{item.title}</Text><Text style={styles.historyDetail}>{item.detail}</Text></View><Text style={[styles.amount, item.positive && {color:'#159567'}]}>{item.amount}</Text></View>)}
      <Text style={styles.footer}>Zyro Wallet · เวอร์ชันทดลอง</Text>
    </ScrollView>

    <Modal visible={!!modal} animationType="slide" transparent onRequestClose={() => setModal('')}>
      <KeyboardAvoidingView style={styles.modalShade} behavior={Platform.OS === 'ios' ? 'padding' : undefined}>
        <View style={styles.modalCard}>
          <View style={styles.modalHead}><Text style={styles.modalTitle}>{modal === 'ai' ? '✦ AI ผู้ช่วยด้านความปลอดภัย' : '⌕ ตรวจสอบ URL'}</Text><Pressable onPress={() => setModal('')}><Text style={styles.close}>ปิด</Text></Pressable></View>
          <Text style={styles.modalHint}>{modal === 'ai' ? 'ถามเกี่ยวกับการหลอกลวง บัญชี และความปลอดภัย ห้ามส่งรหัสผ่าน PIN หรือ OTP' : 'ใส่ URL แบบ https://... ห้ามใช้ลิงก์ส่วนตัวหรือลิงก์รีเซ็ตรหัสผ่าน'}</Text>
          <TextInput value={input} onChangeText={setInput} placeholder={modal === 'ai' ? 'พิมพ์คำถาม...' : 'https://example.com'} placeholderTextColor="#8A9AB0" multiline={modal === 'ai'} autoCapitalize="none" autoCorrect={false} keyboardType={modal === 'url' ? 'url' : 'default'} style={styles.input}/>
          <ActionButton title={busy ? 'กำลังตรวจสอบ…' : modal === 'ai' ? 'ส่งคำถาม' : 'ตรวจสอบลิงก์'} onPress={sendRequest}/>
          {busy && <ActivityIndicator style={{marginTop:12}} color={BLUE}/>}
          {!!answer && <ScrollView style={styles.answerBox}><Text selectable style={styles.answerText}>{answer}</Text></ScrollView>}
        </View>
      </KeyboardAvoidingView>
    </Modal>
  </SafeAreaView>;
}

const styles = StyleSheet.create({
  safe:{flex:1,backgroundColor:'#F4F8FF'},page:{padding:18,paddingBottom:32},header:{flexDirection:'row',alignItems:'center',marginBottom:20},
  logo:{width:48,height:48,borderRadius:15,backgroundColor:BLUE,alignItems:'center',justifyContent:'center'},logoText:{fontSize:27,fontWeight:'800',color:'#fff'},
  brand:{fontSize:21,fontWeight:'800',color:INK},subBrand:{fontSize:12,color:MUTED,marginTop:2},profile:{paddingHorizontal:13,paddingVertical:9,backgroundColor:'#fff',borderRadius:12,borderWidth:1,borderColor:'#E4ECF7'},profileText:{color:INK,fontWeight:'600'},
  balanceCard:{backgroundColor:BLUE,borderRadius:24,padding:20,marginBottom:25,shadowColor:'#246BEB',shadowOpacity:0.15,shadowRadius:12,elevation:4},
  balanceTop:{flexDirection:'row',justifyContent:'space-between',alignItems:'center'},balanceLabel:{color:'#E5EEFF',fontSize:13},eye:{color:'#fff',fontWeight:'700',padding:5},balance:{fontSize:34,fontWeight:'800',color:'#fff',marginTop:10},demoNotice:{fontSize:11,color:'#DDE8FF',marginTop:4},
  actions:{flexDirection:'row',gap:8,marginTop:22,flexWrap:'wrap'},actionButton:{backgroundColor:BLUE,borderRadius:13,paddingVertical:13,paddingHorizontal:14,alignItems:'center',justifyContent:'center',minHeight:46},actionText:{color:'#fff',fontSize:13,fontWeight:'700'},secondaryButton:{backgroundColor:'#fff'},secondaryText:{color:BLUE},
  sectionTitle:{fontSize:18,fontWeight:'800',color:INK,marginBottom:13},serviceGrid:{flexDirection:'row',gap:12,marginBottom:16},service:{flex:1,backgroundColor:'#fff',borderRadius:18,padding:16,borderWidth:1,borderColor:'#E8EFF8',minHeight:142},serviceIcon:{fontSize:25,color:BLUE,marginBottom:10},serviceTitle:{fontSize:15,fontWeight:'800',color:INK},serviceDesc:{fontSize:12,color:MUTED,marginTop:6,lineHeight:18},
  infoCard:{backgroundColor:'#E8F1FF',borderRadius:16,padding:16,marginBottom:25},infoTitle:{fontSize:14,fontWeight:'800',color:INK,marginBottom:6},infoBody:{fontSize:12,color:'#425A7B',lineHeight:18},
  historyHeader:{flexDirection:'row',justifyContent:'space-between',alignItems:'center'},demoTag:{color:BLUE,fontSize:10,fontWeight:'800',backgroundColor:'#E3EDFF',paddingHorizontal:9,paddingVertical:5,borderRadius:8,marginBottom:13},historyItem:{backgroundColor:'#fff',padding:13,borderRadius:15,marginBottom:9,flexDirection:'row',alignItems:'center',gap:11,borderWidth:1,borderColor:'#EDF2F9'},historyIcon:{width:39,height:39,borderRadius:13,backgroundColor:'#EDF4FF',alignItems:'center',justifyContent:'center'},historyTitle:{fontSize:13,fontWeight:'700',color:INK},historyDetail:{fontSize:10,color:MUTED,marginTop:4},amount:{fontSize:12,fontWeight:'800',color:INK},footer:{fontSize:11,color:'#93A2B7',textAlign:'center',marginTop:20},
  modalShade:{flex:1,backgroundColor:'rgba(10,24,48,0.45)',justifyContent:'flex-end'},modalCard:{backgroundColor:'#fff',borderTopLeftRadius:25,borderTopRightRadius:25,padding:20,paddingBottom:28,maxHeight:'88%'},modalHead:{flexDirection:'row',alignItems:'center',justifyContent:'space-between',marginBottom:10},modalTitle:{fontSize:17,fontWeight:'800',color:INK,flex:1},close:{color:BLUE,fontWeight:'700',padding:8},modalHint:{fontSize:12,color:MUTED,lineHeight:18,marginBottom:13},input:{borderWidth:1,borderColor:'#DDE6F2',borderRadius:13,padding:13,minHeight:50,maxHeight:130,color:INK,marginBottom:12,textAlignVertical:'top'},answerBox:{maxHeight:230,marginTop:14,backgroundColor:'#F4F8FF',borderRadius:13,padding:13},answerText:{fontSize:13,color:INK,lineHeight:20}
});
