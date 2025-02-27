package com.example.onepicture.utils

import android.os.Bundle
import android.util.Log
import androidx.navigation.NavController
import androidx.navigation.NavDirections

fun NavController.safeNavigateWithArgs(direction: NavDirections, bundle: Bundle? = null) {
    val currentDestinationId = currentDestination?.id
    val actionId = direction.actionId

    // Check if the current destination has the action to prevent navigation errors
    if (currentDestinationId != null && currentDestination?.getAction(actionId) != null) {
        navigate(actionId, bundle)
    } else {
        Log.w("NavControllerExtensions", "Cannot navigate: action not found")
    }
}
