package com.qpay.android.network

import com.qpay.android.dth.DthFetchBillRequest
import com.qpay.android.dth.dthBillPay
import com.qpay.android.gas_cylinder.GasCynFetchBillRequest
import com.qpay.android.rechargePostPaid.PostPaidQuickBillPay
import com.qpay.android.rechargePrepad.PaymentValidationRequestModel
import com.qpay.android.rechargePrepad.RechargePay
import com.qpay.android.requestModel.AddBankRequest
import com.qpay.android.requestModel.AddMoneyRequest
import com.qpay.android.requestModel.BankAmountTransferRequest
import com.qpay.android.requestModel.ElectricityBillPay
import com.qpay.android.requestModel.ElectricityFetchBillRequest
import com.qpay.android.requestModel.FasTagBillPay
import com.qpay.android.requestModel.FasTagBillerRequest
import com.qpay.android.requestModel.FasTagFetchBillRequest
import com.qpay.android.requestModel.LoginRequest
import com.qpay.android.requestModel.OTPRequest
import com.qpay.android.requestModel.PinSetUpRequest
import com.qpay.android.requestModel.PostPaidBillPay
import com.qpay.android.requestModel.PostPaidFetchBillRequest
import com.qpay.android.requestModel.PrepaidGetCompanyRequest
import com.qpay.android.requestModel.RedeemRequest
import com.qpay.android.requestModel.ReferCodeRequest
import com.qpay.android.requestModel.SendAmountRequest
import com.qpay.android.requestModel.TokenRequest
import io.reactivex.Observable
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiInterface
{
  @GET("aboutUs")
  fun getAboutUs() : Call<ResponseBody>

  @GET("customer/profile")
  fun getProfile(@Header ("x-access-token") header: String) : Call<ResponseBody>

  @GET("customer/getPin")
  fun getPin(@Header ("x-access-token") header: String) : Call<ResponseBody>

  @GET("customer/referralPoint")
  fun getReferalPoints(@Header ("x-access-token") header: String) : Call<ResponseBody>

  @GET("customer/bankAccount")
  fun getSavedBank(@Header ("x-access-token") header: String) : Call<ResponseBody>

  @GET("banner/list")
  fun getBanner(@Header ("x-access-token") header: String) : Call<ResponseBody>

  @GET("cms/page/about-us")
  fun getAboutUs(@Header ("x-access-token") header: String) : Call<ResponseBody>

  @GET("cms/page/privacy-policy")
  fun getTerms(@Header ("x-access-token") header: String) : Call<ResponseBody>

  @GET("wallet/list")
  fun getBalance(@Header ("x-access-token") header: String) : Call<ResponseBody>

  @GET("transfer/transfer-history")
  fun getHistory(@Header ("x-access-token") header: String) : Call<ResponseBody>

  @POST("auth/customer-login")
  fun login(@Body param: LoginRequest) : Call<ResponseBody>

  @POST("customer/referralWallet")
  fun redeemPoint(@Header ("x-access-token") header: String,@Body param: RedeemRequest) : Call<ResponseBody>

  @POST("auth/resend")
  fun reSendOtp(@Body param: LoginRequest) : Call<ResponseBody>

  @POST("auth/verifyOTP")
  fun verifyOtp(@Body param: OTPRequest) : Call<ResponseBody>

  @POST("customer/updatePin")
  fun pinSetUp(@Header ("x-access-token") header: String,@Body param: PinSetUpRequest) : Call<ResponseBody>

  @POST("customer/bankAccount")
  fun addBank(@Header ("x-access-token") header: String,@Body param: AddBankRequest) : Call<ResponseBody>

  @POST("customer/requestTransfer")
  fun amountTransfer(@Header ("x-access-token") header: String,@Body param: BankAmountTransferRequest) : Call<ResponseBody>

  @DELETE("customer/bankAccount/{id}")
  fun deleteBank(@Header ("x-access-token") header: String, @Path("id") id: String) : Call<ResponseBody>

  @PUT("customer/bankAccount/{id}")
  fun setPrimaryBank(@Header ("x-access-token") header: String, @Path("id") id: String) : Call<ResponseBody>

  @POST("bbps/biller/getBillerAllDetailsByCategoryName")
  fun getFasTagList(@Header ("x-access-token") header: String,@Body param: FasTagBillerRequest) : Call<ResponseBody>

  @POST("bbps/bbpsPrepaid/paymentValidation")
  fun getPaymentValidation(@Header ("x-access-token") header: String,@Body param: PaymentValidationRequestModel) : Call<ResponseBody>


  @POST("bbps/biller/fetchBill")
  fun getFasTagBill(@Header ("x-access-token") header: String,@Body param: FasTagFetchBillRequest) : Call<ResponseBody>

  @POST("bbps/biller/fetchBill")
  fun getDthBill(@Header ("x-access-token") header: String,@Body param: DthFetchBillRequest) : Call<ResponseBody>

  @POST("bbps/biller/fetchBill")
  fun getElectricityBill(@Header ("x-access-token") header: String,@Body param: ElectricityFetchBillRequest) : Call<ResponseBody>


  @POST("bbps/biller/fetchBill")
  fun getGasCynBill(@Header ("x-access-token") header: String,@Body param: GasCynFetchBillRequest) : Call<ResponseBody>

  @POST("bbps/bbpsPrepaid/getOperatorAndCircleInfoAndMobilePlan")
  fun getCompanyName(@Header ("x-access-token") header: String,@Body param: PrepaidGetCompanyRequest) : Call<ResponseBody>


  @POST("bbps/biller/fetchBill")
  fun getPostPaidBill(@Header ("x-access-token") header: String,@Body param: PostPaidFetchBillRequest) : Call<ResponseBody>

  @POST("bbps/biller/billPay")
  fun fasTagBillPay(@Header ("x-access-token") header: String,@Body param: FasTagBillPay) : Call<ResponseBody>

  @POST("bbps/biller/billPay")
  fun dthBillPay(@Header ("x-access-token") header: String,@Body param: dthBillPay) : Call<ResponseBody>

  @POST("bbps/biller/billPay")
  fun electricityBillPay(@Header ("x-access-token") header: String,@Body param: ElectricityBillPay) : Call<ResponseBody>

  @POST("bbps/biller/billPay")
  fun lpgGasBillPay(@Header ("x-access-token") header: String,@Body param: ElectricityBillPay) : Call<ResponseBody>

  @POST("bbps/bbpsPrepaid/billPaymentRequest")
  fun rechargePay(@Header ("x-access-token") header: String,@Body param: RechargePay) : Call<ResponseBody>

  @POST("bbps/biller/billPayPostPad")
  fun postPaidBillPay(@Header ("x-access-token") header: String,@Body param: PostPaidBillPay) : Call<ResponseBody>

  @POST("bbps/biller/billPayPostPad")
  fun postPaidBillPayQuick(@Header ("x-access-token") header: String,@Body param: PostPaidQuickBillPay) : Call<ResponseBody>

  @POST("wallet/cftoken")
  fun getToken(@Header ("x-access-token") header: String,@Body param: TokenRequest) : Call<ResponseBody>

  @POST("transfer/send_money")
  fun amountSend(@Header ("x-access-token") header: String,@Body param: SendAmountRequest) : Call<ResponseBody>

  @POST("wallet/create")
  fun addMoney(@Header ("x-access-token") header: String,@Body param: AddMoneyRequest) : Call<ResponseBody>

  @POST("customer/checkReferralCode")
  fun referCode(@Header ("x-access-token") header: String,@Body param: ReferCodeRequest) : Call<ResponseBody>

  @GET("currentBalance")
  fun currentBalance() : Call<ResponseBody>

  @Multipart
  @POST("customer/updateProfile")
  fun updateProfile(@Header ("x-access-token") header: String,
    @Part fname: MultipartBody.Part,
    @Part lname: MultipartBody.Part,
    @Part email: MultipartBody.Part,
    @Part address: MultipartBody.Part,
    @Part image: MultipartBody.Part,
  ): Call<ResponseBody>

  @Multipart
  @POST("customer/kycDocument")
  fun kycRequest(@Header ("x-access-token") header: String,
    @Part type: MultipartBody.Part,
    @Part imageFront: MultipartBody.Part,
    @Part imageBack: MultipartBody.Part,
  ): Call<ResponseBody>

  companion object {

    /*var BASE_URL = "http://65.1.86.136:2021/v1/"*/
    var BASE_URL = "https://dev-bbps-api.queepay.com/v1/"


    fun create() : ApiInterface {

      val retrofit = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl(BASE_URL)
        .build()
      return retrofit.create(ApiInterface::class.java)

    }
  }
}