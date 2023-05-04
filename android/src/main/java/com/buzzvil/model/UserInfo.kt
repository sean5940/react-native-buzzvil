package com.buzzvil.model

import com.buzzvil.buzzad.benefit.core.models.UserProfile

data class UserInfo(val id: String, val gender: UserProfile.Gender?, val birthYear: Int?)
