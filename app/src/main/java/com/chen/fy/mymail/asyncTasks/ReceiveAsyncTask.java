package com.chen.fy.mymail.asyncTasks;

import android.os.AsyncTask;
import android.util.Log;

import com.chen.fy.mymail.R;
import com.chen.fy.mymail.beans.InboxItem;
import com.chen.fy.mymail.interfaces.IReceiveAsyncResponse;
import com.chen.fy.mymail.utils.DateUtils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.internet.MimeUtility;


public class ReceiveAsyncTask extends AsyncTask<File, Integer, List<InboxItem>> {

    // 定义连接POP3服务器的属性信息
    String pop3Server = "pop.qq.com";
    String protocol = "pop3";
    String username = "1670617529";
    String password = "iceearjigcltegih"; // QQ邮箱的SMTP的授权码，什么是授权码，它又是如何设置？

    private IReceiveAsyncResponse asyncResponse;

    private int idFile = 1;

    public void setOnAsyncResponse(IReceiveAsyncResponse asyncResponse) {
        this.asyncResponse = asyncResponse;
    }

    @Override
    protected List<InboxItem> doInBackground(File... files) {

        try {
            return receiveMail(files[0]);
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

    private List<InboxItem> receiveMail(File file) throws Exception {

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
                fromAddress = froms[1].substring(0, froms[1].length() - 1);
            } else {
                fromAddress = froms[0];
            }

            String subject = message.getSubject();// 获得邮件主题

            StringBuffer sbContent = new StringBuffer(30);
            getMailTextContent(message, sbContent);
            Date date = message.getSentDate();

            File attachmentFile = new File(file, DateUtils.dateToDateString(date)
                    + DateUtils.dateToTimeString(date) + ".jpg");
            saveAttachment(message, attachmentFile);

            InboxItem inboxItem = new InboxItem(
                    R.drawable.user_test
                    , fromAddress
                    , subject
                    , sbContent.toString()
                    , date);
            if (attachmentFile.length() > 0) {
                Log.d("hahahaha", ":" + attachmentFile.length());
                inboxItem.setFile(attachmentFile);
            }
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
     * 获得邮件文本内容
     *
     * @param part    邮件体
     * @param content 存储邮件文本内容的字符串
     */
    private void getMailTextContent(Part part, StringBuffer content) throws MessagingException, IOException {
        //如果是文本类型的附件，通过getContent方法可以取到文本内容，但这不是我们需要的结果，所以在这里要做判断
        boolean isContainTextAttach = part.getContentType().indexOf("name") > 0;
        if (part.isMimeType("text/plain") && !isContainTextAttach) {
            Log.d("chenchen", "111");
            content.append(part.getContent().toString());
        } else if (part.isMimeType("text/html") && !isContainTextAttach) {
            Log.d("chenchen", "222");
            //content.append(part.getContent().toString());
        } else if (part.isMimeType("message/rfc822")) {
            Log.d("chenchen", "333");
            getMailTextContent((Part) part.getContent(), content);
        } else if (part.isMimeType("multipart/*")) {
            Log.d("chenchen", "444");
            Multipart multipart = (Multipart) part.getContent();
            int partCount = multipart.getCount();
            for (int i = 0; i < partCount; i++) {
                BodyPart bodyPart = multipart.getBodyPart(i);
                getMailTextContent(bodyPart, content);
            }
        }
    }

//    /**
//     * 解析综合数据
//     * @param part 邮件
//     */
//    private static String getAllMultipart(Part part) throws Exception {
//        String contentType = part.getContentType();
//        int index = contentType.indexOf("name");
//        boolean conName = false;
//        if (index != -1) {
//            conName = true;
//        }
//        //判断part类型
//        if (part.isMimeType("text/plain") && !conName) {
//            return (String) part.getContent();
//        } else if (part.isMimeType("text/html") && !conName) {
//            return (String) part.getContent();
//        }
////         else if (part.isMimeType("multipart/*")) {
////            Multipart multipart = (Multipart) part.getContent();
////            int counts = multipart.getCount();
////            for (int i = 0; i < counts; i++) {
////                //递归获取数据
////                getAllMultipart(multipart.getBodyPart(i));
////                //附件可能是截图或上传的(图片或其他数据)
////                if (multipart.getBodyPart(i).getDisposition() != null) {
////                    //附件为截图
////                    if (multipart.getBodyPart(i).isMimeType("image/*")) {
////                        InputStream is = multipart.getBodyPart(i)
////                                .getInputStream();
////                        String name = multipart.getBodyPart(i).getFileName();
////                        String fileName;
////                        //截图图片
////                        if(name.startsWith("=?")){
////                            fileName = name.substring(name.lastIndexOf(".") - 1,name.lastIndexOf("?="));
////                        }else{
////                            //上传图片
////                            fileName = name;
////                        }
////
////                        FileOutputStream fos = new FileOutputStream("D:\\"
////                                + fileName);
////                        int len = 0;
////                        byte[] bys = new byte[1024];
////                        while ((len = is.read(bys)) != -1) {
////                            fos.write(bys,0,len);
////                        }
////                        fos.close();
////                    } else {
////                        //其他附件
////                        InputStream is = multipart.getBodyPart(i)
////                                .getInputStream();
////                        String name = multipart.getBodyPart(i).getFileName();
////                        FileOutputStream fos = new FileOutputStream("D:\\"
////                                + name);
////                        int len = 0;
////                        byte[] bys = new byte[1024];
////                        while ((len = is.read(bys)) != -1) {
////                            fos.write(bys,0,len);
////                        }
////                        fos.close();
////                    }
////                }
////            }
////        }else if (part.isMimeType("message/rfc822")) {
////            getAllMultipart((Part) part.getContent());
////        }
//        return "内容解析出错";
//    }

    /**
     * 保存附件
     *
     * @param part 邮件中多个组合体中的其中一个组合体
     * @param file 附件保存文件
     */
    public void saveAttachment(Part part, File file) throws UnsupportedEncodingException, MessagingException,
            FileNotFoundException, IOException {
        if (part.isMimeType("multipart/*")) {
            Multipart multipart = (Multipart) part.getContent();    //复杂体邮件
            //复杂体邮件包含多个邮件体
            int partCount = multipart.getCount();
            for (int i = 0; i < partCount; i++) {
                //获得复杂体邮件中其中一个邮件体
                BodyPart bodyPart = multipart.getBodyPart(i);
                //某一个邮件体也有可能是由多个邮件体组成的复杂体
                String disp = bodyPart.getDisposition();
                if (disp != null && (disp.equalsIgnoreCase(Part.ATTACHMENT) || disp.equalsIgnoreCase(Part.INLINE))) {
                    InputStream is = bodyPart.getInputStream();
                    saveFile(is, file);
                } else if (bodyPart.isMimeType("multipart/*")) {
                    saveAttachment(bodyPart, file);
                } else {
                    String contentType = bodyPart.getContentType();
                    if (contentType.indexOf("name") != -1 || contentType.indexOf("application") != -1) {
                        saveFile(bodyPart.getInputStream(), file);
                    }
                }
            }
        } else if (part.isMimeType("message/rfc822")) {
            saveAttachment((Part) part.getContent(), file);
        }
    }

    /**
     * 读取输入流中的数据保存至指定目录
     *
     * @param is   输入流
     * @param file 附件保存文件
     */
    private File saveFile(InputStream is, File file)
            throws FileNotFoundException, IOException {
        BufferedInputStream bis = new BufferedInputStream(is);
        BufferedOutputStream bos = new BufferedOutputStream(
                new FileOutputStream(file));
        int len = -1;
        while ((len = bis.read()) != -1) {
            bos.write(len);
            bos.flush();
        }
        bos.close();
        bis.close();
        return file;
    }

    /**
     * 文本解码
     *
     * @param encodeText 解码MimeUtility.encodeText(String text)方法编码后的文本
     * @return 解码后的文本
     * @throws UnsupportedEncodingException
     */
    public static String decodeText(String encodeText) throws UnsupportedEncodingException {
        if (encodeText == null || "".equals(encodeText)) {
            return "";
        } else {
            return MimeUtility.decodeText(encodeText);
        }
    }
}