package com.satrango.ui.user.user_dashboard.drawer_menu.post_a_job

import com.satrango.base.BaseRepository
import com.satrango.remote.RetrofitBuilder
import com.satrango.remote.end_points.UserApiEndPoints
import com.satrango.ui.user.user_dashboard.drawer_menu.post_a_job.attachments.models.PostJobSkillsResModel
import com.satrango.ui.user.user_dashboard.drawer_menu.post_a_job.description.models.UserBidRangesResModel
import com.satrango.ui.user.user_dashboard.drawer_menu.post_a_job.models.post_job_single_move.PostJobSingleMoveReqModel
import com.satrango.ui.user.user_dashboard.drawer_menu.post_a_job.models.post_job_single_move.PostJobSingleMoveResModel
import com.satrango.ui.user.user_dashboard.drawer_menu.post_a_job.plans.models.PostJobPlansResModel
import com.satrango.ui.user.user_dashboard.drawer_menu.post_a_job.plans.models.UserPlanPaymentReqModel
import com.satrango.ui.user.user_dashboard.drawer_menu.post_a_job.plans.models.UserPlanPaymentResModel
import okhttp3.RequestBody

class PostJobRepository: BaseRepository() {

    suspend fun getSkills(): PostJobSkillsResModel {
        return RetrofitBuilder.getUserRetrofitInstance().getSkills(RetrofitBuilder.USER_KEY)
    }

    suspend fun getUserPlans(): PostJobPlansResModel {
        return RetrofitBuilder.getUserRetrofitInstance().getUserPlans(RetrofitBuilder.USER_KEY)
    }

    suspend fun saveUserPaymentPlan(requestBody: UserPlanPaymentReqModel): UserPlanPaymentResModel {
        return RetrofitBuilder.getUserRetrofitInstance().saveUserPaymentPlan(requestBody)
    }

    suspend fun getUserBidRanges(): UserBidRangesResModel {
        return RetrofitBuilder.getUserRetrofitInstance().userBidRanges(RetrofitBuilder.USER_KEY)
    }

    suspend fun postJobSingleMove(requestBody: PostJobSingleMoveReqModel): PostJobSingleMoveResModel {
        return RetrofitBuilder.getUserRetrofitInstance().postJobSingleMove(requestBody)
    }

}