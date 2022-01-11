package com.satrango.ui.user.user_dashboard.drawer_menu.post_a_job

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.satrango.remote.NetworkResponse
import com.satrango.ui.user.user_dashboard.drawer_menu.my_job_posts.models.MyJobPostReqModel
import com.satrango.ui.user.user_dashboard.drawer_menu.my_job_posts.models.MyJobPostResModel
import com.satrango.ui.user.user_dashboard.drawer_menu.my_job_posts.my_job_post_edit.models.AttachmentDeleteReqModel
import com.satrango.ui.user.user_dashboard.drawer_menu.my_job_posts.my_job_post_edit.models.AttachmentDeleteResModel
import com.satrango.ui.user.user_dashboard.drawer_menu.my_job_posts.my_job_post_edit.models.MyPostJobEditResModel
import com.satrango.ui.user.user_dashboard.drawer_menu.my_job_posts.my_job_post_edit.models.blue_collar.MyJobPostEditBlueCollarReqModel
import com.satrango.ui.user.user_dashboard.drawer_menu.my_job_posts.my_job_post_edit.models.multi_move.MyJobPostMultiMoveEditReqModel
import com.satrango.ui.user.user_dashboard.drawer_menu.my_job_posts.my_job_post_edit.models.single_move.MyJobPostSingleMoveEditReqModel
import com.satrango.ui.user.user_dashboard.drawer_menu.my_job_posts.my_job_post_view.discussion_board.models.*
import com.satrango.ui.user.user_dashboard.drawer_menu.my_job_posts.my_job_post_view.models.MyJobPostViewReqModel
import com.satrango.ui.user.user_dashboard.drawer_menu.my_job_posts.my_job_post_view.models.MyJobPostViewResModel
import com.satrango.ui.user.user_dashboard.drawer_menu.my_job_posts.my_job_post_view.set_goals.models.installment_payments.InstallmentPaymentReqModel
import com.satrango.ui.user.user_dashboard.drawer_menu.my_job_posts.my_job_post_view.set_goals.models.installment_payments.InstallmentPaymentResModel
import com.satrango.ui.user.user_dashboard.drawer_menu.my_job_posts.my_job_post_view.set_goals.models.save_installments.SaveInstallmentReqModel
import com.satrango.ui.user.user_dashboard.drawer_menu.my_job_posts.my_job_post_view.set_goals.models.save_installments.SaveInstallmentResModel
import com.satrango.ui.user.user_dashboard.drawer_menu.my_job_posts.my_job_post_view.view_bid_details.models.RejectJobPostStatusReqModel
import com.satrango.ui.user.user_dashboard.drawer_menu.my_job_posts.my_job_post_view.view_bid_details.models.ViewProposalReqModel
import com.satrango.ui.user.user_dashboard.drawer_menu.my_job_posts.my_job_post_view.view_bid_details.models.ViewProposalResModel
import com.satrango.ui.user.user_dashboard.drawer_menu.my_job_posts.my_job_post_view.view_bids.models.ViewBidsReqModel
import com.satrango.ui.user.user_dashboard.drawer_menu.my_job_posts.my_job_post_view.view_bids.models.ViewBidsResModel
import com.satrango.ui.user.user_dashboard.drawer_menu.post_a_job.attachments.models.Data
import com.satrango.ui.user.user_dashboard.drawer_menu.post_a_job.models.post_job_blue_collar.PostJobBlueCollarReqModel
import com.satrango.ui.user.user_dashboard.drawer_menu.post_a_job.models.post_job_blue_collar.PostJobBlueCollarResModel
import com.satrango.ui.user.user_dashboard.drawer_menu.post_a_job.models.post_job_multi_move.PostJobMultiMoveReqModel
import com.satrango.ui.user.user_dashboard.drawer_menu.post_a_job.models.post_job_multi_move.PostJobMultiMoveResModel
import com.satrango.ui.user.user_dashboard.drawer_menu.post_a_job.models.post_job_single_move.PostJobSingleMoveReqModel
import com.satrango.ui.user.user_dashboard.drawer_menu.post_a_job.models.post_job_single_move.PostJobSingleMoveResModel
import com.satrango.ui.user.user_dashboard.drawer_menu.post_a_job.plans.models.PostJobPlansResModel
import com.satrango.ui.user.user_dashboard.drawer_menu.post_a_job.plans.models.UserPlanPaymentReqModel
import com.satrango.ui.user.user_dashboard.drawer_menu.post_a_job.plans.models.UserPlanPaymentResModel
import com.satrango.utils.hasInternetConnection
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import org.json.JSONObject

