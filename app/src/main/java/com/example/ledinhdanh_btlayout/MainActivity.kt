package com.example.ledinhdanh_btlayout

import android.graphics.Color
import android.os.Bundle
import android.widget.Toast
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.ledinhdanh_btlayout.databinding.ActivityMainBinding
import java.util.Locale
import com.example.ledinhdanh_btlayout.R

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    // Lưu kết quả gần nhất để Switch đổi cách hiển thị.
    private var lastResult: Double? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(Color.TRANSPARENT)
        )

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Chừa khoảng trống cho thanh hệ thống và bàn phím.
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
            val safeInsets = insets.getInsets(
                WindowInsetsCompat.Type.systemBars() or
                        WindowInsetsCompat.Type.displayCutout() or
                        WindowInsetsCompat.Type.ime()
            )

            view.setPadding(
                safeInsets.left,
                safeInsets.top,
                safeInsets.right,
                safeInsets.bottom
            )

            insets
        }

        binding.btnCalculate.setOnClickListener {
            calculateResult()
        }

        // Bật/tắt Switch sẽ cập nhật kết quả ngay.
        binding.switchRound.setOnCheckedChangeListener { _, _ ->
            showResult()
        }
    }

    private fun calculateResult() {
        binding.edtNumber1.error = null
        binding.edtNumber2.error = null

        lastResult = null
        showResult()

        // Hỗ trợ nhập dấu chấm hoặc dấu phẩy thập phân.
        val number1 = binding.edtNumber1.text.toString()
            .trim()
            .replace(',', '.')
            .toDoubleOrNull()

        val number2 = binding.edtNumber2.text.toString()
            .trim()
            .replace(',', '.')
            .toDoubleOrNull()

        if (number1 == null || !number1.isFinite()) {
            binding.edtNumber1.error =
                getString(R.string.err_invalid_number)
            binding.edtNumber1.requestFocus()
            return
        }

        if (number2 == null || !number2.isFinite()) {
            binding.edtNumber2.error =
                getString(R.string.err_invalid_number)
            binding.edtNumber2.requestFocus()
            return
        }

        val result = when (binding.rgOperations.checkedRadioButtonId) {
            R.id.rbAdd -> number1 + number2

            R.id.rbSub -> number1 - number2

            R.id.rbMul -> number1 * number2

            R.id.rbDiv -> {
                if (number2 == 0.0) {
                    binding.edtNumber2.error =
                        getString(R.string.err_div_zero)
                    binding.edtNumber2.requestFocus()
                    return
                }

                number1 / number2
            }

            else -> {
                Toast.makeText(
                    this,
                    R.string.err_no_operation,
                    Toast.LENGTH_SHORT
                ).show()

                return
            }
        }

        if (!result.isFinite()) {
            Toast.makeText(
                this,
                R.string.err_out_of_range,
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        lastResult = result
        showResult()
    }

    private fun showResult() {
        val result = lastResult

        binding.tvResultValue.text = when {
            result == null -> {
                getString(R.string.result_placeholder)
            }

            binding.switchRound.isChecked -> {
                String.format(Locale.getDefault(), "%.2f", result)
            }

            else -> {
                result.toString()
            }
        }
    }
}