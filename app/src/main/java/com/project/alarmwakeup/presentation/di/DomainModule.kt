package com.project.alarmwakeup.presentation.di

import com.project.alarmwakeup.domain.alarm_clock.usecase.AddAlarmUseCase
import com.project.alarmwakeup.domain.alarm_clock.usecase.DeleteAlarmUseCase
import com.project.alarmwakeup.domain.alarm_clock.usecase.GetAlarmClocksUseCase
import com.project.alarmwakeup.domain.alarm_clock.usecase.SwitchEnablingUseCase
import org.koin.dsl.module

val domainModule = module {
    factory<AddAlarmUseCase>{
        AddAlarmUseCase(alarmRepository = get())
    }

    factory<GetAlarmClocksUseCase>{
        GetAlarmClocksUseCase(alarmRepository = get())
    }

    factory<DeleteAlarmUseCase>{
        DeleteAlarmUseCase(alarmRepository = get())
    }

    factory<SwitchEnablingUseCase>{
        SwitchEnablingUseCase(alarmRepository = get())
    }
}