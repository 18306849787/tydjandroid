package com.typartybuilding.retrofit;

import android.content.res.Resources;

import com.typartybuilding.base.MyApplication;
import com.typartybuilding.gsondata.loginregister.IntoFaceData;
import com.typartybuilding.gsondata.loginregister.ReciMsgData;
import com.typartybuilding.gsondata.loginregister.UserData;
import com.typartybuilding.gsondata.loginregister.VisitorData;
import com.typartybuilding.gsondata.personaldata.PartyCertificationData;

import io.reactivex.Observable;
import okhttp3.Response;
import okhttp3.ResponseBody;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * 登陆注册 retrofit网络请求接口
 */

public interface LoginRegister {


    public Resources res = MyApplication.getContext().getResources();


    String sentMsg = "applogin/sentMsg";                    //发送手机号，获取验证码
    String validate_code = "applogin/validateCode";        //验证码 验证

    String party_register = "applogin/partyRegister";      //党员认证,  用于登陆注册页面
    String validateParty = "app/user/validateParty";       //个人中心-党员认证

    String login = "applogin/login";                        //密码登陆
    String face_login = "applogin/faceLogin";          //人脸识别登陆
    String temp_login = "applogin/tempLogin";          //临时登陆

    String face_register = "applogin/faceRegister";    //人脸注册
    String register = "applogin/register";             //设置密码
    String editPwd = "applogin/editPwd";               //重置密码（用于 忘记密码时 重新设置密码）
    String exit = "app/user/exit";                     //退出


    @FormUrlEncoded
    @POST(sentMsg)      //输入手机号   type : 0注册 ，1忘记密码
    Observable<ReciMsgData> sendMsg(@Field("type") int type, @Field("phone") String phone);

    @FormUrlEncoded
    @POST(validate_code)  //输入验证码
    Observable<ReciMsgData> inputCode(@Field("phone") String phone, @Field("code") String code);

    @FormUrlEncoded
    @POST(party_register)  //党员认证     userType: 1:群众，2：党员,   idCard: 身份证
    Observable<ReciMsgData> partyCertify(@Field("phone") String phone, @Field("userType") int userTyp,@Field("idCard") String idCard);


    /**
     * 个人中心-党员认证
     * @param phone
     * @param idCard
     * @param token
     * @return
     */
    @FormUrlEncoded
    @POST(validateParty)  //党员认证     userType: 1:群众，2：党员,   idCard: 身份证
    Observable<PartyCertificationData> partyCertify2(@Field("phone") String phone, @Field("idCard") String idCard, @Field("token") String token);


    @FormUrlEncoded
    @POST(face_register)   //人脸注册
    Observable<ResponseBody> faceRegister(@Field("phone")String phone, @Field("base64Img")String base64Img );

    @FormUrlEncoded
    @POST(register)   //设置密码
    Observable<ReciMsgData> setPassword(@Field("phone") String phone, @Field("password") String password);

    @FormUrlEncoded
    @POST(editPwd)   //重置密码，用于忘记
    Observable<ReciMsgData> resetPassword(@Field("phone") String phone, @Field("password") String password);

    @FormUrlEncoded
    @POST(login)  //密码登陆      CID : 从个推 获取的clientid
    Observable<UserData> passwardLongin(@Field("phone") String phone, @Field("password") String password, @Field("CID") String clientId);

    @FormUrlEncoded
    @POST(temp_login)  //访客登陆
    Observable<VisitorData> visitorLogin(@Field("CID") String clientId);

    @FormUrlEncoded
    @POST(face_login)  //人脸识别登陆
    Observable<UserData> faceLogin(@Field("base64Img") String base64Img, @Field("CID") String clientId);
    //Observable<ResponseBody> faceLogin(@Field("base64Img") String base64Img, @Field("CID") String clientId);

    @FormUrlEncoded
    @POST(exit)   //退出登陆
    Observable<ReciMsgData> exitLogin(@Field("userId") int userId, @Field("token") String token);


}
