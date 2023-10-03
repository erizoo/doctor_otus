package ru.soft.feature_home.presentation.list_orders

import android.os.*
import android.view.*
import androidx.activity.*
import androidx.core.view.*
import androidx.fragment.app.*
import com.github.terrakok.cicerone.*
import com.github.terrakok.cicerone.androidx.*
import ru.soft.*
import ru.soft.base_arch.base.*
import ru.soft.base_arch.recycler.*
import ru.soft.base_arch.utils.*
import ru.soft.core_api.mediator.*
import ru.soft.feature_home.*
import ru.soft.feature_home.R
import ru.soft.feature_home.databinding.*
import ru.soft.feature_home.di.*
import ru.soft.feature_home.presentation.*
import ru.soft.feature_home.presentation.order.*
import ru.soft.main_api.*
import javax.inject.*

class ListOrdersFragment : BaseFragment(R.layout.fragment_list_orders) {

    private val binding by viewBinding(FragmentListOrdersBinding::bind)
    private val status: String? by argument(ARG_STATUS, null)

    companion object {
        fun newInstance(
            status: String
        ): ListOrdersFragment {
            val fragment = ListOrdersFragment()
            val args = Bundle()
            args.putString(ARG_STATUS, status)
            fragment.arguments = args
            return fragment
        }

        const val ARG_STATUS = "ARG_STATUS"
    }

    @Inject
    lateinit var viewModelFactory: ListOrdersViewModelFactory
    private val viewModel by viewModels<ListOrdersViewModel> {
        viewModelFactory
    }

    @Inject
    lateinit var mainMediator: MainMediator

    @Inject
    lateinit var authMediator: AuthMediator

    @Inject
    lateinit var router: Router

    private val orderAdapter by lazy {
        MultiBindingAdapter(
            orders(
                onClick = {
                    if (it.requestGUID != null) {
                        router.replaceScreen(
                            object : FragmentScreen {
                                override fun createFragment(factory: FragmentFactory): Fragment {
                                    return OrderFragment.newInstance(
                                        requestId = it.requestGUID
                                    )
                                }
                            }
                        )
                    }
                }
            )
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        HomeComponent.create((requireActivity().application as AppWithFacade).getFacade())
            .inject(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews()
        initObservers()

        status?.let { viewModel.getOrders(status = it) }
    }

    private fun initViews() {
        binding.rvOrders.adapter = orderAdapter
    }

    private fun initObservers() {
        launchAndRepeatWithViewLifecycle {
            viewModel.isErrorToken.collect {
                if (it) {
                    mainMediator.hideBottomBar(requireContext())
                    authMediator.startAuthScreen()
                }
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback {
            router.exit()
        }
        binding.ivSearch.doAfterTextChanged {
            if (it.isNotEmpty()) {
                val list = viewModel.listResult.filter { item ->
                    item.patient?.contains(it, ignoreCase = true) == true
                }
                binding.tvEmptyList.isVisible = list.isEmpty()
                orderAdapter.items = list
            } else {
                binding.tvEmptyList.isVisible = viewModel.listResult.isEmpty()
                orderAdapter.items = viewModel.listResult
            }
        }
        launchAndRepeatWithViewLifecycle {
            viewModel.listOrders.collect {
                if (it.isNotEmpty()) {
                    binding.tvEmptyList.isVisible = it.isEmpty()
                    orderAdapter.items = it
                }
            }
        }
        launchAndRepeatWithViewLifecycle {
            viewModel.loading.collect {
                binding.layoutProgress.layoutProgress.isVisible = it
            }
        }
    }
}