package com.elyric.bredio.view.component.splashPage

import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import com.elyric.bredio.R
import com.elyric.bredio.view.component.fragment.BaseDialogFragment
import com.elyric.common.data.BTextUtils
import com.elyric.common.utils.BProcessUtils
import com.elyric.common.view.BWindowUtils


class ServiceAgreeDialogFragment : BaseDialogFragment() {
    lateinit var onClickAgreeListener: View.OnClickListener
    lateinit var btDisagree: Button
    lateinit var btAgree: Button
    lateinit var contentTextView: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_service_agree_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onResume() {
        super.onResume()
        // 设置提示框合适宽高
        setDialogLayoutParams()
    }
    // region 初始化
    override fun initViews() {
        super.initViews()
        isCancelable = false
        btDisagree = findViewById(R.id.btnDisagreementConfirm)
        btAgree = findViewById(R.id.btnAgreementConfirm)
    }

    override fun initListeners() {
        super.initListeners()
        contentTextView = requireView().findViewById(R.id.tvAgreementContent)
        contentTextView.text = BTextUtils.htmlResToSpanned(requireContext(), R.string.service_agreement_content)
        contentTextView.setLinkTextColor(requireContext().getColor(R.color.primary))
        contentTextView.movementMethod = LinkMovementMethod.getInstance()

        btAgree.setOnClickListener {
            dismiss()
            onClickAgreeListener.onClick(it)
        }
        btDisagree.setOnClickListener {
            BProcessUtils.killApp()
        }
    }
    // endregion
    fun setDialogLayoutParams(){
        val params: ViewGroup.LayoutParams = dialog!!.window!!.attributes
        val screenInfo = BWindowUtils.getScreenSize(requireContext())
        params.width = (screenInfo.first * 0.9).toInt()
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT
        dialog!!.window!!.attributes = params as WindowManager.LayoutParams
    }
    companion object {
        fun show(supportFragmentManager: androidx.fragment.app.FragmentManager,onClickAgreeListener:View.OnClickListener){
            val dialogFragment = ServiceAgreeDialogFragment()
            dialogFragment.onClickAgreeListener = onClickAgreeListener
            dialogFragment.show(supportFragmentManager,"ServiceAgreeDialogFragment")
        }
        @JvmStatic
        fun newInstance() =
            ServiceAgreeDialogFragment().apply {

            }
    }
}

