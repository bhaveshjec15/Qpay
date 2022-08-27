package com.qpay.android.rechargePrepad

import android.R.layout
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.qpay.android.R
import com.qpay.android.databinding.ActivityAllPlansBinding
import com.qpay.android.network.ApiInterface
import com.qpay.android.rechargePrepad.all.AllListAdapter
import com.qpay.android.rechargePrepad.all.AllPackModel
import com.qpay.android.rechargePrepad.comboPack.ComboPackListAdapter
import com.qpay.android.rechargePrepad.comboPack.ComboPackModel
import com.qpay.android.rechargePrepad.fullTalkTime.FullTalkTimeListAdapter
import com.qpay.android.rechargePrepad.fullTalkTime.FullTalkTimeModel
import com.qpay.android.rechargePrepad.internationalRomaning.InternationalListAdapter
import com.qpay.android.rechargePrepad.internationalRomaning.InternationalRomaningModel
import com.qpay.android.rechargePrepad.internet.InternetListAdapter
import com.qpay.android.rechargePrepad.internet.InternetModel
import com.qpay.android.rechargePrepad.special.SpecialListAdapter
import com.qpay.android.rechargePrepad.special.SpecialModel
import com.qpay.android.requestModel.PrepaidGetCompanyRequest
import com.qpay.android.utils.CommonUtils
import com.qpay.android.utils.getStringShrd
import com.qpay.android.utils.hideKeyboardFrom
import com.qpay.android.utils.showSnackBar
import okhttp3.ResponseBody
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.regex.Pattern

class AllPlansActivity : AppCompatActivity() {
  var mobileNumber: String? = ""
  private lateinit var binding: ActivityAllPlansBinding
  var list: ArrayList<CompanyNameModel> = ArrayList()
  var listName: ArrayList<String> = ArrayList()
  var listCircleName: ArrayList<String> = ArrayList()
  var listPlanInfo: ArrayList<PlanInfoModel> = ArrayList()

  var listCombo: ArrayList<ComboPackModel> = ArrayList()
  var listInternational: ArrayList<InternationalRomaningModel> = ArrayList()
  var listFullTalkTime: ArrayList<FullTalkTimeModel> = ArrayList()
  var listInternet: ArrayList<InternetModel> = ArrayList()
  var listSpecial: ArrayList<SpecialModel> = ArrayList()
  var listAll: ArrayList<AllPackModel> = ArrayList()
  companion object{
    var paramBillerId: String = ""
    var paramBillerName: String = ""
    var paramCircleRefId: String = ""
    var paramCustomerEmail: String = ""
    var paramCustomerName: String = ""
    var paramCustomerMobile:String = ""
    var paramRefId: String = ""
    var paramOperatorCode: String = ""
    var paramAmount: String = ""
    var paramPlanName: String = ""
    var paramValidity: String = ""
    var paramDescription: String = ""

    var operatorName: String = ""
  }



  val selectedState: MutableLiveData<String?> = MutableLiveData("")
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    binding = DataBindingUtil.setContentView(this, R.layout.activity_all_plans)

