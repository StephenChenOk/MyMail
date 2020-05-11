package com.chen.fy.mymail.asyncTasks;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.chen.fy.mymail.R;
import com.chen.fy.mymail.adapter.InboxAdapter;
import com.chen.fy.mymail.beans.InboxItem;
import com.chen.fy.mymail.interfaces.IReceiveAsyncResponse;
import com.chen.fy.mymail.utils.DateUtils;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.core.BasePopupView;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.mail.Address;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.Store;


public class ReceiveAsyncTask extends AsyncTask<Void, Integer, List<InboxItem>> {

    // 定义连接POP3服务器的属性信息
    String pop3Server = "pop.qq.com";
    String protocol = "pop3";
    String username = "1670617529";
    String password = "iceearjigcltegih"; // QQ邮箱的SMTP的授权码，什么是授权码，它又是如何设置？

    private IReceiveAsyncResponse asyncResponse;

    public void setOnAsyncResponse(IReceiveAsyncResponse asyncResponse) {
        this.asyncResponse = asyncResponse;
    }

    @Override
    protected List<InboxItem> doInBackground(Void... voids) {

        try {
            return receiveMail();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(List<InboxItem> inboxItems) {
        super.onPostExecute(inboxItems);
        if (inboxItems != null) {
            asyncResponse.onDataReceivedSuccess(inboxItems);
        } else {
            asyncResponse.onDataReceivedFailed();
        }
    }

    private List<InboxItem> receiveMail() throws Exception {

        ArrayList<InboxItem> inboxItems = new ArrayList<>();

        Properties props = new Properties();
        props.setProperty("mail.transport.protocol", protocol); // 使用的协议（JavaMail规范要求）
        props.setProperty("mail.smtp.host", pop3Server); // 发件人的邮箱的 SMTP服务器地址

        // 获取连接
        Session session = Session.getDefaultInstance(props);
        session.setDebug(false);

        // 获取Store对象
        Store store = session.getStore(protocol);
        store.connect(pop3Server, username, password); // POP3服务器的登陆认证

        // 通过POP3协议获得Store对象调用这个方法时，邮件夹名称只能指定为"INBOX"
        Folder folder = store.getFolder("INBOX");// 获得用户的邮件帐户
        folder.open(Folder.READ_WRITE); // 设置对邮件帐户的访问权限
        Message[] messages = folder.getMessages();// 得到邮箱帐户中的所有邮件

        for (Message message : messages) {
            Address from = message.getFrom()[0];// 获得发送者地址
            String[] froms = from.toString().split("<");
            String fromAddress;
            if (froms.length != 1) {
                fromAddress = froms[1].substring(0, froms[1].length() - 2);
            } else {
                fromAddress = froms[0];
            }

            String subject = message.getSubject();// 获得邮件主题

            String content = getAllMultipart(message);
            Date date = message.getSentDate();

            Log.d("chenyisheng", date.toString());

            InboxItem inboxItem = new InboxItem(
                    R.drawable.user_test
                    , fromAddress
                    , subject
                    , content
                    , date);
            inboxItems.add(inboxItem);
        }

        folder.close(false);// 关闭邮件夹对象
        store.close(); // 关闭连接对象

        //按日期排序
        Collections.sort(inboxItems, new Comparator<InboxItem>() {
            @Override
            public int compare(InboxItem o1, InboxItem o2) {
                return o2.getDate().compareTo(o1.getDate());
            }
        });

        return inboxItems;
    }

    /**
     * 解析综合数据
     *
     * @param part 邮件
     */
    private static String getAllMultipart(Part part) throws Exception {
        String contentType = part.getContentType();
        int index = contentType.indexOf("name");
        boolean conName = false;
        if (index != -1) {
            conName = true;
        }
        //判断part类型
        if (part.isMimeType("text/plain") && !conName) {
            return (String) part.getContent();
        } else if (part.isMimeType("text/html") && !conName) {
            return (String) part.getContent();
        }
        return null;
//        }else if (part.isMimeType("multipart/*")) {
//            Multipart multipart = (Multipart) part.getContent();
//            int counts = multipart.getCount();
//            for (int i = 0; i < counts; i++) {
//                //递归获取数据
//                getAllMultipart(multipart.getBodyPart(i));
//                //附件可能是截图或上传的(图片或其他数据)
//                if (multipart.getBodyPart(i).getDisposition() != null) {
//                    //附件为截图
//                    if (multipart.getBodyPart(i).isMimeType("image/*")) {
//                        InputStream is = multipart.getBodyPart(i)
//                                .getInputStream();
//                        String name = multipart.getBodyPart(i).getFileName();
//                        String fileName;
//                        //截图图片
//                        if(name.startsWith("=?")){
//                            fileName = name.substring(name.lastIndexOf(".") - 1,name.lastIndexOf("?="));
//                        }else{
//                            //上传图片
//                            fileName = name;
//                        }
//
//                        FileOutputStream fos = new FileOutputStream("D:\\"
//                                + fileName);
//                        int len = 0;
//                        byte[] bys = new byte[1024];
//                        while ((len = is.read(bys)) != -1) {
//                            fos.write(bys,0,len);
//                        }
//                        fos.close();
//                    } else {
//                        //其他附件
//                        InputStream is = multipart.getBodyPart(i)
//                                .getInputStream();
//                        String name = multipart.getBodyPart(i).getFileName();
//                        FileOutputStream fos = new FileOutputStream("D:\\"
//                                + name);
//                        int len = 0;
//                        byte[] bys = new byte[1024];
//                        while ((len = is.read(bys)) != -1) {
//                            fos.write(bys,0,len);
//                        }
//                        fos.close();
//                    }
//                }
//            }
//        }else if (part.isMimeType("message/rfc822")) {
//           // getAllMultipart((Part) part.getContent());
//
//        }
    }
}