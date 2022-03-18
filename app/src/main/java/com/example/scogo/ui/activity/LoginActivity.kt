package com.example.scogo.ui.activity

import android.os.Bundle
import com.example.scogo.R
import com.example.scogo.base_classes.BaseActivity
import com.example.scogo.databinding.ActivityLogin2Binding
import com.example.scogo.utils.leftDrawable

class LoginActivity : BaseActivity() {
    private lateinit var binding: ActivityLogin2Binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLogin2Binding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.tiUserID.leftDrawable(R.drawable.ic_user, R.dimen.edit_field_icon_size)
        binding.tiPassword?.leftDrawable(R.drawable.ic_password, R.dimen.edit_field_icon_size)

        setClick()
    }

    private fun setClick() {
        binding.imgExit?.setOnClickListener {
            finishAffinity()
        }
        binding.textZero.setOnClickListener {
            setValue(binding.textZero.text.toString())
        }
        binding.textOne.setOnClickListener {
            setValue(binding.textOne.text.toString())
        }
        binding.textTwo.setOnClickListener {
            setValue(binding.textTwo.text.toString())
        }
        binding.textThree.setOnClickListener {
            setValue(binding.textThree.text.toString())
        }
        binding.textFour.setOnClickListener {
            setValue(binding.textFour.text.toString())
        }
        binding.textFive.setOnClickListener {
            setValue(binding.textFive.text.toString())
        }
        binding.textSix.setOnClickListener {
            setValue(binding.textSix.text.toString())
        }
        binding.textSeven.setOnClickListener {
            setValue(binding.textSeven.text.toString())
        }
        binding.textEight.setOnClickListener {
            setValue(binding.textEight.text.toString())
        }
        binding.textNine.setOnClickListener {
            setValue(binding.textNine.text.toString())
        }

        binding.imgRemove.setOnClickListener {
            removeDigit()
        }
    }

    private fun removeDigit() {
        if (binding.tiPassword.text.toString().isEmpty()) binding.tiUserID.setText(binding.tiUserID.text.toString().dropLast(1))
        else binding.tiPassword.setText(binding.tiPassword.text.toString().dropLast(1))
//        else binding.tiUserID.setText(binding.tiUserID.text.toString().dropLast(1))
    }

    private fun setValue(editValue: String) {
        if (isForUserID()) {
            binding.tiUserID.setText("${binding.tiUserID.text.toString()}${editValue}")
        } else {
            binding.tiPassword.setText("${binding.tiPassword.text.toString()}${editValue}")
        }
    }

    private fun isForUserID(): Boolean {
        val inputString = "${binding.tiUserID.text.toString()}${binding.tiPassword.text.toString()}"
        return inputString.isEmpty() || inputString.length < 4
    }
}