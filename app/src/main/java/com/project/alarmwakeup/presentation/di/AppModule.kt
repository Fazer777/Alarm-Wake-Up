package com.project.alarmwakeup.presentation.di

import com.project.alarmwakeup.presentation.viewmodels.MainViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    viewModel<MainViewModel>{
        MainViewModel(
            addAlarmUseCase = get(),
            deleteAlarmUseCase = get(),
            getAlarmClocksUseCase = get(),
            switchEnablingUseCase = get()
        )
    }
}