package id.my.rizalanggoro.arta.core.application

import android.app.Application
import id.my.rizalanggoro.arta.core.data.AuthPrefs
import id.my.rizalanggoro.arta.core.data.ThemePrefs
import id.my.rizalanggoro.arta.feature.auth.data.AuthApiService
import id.my.rizalanggoro.arta.feature.auth.data.AuthRepository
import id.my.rizalanggoro.arta.feature.category.data.CategoryApiService
import id.my.rizalanggoro.arta.feature.category.data.CategoryRepository
import id.my.rizalanggoro.arta.feature.gold.data.GoldApiService
import id.my.rizalanggoro.arta.feature.gold.data.GoldRepository
import id.my.rizalanggoro.arta.feature.wallet.data.WalletApiService
import id.my.rizalanggoro.arta.feature.wallet.data.WalletRepository
import id.my.rizalanggoro.arta.core.network.RetrofitProvider

class MyApplication : Application() {
    lateinit var authPrefs: AuthPrefs
        private set

    lateinit var themePrefs: ThemePrefs
        private set

    lateinit var authRepository: AuthRepository
        private set

    lateinit var categoryRepository: CategoryRepository
        private set

    lateinit var walletRepository: WalletRepository
        private set

    lateinit var goldRepository: GoldRepository
        private set
    lateinit var selectedWalletPrefs: id.my.rizalanggoro.arta.core.data.SelectedWalletPrefs
        private set

    override fun onCreate() {
        super.onCreate()

        authPrefs = AuthPrefs(applicationContext)
        themePrefs = ThemePrefs(applicationContext)
        selectedWalletPrefs = id.my.rizalanggoro.arta.core.data.SelectedWalletPrefs(applicationContext)

        val authApiService: AuthApiService = RetrofitProvider.create(AuthApiService::class.java)
        val categoryApiService: CategoryApiService = RetrofitProvider.create(CategoryApiService::class.java)
        val walletApiService: WalletApiService = RetrofitProvider.create(WalletApiService::class.java)
        val goldApiService: GoldApiService = RetrofitProvider.create(GoldApiService::class.java)

        authRepository = AuthRepository(authApiService)

        categoryRepository = CategoryRepository(
            apiService = categoryApiService,
            authSessionProvider = { authPrefs.currentSession.value },
        )

        walletRepository = WalletRepository(
            apiService = walletApiService,
            authSessionProvider = { authPrefs.currentSession.value },
        )

        goldRepository = GoldRepository(
            apiService = goldApiService,
            authSessionProvider = { authPrefs.currentSession.value },
        )
        // selectedWalletPrefs available via application instance
    }
}