class PostJobViewModel(private val repository: PostJobRepository): ViewModel() {

    var skills = MutableLiveData<NetworkResponse<List<Data>>>()
    var skillsByCategory = MutableLiveData<NetworkResponse<List<Data>>>()
    var userPlans = MutableLiveData<NetworkResponse<PostJobPlansResModel>>()
    var planPayment = MutableLiveData<NetworkResponse<UserPlanPaymentResModel>>()
    var bidRanges = MutableLiveData<NetworkResponse<List<com.satrango.ui.user.user_dashboard.drawer_menu.post_a_job.description.models.Data>>>()

    var postJobSingleMove = MutableLiveData<NetworkResponse<PostJobSingleMoveResModel>>()
    var postJobBlueCollar = MutableLiveData<NetworkResponse<PostJobBlueCollarResModel>>()
    var postJobMultiMove = MutableLiveData<NetworkResponse<PostJobMultiMoveResModel>>()

    var myJobPosts = MutableLiveData<NetworkResponse<MyJobPostResModel>>()
    var myJobPostsViewDetails = MutableLiveData<NetworkResponse<MyJobPostViewResModel>>()
    var viewBids = MutableLiveData<NetworkResponse<ViewBidsResModel>>()
    var viewProposal = MutableLiveData<NetworkResponse<ViewProposalResModel>>()

    var discussionMessage = MutableLiveData<NetworkResponse<DiscussionBoardMessageResModel>>()
    var discussionList = MutableLiveData<NetworkResponse<List<DiscussionDetail>>>()

    var setGoals = MutableLiveData<NetworkResponse<List<com.satrango.ui.user.user_dashboard.drawer_menu.my_job_posts.my_job_post_view.set_goals.models.setgoals.Data>>>()
    var saveInstallments = MutableLiveData<NetworkResponse<SaveInstallmentResModel>>()
    var installmentsPayment = MutableLiveData<NetworkResponse<InstallmentPaymentResModel>>()

    var deleteAttachment = MutableLiveData<NetworkResponse<AttachmentDeleteResModel>>()
    var likeClicked = MutableLiveData<NetworkResponse<LikePostDiscussionResModel>>()

    var updateSingleMoveMyJobPost = MutableLiveData<NetworkResponse<MyPostJobEditResModel>>()
    var updateBlueCollarMyJobPost = MutableLiveData<NetworkResponse<MyPostJobEditResModel>>()
    var updateMultiMoveMyJobPost = MutableLiveData<NetworkResponse<MyPostJobEditResModel>>()

    var rejectJobPostStatus = MutableLiveData<NetworkResponse<String>>()

    fun skills(context: Context): MutableLiveData<NetworkResponse<List<Data>>> {

//        if (hasInternetConnection(context)) {
            viewModelScope.launch {
                try {
                    skills.value = NetworkResponse.Loading()
                    val response = async { repository.getSkills() }
                    val data = response.await()
                    if (data.status == 200) {
                        skills.value = NetworkResponse.Success(data.data)
                    } else {
                        skills.value = NetworkResponse.Failure(data.message)
                    }
                } catch (e: Exception) {
                    skills.value = NetworkResponse.Failure(e.message)
                }
            }
//        } else {
//            skills.value = NetworkResponse.Failure("No Internet Connection!")
//        }
        return skills
    }

    fun skillsByCategoryId(context: Context, categoryId: String): MutableLiveData<NetworkResponse<List<Data>>> {
//        if (hasInternetConnection(context)) {
            viewModelScope.launch {
                try {
                    skillsByCategory.value = NetworkResponse.Loading()
                    val response = async { repository.getSkillsByCategory(categoryId) }
                    val data = response.await()
                    if (data.status == 200) {
                        skillsByCategory.value = NetworkResponse.Success(data.data)
                    } else {
                        skillsByCategory.value = NetworkResponse.Failure(data.message)
                    }
                } catch (e: Exception) {
                    skillsByCategory.value = NetworkResponse.Failure(e.message)
                }
            }
//        } else {
//            skillsByCategory.value = NetworkResponse.Failure("No Internet Connection!")
//        }
        return skillsByCategory
    }

