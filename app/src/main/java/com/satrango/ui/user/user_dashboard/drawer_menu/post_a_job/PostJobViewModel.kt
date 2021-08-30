package com.satrango.ui.user.user_dashboard.drawer_menu.post_a_job

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.satrango.remote.NetworkResponse
import com.satrango.ui.user.user_dashboard.drawer_menu.post_a_job.attachments.models.Data
import com.satrango.ui.user.user_dashboard.drawer_menu.post_a_job.attachments.models.PostJobSkillsResModel
import com.satrango.ui.user.user_dashboard.drawer_menu.post_a_job.description.models.UserBidRangesResModel
import com.satrango.ui.user.user_dashboard.drawer_menu.post_a_job.models.post_job_blue_collar.PostJobBlueCollarReqModel
import com.satrango.ui.user.user_dashboard.drawer_menu.post_a_job.models.post_job_blue_collar.PostJobBlueCollarResModel
import com.satrango.ui.user.user_dashboard.drawer_menu.post_a_job.models.post_job_multi_move.PostJobMultiMoveReqModel
import com.satrango.ui.user.user_dashboard.drawer_menu.post_a_job.models.post_job_multi_move.PostJobMultiMoveResModel
import com.satrango.ui.user.user_dashboard.drawer_menu.post_a_job.models.post_job_single_move.PostJobSingleMoveReqModel
import com.satrango.ui.user.user_dashboard.drawer_menu.post_a_job.models.post_job_single_move.PostJobSingleMoveResModel
import com.satrango.ui.user.user_dashboard.drawer_menu.post_a_job.plans.models.UserPlanPaymentReqModel
import com.satrango.ui.user.user_dashboard.drawer_menu.post_a_job.plans.models.UserPlanPaymentResModel
import com.satrango.utils.hasInternetConnection
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class PostJobViewModel(private val repository: PostJobRepository): ViewModel() {

    var skills = MutableLiveData<NetworkResponse<List<Data>>>()
    var userPlans = MutableLiveData<NetworkResponse<List<com.satrango.ui.user.user_dashboard.drawer_menu.post_a_job.plans.models.Data>>>()
    var planPayment = MutableLiveData<NetworkResponse<UserPlanPaymentResModel>>()
    var bidRanges = MutableLiveData<NetworkResponse<List<com.satrango.ui.user.user_dashboard.drawer_menu.post_a_job.description.models.Data>>>()

    var postJobSingleMove = MutableLiveData<NetworkResponse<PostJobSingleMoveResModel>>()
    var postJobBlueCollar = MutableLiveData<NetworkResponse<PostJobBlueCollarResModel>>()
    var postJobMultiMove = MutableLiveData<NetworkResponse<PostJobMultiMoveResModel>>()

    fun skills(context: Context): MutableLiveData<NetworkResponse<List<Data>>> {

        if (hasInternetConnection(context)) {
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
        } else {
            skills.value = NetworkResponse.Failure("No Internet Connection!")
        }

        return skills
    }

    fun getUserPlans(context: Context): MutableLiveData<NetworkResponse<List<com.satrango.ui.user.user_dashboard.drawer_menu.post_a_job.plans.models.Data>>> {

        if (hasInternetConnection(context)) {
            viewModelScope.launch {
                try {
                    userPlans.value = NetworkResponse.Loading()
                    val response = async { repository.getUserPlans() }
                    val data = response.await()
                    if (data.status == 200) {
                        userPlans.value = NetworkResponse.Success(data.data)
                    } else {
                        userPlans.value = NetworkResponse.Failure(data.message)
                    }
                } catch (e: Exception) {
                    userPlans.value = NetworkResponse.Failure(e.message)
                }
            }
        } else {
            userPlans.value = NetworkResponse.Failure("No Internet Connection!")
        }
        return userPlans
    }

    fun saveUserPlanPayment(context: Context, requestBody: UserPlanPaymentReqModel): MutableLiveData<NetworkResponse<UserPlanPaymentResModel>> {
        if (hasInternetConnection(context)) {
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
        } else {
            planPayment.value = NetworkResponse.Failure("No Internet Connection!")
        }
        return planPayment
    }

    fun bidRanges(context: Context): MutableLiveData<NetworkResponse<List<com.satrango.ui.user.user_dashboard.drawer_menu.post_a_job.description.models.Data>>> {
        if (hasInternetConnection(context)) {
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
        } else {
            bidRanges.value = NetworkResponse.Failure("No Internet Connection!")
        }
        return bidRanges
    }

    fun postJobSingleMove(context: Context, requestBody: PostJobSingleMoveReqModel): MutableLiveData<NetworkResponse<PostJobSingleMoveResModel>> {
        if (hasInternetConnection(context)) {
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
        } else {
            postJobSingleMove.value = NetworkResponse.Failure("No Internet Connection!")
        }
        return postJobSingleMove
    }

    fun postJobBlueCollar(context: Context, requestBody: PostJobBlueCollarReqModel): MutableLiveData<NetworkResponse<PostJobBlueCollarResModel>> {
        if (hasInternetConnection(context)) {
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
        } else {
            postJobBlueCollar.value = NetworkResponse.Failure("No Internet Connection!")
        }
        return postJobBlueCollar
    }

    fun postJobMultiMove(context: Context, requestBody: PostJobMultiMoveReqModel): MutableLiveData<NetworkResponse<PostJobMultiMoveResModel>> {
        if (hasInternetConnection(context)) {
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
        } else {
            postJobMultiMove.value = NetworkResponse.Failure("No Internet Connection!")
        }
        return postJobMultiMove
    }

}