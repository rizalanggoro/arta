package id.my.rizalanggoro.arta.core.application

import android.app.Application
import id.my.rizalanggoro.arta.core.data.AuthPrefs
import id.my.rizalanggoro.arta.core.data.SelectedWalletPrefs
import id.my.rizalanggoro.arta.core.data.ThemePrefs
import id.my.rizalanggoro.arta.core.network.RetrofitProvider
import id.my.rizalanggoro.arta.feature.category.data.CategoryApiService
import id.my.rizalanggoro.arta.feature.category.data.CategoryRepository
import id.my.rizalanggoro.arta.feature.gold.data.GoldApiService
import id.my.rizalanggoro.arta.feature.gold.data.GoldRepository
import id.my.rizalanggoro.arta.feature.home.data.DashboardApiService
import id.my.rizalanggoro.arta.feature.home.data.DashboardRepository
import id.my.rizalanggoro.arta.feature.transaction.data.TransactionApiService
import id.my.rizalanggoro.arta.feature.transaction.data.TransactionRepository
import id.my.rizalanggoro.arta.feature.wallet.data.WalletApiService
import id.my.rizalanggoro.arta.feature.wallet.data.WalletRepository

class MyApplication : Application() {
    lateinit var authPrefs: AuthPrefs
        private set

    lateinit var themePrefs: ThemePrefs
        private set

    lateinit var dashboardRepository: DashboardRepository
        private set

    lateinit var categoryRepository: CategoryRepository
        private set

    lateinit var walletRepository: WalletRepository
        private set

    lateinit var transactionRepository: TransactionRepository
        private set

    lateinit var goldRepository: GoldRepository
        private set

    lateinit var selectedWalletPrefs: SelectedWalletPrefs
        private set

    override fun onCreate() {
        super.onCreate()

        authPrefs = AuthPrefs(applicationContext)
        themePrefs = ThemePrefs(applicationContext)
        selectedWalletPrefs = SelectedWalletPrefs(applicationContext)

        val dashboardApi = RetrofitProvider.create(DashboardApiService::class.java)
        val categoryApi = RetrofitProvider.create(CategoryApiService::class.java)
        val walletApi = RetrofitProvider.create(WalletApiService::class.java)
        val transactionApi = RetrofitProvider.create(TransactionApiService::class.java)
        val goldApi = RetrofitProvider.create(GoldApiService::class.java)

        dashboardRepository = DashboardRepository(
            apiService = dashboardApi,
            authSessionProvider = { authPrefs.currentSession.value },
        )

        categoryRepository = CategoryRepository(
            apiService = categoryApi,
            authSessionProvider = { authPrefs.currentSession.value },
        )

        walletRepository = WalletRepository(
            apiService = walletApi,
            authSessionProvider = { authPrefs.currentSession.value },
        )

        transactionRepository = TransactionRepository(
            apiService = transactionApi,
            authSessionProvider = { authPrefs.currentSession.value },
        )

        goldRepository = GoldRepository(
            apiService = goldApi,
            authSessionProvider = { authPrefs.currentSession.value },
        )
    }
}