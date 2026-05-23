package id.my.rizalanggoro.arta.core.application

import android.app.Application
import id.my.rizalanggoro.arta.core.data.AuthPrefs
import id.my.rizalanggoro.arta.core.data.SelectedWalletPrefs
import id.my.rizalanggoro.arta.core.data.ThemePrefs
import id.my.rizalanggoro.arta.core.network.RetrofitProvider
import id.my.rizalanggoro.arta.openapi.apis.DashboardApi
import id.my.rizalanggoro.arta.openapi.apis.GoldApi
import id.my.rizalanggoro.arta.openapi.apis.CategoryApi
import id.my.rizalanggoro.arta.openapi.apis.TransactionApi
import id.my.rizalanggoro.arta.openapi.apis.WalletApi

class MyApplication : Application() {
    lateinit var authPrefs: AuthPrefs
        private set

    lateinit var themePrefs: ThemePrefs
        private set

    

    lateinit var categoryApi: CategoryApi
        private set

    lateinit var walletApi: WalletApi
        private set

    lateinit var transactionApi: TransactionApi
        private set

    lateinit var dashboardApi: DashboardApi
        private set

    lateinit var goldApi: GoldApi
        private set

    lateinit var selectedWalletPrefs: SelectedWalletPrefs
        private set

    override fun onCreate() {
        super.onCreate()

        authPrefs = AuthPrefs(applicationContext)
        themePrefs = ThemePrefs(applicationContext)
        selectedWalletPrefs = SelectedWalletPrefs(applicationContext)

        val dashboardApi = RetrofitProvider.create(DashboardApi::class.java)
        val categoryApi = RetrofitProvider.create(CategoryApi::class.java)
        val walletApi = RetrofitProvider.create(WalletApi::class.java)
        val transactionApi = RetrofitProvider.create(TransactionApi::class.java)
        val goldApi = RetrofitProvider.create(GoldApi::class.java)

        this.categoryApi = categoryApi

        this.walletApi = walletApi

        this.transactionApi = transactionApi

        this.dashboardApi = dashboardApi
        this.goldApi = goldApi
    }
}