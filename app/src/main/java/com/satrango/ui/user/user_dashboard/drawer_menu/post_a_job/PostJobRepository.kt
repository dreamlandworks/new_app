package com.satrango.ui.user.user_dashboard.drawer_menu.post_a_job

import com.satrango.base.BaseRepository
import com.satrango.remote.RetrofitBuilder
import com.satrango.remote.end_points.UserApiEndPoints
import com.satrango.ui.user.user_dashboard.drawer_menu.my_job_posts.models.MyJobPostReqModel
import com.satrango.ui.user.user_dashboard.drawer_menu.my_job_posts.models.MyJobPostResModel
import com.satrango.ui.user.user_dashboard.drawer_menu.my_job_posts.my_job_post_view.discussion_board.models.DiscussionBoardMessageReqModel
import com.satrango.ui.user.user_dashboard.drawer_menu.my_job_posts.my_job_post_view.discussion_board.models.DiscussionBoardMessageResModel
import com.satrango.ui.user.user_dashboard.drawer_menu.my_job_posts.my_job_post_view.discussion_board.models.DiscussionListReqModel
import com.satrango.ui.user.user_dashboard.drawer_menu.my_job_posts.my_job_post_view.discussion_board.models.DiscussionListResModel
import com.satrango.ui.user.user_dashboard.drawer_menu.my_job_posts.my_job_post_view.models.MyJobPostViewReqModel
import com.satrango.ui.user.user_dashboard.drawer_menu.my_job_posts.my_job_post_view.models.MyJobPostViewResModel
import com.satrango.ui.user.user_dashboard.drawer_menu.my_job_posts.my_job_post_view.view_bid_details.models.ViewProposalReqModel
import com.satrango.ui.user.user_dashboard.drawer_menu.my_job_posts.my_job_post_view.view_bid_details.models.ViewProposalResModel
import com.satrango.ui.user.user_dashboard.drawer_menu.my_job_posts.my_job_post_view.view_bids.models.ViewBidsReqModel
import com.satrango.ui.user.user_dashboard.drawer_menu.my_job_posts.my_job_post_view.view_bids.models.ViewBidsResModel
import com.satrango.ui.user.user_dashboard.drawer_menu.post_a_job.attachments.models.PostJobSkillsResModel
import com.satrango.ui.user.user_dashboard.drawer_menu.post_a_job.description.models.UserBidRangesResModel
import com.satrango.ui.user.user_dashboard.drawer_menu.post_a_job.models.post_job_blue_collar.PostJobBlueCollarReqModel
import com.satrango.ui.user.user_dashboard.drawer_menu.post_a_job.models.post_job_blue_collar.PostJobBlueCollarResModel
import com.satrango.ui.user.user_dashboard.drawer_menu.post_a_job.models.post_job_multi_move.PostJobMultiMoveReqModel
import com.satrango.ui.user.user_dashboard.drawer_menu.post_a_job.models.post_job_multi_move.PostJobMultiMoveResModel
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

    suspend fun postJobBlueCollar(requestBody: PostJobBlueCollarReqModel): PostJobBlueCollarResModel {
        return RetrofitBuilder.getUserRetrofitInstance().postJobBlueCollar(requestBody)
    }

    suspend fun postJobMultiMove(requestBody: PostJobMultiMoveReqModel): PostJobMultiMoveResModel {
        return RetrofitBuilder.getUserRetrofitInstance().postJobMultiMove(requestBody)
    }

    suspend fun myJobPosts(requestBody: MyJobPostReqModel): MyJobPostResModel {
        return RetrofitBuilder.getUserRetrofitInstance().userJobPostDetails(requestBody)
    }

    suspend fun myJobPostsViewDetails(requestBody: MyJobPostViewReqModel): MyJobPostViewResModel {
        return RetrofitBuilder.getUserRetrofitInstance().userJobPostViewDetails(requestBody)
    }

    suspend fun viewBids(requestBody: ViewBidsReqModel): ViewBidsResModel {
        return RetrofitBuilder.getUserRetrofitInstance().viewBids(requestBody)
    }

    suspend fun viewProposal(requestBody: ViewProposalReqModel): ViewProposalResModel {
        return RetrofitBuilder.getUserRetrofitInstance().viewProposal(requestBody)
    }

    suspend fun sendDiscussionMessage(requestBody: DiscussionBoardMessageReqModel): DiscussionBoardMessageResModel {
        return RetrofitBuilder.getUserRetrofitInstance().postDiscussion(requestBody)
    }

    suspend fun discussionList(requestBody: DiscussionListReqModel): DiscussionListResModel {
        return RetrofitBuilder.getUserRetrofitInstance().postDiscussionList(requestBody)
    }

}