    fun getUserPlans(context: Context): MutableLiveData<NetworkResponse<PostJobPlansResModel>> {
//        if (hasInternetConnection(context)) {
            viewModelScope.launch {
                try {
                    userPlans.value = NetworkResponse.Loading()
                    val response = async { repository.getUserPlans(context) }
                    val data = response.await()
                    if (data.status == 200) {
                        userPlans.value = NetworkResponse.Success(data)
                    } else {
                        userPlans.value = NetworkResponse.Failure(data.message)
                    }
                } catch (e: Exception) {
                    userPlans.value = NetworkResponse.Failure(e.message)
                }
            }
//        } else {
//            userPlans.value = NetworkResponse.Failure("No Internet Connection!")
//        }
        return userPlans
    }

    fun saveUserPlanPayment(context: Context, requestBody: UserPlanPaymentReqModel): MutableLiveData<NetworkResponse<UserPlanPaymentResModel>> {
//        if (hasInternetConnection(context)) {
            viewModelScope.launch {
                try {
                    planPayment.value = NetworkResponse.Loading()
                    val response = async { repository.saveUserPaymentPlan(requestBody) }
                    val data = response.await()
                    if (data.status == 200) {
                        planPayment.value = NetworkResponse.Success(data)
                    } else {
                        planPayment.value = NetworkResponse.Failure(data.message)
                    }
                } catch (e: Exception) {
                    planPayment.value = NetworkResponse.Failure(e.message)
                }
            }
//        } else {
//            planPayment.value = NetworkResponse.Failure("No Internet Connection!")
//        }
        return planPayment
    }

    fun bidRanges(context: Context): MutableLiveData<NetworkResponse<List<com.satrango.ui.user.user_dashboard.drawer_menu.post_a_job.description.models.Data>>> {
//        if (hasInternetConnection(context)) {
            viewModelScope.launch {
                try {
                    bidRanges.value = NetworkResponse.Loading()
                    val response = async { repository.getUserBidRanges() }
                    val data = response.await()
                    if (data.status == 200) {
                        bidRanges.value = NetworkResponse.Success(data.data)
                    } else {
                        bidRanges.value = NetworkResponse.Failure(data.message)
                    }
                } catch (e: Exception) {
                    bidRanges.value = NetworkResponse.Failure(e.message)
                }
            }
//        } else {
//            bidRanges.value = NetworkResponse.Failure("No Internet Connection!")
//        }
        return bidRanges
    }

    fun postJobSingleMove(context: Context, requestBody: PostJobSingleMoveReqModel): MutableLiveData<NetworkResponse<PostJobSingleMoveResModel>> {
//        if (hasInternetConnection(context)) {
            viewModelScope.launch {
                try {
                    postJobSingleMove.value = NetworkResponse.Loading()
                    val response = async { repository.postJobSingleMove(requestBody) }
                    val data = response.await()
                    Log.e("JSON RESPONSE", Gson().toJson(data))
                    if (data.status == 200) {
                        postJobSingleMove.value = NetworkResponse.Success(data)
                    } else {
                        postJobSingleMove.value = NetworkResponse.Failure(data.message)
                    }
                } catch (e: Exception) {
                    postJobSingleMove.value = NetworkResponse.Failure(e.message)
                }
            }
//        } else {
//            postJobSingleMove.value = NetworkResponse.Failure("No Internet Connection!")
//        }
        return postJobSingleMove
    }

    fun postJobBlueCollar(context: Context, requestBody: PostJobBlueCollarReqModel): MutableLiveData<NetworkResponse<PostJobBlueCollarResModel>> {
//        if (hasInternetConnection(context)) {
            viewModelScope.launch {
                try {
                    postJobBlueCollar.value = NetworkResponse.Loading()
                    val response = async { repository.postJobBlueCollar(requestBody) }
                    val data = response.await()
                    Log.e("JSON RESPONSE", Gson().toJson(data))
                    if (data.status == 200) {
                        postJobBlueCollar.value = NetworkResponse.Success(data)
                    } else {
                        postJobBlueCollar.value = NetworkResponse.Failure(data.message)
                    }
                } catch (e: Exception) {
                    postJobBlueCollar.value = NetworkResponse.Failure(e.message)
                }
            }
//        } else {
//            postJobBlueCollar.value = NetworkResponse.Failure("No Internet Connection!")
//        }
        return postJobBlueCollar
    }

