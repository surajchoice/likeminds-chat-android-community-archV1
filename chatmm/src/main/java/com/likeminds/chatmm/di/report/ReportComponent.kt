package com.likeminds.chatmm.di.report

import com.likeminds.chatmm.report.view.ReportActivity
import com.likeminds.chatmm.report.view.ReportFragment
import dagger.Subcomponent

@Subcomponent(modules = [ReportViewModelModule::class])
interface ReportComponent {

    @Subcomponent.Factory
    interface Factory {
        fun create(): ReportComponent
    }

    fun inject(reportFragment: ReportFragment)
    fun inject(reportActivity: ReportActivity)
}