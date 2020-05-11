package com.chen.fy.mymail.asyncTasks;

import android.os.AsyncTask;
import android.os.Handler;
import android.widget.Toast;

import com.chen.fy.mymail.activities.WriteEmailActivity;

import java.util.Date;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;


public class SendAsyncTask extends AsyncTask<String, Integer, String> {

    //发件人地址
    private String senderAddress = "1670617529@qq.com";
    //收件人地址
//    private  String recipientAddress = "1670617529@qq.com";
    //发件人账户名
    private String senderAccount = "1670617529";
    //发件人SMTP授权码
    private String senderPassword = "iceearjigcltegih";

    private Handler handler;

    public void setHandler(Handler handler){
        this.handler = handler;
    }

    @Override
    protected String doInBackground(String... strings) {

        try {
            sendMail(strings[0], strings[1], strings[2]);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "发送成功";
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        android.os.Message message = handler.obtainMessage();
        message.what = WriteEmailActivity.SEND_SUCCESS_CODE;
        message.obj = s;
        handler.sendMessage(message);
    }

    /**
     * 发送邮件
     *
     * @param recipientAddress 接收人邮箱地址
     */
    private void sendMail(String recipientAddress, String subject, String content) throws Exception {

        Properties props = new Properties();
        props.setProperty("mail.smtp.host", "smtp.qq.com");
        props.setProperty("mail.transport.protocol", "smtp");
        props.setProperty("mail.smtp.auth", "true");
        props.setProperty("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.setProperty("mail.smtp.port", "465");
        props.setProperty("mail.smtp.socketFactory.port", "465");
        props.setProperty("mail.debug", "true");
        props.put("mail.smtp.ssl.enable", true);

        //2、创建定义整个应用程序所需的环境信息的 Session 对象
        Session session = Session.getInstance(props);
        //设置调试信息在控制台打印出来
        session.setDebug(true);
        //3、创建邮件的实例对象
        Message msg = getMimeMessage(recipientAddress, subject, content, session);
        //4、根据session对象获取邮件传输对象Transport
        Transport transport = session.getTransport("smtp");
        //设置发件人的账户名和密码
        transport.connect("smtp.qq.com", senderAccount, senderPassword);
        //发送邮件，并发送到所有收件人地址，message.getAllRecipients() 获取到的是在创建邮件对象时添加的所有收件人, 抄送人, 密送人
        transport.sendMessage(msg, msg.getAllRecipients());

        //如果只想发送给指定的人，可以如下写法
        //transport.sendMessage(msg, new Address[]{new InternetAddress("xxx@qq.com")});

        //5、关闭邮件连接
        transport.close();
    }

    /**
     * 获得创建一封邮件的实例对象
     */
    private MimeMessage getMimeMessage(String recipientAddress, String subject, String content
            , Session session) throws Exception {
        //创建一封邮件的实例对象
        MimeMessage msg = new MimeMessage(session);
        //设置发件人地址
        msg.setFrom(new InternetAddress(senderAddress));
        /*
         * 设置收件人地址（可以增加多个收件人、抄送、密送），即下面这一行代码书写多行
         * MimeMessage.RecipientType.TO:发送
         * MimeMessage.RecipientType.CC：抄送
         * MimeMessage.RecipientType.BCC：密送
         */
        msg.setRecipient(MimeMessage.RecipientType.TO, new InternetAddress(recipientAddress));
        //设置邮件主题
        msg.setSubject(subject, "UTF-8");
        //设置邮件正文
        msg.setContent(content, "text/html;charset=UTF-8");
        //设置邮件的发送时间,默认立即发送
        msg.setSentDate(new Date());

        return msg;
    }
}