    fun postJobMultiMove(context: Context, requestBody: PostJobMultiMoveReqModel): MutableLiveData<NetworkResponse<PostJobMultiMoveResModel>> {
//        if (hasInternetConnection(context)) {
            viewModelScope.launch {
                try {
                    postJobMultiMove.value = NetworkResponse.Loading()
                    val response = async { repository.postJobMultiMove(requestBody) }
                    val data = response.await()
                    Log.e("JSON RESPONSE", Gson().toJson(data))
                    if (data.status == 200) {
                        postJobMultiMove.value = NetworkResponse.Success(data)
                    } else {
                        postJobMultiMove.value = NetworkResponse.Failure(data.message)
                    }
                } catch (e: Exception) {
                    postJobMultiMove.value = NetworkResponse.Failure(e.message)
                }
            }
//        } else {
//            postJobMultiMove.value = NetworkResponse.Failure("No Internet Connection!")
//        }
        return postJobMultiMove
    }

    fun myJobPosts(context: Context, requestBody: MyJobPostReqModel): MutableLiveData<NetworkResponse<MyJobPostResModel>> {
//        if (hasInternetConnection(context)) {
            viewModelScope.launch {
                try {
                    myJobPosts.value = NetworkResponse.Loading()
                    val response = async { repository.myJobPosts(requestBody) }
                    val data = response.await()
                    Log.e("JSON RESPONSE", Gson().toJson(data))
                    if (data.status == 200) {
                        myJobPosts.value = NetworkResponse.Success(data)
                    } else {
                        myJobPosts.value = NetworkResponse.Failure(data.message)
                    }
                } catch (e: Exception) {
                    myJobPosts.value = NetworkResponse.Failure(e.message)
                }
            }
//        } else {
//            myJobPosts.value = NetworkResponse.Failure("No Internet Connection!")
//        }
        return myJobPosts
    }

    fun myJobPostsViewDetails(context: Context, requestBody: MyJobPostViewReqModel): MutableLiveData<NetworkResponse<MyJobPostViewResModel>> {
//        if (hasInternetConnection(context)) {
            viewModelScope.launch {
                try {
                    myJobPostsViewDetails.value = NetworkResponse.Loading()
                    Log.e("JSON REQUEST", Gson().toJson(requestBody))
                    val response = async { repository.myJobPostsViewDetails(requestBody) }
                    val data = response.await()
                    Log.e("JSON RESPONSE", Gson().toJson(data))
                    if (data.status == 200) {
                        myJobPostsViewDetails.value = NetworkResponse.Success(data)
                    } else {
                        myJobPostsViewDetails.value = NetworkResponse.Failure(data.message)
                    }
                } catch (e: Exception) {
                    myJobPostsViewDetails.value = NetworkResponse.Failure(e.message)
                }
            }
//        } else {
//            myJobPostsViewDetails.value = NetworkResponse.Failure("No Internet Connection!")
//        }
        return myJobPostsViewDetails
    }

    fun viewBids(context: Context, requestBody: ViewBidsReqModel): MutableLiveData<NetworkResponse<ViewBidsResModel>> {
//        if (hasInternetConnection(context)) {
            viewModelScope.launch {
                try {
                    viewBids.value = NetworkResponse.Loading()
                    val response = async { repository.viewBids(requestBody) }
                    val data = response.await()
                    Log.e("JSON RESPONSE", Gson().toJson(data))
                    if (data.status == 200) {
                        viewBids.value = NetworkResponse.Success(data)
                    } else {
                        viewBids.value = NetworkResponse.Failure(data.message)
                    }
                } catch (e: Exception) {
                    viewBids.value = NetworkResponse.Failure(e.message)
                }
            }
//        } else {
//            viewBids.value = NetworkResponse.Failure("No Internet Connection!")
//        }
        return viewBids
    }

