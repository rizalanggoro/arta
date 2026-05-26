package id.my.rizalanggoro.arta.core.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import id.my.rizalanggoro.arta.core.network.RetrofitProvider
import id.my.rizalanggoro.arta.openapi.apis.AuthApi
import id.my.rizalanggoro.arta.openapi.apis.CategoryApi
import id.my.rizalanggoro.arta.openapi.apis.DashboardApi
import id.my.rizalanggoro.arta.openapi.apis.GoldApi
import id.my.rizalanggoro.arta.openapi.apis.ReleaseApi
import id.my.rizalanggoro.arta.openapi.apis.TransactionApi
import id.my.rizalanggoro.arta.openapi.apis.WalletApi
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ApiModule {
    @Provides
    @Singleton
    fun provideAuthApi(): AuthApi =
        RetrofitProvider.create(AuthApi::class.java)

    @Provides
    @Singleton
    fun provideWalletApi(): WalletApi =
        RetrofitProvider.create(WalletApi::class.java)

    @Provides
    @Singleton
    fun provideCategoryApi(): CategoryApi =
        RetrofitProvider.create(CategoryApi::class.java)

    @Provides
    @Singleton
    fun provideTransactionApi(): TransactionApi =
        RetrofitProvider.create(TransactionApi::class.java)

    @Provides
    @Singleton
    fun provideGoldApi(): GoldApi =
        RetrofitProvider.create(GoldApi::class.java)

    @Provides
    @Singleton
    fun provideDashboardApi(): DashboardApi =
        RetrofitProvider.create(DashboardApi::class.java)

    @Provides
    @Singleton
    fun provideReleaseApi(): ReleaseApi =
        RetrofitProvider.create(ReleaseApi::class.java)
}