package com.tanglishvideotranslator.di

import android.content.ContentResolver
import android.content.Context
import androidx.media3.exoplayer.ExoPlayer
import com.tanglishvideotranslator.data.LibraryRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideExoPlayer(@ApplicationContext context: Context): ExoPlayer =
        ExoPlayer.Builder(context).build()

    @Provides
    @Singleton
    fun provideContentResolver(@ApplicationContext context: Context): ContentResolver =
        context.contentResolver

    @Provides
    @Singleton
    fun provideLibraryRepository(contentResolver: ContentResolver): LibraryRepository =
        LibraryRepository(contentResolver)
}