    fun viewProposal(context: Context, requestBody: ViewProposalReqModel): MutableLiveData<NetworkResponse<ViewProposalResModel>> {
//        if (hasInternetConnection(context)) {
            viewModelScope.launch {
                try {
                    viewProposal.value = NetworkResponse.Loading()
                    val response = async { repository.viewProposal(requestBody) }
                    val data = response.await()
                    Log.e("JSON RESPONSE", Gson().toJson(data))
                    if (data.status == 200) {
                        viewProposal.value = NetworkResponse.Success(data)
                    } else {
                        viewProposal.value = NetworkResponse.Failure(data.message)
                    }
                } catch (e: Exception) {
                    viewProposal.value = NetworkResponse.Failure(e.message)
                }
            }
//        } else {
//                viewProposal.value = NetworkResponse.Failure("No Internet Connection!")
//        }
        return viewProposal
    }

    fun discussionMessage(context: Context, requestBody: DiscussionBoardMessageReqModel): MutableLiveData<NetworkResponse<DiscussionBoardMessageResModel>> {
//        if (hasInternetConnection(context)) {
            viewModelScope.launch {
                try {
                    discussionMessage.value = NetworkResponse.Loading()
                    Log.e("JSON RESPONSE", Gson().toJson(requestBody))
                    val response = async { repository.sendDiscussionMessage(requestBody) }
                    val data = response.await()
                    Log.e("JSON RESPONSE", Gson().toJson(data))
                    if (data.status == 200) {
                        discussionMessage.value = NetworkResponse.Success(data)
                    } else {
                        discussionMessage.value = NetworkResponse.Failure(data.message)
                    }
                } catch (e: Exception) {
                    discussionMessage.value = NetworkResponse.Failure(e.message)
                }
            }
//        } else {
//            discussionMessage.value = NetworkResponse.Failure("No Internet Connection!")
//        }
        return discussionMessage
    }

    fun discussionList(context: Context, requestBody: DiscussionListReqModel): MutableLiveData<NetworkResponse<List<DiscussionDetail>>> {
//        if (hasInternetConnection(context)) {
            viewModelScope.launch {
                try {
                    discussionList.value = NetworkResponse.Loading()
                    Log.e("JSON RESPONSE", Gson().toJson(requestBody))
                    val response = async { repository.discussionList(requestBody) }
                    val data = response.await()
                    Log.e("JSON RESPONSE", Gson().toJson(data))
                    if (data.status == 200) {
                        discussionList.value = NetworkResponse.Success(data.discussion_details)
                    } else {
                        discussionList.value = NetworkResponse.Failure(data.message)
                    }
                } catch (e: Exception) {
                    discussionList.value = NetworkResponse.Failure(e.message)
                }
            }
//        } else {
//            discussionList.value = NetworkResponse.Failure("No Internet Connection!")
//        }
        return discussionList
    }

    fun setGoals(context: Context): MutableLiveData<NetworkResponse<List<com.satrango.ui.user.user_dashboard.drawer_menu.my_job_posts.my_job_post_view.set_goals.models.setgoals.Data>>> {
//        if (hasInternetConnection(context)) {
            viewModelScope.launch {
                try {
                    setGoals.value = NetworkResponse.Loading()
                    val response = async { repository.setGoals() }
                    val data = response.await()
                    Log.e("JSON RESPONSE", Gson().toJson(data))
                    if (data.status == 200) {
                        setGoals.value = NetworkResponse.Success(data.data)
                    } else {
                        setGoals.value = NetworkResponse.Failure(data.message)
                    }
                } catch (e: Exception) {
                    setGoals.value = NetworkResponse.Failure(e.message)
                }
            }
//        } else {
//            setGoals.value = NetworkResponse.Failure("No Internet Connection!")
//        }
        return setGoals
    }

    fun saveInstallments(context: Context, requestBody: SaveInstallmentReqModel): MutableLiveData<NetworkResponse<SaveInstallmentResModel>> {
//        if (hasInternetConnection(context)) {
            viewModelScope.launch {
                try {
                    saveInstallments.value = NetworkResponse.Loading()
                    val response = async { repository.saveInstallments(requestBody) }
                    val data = response.await()
                    Log.e("JSON RESPONSE", Gson().toJson(data))
                    if (data.status == 200) {
                        saveInstallments.value = NetworkResponse.Success(data)
                    } else {
                        saveInstallments.value = NetworkResponse.Failure(data.message)
                    }
                } catch (e: Exception) {
                    saveInstallments.value = NetworkResponse.Failure(e.message)
                }
            }
//        } else {
//            saveInstallments.value = NetworkResponse.Failure("No Internet Connection!")
//        }
        return saveInstallments
    }

