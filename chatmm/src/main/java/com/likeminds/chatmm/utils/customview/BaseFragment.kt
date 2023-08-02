package com.likeminds.chatmm.utils.customview

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.viewbinding.ViewBinding
import javax.inject.Inject

abstract class BaseFragment<B : ViewBinding, VM : ViewModel> : Fragment() {

    private var _binding: B? = null
    protected val binding get() = _binding!!
    private var hasInitializedRootView = false
    private var requestCode: Int = -1

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    protected lateinit var viewModel: VM

    protected abstract fun getViewModelClass(): Class<VM>?

    protected abstract fun getViewBinding(): B

    protected open val useSharedViewModel = false

    protected var isGuestUser: Boolean = false

    /**
     * set value to true, if we want to persist binding
     */
    protected open val keepBindingRetained = false

    /**
     * Only use to receive extras in fragment
     */
    protected open fun receiveExtras() {}

    /**
     * attachs to the component
     */
    protected open fun attachDagger() {}

    /**
     * Only use to handle fragment result listeners
     */
    protected open fun handleResultListener() {}

    /**
     * Only use to create/initialise views
     */
    protected open fun setUpViews() {}

    /**
     * Only use to register observers
     */
    protected open fun observeData() {}

    /**
     * This function can be called in case to clearing any view or listener or observer in fragment
     */
    protected open fun doCleanup() {}

    override fun onAttach(context: Context) {
        super.onAttach(context)
        attachDagger()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        init()
        receiveExtras()
        handleResultListener()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        //Reference: https://stackoverflow.com/questions/54581071/fragments-destroyed-recreated-with-jetpacks-android-navigation-components
        if (_binding == null) {
            _binding = getViewBinding()
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (!hasInitializedRootView && keepBindingRetained) {
            hasInitializedRootView = true
            setUpViews()
        } else if (!keepBindingRetained) {
            setUpViews()
        }
        observeData()
    }

    override fun onDestroyView() {
        doCleanup()
        if (!keepBindingRetained) {
            _binding = null
        }
        super.onDestroyView()
    }

    private fun init() {
        if (getViewModelClass() == null) {
            return
        }
        viewModel = if (useSharedViewModel) {
            ViewModelProvider(requireActivity(), viewModelFactory)[getViewModelClass()!!]
        } else {
            ViewModelProvider(this, viewModelFactory)[getViewModelClass()!!]
        }
    }
}