    //var mn = intent.extras?.getString("number")?.startsWith("+91")
    mobileNumber = PhoneNumberWithoutCountryCode(intent.extras?.getString("number")!!)
    paramCustomerMobile = mobileNumber!!.replace(" ","")
    binding.tvNumber.text = mobileNumber
  }
  fun PhoneNumberWithoutCountryCode(phoneNumberWithCountryCode: String): String? { //+91 7698989898
    val compile: Pattern = Pattern.compile(
      "\\+(?:998|996|995|994|993|992|977|976|975|974|973|972|971|970|968|967|966|965|964|963|962|961|960|886|880|856|855|853|852|850|692|691|690|689|688|687|686|685|683|682|681|680|679|678|677|676|675|674|673|672|670|599|598|597|595|593|592|591|590|509|508|507|506|505|504|503|502|501|500|423|421|420|389|387|386|385|383|382|381|380|379|378|377|376|375|374|373|372|371|370|359|358|357|356|355|354|353|352|351|350|299|298|297|291|290|269|268|267|266|265|264|263|262|261|260|258|257|256|255|254|253|252|251|250|249|248|246|245|244|243|242|241|240|239|238|237|236|235|234|233|232|231|230|229|228|227|226|225|224|223|222|221|220|218|216|213|212|211|98|95|94|93|92|91|90|86|84|82|81|66|65|64|63|62|61|60|58|57|56|55|54|53|52|51|49|48|47|46|45|44\\D?1624|44\\D?1534|44\\D?1481|44|43|41|40|39|36|34|33|32|31|30|27|20|7|1\\D?939|1\\D?876|1\\D?869|1\\D?868|1\\D?849|1\\D?829|1\\D?809|1\\D?787|1\\D?784|1\\D?767|1\\D?758|1\\D?721|1\\D?684|1\\D?671|1\\D?670|1\\D?664|1\\D?649|1\\D?473|1\\D?441|1\\D?345|1\\D?340|1\\D?284|1\\D?268|1\\D?264|1\\D?246|1\\D?242|1)\\D?"
    )
    //Log.e(tag, "number::_>" +  number);//OutPut::7698989898
    return phoneNumberWithCountryCode.replace(compile.pattern().toRegex(), "")
  }
  override fun onResume() {
    super.onResume()

    binding.ivBack.setOnClickListener { finish() }

    var param = PrepaidGetCompanyRequest(mobileNumber!!)
    /* var param = ElectricityFetchBillRequest(billerId!!,resources.getString(string.param_fastag),
       "9828520222", NumberData(binding.etVehicleNumber.text.toString())
     )*/
    callApigetCompany(param, this)

    // click on company dropdoen
    binding.spnCompany.setOnItemSelectedListener(object : OnItemSelectedListener {
      override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
        listCircleName.clear()
        listPlanInfo.clear()
        operatorName = list[position].getOperatorName()
        paramOperatorCode = list[position].getOperatorId()
        val selectedName = list[position].getCircleList()
        var dataArray: JSONArray = JSONArray(selectedName)
        if (dataArray.length() > 0) {
          for (i in 0..(dataArray.length() - 1)) {
            var dataObj = dataArray.optJSONObject(i)
            var model1 = PlanInfoModel()

            model1.setCircleId(dataObj.optString("circleId"))
            var plan: JSONArray = dataObj.optJSONArray("plansInfo")
            model1.setPlanInfo(plan.toString())
            model1.setCircleName(dataObj.optString("circleName"))
            listPlanInfo.add(model1)

          }

          paramCircleRefId = listPlanInfo[position].getCircleId()

          if (listPlanInfo.size > 0) {
            for (j in 0..(listPlanInfo.size - 1)) {
              listCircleName.add(listPlanInfo[j].getCircleName())
            }
            if (listCircleName.size > 0) {
              val arrayAdapter =
                ArrayAdapter<String>(baseContext, layout.simple_spinner_item, listCircleName)
              arrayAdapter.setDropDownViewResource(layout.simple_spinner_dropdown_item)
              binding.spnState.setAdapter(arrayAdapter)
            }

          }
        }
      }

      override fun onNothingSelected(parent: AdapterView<*>?) {}
    })

    // click on state
    binding.spnState.setOnItemSelectedListener(object : OnItemSelectedListener {
      override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
        var planArray = JSONArray(listPlanInfo[position].getPlanInfo())
        Log.e("ss", planArray.length().toString())

        listCombo.clear()
        listAll.clear()
        listFullTalkTime.clear()
        listInternational.clear()
        listInternet.clear()
        listSpecial.clear()

        if(planArray !=null)
        for (a in 0..(planArray.length() - 1)) {
          var obj = planArray.optJSONObject(a)
          var allModel = AllPackModel()
          allModel.planName = obj.optString("planName")
          allModel.price = obj.optString("price")
          allModel.validitiy = obj.optString("validity")
          allModel.talktime = obj.optString("talkTime")
          allModel.pkgDescription = obj.optString("packageDescription")
          listAll.add(allModel)
        }

        if(planArray !=null)
        for (a in 0..(planArray.length() - 1)) {
          var obj = planArray.optJSONObject(a)
          var planname = obj.optString("planName")
          if (planname.equals("COMBO PACK", true) || planname.equals("PRIME COMBO PACK", true)) {
            var comboModel = ComboPackModel()
            comboModel.planName = obj.optString("planName")
            comboModel.price = obj.optString("price")
            comboModel.validitiy = obj.optString("validity")
            comboModel.talktime = obj.optString("talkTime")
            comboModel.pkgDescription = obj.optString("packageDescription")
            listCombo.add(comboModel)
          }
        }

        if(planArray !=null)
        for (a in 0..(planArray.length() - 1)) {
          var obj = planArray.optJSONObject(a)
          var planname = obj.optString("planName")
          if (planname.equals("FULL TALKTIME", true)) {
            var fullModel = FullTalkTimeModel()
            fullModel.planName = obj.optString("planName")
            fullModel.price = obj.optString("price")
            fullModel.validitiy = obj.optString("validity")
            fullModel.talktime = obj.optString("talkTime")
            fullModel.pkgDescription = obj.optString("packageDescription")
            listFullTalkTime.add(fullModel)
          }
        }

        if(planArray !=null)
        for (a in 0..(planArray.length() - 1)) {
          var obj = planArray.optJSONObject(a)
          var planname = obj.optString("planName")
          if (planname.equals("INTERNATIONAL ROAMING", true)) {
            var internationalRomaningModel = InternationalRomaningModel()
            internationalRomaningModel.planName = obj.optString("planName")
            internationalRomaningModel.price = obj.optString("price")
            internationalRomaningModel.validitiy = obj.optString("validity")
            internationalRomaningModel.talktime = obj.optString("talkTime")
            internationalRomaningModel.pkgDescription = obj.optString("packageDescription")
            listInternational.add(internationalRomaningModel)
          }
        }

        if(planArray !=null)
        for (a in 0..(planArray.length() - 1)) {
          var obj = planArray.optJSONObject(a)
          var planname = obj.optString("planName")
          if (planname.equals("INTERNET PACK", true)) {
            var internetModel = InternetModel()
            internetModel.planName = obj.optString("planName")
            internetModel.price = obj.optString("price")
            internetModel.validitiy = obj.optString("validity")
            internetModel.talktime = obj.optString("talkTime")
            internetModel.pkgDescription = obj.optString("packageDescription")
            listInternet.add(internetModel)
          }
        }

        if(planArray !=null)
        for (a in 0..(planArray.length() - 1)) {
          var obj = planArray.optJSONObject(a)
          var planname = obj.optString("planName")
          if (planname.equals("SPECIAL RECHARGE", true)) {
            var specialModel = SpecialModel()
            specialModel.planName = obj.optString("planName")
            specialModel.price = obj.optString("price")
            specialModel.validitiy = obj.optString("validity")
            specialModel.talktime = obj.optString("talkTime")
            specialModel.pkgDescription = obj.optString("packageDescription")
            listSpecial.add(specialModel)
          }
        }

        if(planArray !=null)
        selectedState.value = planArray.length().toString()

        clickTvCombo()

      }

      override fun onNothingSelected(parent: AdapterView<*>?) {}
    })
    Handler().postDelayed(Runnable {
      clickTvCombo()

      if(listFullTalkTime.size==0) binding.tvFullTalkTime.visibility = View.GONE else binding.tvFullTalkTime.visibility = View.VISIBLE
      if(listInternational.size==0) binding.tvInternational.visibility = View.GONE else binding.tvInternational.visibility = View.VISIBLE
    }, 4000)

    binding.tvComboPack.setOnClickListener { clickTvCombo() }
    binding.tvAll.setOnClickListener { clickTvAll() }
    binding.tvFullTalkTime.setOnClickListener { clickTvFullTalkTime() }
    binding.tvInternational.setOnClickListener { clickTvInternational() }
    binding.tvInternet.setOnClickListener { clickTvInternet() }
    binding.tvSpecial.setOnClickListener { clickTvSpecial() }

    selectedState.observe(this, Observer {
      clickTvCombo()
    })
  }

  private fun clickTvCombo() {
    binding.rvComboList.visibility = View.VISIBLE
    binding.rvAllList.visibility = View.GONE
    binding.rvFullTalkTimeList.visibility = View.GONE
    binding.rvInternationalList.visibility = View.GONE
    binding.rvInternetList.visibility = View.GONE

    binding.tvComboPack.setTextColor(baseContext.resources.getColor(R.color.colorBtn))
    binding.tvAll.setTextColor(baseContext.resources.getColor(R.color.colorBlack))
    binding.tvFullTalkTime.setTextColor(baseContext.resources.getColor(R.color.colorBlack))
    binding.tvInternational.setTextColor(baseContext.resources.getColor(R.color.colorBlack))
    binding.tvInternet.setTextColor(baseContext.resources.getColor(R.color.colorBlack))
    binding.tvSpecial.setTextColor(baseContext.resources.getColor(R.color.colorBlack))

    var adapterCombo: ComboPackListAdapter? = null
    if (listCombo.size > 0) {
      adapterCombo = ComboPackListAdapter(baseContext, listCombo)
      binding.rvComboList.adapter = adapterCombo
      adapterCombo?.notifyDataSetChanged()
    }
  }

  private fun clickTvFullTalkTime() {
    binding.rvComboList.visibility = View.GONE
    binding.rvAllList.visibility = View.GONE
    binding.rvFullTalkTimeList.visibility = View.VISIBLE
    binding.rvInternationalList.visibility = View.GONE
    binding.rvInternetList.visibility = View.GONE

    binding.tvComboPack.setTextColor(baseContext.resources.getColor(R.color.colorBlack))
    binding.tvAll.setTextColor(baseContext.resources.getColor(R.color.colorBlack))
    binding.tvFullTalkTime.setTextColor(baseContext.resources.getColor(R.color.colorBtn))
    binding.tvInternational.setTextColor(baseContext.resources.getColor(R.color.colorBlack))
    binding.tvInternet.setTextColor(baseContext.resources.getColor(R.color.colorBlack))
    binding.tvSpecial.setTextColor(baseContext.resources.getColor(R.color.colorBlack))

    var adapterFullTalkTime: FullTalkTimeListAdapter? = null
    if (listFullTalkTime.size > 0) {
      adapterFullTalkTime = FullTalkTimeListAdapter(baseContext, listFullTalkTime)
      binding.rvFullTalkTimeList.adapter = adapterFullTalkTime
      adapterFullTalkTime?.notifyDataSetChanged()
    }
  }

  private fun clickTvInternational() {
    binding.rvComboList.visibility = View.GONE
    binding.rvAllList.visibility = View.GONE
    binding.rvFullTalkTimeList.visibility = View.GONE
    binding.rvInternationalList.visibility = View.VISIBLE
    binding.rvInternetList.visibility = View.GONE


    binding.tvComboPack.setTextColor(baseContext.resources.getColor(R.color.colorBlack))
    binding.tvAll.setTextColor(baseContext.resources.getColor(R.color.colorBlack))
    binding.tvFullTalkTime.setTextColor(baseContext.resources.getColor(R.color.colorBlack))
    binding.tvInternational.setTextColor(baseContext.resources.getColor(R.color.colorBtn))
    binding.tvInternet.setTextColor(baseContext.resources.getColor(R.color.colorBlack))
    binding.tvSpecial.setTextColor(baseContext.resources.getColor(R.color.colorBlack))

    var adapterInternational: InternationalListAdapter? = null
    if (listInternational.size > 0) {
      adapterInternational = InternationalListAdapter(baseContext, listInternational)
      binding.rvInternationalList.adapter = adapterInternational
      adapterInternational?.notifyDataSetChanged()
    }
  }

  private fun clickTvInternet() {
    binding.rvComboList.visibility = View.GONE
    binding.rvAllList.visibility = View.GONE
    binding.rvFullTalkTimeList.visibility = View.GONE
    binding.rvInternationalList.visibility = View.GONE
    binding.rvInternetList.visibility = View.VISIBLE

    binding.tvComboPack.setTextColor(baseContext.resources.getColor(R.color.colorBlack))
    binding.tvAll.setTextColor(baseContext.resources.getColor(R.color.colorBlack))
    binding.tvFullTalkTime.setTextColor(baseContext.resources.getColor(R.color.colorBlack))
    binding.tvInternational.setTextColor(baseContext.resources.getColor(R.color.colorBlack))
    binding.tvInternet.setTextColor(baseContext.resources.getColor(R.color.colorBtn))
    binding.tvSpecial.setTextColor(baseContext.resources.getColor(R.color.colorBlack))

    var adapterInternet: InternetListAdapter? = null
    if (listInternet.size > 0) {
      adapterInternet = InternetListAdapter(baseContext, listInternet)
      binding.rvInternetList.adapter = adapterInternet
      adapterInternet?.notifyDataSetChanged()
    }
  }

  private fun clickTvSpecial() {
    binding.rvComboList.visibility = View.GONE
    binding.rvAllList.visibility = View.GONE
    binding.rvFullTalkTimeList.visibility = View.GONE
    binding.rvInternationalList.visibility = View.GONE
    binding.rvInternetList.visibility = View.GONE
    binding.rvSpecialList.visibility = View.VISIBLE


    binding.tvComboPack.setTextColor(baseContext.resources.getColor(R.color.colorBlack))
    binding.tvAll.setTextColor(baseContext.resources.getColor(R.color.colorBlack))
    binding.tvFullTalkTime.setTextColor(baseContext.resources.getColor(R.color.colorBlack))
    binding.tvInternational.setTextColor(baseContext.resources.getColor(R.color.colorBlack))
    binding.tvInternet.setTextColor(baseContext.resources.getColor(R.color.colorBlack))
    binding.tvSpecial.setTextColor(baseContext.resources.getColor(R.color.colorBtn))

    var adapterSpecial: SpecialListAdapter? = null
    if (listSpecial.size > 0) {
      adapterSpecial = SpecialListAdapter(baseContext, listSpecial)
      binding.rvSpecialList.adapter = adapterSpecial
      adapterSpecial?.notifyDataSetChanged()
    }
  }

  private fun clickTvAll() {
    binding.rvComboList.visibility = View.GONE
    binding.rvAllList.visibility = View.VISIBLE
    binding.rvFullTalkTimeList.visibility = View.GONE
    binding.rvInternationalList.visibility = View.GONE
    binding.rvInternetList.visibility = View.GONE


    binding.tvComboPack.setTextColor(baseContext.resources.getColor(R.color.colorBlack))
    binding.tvAll.setTextColor(baseContext.resources.getColor(R.color.colorBtn))
    binding.tvFullTalkTime.setTextColor(baseContext.resources.getColor(R.color.colorBlack))
    binding.tvInternational.setTextColor(baseContext.resources.getColor(R.color.colorBlack))
    binding.tvInternet.setTextColor(baseContext.resources.getColor(R.color.colorBlack))
    binding.tvSpecial.setTextColor(baseContext.resources.getColor(R.color.colorBlack))

    var adapterAll: AllListAdapter? = null
    if (listAll.size > 0) {
      adapterAll = AllListAdapter(baseContext, listAll)
      binding.rvAllList.adapter = adapterAll
      adapterAll?.notifyDataSetChanged()
    }
  }

  private fun callApigetCompany(
    param: PrepaidGetCompanyRequest,
    allPlansActivity: AllPlansActivity
  ) {
    binding.progress.visibility = View.VISIBLE
    hideKeyboardFrom(baseContext, binding.root.rootView)
    val apiInterface = ApiInterface.create().getCompanyName(
      getStringShrd(
        baseContext,
        CommonUtils.accessToken
      ), param
    )

    //apiInterface.enqueue( Callback<List<Movie>>())
    apiInterface.enqueue(object : Callback<ResponseBody> {
      override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
        binding.progress.visibility = View.GONE
        var response1: String? = null
        if (response.body() == null) {
          response1 = response.errorBody()?.string()
        } else {
          response1 = response.body()?.string()
        }
        Log.e("response", "getCompany: " + response1.toString())
        list.clear()
        listName.clear()
        var jsonObject = JSONObject(response1)
        var statusCode = jsonObject.optInt("status")
        var message = jsonObject.optString("message")
        if (statusCode == 200) {
          var dataArray = jsonObject.optJSONArray("data")
          var size = dataArray.length() - 1
          for (i in 0..size) {
            var model = CompanyNameModel()
            var dataObj = dataArray.optJSONObject(i)
            model.setOperatorName(dataObj.optString("operatorName"))
            model.setOperatorId(dataObj.optString("operatorId"))

            var circleArray = dataObj.optJSONArray("circleWisePlanLists")
            model.setCircleList(circleArray.toString())

            list.add(model)
          }
          if (list.size > 0) {
            for (j in 0..(list.size - 1)) {
              listName.add(list[j].operatorName)
            }
            if (listName.size > 0) {
              val arrayAdapter =
                ArrayAdapter<String>(allPlansActivity, layout.simple_spinner_item, listName)
              arrayAdapter.setDropDownViewResource(layout.simple_spinner_dropdown_item)
              binding.spnCompany.setAdapter(arrayAdapter)
            }

          }
        } else {
          showSnackBar(allPlansActivity, binding.mainLayout, message)
        }

      }

      override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
        binding.progress.visibility = View.GONE
      }
    })
  }
}