    fun installmentPayments(context: Context, requestBody: InstallmentPaymentReqModel): MutableLiveData<NetworkResponse<InstallmentPaymentResModel>> {
//        if (hasInternetConnection(context)) {
            viewModelScope.launch {
                try {
                    installmentsPayment.value = NetworkResponse.Loading()
                    val response = async { repository.installmentPayments(requestBody) }
                    val data = response.await()
                    Log.e("JSON RESPONSE", Gson().toJson(data))
                    if (data.status == 200) {
                        installmentsPayment.value = NetworkResponse.Success(data)
                    } else {
                        installmentsPayment.value = NetworkResponse.Failure(data.message)
                    }
                } catch (e: Exception) {
                    installmentsPayment.value = NetworkResponse.Failure(e.message)
                }
            }
//        } else {
//            installmentsPayment.value = NetworkResponse.Failure("No Internet Connection!")
//        }
        return installmentsPayment
    }

    fun deleteAttachment(context: Context, requestBody: AttachmentDeleteReqModel): MutableLiveData<NetworkResponse<AttachmentDeleteResModel>> {
//        if (hasInternetConnection(context)) {
            viewModelScope.launch {
                try {
                    deleteAttachment.value = NetworkResponse.Loading()
                    val response = async { repository.deleteAttachment(requestBody) }
                    val data = response.await()
                    Log.e("JSON RESPONSE", Gson().toJson(data))
                    if (data.status == 200) {
                        deleteAttachment.value = NetworkResponse.Success(data)
                    } else {
                        deleteAttachment.value = NetworkResponse.Failure(data.message)
                    }
                } catch (e: Exception) {
                    deleteAttachment.value = NetworkResponse.Failure(e.message)
                }
            }
//        } else {
//            deleteAttachment.value = NetworkResponse.Failure("No Internet Connection!")
//        }
        return deleteAttachment
    }

    fun likeClicked(context: Context, requestBody: LikePostDescussionReqModel): MutableLiveData<NetworkResponse<LikePostDiscussionResModel>> {
//        if (hasInternetConnection(context)) {
            viewModelScope.launch {
                try {
                    likeClicked.value = NetworkResponse.Loading()
                    val response = async { repository.likeClicked(requestBody) }
                    val data = response.await()
                    Log.e("JSON RESPONSE", Gson().toJson(data))
                    if (data.status == 200) {
                        likeClicked.value = NetworkResponse.Success(data)
                    } else {
                        likeClicked.value = NetworkResponse.Failure(data.message)
                    }
                } catch (e: Exception) {
                    likeClicked.value = NetworkResponse.Failure(e.message)
                }
            }
//        } else {
//            likeClicked.value = NetworkResponse.Failure("No Internet Connection!")
//        }
        return likeClicked
    }

    fun updateSingleMoveMyJobPost(context: Context, requestBody: MyJobPostSingleMoveEditReqModel): MutableLiveData<NetworkResponse<MyPostJobEditResModel>> {
//        if (hasInternetConnection(context)) {
            viewModelScope.launch {
                try {
                    updateSingleMoveMyJobPost.value = NetworkResponse.Loading()
                    val response = async { repository.updateSingleMoveMyJobPost(requestBody) }
                    val data = response.await()
                    Log.e("JSON RESPONSE", Gson().toJson(data))
                    if (data.status == 200) {
                        updateSingleMoveMyJobPost.value = NetworkResponse.Success(data)
                    } else {
                        updateSingleMoveMyJobPost.value = NetworkResponse.Failure(data.message)
                    }
                } catch (e: Exception) {
                    updateSingleMoveMyJobPost.value = NetworkResponse.Failure(e.message)
                }
            }
//        } else {
//            updateSingleMoveMyJobPost.value = NetworkResponse.Failure("No Internet Connection!")
//        }
        return updateSingleMoveMyJobPost
    }

