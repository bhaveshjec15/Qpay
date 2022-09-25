package com.qpay.android.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.qpay.android.R
import com.qpay.android.R.drawable
import com.qpay.android.R.layout
import com.qpay.android.activity.AddMoneyActivity
import com.qpay.android.activity.BankAmountTransferActivity
import com.qpay.android.activity.BankTransferActivity
import com.qpay.android.activity.MyAccountActivity
import com.qpay.android.activity.OTPActivity
import com.qpay.android.activity.PinSetupActivity
import com.qpay.android.activity.QRCodeScanActivity
import com.qpay.android.activity.ReferActivity
import com.qpay.android.activity.ReferalCodeActivity
import com.qpay.android.activity.RewardActivity
import com.qpay.android.activity.SendToMobileActivity
import com.qpay.android.adapter.SliderNewAdapter
import com.qpay.android.databinding.ActivityLoginBinding
import com.qpay.android.databinding.FragmentHomeBinding
import com.qpay.android.dth.ProviderListActivity
import com.qpay.android.electricty.ElectrictyBillerList
import com.qpay.android.fasTag.FasTagActivity
import com.qpay.android.gas.GasPipeProviderListActivity
import com.qpay.android.gas_cylinder.GasCynProviderListActivity
import com.qpay.android.model.SliderData
import com.qpay.android.network.ApiInterface
import com.qpay.android.rechargePostPaid.PostpaidBillerList
import com.qpay.android.rechargePrepad.ContactListActivity
import com.qpay.android.requestModel.PinSetUpRequest
import com.qpay.android.utils.CommonUtils
import com.qpay.android.utils.getStringShrd
import com.qpay.android.utils.saveStringShrd
import com.qpay.android.utils.showSnackBar
import com.smarteist.autoimageslider.SliderAnimations.SIMPLETRANSFORMATION
import com.smarteist.autoimageslider.SliderView
import com.squareup.picasso.Picasso
import com.vdx.designertoast.DesignerToast
import kotlinx.android.synthetic.main.activity_main.profile_image
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    var adapter: SliderNewAdapter? = null
    private var sliderDataArrayList: ArrayList<SliderData>? = null
    val kycStatus: MutableLiveData<String?> = MutableLiveData("")
    val userName: MutableLiveData<String?> = MutableLiveData("")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
         binding = DataBindingUtil.inflate(inflater,
            R.layout.fragment_home, container, false)
       // binding.viewmodel = vm//attach your viewModel to xml
        return binding.root

        callApiGetProfile()
    }

    companion object {
        fun newInstance() = HomeFragment()
    }

    override fun onResume() {
        super.onResume()


        userName.observe(requireActivity(), Observer {
            if (it.isNullOrEmpty()) {
                 binding.tvUserName.text = "Hi... Same"
               // profile_image.setImageResource(drawable.ic_profile)
            } else {
                binding.tvUserName.text = "Hi... $it"
               // Picasso.get().load(it).into(profile_image)
            }
        })
        binding.btnTransfer.text = requireActivity().resources.getString(R.string.Rs)+ " Transfer Money"

        binding.btnTransfer.setOnClickListener {
            val intent = Intent(activity, SendToMobileActivity::class.java)
            startActivity(intent)
        }
       // callApiForGetBalance()
        sliderDataArrayList = ArrayList()
        callApiGetBanner()
        binding.layoutElectricity.setOnClickListener {
            val intent = Intent(activity, ElectrictyBillerList::class.java)
            startActivity(intent)
        }

        binding.layoutDth.setOnClickListener {
            val intent = Intent(activity, ProviderListActivity::class.java)
            startActivity(intent)
        }


        binding.layoutRechargePrepad.setOnClickListener {
           /* if(kycStatus.value.toString().equals("ACCEPTED")){
                val intent = Intent(activity, ContactListActivity::class.java)
                startActivity(intent)
            }
            else DesignerToast.Error(requireContext(),requireActivity().resources.getString(R.string.kyc_pending_msg), Gravity.BOTTOM,Toast.LENGTH_SHORT);
*/
            val intent = Intent(activity, ContactListActivity::class.java)
            startActivity(intent)
        }

        binding.layoutRechargePostpad.setOnClickListener {
            if(kycStatus.value.toString().equals("ACCEPTED")){
                val intent = Intent(activity, PostpaidBillerList::class.java)
                startActivity(intent)
            }
            else DesignerToast.Warning(requireContext(),requireActivity().resources.getString(R.string.kyc_pending_msg), Gravity.BOTTOM,Toast.LENGTH_SHORT);

        }

        binding.layoutFastag.setOnClickListener {
          /*  if(kycStatus.value.toString().equals("ACCEPTED")){
                val intent = Intent(activity, FasTagActivity::class.java)
                startActivity(intent)
            }
            else DesignerToast.Warning(requireContext(),requireActivity().resources.getString(R.string.kyc_pending_msg), Gravity.BOTTOM,Toast.LENGTH_SHORT);
*/
            val intent = Intent(activity, FasTagActivity::class.java)
            startActivity(intent)
        }


        binding.layoutAddMoney.setOnClickListener {
           /* if(getStringShrd(requireActivity(),CommonUtils.userName).equals("")){
                DesignerToast.Warning(requireContext(),requireActivity().resources.getString(R.string.profile_pending_msg), Gravity.BOTTOM,Toast.LENGTH_SHORT);
            }else{
                val intent = Intent(activity, AddMoneyActivity::class.java)
                startActivity(intent)
            }*/
            val intent = Intent(activity, AddMoneyActivity::class.java)
            startActivity(intent)
        }

        binding.layoutSendToMobile.setOnClickListener {
           /* if(kycStatus.value.toString().equals("ACCEPTED")){
                val intent = Intent(activity, SendToMobileActivity::class.java)
                startActivity(intent)
            }
            else DesignerToast.Warning(requireContext(),requireActivity().resources.getString(R.string.kyc_pending_msg), Gravity.BOTTOM,Toast.LENGTH_SHORT);
*/
            val intent = Intent(activity, SendToMobileActivity::class.java)
            startActivity(intent)
        }

        binding.layoutTransferBank.setOnClickListener {
           /* if(kycStatus.value.toString().equals("ACCEPTED")){
                val intent = Intent(activity, BankTransferActivity::class.java)
                startActivity(intent)
            }
            else DesignerToast.Warning(requireContext(),requireActivity().resources.getString(R.string.kyc_pending_msg), Gravity.BOTTOM,Toast.LENGTH_SHORT);
*/
            val intent = Intent(activity, BankAmountTransferActivity::class.java)
            startActivity(intent)
        }

        binding.layoutScan.setOnClickListener {
           /* if(kycStatus.value.toString().equals("ACCEPTED")){
                val intent = Intent(activity, QRCodeScanActivity::class.java)
                startActivity(intent)
            }
            else DesignerToast.Warning(requireContext(),requireActivity().resources.getString(R.string.kyc_pending_msg), Gravity.BOTTOM,Toast.LENGTH_SHORT);
*/
            val intent = Intent(activity, QRCodeScanActivity::class.java)
            startActivity(intent)
        }

        binding.layoutReward.setOnClickListener {
            val intent = Intent(activity, RewardActivity::class.java)
            startActivity(intent)
        }

        binding.layoutRefer.setOnClickListener {
            val intent = Intent(activity, ReferActivity::class.java)
            startActivity(intent)
        }

        binding.layoutWallet.setOnClickListener {
            val intent = Intent(activity, MyAccountActivity::class.java)
            startActivity(intent)
        }

        binding.layoutGasCylinder.setOnClickListener {
            val intent = Intent(activity, GasCynProviderListActivity::class.java)
            startActivity(intent)
        }
        binding.layoutGas.setOnClickListener {
            val intent = Intent(activity, GasPipeProviderListActivity::class.java)
            startActivity(intent)
        }


    }

    private fun callApiForGetBalance() {
        val apiInterface = ApiInterface.create().currentBalance()

        //apiInterface.enqueue( Callback<List<Movie>>())
        apiInterface.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                var response1 = response.body()?.string()
                var jsonObject = JSONObject(response1)
                var statusCode = jsonObject.optBoolean("status")
                var message = jsonObject.optString("message")
                if (statusCode) {

                } else {
                   // showSnackBar(pinSetUpActivity, binding.mainLayout, message)
                }
                Log.e("response", "currentBalance: "+response1.toString())

            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {

            }
        })
    }

    private fun callApiGetBanner() {
        sliderDataArrayList?.clear()
        val apiInterface = ApiInterface.create().getBanner(
            getStringShrd(
                requireActivity(),
                CommonUtils.accessToken
            )
        )

        //apiInterface.enqueue( Callback<List<Movie>>())
        apiInterface.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                var response1 = response.body()?.string()
                var jsonObject = JSONObject(response1)
                var statusCode = jsonObject.optInt("status")
                var message = jsonObject.optString("message")
                if (statusCode == 200) {
                    var jsonData = jsonObject.optJSONArray("data")
                    Log.e("response_banner",jsonData.toString())
                    for (item in 0..jsonData.length() - 1) {
                        val model = SliderData()
                        var jsonImg = jsonData.optJSONObject(item)

                        model.imgUrl = jsonImg.optString("image_url")

                        sliderDataArrayList!!.add(model)

                        // after adding data to our array list we are passing
                        // that array list inside our adapter class.
                        adapter = SliderNewAdapter(activity, sliderDataArrayList)

                        // belows line is for setting adapter
                        // to our slider view
                        binding.slider.setSliderAdapter(adapter!!)

                        // below line is for setting animation to our slider.
                        binding.slider.setSliderTransformAnimation(SIMPLETRANSFORMATION)

                        // below line is for setting auto cycle duration.
                        binding.slider.setAutoCycleDirection(SliderView.AUTO_CYCLE_DIRECTION_BACK_AND_FORTH)

                        // below line is for setting
                        // scroll time animation
                        binding.slider.setScrollTimeInSec(3)

                        // below line is for setting auto
                        // cycle animation to our slider
                        binding.slider.setAutoCycle(true)


                        adapter?.notifyDataSetChanged()
                    }
                }
                Log.e("res", response1.toString())

            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {

            }
        })
    }

    private fun callApiGetProfile() {
        val apiInterface = ApiInterface.create().getProfile(
            getStringShrd(
                requireActivity(),
                CommonUtils.accessToken
            )
        )

        //apiInterface.enqueue( Callback<List<Movie>>())
        apiInterface.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                var response1: String? = null
                if (response.body() == null) {
                    response1 = response.errorBody()?.string()
                } else {
                    response1 = response.body()?.string()
                }
                Log.e("response", "getProfile: " + response1.toString())
                var jsonObject = JSONObject(response1)
                var statusCode = jsonObject.optInt("status")
                var message = jsonObject.optString("message")
                if (statusCode == 200) {
                    var dataObject = jsonObject.optJSONObject("data")
                   // userName.value = dataObject.optString("first_name")
                  //  userImage.value = dataObject.optString("image_url")
                    userName.value = dataObject.optString("first_name")
                    saveStringShrd(requireActivity(),CommonUtils.userId,dataObject.optString("_id"))
                    saveStringShrd(requireActivity(),CommonUtils.mobileNumber, dataObject.optString("mobile_number"))
                    saveStringShrd(requireActivity(),CommonUtils.userName, dataObject.optString("first_name"))
                    saveStringShrd(requireActivity(),CommonUtils.kycStatus, dataObject.optString("kyc_status"))
                    kycStatus.value = dataObject.optString("kyc_status")
                } else {
                    //showSnackBar(pinSetUpActivity, binding.mainLayout, message)
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                // binding.progress.visibility = View.GONE
            }
        })
    }
}
