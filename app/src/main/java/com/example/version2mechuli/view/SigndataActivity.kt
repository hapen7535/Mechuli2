package com.example.version2mechuli.view

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.version2mechuli.R
import com.example.version2mechuli.SignInfo
import com.example.version2mechuli.api.InfoClientData
import com.example.version2mechuli.api.InfoClientMenuImg
//import com.bumptech.glide.Glide
import com.example.version2mechuli.databinding.ActivitySigndataBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SigndataActivity : AppCompatActivity() {

    lateinit var binding : ActivitySigndataBinding
    lateinit var arrMenu : ArrayList<String>
    lateinit var userid : String
    lateinit var userpw : String
    lateinit var gender : String
    lateinit var age : String

    var ratingList = mutableMapOf<String, Float>()

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_signdata)

        var imgList = arrayListOf(binding.menuImg1, binding.menuImg2, binding.menuImg3, binding.menuImg4, binding.menuImg5)
        var textList = arrayListOf(binding.menuName1, binding.menuName2, binding.menuName3, binding.menuName4, binding.menuName5)
        var ratingBars = arrayListOf(binding.ratingBar1,binding.ratingBar2, binding.ratingBar3, binding.ratingBar4, binding.ratingBar5)
        val secondIntent = getIntent()
        val signInfo = intent.getSerializableExtra("info") as SignInfo
        userid = signInfo.id
        userpw = signInfo.pw
        gender = signInfo.gender
        age = signInfo.age

        arrMenu = arrayListOf("떡볶이","파스타","쌈밥","라멘","와플")

        var i = 0
        textList.forEach {
            ratingBars[i].setOnRatingBarChangeListener{ ratingBar, rating, fromUser->
                ratingBar.rating
                ratingList[it.text.toString()] = rating
                Log.d("ratingList", ratingList.toString())
            }
            i += 1
        }

        getMenuImg(arrMenu, imgList, textList)

        binding.returnbtn.setOnClickListener{
            onBackPressed()
        }

        binding.signTest.setOnClickListener {


            Log.d("rating Size", "${ratingList.size}")
            if(ratingList.size < 5){
                Toast.makeText(this, "해당 메뉴의 점수를 모두 매겨주세요.", Toast.LENGTH_SHORT).show()
            }
            else{
                completeInfo(userid, userpw, age, gender, ratingList)
            }
        }
    }


    private fun getMenuImg(nameList : ArrayList<String>, imgLayout : ArrayList<ImageView>,textlayout : ArrayList<TextView>){

        var i = 0

            lifecycleScope.launch {
                Log.d("myTag", "서버 요청 실행")
                //UI
                val res = withContext(Dispatchers.IO) {
                    InfoClientMenuImg.service.requestData(nameList)
                }
                //UI

                val answer = res.resultList

                answer.forEach {
                    textlayout[i].setText(it.foodName)
                    Glide.with(this@SigndataActivity)
                        .load(it.image2)
                        .into(imgLayout[i])
                    i += 1
                }

                Log.d("myTag", "서버 데이터 받음 : " + res)
                Log.d("myTag", "0 : " + res.resultList[0])
                Log.d("myTag", "1 : " + res.resultList[1])

            }

    }

    private fun completeInfo(id : String, pw : String, age : String, gender : String, ratings : MutableMap<String, Float>){
        lifecycleScope.launch{
            //UI
            val res = withContext(Dispatchers.IO){
                InfoClientData.service.requestData(id,pw, gender, age, ratings)
            }
            //UI
            val answer = res.result
            if(answer){
                val intent = Intent(this@SigndataActivity, LoginActivity::class.java)
                Toast.makeText(this@SigndataActivity, "회원가입 처리가 정상적으로 완료되었습니다.", Toast.LENGTH_LONG).show()
                startActivity(intent)
            } else{
                Toast.makeText(this@SigndataActivity, "회원가입 처리가 비정상적으로 처리되었습니다.", Toast.LENGTH_LONG).show()
            }
        }
    }


}