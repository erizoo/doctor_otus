package ru.soft.feature_home.presentation

import android.content.pm.*
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
import ru.soft.feature_home.R
import ru.soft.feature_home.databinding.*
import ru.soft.feature_home.di.*
import ru.soft.feature_home.presentation.list_orders.*
import ru.soft.main_api.*
import javax.inject.*

class HomeFragment : BaseFragment(R.layout.fragment_home) {

    private val binding by viewBinding(FragmentHomeBinding::bind)

    @Inject
    lateinit var viewModelFactory: HomeViewModelFactory
    private val viewModel by viewModels<HomeViewModel> { viewModelFactory }

    @Inject
    lateinit var router: Router

    @Inject
    lateinit var mainMediator: MainMediator

    @Inject
    lateinit var authMediator: AuthMediator

    companion object {
        fun newInstance() = HomeFragment()
    }

    private val statusAdapter by lazy {
        MultiBindingAdapter(
            statusCount {
                if (it.statusNameForServer != null) {
                    router.navigateTo(object : FragmentScreen {
                        override fun createFragment(factory: FragmentFactory): Fragment {
                            return ListOrdersFragment.newInstance(
                                status = it.statusNameForServer
                            )
                        }
                    })
                }
            }
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
        mainMediator.showBottomNav(requireContext())
        viewModel.getListStatus()
    }

    private fun initViews() {
        binding.rvOrders.adapter = statusAdapter
        binding.tvTitle.text = viewModel.doctor

        try {
            val pInfo = requireContext().packageManager.getPackageInfo(
                requireContext().packageName,
                PackageManager.GET_ACTIVITIES
            )
            binding.tvVersion.text =
                "v ${pInfo.versionName}"
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }

    }

    private fun initObservers() {
        requireActivity().onBackPressedDispatcher.addCallback {
            requireActivity().finish()
        }
        launchAndRepeatWithViewLifecycle {
            viewModel.isErrorToken.collect {
                if (it) {
                    mainMediator.hideBottomBar(requireContext())
                    authMediator.startAuthScreen()
                }
            }
        }
        launchAndRepeatWithViewLifecycle {
            viewModel.listStatus.collect {
                if (it.isNotEmpty()) {
                    statusAdapter.items = it
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