    fun updateBlueCollarMyJobPost(context: Context, requestBody: MyJobPostEditBlueCollarReqModel): MutableLiveData<NetworkResponse<MyPostJobEditResModel>> {
//        if (hasInternetConnection(context)) {
            viewModelScope.launch {
                try {
                    updateBlueCollarMyJobPost.value = NetworkResponse.Loading()
                    val response = async { repository.updateBlueCollarMyJobPost(requestBody) }
                    val data = response.await()
                    Log.e("JSON RESPONSE", Gson().toJson(data))
                    if (data.status == 200) {
                        updateBlueCollarMyJobPost.value = NetworkResponse.Success(data)
                    } else {
                        updateBlueCollarMyJobPost.value = NetworkResponse.Failure(data.message)
                    }
                } catch (e: Exception) {
                    updateBlueCollarMyJobPost.value = NetworkResponse.Failure(e.message)
                }
            }
//        } else {
//            updateBlueCollarMyJobPost.value = NetworkResponse.Failure("No Internet Connection!")
//        }
        return updateBlueCollarMyJobPost
    }

    fun updateMultiMoveMyJobPost(context: Context, requestBody: MyJobPostMultiMoveEditReqModel): MutableLiveData<NetworkResponse<MyPostJobEditResModel>> {
//        if (hasInternetConnection(context)) {
            viewModelScope.launch {
                try {
                    updateMultiMoveMyJobPost.value = NetworkResponse.Loading()
                    val response = async { repository.updateMultiMoveMyJobPost(requestBody) }
                    val data = response.await()
                    Log.e("JSON RESPONSE", Gson().toJson(data))
                    if (data.status == 200) {
                        updateMultiMoveMyJobPost.value = NetworkResponse.Success(data)
                    } else {
                        updateMultiMoveMyJobPost.value = NetworkResponse.Failure(data.message)
                    }
                } catch (e: Exception) {
                    updateMultiMoveMyJobPost.value = NetworkResponse.Failure(e.message)
                }
            }
//        } else {
//            updateMultiMoveMyJobPost.value = NetworkResponse.Failure("No Internet Connection!")
//        }
        return updateMultiMoveMyJobPost
    }

    fun rejectPostJobStatus(context: Context, requestBody: RejectJobPostStatusReqModel): MutableLiveData<NetworkResponse<String>> {
//        if (hasInternetConnection(context)) {
            viewModelScope.launch {
                try {
                    rejectJobPostStatus.value = NetworkResponse.Loading()
                    val response = async { repository.rejectPostJobStatus(requestBody) }
                    val data = response.await()
                    Log.e("JSON RESPONSE", Gson().toJson(data))
                    val jsonResponse = JSONObject(data.string())
                    if (jsonResponse.getInt("status") == 200) {
                        rejectJobPostStatus.value = NetworkResponse.Success(jsonResponse.getString("message"))
                    } else {
                        rejectJobPostStatus.value = NetworkResponse.Failure(jsonResponse.getString("message"))
                    }
                } catch (e: Exception) {
                    rejectJobPostStatus.value = NetworkResponse.Failure(e.message)
                }
            }
//        } else {
//            rejectJobPostStatus.value = NetworkResponse.Failure("No Internet Connection!")
//        }
        return rejectJobPostStatus
    }

//    fun skillsByCategoryId(context: Context, categoryId: String): MutableLiveData<NetworkResponse<List<Data>>> {
//
//        if (hasInternetConnection(context)) {
//            viewModelScope.launch {
//                try {
//                    skillsByCategory.value = NetworkResponse.Loading()
//                    val response = async { repository.getSkillsByCategory(categoryId) }
//                    val data = response.await()
//                    if (data.status == 200) {
//                        skillsByCategory.value = NetworkResponse.Success(data.data)
//                    } else {
//                        skillsByCategory.value = NetworkResponse.Failure(data.message)
//                    }
//                } catch (e: Exception) {
//                    skillsByCategory.value = NetworkResponse.Failure(e.message)
//                }
//            }
//        } else {
//            skillsByCategory.value = NetworkResponse.Failure("No Internet Connection!")
//        }
//
//        return skillsByCategory
//    }

}